/*
	eclox
	Copyright (C) 2003 Guillaume Brocker

	This file is part of eclox.

	eclox is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	any later version.

	eclox is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with eclox; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
*/

package eclox.doxyfile.node.value;

import eclox.util.ListenerManager;

/**
 * Provide the abstract interface for all tag values.
 * 
 * @author gbrocker
 */
public abstract class Abstract extends ListenerManager {
	/**
	 * Constructor.
	 */
	public Abstract() {
		super( Listener.class );
	}
	
	/**
	 * Register a new listener.
	 * 
	 * @param	listener	The new listener to register.
	 */
	public void addListener( Listener listener ) {
		super.addListener( listener );
	}
 
	/**
	 * Remove a listener.
	 * 
	 * @param	listener	The listener to remove.
	 */
	public void removeListener( Listener listener ) {
		super.removeListener( listener );
	}

	/**
	 * Retieve the value as a string.
	 * 
	 * @return	A string containing a value representation. 
	 */
	public abstract java.lang.String toString();
	
	/**
	 * Parse the specified string to get the reprsented value.
	 * Implementations should send a notifications was the value has effectively changed.
	 * 
	 * @param value	The string containing a value representation to parse.
	 */
	public abstract void fromString( java.lang.String value );
	
	/**
	 * Notify all listeners that the value has changed.
	 */
	protected void notifyChanged() {
		fireEvent( new Event(this), "valueChanged" );
	}
}
