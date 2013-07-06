/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/ 

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
