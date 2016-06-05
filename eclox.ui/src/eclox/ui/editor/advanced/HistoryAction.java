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

import java.util.Stack;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import eclox.core.doxyfiles.Setting;

/**
 * Implements an action that trigger the navigation in the selection history.
 * This class is used to contribute to the form's action bar manager.
 *
 * @author Guillaume Brocker
 */
class HistoryAction extends Action {

	/**
	 * Implements a menu listener used to trigger selection changes
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
			assert e.widget instanceof MenuItem;

			MenuItem			menuItem = (MenuItem) e.widget;
			NavigableSelection	selection = (NavigableSelection) menuItem.getData();

			masterPart.setSelection(selection, true);
		}

	}

    /**
     * Implements the menu creator tha will create the menu for the action.
     */
	private class MyMenuCreator implements IMenuCreator {

		private Menu menu;

		/**
		 * @see org.eclipse.jface.action.IMenuCreator#dispose()
		 */
		public void dispose() {
			if( menu != null ) {
				menu.dispose();
			}
		}

		/**
		 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
		 */
		public Menu getMenu(Control parent) {
			if( menu == null ) {
				menu = new Menu(parent);
			}
			fill(menu);
			return menu;
		}

		/**
		 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
		 */
		public Menu getMenu(Menu parent) {
			if( menu == null ) {
				menu = new Menu(parent);
			}
			fill(menu);
			return menu;
		}

		/**
		 * Fills the menu with navigation history.
		 *
		 * @param menu	the menu to full
		 */
		private void fill(Menu menu) {
			// Clears the menu content.
			MenuItem [] items = menu.getItems();
			for( int i = 0; i != items.length; ++i ) {
				items[i].dispose();
			}

			// Fills the menu content.
			NavigableSelection	currentSelection = getFollowingSelection(selection);
			while(currentSelection != null) {
				Setting		setting = (Setting) currentSelection.getFirstElement();
				MenuItem	menuItem = new MenuItem(menu, 0);
				menuItem.setText( setting.hasProperty(Setting.TEXT) ? setting.getProperty(Setting.TEXT) : setting.getIdentifier() );
				menuItem.setData(currentSelection);
				menuItem.addSelectionListener(new MySelectionListener());

				currentSelection = getFollowingSelection(currentSelection);
			}
		}

		/**
		 * Retrieves the selection following the given one, according
		 * to the current action's direction.
		 *
		 * @param	selection	a reference selection
		 *
		 * @return	the following selection or null if none
		 */
		private NavigableSelection getFollowingSelection( NavigableSelection selection ) {
			return (direction == BACK) ? selection.getPreviousSelection() : selection.getNextSelection();
		}

    }


	/** defines the back navigation direction */
	public static final int BACK = 0;

	/** defines the forward navigation direction */
	public static final int FORWARD = 1;

	/** the direction of the navigation assigned to the action */
	private int direction;

	/** the master part that holds the nivation history */
	private MasterPart masterPart;

	/** the current selection */
	private NavigableSelection selection;

	/**
	 * Constructor
	 *
	 * @param direction		the navigation direction for the action
	 * @param masterPart	the master part that manage the action
	 */
	public HistoryAction(int direction, MasterPart masterPart) {
		super(new String(), IAction.AS_DROP_DOWN_MENU);

		assert masterPart != null;

		this.direction = direction;
		this.masterPart = masterPart;
		setMenuCreator(new MyMenuCreator());
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		switch(direction) {
		case BACK:
			masterPart.setSelection( selection.getPreviousSelection(), true );
			break;

		case FORWARD:
			masterPart.setSelection( selection.getNextSelection(), true );
			break;
		}
	}

	/**
	 * Tells the action that the selection changed
	 *
	 * @param	newSelection	the new selection
	 */
	public void selectionChanged(ISelection newSelection) {
		assert newSelection instanceof NavigableSelection;

		selection = (NavigableSelection) newSelection;
		Stack<Object> sideElements = direction == BACK ? selection.getPreviousElements() : selection.getNextElements();

		if( sideElements.isEmpty() ) {
			setEnabled(false);
			setText(new String());
		}
		else {
			Setting	setting	= (Setting) sideElements.peek();

			setEnabled(true);
			setText("Go to " + setting.getProperty(Setting.TEXT));
		}
	}

}
