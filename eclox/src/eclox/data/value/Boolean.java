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

package eclox.data.value;

/**
 * Implement a boolean tag value class.
 * 
 * @author gbrocker
 */
public class Boolean extends Abstract {
	/**
	 * The true string representation.
	 */
	private static final java.lang.String TRUE = new java.lang.String( "YES" );
	
	/**
	 * The false string representation.
	 */
	private static final java.lang.String FALSE = new java.lang.String( "NO" );
	
	/**
	 * The real boolean value.
	 */
	private boolean m_value = false;
	
	/**
	 * Parse the specified string and extract the value.
	 * 
	 * @param	value	A string containing the text value reppresentation.
	 */
	public void fromString( java.lang.String value ) {
		if( value.equalsIgnoreCase( TRUE ) == true ) {
			m_value = true;
			notifyChanged();
		}
		else if( value.equalsIgnoreCase( FALSE ) == true ) {
			m_value = false;
			notifyChanged();
		}
	}
	
	/**
	 * Retrieve the boolean value.
	 * 
	 * @return	The booelan value.
	 */
	public boolean get() {
		return m_value;
	}
	
	/**
	 * Update the boolean value.
	 * 
	 * @param value	The new boolean value to set.
	 */
	public void set( boolean value ) {
		m_value = value;
		notifyChanged();
	}
	
	/**
	 * Convert the value to a string.
	 * 
	 * @return	A string containing the value text representation.
	 */
	public java.lang.String toString() {
		return m_value == true ? TRUE : FALSE;
	}
}
