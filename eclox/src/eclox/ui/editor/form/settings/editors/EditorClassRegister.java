//eclox : Doxygen plugin for Eclipse.
//Copyright (C) 2003-2005 Guillaume Brocker
//
//This file is part of eclox.
//
//eclox is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//any later version.
//
//eclox is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with eclox; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	

package eclox.ui.editor.form.settings.editors;

import java.util.HashMap;
import java.util.Map;

import eclox.doxyfiles.Setting;

/**
 * An instance of this class registers all setting editor classes by setting type. 
 * 
 * @author gbrocker
 */
public class EditorClassRegister {
    
    /**
     * The map registering all editor classes.
     */
    private Map register = new HashMap();

    /**
     * Constructor.
     */
    public EditorClassRegister() {
    		register.put( "directory",		DirectoryEditor.class );
    		register.put( "text",			TextEditor.class );
    		register.put( "boolean",			BooleanEditor.class );
    		register.put( "text list",		TextListEditor.class );
    		register.put( "directory list",	DirectoryListEditor.class );
    }
    
    /**
     * Retrieves a class for the specified setting.
     * 
     * @param	setting	a setting for which an editor must be retrieved
     * 
     * @return	a setting editor class
     */
    public Class find(Setting setting) {
        // Retrieves the editor class for that type
        String type = setting.getProperty( Setting.TYPE );
        Class result = (Class) register.get(type);
        
        // Little fallback if no matching editor class was found.
        if(result == null) {
            result = TextEditor.class;
        }
        
        return result;
    }
    
}
