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

/**
 * Implement an integer value class.
 * 
 * @author gbrocker
 */
public class Integer extends Value {
	/**
	 * The real integer value.
	 */
	private int m_value = 0;
	
	/**
	 * Clone the value instance, but not the listeners.
	 * 
	 * @return	A cloned instance of the value.
	 */
	public Object clone() {
		Integer i = new Integer();
		
		i.m_value = this.m_value;
		return i;
	}

	/**
	 * Retrieve the integer value from the specified string. The integer
	 * must be represented in base 10.
	 * 
	 * @param	value	The string base 10 representation of an integer. 
	 */
	public void fromString( java.lang.String value ) {
		try {
			m_value = java.lang.Integer.parseInt( value );
			notifyChanged();
		}
		catch( NumberFormatException numberFormatException ) {
			// Nothing to do.
		}
	}
	
	/**
	 * Retrieve the value in a string representation.
	 *
	 * @return	A string representation of the value.
	 */
	public java.lang.String toString() {
		return java.lang.String.valueOf( m_value );
	}
}
