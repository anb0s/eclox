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

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;


/**
 * Implements the doxygen output console
 * 
 * @author Guillaume Brocker
 */
public class Console extends MessageConsole {
	
	/**
	 * the default console instance
	 */
	private static Console console = new Console();
	
	/**
	 * Shows the doxygen console
	 *
	 */
	public static void show()
	{
		IConsoleManager	manager = ConsolePlugin.getDefault().getConsoleManager();
		
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
	}

	public Console()
	{
		super( "Doxygen", null );
	}

	protected IConsoleDocumentPartitioner getPartitioner()
	{
		return null;
	}
}
