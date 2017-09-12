/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2016, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - added support of eclipse variables to resolve doxygen path
 *
 ******************************************************************************/

package eclox.core.doxygen;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * Implements a custom doxygen interpreter that will wrap a locally installed
 * version of doxygen.
 *
 * @author Guillaume Brocker
 */
public final class CustomDoxygen extends Doxygen {

    /**
     * a string containing the location where doxygen should available
     *
     * This can be either a path to a directory or the full path to the
     * doxygen binary executable file.
     */
    private String location;

    /**
     * Builds a local doxygen wrapper.
     */
    public CustomDoxygen() {
    }

    /**
     * Builds a local doxygen wrapper that uses the given path to search for doxygen.
     */
    public CustomDoxygen(String location) {
        setLocation(location);
    }

    @Override
    public String getCommand() {
        // anb0s: support of variables
        IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
        String resolved_location = null;
        try {
            resolved_location = variableManager.performStringSubstitution(this.location);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        // Retrieves the real location where doxygen may be.
        File realLocation = new File(resolved_location);
        // Builds the path.
        if (realLocation.isFile() == true) {
            return realLocation.getPath();
        } else {
            return realLocation.getPath() + File.separator + COMMAND_NAME;
        }
    }

    @Override
    public String getDescription() {
        return "Using location: '" + getLocation() + "'";
    }

    @Override
    public String getIdentifier() {
        return this.getClass().getName() + " " + location;
    }

    @Override
    public void setLocation(String location) {
        assert (location != null);
        assert (location.length() != 0);
        this.location = new String(location);
    }

    /**
     * Retrieves the location of the custom doxygen.
     */
    public String getLocation() {
        return new String(this.location);
    }

}
