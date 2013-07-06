/*******************************************************************************
 * Copyright (C) 2003-2007, 2013, Guillaume Brocker
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;


/**
 * Implements a resource collector that will search for available doxyfiles
 * either from the workbenck resources' root or from a given resource.
 * 
 * @author gbrocker
 */
public class ResourceCollector implements IResourceVisitor {
    
    /**
     * a collection of all collected resources
     */
    private Collection m_doxyfiles = new ArrayList();

    /**
     * Runs a collector from the workspace resources' root.
     * 
     * @return  a resource collector containing collected doxfiles
     */
    public static ResourceCollector run() throws CoreException {
    	return run( ResourcesPlugin.getWorkspace().getRoot() );
    }
    
    /**
     * Runs a collector from the given root resource.
     * 
     * @param	resource	a resource to search for doxyfiles
     * 
     * @return  a resource collector containing collected doxfiles
     */
	public static ResourceCollector run(IResource root) throws CoreException {
		ResourceCollector   collector = new ResourceCollector();
		root.accept( collector );
		return collector;
	}
    
    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
     */
    public boolean visit(IResource resource) throws CoreException {
        // Determine if the current resource is  doxyfile, and if so, stores the resource
        if( resource.isAccessible() && Doxyfile.isDoxyfile(resource) == true ) {
            m_doxyfiles.add( resource );
        }
        return true;
    }
    
    /**
     * Tells if the collector is empty.
     * 
     * @return	true or false
     */
    public boolean isEmpty() {
    	return m_doxyfiles.isEmpty();
    }
    
    /**
     * Retrieves the first doxyfile.
     * 
     * @return	the first doxyfile, or null when none
     */
    public IFile getFirst() {
    	return m_doxyfiles.isEmpty() ? null : (IFile) m_doxyfiles.iterator().next();
    }
    
    /**
     * Retrieves the collection with all collected dixyfile resources.
     * 
     * @return  a collection with all collected doxyfile resources
     */
    public Collection getDoxyfiles() {
        return m_doxyfiles;
    }
    
    /**
     * Retrieves the number of collected doxyfiles.
     * 
     * @return	the number of collected doxyfiles
     */
    public int getSize() {
    	return m_doxyfiles.size();
    }
    
    /**
     * Retrieves the iterator on the collection of collected doxyfile resources.
     * 
     * @return  an iterator instance
     */
    public Iterator iterator() {
        return m_doxyfiles.iterator();
    }

}
