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

import java.io.File;

/**
 * Implements a custom doxygen interpreter that will wrap a locally installed
 * version of doxygen.
 * 
 * @author Guillaume Brocker
 */
public final class CustomDoxygen extends Doxygen {

	/**
	 * a string containing the location where doxygen should available
	 * 
	 * This can be either a path to a directory or the full path to the 
	 * doxygen binary executable file.
	 */
	private String location;
	
	
	
	/**
	 * Builds a custom doxygen instance from the given identifier
	 * 
	 * @param	identifier	a custom doxygen wrapper identifier
	 * 
	 * @return	a new custom doxygen wrapper instance or null on error
	 */
	public static CustomDoxygen createFromIdentifier( final String identifier ) {
		CustomDoxygen	doxygen = null;
		
		if( identifier.startsWith( CustomDoxygen.class.getName() ) ) {
			final String	location = identifier.substring( identifier.indexOf(' ') + 1 );
			
			doxygen = new CustomDoxygen( location );
		}
		return doxygen;
	}
	
	
	/**
	 * Builds a local doxygen wrapper that uses the given path to search for doxygen.
	 */
	public CustomDoxygen( String location ) {
		this.location = new String( location );
		
		assert( location != null );
		assert( location.length() != 0 );
	}
	
	
	/**
	 * @see eclox.core.doxygen.Doxygen#getCommand()
	 */
	public String getCommand() {
		// Retrieves the real location where doxygen may be.
		File	realLocation = new File( this.location );
		
		// Builds the path.
		if( realLocation.isFile() == true ) {
			return realLocation.getPath();
		}
		else {
			return realLocation.getPath() + File.separator + COMMAND_NAME; 
		}
	}
	
	
	/**
	 * @see eclox.core.doxygen.Doxygen#getDescription()
	 */
	public String getDescription() {
		return "Using location: '" + getLocation() + "'";
	}
	
	
	/**
	 * @see eclox.core.doxygen.Doxygen#getIdentifier()
	 */
	public String getIdentifier() {
		return this.getClass().getName() + " " + location;
	}
	
	
	/**
	 * Retrieves the location of the custom doxygen.
	 */
	public String getLocation() {
		return new String(this.location);
	}

	
	/**
	 * Updates the location of the custom doxygen.
	 */
	public void setLocation( String location ) {
		this.location = new String(location);
	}
}
