/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2004 Guillaume Brocker
	
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

package eclox.preferences;

/**
 * Provides all preference names of the plugin.
 *  
 * @author gbrocker
 */
public final class Names {
	/**
	 * Constant name for the doxygen binary's path.
	 */
	public static final String COMPILER_PATH = "compiler-path";
	
	/**
	 * Constant name for the build history content.
	 */
	public static final String BUILD_HISTORY_CONTENT = "build-history-content";
	
	/**
	 * Constant name for the build history size.
	 */
	public static final String BUILD_HISTORY_SIZE = "build-history-size";
	
	/**
	 * Constant name for workspace autosave flag.
	 */
	public static final String AUTO_SAVE = "auto-save";
}
