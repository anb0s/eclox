/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2005 Guillaume Brocker
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.doxyfiles.Doxyfile;
import eclox.doxyfiles.PropertyProvider;
import eclox.doxyfiles.Setting;
import eclox.ui.editor.form.settings.filters.ByGroup;
import eclox.ui.editor.form.settings.filters.IFilter;

/**
 * Implements the master part's user interface.
 * 
 * @author gbrocker
 */
public class MasterPart extends SectionPart {

    /**
     * the list viewer
     */
    private ListViewer listViewer;
    
    /**
     * the parent composite for filter buttons
     */
    private Composite filterButtonContainer;
        
    /**
     * Implements the master content provider.
     * 
     * @author gbrocker
     */
    protected class MyContentProvider implements IStructuredContentProvider {

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
            Doxyfile    doxyfile = (Doxyfile) inputElement;
            return doxyfile.getSettings();
        }
    }

    /**
     * Implements the label provider.
     * 
     * @author gbrocker
     */
    protected class MyLabelProvider extends LabelProvider {

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
     * Implements a tree viewer selection listener that forwards selection changes to 
     * a managed form.
     * 
     * @author gbrocker
     */
    protected class MySelectionForwarder implements ISelectionChangedListener {

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
         * @param   managedForm a managed form instance that will received forwarded seletion changes
         */
        public MySelectionForwarder(IFormPart formPart, IManagedForm managedForm) {
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
     * Constructor
     * 
     * @param   parent  a composite that is the parent of all part controls
     * @param   toolkit a toolkit used to create the part controls.
     */
    public MasterPart( Composite parent, FormToolkit toolkit ) {
        super( parent, toolkit, Section.TITLE_BAR|Section.COMPACT );
        
        // Initializes the managed section.
        Section section = getSection();
        section.setText("All Settings");
        section.marginHeight = 5;
        section.marginWidth = 10;
        
        // Creates the main section container.
        Composite   rootContainer = toolkit.createComposite( section );
        GridLayout  layout = new GridLayout();
        section.setClient( rootContainer );
        rootContainer.setBackground( new Color(section.getDisplay(), 255, 0, 0));
        rootContainer.setLayout( layout );
        layout.marginWidth = 0;
        layout.marginBottom = 0;

        // Creates the filter buttons.
        filterButtonContainer = toolkit.createComposite( rootContainer );
        filterButtonContainer.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
        addFilter( toolkit, new ByGroup() );
        
        // Creates the list widget that will display all settings.
        List list = new List( rootContainer, SWT.V_SCROLL|SWT.BORDER);
        list.setLayoutData( new GridData(GridData.FILL_BOTH) );
        toolkit.paintBordersFor( section );
        
        // Creates the list viewer.
        listViewer = new ListViewer( list );
        listViewer.setContentProvider( new MyContentProvider() );
        listViewer.setLabelProvider( new MyLabelProvider() );
    }
    
    /**
     * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form) {
        // Installs a listener that will forward the selection changes.
        listViewer.addSelectionChangedListener( new MySelectionForwarder(this, form) );
        super.initialize(form);
    }

    /**
     * @see org.eclipse.ui.forms.AbstractFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput( Object input ) {
        listViewer.setInput( input );
        return super.setFormInput( input );
    }

    /**
     * Adds a new filter to the master part.
     * 
     * @param   toolkit a toolkit to use for the widget creation
     * @param   filter  a new filter to add
     */
    private void addFilter( FormToolkit toolkit, IFilter filter ) {
        Button button = toolkit.createButton( filterButtonContainer, filter.getName(), SWT.FLAT|SWT.TOGGLE);
    }
}
