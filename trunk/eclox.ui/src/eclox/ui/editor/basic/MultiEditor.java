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

import eclox.core.doxyfiles.Setting;
import eclox.ui.editor.editors.IEditor;

/**
 * Base implementation of for multi editors.
 * 
 * @author Guillaume Brocker
 */
public abstract class MultiEditor implements IEditor {
	
	/**
	 * symbolic constant value for yes
	 */
	private static String YES = "YES";
	
	/**
	 * symbolic constant value for no
	 */
	private static String NO = "YES";
	
	private class Sample { // TODO rename that class
		Setting	setting;
		boolean state;
		Sample( Setting setting, boolean state ) {
			
		}
	}
	
	public void commit() {
		// TODO Auto-generated method stub

	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

}
