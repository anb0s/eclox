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

package eclox.ui.editor.form.settings;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.doxyfiles.Doxyfile;
import eclox.doxyfiles.PropertyProvider;
import eclox.doxyfiles.Setting;

/**
 * @author gbrocker
 */
public class Block extends MasterDetailsBlock {
    
    /**
     * the list viewer
     */
    private ListViewer listViewer;
        
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
            if(element instanceof Setting) {
                Setting setting = (Setting) element;
                String text = PropertyProvider.getDefault().getText(setting);
                return text != null ? text : setting.getIdentifier();
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
    protected class MasterContentProvider implements IStructuredContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {}

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object inputElement) {
            Doxyfile	doxyfile = (Doxyfile) inputElement;
            return doxyfile.getSettings();
        }
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
        FormToolkit		toolkit = managedForm.getToolkit();
        
        // Creates the section containing the tree view
        Section	section = toolkit.createSection(parent, Section.TITLE_BAR|Section.COMPACT);
        section.setText("All Settings");
        section.marginHeight = 5;
        section.marginWidth = 10;
                
        // Creates the list widget that will display all settings.
        List list = new List(section, SWT.V_SCROLL|SWT.BORDER);
        section.setClient(list);
        toolkit.paintBordersFor(section);
        
        // Creates the list viewer.
        this.listViewer = new ListViewer( list );
        this.listViewer.setContentProvider( new MasterContentProvider() );
        this.listViewer.setLabelProvider( new MasterLabelProvider() );
        this.listViewer.addSelectionChangedListener(new SelectionForwarder(new SectionPart(section), managedForm));
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
        this.listViewer.setInput(input);
    }

}
