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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eclox.doxyfiles.Setting;



/**
 * Implements a	doxyfile reader.
 * 
 * @author gbrocker
 */
public class DoxyfileParser {

    /**
     * The line reader used to parse the input stream.
     */
    private BufferedReader reader;
    
    /**
     * The collection of setting instances.
     */
    private Map settings;
    
    /**
     * The comment line pattern.
     */
    private Pattern commentPattern = Pattern.compile("#.*");
    
    /**
     * The setting assignement pattern.
     */
    private Pattern settingAssignment = Pattern.compile("(\\w+)\\s*=\\s*(.*)\\s*");
    
    /**
     * Constructor.
     * 
     * @param	input	an input stram instance to parse as a doxyfile
     */
    public DoxyfileParser(InputStream input) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(input));
        this.reader.mark(0);
    }
    
    /**
     * Reads the input stream and returns the doxyfile.
     * 
     * @return	a collection of the setting read from the input stream
     */
    public Map read() throws IOException {
        // Initialization of the system.
        this.reader.reset();
        this.settings = new java.util.HashMap();
        
        // Reads and parses all lines.
        int lineNumber = 0;
        try {
	        String line;
	        for(line = this.reader.readLine(); line != null; line = this.reader.readLine()) {
	            lineNumber++;
	            this.matchLine(line);    
	        }
        }
        catch(Throwable throwable) {
            throw new IOException("Syntax error at line " + lineNumber + ". " + throwable.getMessage());
        }
        
        // The job is done.
        return this.settings;
    }
    
    /**
     * Matches the specified line.
     * 
     * @param	line	a string containing the current line text
     */
    private void matchLine(String line) throws IOException {
        Matcher matcher;
        
        // Matches the current line against an empty line pattern
        if(line.length() == 0) {
            return;
        }
        
        // Matches the current line against the note border pattern.
        matcher = commentPattern.matcher(line);
        if(matcher.matches() == true) {
            return;
        }
        
        // Matches the current line against the setting assignement pattern.
        matcher = settingAssignment.matcher(line);
        if(matcher.matches() == true) {
            // Retrieves the setting identifier and its values.
            String identifier = matcher.group(1);
            String values = matcher.group(2);

            // Call the traitement for the setting assignment and pull-out.
            this.processSettingAssignment(identifier, values);
            return;
        }
                            
        // The line has not been recognized.
        throw new IOException("Unable to match line.");
    }
    
    /**
     * Processes a setting assignment line.
     * 
     * @param	identifier	a string containing the setting identifier
     * @param	value		a string containing the assigned value
     */
    private void processSettingAssignment(String identifier, String value) throws IOException {
        // Creates the setting.
        Setting setting = new Setting(identifier, value);
        this.settings.put(identifier, setting);
  	}
}
