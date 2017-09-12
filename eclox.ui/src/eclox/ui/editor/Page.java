/*******************************************************************************
 * Copyright (C) 2003-2013, Guillaume Brocker
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

package eclox.ui.editor;

import org.eclipse.ui.forms.editor.FormPage;

import eclox.core.doxyfiles.Setting;

public abstract class Page extends FormPage {

    public Page(Editor editor, String id, String title) {
        super(editor, id, title);
    }

    public abstract Setting getCurrentSetting();

    public abstract void setCurrentSetting(Setting setting);
}
