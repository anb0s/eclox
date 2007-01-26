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

import java.io.File;

import eclox.core.IPreferences;
import eclox.core.Plugin;

/**
 * Implements a custom doxygen interpreter that will wrap a locally installed
 * version of doxygen.
 * 
 * @author Guillaume Brocker
 */
public final class CustomDoxygen extends Doxygen {

	/**
	 * a string containing defining the default doxygen command to use
	 */
	private final static String DEFAULT_COMMAND = "doxygen";
	
	/**
	 * a string containing the location where doxygen should available
	 * 
	 * This can be either a path to a directory or the full path to the 
	 * doxygen binary executable file. When null, the IPreferences.DOXYGEN_COMMAND
	 * will be used.
	 */
	private String location;
	
	
	/**
	 * Builds a local doxygen wrapper that uses the user configured location
	 * to search for available doxygen.
	 */
	public CustomDoxygen() {
		location = null;
	}
	
	
	/**
	 * Builds a local doxygen wrapper that uses the given path to search for doxygen.
	 */
	public CustomDoxygen( String location ) {
		this.location = new String( location );
	}
	
	
	/**
	 * @see eclox.core.doxygen.Doxygen#getCommand()
	 */
	public String getCommand() {
		// Retrieves the real location where doxygen may be.
		File	realLocation;		
		
		if( location == null ) {
			final String preference = Plugin.getDefault().getPluginPreferences().getString( IPreferences.DOXYGEN_COMMAND );
			
			realLocation = new File( preference );
		}
		else {
			realLocation = new File( this.location );
		}

		// Builds the path.
		if( realLocation.getPath().length() == 0 ) {
			return DEFAULT_COMMAND;
		}
		else if( realLocation.isFile() == true ) {
			return realLocation.getPath();
		}
		else {
			return realLocation.getPath() + File.separator + DEFAULT_COMMAND; 
		}
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
