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

package eclox.ui.editor.form.settings.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.doxyfiles.Setting;

/**
 * Implements an setting editor for boolean values
 * 
 * @author gbrocker
 */
public class BooleanEditor implements IEditor {
	
	/**
	 * @brief	the container for all widgets 
	 */
	Composite container;

	
    public void commit() {
		// TODO Auto-generated method stub	
	}

	public void createContent(Composite parent, FormToolkit formToolkit) {
    	
    	// Initialize the parent control.
    	container = formToolkit.createComposite(parent);
    	container.setLayout(new RowLayout(SWT.VERTICAL));
    	
    	// Creates the buttons.
    	formToolkit.createButton(container, "Yes", SWT.RADIO);
    	formToolkit.createButton(container, "No", SWT.RADIO);
        formToolkit.createButton(container, "Default", SWT.RADIO);
    }
    
    public void dispose() {
		container.dispose();
	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setInput(Setting input) {
        // TODO implementation required !
    }
}
