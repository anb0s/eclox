/*******************************************************************************
 * Copyright (C) 2003-2004, 2013, Guillaume Brocker
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

package eclox.ui.editor;

import org.eclipse.ui.forms.editor.FormPage;

/**
 * @author gbrocker
 */
public class SourcePage extends FormPage {

    /**
     * Defines the source page identifer.
     */
    public static final String ID = "source";

    /**
     * Constructor.
     * 
     * @param	editor	the editor instance to attach to
     */
    public SourcePage(Editor editor) {
        super(editor, ID, "Sources");
    }
}
