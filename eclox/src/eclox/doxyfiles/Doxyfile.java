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
import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import eclox.doxyfiles.io.Parser;


/**
 * Implements the Doxyfile wrapper.
 * 
 * @author gbrocker
 */
public class Doxyfile {
	
	/**
	 * a collection containing all doxyfile's chunks
	 */
	private AbstractList chunks = new Vector();
    
    /**
     * a map containing all managed settings
     */
    private Map settings = new LinkedHashMap();
    
    /**
     * a map containing all managed groups
     */
    private Map groups = new LinkedHashMap();
    
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
        String          name = file.getName();
		IContentType	contentType = Platform.getContentTypeManager().findContentTypeFor( name );
		
		return contentType != null ? contentType.getId().equals("org.gna.eclox.core.doxyfile") : false;
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param	file	a file resource instance that is assumed to be a doxyfile
	 */
	public Doxyfile( IFile file ) throws IOException, CoreException {
	    Parser parser = new Parser(file.getContents());
	    parser.read( this );
	}
	
	/**
	 * Appends a new chunk to the doxyfile
	 * 
	 * @param	chunk	a chunk to append to the doxyfile
	 */
	public void append( Chunk chunk ) {
		this.chunks.add( chunk );
		if( chunk instanceof Setting ) {
			Setting	setting = (Setting) chunk;
			this.settings.put( setting.getIdentifier(), setting );
            
            // Retrieves the setting group name.
            String groupName = setting.getProperty( Setting.GROUP );
            if( groupName == null ) {
                groupName = new String( "Others" );
                setting.setProperty( Setting.GROUP, groupName );
            }
            
            // Retrieves the setting group and stores the setting in it.
            Group group = (Group) this.groups.get( groupName );
            if( group == null ) {
                group = new Group( groupName );
                this.groups.put( groupName, group );
            }
            group.add( setting );
		}
	}
    
    /**
     * Retrieves all groups present in the doxyfile.
     * 
     * @return  an array containing all groups
     */
    public Object[] getGroups() {
        return this.groups.values().toArray();
    }
	
	/**
	 * Retrieves the last appended chunk
	 * 
	 * @return	the last added chunk or null if none
	 */
	public Chunk getLastChunk() {
		if( this.chunks.isEmpty() == false ) {
			return (Chunk) this.chunks.get( this.chunks.size() - 1 );
		}
		else {
			return null;
		}
	}
	
	/**
	 * Retrieves a single setting for the specified identifier.
	 * 
	 * @param	identifier	a string containing a setting identifier
	 * 
	 * @return	the found setting or null if none
	 */
	public Setting getSetting( String identifier ) {
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
	 * Retrieves the iterator the whole chunk collection.
	 * 
	 * @return	an iterator on chunks
	 */
	public Iterator iterator() {
		return this.chunks.iterator();
	}
	
	/**
	 * Retrieves the iterator on the setting collection.
	 * 
	 * @return	an iterator on Setting instances
	 */
	public Iterator settingIterator() {
		return settings.values().iterator();
	}
	
}
