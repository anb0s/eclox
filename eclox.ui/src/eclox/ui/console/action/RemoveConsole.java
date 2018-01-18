/*******************************************************************************
 * Copyright (C) 2007, 2013, Guillaume Brocker
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
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

import eclox.ui.Images;
import eclox.ui.Plugin;
import eclox.ui.console.Console;

/**
 * Implements an action that removes a given console.
 *
 * @author gbrocker
 */
public class RemoveConsole extends Action {

    /**
     * the console the action is attached to
     */
    Console console;

    /**
     * Constructor
     *
     * @param	console	the build console
     */
    public RemoveConsole(Console console) {
        super("Remove", Plugin.getImageDescriptor(Images.REMOVE.getId()));
        this.console = console;
        setToolTipText("Remove Doxygen Build Console");
    }

    public void run() {
        ConsolePlugin.getDefault().getConsoleManager().removeConsoles(new IConsole[] { console });
        super.run();
    }
}
