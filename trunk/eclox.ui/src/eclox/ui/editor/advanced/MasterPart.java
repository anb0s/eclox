// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2007 Guillaume Brocker
// 
// This file is part of eclox.
// 
// eclox is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
// 
// eclox is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with eclox; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA    

package eclox.ui.editor.advanced;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
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
 * 
 * TODO Add support for history action drop down menu.
 */
public class MasterPart extends SectionPart implements IPartSelectionListener {

	/**
     * Implements the master content provider.
     */
    private class MyContentProvider implements IStructuredContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {}

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object inputElement) {
            Doxyfile    doxyfile = (Doxyfile) inputElement;
            return doxyfile.getSettings();
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
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
     * Implements the label provider.
     */
	private class MyLabelProvider extends LabelProvider implements ITableLabelProvider, ISettingPropertyListener {
		
		/**
		 * the set of all settings the label provider has registered to
		 */
		private Set settings = new HashSet();
		
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

    }
    
    /**
     * Implements a tree viewer selection listener.
     */
    private class MySelectionListener implements ISelectionChangedListener {

    	public boolean sleeping = false;
    	
        /**
         * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
         */
        public void selectionChanged(SelectionChangedEvent event) {
        	if( sleeping == false ) {
	        	ISelection selection = event.getSelection();
	        	
	        	if( selection.isEmpty() == false ) {
	        		goneTo(selection);
	        		goBack.refresh();
	        		goForward.refresh();
	        	}
	        	fireSelectionChanged(selection);
        	}
        }

    }
        
    /**
     * Implements the menu creator for history actions
     */
//    private class MyHistoryActionMenuCreator implements IMenuCreator {
//    	
//    	Menu menu;
//    	Vector history;
//    	
//    	public MyHistoryActionMenuCreator(Vector history) {
//    		this.history = history;
//    	}
//
//		public void dispose() {
//			// Pre-condition
//			assert menu != null;
//			
//			menu.dispose();
//		}
//
//		public Menu getMenu(Control parent) {
//			menu = new Menu(parent);
//			refresh(menu);
//			return menu;
//		}
//
//		public Menu getMenu(Menu parent) {
//			menu = new Menu(parent);
//			refresh(menu);
//			return menu;
//		}
//		
//		public void refresh(Menu menu) {
//			ListIterator	i = history.listIterator(history.size());
//			while(i.hasPrevious()) {
//				IStructuredSelection	selection = (IStructuredSelection) i.previous();
//				Setting					setting = (Setting) selection.getFirstElement();
//				MenuItem				menuItem = new MenuItem(menu, 0);
//				
//				menuItem.setText(setting.getProperty(Setting.TEXT));
//			}
//		}
//    	
//    }
    
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
     * the table viewer selection listener
     */
    private MySelectionListener tableViewerSelectionListener;
	
    /**
     * the collection of selections preceeding the current selection
     */
    private Stack backSelections = new Stack();

    /**
     * the collection of selections following the current selection 
     */
    private Stack forwardSelections = new Stack();
    
    /**
     * the current selection
     */
    private ISelection currentSelection;
    
    /**
     * the go backward history action
     */
    private HistoryAction goBack = new HistoryAction(HistoryAction.BACK, this);
    
    /**
     * the go foreward history action
     */
    private HistoryAction goForward = new HistoryAction(HistoryAction.FORWARD, this);
    
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
	        tableViewer.refresh();
	        
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
	 * Notifies the form that the selection changed.
	 * 
	 * @param	selection	the new selection
	 */
	private void fireSelectionChanged( ISelection selection ) {
		getManagedForm().fireSelectionChanged(this, selection);
	}
	
	/**
	 * Retrieves the back selection history.
	 * 
	 * @return	a stack representing the back selection history
	 */
	public Stack getBackSelections() {
		return backSelections;
	}
	
	/**
	 * Retrieves the forward selection history.
	 * 
	 * @return	a stack representing the forward selection history
	 */
	public Stack getForwardSelections() {
		return forwardSelections;
	}
	
	/**
	 * Steps back in the selection history.
	 */
	public void goBack() {
		// Pre-condition
		assert backSelections.empty() == false;
		
		// Saves the current selection into the foreward selection stack.
		if( currentSelection != null ) {
			forwardSelections.push(currentSelection);
		}
		
		// Restores the previous selection.
		tableViewerSelectionListener.sleeping = true;
		
		currentSelection = (ISelection) backSelections.pop();
		activateFilter( defaultFilter );
		tableViewer.setSelection( currentSelection, true );
		fireSelectionChanged(currentSelection);
		
		tableViewerSelectionListener.sleeping = false;

		// Refreshes the history actions.
		goBack.refresh();
		goForward.refresh();
		
		// Notifies about the selection change.
		fireSelectionChanged(currentSelection);
	}

	/**
	 * Steps forward in the selectio history
	 */
	public void goForward() {
		// Pre-condition
		assert forwardSelections.empty() == false;

		// Saves the current selection into the backward selection stack.
		if( currentSelection != null ) {
			backSelections.push(currentSelection);
		}
		
		// Restores the previous selection
		tableViewerSelectionListener.sleeping = true;
		
		currentSelection = (ISelection) forwardSelections.pop();
		activateFilter( defaultFilter );
		tableViewer.setSelection( currentSelection, true );
		fireSelectionChanged(currentSelection);
		
		tableViewerSelectionListener.sleeping = false;
				
		// Refreshes the history actions.
		goBack.refresh();
		goForward.refresh();

		// Notifies about the selection change.
		fireSelectionChanged(currentSelection);
	}
	
	/**
	 * Notifies that the selection has changed and the hitory must be updated
	 * 
	 * @param selection	the new selection
	 */
	private void goneTo( ISelection selection ) {
		if( currentSelection == null || selection.equals(currentSelection) == false ) {
			// Saves the current selection in the backward stack.
			if( currentSelection != null ) {
				backSelections.push(currentSelection);
			}
			
			// Clears the foreward selection stack.
			forwardSelections.clear();
			
			// Installs the current selection.
			currentSelection = selection;
		}
	}
    
    /**
     * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form) {
        // Pre-condition
        assert tableViewer != null;
        
        // Installs several listeners.
        tableViewerSelectionListener = new MySelectionListener();
        
        tableViewer.addPostSelectionChangedListener( tableViewerSelectionListener );
        tableViewer.addDoubleClickListener( new MyDoubleClickListener() );
        
        // Installs the actions
        goBack.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
        goForward.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
        form.getForm().getToolBarManager().add(goBack);
        form.getForm().getToolBarManager().add(goForward);
		goBack.refresh();
		goForward.refresh();
        
        // Default job done by super class.
        super.initialize(form);
    }
    
    /**
	 * @see org.eclipse.ui.forms.AbstractFormPart#isStale()
	 */
	public boolean isStale() {
		// We always answer yes because it is currently not trivial
		// to know if the data model has changed since last refresh.
		return true;
	}
	
    /**
     * @see org.eclipse.ui.forms.AbstractFormPart#refresh()
     */
    public void refresh() {
		tableViewer.refresh();
		super.refresh();
	}
    
    /**
	 * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IFormPart part, ISelection selection) {
		if( part != this ) {
			activateFilter( defaultFilter );
			tableViewer.setSelection( selection, true );
			goneTo(selection);
			goBack.refresh();
			goForward.refresh();
		}
	}
	
}
