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

package eclox.doxyfiles.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eclox.doxyfiles.PropertyProvider;

/**
 * Implements a node property file reader class.
 * 
 * @author gbrocker
 */
public class PropertyReader {
    
    /**
     * The line reader used to parse the node descrption file.
     */
    private BufferedReader reader;
    
    /**
     * The comment line pettern.
     */
    private Pattern commentPattern = Pattern.compile("#.*");
    
    /**
     * The node identifier line pattern.
     */
    private Pattern identifierPattern = Pattern.compile("\\[(.+)\\]");
    
    /**
     * The property line pattern.
     */
    private Pattern propertyPattern = Pattern.compile("(\\w+)\\s*=\\s*(.*)");

    /**
     * Constructor.
     * 
     * @param	input	an input stream contating the description to read
     */
    public PropertyReader(InputStream input) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(input));
        this.reader.mark(0);
    }
    
    /**
     * Reads the description
     * 
     * @param	provider	a property provider instance to fill 
     * 
     * @author gbrocker
     */
    public void read(PropertyProvider provider) throws IOException {
        // Resets the reader state.
        String nodeIdentifier = null;
        String propertyName = null;
        String propertyValue = null;
        this.reader.reset();
        
        // Reads all lines of the node property file
        for(String line = this.reader.readLine(); line != null; line = this.reader.readLine()) {
            Matcher matcher;
            
            // Skip empty lines
            if(line.length() == 0) {
                nodeIdentifier = null;
                propertyName = null;
                propertyValue = null;
                continue;
            }
            
            // Skip comment lines.
            matcher = commentPattern.matcher(line);
            if(matcher.matches() == true) {
                continue;
            }
            
            // Process node identifier lines.
            matcher = identifierPattern.matcher(line);
            if(matcher.matches() == true) {
                nodeIdentifier = matcher.group(1);
                propertyName = null;
                propertyValue = null;
                continue;
            }
            
            // Process property declaration line
            matcher = propertyPattern.matcher(line);
            if(matcher.matches() == true) {
                propertyName = matcher.group(1);
                propertyValue = matcher.group(2);
                provider.setProperty(nodeIdentifier, propertyName, propertyValue);
                continue;                
            }
            
            // By default, the current line is processed as a full text line.
            if(nodeIdentifier != null && propertyName != null && propertyValue != null) {
                propertyValue = propertyValue + " " + line;
                provider.setProperty(nodeIdentifier, propertyName, propertyValue);
            }
        }        
    }
}
