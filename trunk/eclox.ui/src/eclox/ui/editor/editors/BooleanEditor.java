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
	
	/**
	 * the yes selection value
	 */
	private static String YES = "YES";
	
	/**
	 * the yes selection value
	 */
	private static String NO = "NO";
	
	/**
     * @brief   the yes button
     */
    private Button  yesButton;
    
    /**
     * @brief   the no button
     */
    private Button  noButton;
    
    /**
     * @brief   the default button
     */
    private Button  defaultButton;
    
    /**
     * @brief   a boolean telling if the editor is dirty or not
     */
    private boolean isDirty = false;
    
    /**
     * @brief   Implements a selection listener that will be attached to each button.
     */
    private class MySelectionListener implements SelectionListener {

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            isDirty = true;
            commit();
        }

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            isDirty = true;
            commit();
        }
        
    }
    
	
    public void commit() {
    	getInput().setValue(getSelection());
        isDirty = false;
	}

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
     * @see eclox.ui.editor.editors.IEditor#fillVertically()
     */
    public boolean fillVertically() {
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
		return getSelection().equalsIgnoreCase(getInput().getValue()) == false;
	}
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#refresh()
	 */
	public void refresh() {
        // Pre-condition
        assert yesButton != null;
        assert noButton != null;
        assert defaultButton != null;
        
        this.isDirty = false;
        
        String  value = getInput().getValue();
        
        yesButton.setSelection		( value.compareToIgnoreCase(YES) == 0 );
        noButton.setSelection		( value.compareToIgnoreCase(NO) == 0 );
        defaultButton.setSelection	( value.length() == 0 );
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
