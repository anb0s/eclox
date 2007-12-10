/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2007 Guillaume Brocker
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

package eclox.ui.console.action;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

import eclox.ui.Images;
import eclox.ui.Plugin;
import eclox.ui.console.Console;

/**
 * Implements an action that removes a given console.
 * 
 * @author gbrocker
 */
public class RemoveConsole extends Action {

	/**
	 * the console the action is attached to
	 */
	Console console;
	
	/**
	 * Constructor
	 * 
	 * @param	console	the build console
	 */
	public RemoveConsole( Console console ) {
		super( "Remove", Plugin.getImageDescriptor(Images.REMOVE) );
		this.console = console;
		setToolTipText( "Remove Doxygen Build Console" );
	}
	
	public void run() {
		ConsolePlugin.getDefault().getConsoleManager().removeConsoles( new IConsole[] {console} );
		super.run();
	}
}
