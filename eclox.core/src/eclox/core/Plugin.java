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
 *     Andre Bossert - Improvement static declaration of plugin relative identifier
 *                   - #212: add support for multiple lines (lists) concatenated by backslash (\)
 *                   - #214: add support for TAG and VALUE format
 *                   - #215: add support for line separator
 *
 ******************************************************************************/

package eclox.core;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * Implements the core eclox plugin
 *
 * @author gbrocker
 */
public class Plugin extends org.eclipse.core.runtime.Plugin {

    /**
     * the default plugin instance
     */
    private static Plugin plugin;

    /**
     * The constructor.
     */
    public Plugin() {
        plugin = this;
    }

    public String getLineSeparator() {
        return System.lineSeparator();
    }

    public int listSeparateMode() {
        // TODO: add UI option for this:
        // LIST format:
        //   - do not change (default) = 0
        //   - separate = 1
        //   - one line = 2
        return 0;
    }

    public boolean isIdFixedLengthEnabled() {
        // TODO: add UI option for this:
        // TAG format:
        //   - trimmed length (default) = false
        //   - fixed length = true
        return false;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static Plugin getDefault() {
        return plugin;
    }

    /**
     * Adds the specified message into the plugin's log as a warning.
     *
     * @param message	a string containing a message to log as a warning
     */
    public void logWarning(String message) {
        getLog().log(new Status(Status.WARNING, getBundle().getSymbolicName(), 0, message, null));
    }

    /**
     * Adds the specified message into the plugin's log as an error.
     *
     * @param message	a string containing a message to log as an error
     */
    public void logError(String message) {
        getLog().log(new Status(Status.ERROR, getBundle().getSymbolicName(), 0, message, null));
    }

    /**
     * Adds the specified throwable object into the plugin's log as an error.
     *
     * @param throwable	a throwable instance to log
     */
    public static void log(Throwable throwable) {
        plugin.getLog().log(new Status(Status.ERROR, plugin.getBundle().getSymbolicName(), 0,
                "Exception caught. " + throwable.toString(), throwable));
    }

    public static InputStream getResourceAsStream(IPath path) {
        try {
            return FileLocator.openStream(plugin.getBundle(), path, true);
        } catch (IOException e) {
            log(e);
        }
        return null;
    }

    public static InputStream getResourceAsStream(String path) {
        return getResourceAsStream(new Path(path));
    }
}
