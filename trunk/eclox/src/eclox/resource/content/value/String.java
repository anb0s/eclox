/*
	eclox
	Copyright (C) 2003-2004 Guillaume Brocker

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

package eclox.resource.content.value;

/**
 * Implement a string tag value class.
 * 
 * @author gbrocker
 */
public class String extends Value {
	/**
	 * The string value.
	 */
	private java.lang.String m_value = new java.lang.String();
	
	/**
	 * Clone the value instance, but not the listeners.
	 * 
	 * @return	A cloned instance of the value.
	 */
	public Object clone() {
		String s = new String();
		
		s.m_value = new java.lang.String(this.m_value);
		return s;
	}
	
	/**
	 * Retrieve the value represented in the specified string.
	 * 
	 * @param	value	the string to parse to get the represented value.
	 */
	public void fromString( java.lang.String value ) {
		m_value = new java.lang.String( value );
		notifyChanged();
	}
	
	/**
	 * Retrieve the string representation of the value.
	 * 
	 * @return	A string containing the value as a string.
	 */
	public java.lang.String toString() {
		return m_value;
	}
}
