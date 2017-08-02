/*******************************************************************************
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andre Bossert - first implementation
 *
 ******************************************************************************/

package eclox.ui.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxygen.BuildJob;
import eclox.ui.Plugin;

public class BuildJobLabelProvider implements ILabelProvider {

    @Override
    public void addListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof BuildJob) {
            Doxyfile doxyfile = ((BuildJob)element).getDoxyfile();
            IFile ifile = doxyfile.getIFile();
            if (ifile != null) {
                Image               result = null;
                IResource           resourse = (IResource)ifile;
                IWorkbenchAdapter   workbenchAdapter = (IWorkbenchAdapter) resourse.getAdapter( IWorkbenchAdapter.class );
                if( workbenchAdapter != null ) {
                    result = workbenchAdapter.getImageDescriptor(ifile).createImage();
                }
                return result;
            } else {
                return Plugin.getImage("user");
            }
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof BuildJob) {
            Doxyfile doxyfile = ((BuildJob)element).getDoxyfile();
            return doxyfile.getFullPath();
        }
        return null;
    }

}
