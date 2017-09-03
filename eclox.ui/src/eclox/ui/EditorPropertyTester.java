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

package eclox.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import eclox.ui.action.BuildPopupActionDelegate;

public class EditorPropertyTester extends PropertyTester {

    public EditorPropertyTester() {
        super();
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if("hasResourceSelection".equals(property) && receiver instanceof IWorkbenchPart){
            if (args.length > 0 && args[0].equals("resourceType")) {
                ISelection selection = ((IWorkbenchPart)receiver).getSite().getSelectionProvider().getSelection();
                return BuildPopupActionDelegate.getDoxygenResourceForType(selection, null, ResourceType.getFromEnum((String)expectedValue)) != null;
            }
        }
        return false;
    }

    static public BuildPopupActionDelegate getAction(IWorkbenchPart part) {
        ISelection selection = part.getSite().getSelectionProvider().getSelection();
        if (selection != null) {
            BuildPopupActionDelegate action = new BuildPopupActionDelegate();
            action.selectionChanged(null, selection);
            return action;
        }
        return null;
    }

}
