/*******************************************************************************
 * Copyright (C) 2003, 2004, 2007, 2008, 2013, Guillaume Brocker
 * Copyright (C) 2015-2016, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - Add ability to use Doxyfile not in project scope
 *
 ******************************************************************************/

package eclox.core.doxyfiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import eclox.core.doxyfiles.io.Parser;


/**
 * Implements the Doxyfile wrapper.
 *
 * @author gbrocker
 */
public class Doxyfile {

    /**
     * the eclipse file that holds the doxyfile content
     */
    private IFile ifile;

    /**
     * the system file (outside eclipse) that holds the doxyfile content
     */
    private File file;

    /**
     * a collection containing all doxyfile's chunks
     */
    private AbstractList<Chunk> chunks = new Vector<Chunk>();

    /**
     * a map containing all managed settings
     */
    private Map<String, Setting> settings = new LinkedHashMap<String, Setting>();

    /**
     * a map containing all managed groups
     */
    private Map<String, Group> groups = new LinkedHashMap<String, Group>();

    /**
     * Tells if the given object is a doxyfile
     *
     * @param	object	an object to test
     *
     * @return	true or false
     */
    public static boolean isDoxyfile(Object object) {
        return (object instanceof IResource) && isDoxyfile((IResource) object);
    }

    /**
     * Tells if the specified resource is a doxyfile.
     *
     * @param	resource	the resource to test
     *
     * @return	<code>true</code> or <code>false</code>
     */
    public static boolean isDoxyfile(IResource resource) {
        return (resource != null) && (resource instanceof IFile) && isDoxyfile((IFile) resource);
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
    public Doxyfile( IFile ifile, File file ) {
        this.ifile = ifile;
        this.file = file;
    }

    public void load() throws CoreException, FileNotFoundException, IOException {
        InputStream input;
        if (ifile != null)
            input = ifile.getContents();
        else
            input = new BufferedInputStream(new FileInputStream(file));
        Parser parser = new Parser(input);
        parser.read( this );
    }

    /**
     * Appends a new chunk to the doxyfile.
     *
     * @param	chunk	a chunk to append to the doxyfile
     */
    public void append( Chunk chunk ) {
        // Pre-condition
        assert chunk.getOwner() == null;

        // References the chunk.
        chunk.setOwner( this );
        this.chunks.add( chunk );

        // Do special handling for settings.
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
     * Retrieves the resource file that contains the doxyfile.
     *
     * @return	a resource file
     */
    public IFile getIFile() {
        return ifile;
    }

    /**
     * Retrieves the resource file that contains the doxyfile.
     *
     * @return  a file
     */
    public File getFile() {
        return file;
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
     * Retrieves the container that will receive the documentation build outputs.
     *
     * @return	a folder, or null when none
     */
    public IContainer getOutputContainer() {
        IContainer	outputContainer	= null;
        Setting		outputSetting	= getSetting("OUTPUT_DIRECTORY");
        if(outputSetting != null) {
            Path outputPath = new Path(outputSetting.getValue());
            if(outputPath.isEmpty()) {
                if (ifile != null) {
                    outputContainer = ifile.getParent();
                }
            } else {
                File containerFile = null;
                if(outputPath.isAbsolute()) {
                    containerFile = outputPath.toFile();
                } else {
                    if (ifile != null) {
                        containerFile = ifile.getParent().getLocation().append(outputPath).toFile();
                    } else {
                        containerFile = new Path(file.getParentFile().getAbsolutePath()).append(outputPath).toFile();
                    }
                }
                if (containerFile != null && containerFile.exists()) {
                    IContainer[] foundContainers = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocationURI(containerFile.toURI());
                    if (foundContainers.length >= 1) {
                        outputContainer =  foundContainers[0];
                    }
                }
            }
        }
        return outputContainer;
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
     * Tells if the doxyfile has the given setting.
     *
     * @param	identifier	a string containing a setting identifier
     *
     * @return	true or false
     */
    public boolean hasSetting( String identifier ) {
        return settings.get(identifier) != null;
    }

    /**
     * Tells if the given path is relative to the doxyfile.
     *
     * @return	true or false
     */
    public boolean isPathRelative( IPath path ) {
        if (ifile != null)
            return ifile.getLocation().removeLastSegments(1).isPrefixOf( path );
        else
            return false;
    }

    /**
     * Makes the given path relative to the doxyfile path only of the given
     * path point to a location that is in the sub-directory containing the
     * doxyfile.
     *
     * @return	the argument path made relative to the doxyfile
     */
    public IPath makePathRelative( IPath path ) {
        if( isPathRelative(path) ) {
            int		matchingCount = ifile.getLocation().removeLastSegments(1).matchingFirstSegments( path );
            IPath	relativePath = path.removeFirstSegments( matchingCount ).setDevice( new String() );
            return relativePath.isEmpty() ? new Path(".") : relativePath;
        }
        else {
            return path;
        }
    }

    /**
     * Retrieves the iterator the whole chunk collection.
     *
     * @return	an iterator on chunks
     */
    public Iterator<Chunk> iterator() {
        return this.chunks.iterator();
    }

    /**
     * Retrieves the iterator on the setting collection.
     *
     * @return	an iterator on Setting instances
     */
    public Iterator<Setting> settingIterator() {
        return settings.values().iterator();
    }

    public String getFullPath() {
        if (ifile != null) {
            return ifile.getFullPath().toOSString();
        } else {
            return file.getAbsolutePath();
        }
    }

    public String getName() {
        if (ifile != null) {
            return ifile.getName();
        } else {
            return file.getName();
        }
    }

    public boolean exists() {
        if (ifile != null) {
            return ifile.exists();
        } else {
            return file.exists();
        }
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof Doxyfile) {
            Doxyfile doxyTo = (Doxyfile)arg0;
            return getFullPath().equals(doxyTo.getFullPath());
        }
        return false;
    }

}
