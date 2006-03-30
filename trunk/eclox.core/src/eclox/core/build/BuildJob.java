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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import eclox.core.doxygen.Doxygen;

/**
 * Implement a build job.
 * 
 * @author Guillaume Brocker
 */
public class BuildJob extends Job {
	
	/**
	 * A string buffer containing the job's log.
	 */
	private StringBuffer log = new StringBuffer();
	
	/**
	 * Implements a runnable object that will log output and
	 * error streams of the given process.
	 * 
	 * @author Guillaume Brocker
	 */
	private class ProcessStreamsLogger implements Runnable
	{
		private Selector		selector = Selector.open();
				
		/**
		 * Constructor
		 * 
		 * @param process	a process whose outpus will be logged
		 * 
		 * @throws IOException
		 */
		public ProcessStreamsLogger( Process process ) throws IOException 
		{
			SelectableChannel channel;
			
			channel = (SelectableChannel) Channels.newChannel( process.getInputStream() );
			channel.register( selector, channel.validOps() );
			
			channel = (SelectableChannel) Channels.newChannel( process.getErrorStream() );
			channel.register( selector, channel.validOps() );
		}
		
		public void run() {
			for(;;)
			{
				try
				{
					// Selects channels ready to read.
					selector.select();
					
					// Retrieves the iterator on the ready channels.
					Iterator		it = selector.selectedKeys().iterator();
					while( it.hasNext() )
					{
						// Retrieves usefull objects 
						SelectionKey			key = (SelectionKey) it.next();
						ReadableByteChannel	channel = (ReadableByteChannel) key.channel();
						ByteBuffer			buffer = ByteBuffer.allocate( 1024 );

						// Reads the channel content and append it to the log.
						channel.read( buffer );
						log.append( buffer.toString() );
						
						// Removes the current selection from the selected keys.
						it.remove();
					}
				}
				catch( IOException e )
				{
					break;
				}
			}			
		}
		
	};
	
	/**
	 * The global build job instance.
	 */
	private static BuildJob defaultInstance = new BuildJob();
	
	/**
	 * The current doxyfile to build.
	 */
	private IFile doxyfile;
	
	/**
	 * Constructor.
	 */
	private BuildJob() {
		super("Doxygen Build");
		this.setPriority(Job.BUILD);
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
	public static void buildDoxyfile( IFile doxyfile ) throws /*PartInitException,*/ BuildInProgressError {
		Preferences preferences = eclox.core.Plugin.getDefault().getPluginPreferences();
		String		autoSaveValue = preferences.getString( eclox.core.preferences.Names.AUTO_SAVE );
		
//		 TODO eclipse split refactoring
//		if( autoSaveValue == eclox.core.preferences.Values.AUTO_SAVE_ASK ) {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( true );
//		}
//		else if( autoSaveValue == eclox.core.preferences.Values.AUTO_SAVE_ALWAYS ) {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( false );
//		}
		
		BuildJob.getDefault().setDoxyfile(doxyfile);
		BuildJob.getDefault().schedule();
	}

	
	/**
	 * Retrieve the current build job instance.
	 * 
	 * @return	The current build job instance.
	 */
	public static BuildJob getDefault() {
		return BuildJob.defaultInstance;
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
	 * Run the job.
	 * 
	 * @param	monitor	The progress monitor to use to report work progression
	 * 					and handle cancelation requests.
	 * 
	 * @return	The job status.
	 */
	protected IStatus run(IProgressMonitor monitor) {
		try
		{
			Process buildProcess = Doxygen.build(this.doxyfile);
			Thread	outputLogger = new Thread( new ProcessStreamsLogger(buildProcess) );
	
			monitor.beginTask(this.doxyfile.getFullPath().toString(), 100);
			outputLogger.start();
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
	 * Set the next doxyfile to build.
	 * 
	 * @param	doxyfile	The next doxyfile to build.
	 */
	public void setDoxyfile(IFile doxyfile) throws BuildInProgressError {
		if(this.getState() == Job.NONE) {
			this.doxyfile = doxyfile;
		}
		else {
			throw new BuildInProgressError();
		}
	}
	
}
