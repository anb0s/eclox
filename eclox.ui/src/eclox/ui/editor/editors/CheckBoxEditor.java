/*******************************************************************************
 * Copyright (C) 2003-2008, 2013, Guillaume Brocker
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

package eclox.ui.editor.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author gbrocker
 *
 */
public class CheckBoxEditor extends SettingEditor {
	
	/**
	 * Implements a selection listener for the button
	 */
	private class MySelectionListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			dirty = true;
			fireEditorChanged();
			commit();
		}
		
	}
	
	private static String YES = "YES";	///< Defines the yes setting value
	private static String NO = "NO";	///< Defines the no setting value
	
	private String text;	///< the text of the check box button
	private Button button;	///< the check box button
	
	private boolean dirty = false;	///< the current dirty state
	
	/**
	 * Constructor
	 * 
	 * @param	text	the text to set along to the check box button
	 */
	public CheckBoxEditor( String text ) {
		this.text = new String(text);
	}
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#commit()
	 */
	public void commit() {
		if( hasInput() ) {
			getInput().setValue( getSelection() );
			dirty = false;
			fireEditorChanged();
		}
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	public void createContent(Composite parent, FormToolkit formToolkit) {
		// Pre-condition
		assert button == null;
		
		button = formToolkit.createButton(parent, text, SWT.CHECK);
		button.addSelectionListener(new MySelectionListener());
		parent.setLayout(new FillLayout() );
		
		// Post-condition
		assert button != null;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#dispose()
	 */
	public void dispose() {
		// Pre-condition
		assert button != null;
		
		button.dispose();
		button = null;
		
		super.dispose();
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#grabVerticalSpace()
	 */
	public boolean grabVerticalSpace() {
		return false;
	}

	/**
	 * Retrieves the current value of the editor.
	 * 
	 * @return	the current value of the editor
	 */
	public boolean getValue() {
		// Pre-condition
		assert button != null;
		
		return button.getSelection();
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#isDirty()
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#isStale()
	 */
	public boolean isStale() {
		boolean	result = false;
		
		if( hasInput() ) {
			result = getSelection().equalsIgnoreCase(getInput().getValue()) == false;
		}
		return result;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#refresh()
	 */
	public void refresh() {
		// Pre-condition
		assert getInput() != null;
		assert button != null;
		assert button.isDisposed() == false;
		
		if( hasInput() ) {
			// Updates the button state.
			button.setSelection( getInput().getValue().equalsIgnoreCase(YES) );
			
			fireEditorChanged();
		}
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		// Pre-condition
		assert button != null;
		
		button.setEnabled(enabled);
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setFocus()
	 */
	public void setFocus() {
		// Pre-condition
		assert button != null;
		assert button.isDisposed() == false;

		button.setFocus();
	}
	
	/**
	 * Retrieves the current value selected in the user interface button.
	 * 
	 * @return	a string containing the selected value
	 */
	private String getSelection() {
		// Pre-condition
		assert button != null;
		assert button.isDisposed() == false;
		
		return button.getSelection() == true ? YES : NO;		
	}
}
