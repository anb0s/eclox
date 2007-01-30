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

package eclox.core.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import eclox.core.IPreferences;
import eclox.core.doxygen.Doxygen;
import eclox.core.doxygen.CustomDoxygen;

/**
 * @author Guillaume Brocker
 */
public class DefaultDoxygenFieldEditor extends FieldEditor {
	
	/**
	 * defines the version column index
	 */
	private final int VERSION_COLUMN_INDEX = 0;
	
	/**
	 * defines the description column index
	 */
	private final int DESCRIPTION_COLUMN_INDEX = 1;
	
	/**
	 * the collection of available doxygen wrappers
	 */
	private Collection doxygens = Doxygen.getAll();
	
	/**
	 * the table control showing available doxygen wrappers
	 */
	private Table table;
	
	/**
	 * the composite containing all buttons
	 */
	private Composite buttons;
	
	/**
	 * the button that triggers the addition of a custom doxygen
	 */
	private Button add;
	
	/**
	 * the button that triggers the edition of a custom doxygen
	 */
	private Button edit;
	
	/**
	 * the button that triggers the removal of a custom doxygen
	 */
	private Button remove;
	
	/**
	 * the valid state of the field editor
	 */
	private boolean valid = true;
	
	/**
	 * the preference change listener
	 */
	private MyPrefercenceChangeListener preferenceChangeListener;
	
	
	/**
	 * Implements a selection listener for the owned table control.
	 * 
	 * It is reponsible to ensure that only one table item is selected at a time
	 * and it also fires value change notifications.
	 */
	private class MySelectionListener implements SelectionListener {

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected( e );
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			TableItem	item	= (TableItem) e.item;
			
			if( item.getChecked() == true ) {
				TableItem[]	checked	= getCheckedItems();
				
				// Updates chekced items so that only one is chekced at the same time.
				for( int i = 0; i < checked.length; ++i ) {
					if( checked[i] != item ) {
						checked[i].setChecked( false );
					}
				}
				
				// Fires some notifications.
				fireValueChanged( VALUE, null, null );
			}
			
			// Refreshes the field validity.
			refreshValidState();
		}
		
	}
	
	
	/**
	 * Implements a listener of the preference store that will trigger updates
	 * of the version information for local doxygen.
	 */
	private class MyPrefercenceChangeListener implements IPropertyChangeListener {

		/**
		 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
		 */
		public void propertyChange( PropertyChangeEvent event ) {
			if( event.getProperty().equals(IPreferences.DOXYGEN_COMMAND) ) {
				TableItem[]	items	= table.getItems();
				for( int i = 0; i < items.length; ++i ) {
					Doxygen	dox = (Doxygen) items[i].getData();
					if (dox instanceof CustomDoxygen) {
						final String	version = dox.getVersion();
						
						items[i].setText( VERSION_COLUMN_INDEX, (version != null) ? version : "unknown" );
					}
				}
			}
		}
		
	}
	
	
	/**
	 * Checkes the table item representing the given doxygen instance.
	 */
	private void checkItem( Doxygen doxygen ) {
		TableItem[]	items = table.getItems();
		
		for( int i = 0; i < items.length; ++i ) {
			items[i].setChecked( items[i].getData() == doxygen ); 
		}
	}
	
	
	/**
	 * Retrieves checked items.
	 * 
	 * @return	the checked item or null if none
	 */
	private TableItem[] getCheckedItems() {
		// Pre-condition
		assert( table != null );
		
		Vector		checked	= new Vector();
		TableItem[]	items	= table.getItems();
		
		for( int i = 0; i < items.length; ++i ) {
			if( items[i].getChecked() == true ) {
				checked.add( items[i] );
			}
		}
		return (TableItem[]) checked.toArray( new TableItem[0] );
	}
	

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		// Pre-condition
		assert( table != null );
		
		GridData	tableData	= (GridData) table.getLayoutData();
		GridData	buttonsData	= (GridData) buttons.getLayoutData();
		
		tableData.horizontalSpan = numColumns - 1;
		buttonsData.horizontalSpan = 1;
	}

	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		// Pre-condition
		assert( table == null );
		
		// Creates the combo controls containing all available doxygen wrappers.
		GridData	tableData = new GridData( GridData.FILL_BOTH );
		
		table = new Table( parent, SWT.SINGLE|SWT.CHECK|SWT.BORDER );
		tableData.horizontalSpan = numColumns - 1;
		table.setLayoutData( tableData );
		table.addSelectionListener( new MySelectionListener() );
		
		TableColumn	versionColumn		= new TableColumn( table, SWT.LEFT, VERSION_COLUMN_INDEX );
		TableColumn	descriptionColumn	= new TableColumn( table, SWT.LEFT, DESCRIPTION_COLUMN_INDEX );
		
		versionColumn.setText( "Version" );
		descriptionColumn.setText( "Description" );
		table.setHeaderVisible( true );
		
		
		// Creates the composite containing all buttons and located on the right side of the table.
		GridData	buttonsData		= new GridData( GridData.END );
		FillLayout	buttonsLayout	= new FillLayout( SWT.VERTICAL );
		
		buttons = new Composite( parent, SWT.NO_BACKGROUND );
		buttonsData.horizontalSpan = 1;
		buttonsData.horizontalAlignment = SWT.FILL;
		buttons.setLayoutData( buttonsData );
		buttonsLayout.spacing = 5;
		buttons.setLayout( buttonsLayout );
		
		
		
		// Creates the button controlling custom doyxgen wrappers.
		add		= new Button( buttons, SWT.PUSH );
		edit	= new Button( buttons, SWT.PUSH );
		remove	= new Button( buttons, SWT.PUSH );
		
		add.setText( "Add..." );
		edit.setText( "Edit..." );
		remove.setText( "Remove" );
	}

	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad() {
		Doxygen	doxygen = Doxygen.get( getPreferenceStore().getString(getPreferenceName()) );
		
		checkItem( doxygen );		
	}

	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		Doxygen	doxygen = Doxygen.get( getPreferenceStore().getDefaultString(getPreferenceName()) );
		
		checkItem( doxygen );
	}

	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		TableItem[]	checked = getCheckedItems();
		
		if( checked.length == 1 ) {
			Doxygen	doxygen = (Doxygen) checked[0].getData();
			
			getPreferenceStore().setValue( getPreferenceName(), doxygen.getIdentifier() );
		}
	}
	
	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#refreshValidState()
	 */
	protected void refreshValidState() {
		TableItem[]	checked		= getCheckedItems();
		boolean		oldValid	= valid;
		boolean		newValid	= checked.length == 1;
		
		// Updates validity and error message. 
		valid = newValid;
		if( valid == false ) {
			showErrorMessage( "Select the doxygen version to use." );
		}
		else {
			clearErrorMessage();
		}
		
		// Send some notifications.
		if( newValid != oldValid ) {
			fireStateChanged( IS_VALID, oldValid, newValid );
		}
	}
	
	
	/**
	 * Constructor
	 */
	public DefaultDoxygenFieldEditor( Composite parent ) {
		super( IPreferences.DEFAULT_DOXYGEN, "Doxygen:", parent );

		// Pre-condition
		assert( table != null );

		// Fills the table with all available doxygen wrappers.
		Iterator	i = doxygens.iterator();
		while( i.hasNext() ) {
			// Item data preparation.
			Doxygen	doxygen		= (Doxygen) i.next();
			String	description = doxygen.getDescription();
			String	version		= doxygen.getVersion();
			
			if( version == null ) {
				version = "unknown";
			}
			
			// Item creation.
			TableItem	item = new TableItem( table, 0 );
			
			item.setText( VERSION_COLUMN_INDEX, version );
			item.setText( DESCRIPTION_COLUMN_INDEX, description );
			item.setData( doxygen );
		}
		table.getColumn( VERSION_COLUMN_INDEX ).pack();
		table.getColumn( DESCRIPTION_COLUMN_INDEX ).pack();
		table.layout();
	}

	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		return 2;
	}


	/**
	 * @see org.eclipse.jface.preference.FieldEditor#isValid()
	 */
	public boolean isValid() {
		return valid;
	}


	public void setPreferenceStore(IPreferenceStore store) {
		if( store != null ) {
			preferenceChangeListener = new MyPrefercenceChangeListener();
			store.addPropertyChangeListener( preferenceChangeListener );
		}
		else {
			getPreferenceStore().removePropertyChangeListener( preferenceChangeListener );
			preferenceChangeListener = null;
		}
		super.setPreferenceStore(store);
	}


	/**
	 * @see org.eclipse.jface.preference.FieldEditor#setFocus()
	 */
	public void setFocus() {
		super.setFocus();
		table.setFocus();		
	}
	
}
