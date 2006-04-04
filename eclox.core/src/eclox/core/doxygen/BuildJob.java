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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;


/**
 * Implement a build job.
 * 
 * @author Guillaume Brocker
 */
public class BuildJob extends Job {
	
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
				jobs.remove( job );
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
	 * A string buffer containing the job's log.
	 */
	private StringBuffer log = new StringBuffer();
	
	/**
	 * Constructor.
	 */
	private BuildJob( IFile doxyfile) {
		super( "Doxygen Build" );
		
		this.doxyfile = doxyfile;
		this.setPriority( Job.BUILD );
		this.setRule( doxyfile );
		
		// References the jobs in the global collection and add a doxyfile listener.
		jobs.add( this );
		ResourcesPlugin.getWorkspace().addResourceChangeListener( new MyResourceChangeListener(this), IResourceChangeEvent.POST_CHANGE );
	}
	
	/**
	 * Launches the build of the specified file.
	 *
	 * @param	doxyfile	a file resource to build
	 * 
	 * @throws PartInitException	the build log view could not be displayed
	 * @throws BuildInProgreeError	a build is already in progrees
	 * 
	 * @author gbrocker
	 */
	public static BuildJob scheduleBuild( IFile doxyfile ) /*PartInitException,*/  {
//		 TODO eclipse split refactoring
//		Preferences preferences = eclox.core.Plugin.getDefault().getPluginPreferences();
//		String		autoSaveValue = preferences.getString( eclox.core.preferences.Names.AUTO_SAVE );
//		
//		if( autoSaveValue == eclox.core.preferences.Values.AUTO_SAVE_ASK ) {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( true );
//		}
//		else if( autoSaveValue == eclox.core.preferences.Values.AUTO_SAVE_ALWAYS ) {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( false );
//		}
		
		// If no current job exists for the given doxyfile, then creates a new one.
		BuildJob	buildJob = findJob( doxyfile );
		if( buildJob == null )
		{
			buildJob = new BuildJob( doxyfile );
		}
		buildJob.schedule();
		
		// Job's done.
		return buildJob;
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
	 * Retrieves the build job assocuated to the given doxyfile.
	 * 
	 * @param	doxyfile	a given doxyfile instance
	 * 
	 * @return	a build job that is in charge of building the gven doxyfile, or null
	 * 			when none
	 */
	public static BuildJob findJob( IFile doxyfile )
	{
		// Walks through the found jobs to find a relevant build job.
		BuildJob	result = null;
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
		
		// Job's done.
		return result;
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
	 * Retrives the build log of the job.
	 * 
	 * @return	the build log
	 */
	public String getLog() {
		return log.toString();
	}
	
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
			Process	buildProcess = Doxygen.build(this.doxyfile);
			
			monitor.beginTask( this.doxyfile.getFullPath().toString(), 100 );
			logStream( buildProcess.getInputStream() );
			buildProcess.waitFor();
			monitor.done();
			return Status.OK_STATUS;
		}
		catch( Throwable throwable )
		{
			return new Status( Status.ERROR, "!!plugin ID!!", 0, "Unexpected exception", throwable );
		}
	}
	
	/**
	 * Logs the given stream content until the stream gets closed.
	 * 
	 * @param stream	the stream to log
	 */
	private void logStream( InputStream stream )
	{
		BufferedReader	reader = new BufferedReader( new InputStreamReader(stream) );
		String			line;
		
		log.delete( 0, log.length() );
		for(;;)
		{
			// Reads the new line.
			try
			{
				line = reader.readLine();
			}
			catch( IOException e )
			{
				break;
			}
			
			// If there is a line, appends it to the log.
			if( line != null )
			{
				log.append( line );
				log.append( "\n" );
			}
			else
			{
				break;
			}	
		}
	}
	
}
