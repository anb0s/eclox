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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import eclox.core.build.BuildJob;
import eclox.ui.Plugin;


/**
 * Implements the doxygen output console
 * 
 * @author Guillaume Brocker
 */
public class Console extends MessageConsole {
	
	/**
	 * Shows the doxygen console and return that console
	 * 
	 * @return	the console that has been shown
	 */
	public static Console show()
	{
		IConsoleManager	manager = ConsolePlugin.getDefault().getConsoleManager();
		Console			console = Plugin.getDefault().getConsole();
		
		// Searches if the consoles is alreay registered.
		IConsole	[]		existings = manager.getConsoles();
		boolean			exists = false;
		for (int i = 0; i < existings.length; i++) {
			if(console == existings[i])
				exists = true;
		}
		
		// Adds the console if not already registered.
		if( exists == false )
		{
			manager.addConsoles( new IConsole[] { console } );
		}
		
		// Finaly shows the console to the world.
		manager.showConsoleView( console );
		return console;
	}
	
	/**
	 * Implements a runnable object that will monitor the log of the given
	 * build job and append new entries to the given message console stream
	 * 
	 * @author Guillaume Brocker
	 */
	class LogMonitor implements Runnable
	{
		private BuildJob				job;
		private MessageConsoleStream	stream;
		private int current				= 0;
		
		public LogMonitor( BuildJob job, MessageConsoleStream stream )
		{
			this.job = job;
			this.stream = stream;
			
			schedule();
		}
		
		public void run() {
			String	log = job.getLog();
			int		logLength = log.length(); 
			if( current != logLength )
			{
				stream.print( log.substring(current) );
				current = logLength;
			}
			
			schedule();			
		}
		
		private void schedule()
		{
			if( job.getState() != Job.NONE )
			{
				ConsolePlugin.getStandardDisplay().timerExec( 125, this );
			}
		}		
	}

	/**
	 * the current build job
	 */
//	private BuildJob job;
	
	/**
	 * Constructor
	 */
	public Console()
	{
		super( "Doxygen", null );
	}
	
	/**
	 * Updates the current build job whose log will be displayed or null if none
	 * 
	 * @param	job	a doxygen build job or null if none
	 */
	public void setJob( BuildJob job )
	{
		// References the new job.
//		this.job = job;
		
		// Updates the console name.
		String	name = "Doxyfile";
		if( job != null )
		{
			name = name.concat( " [" + job.getDoxyfile().getFullPath().toString() + "]" );
		}
		setName( name );
		clearConsole();
		
		new LogMonitor( job, newMessageStream() );
	}
}
