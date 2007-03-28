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

package eclox.ui.editor.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Implements a multi editor that presents choices in a combo box.
 * 
 * @author Guillaume Brocker
 */
public class ComboMultiEditor extends MultiEditor {
	
	/**
	 * Implements a selection listener that will handle selection changes in the combo control.
	 */
	private class MySelectionListener implements SelectionListener {

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		/**
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			selectState( combo.getItem(combo.getSelectionIndex()) );
			commit();
		}
		
	}
	/**
	 * the combo control that is the representation of the editor
	 */
	Combo combo;
	
	/**
	 * Constructor
	 * 
	 * @param	states	an array of string representing all posible states
	 */
	ComboMultiEditor( String [] states ) {
		super( states );
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#createContent(org.eclipse.swt.widgets.Composite, org.eclipse.ui.forms.widgets.FormToolkit)
	 */
	public void createContent(Composite parent, FormToolkit formToolkit) {
		// Pre-condition
		assert combo != null;
		
		// Creates the combo control.
		combo = new Combo(parent, SWT.DROP_DOWN|SWT.READ_ONLY);
		combo.addSelectionListener( new MySelectionListener() );
		formToolkit.adapt(combo, true, true);
		
		// Fills the combo with the state names.
		for( int i = 0; i != states.length; ++i ) {
			combo.add( states[i].getName() );
		}
		combo.setVisibleItemCount(states.length);
		
		// Installs a layout in the parent composite
		parent.setLayout(new FillLayout());
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#dispose()
	 */
	public void dispose() {
		combo = null;
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#fillVertically()
	 */
	public boolean fillVertically() {
		return false;
	}
	
	/**
	 * @see eclox.ui.editor.basic.MultiEditor#refresh()
	 */
	public void refresh() {
		// Pre-condition
		assert combo != null;
		
		super.refresh();
		
		// Selectes the string corresponding to the current selection
		State	selection = getSelection();
		if( selection != null ) {
			combo.select( combo.indexOf(selection.getName()) );
		}
		else {
			combo.deselectAll();
		}
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		// Pre-condition
		assert combo != null;
		
		combo.setEnabled(enabled);
	}

	/**
	 * @see eclox.ui.editor.editors.IEditor#setFocus()
	 */
	public void setFocus() {
		// Pre-condition
		assert combo != null;
		
		combo.setFocus();
	}

}
