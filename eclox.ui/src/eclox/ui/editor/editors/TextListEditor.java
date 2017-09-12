/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import eclox.core.doxyfiles.Setting;

/**
 * Implements a list editor that handle value compounds as simple text
 * 
 * @author gbrocker
 */
public class TextListEditor extends ListEditor {

    /**
     * Implements an input validator for the input dialog used to edit value compunds.
     */
    private class MyInputValidator implements IInputValidator {

        public String isValid(String newText) {
            if (newText.length() == 0) {
                return "Empty value is not allowed.";
            } else {
                return null;
            }
        }

    }

    protected String editValueCompound(Shell parent, Setting setting, String compound) {
        // Creates the edition dialog.
        InputDialog dialog = new InputDialog(parent, "Value Edition", "Enter the new text for the value.",
                Convenience.unescapeValue(compound), new MyInputValidator());

        // Lauches the value edition
        if (dialog.open() == InputDialog.OK) {
            return Convenience.escapeValue(dialog.getValue());
        } else {
            return null;
        }
    }

}
