/*
eclox : Doxygen plugin for Eclipse.
Copyright (C) 2003-2004 Guillaume Brocker

This file is part of eclox.

eclox is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
any later version.

eclox is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with eclox; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
*/

package eclox.resource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import eclox.resource.content.DoxyfileContent;
import eclox.resource.content.io.Deserializer;
import eclox.resource.content.io.Serializer;

/**
 * Implement global services for doxyfiles.
 * 
 * @author gbrocker
 */
public final class Doxyfile {
	/**
	 * Retreieve the doxyfile content of the specified file.
	 * 
	 * @param doxyfile	the doxyfile to parse
	 * 
	 * @return	the content of the doxyfile
	 * 
	 * @throws	DoxyfileException	error while reading the doxyfile content
	 */
	public static DoxyfileContent getContent(IFile file) throws DoxyfileException {
		try {
			Deserializer deserializer = new Deserializer(file);
			
			return deserializer.createDoxyfile();
		}
		catch(Throwable throwable) {
			throw new DoxyfileException("Error while loading the doxyfile. " + throwable.getMessage(), throwable);
		}
	}
	
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
	 * Set a doxyfile content into the specified file.
	 * 
	 * @param	content			the doxyfile content to save
	 * @param	file			the file that will receive the doxyfile content
	 * @param	progressMonitor	a progress monitor
	 * 
	 * @throws	DoxyfileException	error while saving the doxyfile content 
	 */
	public static void setContent(DoxyfileContent content, IFile file, org.eclipse.core.runtime.IProgressMonitor progressMonitor) throws DoxyfileException {
		try {
			Serializer saver = new Serializer();
			
			content.accept(saver);
			file.setContents(saver, true, true, progressMonitor);
		}
		catch(Throwable throwable) {
			throw new DoxyfileException("Error while write doxyfile content. " + throwable.getMessage(), throwable);
		}		
	}
}
