/*******************************************************************************
 * Copyright (C) 2003, 2006, 2007, 2013, Guillaume Brocker
 * Copyright (C) 2015-2016, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - Add ability to use Doxyfile not in project scope
 *                   - Refactoring of deprecated API usage
 ******************************************************************************/

package eclox.ui.editor.editors;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import eclox.core.doxyfiles.Doxyfile;
import eclox.ui.IPreferences;
import eclox.ui.Plugin;

/**
 * Implements convenience methods for setting editors.
 *
 * @author gbrocker
 */
public final class Convenience {

    /**
     * Implements a specialized input dialog that adds buttons for browsing
     * workspace or file system for paths.
     */
    private static class PathInputDialog extends InputDialog {

        /**
         * Implements an input validator for the input dialog used to edit value compunds.
         */
        private static class MyInputValidator implements IInputValidator {

            public String isValid(String newText) {
                if (newText.length() == 0) {
                    return "Empty value is not allowed.";
                } else {
                    return null;
                }
            }

        }

        private final static int BROWSE_FILESYSTEM_FILE_ID = IDialogConstants.CLIENT_ID + 3;

        private final static int BROWSE_FILESYSTEM_DIRECTORY_ID = IDialogConstants.CLIENT_ID + 4;

        private Doxyfile doxyfile;

        private int styles;

        /**
         * Constructor
         *
         * @param	shell		the parent shell
         * @param	doxyfile	the reference doxyfile
         * @param	initValue	the initial value
         * @param	styles		a combination of browse facility flags
         */
        public PathInputDialog(Shell shell, Doxyfile doxyfile, String initValue, int styles) {
            super(shell, "Value Edition", "Edit the path. You may use buttons bellow to browse for a path.", initValue,
                    new MyInputValidator());

            this.doxyfile = doxyfile;
            this.styles = styles;
        }

        protected void createButtonsForButtonBar(Composite parent) {
            // Create additionnal buttons.
            if ((styles & BROWSE_FILESYSTEM_FILE) != 0) {
                createButton(parent, BROWSE_FILESYSTEM_FILE_ID, "Browse File...", false);
            }
            if ((styles & BROWSE_FILESYSTEM_DIRECTORY) != 0) {
                createButton(parent, BROWSE_FILESYSTEM_DIRECTORY_ID, "Browse Directory...", false);
            }

            // Create default buttons.
            super.createButtonsForButtonBar(parent);
        }

        protected void buttonPressed(int buttonId) {
            // Retrieves the path if one of the browse buttons is pressed.
            String path = null;
            switch (buttonId) {
            case BROWSE_FILESYSTEM_DIRECTORY_ID:
                path = Convenience.browseFileSystemForDirectory(getShell(), doxyfile, getText().getText());
                break;

            case BROWSE_FILESYSTEM_FILE_ID:
                path = Convenience.browseFileSystemForFile(getShell(), doxyfile, getText().getText());
                break;

            default:
                super.buttonPressed(buttonId);
            }

            // Updates the input text if a path has been retrieved.
            if (path != null) {
                getText().setText(path);
            }
        }

    }

    /**
     * flag that activates the facility to browse for a file system file
     */
    public static final int BROWSE_FILESYSTEM_FILE = 1;

    /**
     * flag that activates the facility to browse for a file system directory
     */
    public static final int BROWSE_FILESYSTEM_DIRECTORY = 2;

    /**
     * flag that activates the facility to browse for a workspace or file system path
     */
    public static final int BROWSE_ALL = BROWSE_FILESYSTEM_FILE | BROWSE_FILESYSTEM_DIRECTORY;

    /**
     * @brief	Retrieves a path from the user that will be relative to the given doxyfile.
     *
     * @param	shell		the parent shell
     * @param	doxyfile	the reference doxyfile
     * @param	initPath	the initial path (may relative to the given doxyfile or absolute)
     * @param	style		a combination of flags to tell which browse facilities will be available
     *
     * @return	a string containing the path entered by the user or null if none
     */
    public static String browserForPath(Shell shell, Doxyfile doxyfile, String initPath, int style) {
        PathInputDialog dialog = new PathInputDialog(shell, doxyfile, initPath, style);

        if (dialog.open() == PathInputDialog.OK) {
            return dialog.getValue();
        } else {
            return null;
        }
    }

    /**
     * Browses the file system for a file and retrieves a path in the file system that
     * may be relative to the given doxyfile's path
     *
     * @param	shell		a shell that will be the parent of dialogs
     * @param	doxyfile	a doxyfile that is the reference for relative paths
     * @param	path		a string containing an initial path
     *
     * @return	a string containing the browsed path, or null if none
     */
    public static String browseFileSystemForFile(Shell shell, Doxyfile doxyfile, String path) {
        // Retrieves the initial path.
        IPath initialPath = new Path(path);
        if (initialPath.isAbsolute() == false) {
            initialPath = doxyfile.getIFile().getParent().getLocation().append(initialPath);
        }

        // If the initial path is not valid, use the doxyfile location as fall back.
        File file = new File(initialPath.toOSString());
        if (file.exists() == false) {
            initialPath = doxyfile.getIFile().getParent().getLocation();
        }

        // Displays the directory dialog to the user
        FileDialog dialog = new FileDialog(shell);
        String choosenPathString;

        dialog.setText("File System File Selection");
        dialog.setFilterPath(initialPath.toOSString());
        choosenPathString = dialog.open();

        // Parses the result.
        if (choosenPathString != null) {
            IPath choosenPath = Path.fromOSString(choosenPathString);
            IPath finalPath = doxyfile.makePathRelative(choosenPath);

            return finalPath.toOSString();
        } else {
            return null;
        }
    }

    /**
     * Browses the file system for a directory and retrieves a path in the file system that
     * may be relative to the given doxyfile's path.
     *
     * @param	shell		a shell that will be the parent of dialogs
     * @param	doxyfile	a doxyfile that is the reference for relative paths
     * @param	path		a string containing an initial path
     *
     * @return	a string containing the browsed path, or null if none
     */
    public static String browseFileSystemForDirectory(Shell shell, Doxyfile doxyfile, String path) {
        // Retrieves the initial path.
        IPath initialPath = new Path(path);
        if (initialPath.isAbsolute() == false) {
            initialPath = doxyfile.getIFile().getParent().getLocation().append(initialPath);
        }

        // If the initial path is not valid, use the doxyfile location as fall back.
        File file = new File(initialPath.toOSString());
        if (file.exists() == false) {
            initialPath = doxyfile.getIFile().getParent().getLocation();
        }

        // Displays the directory dialog to the user
        DirectoryDialog dialog = new DirectoryDialog(shell);
        String choosenPathString;
        dialog.setText("File System Directory Selection");
        dialog.setFilterPath(initialPath.toOSString());
        choosenPathString = dialog.open();

        // Parses the result.
        if (choosenPathString != null) {
            IPath choosenPath = Path.fromOSString(choosenPathString);
            IPath finalPath = doxyfile.makePathRelative(choosenPath);

            return finalPath.toOSString();
        } else {
            return null;
        }
    }

    /**
     * Escapes the given value string. This will place back slashes before
     * any backslash or double quote.
     *
     * @param	value	the value string to escape
     *
     * @return	the escaped value string
     *
     * @see		unescapeValue
     */
    public static String escapeValue(String value) {
        String result = value;
        // Retrieves the plug-in preferences.
        IPreferenceStore preferences = Plugin.getDefault().getPreferenceStore();
        if (preferences.getBoolean(IPreferences.HANDLE_ESCAPED_VALUES) == true) {
            result = replace(result, "\\", "\\\\"); // Replaces all \ by \\
            result = replace(result, "\"", "\\\""); // Replaces all " by \"
        }
        return result;
    }

    /**
     * Un-escapes the given value string. This will remove escape backslashes
     * located before backslashes and double quotes.
     *
     * @param	value	the value string to un-escape
     *
     * @return	the escaped value string
     *
     * @see		escapeValue
     */
    public static String unescapeValue(String value) {
        String result = value;
        // Retrieves the plug-in preferences.
        IPreferenceStore preferences = Plugin.getDefault().getPreferenceStore();
        if (preferences.getBoolean(IPreferences.HANDLE_ESCAPED_VALUES) == true) {
            result = replace(result, "\\\"", "\""); // Replaces all \" by "
            result = replace(result, "\\\\", "\\"); // Replaces all \\ by \
        }
        return result;
    }

    /**
     * In the given string, replaces all matching substring by the given replacement.
     *
     * @param	string	the string to update
     * @param	search	the sub string to search and replace
     * @param	replace	the string to place where search matches
     *
     * @return	the resulting string
     */
    public static String replace(String string, String search, String replace) {
        StringBuffer buffer = new StringBuffer(string);

        for (int i = buffer.indexOf(search); i != -1; i = buffer.indexOf(search, i + replace.length())) {
            buffer = buffer.replace(i, i + search.length(), replace);
        }
        return buffer.toString();
    }
}
