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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;

/**
 * Implement a text field.
 * 
 * @author gbrocker
 */
public class Text extends Field {
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
			getValue().fromString( textCtrl.getText() );
		}
	}
	
	/**
	 * Implement a key listener class that will process special keys.
	 */
	private class TextKeyListener implements KeyListener {
		/**
		 * Notify thet a key has been pressed.
		 *
		 * @param	event	The event to process.
		 */
		public void keyPressed(KeyEvent event) {	
		}
		
		/**
		 * Notify that a key has been released.
		 * 
		 * @param	event	The event to process.
		 */
		public void keyReleased(KeyEvent event) {
			if(event.keyCode == SWT.ESC) {
				cancelEdition();
			}
			else if(event.keyCode == SWT.CR) {
				completEdition();
			}
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
		textCtrl.setText( getValue().toString() );
		textCtrl.selectAll();
		textCtrl.addModifyListener( new TextListener() );
		textCtrl.addKeyListener( new TextKeyListener() );		
		return textCtrl;
	}
}
