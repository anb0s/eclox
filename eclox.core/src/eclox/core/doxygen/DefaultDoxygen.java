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

/**
 * Implements the doxygen wrapper that use the eventual doxygen program reachable
 * in the default path.
 * 
 * @author gbrocker
 */
public class DefaultDoxygen extends Doxygen {
	
	/**
	 * Creates a new default doxygen instance from the given identifier.
	 * 
	 * @param	identifier	a string containing an identifier
	 * 
	 * @return	a new default doxygen instance or null when creation failed
	 */
	public static DefaultDoxygen createFromIdentifier( final String identifier ) {
		if( DefaultDoxygen.class.getName().equalsIgnoreCase( identifier ) ) {
			return new DefaultDoxygen();
		}
		else {
			return null;
		}
	}

	/**
	 * @see eclox.core.doxygen.Doxygen#getCommand()
	 */
	public String getCommand() {
		return COMMAND_NAME;
	}

	/**
	 * @see eclox.core.doxygen.Doxygen#getDescription()
	 */
	public String getDescription() {
		return new String();
	}

	public String getIdentifier() {
		return getClass().getName();
	}

}
