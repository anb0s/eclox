/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2019, Andre Bossert
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

package eclox.core.doxygen;

/**
 * Implements the doxygen wrapper that use the eventual doxygen program reachable
 * in the default path.
 *
 * @author gbrocker
 */
public class DefaultDoxygen extends Doxygen {

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return new String();
    }

    @Override
    public String getIdentifier() {
        return getClass().getName();
    }

    @Override
    public void setLocation(String location) {
    }

}
