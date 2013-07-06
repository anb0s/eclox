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
 * Derfines the interface for listeners receiving notifications about property changes.
 * 
 * @author gbrocker
 */
public interface ISettingPropertyListener extends ISettingListener {

    /**
     * Notifies that a setting property has been added or its has changed.
     * 
     * @param	setting		the setting that raised the event
     * @param	property		a string containing a property name	
     */
    void settingPropertyChanged( Setting setting, String property );
    
    /**
     * Notifies that a setting property has been removed.
     * 
     * @param	setting		the setting that raised the event
     * @param	property		a string containing a property name
     */
    void settingPropertyRemoved( Setting setting, String property );

}
