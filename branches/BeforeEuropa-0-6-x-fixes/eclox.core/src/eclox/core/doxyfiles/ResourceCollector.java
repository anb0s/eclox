// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2007 Guillaume Brocker
// 
// This file is part of eclox.
// 
// eclox is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
// 
// eclox is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with eclox; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA    

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
        if( Doxyfile.isDoxyfile(resource) == true ) {
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
