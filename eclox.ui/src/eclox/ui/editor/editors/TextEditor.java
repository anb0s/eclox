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

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

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
            if (sleeping == false) {
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
        if (hasInput()) {
            getInput().setValue(text.getText());
            hasChanged = false;
            fireEditorChanged();
        }
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
     */
    public void createContent(Composite parent, FormToolkit formToolkit) {
        // Activates border painting.
        formToolkit.paintBordersFor(parent);

        // Prepere the parent's layout.
        GridLayout layout = new GridLayout();
        layout.marginTop = 0;
        layout.marginLeft = 0;
        layout.marginBottom = 0;
        layout.marginRight = 0;
        layout.marginHeight = 2;
        layout.marginWidth = 1;
        layout.horizontalSpacing = 5;
        parent.setLayout(layout);

        // Creates the text widget.
        text = formToolkit.createText(parent, new String());
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
        text.addModifyListener(textModifyListener);
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
        boolean result = false;

        if (hasInput()) {
            result = text.getText().equals(getInput().getValue()) == false;
        }
        return result;
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#refresh()
     */
    public void refresh() {
        if (hasInput()) {
            textModifyListener.sleeping = true;
            text.setText(getInput().getValue());
            hasChanged = false;
            textModifyListener.sleeping = false;
            fireEditorChanged();
        }
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
    //	public void setInput(Setting input) {
    //		super.setInput(input);
    //		
    //		if( hasInput() ) {
    //			textModifyListener.sleeping = true;
    //			text.setText(input.getValue());
    //			textModifyListener.sleeping = false;
    //	        hasChanged = false;
    //		}
    //    }

}
