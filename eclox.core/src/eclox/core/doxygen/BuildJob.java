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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
	 * Retrieves all doxygen build jobs.
	 * 
	 * @return	an arry containing all doxygen build jobs (can be empty).
	 */
	public static BuildJob[] getAllJobs()
	{
		return (BuildJob[]) jobs.toArray( new BuildJob[0] );
	}

	
	/**
	 * the current doxyfile to build
	 */
	private IFile doxyfile;
	
	/**
	 * the collection of pipes used by clients to receive the build job output while running
	 */
	private Collection pipes = new Vector();
	
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
	 * Retrieves the build job associated to the given doxyfile. If needed,
	 * a new job will be created.
	 * 
	 * @param	doxyfile	a given doxyfile instance
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
	 * Retrieve the current doxyfile.
	 * 
	 * @return	The current doxyfile.	
	 */
	public IFile getDoxyfile() {
		return this.doxyfile;
	}
	
	/**
	 * Creates a new channel where a thread can read the ouput of the build job.
	 * 
	 * If the job has already started, some parts of the log will be lost.
	 * 
	 * @return	a channel where the output of the build job can be retrieved.
	 */
	public SourceChannel newOutputChannel() throws IOException
	{
		synchronized( pipes ) {
			Pipe	pipe = Pipe.open();
			
			pipes.add( pipe );
			return pipe.source();
		}
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
			
			// Wait a little bit to allow client thread to get synchronized.
			Thread.sleep( 1000 );
						
			monitor.beginTask( this.doxyfile.getFullPath().toString(), 100 );
			feedPipes( buildProcess.getInputStream() );
			buildProcess.waitFor();
			monitor.done();
			closePipes();

			return Status.OK_STATUS;
		}
		catch( Throwable throwable )
		{
			return new Status( Status.ERROR, "!!plugin ID!!", 0, "Unexpected exception", throwable );
		}
	}
	
	
	/**
	 * Closes and removes all registered pipes.
	 */
	private void closePipes()
	{
		synchronized ( pipes ) {
			// Closes all sink pipe ends.
			Iterator	i = pipes.iterator();
			while( i.hasNext() ) {
				Pipe	pipe = (Pipe) i.next();
				
				try {
					pipe.sink().close();
				}
				catch( IOException io ) {
					Plugin.log( io );
				}
			}
			
			// Clears the registered pipes.
			pipes.clear();
		}
	}
	
	/**
	 * Forward the given stream to the registered pipes.
	 * 
	 * @param stream	the stream to log
	 */
	private void feedPipes( InputStream stream )
	{
		try {
			ReadableByteChannel	sourceChannel = Channels.newChannel( stream );
			ByteBuffer			buffer = ByteBuffer.allocate( 64 );
			
			for(;;)	{
				int	readLength;
				
				buffer.clear();
				readLength = sourceChannel.read( buffer );
				
				// If there is a line, appends it to the log.
				if( readLength > 0 ) {
					synchronized ( pipes ) {
						buffer.flip();
						Iterator	i = pipes.iterator();
						while( i.hasNext() ) {
							Pipe	pipe = (Pipe) i.next();
							buffer.rewind();
							pipe.sink().write( buffer );
						}
					}
				}
				else if( readLength == 0 ) {
					Thread.yield();
				}
				else {
					break;
				}
			}
		}
		catch( IOException io ) {
			Plugin.log( io );
		}
	}
	
}
