// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2007 Guillaume Brocker
//
// This file is part of eclox.
//
// eclox is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
//
// eclox is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with eclox; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	

package eclox.ui.editor.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RadioMultiEditor extends MultiEditor {
	
	/**
	 * Implements the selection listener used for radio button
	 */
	private class MySelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			Button	button = (Button) e.widget;
			
			if( button.getSelection() == true ) {
				selectState( button.getText() );
				commit();
			}
		}
		
	}
	/**
	 * an array containing all radio button representing this editor
	 */
	private Button[] buttons;
	
	/**
	 * Constructor
	 * 
	 * @param states	an array of string representing the states of the editor
	 */
	public RadioMultiEditor( String [] states ) {
		super( states );
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	public void createContent(Composite parent, FormToolkit formToolkit) {
		// Creates all radio buttons.
		buttons = new Button[states.length];
		for( int i = 0; i != states.length; ++i ) {
			buttons[i] = formToolkit.createButton(parent, states[i].getName(), SWT.RADIO);
			buttons[i].addSelectionListener( new MySelectionListener() );
		}
		
		// Installs the layout.
		parent.setLayout( new FillLayout(SWT.VERTICAL) );
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#dispose()
	 */
	public void dispose() {
		buttons = null;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#grabVerticalSpace()
	 */
	public boolean grabVerticalSpace() {
		return false;
	}

	/**
	 * @see eclox.ui.editor.basic.MultiEditor#refresh()
	 */
	public void refresh() {
		// Pre-condition
		assert buttons != null;
		
		super.refresh();
		
		// Refreshes managed buttons
		State	selection = getSelectionAsState();
		for( int i = 0; i != buttons.length; ++i ) {
			buttons[i].setSelection( selection != null && selection.getName().equals(buttons[i].getText()) );
		}
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		// Pre-condition
		assert buttons != null;
		
		for( int i = 0; i != buttons.length; ++i ) {
			buttons[i].setEnabled(enabled);
		}
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setFocus()
	 */
	public void setFocus() {
		// Pre-condition
		assert buttons != null;
		
		buttons[0].setFocus();
	}

}
