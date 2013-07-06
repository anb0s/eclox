/*******************************************************************************
 * Copyright (C) 2003-2005, 2013, Guillaume Brocker
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

package eclox.core.doxyfiles;

/**
 * Defines the interface for listener listening for setting value changes.
 * 
 * @author gbrocker
 */
public interface ISettingValueListener extends ISettingListener {
    
    /**
     * Notifies that a setting value changed.
     *
     * @param	setting	the setting that raised the event 
     */
    void settingValueChanged( Setting setting );

}
