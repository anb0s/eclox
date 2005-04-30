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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import eclox.doxyfiles.nodes.PropertyProvider;
import eclox.doxyfiles.nodes.Group;
import eclox.doxyfiles.nodes.Node;
import eclox.doxyfiles.nodes.Setting;

/**
 * @author gbrocker
 */
public class Block extends MasterDetailsBlock {
    
    /**
     * The tree viewer.
     */
    private TreeViewer treeViewer;
        
    /**
     * Implements the master label provider.
     * 
     * @author gbrocker
     */
    protected class MasterLabelProvider extends LabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         */
        public String getText(Object element) {
            if(element instanceof Node) {
                Node node = (Node) element;
                String text = PropertyProvider.getDefault().getText(node);
                return text != null ? text : node.getIdentifier();
            }
            else {
                return super.getText(element);
            }
        }
}
    
    /**
     * Implements the master content provider.
     * 
     * @author gbrocker
     */
    protected class MasterContentProvider implements ITreeContentProvider {

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren(Object parentElement) {
            if(parentElement instanceof Group) {
                Group group = (Group) parentElement;
                return group.toArray();
            }
            else {
                return null;
            }
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent(Object element) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren(Object element) {
            return element instanceof Group ? ((Group) element).size() != 0 : false;
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object inputElement) {
            return this.getChildren(inputElement);
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {}

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

    }
    
    /**
     * Implements a tree viewer selection listener that forwards selection changes to 
     * a managed form.
     * 
     * @author gbrocker
     */
    protected class SelectionForwarder implements ISelectionChangedListener {

        /**
         * The form part that is the source of the selection change notifications.
         */
        private IFormPart formPart;
        
        /**
         * The managed form to forward the selection changes to.
         */
        private IManagedForm managedForm;
        
        /**
         * Constructor.
         * 
         * @param	managedForm	a managed form instance that will received forwarded seletion changes
         */
        public SelectionForwarder(IFormPart formPart, IManagedForm managedForm) {
            this.formPart = formPart;
            this.managedForm = managedForm;
        }
        
        /**
         * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
         */
        public void selectionChanged(SelectionChangedEvent event) {
            this.managedForm.fireSelectionChanged(this.formPart, event.getSelection());
        }

    }
    
    /**
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart(IManagedForm managedForm, Composite parent) {
        ScrolledForm	form = managedForm.getForm();
        FormToolkit		toolkit = managedForm.getToolkit();
        
        // TODO !!! find the relevant layout configuration here !!!
        // Initalizes the parent control layout
        //parent.setLayout(new FillLayout());
        //parent.setLayoutData(null);
        
        // Creates the section containing the tree view
        Section	section = toolkit.createSection(parent, Section.TITLE_BAR|Section.COMPACT);
        section.setText("Items");
        section.marginHeight = 5;
        section.marginWidth = 10;
                
        // Creates the tree.
        Tree tree = toolkit.createTree(section, SWT.V_SCROLL);
        section.setClient(tree);
        toolkit.paintBordersFor(section);
        
        // TODO remove this layout test code
        //section.setLayoutData(new FormData(50,50));
        //((SashForm)parent).setMaximizedControl(section);
        
        // Creates the tree viewer.
        this.treeViewer = new TreeViewer(tree);
        this.treeViewer.setContentProvider(new MasterContentProvider());
        this.treeViewer.setLabelProvider(new MasterLabelProvider());
        this.treeViewer.addSelectionChangedListener(new SelectionForwarder(new SectionPart(section), managedForm));
     }

    /**
     * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
     */
    protected void registerPages(DetailsPart detailsPart) {
        detailsPart.registerPage(Setting.class, new DetailsPage());
    }

    /**
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createToolBarActions(IManagedForm managedForm) {}
    
    /**
     * Sets the input object.
     * 
     * @param	input	the new input
     */
    public void setInput(Object input) {
        this.treeViewer.setInput(input);
    }

}
