/*******************************************************************************
 * Copyright (C) 2003, 2004, 2007, 2008, 2013, Guillaume Brocker
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

package eclox.ui.wizard;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import eclox.core.doxygen.Doxygen;
import eclox.core.doxygen.InvokeException;
import eclox.core.doxygen.RunException;
import eclox.ui.Plugin;

/**
 * Implement the new file wizard extension to provide a way to create
 * new doxyfiles.
 * 
 * @author gbrocker
 */
public class NewDoxyfileWizard extends Wizard implements INewWizard {

    private IFile m_doxyfile; ///< The created doxyfile.
    private NewDoxyfileWizardPage m_page; ///< The wizard page used to get the file name.

    /**
     * Retrieves the created doxyfile.
     * 
     * @return	the created doxyfile, null if none
     */
    public IFile getDoxyfile() {
        return m_doxyfile;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        m_page = new NewDoxyfileWizardPage(selection);
        addPage(m_page);
        setWindowTitle("New Doxygen Configuration");
    }

    /**
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish() {
        for (;;) {
            // Creates the doxyfile resource.
            IFile doxyfile = createFile(m_page.getContainerFullPath(), m_page.getFileName());

            // Warn user if the doxyfile exists.
            if (doxyfile.exists()) {
                MessageDialog.openWarning(getShell(), "Resource Exists",
                        "The resource " + doxyfile.getFullPath().toString() + " already exists !");
                return false;
            }

            // Creates the effective resource file.
            try {
                Doxygen.getDefault().generate(doxyfile);
                m_doxyfile = doxyfile;
                return true;
            }
            // Doxygen returned an error.
            catch (RunException runException) {
                MessageDialog.openError(getShell(), "Doxygen Error",
                        "An error occured while running doxygen. " + runException.toString());
                return true;
            }
            // Doxygen was impossible to run. 
            catch (InvokeException invokeException) {
                if (Plugin.editPreferencesAfterDoxygenInvocationFailed()) {
                    continue;
                }

                // Stops the wizard.
                return true;
            }
        }
    }

    /**
     * Create the resource file.
     * 
     * @param containerPath	The path to the container for the resource to create.
     * @param fileName		A string containnig the name of the file resource to create.
     * 
     * @return	The created file resource. The file system's file hasn't been created at this step.
     */
    private IFile createFile(IPath containerPath, String fileName) {
        IFile result;
        IPath filePath;
        String fileExtension;

        filePath = containerPath.append(fileName);
        fileExtension = filePath.getFileExtension();
        if ((fileExtension == null || fileExtension.compareToIgnoreCase("Doxyfile") != 0)
                && fileName.compareToIgnoreCase("Doxyfile") != 0) {
            filePath = filePath.addFileExtension("Doxyfile");
        }
        result = ResourcesPlugin.getWorkspace().getRoot().getFile(filePath);
        return result;
    }
}
