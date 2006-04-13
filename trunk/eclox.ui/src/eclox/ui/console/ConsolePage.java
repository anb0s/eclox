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

package eclox.ui.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.part.Page;

import eclox.ui.console.action.CancelJob;
import eclox.ui.console.action.ClearLog;
import eclox.ui.console.action.LockScroll;


/**
 * Implements the page for the doxygen console.
 * 
 * @author gbrocker
 */
public class ConsolePage extends Page {
	
	/**
	 * the control used to display text
	 */
	private StyledText	text;
	
	/**
	 * the console the page is attached to
	 */
	private Console console;
	
	/**
	 * the action that cancles the build job
	 */
	private CancelJob cancelJobAction;
	
	
	/**
	 * Constructor
	 * 
	 * @param	console	the console the page is attached to
	 */
	public ConsolePage( Console console )
	{
		this.console = console;
		this.cancelJobAction = new CancelJob( console );
	}

	public void dispose() {
		console = null;
		text = null;
		cancelJobAction = null;
		super.dispose();
	}

	public void createControl(Composite parent)
	{
		// Pre-condition
		assert text == null;
	
		// Creates the text control.
		text = new StyledText( parent, SWT.READ_ONLY|SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL  );
		text.setFont( JFaceResources.getTextFont() );
		
		// Creates the actions
		IActionBars	actionBars = getSite().getActionBars();
		actionBars.getToolBarManager().appendToGroup( IConsoleConstants.LAUNCH_GROUP, cancelJobAction );
		actionBars.getToolBarManager().appendToGroup( IConsoleConstants.OUTPUT_GROUP, new ClearLog(console) );
		actionBars.getToolBarManager().appendToGroup( IConsoleConstants.OUTPUT_GROUP, new LockScroll(console) );
		actionBars.updateActionBars();
	}
	
	public IAction getCancelJobAction() {
		return cancelJobAction;
	}

	public Control getControl() {
		return text;
	}

	public StyledText getStyledText() {
		return text;
	}
	
	public void setFocus() {
		// Pre-condition
		assert text != null;
		
		text.setFocus();
	}

}
