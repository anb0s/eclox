/*******************************************************************************
 * Copyright (C) 2003-2005, 2013 Guillaume Brocker
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

package eclox.ui.editor.advanced.filters;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import eclox.core.doxyfiles.Doxyfile;

/**
 * Defines the interface for setting filters.
 * 
 * @author gbrocker
 */
public interface IFilter {
	
	/**
	 * Tells the filter which doxyfile is to filter.
	 * 
	 * @param	doxyfile	a doxyfile instance
	 */
	void setDoxyfile( Doxyfile doxyfile );

    /**
     * Asks the filter to create its user interface controls.
     * 
     * @param   managedForm a managed form to use for the widget creation
     * @param   parent      a composite being the parent of all widgets
     */
    void createControls( IManagedForm managedForm, Composite parent );
    
    /**
     * Asks the filter to create viewer filter and add them in a given viewer
     * 
     * @param   viewer  a viewer where filter must be added
     */
    void createViewerFilters( StructuredViewer viewer );
    
    /**
     * Asks the filter to dispose its controls.
     */
    void disposeControls();
    
    /**
     * Asks the filter to dispose created viewer filter given a viewer
     * 
     * @param   viewer  a structured view from which created viewer filter must be removed
     */
    void disposeViewerFilers( StructuredViewer viewer );
    
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
