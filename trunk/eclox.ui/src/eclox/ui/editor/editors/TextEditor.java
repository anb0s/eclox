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

package eclox.ui.editor.editors;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.core.doxyfiles.Setting;

/**
 * Implements a simple setting editor.
 * 
 * @author gbrocker
 */
public class TextEditor extends SettingEditor {
    
	/**
	 * Defines a modify listener class.
	 */
	private class TextModifyListener implements ModifyListener {

		/**
		 * Tells if the listener should sleep (ignore notifications).
		 */
		public boolean sleeping = true;
		
		public void modifyText(ModifyEvent e) {
			if(sleeping == false) {
				hasChanged = true;
				fireEditorChanged();
				commit();
			}
		}
		
	};
	
    /**
     * The text widget.
     */
    protected Text text;
    
    /**
     * The current modification listener of the text control
     */
    private TextModifyListener textModifyListener = new TextModifyListener();
    
    /**
     * Remerbers if the text has changed.
     */
    private boolean hasChanged = false;
    
    
    /**
     * @see eclox.ui.editor.editors.IEditor#commit()
     */
    public void commit() {
		getInput().setValue( text.getText() );
		hasChanged = false;
		fireEditorChanged();
	}
    
    /**
     * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
     */
    public void createContent( Composite parent, FormToolkit formToolkit ) {
    	// Activates border painting.
    	formToolkit.paintBordersFor( parent );

    	// Prepere the parent's layout.
    	GridLayout	layout = new GridLayout();
    	layout.marginTop			= 0;
    	layout.marginLeft			= 0;
    	layout.marginBottom			= 0;
    	layout.marginRight			= 0;
    	layout.marginHeight			= 2;
    	layout.marginWidth			= 1;
    	layout.horizontalSpacing	= 5;
    	parent.setLayout( layout );
    		
        // Creates the text widget.
    	text = formToolkit.createText(parent, new String());
    	text.setLayoutData( new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_CENTER) );
        text.addModifyListener( textModifyListener );
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
		text.dispose();		
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#isDirty()
	 */
	public boolean isDirty() {
		return hasChanged;
	}
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#isStale()
	 */
	public boolean isStale() {
		return text.getText().equals( getInput().getValue() ) == false;
	}
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#refresh()
	 */
	public void refresh() {
		textModifyListener.sleeping = true;
		text.setText( getInput().getValue() );
		hasChanged = false;
		textModifyListener.sleeping = false;
		fireEditorChanged();
	}
    
    /**
     * @see eclox.ui.editor.editors.IEditor#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
		// pre-condition
    	assert text != null;
    	
    	text.setEnabled(enabled);		
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setFocus()
	 */
	public void setFocus() {
    	text.selectAll();
        text.setFocus();
    }

	/**
	 * @see eclox.ui.editor.editors.SettingEditor#setInput(eclox.core.doxyfiles.Setting)
	 */
	public void setInput(Setting input) {
		super.setInput(input);
		
		textModifyListener.sleeping = true;
		text.setText(input.getValue());
		textModifyListener.sleeping = false;
        hasChanged = false;
    }
    
}
