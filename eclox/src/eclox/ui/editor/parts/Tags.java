/*
	eclox
	Copyright (C) 2003 Guillaume Brocker

	This file is part of eclox.

	eclox is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	any later version.

	eclox is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with eclox; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
*/

package eclox.ui.editor.parts;

import java.util.AbstractMap;

import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseEvent;

import eclox.ui.editor.fields.Field;
import eclox.ui.editor.fields.ClassProvider;

/**
 * Implement the tag management editor part.
 * 
 * @author gbrocker
 */
public final class Tags extends Part {
	/**
	 * Implement a section listener responsible for the tags update when the section selection
	 * changes.
	 * 
	 * @author gbrocker
	 */
	private class SectionListener implements eclox.ui.editor.parts.SelectionListener {
		/**
		 * Process a selection change event.
		 * 
		 * @param	event	The event to process.
		 */
		public void selectionChanged( eclox.ui.editor.parts.SelectionEvent event ) {
			eclox.data.Section		section;
		
			// Get the selected section.
			section = (eclox.data.Section) event.selection.getSelected();
			// Update the tag table content.
			removeAllTags();
			if( section != null ) {
				java.util.Collection	children;
				java.util.Iterator		childPointer;
	
				children = section.getChildren();
				childPointer = children.iterator();
				while( childPointer.hasNext() ) {
					Object	child = childPointer.next();
		
					if( child.getClass().equals( eclox.data.Tag.class ) ) {
						addTag( (eclox.data.Tag) child );
					}
				}			
			}
			// Notify all listeners that the selection changed.
			selection.notifySelectionChanged();  
		}
	}
	
	/**
	 * Implement a table control selection listener.
	 *
	 * @author gbrocker
	 */
	public class TableSelection extends Selection implements SelectionListener {
		/**
		 * Process the table control selection change event.
		 * 
		 * @param	event	The event to process.
		 */
		public void widgetDefaultSelected( org.eclipse.swt.events.SelectionEvent event ) {
			notifySelectionChanged();
		}

		/**
		 * Process the table control selection change event.
		 * 
		 * @param	event	The event to process.
		 */
		public void widgetSelected( org.eclipse.swt.events.SelectionEvent event ) {
			notifySelectionChanged();
		}
		
		/**
		 * Retrieve the selected object in the part.
		 * 
		 * @return	The selected object.
		 */
		public Object getSelected() {
			TableItem	tableItems[];
			Object		result;
	
			tableItems = m_table.getSelection();
			if( tableItems.length == 1 ) {
				result = tableItems[0].getData();
			}
			else {
				result = null;
			}
			return result;
		}
	} 
	
	/**
	 * Implement a value listener. The value listener is responsible for the update of the
	 * table items when a value changes.
	 * 
	 * @author gbrocker
	 */
	private class ValueListener implements eclox.data.value.Listener {
		/**
		 * The association between the value to monitor and the table items.
		 */
		private AbstractMap m_tableItems = new java.util.HashMap();
		
		/**
		 * Register a new value and its associated table item.
		 */
		public void addValue( eclox.data.value.Abstract value, TableItem tableItem ) {
			m_tableItems.put( value, tableItem );
			value.addListener( this );
		}
		
		/**
		 * Remove all registered values.
		 */
		public void removeAllValues() {
			// Unregister from all values.
			java.util.Set		keys = m_tableItems.keySet();
			java.util.Iterator	keyPointer = keys.iterator();
			
			while( keyPointer.hasNext() ) {
				eclox.data.value.Abstract	value = (eclox.data.value.Abstract) keyPointer.next();
				
				value.removeListener( this ); 
			}
			
			// Clear the values and associated table items.  
			m_tableItems.clear();
		}
		
		/**
		 * Process a value change event.
		 * 
		 * @param	event	The value event to process.
		 */
		public void valueChanged( eclox.data.value.Event event ) {
			TableItem	tableItem;
			
			// Get the table item corresponding to the channged value.
			tableItem = (TableItem) m_tableItems.get( event.m_value );
			// Update the table item.
			if( tableItem != null ) {
				tableItem.setText( Tags.VALUE_COL_INDEX, event.m_value.toString() );
			}
		}
	}
	
	/**
	 * This class is responsible for the edition of tag values.
	 * 
	 * @author gbrocker
	 */
	private class FieldManager implements MouseListener, FocusListener {
		/**
		 * The current table editor that manage the current edition field.
		 */
		private TableEditor m_tableEditor;
		
		/**
		 * The current field.
		 */
		private Field m_field;
		
		/**
		 * Process a focus gained event on the current field control.
		 * 
		 * @param	event	The event to process.
		 */
		public void focusGained( FocusEvent event ) {			
		}
		
		/**
		 * Process a focus gained event on the current field control.
		 * 
		 * @param	event	The event to process.
		 */
		public void focusLost( FocusEvent event ) {
			removeCurrentField();			
		}
		
		/**
		 * Process a mouse double click on the table.
		 * 
		 * @param	event	The event to process.
		 */
		public void mouseDoubleClick( MouseEvent event ) {
			setCurrentField();	
		}	
		
		/**
		 * Process a mouse down on the table.
		 * 
		 * @param	event	The event to process.
		 */
		public void mouseDown( MouseEvent event ) {
		}

		/**
		 * Process a mouse up on the table.
		 * 
		 * @param	event	The event to process.
		 */
		public void mouseUp( MouseEvent event ) {
		}
		
		/**
		 * Retrieve the field instance for the specified tag.
		 * 
		 * @param tag	The tag for which a field must be created.
		 * 
		 * @return	The field instance. Note theat the field hasn't been attached to the tag yet.
		 */
		private Field createFieldInstance( eclox.data.Tag tag ) {
			Field	result;
			
			try {
				Class	fieldClass;
				
				
				fieldClass = m_fieldClassProvider.getFieldClass( tag.getValue().getClass() );
				result = (Field) fieldClass.newInstance();
			}
			catch( Exception exp ) {
				result = null;
			}
			return result;
		} 
		
		/**
		 * Remove the current field.
		 */
		public void removeCurrentField() {
			if( m_field != null ) {
				m_tableEditor.getEditor().dispose();
				m_tableEditor.dispose();
				m_field.detach();
				m_tableEditor = null;
				m_field = null;				
			}						
		}
		
		/**
		 * Set the current field for the current selected table item.
		 */
		private void setCurrentField() {
			TableItem	selectedItems[] = m_table.getSelection();
			
			if( selectedItems.length == 1 ) {
				eclox.data.Tag	tag;
				Control			fieldControl;
				
				tag = (eclox.data.Tag) selectedItems[0].getData();
				// Create the field.
				m_field = createFieldInstance( tag );
				m_field.attachTo( tag );
				fieldControl = m_field.createControl(m_table);
				fieldControl.setFocus();
				fieldControl.addFocusListener( this );
				// Setup the table editor.
				m_tableEditor = new TableEditor( m_table );
				m_tableEditor.grabHorizontal = true;
				m_tableEditor.grabVertical = true;
				m_tableEditor.setEditor(
					fieldControl,
					selectedItems[0],
					VALUE_COL_INDEX);
			}
		}
	}
	
	/**
	 * Define the name column index.
	 */
	private static int NAME_COL_INDEX = 0;
	
	/**
	 * Define the value column index.
	 */
	private static int VALUE_COL_INDEX = 1;
	
	/**
	 * Define the name column text.
	 */
	private static java.lang.String NAME_COL_TEXT = "Name";
	
	/**
	 * Define the value column text.
	 */
	private static java.lang.String VALUE_COL_TEXT = "Value";
	
	/**
	 * The instance of field class provider.
	 */
	private static ClassProvider m_fieldClassProvider = new ClassProvider();

	
	/**
	 * The owned table control displaying tags.
	 */
	private org.eclipse.swt.widgets.Table m_table;
	
	/**
	 * The object responsible for the value change management.
	 */
	private ValueListener m_valueListener = new ValueListener();
	
	/**
	 * The object responsible for the edition field management.
	 */
	private FieldManager m_fieldManager = new FieldManager();
	
	/**
	 * The table selection manager.
	 */
	public TableSelection selection = new TableSelection();
	
	/**
	 * Constructor.
	 * 
	 * @param sections	The section manager to attach to.
	 */
	public Tags( Sections sections ) {
		sections.selection.addSelectionListener( new SectionListener() );
	}
	
	/**
	 * Create the editor part control.
	 * 
	 * @param	parent	The parent composite control for the part control.
	 * 
	 * @return	The created part control. 
	 */
	protected void createContent() {
		m_table = createTable( getContentParent() );
		m_table.addSelectionListener( selection );
		m_table.addMouseListener( m_fieldManager );
		setContent( m_table );
		setTitle( "Tags" );
	}
	
	/**
	 * Add a new tag.
	 *  
	 * @param tag	The tag to add.
	 */
	private void addTag( eclox.data.Tag tag ) {
		org.eclipse.swt.widgets.TableItem	item;

		item = new TableItem( m_table, 0 );
		item.setText( NAME_COL_INDEX, tag.getName() );
		item.setText( VALUE_COL_INDEX, tag.getValue().toString() );
		item.setData( tag );
		m_valueListener.addValue( tag.getValue(), item );
	}
	
	/**
	 * Remove all table tags.
	 */
	private void removeAllTags() {
		m_table.deselectAll();
		m_table.removeAll();
		m_fieldManager.removeCurrentField();
		m_valueListener.removeAllValues();
	}
	
	/**
	 * Create the table control.
	 * 
	 * @param parent	The parent composite control for the table to create.
	 */
	static private org.eclipse.swt.widgets.Table createTable( org.eclipse.swt.widgets.Composite parent ) {
		org.eclipse.swt.widgets.Table		table;
		org.eclipse.swt.layout.GridData		layoutData;
		org.eclipse.swt.widgets.TableColumn	curTableColumn;
	
		// Creation of the table
		table = new org.eclipse.swt.widgets.Table(
			parent,
			org.eclipse.swt.SWT.FULL_SELECTION );
		layoutData = new org.eclipse.swt.layout.GridData();
		layoutData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true; 
		layoutData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		layoutData.grabExcessVerticalSpace = true;
		table.setLayoutData( layoutData );
		table.setHeaderVisible( true );
		table.setLinesVisible( true );
		// Creation of the table columns.
		curTableColumn = new org.eclipse.swt.widgets.TableColumn( table, 0, NAME_COL_INDEX );
		curTableColumn.setText( NAME_COL_TEXT );
		curTableColumn.setWidth(150);
		curTableColumn = new org.eclipse.swt.widgets.TableColumn( table, 0, VALUE_COL_INDEX );
		curTableColumn.setText( VALUE_COL_TEXT );
		curTableColumn.setWidth(250);
		
		return table;
	}
}