//eclox : Doxygen plugin for Eclipse.
//Copyright (C) 2003-2007 Guillaume Brocker
//
//This file is part of eclox.
//
//eclox is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//any later version.
//
//eclox is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with eclox; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	

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
	
	/**
	 * Defines the yes setting value
	 */
	private static String YES = "YES";

	/**
	 * Defines the no setting value
	 */
	private static String NO = "NO";
	
	/**
	 * the text of the check box button
	 */
	private String text;
	
	/**
	 * the check box button
	 */
	private Button button;
	
	/**
	 * the current dirty state
	 */
	private boolean dirty = false;
	
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
		// Pre-condition
		assert getInput() != null;
		
		getInput().setValue( getSelection() );
		dirty = false;
		fireEditorChanged();
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
		// Pre-condition
		assert getInput() != null;
		
		return getSelection().equalsIgnoreCase(getInput().getValue()) == false;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#refresh()
	 */
	public void refresh() {
		// Pre-condition
		assert getInput() != null;
		assert button != null;
		assert button.isDisposed() == false;
		
		// Updates the button state.
		button.setSelection( getInput().getValue().equalsIgnoreCase(YES) );
		
		fireEditorChanged();
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
	 * @retrun	a string containing the selected value
	 */
	private String getSelection() {
		// Pre-condition
		assert button != null;
		assert button.isDisposed() == false;
		
		return button.getSelection() == true ? YES : NO;		
	}
}
