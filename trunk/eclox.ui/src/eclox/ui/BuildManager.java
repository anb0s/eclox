/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2007 Guillaume Brocker
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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

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
	

	/**
	 * Contains the history the launched build jobs
	 */
	private List jobHistory = new LinkedList();
	
	
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
		
		// Retreives the build job for the given doxyfile.
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
		job.schedule();
	}
	
	/**
	 * Retrieves the latest build jobs that have been registered in the history.
	 * 
	 * @return	an array containing the most recent build jobs.
	 */
	public BuildJob[] getRecentBuildJobs()
	{
		return (BuildJob[]) jobHistory.toArray( new BuildJob[0] );
	}
	
}
