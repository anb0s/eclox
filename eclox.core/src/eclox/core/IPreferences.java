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

package eclox.core;

/**
 * Provides all preference names of the plugin.
 *
 * @author gbrocker
 */
public interface IPreferences {

    /**
     * Constant definition for the default doxygen wrapper to use.
     */
    public static final String DEFAULT_DOXYGEN = "doxygen.default";

    /**
     * Constant definition for the user defined doxygen wrappers.
     */
    public static final String CUSTOM_DOXYGENS = "doxygen.customs";

}
