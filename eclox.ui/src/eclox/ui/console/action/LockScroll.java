/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
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
     * @param consolePage	the console to act on
     */
    public LockScroll(ConsolePage consolePage) {
        super("Scroll Lock", AS_CHECK_BOX);
        this.consolePage = consolePage;
        setImageDescriptor(Plugin.getImageDescriptor(Images.LOCK_CONSOLE.getId()));
        setToolTipText("Scroll Lock");
    }

    public void run() {
        consolePage.setScrollLocked(isChecked());
        super.run();
    }
}
