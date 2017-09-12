/*******************************************************************************
 * Copyright (C) 2003-2006, 2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - Improvement static declaration of plugin relative identifier
 *                   - fixed java.lang.IllegalArgumentException: endRule without matching beginRule
 *                     https://github.com/anb0s/eclox/issues/175
 *
 ******************************************************************************/

package eclox.core.doxygen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import eclox.core.Plugin;

/**
 * Defines some constant used with markers resulting from a build.
 *
 * @author gbrocker
 */
public class Marker {

    private static final String SEVERITY_WARNING = "warning"; ///< Defines the warning severity string.
    private static final String SEVERITY_ERROR = "error"; ///< Defines the warning severity string.

    public static final String ECLOX_DOXYGEN_NAME = Plugin.getDefault().getBundle().getSymbolicName();
    public static final String DOXYGEN_MARKER = ECLOX_DOXYGEN_NAME + ".doxygen.marker"; ///< Defines the doxygen marker type attribute name
    public static final String SETTING = DOXYGEN_MARKER + ".setting"; ///< Defines the optional attribute name that hold the name of a setting

    /**
     * Creates a single marker for the given file.
     *
     * If @c file is null, no marker will be created.
     *
     * @param	file		a resource file to create a marker for
     * @param	line		a line number
     * @param	message		a message explaining the problem
     * @param	severity	a severity level
     */
    public static IMarker create(IFile file, int line, String message, int severity) throws CoreException {
        IMarker marker = null;
        if (file != null && file.exists()) {
            marker = file.createMarker(Marker.DOXYGEN_MARKER);
            marker.setAttribute(IMarker.MESSAGE, message);
            marker.setAttribute(IMarker.LINE_NUMBER, line);
            marker.setAttribute(IMarker.LOCATION, file.getProjectRelativePath().toPortableString());
            marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
            marker.setAttribute(IMarker.SEVERITY, severity);
        }
        return marker;
    }

    /**
     * Creates a single marker for the given file.
     *
     * If @c file is null, no marker will be created.
     *
     * @param	file		a resource file to create a marker for
     * @param	setting		a string containing the name of a setting
     * @param	line		a line number
     * @param	message		a message explaining the problem
     * @param	severity	a severity level
     */
    public static IMarker create(IFile file, String setting, int line, String message, int severity)
            throws CoreException {
        IMarker marker = create(file, line, message, severity);
        if (marker != null && setting != null) {
            marker.setAttribute(SETTING, setting);
        }
        return marker;
    }

    /**
     * Retrieves the marker severity from the given text. The given
     * text may be warning or error.
     *
     * @param severity	a string to convert to a marker severity
     *
     * @return the marker severity value
     */
    public static int toMarkerSeverity(String severity) {
        if (severity.compareToIgnoreCase(SEVERITY_ERROR) == 0) {
            return IMarker.SEVERITY_ERROR;
        } else if (severity.compareToIgnoreCase(SEVERITY_WARNING) == 0) {
            return IMarker.SEVERITY_WARNING;
        } else {
            return IMarker.SEVERITY_ERROR;
        }
    }

}
