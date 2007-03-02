// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2006 Guillaume Brocker
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

package eclox.ui.editor.advanced.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.core.doxyfiles.ISettingValueListener;
import eclox.core.doxyfiles.Setting;

/**
 * Implements a list setting editor. This class is abstract since it provides no way to
 * edit value compounds. See derived classes.
 * 
 * @author gbrocker
 */
public abstract class ListEditor implements IEditor, ISettingValueListener {
	
	/**
	 * Implements the table viewer content provider.
	 */
	private class MyContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			// Pre-condition
			assert inputElement == setting;
			
			// Allocates an array containing the indices of value compounds.
			Integer[]	indices = new Integer[valueCompounds.size()];
			for( int i = 0; i < indices.length; ++i ) {
				indices[i] = new Integer(i);
			}
			return indices;
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		
	}
	
	/**
	 * Implements the table viewer label provider.
	 */
	private class MyLabelProvider extends LabelProvider {
		
		public String getText(Object element) {
			// Pre-condition
			assert element instanceof Integer;
			assert valueCompounds != null;
			
			// Retrieves the value comound index.
			Integer	index = (Integer) element;
			
			// Retrieves the value text.
			return (String) valueCompounds.get( index.intValue() );
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
			handleButtonClick( e.widget );
		}

		public void widgetSelected(SelectionEvent e) {
			handleButtonClick( e.widget );
		}
		
		private void handleButtonClick( Widget widget ) {
			if( widget == addButton ) {
				addValueCompound();
			}
			else if( widget == removeButton ) {
				removeValueCompounds();
			}
			else if( widget == upButton ) {
				moveValueCompoundsUp();
			}
			else if( widget == downButton ) {
				moveValueCompoundsDown();
			}
			else {
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
	
	/**
	 * the setting being edited
	 */
	private Setting		setting;
	
	/**
	 * the collection of the setting's value compounds.
	 */
	private Vector		valueCompounds;
	
	/**
	 * the table viewer used to edit the managed setting
	 */
	private ListViewer	listViewer;
	
	/**
	 * the button allowing to trigger a new value addition
	 */
	private Button		addButton;
	
	/**
	 * the button allowing to trigger the deletion of selected values 
	 */
	private Button		removeButton;
	
	/**
	 * the button allowing to move the selected values up
	 */
	private Button		upButton;
	
	/**
	 * the button allowing to move the selected values down
	 */
	private Button		downButton;

	public void commit() {
		// Pre-condition
		assert listViewer != null;
		assert setting != null;
		assert valueCompounds != null;
		
		setting.setValue( valueCompounds );
	}

	public void createContent(Composite parent, FormToolkit formToolkit) {
		// Pre-condition
		assert listViewer == null;
		
		// Installs the layout.
		FormLayout	layout = new FormLayout();
		layout.spacing = 4;
		parent.setLayout( layout );
		
		// Creates the list viewer and installs it in the layout.
		FormData		formData;
		
		listViewer = new ListViewer( parent );
		formData = new FormData();
		formData.top = new FormAttachment( 0, 0 );
		formData.right = new FormAttachment( 85, 0 );
		formData.bottom = new FormAttachment( 100, 0 );
		formData.left = new FormAttachment( 0, 0 );
		listViewer.getControl().setLayoutData( formData );
		
		// Initializes the list viewer.
		listViewer.setContentProvider( new MyContentProvider() );
		listViewer.setLabelProvider( new MyLabelProvider() );
		listViewer.addOpenListener( new MyOpenListener() );
		
		// Creates various buttons and installs them in the layout.
		addButton		= formToolkit.createButton( parent, "Add",    0 );
		removeButton		= formToolkit.createButton( parent, "Remove", 0 );
		upButton			= formToolkit.createButton( parent, "Up",     0 );
		downButton		= formToolkit.createButton( parent, "Down",   0 );
		
		formData = new FormData();
		formData.top = new FormAttachment( 0, 0 );
		formData.right = new FormAttachment( 100, 0 );
		formData.left = new FormAttachment( listViewer.getControl(), 0, SWT.RIGHT );
		addButton.setLayoutData( formData );
		
		formData = new FormData();
		formData.top = new FormAttachment( addButton, 0, SWT.BOTTOM );
		formData.right = new FormAttachment( 100, 0 );
		formData.left = new FormAttachment( listViewer.getControl(), 0, SWT.RIGHT );
		removeButton.setLayoutData( formData );
		
		formData = new FormData();
		formData.top = new FormAttachment( removeButton, 6, SWT.BOTTOM );
		formData.right = new FormAttachment( 100, 0 );
		formData.left = new FormAttachment( listViewer.getControl(), 0, SWT.RIGHT );
		upButton.setLayoutData( formData );
		
		formData = new FormData();
		formData.top = new FormAttachment( upButton, 0, SWT.BOTTOM );
		formData.right = new FormAttachment( 100, 0 );
		formData.left = new FormAttachment( listViewer.getControl(), 0, SWT.RIGHT );
		downButton.setLayoutData( formData );
		
		// Assignes a selection listener to the managed buttons.
		MyButtonSelectionListener selectionListener = new MyButtonSelectionListener();
		
		addButton.addSelectionListener( selectionListener );
		removeButton.addSelectionListener( selectionListener );
		upButton.addSelectionListener( selectionListener );
		downButton.addSelectionListener( selectionListener );
		
		// Adds a selection change listener to the list viewer and initializes the button states.
		listViewer.addPostSelectionChangedListener( new MySelectionChangedListener() );
		updateButtons();
		
		// Post-condition
		assert listViewer != null;
	}

	public boolean fillVertically() {
		return true;
	}

	public void dispose() {
		// Pre-condition
		assert setting		!= null;
		assert listViewer	!= null;
		assert addButton		!= null;
		assert removeButton	!= null;
		assert upButton		!= null;
		assert downButton	!= null;
		
		// Detaches us from the setting observers and unreferences the setting.
		setting.removeSettingListener( this );
		setting = null;
		
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
		
		// Post-condition
		assert setting		== null;
		assert listViewer	== null;
		assert addButton		== null;
		assert removeButton	== null;
		assert upButton		== null;
		assert downButton	== null;
	}

	public void setFocus() {
		// Pre-condition
		assert listViewer != null;
		
		listViewer.getControl().setFocus();

	}

	public void setInput(Setting input) {
		// Pre-condition
		assert listViewer != null;
		assert setting == null;
		
		// References the setting and attaches us as a setting value listener.
		setting = input;
		setting.addSettingListener( this );
		refreshValueCompounds();
		updateButtons();
		
		// Post-condition
		assert setting != null;		
	}

	public boolean isDirty() {
		return false;
	}
	
	public void settingValueChanged(Setting setting) {
		refreshValueCompounds();
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
	abstract protected String editValueCompound( Shell parent, Setting setting, String compound );
	
	/**
	 * Refreshes the value compounds and the associated viewer.
	 */
	private void refreshValueCompounds() {
		// Pre-condition
		assert setting != null;
		assert listViewer != null;
		
		// Retrieves the compounds of the setting value and assignes the input to the managed viewer.
		valueCompounds = new Vector();
		setting.getSplittedValue( valueCompounds );
		listViewer.setInput( setting );
		
		// Post-condition
		assert valueCompounds != null;
	}
	
	/**
	 * Adds a new value compound.
	 */
	private void addValueCompound() {
		// Pre-condition
		assert listViewer != null;
		assert valueCompounds != null;
		
		// Edits a new value.
		String	newCompound = editValueCompound( listViewer.getControl().getShell(), setting, "new value" );
		
		// Inserts the new compound if it has been validated.
		if( newCompound != null )
		{
			valueCompounds.add( newCompound );
			commit();
			listViewer.setSelection( new StructuredSelection(new Integer(valueCompounds.size() -1)) );
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
		IStructuredSelection		selection = (IStructuredSelection) listViewer.getSelection();
		Integer					index = (Integer) selection.getFirstElement();
		
		// Retrieves the value compound to edit and the edited value.
		String	originalCompound = valueCompounds.get( index.intValue() ).toString();
		String	editedCompound = editValueCompound( listViewer.getControl().getShell(), setting, originalCompound );
		
		// Processes the edited compound.
		if( editedCompound != null ) {			
			// Updates the setting.
			valueCompounds.remove( index.intValue() );
			valueCompounds.add( index.intValue(), editedCompound );
			
			// Commit changes and erstores the selection.
			listViewer.getControl().setRedraw( false );
			commit();
			listViewer.setSelection( selection );
			listViewer.getControl().setRedraw( true );
			
			// Job's done.
			return true;
		}
		else {
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
		IStructuredSelection		selection = (IStructuredSelection) listViewer.getSelection();
		if( selection.isEmpty() == true ) {
			return;
		}
		
		// Retrieves the list of the selected items.
		List			selectedItems = selection.toList();
		Collections.sort( selectedItems );
		
		// Moves each item up.
		Iterator		i = selectedItems.iterator();
		while( i.hasNext() ) {
			Integer	currentItem = (Integer) i.next();
			int		index = currentItem.intValue();
			if( index > 0 && Collections.binarySearch(selectedItems, new Integer(index -1)) < 0 ) {
				Collections.swap( valueCompounds, index, index -1 );
				Collections.replaceAll( selectedItems, currentItem, new Integer( index -1) );
			}			
		}
		
		// Commits changes and reselected moved objects.
		listViewer.getControl().setRedraw( false );
		commit();
		listViewer.setSelection( new StructuredSelection(selectedItems) );
		listViewer.getControl().setRedraw( true );
	}

	/**
	 * Moves the value compounds corresponding to the current selection down.
	 */
	private void moveValueCompoundsDown() {
		// Pre-condition
		assert listViewer != null;
		assert valueCompounds != null;
		
		// Retrieves the current selection and skip if it is empty.
		IStructuredSelection		selection = (IStructuredSelection) listViewer.getSelection();
		if( selection.isEmpty() == true ) {
			return;
		}
		
		// Retrieves the list of the selected items.
		List			selectedItems = selection.toList();
		Collections.sort( selectedItems );
		Collections.reverse( selectedItems );
		
		// Moves each item up.
		Iterator		i = selectedItems.iterator();
		while( i.hasNext() ) {
			Integer	currentItem = (Integer) i.next();
			int		index = currentItem.intValue();
			if( index < valueCompounds.size() - 1 && Collections.binarySearch(selectedItems, new Integer(index+1)) < 0 ) {
				Collections.swap( valueCompounds, index, index + 1 );
				Collections.replaceAll( selectedItems, currentItem, new Integer( index + 1) );
			}			
		}
		
		// Commits changes and reselected moved objects.
		listViewer.getControl().setRedraw( false );
		commit();
		listViewer.setSelection( new StructuredSelection(selectedItems) );
		listViewer.getControl().setRedraw( true );
	}
	
	/**
	 * Removes the selected value compounds.
	 */
	private void removeValueCompounds() {
		// Pre-condition
		assert listViewer != null;
		assert valueCompounds != null;
		
		// Retrieves the current selection and skip if it is empty.
		IStructuredSelection		selection = (IStructuredSelection) listViewer.getSelection();
		if( selection.isEmpty() == true ) {
			return;
		}
		
		// Retrieves the selected item list.
		List		selectedItems = selection.toList();
		Collections.sort( selectedItems );
		Collections.reverse( selectedItems );
		
		// Walks through the selected items and remove the corresponding value compounds.
		Iterator		i = selectedItems.iterator();
		while( i.hasNext() ) {
			Integer	current = (Integer) i.next();
			valueCompounds.remove( current.intValue() );
		}
		
		// Commits changes.
		commit();
		
		// Updates the selection of the list viewer with the
		List	newSelectedItems = new ArrayList();
		newSelectedItems.add( selectedItems.get(selectedItems.size() - 1) );
		listViewer.setSelection( new StructuredSelection(newSelectedItems), true );
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
		
		removeButton.setEnabled( willBeEnabled );
		upButton.setEnabled( willBeEnabled );
		downButton.setEnabled( willBeEnabled );
	}

}
