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

package eclox.ui;

/**
 * Provides all preference names of the plugin.
 *  
 * @author gbrocker
 */
public interface IPreferences {
	
	/**
	 * Constant name for the build history content.
	 */
//	public static final String BUILD_HISTORY_CONTENT = "build-history-content";
	
	/**
	 * Constant name for the build history size.
	 */
//	public static final String BUILD_HISTORY_SIZE = "build-history-size";
	
	/**
	 * Constant name for the build job history size.
	 */
	public static final String BUILD_HISTORY_SIZE = "buildHistory.size";

	/**
	 * Constant name for workspace autosave flag.
	 */
	public static final String AUTO_SAVE = "editor.autosave";
	
	
	/**
	 * Constant for AUTO_SAVE never state.
	 */
	public static final String AUTO_SAVE_NEVER = "never";
	
	/**
	 * Constant for AUTO_SAVE none always.
	 */
	public static final String AUTO_SAVE_ALWAYS = "always";
	
	/**
	 * Constant for AUTO_SAVE none ask.
	 */
	public static final String AUTO_SAVE_ASK = "ask";
	
	/**
	 * Constant name for the automatic value string escapes.
	 */
	public static final String HANDLE_ESCAPED_VALUES = "editor.handleEscapedValues";
}
