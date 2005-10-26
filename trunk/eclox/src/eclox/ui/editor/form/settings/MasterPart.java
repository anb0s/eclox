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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.doxyfiles.Doxyfile;
import eclox.doxyfiles.Setting;
import eclox.ui.editor.form.settings.filters.All;
import eclox.ui.editor.form.settings.filters.ByGroup;
import eclox.ui.editor.form.settings.filters.IFilter;

/**
 * Implements the master part's user interface.
 * 
 * @author gbrocker
 */
public class MasterPart extends SectionPart {

    /**
     * the active filter
     */
    private IFilter activeFilter;
    
    /**
     * the parent composite for filter buttons
     */
    private Composite filterButtons;
    
    /**
     * the parent composite for filter controls
     */
    private Composite filterControls;
        
    /**
     * the list viewer
     */
    private ListViewer listViewer;
    
    /**
     * Implements the master content provider.
     */
    private class MyContentProvider implements IStructuredContentProvider {

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
     */
    private class MyLabelProvider extends LabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
         */
        public String getText(Object element) {
            if(element instanceof Setting) {
                Setting setting = (Setting) element;
                String text = setting.getProperty( Setting.TEXT );
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
     */
    private class MySelectionForwarder implements ISelectionChangedListener {

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
     * Implements a filter button listener
     */
    private class MyFilterButtonListener implements SelectionListener {

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            Object  data    = e.widget.getData(); 
            IFilter  filter = (IFilter) data;
            activateFilter( filter );
        }

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            Object  data    = e.widget.getData(); 
            IFilter  filter = (IFilter) data;
            activateFilter( filter );
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
        toolkit.paintBordersFor( section );
        
        // Creates the main section container.
        Composite   rootContainer = toolkit.createComposite( section );
        FormLayout  layout = new FormLayout();
        section.setClient( rootContainer );
        rootContainer.setLayout( layout );
        
        // Continues with other initializations.
        createFilterControls( toolkit, rootContainer );
        createListViewer( rootContainer );
        
        // Adds some filters.
        IFilter defaultFilter = new All();  
        addFilter( toolkit, defaultFilter );
        addFilter( toolkit, new ByGroup() );
        activateFilter( defaultFilter );
    }
    
    /**
     * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form) {
        // Pre-condition
        assert listViewer != null;
        
        // Installs a listener that will forward the selection changes.
        listViewer.addSelectionChangedListener( new MySelectionForwarder(this, form) );
        
        // Default job done by super class.
        super.initialize(form);
    }

    /**
     * @see org.eclipse.ui.forms.AbstractFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput( Object input ) {
        // Pre-condition
        assert listViewer != null;
        
        // Assignes the form input to the manager list viewer.
        listViewer.setInput( input );
        return super.setFormInput( input );
    }

    /**
     * Creates all buttons for setting filters.
     * 
     * @param   toolkit the form tool to use for the control creation
     * @param   parent  a composite that will be the parent of all controls
     */
    private void createFilterControls( FormToolkit toolkit, Composite parent ) {
        // Pre-condition
        assert filterButtons == null;
        assert filterControls == null;
        
        // Creates the filter button container.
        FillLayout   buttonContainerLayout   = new FillLayout( SWT.HORIZONTAL );
        filterButtons = toolkit.createComposite( parent );
        buttonContainerLayout.marginWidth    = 0;
        buttonContainerLayout.marginHeight  = 0;
        filterButtons.setLayout( buttonContainerLayout );
        
        // Assignes layout data fo the filter button container.
        FormData    buttonFormData = new FormData();
        buttonFormData.top   = new FormAttachment(   0, 0 );
        buttonFormData.right = new FormAttachment( 100, 0 );
        buttonFormData.left  = new FormAttachment(   0, 0 );
        filterButtons.setLayoutData( buttonFormData );
        
        // Creates the filter control container.
        FormData    controlFormData = new FormData();
        controlFormData.top   = new FormAttachment( filterButtons, 5, SWT.BOTTOM );
        controlFormData.right = new FormAttachment( 100, 0);
        controlFormData.left  = new FormAttachment(   0, 0 );
        filterControls = toolkit.createComposite( parent );
        filterControls.setLayoutData( controlFormData );

        // Post-condition
        assert filterButtons != null;
        assert filterControls != null;
    }
    
    /**
     * Creates the list viewer
     * 
     * @param   parent  a composite being the parent of the list viewer
     */
    private void createListViewer( Composite parent ) {
        // Pre-condition
        assert listViewer == null;
        assert filterControls != null;
        
       // Creates the list widget that will display all settings.
       List     list = new List( parent, SWT.V_SCROLL|SWT.BORDER);
       FormData formData = new FormData();
       formData.top    = new FormAttachment( filterButtons, 5, SWT.BOTTOM );
       formData.right  = new FormAttachment( 100, 0 );
       formData.bottom = new FormAttachment( 100, 0 );
       formData.left   = new FormAttachment(   0, 0 );
       formData.height = 10;
       list.setLayoutData( formData );
       
       // Creates the list viewer.
       listViewer = new ListViewer( list );
       listViewer.setContentProvider( new MyContentProvider() );
       listViewer.setLabelProvider( new MyLabelProvider() );
       
       // Post-condition
       assert listViewer != null;
    }
    
    /**
     * Adds a new filter to the master part.
     * 
     * @param   toolkit a toolkit to use for the widget creation
     * @param   filter  a new filter to add
     */
    private void addFilter( FormToolkit toolkit, IFilter filter ) {
        Button 	button = toolkit.createButton( filterButtons, filter.getName(), SWT.FLAT|SWT.TOGGLE);
        button.setData( filter );
        button.addSelectionListener( new MyFilterButtonListener() );
    }
    
    /**
     * Activates the specified filter.
     * 
     * @param   filter  a filter that must be activated
     */
    private void activateFilter( IFilter filter ) {
        // Deactivates the previous filter.
        if( activeFilter != null ) {
            activeFilter.disposeViewerFilers( listViewer );
            activeFilter.disposeControls();
            activeFilter = null;
        }
        
        // Updates the filter button's state.
        Control[]   controls = filterButtons.getChildren();
        int         i;
        for( i = 0; i < controls.length; ++i ) {
            Control control = controls[i];
            Button  button = (Button) control;
            
            button.setSelection( control.getData() == filter );
        }
        
        // Activates the new filter.
        activeFilter = filter;
        activeFilter.createControls( getManagedForm(), filterControls );
        activeFilter.createViewerFilters( listViewer );
        
        // Adapts the size of the filter control container & relayout the section content.
        Object      layoutData = listViewer.getList().getLayoutData();
        FormData    formData = (FormData) layoutData;
        if( filterControls.getChildren().length == 0 ) {
        	filterControls.setVisible( false );
        	formData.top.control = filterButtons;
        }
        else {
        	filterControls.setVisible( true );
        	formData.top.control = filterControls;
        }
        getSection().layout( true );
    }
}
