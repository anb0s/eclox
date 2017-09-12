/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
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

import eclox.ui.Images;
import eclox.ui.Plugin;
import eclox.ui.console.Console;

/**
 * Implements an action that will cancel the current build job of the console.
 *
 * @author gbrocker
 */
public class CancelJob extends Action {

    /**
     * the console the action is attached to
     */
    Console console;

    /**
     * Constructor
     *
     * @param	console	the build console
     */
    public CancelJob(Console console) {
        super("Terminate", Plugin.getImageDescriptor(Images.TERMINATE.getId()));
        this.console = console;
        setToolTipText("Terminate Build");
        setEnabled(false);
    }

    public void run() {
        console.getJob().cancel();
        super.run();
    }
}
