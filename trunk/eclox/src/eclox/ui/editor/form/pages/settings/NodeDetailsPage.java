/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2004 Guillaume Brocker
 * 
 * This file is part of eclox.
 * 
 * eclox is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 * 
 * eclox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with eclox; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
 */

package eclox.ui.editor.form.pages.settings;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.doxyfiles.nodes.Node;
import eclox.doxyfiles.nodes.PropertyProvider;


/**
 * Implements the generic details node page.
 * 
 * @author gbrocker
 */
public class NodeDetailsPage implements IDetailsPage {
    
    /**
     * Retrieves the node instance that is in the specified selection
     * 
     * @param	selection	a selection from which a node instance must be retrieved
     * 
     * @return	the found node instance or null if none
     */
    protected static Node getNodeFromSelection(ISelection selection) {
        Node result = null;
        if(selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Object object = structuredSelection.getFirstElement();
            if(object instanceof Node) {
                result = (Node) object;
            } 
        }
        return result;
    }
    
    /**
     * The section that contains all our controls.
     */
    private Section section;
    
    /**
     * The managed form the page is attached to.
     */
    protected IManagedForm managedForm;
    
    /**
     * The label displaying the note text.
     */
    private Label noteLabel;

    /**
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents(Composite parent) {
        FormToolkit toolkit = this.managedForm.getToolkit();
        
        // Initializes the parent control.
        RowLayout layout = new RowLayout(SWT.VERTICAL);
        parent.setLayout(layout);
        
        // Creates the section
        this.section = toolkit.createSection(parent, Section.TITLE_BAR);
        this.section.marginHeight = 5;
        this.section.marginWidth = 10;
        this.section.setText("Annotation");
        
        // Creates the label display the note content.
        this.noteLabel = this.managedForm.getToolkit().createLabel(section, "", org.eclipse.swt.SWT.WRAP);
        section.setClient(this.noteLabel);        
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit(boolean onSave) {}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose() {}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form) {
        this.managedForm = form;
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#isDirty()
     */
    public boolean isDirty() {
        return false;
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#isStale()
     */
    public boolean isStale() {
        return false;
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh() {}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus() {
        this.noteLabel.setFocus();
	}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input) {
        Node node = (Node) input;
        this.updateUIControls(node);
        return false;
    }
    
    /**
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IFormPart part, ISelection selection) {
        Node node = getNodeFromSelection(selection);
        this.updateUIControls(node);
    }
    
    /**
     * Updates the UI controls for the specified node.
     * 
     * @param	node	a node instance to use to refresh the UI controls.
     */
    private void updateUIControls(Node node) {
        String text = PropertyProvider.getDefault().getAnnotation(node);
        this.noteLabel.setText(text);
    }
}
