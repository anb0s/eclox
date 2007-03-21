/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2006 Guillaume Brocker
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

package eclox.ui.editor.advanced;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IPartSelectionListener;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.ISettingPropertyListener;
import eclox.core.doxyfiles.Setting;
import eclox.ui.editor.Editor;
import eclox.ui.editor.advanced.filters.All;
import eclox.ui.editor.advanced.filters.ByGroup;
import eclox.ui.editor.advanced.filters.Custom;
import eclox.ui.editor.advanced.filters.IFilter;
import eclox.ui.editor.advanced.filters.Modified;

/**
 * Implements the master part's user interface.
 * 
 * @author gbrocker
 */
public class MasterPart extends SectionPart implements IPartSelectionListener {

	/**
	 * the text column index
	 */
	private final static int TEXT_COLUMN = 0;
	
	/**
	 * the value column index
	 */
	private final static int VALUE_COLUMN = 1;
	
	/**
	 * the doxyfile being edited
	 */
	private Doxyfile doxyfile;
	
    /**
     * the active filter
     */
    private IFilter activeFilter;
    
    /**
     * the default filter
     */
    private IFilter defaultFilter = new All();
    
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
    private TableViewer tableViewer;
    
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
     * Implements the mouse listener attached to the table.
     * 
     * This listener will search for a DetailsPart in the managed form given
     * at construction time and will set the focus on that part.
     */
    private class MyDoubleClickListener implements IDoubleClickListener {
        
        /**
         * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
         */
        public void doubleClick(DoubleClickEvent event) {
        	IManagedForm	managedForm = getManagedForm();
            
            // Searchs for the details part in the managed form and set the focus on it.
            IFormPart   parts[] = managedForm.getParts();
            for( int i = 0; i < parts.length; ++i ) {
                IFormPart   currentPart = parts[i];
                if( currentPart instanceof DetailsPart ) {
                    currentPart.setFocus();
                    break;
                }
            }
        }
        
    }

    /**
     * Implements the label provider.
     */
	private class MyLabelProvider extends LabelProvider implements ITableLabelProvider, ISettingPropertyListener {
		
		/**
		 * the set of all settings the label provider has registered to
		 */
		private Set settings = new HashSet();
		
		public void settingPropertyChanged(Setting setting, String property) {
			if( property.equals( Editor.PROP_SETTING_DIRTY ) ) {
				fireLabelProviderChanged( new LabelProviderChangedEvent(this, setting) );
			}
		}
		
		public void settingPropertyRemoved(Setting setting, String property) {
			if( property.equals( Editor.PROP_SETTING_DIRTY ) ) {
				fireLabelProviderChanged( new LabelProviderChangedEvent(this, setting) );
			}
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			// We don't have any image to return.
			return null;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			// Pre-condition
			assert element instanceof Setting;
			
			// Retrieves the setting's text.
            Setting setting = (Setting) element;
            
            // Registers as an observer of the setting
            setting.addSettingListener( this );
            settings.add( setting );
            
            // Determine the text to return according to the given column index.
            String	columnText;
            if( columnIndex == TEXT_COLUMN ) {
            		columnText = setting.hasProperty( Setting.TEXT )					? setting.getProperty( Setting.TEXT ) : setting.getIdentifier();
                columnText = setting.hasProperty( Editor.PROP_SETTING_DIRTY )	? ("*").concat( columnText ) : columnText; 
            }
            else if (columnIndex == VALUE_COLUMN ){
            		columnText = setting.getValue();
            }
            else {
            		columnText = null;
            }
            return columnText;
		}

		/**
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			// Walks through all settings and unregisters from the listeners
			Iterator	i = settings.iterator();
			while( i.hasNext() == true ) {
				Object	object = i.next();
				Setting	setting = (Setting) object;
				
				setting.removeSettingListener( this );
			}
			settings.clear();
			
			super.dispose();
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
            Object  data   = e.widget.getData(); 
            IFilter filter = (IFilter) data;
           	activateFilter( filter );
        }

        /**
         * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        public void widgetSelected(SelectionEvent e) {
            Object  data   = e.widget.getData(); 
            IFilter filter = (IFilter) data;
            activateFilter( filter );
        }
        
    }
    
    /**
     * Constructor
     * 
     * @param   parent		a composite that is the parent of all part controls
     * @param   toolkit		a toolkit used to create the part controls.
     * @param	doxyfile	a doxyfile to edit
     */
    public MasterPart( Composite parent, FormToolkit toolkit, Doxyfile doxyfile ) {
        super( parent, toolkit, Section.TITLE_BAR|Section.COMPACT );
        
        this.doxyfile = doxyfile;
        
        // Initializes the managed section.
        Section section = getSection();
        section.setText("Settings");
        section.marginHeight = 5;
        section.marginWidth = 10;
        toolkit.paintBordersFor( section );
        
        // Creates the main section container.
        Composite   rootContainer = toolkit.createComposite( section );
        section.setClient( rootContainer );
        rootContainer.setLayout( new FormLayout() );
        
        // Continues with other initializations.
        createFilterButtons( toolkit, rootContainer );
        createSubControls( toolkit, rootContainer );
                
        // Adds some filters.
        addFilter( toolkit, defaultFilter );
        addFilter( toolkit, new ByGroup() );
        addFilter( toolkit, new Custom() );
        addFilter( toolkit, new Modified() );
        
        // Activates the default filter.
        activateFilter( defaultFilter );
    }
    
    /**
     * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form) {
        // Pre-condition
        assert tableViewer != null;
        
        // Installs several listeners.
        tableViewer.addSelectionChangedListener( new MySelectionForwarder(this, form) );
        tableViewer.addDoubleClickListener( new MyDoubleClickListener() );
        
        // Default job done by super class.
        super.initialize(form);
    }

	/**
	 * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IFormPart part, ISelection selection) {
		if( part != this ) {
			activateFilter( defaultFilter );
			tableViewer.setSelection( selection, true );
		}			
	}

    /**
     * Creates all buttons for setting filters.
     * 
     * @param   toolkit the form tool to use for the control creation
     * @param   parent  a composite that will be the parent of all controls
     */
    private void createFilterButtons( FormToolkit toolkit, Composite parent ) {
        // Pre-condition
        assert filterButtons == null;
        assert filterControls == null;
        
        // Creates the filter button container.
        FillLayout   buttonContainerLayout   = new FillLayout( SWT.HORIZONTAL );
        filterButtons = toolkit.createComposite( parent );
        buttonContainerLayout.marginWidth    = 0;
        buttonContainerLayout.marginHeight  = 0;
        buttonContainerLayout.spacing = 3;
        filterButtons.setLayout( buttonContainerLayout );
        
        // Assignes layout data fo the filter button container.
        FormData    buttonFormData = new FormData();
        buttonFormData.top   = new FormAttachment(   0, 0 );
        buttonFormData.right = new FormAttachment( 100, 0 );
        buttonFormData.left  = new FormAttachment(   0, 0 );
        filterButtons.setLayoutData( buttonFormData );
        
        
        // Post-condition
        assert filterButtons != null;
    }
    
    /**
     * Creates controls subordinated to the filter buttons.
     */
    private void createSubControls( FormToolkit toolkit, Composite parent ) {
        // Pre-condition
        assert filterButtons != null;
        assert filterControls == null;
        assert tableViewer == null;
        
        toolkit.paintBordersFor( parent );
                
        // Creates the sub parent control container.
        Composite   subParent = toolkit.createComposite( parent );
        FormData    subParentFormData = new FormData();
        subParentFormData.top    = new FormAttachment( filterButtons, 6, SWT.BOTTOM );
        subParentFormData.right  = new FormAttachment( 100, -2 );
        subParentFormData.bottom = new FormAttachment( 100, -2 );
        subParentFormData.left   = new FormAttachment(   0,  1 );
        subParentFormData.height = 50;
        subParent.setLayoutData( subParentFormData );
        subParent.setLayout( new FormLayout() );
        subParent.setData( FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER );

        
        // Creates the filter control container.
        FormData    controlFormData = new FormData();
        controlFormData.top   = new FormAttachment(   0,  5 );
        controlFormData.right = new FormAttachment( 100, -5 );
        controlFormData.left  = new FormAttachment(   0,  5 );
        filterControls = toolkit.createComposite( subParent );
        filterControls.setLayoutData( controlFormData );
        
        // Creates the table widget that will display all settings.
        Table	table = new Table( subParent, SWT.V_SCROLL|SWT.FULL_SELECTION );
        FormData formData = new FormData();
        formData.top    = new FormAttachment( filterControls, 5, SWT.BOTTOM );
        formData.right  = new FormAttachment( 100, 0 );
        formData.bottom = new FormAttachment( 100, 0 );
        formData.left   = new FormAttachment(   0, 0 );
        formData.height = 10;
        formData.width  = 10;
        table.setLayoutData( formData );
        table.setHeaderVisible( true );
        
        // Adds some columns to the table 
        TableColumn	tableColumn;
        tableColumn = new TableColumn( table, SWT.LEFT, TEXT_COLUMN );
        tableColumn.setText( "Name" );
        tableColumn = new TableColumn( table, SWT.LEFT, VALUE_COLUMN );
        tableColumn.setText( "Value" );
        
        // Creates the table viewer.
        tableViewer = new TableViewer( table );
        tableViewer.setContentProvider( new MyContentProvider() );
        tableViewer.setLabelProvider( new MyLabelProvider() );
        tableViewer.setInput( doxyfile );
        
        // Updates the column widths.
        TableColumn	columns[] = table.getColumns();
        columns[ TEXT_COLUMN ].pack();
        columns[ VALUE_COLUMN ].pack();

        
        // Post-condition
        assert filterControls != null;
        assert tableViewer != null;
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
		Section	section = getSection();
		
		// Freezes the section widget.
		section.setRedraw( false );
			
        // Updates the filter button's state.
        Control[]   controls = filterButtons.getChildren();
        for( int i = 0; i < controls.length; ++i ) {
            Control control	= controls[i];
            Button  button	= (Button) control;
            
            button.setSelection( control.getData() == filter );
        }

        // If there is a new filter to activate, do the activation job.
        if( filter != activeFilter ) {

   	        // Deactivates the previous filter.
	        if( activeFilter != null ) {
	            activeFilter.disposeViewerFilers( tableViewer );
	            activeFilter.disposeControls();
	            activeFilter.setDoxyfile( null );
	            activeFilter = null;
	        }
	        
	        // Activates the new filter.
	        activeFilter = filter;
	        activeFilter.setDoxyfile( doxyfile );
	        activeFilter.createControls( getManagedForm(), filterControls );
	        activeFilter.createViewerFilters( tableViewer );
	        
	        // Adapts the size of the filter control container & relayout the section content.
	        Object      tableLayoutData = tableViewer.getTable().getLayoutData();
	        FormData    tableFormData = (FormData) tableLayoutData;
	        if( filterControls.getChildren().length == 0 ) {
	        		filterControls.setVisible( false );
	        		tableFormData.top = new FormAttachment( 0, 0 );
	        }
	        else {
	        		filterControls.setVisible( true );
	        		tableFormData.top = new FormAttachment( filterControls, 6, SWT.BOTTOM );
	        }
	        
	        // Reactivates section widget.
	        section.layout( true, true );
        }
        
        // Reactivates the redrawing.
        section.setRedraw( true );
    }
}
