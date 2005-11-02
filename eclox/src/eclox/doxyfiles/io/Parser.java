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

import eclox.doxyfiles.Chunk;
import eclox.doxyfiles.Doxyfile;
import eclox.doxyfiles.RawText;
import eclox.doxyfiles.Setting;



/**
 * Implements a	doxyfile parser.
 * 
 * @author gbrocker
 */
public class Parser {

    /**
     * The line reader used to parse the input stream.
     */
    private BufferedReader reader;
    
    /**
     * The comment line pattern.
     */
    private Pattern commentPattern = Pattern.compile("#.*");
    
    /**
     * the setting assignement pattern
     */
    private Pattern settingAssignment = Pattern.compile("(\\w+)\\s*=\\s*(.*?)\\s*(\\\\)?");
    
    /**
     * the continued setting assignement pattern
     */
    private Pattern continuedSettingAssignment = Pattern.compile("\\s*(.*?)\\s*(\\\\)?");
    
    /**
     * the setting being continued on multiple lines.
     */
    private Setting continuedSetting;
    
    /**
     * Constructor.
     * 
     * @param	input	an input stram instance to parse as a doxyfile
     */
    public Parser( InputStream input ) throws IOException {
        this.reader = new BufferedReader( new InputStreamReader(input) );
        this.reader.mark(0);
    }
    
    /**
     * Reads the input stream and returns the doxyfile.
     * 
     * @param	doxyfile	a doxyfile where the parser results will be stored
     * 
     * @return	a collection of the setting read from the input stream
     */
    public void read( Doxyfile doxyfile ) throws IOException {
        // Initialization of the system.
        this.reader.reset();
        
        // Reads and parses all lines.
        int lineNumber = 0;
        try {
	        String	line;
	        for( line = reader.readLine(); line != null; line = reader.readLine() ) {
	        	lineNumber++;
	        	this.matchLine( doxyfile, line );	        	
	        }
        }
        catch(Throwable throwable) {
            throw new IOException( "Syntax error at line " + lineNumber + ". " + throwable.getMessage() );
        }
    }
    
    /**
     * Matches the specified line.
     * 
     * @param	doxyfile	a doxyfile where the result will be stored
     * @param	line		a string containing the current line text
     */
    private void matchLine( Doxyfile doxyfile, String line ) throws IOException {
        Matcher matcher;
        
        // Matches the current line against an empty line pattern
        if(line.length() == 0) {
        	this.processAnyLine( doxyfile, line );
            return;
        }
        
        // Matches the current line against the note border pattern.
        matcher = commentPattern.matcher(line);
        if(matcher.matches() == true) {
        	this.processAnyLine( doxyfile, line );
            return;
        }
        
        // Matches the current line against the setting assignment pattern.
        matcher = settingAssignment.matcher(line);
        if(matcher.matches() == true) {
            // Retrieves the setting identifier and its values.
            String  identifier = matcher.group(1);
            String  values = matcher.group(2);
            String  continued = matcher.group(3);
            
            // Call the traitement for the setting assignment and pull-out.
            this.processSettingAssignment( doxyfile, identifier, values, continued != null );
            return;
        }
        
        // Matches the current line against the contibued setting assignment pattern.
        matcher = continuedSettingAssignment.matcher(line);
        if( matcher.matches() == true ) {
            // Retrieves the setting identifier and its values.
            String  values = matcher.group(1);
            String  continued = matcher.group(2);
            
            // Call the traitement for the setting assignment and pull-out.
            //Setting setting;
            //setting = this.processSettingAssignment( doxyfile, identifier, values);
            
            // Remembers the continued setting.
            //continuedSetting = ( continued != null ) ? setting : null; 
            
            return;
            
        }
        
        // The line has not been recognized.
        throw new IOException("Unable to match line.");
    }
    
    /**
     * Processes any line of a doxyfile that is not interesting and should only be stored
     * for later use (saving for example).
     * 
     * @param	doxyfile	a doxyfile where the line will be stored
     * @param	text		a string containing the line text
     */
    private void processAnyLine( Doxyfile doxyfile, String text ) {
    	// Retrieves the last raw text chunk.
    	Chunk	lastChunk = doxyfile.getLastChunk();
    	RawText	rawText;
    	if( lastChunk instanceof RawText ) {
    		rawText = (RawText) lastChunk;
    	}
    	else {
    		rawText = new RawText();
    		doxyfile.append( rawText );
    	}
    	
    	// Stores the line's text in the raw text chunk.
    	rawText.append( text );
    	rawText.append( "\n" );
        
        // Resets the continued setting.
        continuedSetting = null;
    }
    
    /**
     * Processes a setting assignment line.
     * 
     * @param	doxyfile	a doxyfile where the setting assignment will be stored
     * @param	identifier	a string containing the setting identifier
     * @param	value		a string containing the assigned value
     * @param   continued   a boolean telling if the setting assignement is continued on multiple line
     */
    private void processSettingAssignment( Doxyfile doxyfile, String identifier, String value, boolean continued ) throws IOException {
        Setting setting = new Setting( identifier, value ); 
        doxyfile.append( setting );
        continuedSetting = ( continued == true ) ? setting : null;
  	}
}
