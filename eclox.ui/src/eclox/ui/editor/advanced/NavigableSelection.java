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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Implements a selection that provides the history of all selected
 * items as well as the navigation among those items.
 * 
 * @author gbrocker
 */
public class NavigableSelection implements IStructuredSelection {
	
	private List	history;	///< contains all selected objects
	private int		selection;	///< the index of the object being selected 

	/**
	 * @brief	Default constructor that builds an empty selection
	 */
	public NavigableSelection()	{
		history = new Vector();
		selection = -1;
	}
	
	/**
	 * @brief	Builds a selection with one item.
	 * 
	 * @param	object	the object being selected
	 */
	public NavigableSelection(Object object) {
		history = new Vector();
		history.add(object);
		selection = history.size() - 1;
	}
	
	/**
	 * Builds a selection with the given object and	selection history
	 * 
	 * @param	selection	a selection history
	 * @param	object		an object to select
	 */
	public NavigableSelection(NavigableSelection selection, Object object ) {
		history = new Vector();
		
		// Collects some data from the reference selection
		if(selection.isEmpty() == false) {
			history.addAll(selection.getPreviousElements());
			history.add(selection.getFirstElement());
		}
		
		// Adds the given object to the selection.
		history.add(object);
		this.selection = history.size() - 1;
	}
	
	/**
	 * Builds a selection with the given history and index.
	 * 
	 * @param	history		the collection of items in the selection history
	 * @param	selection	the index of the object being selected
	 */
	private NavigableSelection(Collection history, int selection) {
		this.history = new Vector(history);
		this.selection = selection;
	}
	
	/**
	 * @brief	Retrieves the collection of items that follow the current item
	 * 			in the history.
	 * 
	 * @return	a collection of items
	 */
	public Collection getNextElements() {
		Collection	result = new Vector();
		if( selection != -1 && selection < history.size() - 1 ) {
			result = new Vector( history.subList(selection+1, history.size()) ); 
		}
		return result;
	}
	
	/**
	 * @brief	Builds the selection that follows in the history.
	 * 
	 * @return	a selection or null if none
	 */
	public NavigableSelection getNextSelection() {
		NavigableSelection result = null;
		if( selection != -1 && selection < history.size() - 1 ) {
			result = new NavigableSelection(history, selection + 1);
		}
		return result;
	}
	
	/**
	 * @brief	Retrieves the collection of items that preceed the current item
	 * 			in the history.
	 * 
	 * @return	a collection of items
	 */
	public Collection getPreviousElements() {
		Vector result;
		if( selection != -1 ) {
			result = new Vector( history.subList(0, selection) );
			Collections.reverse(result);			
		}
		else {
			result = new Vector();
		}
		return result;
	}
	
	/**
	 * @brief	Builds the selection that preceeds in the history.
	 * 
	 * @return	a selection or null if none
	 */
	public NavigableSelection getPreviousSelection() {
		NavigableSelection result = null;
		if( selection != -1 && selection > 0 ) {
			return new NavigableSelection(history, selection - 1);
		}
		return result;
	}
	
	/**
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty() {
		return history.isEmpty();
	}
	
	/**
	 * Retrieves the selected element.
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredSelection#getFirstElement()
	 */
	public Object getFirstElement() {
		return (selection != -1) ? history.get(selection) : null;
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#iterator()
	 */
	public Iterator iterator() {
		return history.iterator();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#size()
	 */
	public int size() {
		return history.size();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toArray()
	 */
	public Object[] toArray() {
		return history.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toList()
	 */
	public List toList() {
		return new Vector(history);
	}

}
