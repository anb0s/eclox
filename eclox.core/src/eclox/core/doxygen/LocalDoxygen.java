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

package eclox.core.doxygen;

import eclox.core.IPreferences;
import eclox.core.Plugin;

/**
 * Implements a concret doxygen interpreted that will wrap a locally installed
 * version of doxygen.
 * 
 * @author Guillaume Brocker
 */
public final class LocalDoxygen extends Doxygen {

	/**
	 * a string containing defining the default doxygen command to use
	 */
	private final static String DEFAULT_COMMAND = "doxygen";
	
	
	/**
	 * @see eclox.core.doxygen.Doxygen#getCommand()
	 */
	public String getCommand() {
		String command = Plugin.getDefault().getPluginPreferences().getString( IPreferences.DOXYGEN_COMMAND );
		
		return command.length() != 0 ? command : DEFAULT_COMMAND;
		
		// TODO support directory of file path in command.
	}
	
	
	/**
	 * @see eclox.core.doxygen.Doxygen#getDescription()
	 */
	public String getDescription() {
		return new String("Local");
	}
	
	
	/**
	 * @see eclox.core.doxygen.Doxygen#getIdentifier()
	 */
	public String getIdentifier() {
		return this.getClass().getName();
	}

}
