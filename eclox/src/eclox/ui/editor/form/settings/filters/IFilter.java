/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2005 Guillaume Brocker
 * 
 * This file is part of eclox.
 * 
 * eclox is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 * 
 * eclox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with eclox; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA    
 */

package eclox.ui.editor.form.settings.filters;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Defines the interface for setting filters.
 * 
 * @author gbrocker
 */
public interface IFilter {

    /**
     * Askes the filter to create its user interface controls.
     * 
     * @param   managedForm a managed form to use for the widget creation
     * @param   parent      a composite being the parent of all widgets
     */
    void createControls( IManagedForm managedForm, Composite parent );
    
    /**
     * Retrieves the filter name.
     * 
     * This name must by human readable sinci it is used in the graphical
     * user interface.
     * 
     * @return  a string containing the filter name
     */
    String getName();
}
