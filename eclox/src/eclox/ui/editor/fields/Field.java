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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;

import eclox.resource.content.value.Value;
import eclox.util.ListenerManager;

/**
 * The abstract field class.
 * 
 * @author gbrocker
 */
public abstract class Field extends ListenerManager {
	/**
	 * The tag value to edit.
	 */
	Value m_value;
	
	/**
	 * Constructor.
	 */
	Field() {
		super(FieldListener.class);
	}
	
	/**
	 * Add the specified listener.
	 * 
	 * @param	listener	The listener to add.
	 */
	public void addFieldListener( FieldListener listener ) {
		super.addListener( listener );
	}

	/**
	 * Remove the specified listener.
	 * 
	 * @param	listener	The listener to remove.
	 */
	public void removeFieldListener( FieldListener listener ) {
		super.removeListener( listener );
	}
	
	/**
	 * Make the field to complet its edition.
	 */
	public void completEdition() {
		notifyFieldEditionCompleted();
	}
	
	/**
	 * Make the field cancel the current edition.
	 *
	 */
	public void cancelEdition() {
		notifyFieldEditionCanceled();
	}
	
	/**
	 * Create the control of the field.
	 * 
	 * @param	parent	The composite object instance that must be the parent of the field control.
	 * 
	 * @return	The control of the field.
	 */
	public abstract Control createControl( Composite parent );
	
	/**
	 * Retrieve the value being edited.
	 * 
	 * @return	The tag being edited.
	 */
	public Value getValue() {
		return m_value;
	}
	
	/**
	 * Set the value being edited.
	 * 
	 * @param	value	The new value to edit.
	 */
	public void setValue( Value value ) {
		m_value = value;
	}
	/**
	 * Notify all listeners that the edition has been canceled.
	 * 
	 * @todo	Handle exceptions.
	 */
	private void notifyFieldEditionCanceled() {
		super.fireEvent( new FieldEvent( this ), "fieldEditionCanceled" );	 	
	}
	
	/**
	 * Notify all listeners that the edition has been completed.
	 * 
	 * @todo	Handle exceptions.
	 */
	private void notifyFieldEditionCompleted() {
		super.fireEvent( new FieldEvent( this ), "fieldEditionCompleted" );	 	
	}
}
