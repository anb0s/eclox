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

package eclox.ui.editor.form.pages.settings.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.doxyfiles.nodes.Setting;

/**
 * Implements a simple setting editor
 * 
 * @author gbrocker
 */
public class TextEditor implements IEditor {
    
    /**
     * The text widget.
     */
    private Text text;
    
    /**
     * @see eclox.ui.editor.form.pages.settings.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite)
     */
    public void createContent(Composite parent, FormToolkit formToolkit) {
        parent.setLayout(new FillLayout());
        text = formToolkit.createText(parent, new String(), SWT.BORDER);
    }
    
    /**
     * @see eclox.ui.editor.form.pages.settings.editors.IEditor#setInput(eclox.doxyfiles.nodes.Setting)
     */
    public void setInput(Setting input) {
        text.setText(input.getValue());
    }
}
