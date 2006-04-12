/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2006 Guillaume Brocker
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

package eclox.ui.console;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;

import eclox.core.doxygen.BuildJob;
import eclox.ui.Plugin;


/**
 * Implements the doxygen output console
 * 
 * @author Guillaume Brocker
 */
public class Console extends MessageConsole {

	/**
	 * Implements a job listener that will maintain the console up-to-date
	 * with the job state.
	 * 
	 * @author gbrocker
	 */
	private class MyJobListener implements IJobChangeListener
	{
		private Console console;
		
		public MyJobListener( Console console )
		{
			this.console = console;
		}
		
		public void aboutToRun(IJobChangeEvent event)
		{}

		public void awake(IJobChangeEvent event)
		{}

		public void done(IJobChangeEvent event)
		{}

		public void running(IJobChangeEvent event)
		{
			console.startWatchThread();
		}

		public void scheduled(IJobChangeEvent event)
		{}

		public void sleeping(IJobChangeEvent event)
		{}
		
	}
	
	private class MyJobWatchThread implements Runnable {
		
		private Console console;
		
		public MyJobWatchThread( Console console )
		{
			this.console = console;	
		}
		
		public void run()
		{
			clearConsole();
			watchJobLog();
		}
		
		private void clearConsole()
		{
			ConsolePlugin.getStandardDisplay().syncExec(
						new Runnable()
						{
							public void run()
							{
								console.clearConsole();		
							}
						}
					);
		}
		
		private void watchJobLog()
		{
			try {				
				IOConsoleOutputStream	stream = console.newOutputStream();
				Pipe.SourceChannel		sourceChannel = job.newOutputChannel();
				ByteBuffer				buffer = ByteBuffer.allocate( 64 );
				
				for(;;) {
					int	readLength;
					
					buffer.clear();
					readLength = sourceChannel.read( buffer );
					
					if( readLength > 0 ) {
						byte[]	array = new byte[ buffer.position() ];
						
						buffer.flip();
						buffer.get( array );
						stream.write( array );
					}
					else if( readLength == 0 ) {
						Thread.yield();
					}
					else {
						break;
					}
				}
				
				sourceChannel.close();
				stream.flush();
			}
			catch( IOException io ) {
				Plugin.log( io );
			}
		}
	}
		
	/**
	 * the current build job
	 */
	private BuildJob job;
	
	/**
	 * the current build job listener
	 */
	private MyJobListener jobListener;
	
	/**
	 * Constructor
	 * 
	 * @param	job	a build job whose log will be shown
	 */
	public Console( BuildJob job )
	{
		super( job.getDoxyfile().getFullPath().toString() + " [Doxygen Build]", null );
		
		this.job = job;
		this.jobListener = new MyJobListener(this);
		this.job.addJobChangeListener( jobListener );
	}
	
	private void startWatchThread() {
		Thread logReaderThread = new Thread( new MyJobWatchThread(this), "Doxygen Build Console Feeder" );
		logReaderThread.start();
	}
	
}
