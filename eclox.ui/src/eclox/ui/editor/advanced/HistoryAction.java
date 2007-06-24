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
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.forms.IManagedForm;

import eclox.core.doxyfiles.Setting;

/**
 * Implements an action that trigger the navigation in the selection history.
 * This class is used to contribute to the form's action bar manager.
 * 
 * @author Guillaume Brocker
 */
class HistoryAction extends Action {
	
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
	 * the menu creator
	 */
//	private MyHistoryActionMenuCreator menuCreator;
	
	/**
	 * Constructor
	 * 
	 * @param direction		the navigation direction for the action
	 * @param masterPart	the master part that manage the action
	 */
	public HistoryAction(int direction, MasterPart masterPart) {
		// Pre-condition
		assert masterPart != null;
		
//		super(new String(), IAction.AS_DROP_DOWN_MENU);
		this.direction = direction;
		this.masterPart = masterPart;
//		this.menuCreator = new MyHistoryActionMenuCreator(history);
		
//		setMenuCreator(menuCreator);
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
	 * @see org.eclipse.jface.action.Action#runWithEvent(org.eclipse.swt.widgets.Event)
	 */
//	public void runWithEvent(Event event) {
//	}
	
	/**
	 * Tells the action that the selection changed
	 * 
	 * @param	newSelection	the new selection
	 */
	public void selectionChanged(ISelection newSelection) {
		assert newSelection instanceof NavigableSelection;
		
		selection = (NavigableSelection) newSelection;
		Collection sideElements = direction == BACK ? selection.getPreviousElements() : selection.getNextElements();
		
		if( sideElements.isEmpty() ) {
			setEnabled(false);
			setText(new String());
		}
		else {
			Setting	setting	= (Setting) sideElements.iterator().next();
			
			setEnabled(true);
			setText("Go to " + setting.getProperty(Setting.TEXT));
		}
	}

}
