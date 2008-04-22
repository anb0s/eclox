/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003, 2004, 2005, 2006, 2007, 2008, Guillaume Brocker
 * 
 * This file is part of eclox.
 * 
 * eclox is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 * 
 * eclox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with eclox; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
 */

package eclox.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
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
	

	private static String		STATE_FILE = new String("build-job-history.txt");
	private List 				jobHistory = new LinkedList();						///< Contains the history the launched build jobs. 
	
	
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
	public void build( IFile doxyfile )
	{
		// Retrieves the plug-in preferences.
		Preferences	preferences = Plugin.getDefault().getPluginPreferences();
		
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
		BuildJob		job = BuildJob.getJob( doxyfile );
		
		// Attaches a listener if applicable.
		if( jobHistory.contains(job) == false ) {
			job.addBuidJobListener( new MyJobListener() );
		}
		
		// Updates the job history.
		int	preferedHistorySize = preferences.getInt( IPreferences.BUILD_HISTORY_SIZE );
		
		jobHistory.remove( job );
		if( jobHistory.size() >= preferedHistorySize && jobHistory.isEmpty() == false ) {
			jobHistory.remove( 0 );
		}
		jobHistory.add( job );
		
		// Updates the console.
		Console.show( job );
		
		// Schedule the job to build.
		job.schedule(1000);
	}
	
	
	/**
	 * Retrieves the latest build jobs that have been registered in the history.
	 * 
	 * @return	an array containing the most recent build jobs.
	 */
	public BuildJob[] getRecentBuildJobs() {
		return (BuildJob[]) jobHistory.toArray( new BuildJob[0] );
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
					IPath			doxyfilePath	= new Path( stateLine );
					IResource		resource		= ResourcesPlugin.getWorkspace().getRoot().findMember(doxyfilePath);
					
					if( Doxyfile.isDoxyfile(resource) ) {
						IFile		doxyfile = (IFile) resource;
						BuildJob	job = BuildJob.getJob(doxyfile);
						
						jobHistory.add(job);
					}
					
					stateLine = stateReader.readLine();
				}
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
			Iterator	i = jobHistory.iterator();
			
			while( i.hasNext() ) {
				BuildJob	job			= (BuildJob) i.next();
				IFile		doxyfile	= job.getDoxyfile();
				
				stateWriter.write( doxyfile.getFullPath().toString() );
				stateWriter.write( String.valueOf('\n') );
			}
			
			stateWriter.flush();
		}
		catch( Throwable t ) {
			Plugin.log(t);
		}
	}
	
}
