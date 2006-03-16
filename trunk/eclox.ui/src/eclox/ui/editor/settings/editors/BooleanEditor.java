//eclox : Doxygen plugin for Eclipse.
//Copyright (C) 2003-2005 Guillaume Brocker
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

package eclox.ui.editor.settings.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.core.doxyfiles.Setting;

/**
 * Implements an setting editor for boolean values
 * 
 * @author gbrocker
 */
public class BooleanEditor implements IEditor {
	
	/**
	 * @brief	the container for all widgets 
	 */
	private Composite container;
    
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
     * @brief   the setting being the editor's input
     */
    private Setting input;
    
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
        // Pre-condition
        assert yesButton != null;
        assert noButton != null;
        assert defaultButton != null;
        assert input != null;
        
        if( yesButton.getSelection() == true ) {
            input.setValue( "YES" );
        }
        else if( noButton.getSelection() == true ) {
            input.setValue( "NO" );            
        }
        else if( defaultButton.getSelection() == true ) {
            input.setValue( "" );
        }
        else {
            assert false; // What's going on ?
        }        
        isDirty = false;
	}

	public void createContent(Composite parent, FormToolkit formToolkit) {
		// Initialize the parent control.
		RowLayout	layout = new RowLayout(SWT.VERTICAL);
		container = formToolkit.createComposite( parent );
		layout.marginWidth = 0;		
		container.setLayout( layout );
		
		// Creates the buttons.
		yesButton      = formToolkit.createButton( container, "Yes", SWT.RADIO );
		noButton       = formToolkit.createButton( container, "No", SWT.RADIO );
		defaultButton  = formToolkit.createButton( container, "Default", SWT.RADIO );
		
		// Attaches a selection listener instance to each button.
		yesButton.addSelectionListener( new MySelectionListener() );
		noButton.addSelectionListener( new MySelectionListener() );
		defaultButton.addSelectionListener( new MySelectionListener() );
    }
    
    public boolean fillVertically() {
		return false;
	}

	public void dispose() {
		container.dispose();
	}

	public boolean isDirty() {
		return isDirty;
	}
    
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

	public void setInput(Setting input) {
        // Pre-condition
        assert yesButton != null;
        assert noButton != null;
        assert defaultButton != null;
        
        // Initializes local references
        this.isDirty = false;
        this.input = input;
        
        // Initializes buttons.
        String  value = input.getValue();
        
        yesButton.setSelection(     value.compareToIgnoreCase("YES") == 0 );
        noButton.setSelection(      value.compareToIgnoreCase("NO") == 0 );
        defaultButton.setSelection( value.length() == 0 );
        
        // Post-condition
        assert input != null;
    }
}
