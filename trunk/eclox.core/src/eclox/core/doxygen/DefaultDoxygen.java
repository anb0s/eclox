/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/ 

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
