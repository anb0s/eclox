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
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import eclox.doxyfiles.io.DoxyfileParser;


/**
 * Implements the Doxyfile wrapper.
 * 
 * @author gbrocker
 */
public class Doxyfile {
    
    /**
     * The collection of all settings
     */
    private Map settings;
    
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
	    DoxyfileParser reader = new DoxyfileParser(file.getContents());
	    this.settings = reader.read();
	}
	
	/**
	 * Retrieves a single setting for the specified identifier.
	 * 
	 * @param	identifier	a string containing a setting identifier
	 * 
	 * @return	the found setting or null if none
	 */
	public Setting getSetting(String identifier) {
		return (Setting) settings.get(identifier);
	}
	
	/**
	 * Retrieves all settings as an array
	 * 
	 * @return	an array of settings
	 */
	public Object[] getSettings() {
		return settings.values().toArray();
	}
	
	/**
	 * Retrieves the iterator on the doxyfile's settings
	 * 
	 * @return	an iterator on Setting instances
	 */
	public Iterator iterator() {
		return settings.values().iterator();
	}
}
