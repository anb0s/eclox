// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2005 Guillaume Brocker
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

package eclox.ui.editor.form.settings.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.doxyfiles.Setting;

/**
 * Defines the interface for settings editors.
 * 
 * @author gbrocker
 */
public interface IEditor {
    
    /**
     * Commits any changes made in the editor.
     */
    //public void commit();
    
    /**
     * Creates the editor contents.
     * 
     * @param	parent		the composite control that will contain the editor controls
     * @param	formToolkit	the form toolkit to use for the control creation
     */
    void createContent(Composite parent, FormToolkit formToolkit);
    
    /**
     * Sets the editor input
     * 
     * @param	input	a setting that is the new editor input
     */
    void setInput(Setting input);
    
    /**
     * Tells if the editor is dirty.
     * 
     * @return	true or false
     */
    //public boolean isDirty();
}
