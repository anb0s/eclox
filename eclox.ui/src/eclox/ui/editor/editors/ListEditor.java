/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.core.doxyfiles.Setting;

/**
 * Implements a list setting editor. This class is abstract since it provides no way to
 * edit value compounds. See derived classes.
 *
 * @author gbrocker
 */
public abstract class ListEditor extends SettingEditor {

    /**
     * Implements the table viewer content provider.
     */
    private class MyContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            Vector<?> compounds = (Vector<?>) inputElement;

            return compounds.toArray();
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * Implements the table viewer label provider.
     */
    private class MyLabelProvider extends LabelProvider {

        public String getText(Object element) {
            return new String((String) element);
        }

    }

    /**
     * Implements an open listener that will trigger the edition of the selected list viewer item.
     */
    private class MyOpenListener implements IOpenListener {

        public void open(OpenEvent event) {
            editSelectedValueCompound();
        }

    }

    /**
     * Implements a button selection listener.
     */
    private class MyButtonSelectionListener implements SelectionListener {

        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }

        public void widgetSelected(SelectionEvent e) {
            if (e.widget == addButton) {
                addValueCompound();
            } else if (e.widget == removeButton) {
                removeValueCompounds();
            } else if (e.widget == upButton) {
                moveValueCompoundsUp();
            } else if (e.widget == downButton) {
                moveValueCompoundsDown();
            } else {
                // Unsuported widget.
                assert false;
            }
        }

    }

    /**
     * Implements a selection change listener to update the button states.
     */
    private class MySelectionChangedListener implements ISelectionChangedListener {

        public void selectionChanged(SelectionChangedEvent event) {
            updateButtons();
        }

    }

    private Vector<String> valueCompounds; ///< The collection of the setting's value compounds.

    private ListViewer listViewer; ///< the table viewer used to edit the managed setting
    private Button addButton; ///< the button allowing to trigger a new value addition
    private Button removeButton; ///< the button allowing to trigger the deletion of selected values
    private Button upButton; ///< the button allowing to move the selected values up
    private Button downButton; ///< the button allowing to move the selected values down

    /**
     * @see eclox.ui.editor.editors.IEditor#commit()
     */
    public void commit() {
        if (hasInput()) {
            getInput().setValue(valueCompounds);
            fireEditorChanged();
        }
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
     */
    public void createContent(Composite parent, FormToolkit formToolkit) {
        // Pre-condition
        assert listViewer == null;

        // Activates border painting.
        formToolkit.paintBordersFor(parent);

        // Installs the layout.
        FormLayout layout = new FormLayout();
        layout.spacing = 2;
        parent.setLayout(layout);

        // Creates the list viewer and installs it in the layout.
        FormData formData;

        listViewer = new ListViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        formData = new FormData();
        formData.top = new FormAttachment(0, 2);
        formData.right = new FormAttachment(85, -1);
        formData.bottom = new FormAttachment(100, -2);
        formData.left = new FormAttachment(0, 1);
        listViewer.getControl().setLayoutData(formData);
        listViewer.getControl().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

        // Initializes the list viewer.
        listViewer.setContentProvider(new MyContentProvider());
        listViewer.setLabelProvider(new MyLabelProvider());
        listViewer.addOpenListener(new MyOpenListener());

        // Creates various buttons and installs them in the layout.
        addButton = formToolkit.createButton(parent, "Add", 0);
        removeButton = formToolkit.createButton(parent, "Remove", 0);
        upButton = formToolkit.createButton(parent, "Up", 0);
        downButton = formToolkit.createButton(parent, "Down", 0);

        formData = new FormData();
        formData.top = new FormAttachment(0, 0);
        formData.right = new FormAttachment(100, 0);
        formData.left = new FormAttachment(listViewer.getControl(), 2, SWT.RIGHT);
        addButton.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(addButton, 0, SWT.BOTTOM);
        formData.right = new FormAttachment(100, 0);
        formData.left = new FormAttachment(listViewer.getControl(), 2, SWT.RIGHT);
        removeButton.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(removeButton, 6, SWT.BOTTOM);
        formData.right = new FormAttachment(100, 0);
        formData.left = new FormAttachment(listViewer.getControl(), 2, SWT.RIGHT);
        upButton.setLayoutData(formData);

        formData = new FormData();
        formData.top = new FormAttachment(upButton, 0, SWT.BOTTOM);
        formData.right = new FormAttachment(100, 0);
        formData.left = new FormAttachment(listViewer.getControl(), 2, SWT.RIGHT);
        downButton.setLayoutData(formData);

        // Assignes a selection listener to the managed buttons.
        MyButtonSelectionListener selectionListener = new MyButtonSelectionListener();

        addButton.addSelectionListener(selectionListener);
        removeButton.addSelectionListener(selectionListener);
        upButton.addSelectionListener(selectionListener);
        downButton.addSelectionListener(selectionListener);

        // Adds a selection change listener to the list viewer and initializes the button states.
        listViewer.addPostSelectionChangedListener(new MySelectionChangedListener());
        updateButtons();

        // Post-condition
        assert listViewer != null;
    }

    public boolean grabVerticalSpace() {
        return true;
    }

    public void dispose() {
        // Pre-condition
        assert listViewer != null;
        assert addButton != null;
        assert removeButton != null;
        assert upButton != null;
        assert downButton != null;

        listViewer.getControl().dispose();
        listViewer = null;
        addButton.dispose();
        addButton = null;
        removeButton.dispose();
        removeButton = null;
        upButton.dispose();
        upButton = null;
        downButton.dispose();
        downButton = null;

        super.dispose();

        // Post-condition
        assert listViewer == null;
        assert addButton == null;
        assert removeButton == null;
        assert upButton == null;
        assert downButton == null;
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        // Pre-condition
        assert listViewer != null;
        assert addButton != null;
        assert removeButton != null;
        assert upButton != null;
        assert downButton != null;

        listViewer.getControl().setEnabled(enabled);
        addButton.setEnabled(enabled);
        removeButton.setEnabled(enabled);
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#setFocus()
     */
    public void setFocus() {
        // Pre-condition
        assert listViewer != null;

        listViewer.getControl().setFocus();

    }

    /**
     * @see eclox.ui.editor.editors.IEditor#refresh()
     */
    public void refresh() {
        // Pre-condition
        assert listViewer != null;

        if (hasInput()) {
            valueCompounds = new Vector<String>();
            getInput().getSplittedValue(valueCompounds);
            listViewer.setInput(valueCompounds);

            updateButtons();
            fireEditorChanged();
        }
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#isDirty()
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * @see eclox.ui.editor.editors.IEditor#isStale()
     */
    public boolean isStale() {
        boolean result = false;

        if (hasInput()) {
            Collection<String> values = new Vector<String>();

            getInput().getSplittedValue(values);
            result = valueCompounds.equals(values) == false;
        }
        return result;
    }

    /**
     * Edits the given value compound and returns the new comound value.
     * Null is interpreted as a canceled edition.
     *
     * Sub classes have to implement this method to provide a dialog to the user to do
     * the value compund edition.
     *
     * @param parent		the parent shell that implementors may use as parent window for any dialog
     * @param setting	the setting begin edited (it is just given as information and should be edited directly)
     * @param compound	the value compund to edit
     *
     * @return	a string containing the new compund value or null
     */
    abstract protected String editValueCompound(Shell parent, Setting setting, String compound);

    /**
     * Adds a new value compound.
     */
    private void addValueCompound() {
        // Pre-condition
        assert listViewer != null;
        assert valueCompounds != null;

        // Edits a new value.
        String newCompound = editValueCompound(listViewer.getControl().getShell(), getInput(), "new value");

        // Inserts the new compound if it has been validated.
        if (newCompound != null) {
            valueCompounds.add(newCompound);
            fireEditorChanged();
            commit();
            listViewer.refresh();
            listViewer.setSelection(new StructuredSelection(new Integer(valueCompounds.size() - 1)));
        }
    }

    /**
     * Edits the value compound that is selected in the list viewer.
     *
     * @return	true when a compound has been modified, false otherwise
     */
    private boolean editSelectedValueCompound() {
        // Pre-condition
        assert listViewer != null;
        assert valueCompounds != null;

        // Retrieves and handles the selection.
        IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
        String original = (String) selection.getFirstElement();
        String edited = editValueCompound(listViewer.getControl().getShell(), getInput(), original);

        // Processes the edited compound.
        if (edited != null) {
            // Updates the setting.
            valueCompounds.set(valueCompounds.indexOf(original), edited);
            fireEditorChanged();

            // Commit changes and erstores the selection.
            listViewer.getControl().setRedraw(false);
            commit();
            listViewer.refresh();
            listViewer.setSelection(new StructuredSelection(edited));
            listViewer.getControl().setRedraw(true);

            // Job's done.
            return true;
        } else {
            return false;
        }
    }

    /**
     * Moves the value compounds corresponding to the current selection up.
     */
    private void moveValueCompoundsUp() {
        // Pre-condition
        assert listViewer != null;
        assert valueCompounds != null;

        // Retrieves the current selection and skip if it is empty.
        IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
        if (selection.isEmpty() == true) {
            return;
        }

        // Retrieves the list of the selected items.
        @SuppressWarnings("unchecked")
        Vector<Object> selected = new Vector<Object>(selection.toList());
        Iterator<String> i = valueCompounds.iterator();
        while (i.hasNext() == true && selected.isEmpty() == false) {
            Object current = i.next();

            if (current.equals(selected.get(0))) {
                int index = valueCompounds.indexOf(current);
                if (index > 0) {
                    Collections.swap(valueCompounds, index, index - 1);
                } else {
                    break;
                }
                selected.remove(0);
            }
        }
        fireEditorChanged();

        // Commits changes and reselected moved objects.
        listViewer.getControl().setRedraw(false);
        commit();
        listViewer.refresh();
        listViewer.getControl().setRedraw(true);
    }

    /**
     * Moves the value compounds corresponding to the current selection down.
     */
    private void moveValueCompoundsDown() {
        // Pre-condition
        assert listViewer != null;
        assert valueCompounds != null;

        // Retrieves the current selection and skip if it is empty.
        IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
        if (selection.isEmpty() == true) {
            return;
        }

        // Retrieves the list of the selected items.
        @SuppressWarnings("unchecked")
        Vector<?> selected = new Vector<Object>(selection.toList());

        Collections.reverse(selected);
        Collections.reverse(valueCompounds);

        // Retrieves the list of the selected items.
        Iterator<String> i = valueCompounds.iterator();
        while (i.hasNext() == true && selected.isEmpty() == false) {
            Object current = i.next();

            if (current.equals(selected.get(0))) {
                int index = valueCompounds.indexOf(current);
                if (index > 0) {
                    Collections.swap(valueCompounds, index, index - 1);
                } else {
                    break;
                }
                selected.remove(0);
            }
        }

        Collections.reverse(valueCompounds);
        fireEditorChanged();

        // Commits changes and reselected moved objects.
        listViewer.getControl().setRedraw(false);
        commit();
        listViewer.refresh();
        listViewer.getControl().setRedraw(true);
    }

    /**
     * Removes the selected value compounds.
     */
    private void removeValueCompounds() {
        // Pre-condition
        assert listViewer != null;
        assert valueCompounds != null;

        // Retrieves the current selection and skip if it is empty.
        IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
        if (selection.isEmpty() == true) {
            return;
        }

        // Removes selected items from the value compounds.
        valueCompounds.removeAll(selection.toList());
        fireEditorChanged();

        // Commits changes.
        commit();
        listViewer.refresh();
    }

    /**
     * Updates the state of some buttons.
     */
    private void updateButtons() {
        // Pre-condition
        assert listViewer != null;
        assert removeButton != null;
        assert upButton != null;
        assert downButton != null;

        // Retrieves the selection emptiness and updates the buttons.
        boolean willBeEnabled = listViewer.getSelection().isEmpty() == false;

        removeButton.setEnabled(willBeEnabled);
        upButton.setEnabled(willBeEnabled);
        downButton.setEnabled(willBeEnabled);
    }

}
