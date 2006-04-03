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

package eclox.core.build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

import eclox.core.doxygen.Doxygen;

/**
 * Implement a build job.
 * 
 * @author Guillaume Brocker
 */
public class BuildJob extends Job {
	
	/**
	 * a string that is used to identify the doxygen build job family 
	 */
	public static String FAMILY = "Doxygen Build Job";
	
	
	/**
	 * Implements a runnable object that will read the content of a stream line by line,
	 * prefix and appende each line to the log. 
	 * 
	 * @author Guillaume Brocker
	 */
	private class StreamLogger implements Runnable
	{
		/**
		 * the buffered input stream reader used to get text from the given stream
		 */
		BufferedReader	reader;
		
		/**
		 * Constructor
		 * 
		 * @param process	a process whose outpus will be logged
		 * 
		 * @throws IOException
		 */
		public StreamLogger( InputStream stream ) throws IOException 
		{
			reader = new BufferedReader( new InputStreamReader(stream) );
		}
		
		public void run() {
			for(;;)
			{
				// Reads the new line.
				String	line;
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
		
	};
	
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
	public static BuildJob buildDoxyfile( IFile doxyfile ) /*PartInitException,*/  {
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
	 * Retrieves the build job assocuated to the given doxyfile.
	 * 
	 * @param	doxyfile	a given doxyfile instance
	 * 
	 * @return	a build job that is in charge of building the gven doxyfile, or null
	 * 			when none
	 */
	public static BuildJob findJob( IFile doxyfile )
	{
		// Retrieves all jobs of the build job family.
		IJobManager	jobManager = Platform.getJobManager();
		Job			jobs[] = jobManager.find( FAMILY );
		BuildJob	result = null;
		
		// Walks through the found jobs to find a relevant build job.
		for( int i = 0; i != jobs.length; ++i )
		{
			BuildJob	buildJob = (BuildJob) jobs[i];
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
			Thread	outLogger = new Thread( new StreamLogger(buildProcess.getInputStream()), "Doxygen Output Logger" );
			
			log.delete( 0, log.length() );
			monitor.beginTask( this.doxyfile.getFullPath().toString(), 100 );
			outLogger.start();
			buildProcess.waitFor();
			monitor.done();
			return Status.OK_STATUS;
		}
		catch( Throwable throwable )
		{
			return new Status( Status.ERROR, "!!plugin ID!!", 0, "Unexpected exception", throwable );
		}
	}
	
}
