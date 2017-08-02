/*******************************************************************************
 * Copyright (C) 2003-2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - Add ability to use Doxyfile not in project scope
 *                   - Refactoring of deprecated API usage
 *
 ******************************************************************************/

package eclox.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxygen.BuildJob;
import eclox.core.doxygen.IBuildJobListener;
import eclox.ui.console.Console;

/**
 * This class is responsible to launch the build for the given doxyfile
 * and also manages the build history.
 *
 * @see		Plugin
 *
 * @author	gbrocker
 */
public class BuildManager {

	/**
	 * Implements a job listener that will update the job history as jobs get removed.
	 */
	private class MyJobListener implements IBuildJobListener
	{

		public void buildJobLogCleared(BuildJob job) {
		}

		public void buildJobLogUpdated(BuildJob job, String output) {
		}

		public void buildJobRemoved(BuildJob job) {
			jobHistory.remove( job );
			job.removeBuidJobListener( this );
		}

	}

	/**
	 * Implements a preference listener that will maintain the length of the job history
	 * according to the relevant preference changes.
	 */
	private class MyPreferenceListener implements IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			if( event.getProperty() == IPreferences.BUILD_HISTORY_SIZE )
			{
				int	newSize = ((Integer) event.getNewValue()).intValue();
				int curSize = jobHistory.size();
				if( curSize > newSize ) {
				    jobHistory = jobHistory.subList( curSize - newSize, curSize );
				}
			}
		}
	}

	private static String STATE_FILE = new String("build-job-history.txt");
	private List<BuildJob> jobHistory = new LinkedList<BuildJob>();						///< Contains the history the launched build jobs.

	/**
	 * Constructor
	 */
	public BuildManager()
	{
		// Attaches a property change listener on the plugin's preference store.
		Plugin.getDefault().getPreferenceStore().addPropertyChangeListener( new MyPreferenceListener() );
	}

	/**
	 * Launches the build of the given doxyfile.
	 *
	 * @param	doxyfile	the doxyfile to build
	 */
	public void build( Doxyfile doxyfile )
	{
		// Retrieves the plug-in preferences.
	    IPreferenceStore preferences = Plugin.getDefault().getPreferenceStore();

		// Ask the user if he wants to save all opened editors before proceeding to build.
		final String	autoSave = preferences.getString( IPreferences.AUTO_SAVE );
		if( autoSave.equals( IPreferences.AUTO_SAVE_ALWAYS ) )
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( false );
		}
		else if( autoSave.equals( IPreferences.AUTO_SAVE_ASK ) )
		{
			boolean saved;

			saved = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( true );
			if( saved == false )
			{
				return;
			}
		}
		else if( autoSave.equals( IPreferences.AUTO_SAVE_NEVER) )
		{
			// Nothing to perform.
		}
		else
		{
			// Not supported.
			assert( false );
			Plugin.log( autoSave + ": unsupported auto save state.");
		}

		// Retrieves the build job for the given doxyfile.
		BuildJob job = BuildJob.getJob(doxyfile);

		// Attaches a listener if applicable.
		if( jobHistory.contains(job) == false ) {
			job.addBuidJobListener( new MyJobListener() );
		}

		// Updates the job history.
		int	preferedHistorySize = preferences.getInt( IPreferences.BUILD_HISTORY_SIZE );

		jobHistory.remove(job);
		if( jobHistory.size() >= preferedHistorySize && jobHistory.isEmpty() == false ) {
			jobHistory.remove( 0 );
		}
		jobHistory.add(job);

		// Updates the console.
		Console.show(job);

		// Schedule the job to build.
		job.schedule(1000);
	}

	/**
	 * Retrieves the latest build jobs that have been registered in the history.
	 *
	 * @return	an array containing the most recent build jobs.
	 */
	public BuildJob[] getRecentBuildJobs() {
		return (BuildJob[]) jobHistory.toArray(new BuildJob[0]);
	}

    /**
     * Retrieves the latest build jobs that have been registered in the history.
     *
     * @return  an array containing the most recent build jobs REVERSED
     */
    public BuildJob[] getRecentBuildJobsReversed() {
        BuildJob[] jobs = getRecentBuildJobs();
        for(int i = 0; i < jobs.length / 2; i++)
        {
            BuildJob temp = jobs[i];
            jobs[i] = jobs[jobs.length - i - 1];
            jobs[jobs.length - i - 1] = temp;
        }
        return jobs;
    }

	/**
	 * Restores the state from a file.
	 */
	public void restoreState() {
		try {
			IPath	statePath = Plugin.getDefault().getStateLocation().append(STATE_FILE);
			File	stateFile = new File( statePath.toOSString() );

			if( stateFile.canRead() == true ) {
				BufferedReader	stateReader = new BufferedReader(new FileReader(stateFile));
				String			stateLine = stateReader.readLine();
				while( stateLine != null ) {
					IPath doxyfilePath= new Path( stateLine );
					IFile doxyIFile = null;
					File doxyFile = null;
					IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(doxyfilePath);
					if( Doxyfile.isDoxyfile(resource) ) {
					    doxyIFile = (IFile)resource;
					} else {
					    doxyFile = new File(stateLine);
					}
					Doxyfile doxyfile = new Doxyfile(doxyIFile, doxyFile);
					BuildJob job = BuildJob.getJob(doxyfile);
					if (jobHistory.isEmpty()) {
					    jobHistory.add(job);
					} else {
					    boolean contains = false;
    					for(int i=0;i<jobHistory.size();i++) {
    					    BuildJob jobOld = jobHistory.get(i);
    					    if (jobOld.getDoxyfile().equals(job.getDoxyfile())) {
    					        contains = true;
    					        break;
    					    }
    					}
    					if (!contains) {
    					    jobHistory.add(job);
    					}
					}
					stateLine = stateReader.readLine();
				}
				stateReader.close();
			}
		}
		catch( Throwable t ) {
			Plugin.log( t );
		}
	}


	/**
	 * Saves the state to a file.
	 */
	public void saveState() {
		IPath	statePath = Plugin.getDefault().getStateLocation().append(STATE_FILE);
		File	stateFile = new File( statePath.toOSString() );

		stateFile.delete();

		// Now saves the resource path of the next doxyfile to build.
		try
		{
			FileWriter	stateWriter = new FileWriter( stateFile );
			Iterator<BuildJob>	i = jobHistory.iterator();

			while( i.hasNext() ) {
				BuildJob job	  = (BuildJob) i.next();
				Doxyfile doxyfile = job.getDoxyfile();
				stateWriter.write( doxyfile.getFullPath() );
				stateWriter.write( String.valueOf('\n') );
			}

			stateWriter.flush();
			stateWriter.close();
		}
		catch( Throwable t ) {
			Plugin.log(t);
		}
	}

    public void removeAll(BuildJob[] objects) {
        if (!jobHistory.isEmpty()) {
            Collection<BuildJob> collection = Arrays.asList(objects);
            jobHistory.removeAll(collection);
        }
    }

    public void removeAll() {
        if (!jobHistory.isEmpty()) {
            jobHistory.clear();
        }
    }

}
