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

package eclox.ui.console;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.console.AbstractConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;

import eclox.core.doxygen.BuildJob;
import eclox.core.doxygen.IBuildJobListener;


/**
 * Implements the doxygen output console
 * 
 * @author Guillaume Brocker
 */
public class Console extends AbstractConsole {

	/**
	 * @brief	Implements a build job listener.
	 */
	private class MyBuildJobListener implements IBuildJobListener {

		/**
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobLogCleared(eclox.core.doxygen.BuildJob)
		 */
		public void buildJobLogCleared(BuildJob job) {}

		/**
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobLogUpdated(eclox.core.doxygen.BuildJob, java.lang.String)
		 */
		public void buildJobLogUpdated(BuildJob job, String output) {}

		/**
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobRemoved(eclox.core.doxygen.BuildJob)
		 */
		public void buildJobRemoved(BuildJob job) {
			final BuildJob	localJob = job;
			
			ConsolePlugin.getStandardDisplay().asyncExec(
					new Runnable() {
						public void run() {
							remove(localJob);		
						}
					}
				);
		}		
	}

	private static Map	consoles = new HashMap();	///< Holds all known console instances.
	private BuildJob	job;						///< the build job
	
	
	/**
	 * Constructor
	 * 
	 * @param	job	the build job whose output must be shown
	 */
	private Console(BuildJob job) {
		super( job.getName(), null );
		this.job = job;
		this.job.addBuidJobListener(new MyBuildJobListener());
	}
	
	/**
	 * @see org.eclipse.ui.console.IConsole#createPage(org.eclipse.ui.console.IConsoleView)
	 */
	public IPageBookViewPage createPage(IConsoleView view) {
		return new ConsolePage(this);
	}
	
	/**
	 * @see org.eclipse.ui.console.AbstractConsole#dispose()
	 */
	protected void dispose() {
		consoles.remove(job);
		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.console.AbstractConsole#init()
	 */
	protected void init() {
		consoles.put(job, this);
		super.init();
	}

	/**
	 * Retrieves the job currently monitored by the console
	 * 
	 * @return	a build job or null if none
	 */
	public BuildJob getJob() {
		return job;
	}
	
	/**
	 * Shows a console for the given job.
	 * 
	 * @param	job	a build that needs a console to be shown
	 */
	static public void show( BuildJob job ) {
		// Retrieves the active workbench window and console manager.
		IConsoleManager		consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		Console				console = null;
		
		if( consoles.containsKey(job) ) {
			console = (Console) consoles.get(job);
		}
		else {
			console = new Console(job);
			consoleManager.addConsoles( new IConsole[] {console} );
		}
		
		// Shows the console view.
		console.activate();
	}
	
	/**
	 * Removes the console for the given build job.
	 * 
	 * @param	job	a build job
	 */
	public static void remove(BuildJob job) {
		if( consoles.containsKey(job) ) {
			Console console = (Console) consoles.get(job);
			
			ConsolePlugin.getDefault().getConsoleManager().removeConsoles( new IConsole[] {console} );
		}
	}
}
