/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2004 Guillaume Brocker
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

package eclox.doxyfiles;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import eclox.doxyfiles.io.DoxyfileReader;
import eclox.doxyfiles.nodes.Group;

/**
 * Implements the Doxyfile wrapper.
 * 
 * @author gbrocker
 */
public class Doxyfile {
    
    /**
     * The doxyfile root node.
     */
    private Group root = null;
    
    /**
	 * Tells if the specified resource is a doxyfile.
	 * 
	 * @param	resource	the resource to test
	 * 
	 * @return	<code>true</code> or <code>false</code>  
	 */
	public static boolean isDoxyfile(IResource resource) {
		return (resource instanceof IFile) && isDoxyfile((IFile) resource);
	}
	
	/**
	 * Tells if the specified file is a doxyfile.
	 * 
	 * @param	file	the file to test
	 * 
	 * @return	<code>true</code> or <code>false</code>  
	 */
	public static boolean isDoxyfile(IFile file) {
		IContentType	contentType;
		
		contentType = Platform.getContentTypeManager().findContentTypeFor(file.toString());
		
		return contentType != null ? contentType.getId().equals("eclox.doxyfile") : false;
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param	file	a file resource instance that is assumed to be a doxyfile
	 */
	public Doxyfile(IFile file) throws IOException, CoreException {
	    DoxyfileReader reader = new DoxyfileReader(file.getContents());
	    this.root = reader.read();
	}
	
	
	/**
	 * Retrieves the doxyfile root node.
	 * 
	 * @return	the root node instance can be null if none
	 */
	public Group getRoot() {
	    return this.root;
	}
	
}
