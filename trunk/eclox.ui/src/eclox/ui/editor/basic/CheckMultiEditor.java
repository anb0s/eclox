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

/**
 * Implements a specialized multi editor that is represented by a check box and
 * provides only two states: SELECTED and DESELECTED. 
 *  
 * @author Guillaume Brocker
 */
public class CheckMultiEditor extends MultiEditor {
	
	/**
	 * the selected state name constant
	 */
	public static final String SELECTED = "Selected";
	
	/**
	 * the deselected state name constant
	 */
	public static final String DESELECTED = "Deselected";
	
	/**
	 * Defines the selection listener for the check box button
	 */
	private class MySelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);			
		}

		public void widgetSelected(SelectionEvent e) {
			Button	button = (Button) e.widget;
			selectState( button.getSelection() ? SELECTED : DESELECTED );
			commit();
		}
		
	}
	
	/**
	 * a string containing the text to use for the check box button
	 */
	private String	text;
	
	/**
	 * the check box button that represents the editor
	 */
	private Button	button;
	
	/**
	 * Constructor
	 * 
	 * @param text	a string containing the text that will be used for the check box control
	 */
	public CheckMultiEditor( String text ) {
		super( new String[] {SELECTED, DESELECTED} );
		this.text = text;
		
	}
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	public void createContent(Composite parent, FormToolkit formToolkit) {
		button = formToolkit.createButton(parent, text, SWT.CHECK);
		button.addSelectionListener( new MySelectionListener() );
		parent.setLayout(new FillLayout(SWT.VERTICAL));
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#dispose()
	 */
	public void dispose() {
		button = null;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#fillVertically()
	 */
	public boolean fillVertically() {
		return false;
	}

	/**
	 * @see eclox.ui.editor.basic.MultiEditor#refresh()
	 */
	public void refresh() {
		// Pre-condition
		assert button != null;
		
		// Refreshes the states.
		super.refresh();
		
		// Refreshes the check box
		State	selection = getSelection();
		button.setSelection( selection != null && selection.getName().equals(SELECTED) );
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setFocus()
	 */
	public void setFocus() {
		// Pre-condition
		assert button != null;
		
		button.setFocus();
	}

}
