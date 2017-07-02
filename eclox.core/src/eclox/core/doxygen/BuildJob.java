/*******************************************************************************
 * Copyright (C) 2003, 2006-2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - improved thread handling, added show command in console / title
 *                   - Add ability to use Doxyfile not in project scope
 *                   - Refactoring of deprecated API usage
 *                   - Support resources in linked folders
 *                     https://github.com/anb0s/eclox/issues/176
 *                     Thanks to Corderbollie!
 *                   - fixed java.lang.IllegalArgumentException: endRule without matching beginRule
 *                     https://github.com/anb0s/eclox/issues/175
 *                   - fixed obsolete settings are not marked
 *                     https://github.com/anb0s/eclox/issues/187
 *
 ******************************************************************************/

package eclox.core.doxygen;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.filesystem.URIUtil;

import eclox.core.Plugin;
import eclox.core.doxyfiles.Doxyfile;


/**
 * Implement a build job.
 *
 * @author Guillaume Brocker
 */
public class BuildJob extends Job {

    /**
     * Defines the doxygen not found error code
     */
    public static final int ERROR_DOXYGEN_NOT_FOUND = 2; // ATTENTION: Status.OK_STATUS = 0, Status.CANCEL_STATUS = 1

    /**
     * Defines the pattern used to match doxygen warnings and errors
     */
    private static final Pattern problemPattern = Pattern.compile("^(.+?):\\s*(\\d+)\\s*:\\s*(.+?)\\s*:\\s*(.*$(\\s+^  .*$)*)", Pattern.MULTILINE);

    /**
     * Defines the pattern used to match doxygen warnings about obsolete tags.
     */
    private static final Pattern obsoleteTagWarningPattern = Pattern.compile("(?i)^warning: Tag `(.+)' at line (\\d+) of file `(.+)' has become obsolete.$", Pattern.MULTILINE);


    /**
     * Implements a runnable log feeder that reads the given input stream
     * line per line and writes those lines back to the managed log. Once
     * the stream end has been reached, the feeder exists.
     *
     * @author	Guillaume Brocker
     */
    private class MyLogFeeder implements Runnable
    {

        /**
         * the input stream to read and write by to the log
         */
        private InputStream	input;

        private boolean cancel = false;

        /**
         * Constructor
         *
         * @param	input	the input stream to read to write back to the log
         */
        public MyLogFeeder( InputStream input )
        {
            this.input = input;
        }

        public void run()
        {
            try
            {
                BufferedReader	reader = new BufferedReader( new InputStreamReader(input) );
                String			newLine;

                for(;;)	{
                    // check if cancel
                    if (cancel) {
                        break;
                    }
                    // Processes a new process output line.
                    newLine = reader.readLine();
                    if( newLine != null ) {
                        newLine = newLine.concat( "\n" );
                        log.append( newLine );
                        fireLogUpdated( newLine );
                    }
                    else {
                        break;
                    }
                    Thread.yield();
                }
            }
            catch( Throwable t )
            {
                Plugin.log( t );
            }
        }

        public void cancel() {
            cancel = true;
        }
    }


    /**
     * Implements a resource change listener that will remove a given job if its
     * doxyfile gets deleted.
     *
     * @author	Guillaume Brocker
     */
    private class MyResourceChangeListener implements IResourceChangeListener
    {
        /**
         * the build job whose doxyfile will be monitored
         */
        private BuildJob job;

        public MyResourceChangeListener( BuildJob job )
        {
            this.job = job;
        }

        public void resourceChanged(IResourceChangeEvent event) {
            IFile ifile = job.getDoxyfile().getIFile();
            if (ifile != null) {
                IResourceDelta	doxyfileDelta = event.getDelta().findMember( ifile.getFullPath() );
                if( doxyfileDelta != null && doxyfileDelta.getKind() == IResourceDelta.REMOVED )
                {
                    job.clearMarkers();
                    jobs.remove( job );
                    job.fireRemoved();
                    ResourcesPlugin.getWorkspace().removeResourceChangeListener( this );
                }
            }
        }

    }

    /**
     * a string that is used to identify the doxygen build job family
     */
    public static String FAMILY = "Doxygen Build Job";

    /**
     * a collection containing all created build jobs
     */
    private static Collection<BuildJob> jobs = new HashSet<BuildJob>();

    /**
     * the path of the doxygen
     */
    private String	command;

    /**
     * the path of the doxyfile to build
     */
    private Doxyfile doxyfile;

    /**
     * the buffer containing the whole build output log
     */
    private StringBuffer log = new StringBuffer();

    /**
     * a set containing all registered build job listeners
     */
    private Set<IBuildJobListener> listeners = new HashSet<IBuildJobListener>();

    /**
     * a collection containing all markers corresponding to doxygen warning and errors
     */
    private Collection<IMarker> markers = new Vector<IMarker>();


    /**
     * Constructor.
     */
    private BuildJob(Doxyfile dxfile) {
        super("");

        doxyfile = dxfile;

        updateJobName();

        setPriority( Job.BUILD );
        setUser(true);

        // References the jobs in the global collection and add a doxyfile listener.
        jobs.add( this );
        ResourcesPlugin.getWorkspace().addResourceChangeListener( new MyResourceChangeListener(this), IResourceChangeEvent.POST_CHANGE );
    }


    /**
     * Retrieves all doxygen build jobs.
     *
     * @return	an arry containing all doxygen build jobs (can be empty).
     */
    public static BuildJob[] getAllJobs()
    {
        return (BuildJob[]) jobs.toArray( new BuildJob[0] );
    }

    /**
     * Retrieves the build job associated to the given doxyfile. If needed,
     * a new job will be created.
     *
     * @param	doxyfile	a given doxyfile instance
     * @param	doxygen		a string containing the doxygen command to use
     *
     * @return	a build job that is in charge of building the given doxyfile
     */
    public static BuildJob getJob( Doxyfile doxyfile )
    {
        BuildJob	result = findJob( doxyfile );

        // If no jobs has been found, then creates a new one.
        if( result == null )
        {
            result = new BuildJob(doxyfile);
        }

        // set new command
        result.setCommand(Doxygen.getDefault().getCommand());

        // Job's done.
        return result;
    }

    /**
     * Searches for a build job associated to the given doxyfile.
     *
     * @param	doxyfile	a given doxyfile instance
     *
     * @return	a build job for the given doxyfile or null if none
     */
    public static BuildJob findJob( Doxyfile doxyfile )
    {
        BuildJob	result = null;

        // Walks through the found jobs to find a relevant build job.
        Iterator<BuildJob>	i = jobs.iterator();
        while( i.hasNext() )
        {
            BuildJob buildJob = (BuildJob)i.next();
            if( buildJob.getDoxyfile().equals(doxyfile) )
            {
                result = buildJob;
                break;
            }
        }

        return result;
    }

    /**
     * Adds the given listener to the job.
     *
     * @param	listener	a given listener instance
     */
    public void addBuidJobListener( IBuildJobListener listener )
    {
        synchronized ( listeners ) {
            listeners.add( listener );
        }
    }

    /**
     * Removes the given listener from the job.
     *
     * @param	listener	a given listener instance
     */
    public void removeBuidJobListener( IBuildJobListener listener )
    {
        synchronized( listeners ) {
            listeners.remove( listener );
        }
    }

    /**
     * Clears the log and notifies attached listeners
     */
    public void clearLog() {
        log.delete( 0, log.length() );
        fireLogCleared();
    }

    /**
     * Clears the markers managed by the build job.
     */
    public void clearMarkers()
    {
        // Removes all markers from their respective resource
        Iterator<IMarker>	i = markers.iterator();
        while( i.hasNext() )
        {
            IMarker	marker = (IMarker) i.next();
            try
            {
                marker.delete();
            }
            catch( Throwable t )
            {
                Plugin.log( t );
            }
        }

        // Clear the marker collection
        markers.clear();
    }

    /**
     * Retrieves the doxyfile that is managed by the job.
     *
     * @return	a file taht is the builded doxyfile
     */
    public Doxyfile getDoxyfile() {
        return doxyfile;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
        updateJobName();
    }

    public void updateJobName() {
        setName("Doxygen Build ["+ command + " -b " + doxyfile.getFullPath()+"]");
    }

    /**
     * Retrieves the job's whole log.
     *
     * @return	a string containing the build job's log.
     */
    public String getLog() {
        return log.toString();
    }


    /**
     * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
     */
    public boolean belongsTo(Object family) {
        if( family == FAMILY )
        {
            return true;
        }
        else
        {
            return super.belongsTo( family );
        }
    }


    /**
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run( IProgressMonitor monitor ) {
        IFile doxyIFile = getDoxyfile().getIFile();
        File doxyFile   = getDoxyfile().getFile();
        try {
            // Initializes the progress monitor.
            SubMonitor subMonitor = SubMonitor.convert(monitor, doxyfile.getFullPath(), 6);

            // Clears log and markers.
            clearLog();
            clearMarkers();
            subMonitor.worked( 1 );

            // Locks access to the doxyfile.
            if (doxyIFile != null) {
                getJobManager().beginRule(doxyIFile, subMonitor);
            }

            // Creates the doxygen build process and log feeders.
            Process	buildProcess	= null;
            if (doxyIFile != null) {
                buildProcess = Doxygen.getDefault().build(doxyIFile);
            } else {
                buildProcess = Doxygen.getDefault().build(doxyFile);
            }

            // stdin and stderr
            MyLogFeeder inputLogFeeder = new MyLogFeeder(buildProcess.getInputStream());
            Thread	inputLogThread	= new Thread(inputLogFeeder);
            MyLogFeeder errorLogFeeder = new MyLogFeeder(buildProcess.getErrorStream());
            Thread	errorLogThread	= new Thread(errorLogFeeder);

            // Wait either for the feeders to terminate or the user to cancel the job.
            inputLogThread.start();
            errorLogThread.start();
            for(;;)	{
                // Tests of the log feeders have terminated.
                if( inputLogThread.isAlive() == false && errorLogThread.isAlive() == false ) {
                    break;
                }

                // Tests if the jobs is supposed to terminate.
                if( monitor.isCanceled() == true ) {
                    // stop the log threads
                    inputLogFeeder.cancel();
                    errorLogFeeder.cancel();
                    buildProcess.destroy();
                    buildProcess.waitFor();
                    return Status.CANCEL_STATUS;
                }

                // Allows other threads to run (and cancel!)
                Thread.yield();
            }
            subMonitor.worked( 2 );

            // Unlocks the doxyfile.
            if (doxyIFile != null) {
                getJobManager().endRule(doxyIFile);
                doxyIFile = null;
            }

            // Builds error and warning markers
            createMarkers( subMonitor );
            subMonitor.worked( 3 );

            // Ensure that doxygen process has finished.
            buildProcess.waitFor();
            subMonitor.worked( 4 );

            // Refreshes the container that has received the documentation outputs.
            Doxyfile parsedDoxyfile	= getDoxyfile();
            parsedDoxyfile.load();
            IContainer	outputContainer = parsedDoxyfile.getOutputContainer();
            if( outputContainer != null ) {
                outputContainer.refreshLocal( IResource.DEPTH_INFINITE, SubMonitor.convert(subMonitor, "Refresh doxygen output folder...", 1) );
            }
            subMonitor.done();

            // Job's done.
            return Status.OK_STATUS;
        } catch(OperationCanceledException e) {
            return Status.CANCEL_STATUS;
        } catch( InvokeException e ) {
            return new Status(
                    Status.WARNING,
                    Plugin.getDefault().getBundle().getSymbolicName(),
                    ERROR_DOXYGEN_NOT_FOUND,
                    "Doxygen was not found.",
                    e );
        } catch( Throwable t ) {
            return new Status(
                    Status.ERROR,
                    Plugin.getDefault().getBundle().getSymbolicName(),
                    0,
                    t.getMessage(),
                    t );
        }
        finally {
            if (doxyIFile != null) {
                getJobManager().endRule(doxyIFile);
            }
        }
    }

    /**
     * Creates resource markers while finding warning and errors in the
     * managed log.
     *
     * @param   monitor	the progress monitor used to watch for cancel requests.
     * @throws  CoreException, URISyntaxException
     */
    private void createMarkers( IProgressMonitor monitor ) throws CoreException, URISyntaxException {
        Matcher			matcher = null;

        // Searches documentation errors and warnings.
        matcher = problemPattern.matcher( log );
        while( matcher.find() == true ) {
            Path		resourcePath = new Path( matcher.group(1) );
            Integer		lineNumer    = new Integer( matcher.group(2) );
            int			severity     = Marker.toMarkerSeverity( matcher.group(3) );
            String		message      = new String( matcher.group(4) );
            createMarkersForResource(resourcePath, null, lineNumer, severity, message);
        }
        matcher = null;

        // Searches obsolete tags warnings.
        matcher = obsoleteTagWarningPattern.matcher( log );
        while( matcher.find() == true ) {
            String		message = new String( matcher.group(0) );
            String		setting = new String( matcher.group(1) );
            Integer		lineNumer = new Integer( matcher.group(2) );
            Path		resourcePath = new Path( matcher.group(3) );
            createMarkersForResource(resourcePath, setting, lineNumer, IMarker.SEVERITY_WARNING, message);
        }
        matcher = null;
    }

    private void createMarkersForResource(Path resourcePath, String setting, Integer lineNumer, int severity, String message) throws CoreException {
        if (resourcePath != null) {
            IWorkspaceRoot  workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
            IFile[] files = workspaceRoot.findFilesForLocationURI(URIUtil.toURI(resourcePath));
            for (IFile file : files) {
                IMarker marker = Marker.create(file, setting, lineNumer.intValue(), message, severity);
                if( marker != null ) {
                    markers.add(marker);
                    break; // exit loop
                }
            }
        }
    }

    /**
     * Notifies observers that the log has been cleared.
     */
    private void fireLogCleared() {
        synchronized ( listeners ) {
            Iterator<IBuildJobListener>	i = listeners.iterator();
            while( i.hasNext() ) {
                IBuildJobListener	listener = (IBuildJobListener) i.next();

                listener.buildJobLogCleared( this );
            }
        }
    }

    /**
     * Notifies observers that the log has been updated with new text.
     *
     * @param newText	a string containing the new text of the log
     */
    private void fireLogUpdated( String newText ) {
        synchronized ( listeners ) {
            Iterator<IBuildJobListener>	i = listeners.iterator();
            while( i.hasNext() ) {
                IBuildJobListener	listener = (IBuildJobListener) i.next();

                listener.buildJobLogUpdated( this, newText );
            }
        }
    }

    /**
     * Notifies observers that the job has been removed.
     */
    private void fireRemoved() {
        synchronized ( listeners ) {
            Iterator<IBuildJobListener>	i = listeners.iterator();
            while( i.hasNext() ) {
                IBuildJobListener	listener = (IBuildJobListener) i.next();

                listener.buildJobRemoved( this );
            }
        }
    }

}
