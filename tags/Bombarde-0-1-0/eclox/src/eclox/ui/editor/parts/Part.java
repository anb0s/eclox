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
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridLayout;

/**
 * Define the editor part interface.
 * 
 * @author gbrocker
 */
public abstract class Part {
	/**
	 * Implement a control for the view form title.
	 */
	private class TitleControl extends Composite {
		/**
		 * The image control.
		 */
		private Label m_imageCtrl;
		
		/**
		 * The text control.
		 */
		private Label m_textCtrl;
		
		/**
		 * Constructor
		 */
		TitleControl( Composite parent ) {
			super( parent, 0 );
			
			// Set the layout
			GridLayout	gridLayout;
			
			gridLayout = new GridLayout( 2, false );
			gridLayout.marginHeight = 3;
			gridLayout.marginWidth = 3;
			setLayout( gridLayout );
			
			// Create the image and text controls.
			m_imageCtrl = new Label( this, 0 );
			m_textCtrl = new Label( this, 0 );
		}
		
		/**
		 * Set the title image.
		 *  
		 * @param image	The image to set.
		 */
		public void setImage( Image image ) {
			image.setBackground( m_imageCtrl.getBackground() );
			m_imageCtrl.setImage( image );
		}
		
		/**
		 * Set the title text.
		 * 
		 * @param	text	The new title text.
		 */
		public void setText( String text ) {
			m_textCtrl.setText( text );
		}
	}
	
	/**
	 * The control that wil recieve the part content. 
	 */
	private ViewForm m_control;
	
	/**
	 * Ask the part to create its content.
	 * 
	 * @param	parent	The parent composite for the part control.
	 * 
	 * @return	The created part control.
	 */
	public Control createControl( Composite parent ) {
		// Create the control.
		m_control = new ViewForm( parent, SWT.BORDER|SWT.FLAT );
		m_control.setTopLeft( new TitleControl( m_control ) );
		
		// Let the derived implementation create their own content.
		createContent();
		
		// Job's done !
		return m_control;
	}
	
	/**
	 * Let derived implementors create their own content for the part.
	 */
	protected abstract void createContent();
	
	/**
	 * Retrieve the content parent composite control.
	 * 
	 * @return	The composite control to use as parent for any content control.
	 */
	protected Composite getContentParent() {
		return m_control;
	}
	
	/**
	 * Set the new part content.
	 * 
	 * @param	content	A control instance that becomes the new part content.
	 */
	protected void setContent( Control content ) {
		m_control.setContent( content );
	}
	
	/**
	 * Set the title image.
	 *  
	 * @param image	The image to set.
	 */
	protected void setImage( Image image ) {
		((TitleControl) m_control.getTopLeft()).setImage( image );
	}
	
	/**
	 * Set the part title.
	 * 
	 * @param title	A string containing the new part title.
	 */
	protected void setTitle( String title ) {
		((TitleControl) m_control.getTopLeft()).setText( title );
	}
}
