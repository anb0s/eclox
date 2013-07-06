/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
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
	 * 
	 * @see	AUTO_SAVE_NEVER
	 * @see AUTO_SAVE_ALWAYS
	 * @see AUTO_SAVE_ASK
	 */
	public static final String AUTO_SAVE = "editor.autosave";
	
	
	/**
	 * Constant for AUTO_SAVE never state.
	 * 
	 * @see	AUTO_SAVE
	 * @see AUTO_SAVE_ALWAYS
	 * @see AUTO_SAVE_ASK
	 */
	public static final String AUTO_SAVE_NEVER = "never";
	
	/**
	 * Constant for AUTO_SAVE always state.
	 * 
	 * @see	AUTO_SAVE
	 * @see AUTO_SAVE_NEVER
	 * @see AUTO_SAVE_ASK
	 */
	public static final String AUTO_SAVE_ALWAYS = "always";
	
	/**
	 * Constant for AUTO_SAVE ask state.
	 * 
	 * @see	AUTO_SAVE
	 * @see AUTO_SAVE_ALWAYS
	 * @see AUTO_SAVE_NEVER
	 */
	public static final String AUTO_SAVE_ASK = "ask";
	
	/**
	 * Constant name for the automatic value string escapes.
	 */
	public static final String HANDLE_ESCAPED_VALUES = "editor.handleEscapedValues";
}
