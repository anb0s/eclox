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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.MessageConsole;

import eclox.core.doxygen.BuildJob;


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
			updateConsoleName();
			watchJobLog();
			updateConsoleName();
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
				int						logOffset = 0;
				IOConsoleOutputStream	stream = console.newOutputStream();
				
				for(;;) {
					// Waits for the log to change.
					console.job.waitLog();
					
					// Retrieves the log.
					String	log			= console.job.getLog();
					int		logLength	= log.length();
					
					// Puts the log changes to the console.
					if( logLength > logOffset ) {
						stream.write( log.substring(logOffset) );
						stream.flush();
						logOffset = logLength;
					}
					else {
						break;
					}
				}
			}
			catch( IOException io ) {
				// TODO log.
			}
			catch( InterruptedException interrupted ) {
				// Nothing to do.
			}
		}
		
		private void updateConsoleName()
		{
			ConsolePlugin.getStandardDisplay().syncExec(
						new Runnable()
						{
							public void run()
							{
								console.updateName();		
							}
						}
					);
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
		super( "Doxygen", null );
		
		this.job = job;
		this.jobListener = new MyJobListener(this);
		this.job.addJobChangeListener( jobListener );
	}
	
	/**
	 * Updates the name of the console from its current job
	 */
	private void updateName()
	{
		// Pre-condition
		assert job != null;
		
		String name = new String();
		name = name.concat( job.getDoxyfile().getFullPath().toString() );
		name = name.concat( " [Doxygen Build]" );		
		if( job.getState() == Job.NONE && job.isLogEmpty() == false ) {
			name = new String("<done> ").concat( name ); 
		}
		setName( name );
	}
	
	private void startWatchThread() {
		Thread logReaderThread = new Thread( new MyJobWatchThread(this) );
		logReaderThread.start();
	}
	
}
