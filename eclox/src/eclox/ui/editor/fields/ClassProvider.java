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

package eclox.ui.editor.fields;

/**
 * Provide field classes for any specified tag name.
 * 
 * @author gbrocker
 */
public class ClassProvider {
	/**
	 * The available field classes associated to the corresponding tag value class.  
	 */
	java.util.Map m_fieldClasses = new java.util.HashMap();
	
	/**
	 * Constructor.
	 */
	public ClassProvider() {
		m_fieldClasses.put( eclox.resource.content.value.String.class, Text.class );
		//m_fieldClasses.put( eclox.doxyfile.node.value.Boolean.class, Boolean.class );
	}
	
	/**
	 * Retrieve the field class matching the specified tag name.
	 * 
	 * @param	valueClass	An instance representing a tag value class.
	 * 
	 * @return	The class of the field to use with the specified tag value class.
	 */
	public java.lang.Class getFieldClass( Class valueClass ) {
		java.lang.Class	result;
		
		// Normal field class search.
		result = (java.lang.Class) m_fieldClasses.get( valueClass );
		// If no field class has been found, set a default one.
		if( result == null ) {
			result = Text.class;
		}
		return result;
	}
}
