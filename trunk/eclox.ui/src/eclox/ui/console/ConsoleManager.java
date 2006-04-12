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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxygen.BuildJob;

/**
 * Manages all consoles that shows doxygen build logs.
 * 
 * @author gbrocker
  */
public final class ConsoleManager {

	/**
	 * Implements a selection listener that will update the current console job
	 * according to the selection in the workbench.
	 * 
	 * @author	Guillaume Brocker
	 */
	private class MySelectionListener implements ISelectionListener
	{
		private ConsoleManager consoleManager;
		
		public MySelectionListener( ConsoleManager consoleManager )
		{
			this.consoleManager = consoleManager;
		}
		
		public void selectionChanged( IWorkbenchPart part, ISelection selection ) {
			if( selection != null && selection instanceof IStructuredSelection ) {
				IStructuredSelection	structuredSelection = (IStructuredSelection) selection;
				
				if( structuredSelection != null && structuredSelection.isEmpty() == false ) {
					Object	element = structuredSelection.getFirstElement();
				
					if( element != null && element instanceof IFile ) {
						IFile	file = (IFile) element;
					
						if( Doxyfile.isDoxyfile(file) ) {
							BuildJob job = BuildJob.findJob( file );
						
							if( job != null ) {
								consoleManager.addConsole( job );
							}
						}
					}
				}
			}
		}
		
	}

	/**
	 * Holds all managed consoles.
	 */
	private Map	consoles = new HashMap();
	
	
	/**
	 * Constructor.
	 */
	public ConsoleManager()
	{
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addPostSelectionListener( new MySelectionListener(this) );
	}
	
	/**
	 * Adds a new console for the given. The method returns immediatly is
	 * there is already a console for the given job.
	 * 
	 * @param	job	a given build job
	 * 
	 * @return	the console attached to the given job
	 */
	public Console addConsole( BuildJob job )
	{
		IConsoleManager	manager = ConsolePlugin.getDefault().getConsoleManager();
		Console			console = (Console) consoles.get( job );
		
		// Creates the console if none exists for the given job.
		if( console == null ) {
			console = new Console( job );
			consoles.put( job, console );
			manager.addConsoles( new IConsole[] { console } );
		}
		return console;
	}
	
	/**
	 * Shows the console attached to the given build job. The console
	 * may be created if none is already attached.
	 * 
	 * @param	job	a build job
	 * 
	 * @return	the console attached to the given build job
	 */
	public Console showConsole( BuildJob job )
	{	
		// Shows the console and the job's done!
		IConsoleManager	manager = ConsolePlugin.getDefault().getConsoleManager();
		Console			console = addConsole(job);
		
		manager.showConsoleView( console );
		return console;
	}
	
}
