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

package eclox.ui.editor.editors;

import org.eclipse.swt.widgets.Shell;

import eclox.core.doxyfiles.Setting;

/**
 * Implements a list editor that handle value compounds as directory path or file path.
 * 
 * @author gbrocker
 */
public class PathListEditor extends ListEditor {

	protected String editValueCompound(Shell parent, Setting setting, String compound) {
		return Convenience.browserForPath( parent, setting.getOwner(), compound, Convenience.BROWSE_ALL );
	}

}
