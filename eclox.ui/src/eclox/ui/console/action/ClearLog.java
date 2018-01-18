/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2018, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - images handling
 *
 ******************************************************************************/

package eclox.ui.console.action;

import org.eclipse.jface.action.Action;

import eclox.core.doxygen.BuildJob;
import eclox.ui.Images;
import eclox.ui.Plugin;
import eclox.ui.console.Console;

public class ClearLog extends Action {

    /**
     * the console to act on
     */
    private Console console;

    /**
     * Constructor
     *
     * @param	console	the console to act on
     */
    public ClearLog(Console console) {
        super("Clear Console", Plugin.getImageDescriptor(Images.CLEAR_CONSOLE.getId()));
        this.console = console;
        setToolTipText("Clear Build Log");
    }

    public void run() {
        BuildJob job = console.getJob();
        if (job != null) {
            job.clearLog();
        }

        super.run();
    }

}
