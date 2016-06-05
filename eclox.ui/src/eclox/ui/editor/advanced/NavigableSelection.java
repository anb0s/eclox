/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/

package eclox.ui.editor.advanced;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Implements a selection that provides the history of all selected
 * items as well as the navigation among those items.
 *
 * @author gbrocker
 */
public class NavigableSelection implements IStructuredSelection {

	private Stack<Object>	previousElements = new Stack<Object>();	///< Contains all previously selected elements.
	private Stack<Object>	nextElements = new Stack<Object>();		///< Contains all next selected elements.
	private Vector<Object>	elements = new Vector<Object>();		///< References the current element of the selection.

	/**
	 * @brief	Builds a new selection that holds the given object.
	 *
	 * @param	object	the new object to select
	 *
	 * @return	the new selection
	 */
	public NavigableSelection select(Object object) {
		NavigableSelection	result;
		if( getFirstElement() != object ) {
			result = new NavigableSelection();
			if( isEmpty() == false ) {
				result.previousElements.addAll(previousElements);
				result.previousElements.push(elements.firstElement());
			}
			result.elements.add(object);
		}
		else {
			result = this;
		}
		return result;
	}

	/**
	 * @brief	Retrieves the collection of items that follow the current item
	 * 			in the history.
	 *
	 * @return	a stack of items
	 */
	public Stack<Object> getNextElements() {
		return nextElements;
	}

	/**
	 * @brief	Builds the selection that follows in the history.
	 *
	 * @return	a selection or null if none
	 */
	public NavigableSelection getNextSelection() {
		NavigableSelection result = null;
		if( nextElements.empty() == false ) {
			result = new NavigableSelection();
			result.previousElements.addAll(previousElements);
			result.previousElements.push(elements.firstElement());
			result.elements.add(nextElements.peek());
			result.nextElements.addAll(nextElements);
			result.nextElements.pop();
		}
		return result;
	}

	/**
	 * @brief	Retrieves the collection of items that preceed the current item
	 * 			in the history.
	 *
	 * @return	a stack of items
	 */
	public Stack<Object> getPreviousElements() {
		return previousElements;
	}

	/**
	 * @brief	Builds the selection that preceeds in the history.
	 *
	 * @return	a selection or null if none
	 */
	public NavigableSelection getPreviousSelection() {
		NavigableSelection result = null;
		if( previousElements.empty() == false ) {
			result = new NavigableSelection();
			result.previousElements.addAll(previousElements);
			result.previousElements.pop();
			result.elements.add(previousElements.peek());
			result.nextElements.addAll(nextElements);
			result.nextElements.push(elements.firstElement());
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 * Retrieves the selected element.
	 *
	 * @see org.eclipse.jface.viewers.IStructuredSelection#getFirstElement()
	 */
	public Object getFirstElement() {
		return elements.isEmpty() ? null : elements.firstElement();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#iterator()
	 */
	public Iterator<Object> iterator() {
		return elements.iterator();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#size()
	 */
	public int size() {
		return elements.size();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toArray()
	 */
	public Object[] toArray() {
		return elements.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toList()
	 */
	public List<Object> toList() {
		return new Vector<Object>(elements);
	}

}
