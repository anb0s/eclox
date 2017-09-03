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

package eclox.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import eclox.ui.EditorPropertyTester;
import eclox.ui.action.BuildPopupActionDelegate;

public class Build extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // get resource type
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        BuildPopupActionDelegate action = EditorPropertyTester.getAction(activePart);
        if (action != null) {
            action.run(null);
        }
        return null;
    }

}
