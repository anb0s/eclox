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

package eclox.ui.editor.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import eclox.data.Tag;

/**
 * Implement the tag detail editor part.
 * 
 * @author gbrocker
 */
public class Hint extends Part {
	/**
	 * Implement a selection listener for the tags management part.
	 * 
	 * @author gbrocker
	 */
	private class TagsSelectionListener implements SelectionListener {
		/**
		 * Process the tags selection changes.
		 * 
		 * @param	event	The selection change event to process.
		 */
		public void selectionChanged( SelectionEvent event ) {
			Tag	tag;
	
			tag = (Tag) event.selection.getSelected();
			if( tag != null ) {			
				m_hintCtrl.setText( tag.getComment().getText() );
			}
			else {
				m_hintCtrl.setText( "No tag selected!" );
			}
		}
	}
	
	/**
	 * The control that displays the tag hint.
	 */
	private Text m_hintCtrl;
	
	/**
	 * Constructor.
	 * 
	 * @param tags	The tag part to attach to.
	 */
	public Hint( Tags tags ) {
		tags.selection.addSelectionListener( new TagsSelectionListener() );
	}
	
	/**
	 * Ask the part to create its control.
	 */
	public void createContent() {
		m_hintCtrl = new Text( getContentParent(), SWT.MULTI|SWT.WRAP|SWT.V_SCROLL|SWT.READ_ONLY );
		setContent( m_hintCtrl );
		setImage( new Image( Display.getCurrent(), getClass().getResourceAsStream("help.gif")));
		setTitle( "Hint" );		
	}
}
