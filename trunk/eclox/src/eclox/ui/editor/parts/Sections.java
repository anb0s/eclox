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

import org.eclipse.swt.events.SelectionListener;

/**
 * Implement the section management editor part.
 * 
 * @author gbrocker
 */
public class Sections extends Part {
	/**
	 * Implement a selection listener for the list control.
	 * 
	 * @author gbrocker
	 */
	public class ListSelection extends Selection implements SelectionListener {
		/**
		 * Process default selection change notification comming from the owned list control.
		 * 
		 * @param	event	The selection event to proceed.
		 */
		public void widgetDefaultSelected( org.eclipse.swt.events.SelectionEvent event ) {
			// Notify all our listeners that the selection changed.
			notifySelectionChanged();
		}

		/**
		 * Process selection change notification comming from the owned list control.
		 * 
		 * @param	event	The selection event to proceed.
		 */
		public void widgetSelected( org.eclipse.swt.events.SelectionEvent event ) {
			// Notify all our listeners that the selection changed.
			notifySelectionChanged();
		}
		
		/**
		 * Retrieve the current selected section.
		 * 
		 * @return	The current selection section or null if none.
		 */
		public Object getSelected() {
			Object	result = null;
			int		selectionIndex;
	
			selectionIndex = m_list.getSelectionIndex(); 
			if( selectionIndex != -1 ) {
				result = m_sections.get( selectionIndex );
			}		
			return result;
		}
	}
	
	/**
	 * The list control containing the section names.
	 */
	private org.eclipse.swt.widgets.List m_list;
	
	/**
	 * The array of all owned sections.
	 */
	private java.util.List m_sections = new java.util.Vector();
	
	/**
	 * The list selection manager.
	 */
	public ListSelection selection = new ListSelection();
	
	/**
	 * Create the section management part control.
	 * 
	 * @author gbrocker
	 */
	public void createContent() {
		m_list = createList( getContentParent() );
		m_list.addSelectionListener( selection );
		setContent( m_list );
		setTitle( "Sections" );
	}
	
	/**
	 * Populate the part with all sections contained in the specufued data root.
	 * 
	 * @param root	The node containing all sections.
	 */
	public void populate( eclox.doxyfile.node.Doxyfile root ) {
		java.util.Iterator	itemPointer = root.getChildren().iterator();
		
		while( itemPointer.hasNext() ) {
			eclox.doxyfile.node.Node	item = (eclox.doxyfile.node.Node) itemPointer.next();
			
			if( item.getClass().equals( eclox.doxyfile.node.Section.class ) ) {
				eclox.doxyfile.node.Section	section = (eclox.doxyfile.node.Section) item;
				
				m_sections.add( section );
				m_list.add( section.getName() );
			}
		}
	}

	/**
	 * Create the list control.
	 * 
	 * @param parent	The parent composite control for the list to create.
	 * 
	 * @return	The created list control.
	 */
	static private org.eclipse.swt.widgets.List createList( org.eclipse.swt.widgets.Composite parent ) {
		org.eclipse.swt.widgets.List	list;
		org.eclipse.swt.layout.GridData	layoutData;
		
		list = new org.eclipse.swt.widgets.List(
			parent,
			org.eclipse.swt.SWT.H_SCROLL | org.eclipse.swt.SWT.V_SCROLL );
		layoutData = new org.eclipse.swt.layout.GridData();
		layoutData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true; 
		layoutData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		layoutData.grabExcessVerticalSpace = true;
		list.setLayoutData( layoutData );
		return list;
	}
}
