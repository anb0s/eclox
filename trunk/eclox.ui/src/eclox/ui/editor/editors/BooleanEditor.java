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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Implements an setting editor for boolean values
 * 
 * @author gbrocker
 */
public class BooleanEditor extends SettingEditor {
	
	private static String YES = "YES";	///< the yes selection value
	private static String NO = "NO";	///< the no selection value
	
	private Button  yesButton;		///< the yes button
    private Button  noButton;		///< the no button
    private Button  defaultButton;	///< the default button
    
    private boolean isDirty = false;	///< a boolean telling if the editor is dirty or not
    
    /**
     * @brief   Implements a selection listener that will be attached to each button.
     */
    private class MySelectionListener implements SelectionListener {

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            isDirty = true;
            fireEditorChanged();
            commit();
        }

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            isDirty = true;
            fireEditorChanged();
            commit();
        }
        
    }
    
    /**
     * @see eclox.ui.editor.editors.IEditor#commit()
     */
    public void commit() {
    	if( hasInput() ) {
	    	getInput().setValue(getSelection());
	        isDirty = false;
	        fireEditorChanged();
    	}
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	public void createContent(Composite parent, FormToolkit formToolkit) {
		// Initialize the parent control.
		RowLayout	layout = new RowLayout(SWT.VERTICAL);
		
		layout.marginWidth = 0;		
		parent.setLayout( layout );
		
		// Creates the buttons.
		yesButton      = formToolkit.createButton( parent, "Yes", SWT.RADIO );
		noButton       = formToolkit.createButton( parent, "No", SWT.RADIO );
		defaultButton  = formToolkit.createButton( parent, "Default", SWT.RADIO );
		
		// Attaches a selection listener instance to each button.
		yesButton.addSelectionListener( new MySelectionListener() );
		noButton.addSelectionListener( new MySelectionListener() );
		defaultButton.addSelectionListener( new MySelectionListener() );
    }
    
    /**
     * @see eclox.ui.editor.editors.IEditor#grabVerticalSpace()
     */
    public boolean grabVerticalSpace() {
		return false;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#dispose()
	 */
	public void dispose() {
		// Pre-condition
		assert yesButton != null;
		assert noButton != null;
		assert defaultButton != null;
		
		// Release all resources.
		yesButton.dispose();
		noButton.dispose();
		defaultButton.dispose();
		
		yesButton = null;
		noButton = null;
		defaultButton = null;
		
		super.dispose();
	}
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#isDirty()
	 */
	public boolean isDirty() {
		return isDirty;
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
        assert yesButton != null;
        assert noButton != null;
        assert defaultButton != null;
        
        if( hasInput() ) {
	        this.isDirty = false;
	        
	        String  value = getInput().getValue();
	        
	        yesButton.setSelection		( value.compareToIgnoreCase(YES) == 0 );
	        noButton.setSelection		( value.compareToIgnoreCase(NO) == 0 );
	        defaultButton.setSelection	( value.length() == 0 );
	        
	        fireEditorChanged();
        }
	}
    
    /**
     * @see eclox.ui.editor.editors.IEditor#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        // Pre-condition
        assert yesButton != null;
        assert noButton != null;
        assert defaultButton != null;

        yesButton.setEnabled(enabled);
        noButton.setEnabled(enabled);
        defaultButton.setEnabled(enabled);
	}

	/**
     * @see eclox.ui.editor.editors.IEditor#setFocus()
     */
    public void setFocus() {
        // Pre-condition
        assert yesButton != null;
        assert noButton != null;
        assert defaultButton != null;
        
        Button  selectedButton = null;
        if( yesButton.getSelection() == true ) {
            selectedButton = yesButton;
        }
        else if( noButton.getSelection() == true ) {
            selectedButton = noButton;
        }
        else if( defaultButton.getSelection() == true ) {
            selectedButton = defaultButton;
        }
        else {
            assert false;   // What's wrong?
        }
        selectedButton.setFocus();
    }

	/**
	 * Retrieves the selected value from the ui controls
	 * 
	 * @return	a string containing the selected value
	 */
	private String getSelection() {
        // Pre-condition
        assert yesButton != null;
        assert noButton != null;
        assert defaultButton != null;
        
        if( yesButton.getSelection() == true ) {
            return new String(YES);
        }
        else if( noButton.getSelection() == true ) {
        	return new String(NO);            
        }
        else if( defaultButton.getSelection() == true ) {
        	return new String();
        }
        else {
            assert false; // What's going on ?
            return new String();
        }
	}
}
