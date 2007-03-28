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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Defines the interface of doxyfile editors.
 * 
 * @author gbrocker
 */
public interface IEditor {
    
	/**
	 * Commits any changes made in the editor.
	 */
	public void commit();
	
	/**
	 * Creates the editor contents.
	 * 
	 * @param	parent		the composite control that will contain the editor controls
	 * @param	formToolkit	the form toolkit to use for the control creation
	 */
	void createContent(Composite parent, FormToolkit formToolkit);
	
	/**
	 * Asks the editor if it wants to vertically fill the available space.
	 * 
	 * @return	a boolean telling if the editor wants to fill the available vertical space
	 */
	boolean fillVertically();
	
	/**
	 * Asks the editor to dispose its controls.
	 */
	void dispose();
	
	/**
	 * Tells if the editor is dirty.
	 * 
	 * @return	true or false
	 */
	public boolean isDirty();
	
	/**
	 * Tells if the editor is stale, after the underlying setting has changed.
	 * 
	 * @return	true or false
	 */
	public boolean isStale();
	
	/**
	 * Refreshes the editor if is stale. The editor is expected to updates
	 * its state using the data of the underlying model.
	 */
	public void refresh();
    
	/**
	 * Enables or disables the receiver.
	 * 
	 * @param enabled	the new enabled state
	 */
	void setEnabled(boolean enabled);
	
	/**
	 * Makes the editor installs the focus on the right widget.
	 */
	void setFocus();
	
}
