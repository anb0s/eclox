// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2006 Guillaume Brocker
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

package eclox.ui.editor.advanced.editors;

import org.eclipse.swt.widgets.Shell;

import eclox.core.doxyfiles.Setting;

/**
 * Implements a list editor that handle value compounds as direcoty paths.
 * 
 * @author gbrocker
 */
public class DirectoryListEditor extends ListEditor {


	protected String editValueCompound( Shell parent, Setting setting, String compound ) {
		return Convenience.browserForPath( parent, setting.getOwner(), compound, Convenience.BROWSE_FILESYSTEM_DIRECTORY );
	}

}
