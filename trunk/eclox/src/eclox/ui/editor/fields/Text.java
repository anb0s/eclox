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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;

/**
 * Implement a text field.
 * 
 * @author gbrocker
 */
public class Text implements Field {
	/**
	 * Implmenet a text control mofidy listener class.
	 */
	private class TextListener implements ModifyListener {
		/**
		 * Process a text modification event.
		 * 
		 * @param	event	The event to process.
		 */
		public void modifyText( ModifyEvent event ) {
			org.eclipse.swt.widgets.Text	textCtrl;
			
			textCtrl = (org.eclipse.swt.widgets.Text) event.widget;
			m_value.fromString( textCtrl.getText() );
		}
	}
	
	/**
	 * The value being edited.
	 */
	private eclox.data.value.Abstract m_value;
	
	/**
	 * Attach the field to the specified tag.
	 * 
	 * @param	tag	The tag to attach to and thus to edit.
	 */
	public void attachTo( eclox.data.Tag tag ) {
		m_value = tag.getValue();
	}
	
	/**
	 * Detach the field from the value.
	 */
	public void detach() {
		if( m_value != null ) {
			m_value = null;
		}
	}
	
	/**
	 * Create the text control.
	 * 
	 * @param	parent	The parent composite instance.
	 * 
	 * @return	The text control for the field.
	 */
	public Control createControl( Composite parent ) {
		org.eclipse.swt.widgets.Text	textCtrl;
		
		textCtrl = new org.eclipse.swt.widgets.Text( parent, 0 );
		textCtrl.setText( m_value.toString() );
		textCtrl.selectAll();
		textCtrl.addModifyListener( new TextListener() );		
		return textCtrl;
	}
}
