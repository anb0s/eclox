/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2007 Guillaume Brocker
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

package eclox.ui.console.action;

import org.eclipse.jface.action.Action;

import eclox.ui.Images;
import eclox.ui.Plugin;
import eclox.ui.console.ConsolePage;

/**
 * Implements the action that will lock the console from scrolling while output
 * is appended to the log.
 * 
 * @author gbrocker
 */
public class LockScroll extends Action {
	
	/**
	 * the console page to act on
	 */
	private ConsolePage consolePage;

	/**
	 * Constructor
	 * 
	 * @param console	the console to act on
	 */
	public LockScroll( ConsolePage consolePage ) {
		super("Scroll Lock", AS_CHECK_BOX);
		this.consolePage = consolePage;
		setImageDescriptor( Plugin.getImageDescriptor(Images.LOCK_CONSOLE) );
		setToolTipText( "Scroll Lock" );
	}
	
	public void run() {
		consolePage.setScrollLocked( isChecked() );
		super.run();
	}
}
