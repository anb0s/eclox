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

package eclox.ui.editor.fields;

/**
 * Implement a field event object.
 * 
 * @author gbrocker
 */
public class FieldEvent extends java.util.EventObject {

	/**
	 * The field that raised the event.
	 */
	public Field field;
	
	/**
	 * Constructor.
	 *
	 * @param	source The field at the origin of the event.
	 */
	public FieldEvent( Field source ) {
		super( source );
		this.field = source; 
	}	
}
