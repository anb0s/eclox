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

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;

import eclox.util.ListenerManager;
import eclox.doxyfile.node.Tag;

/**
 * The abstract field class.
 * 
 * @author gbrocker
 */
public abstract class Field extends ListenerManager {
	/**
	 * The tag that is being edited.
	 */
	Tag m_tag = null;
	
	/**
	 * The tag value backup.
	 */
	String m_tagValueBackup;
	
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
		m_tag.getValue().fromString(m_tagValueBackup);
		m_tag.setClean();
		notifyFieldEditionCanceled();
	}
	
	/**
	 * Tell the field to attach to the specified tag. This method is called only once, just after
	 * the field creation.
	 * 
	 * @param	tag	The tag to attach to.
	 */
	public void editTag( Tag tag ) {
		m_tag = tag;
		m_tagValueBackup = m_tag.getValue().toString();
	}
	
	/**
	 * Retrieve the tag being edited.
	 * 
	 * @return	The tag being edited.
	 */
	public Tag getEditedTag() {
		return m_tag;
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
