/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - moved and improved dialog code to CustomDoxygenDialog
 *                   - added support of eclipse variables to resolve doxygen path
 *
 ******************************************************************************/

package eclox.core.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.preference.FieldEditor;
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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import eclox.core.IPreferences;
import eclox.core.Plugin;
import eclox.core.doxygen.BundledDoxygen;
import eclox.core.doxygen.CustomDoxygen;
import eclox.core.doxygen.DefaultDoxygen;
import eclox.core.doxygen.Doxygen;

/**
 * @author Guillaume Brocker
 */
public class DefaultDoxygenFieldEditor extends FieldEditor {

	/**
	 * defines the version column index
	 */
	private final int VERSION_COLUMN_INDEX = 0;

	/**
	 * defines the type column index
	 */
	private final int TYPE_COLUMN_INDEX = 1;

	/**
	 * defines the description column index
	 */
	private final int DESCRIPTION_COLUMN_INDEX = 2;

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
			if		(e.item instanceof TableItem )	onItemSelected( (TableItem) e.item );
			else if	(e.getSource() == add )			onAddSelected();
			else if	(e.getSource() == edit)			onEditSelected();
			else if (e.getSource() == remove)		onRemoveSelected();
		}

	}


	/**
	 * Adds a new table item for the given doxygen instance
	 */
	private void addItem( Doxygen doxygen ) {
		TableItem	item = new TableItem( table, 0 );

		item.setData( doxygen );
		updateItem( item );
	}


	/**
	 * Retrieves the type of the given doxygen wrapper instance
	 */
	private static String getDoxygenType( final Doxygen doxygen ) {
		if		( doxygen instanceof DefaultDoxygen	)	return "Default";
		else if	( doxygen instanceof CustomDoxygen	)	return "Custom";
		else if	( doxygen instanceof BundledDoxygen	)	return "Bundled";
		else											return "Uknown";
	}


	/**
	 * Updates the given table item.
	 */
	private void updateItem( TableItem item ) {
		// Item data preparation.
		final Doxygen	doxygen		= (Doxygen) item.getData();
		final String	type		= getDoxygenType( doxygen );
		final String	version		= doxygen.getVersion();
		final String	description = doxygen.getDescription();

		// Updates the item properties.
		item.setImage( (version == null) ? PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK) : null );
		item.setText( VERSION_COLUMN_INDEX, (version == null) ? "unknown" : version );
		item.setText( TYPE_COLUMN_INDEX, type );
		item.setText( DESCRIPTION_COLUMN_INDEX, description );

		// Updates the table layout.
		table.getColumn( VERSION_COLUMN_INDEX ).pack();
		table.getColumn( TYPE_COLUMN_INDEX ).pack();
		table.getColumn( DESCRIPTION_COLUMN_INDEX ).pack();
		table.layout();
	}


	/**
	 * Checkes the table item representing the given doxygen identifier.
	 */
	private void checkItem( final String identifier ) {
		TableItem[]	items = table.getItems();
		for( int i = 0; i < items.length; ++i ) {
			Doxygen	current = (Doxygen) items[i].getData();
			items[i].setChecked( current.getIdentifier().equalsIgnoreCase(identifier) );
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

		Vector<TableItem>		checked	= new Vector<TableItem>();
		TableItem[]	items	= table.getItems();

		for( int i = 0; i < items.length; ++i ) {
			if( items[i].getChecked() == true ) {
				checked.add( items[i] );
			}
		}
		return (TableItem[]) checked.toArray( new TableItem[0] );
	}


	/**
	 * Process the click on the add button.
	 */
	private void onAddSelected() {
		// Asks the user to select a location containing a doxygen binary.
		CustomDoxygenDialog dialog = new CustomDoxygenDialog(getPage().getShell(), null);
		dialog.open();
		String directory = dialog.getTargetDirectory();
		if( directory != null ) {
			addItem( new CustomDoxygen(directory) );
		}
	}


	/**
	 * Process the click on the edit button
	 */
	private void onEditSelected() {
		// Pre-condition
		assert( table != null );


		// Retrieves the checked items
		TableItem[]	selected = table.getSelection();


		// Retrieves the doxygen wrapper associated to the selected item
		assert( selected.length == 1 );
		Doxygen	doxygen = (Doxygen) selected[0].getData();

		// Asks the user to select a location containing a doxygen binary.
		assert( doxygen instanceof CustomDoxygen );
		CustomDoxygen	customDoxygen	= (CustomDoxygen) doxygen;
		CustomDoxygenDialog dialog = new CustomDoxygenDialog(getPage().getShell(), customDoxygen.getLocation());
		dialog.open();
		String directory = dialog.getTargetDirectory();
		if( directory != null ) {
			customDoxygen.setLocation( directory );
			updateItem( selected[0] );
		}
	}


	/**
	 * Process the click on the remove button
	 */
	private void onRemoveSelected() {
		// Pre-condition
		assert( table != null );

		table.remove( table.getSelectionIndex() );
		refreshValidState();
	}


	/**
	 * Process the selection of the given table item.
	 */
	private void onItemSelected( TableItem item ) {
		if( item.getChecked() == true ) {
			TableItem[]	checked	= getCheckedItems();

			// Updates checked items so that only one is checked at the same time.
			for( int i = 0; i < checked.length; ++i ) {
				if( checked[i] != item ) {
					checked[i].setChecked( false );
				}
			}

			// Selects the item that has been checked.
			table.setSelection( table.indexOf(item) );

			// TODO only supported in eclipse 3.2
			// table.setSelection( item );

			// Fires some notifications.
			fireValueChanged( VALUE, null, null );
		}


		// Updates button states
		final boolean	enable = item.getData() instanceof CustomDoxygen;

		edit.setEnabled( enable );
		remove.setEnabled( enable );


		// Refreshes the field validity.
		refreshValidState();
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

		table = new Table( parent, SWT.SINGLE|SWT.CHECK|SWT.BORDER|SWT.FULL_SELECTION );
		tableData.horizontalSpan = numColumns - 1;
		table.setLayoutData( tableData );
		table.addSelectionListener( new MySelectionListener() );

		TableColumn	versionColumn		= new TableColumn( table, SWT.LEFT, VERSION_COLUMN_INDEX );
		TableColumn	typeColumn			= new TableColumn( table, SWT.LEFT, TYPE_COLUMN_INDEX );
		TableColumn	descriptionColumn	= new TableColumn( table, SWT.LEFT, DESCRIPTION_COLUMN_INDEX );

		versionColumn.setText( "Version" );
		typeColumn.setText( "Type" );
		descriptionColumn.setText( "Description" );
		table.setHeaderVisible( true );

		// Creates the composite containing all buttons and located on the right side of the table.
		GridData	buttonsData		= new GridData( GridData.END );
		FillLayout	buttonsLayout	= new FillLayout( SWT.VERTICAL );

		buttons = new Composite( parent, SWT.NO_FOCUS );
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
		add.addSelectionListener( new MySelectionListener() );
		edit.setText( "Edit..." );
		edit.addSelectionListener( new MySelectionListener() );
		edit.setEnabled( false );
		remove.setText( "Remove" );
		remove.addSelectionListener( new MySelectionListener() );
		remove.setEnabled( false );
	}


	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad() {
		// Adds default doxygen instance.
		addItem( new DefaultDoxygen() );

		// Adds custom doxygens.
		final String raw= getPreferenceStore().getString(IPreferences.CUSTOM_DOXYGENS);
		if ( (raw != null) && (!raw.isEmpty()) ) {
	        final String[]  splitted    = raw.split("\n");
	        for( int i = 0; i < splitted.length; ++i ) {
	            if ( (splitted[i] != null) && (!splitted[i].isEmpty()) ) {
    	            Doxygen doxygen = Doxygen.getFromClassAndIdentifier(CustomDoxygen.class, splitted[i]);
    	            if( doxygen != null ) {
    	                addItem(doxygen);
    	            }
    	            else {
    	                Plugin.getDefault().logError( "Invalid custom doxygen identifier found: '" + splitted[i] + "'");
    	            }
	            }
	        }
		}

		// Adds bundled doxygens.
		Collection<?> bundled = BundledDoxygen.getAll();
		Iterator<?>	  i       = bundled.iterator();
		while( i.hasNext() ) {
			addItem( (Doxygen) i.next() );
		}

		// Select the default doxygen wrapper
		checkItem( getPreferenceStore().getString(IPreferences.DEFAULT_DOXYGEN) );
	}


	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		// Adds default doxygen instance.
		addItem( new DefaultDoxygen() );

		// Select the default doxygen wrapper
		checkItem( getPreferenceStore().getDefaultString( IPreferences.DEFAULT_DOXYGEN ) );
	}


	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		// Pre-condition
		assert( table != null );


		// Saves all custom doxygen wrappers.
		TableItem[]	items		= table.getItems();
		String		serialized	= new String();
		for( int i = 0; i < items.length; ++i ) {
			Object	itemData = items[i].getData();

			if( itemData instanceof CustomDoxygen ) {
				CustomDoxygen	doxygen = (CustomDoxygen) itemData;

				serialized = serialized.concat( doxygen.getIdentifier() );
				serialized = serialized.concat( "\n" );
			}
		}
		getPreferenceStore().setValue( IPreferences.CUSTOM_DOXYGENS, serialized );

		// Saves the checked item.
		TableItem[]	checked			= getCheckedItems();
		String		defaultDoxygen	= new String();
		if( checked.length == 1 ) {
			Doxygen	doxygen = (Doxygen) checked[0].getData();

			defaultDoxygen = doxygen.getIdentifier();
		}
		getPreferenceStore().setValue( IPreferences.DEFAULT_DOXYGEN, defaultDoxygen );
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


	/**
	 * @see org.eclipse.jface.preference.FieldEditor#setFocus()
	 */
	public void setFocus() {
		super.setFocus();
		table.setFocus();
	}

}
