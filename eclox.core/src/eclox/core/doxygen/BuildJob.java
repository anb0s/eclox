/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2006 Guillaume Brocker

	This file is part of eclox.

    eclox is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    any later version.

    eclox is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with eclox; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
*/

package eclox.core.doxygen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import eclox.core.Plugin;


/**
 * Implement a build job.
 * 
 * @author Guillaume Brocker
 */
public class BuildJob extends Job {
	
	/**
	 * Defines the pattern used to match doxygen warnings and errors
	 */
	private static final Pattern problemPattern = Pattern.compile("^(.+?):\\s*(\\d+)\\s*:\\s*(.+?)\\s*:\\s*(.*)$", Pattern.MULTILINE);
	
	/**
	 * Defines the warning severity string.
	 */
	private static final String SEVERITY_WARNING = "warning";
	
	/**
	 * Defines the warning severity string.
	 */
	private static final String SEVERITY_ERROR = "error";
	
	
	
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
			IResourceDelta	doxyfileDelta = event.getDelta().findMember( job.getDoxyfile().getFullPath() );
			if( doxyfileDelta != null && doxyfileDelta.getKind() == IResourceDelta.REMOVED )
			{
				job.clearMarkers();
				jobs.remove( job );
				job.fireRemoved();
				ResourcesPlugin.getWorkspace().removeResourceChangeListener( this );
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
	private static Collection jobs = new HashSet();
	
	
	/**
	 * the current doxyfile to build
	 */
	private IFile doxyfile;
	
	/**
	 * the buffer containing the whole build output log 
	 */
	private StringBuffer log = new StringBuffer();
	
	/**
	 * a set containing all registered build job listeners
	 */
	private Set listeners = new HashSet();
	
	/**
	 * a collection containing all markers corresponding to doxygen warning and errors
	 */
	private Collection markers = new Vector();
	
	
	/**
	 * Retrieves the marker severity for the given text. The given
	 * text may be warning or error.
	 * 
	 * @param severity	a string to convert to a marker severity
	 * 
	 * @return the marker severity value
	 */
	private static int getMarkerSeverity( String severity )
	{
		if( severity.compareToIgnoreCase(SEVERITY_ERROR) == 0 ) {
			return IMarker.SEVERITY_ERROR;
		}
		else if( severity.compareToIgnoreCase(SEVERITY_WARNING) == 0 ) {
			return IMarker.SEVERITY_WARNING;
		}
		else {
			return IMarker.SEVERITY_ERROR;
		}
	}
	
	
	/**
	 * Constructor.
	 */
	private BuildJob( IFile doxyfile ) {
		super( "Doxygen Build" );
		
		this.doxyfile = doxyfile;
		this.setPriority( Job.BUILD );
		this.setRule( doxyfile );
		
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
	public static BuildJob getJob( IFile doxyfile )
	{
		BuildJob	result = findJob( doxyfile );

		// If no jobs has been found, then creates a new one.
		if( result == null )
		{
			result = new BuildJob( doxyfile );
		}
		
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
	public static BuildJob findJob( IFile doxyfile )
	{
		BuildJob	result = null;
		
		// Walks through the found jobs to find a relevant build job.
		Iterator	i = jobs.iterator();
		while( i.hasNext() )
		{
			BuildJob	buildJob = (BuildJob) i.next();
			if( buildJob.getDoxyfile() == doxyfile )
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
	 * Retrieve the current doxyfile.
	 * 
	 * @return	The current doxyfile.	
	 */
	public IFile getDoxyfile() {
		return this.doxyfile;
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
		Iterator	i = markers.iterator();
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
	 * Retrieves the job's whole log.
	 * 
	 * @return	a string containing the build job's log.
	 */
	public String getLog() {
		return log.toString();
	}
	
	/**
	 * Overrides.
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
	 * Run the job.
	 * 
	 * @param	monitor	The progress monitor to use to report work progression
	 * 					and handle cancelation requests.
	 * 
	 * @return	The job status.
	 */
	protected IStatus run( IProgressMonitor monitor ) {
		try
		{
			Process	buildProcess = Doxygen.build( this.doxyfile );
			
			monitor.beginTask( this.doxyfile.getFullPath().toString(), 4 );
			
			clearLog();
			clearMarkers();
			monitor.worked( 1 );
			
			feedLog( buildProcess, monitor );
			monitor.worked( 2 );
			
			createMarkers( monitor );
			monitor.worked( 3 );
			
			buildProcess.waitFor();
			monitor.worked( 4 );
			monitor.done();
			
			return Status.OK_STATUS;
		}
		catch( Throwable throwable )
		{
			return new Status(
					Status.ERROR,
					Plugin.getDefault().getBundle().getSymbolicName(),
					0, "Unexpected error. " + throwable.toString(),
					throwable );
		}
	}
	
	/**
	 * Creates resource markers while finding warning and errors in the 
	 * managed log.
	 * 
	 * @param	monitor	the progress monitor used to watch for cancel requests.
	 */
	private void createMarkers( IProgressMonitor monitor ) throws CoreException 
	{
		IWorkspaceRoot	workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		Matcher			matcher = problemPattern.matcher( log );
		
		while( matcher.find() == true )
		{
			Path		resourcePath = new Path( matcher.group(1) );
			Integer		lineNumer = new Integer( matcher.group(2) );
			int			severity = getMarkerSeverity( matcher.group(3) );
			String		message = new String( matcher.group(4) );
			IFile		file = workspaceRoot.getFileForLocation( resourcePath );
			
			if( file != null )
			{
				IMarker	marker = file.createMarker( IMarkers.DOXYGEN_MARKER );
				
				marker.setAttribute( IMarker.MESSAGE, message );
				marker.setAttribute( IMarker.LINE_NUMBER, lineNumer.intValue() );
				marker.setAttribute( IMarker.LOCATION, file.getProjectRelativePath().toPortableString() );
				marker.setAttribute( IMarker.PRIORITY, IMarker.PRIORITY_NORMAL );
				marker.setAttribute( IMarker.SEVERITY, severity );
				
				markers.add( marker );
			}
			
		}
	}
		
	/**
	 * Monitors the given process output and appends that to the managed log.
	 * 
	 * @param	process	the process to monitor
	 * @param	monitor	the progress monitor used to watch for cancel requests.
	 */
	private void feedLog( Process process, IProgressMonitor monitor ) throws IOException
	{
		BufferedReader	reader = new BufferedReader( new InputStreamReader(process.getInputStream()) );
		String			newLine;
			
		for(;;)	{
			// Processes a new process output line.
			newLine = reader.readLine();
			if( newLine != null ) {
				// Appends a line ending to the new line.
				newLine = newLine.concat( "\n" );
				
				// Updates the log.
				log.append( newLine );
				fireLogUpdated( newLine );
			}
			else {
				break;
			}
			
			// Tests if the jobs is supposed to terminate.
			if( monitor.isCanceled() == true ) {
				process.destroy();
				break;
			}
		}
	}
	
	/**
	 * Notifies observers that the log has been cleared.
	 */
	private void fireLogCleared() {
		synchronized ( listeners ) {
			Iterator	i = listeners.iterator();
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
			Iterator	i = listeners.iterator();
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
			Iterator	i = listeners.iterator();
			while( i.hasNext() ) {
				IBuildJobListener	listener = (IBuildJobListener) i.next();
				
				listener.buildJobRemoved( this );
			}
		}
	}
	
}
