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

package eclox.core.doxyfiles.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import eclox.core.Services;
import eclox.core.Plugin;
import eclox.core.doxyfiles.Chunk;
import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.RawText;
import eclox.core.doxyfiles.Setting;



/**
 * Implements a	doxyfile parser.
 * 
 * @author gbrocker
 */
public class Parser {

    /**
     * the current line number
     */
    private int lineNumber = 0;
    
    /**
     * The line reader used to parse the input stream.
     */
    private BufferedReader reader;
    
    /**
     * The comment line pattern.
     */
    private Pattern commentPattern = Pattern.compile("#.*");
    
    /**
     * the empty line pattern
     */
    private Pattern emptyPattern = Pattern.compile("\\s*");
    
    /**
     * the setting assignement pattern
     */
    private Pattern settingAssignmentPattern = Pattern.compile("(\\w+)\\s*=\\s*(.*?)\\s*(\\\\)?");
    
    /**
     * the setting increment pattern
     */
    private Pattern settingIncrementPattern = Pattern.compile("(\\w+)\\s*\\+=\\s*(.*?)\\s*(\\\\)?");
    
    /**
     * the continued setting assignement pattern
     */
    private Pattern continuedSettingAssignmentPattern = Pattern.compile("\\s*(.+?)\\s*(\\\\)?");
    
    /**
     * the include directive pattern
     */
    private Pattern includePattern = Pattern.compile("@INCLUDE\\s*=\\s*(.*)");
    
    /**
     * the include path pattern
     */
    private Pattern includePathPattern = Pattern.compile("@INCLUDE_PATH\\s*=\\s*(.*)");
    
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
        this.lineNumber = 0;
        
        // Reads and parses all lines.
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
        matcher = emptyPattern.matcher( line );
        if( matcher.matches() == true ) {
        	this.processAnyLine( doxyfile, line );
            return;
        }
        
        // Matches the current line against the comment pattern.
        matcher = commentPattern.matcher( line );
        if( matcher.matches() == true ) {
        	this.processAnyLine( doxyfile, line );
            return;
        }
        
        // Matches the current line against the setting assignment pattern.
        matcher = settingAssignmentPattern.matcher( line );
        if( matcher.matches() == true ) {
            // Retrieves the setting identifier and its values.
            String  identifier = matcher.group(1);
            String  values = matcher.group(2);
            String  continued = matcher.group(3);
            
            // Call the traitement for the setting assignment and pull out.
            this.processSettingAssignment( doxyfile, identifier, values, continued != null );
            return;
        }
        
        // Matches the current line against the setting increment pattern.
        matcher = settingIncrementPattern.matcher( line );
        if( matcher.matches() == true ) {
            // Retrieves the setting identifier and its values.
            String  identifier = matcher.group(1);
            String  values = matcher.group(2);
            String  continued = matcher.group(3);
            
            // Call the traitement for the setting assignment and pull out.
            this.processSettingIncrement( doxyfile, identifier, values, continued != null );
            return;
        }

        // Matches the current line agains the include directive pattern.
        matcher = includePattern.matcher( line );
        if( matcher.matches() == true ) {
        	this.processAnyLine( doxyfile, line );
        	return;
        }
        
        // Matches the current line agains the include path directive pattern.
        matcher = includePathPattern.matcher( line );
        if( matcher.matches() == true ) {
        	this.processAnyLine( doxyfile, line );
        	return;
        }
        
        // Matches the current line against the continued setting assignment pattern.
        matcher = continuedSettingAssignmentPattern.matcher( line );
        if( matcher.matches() == true ) {
            // Retrieves the setting identifier and its values.
            String  values = matcher.group(1);
            String  continued = matcher.group(2);
            
            // Call the traitement for the continued setting assignment and pull out.
            this.processContinuedSettingAssignment( doxyfile, values, continued != null );
            return;            
        }
        
        // The line has not been recognized.
        throw new IOException( "Unable to match line." );
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
    	// Retrieves the setting.
    	Setting	setting = doxyfile.getSetting( identifier );
    	if( setting == null ) {
    		setting = new Setting( identifier, value ); 
            doxyfile.append( setting );
        }
    	
    	// Updates the setting value.
    	setting.setValue( value );
    	
        // Remembers if the setting assignment is continued or not.
        continuedSetting = ( continued == true ) ? setting : null;
  	}
    
    /**
     * Processes a setting increment line.
     * 
     * @param   doxyfile    a doxyfile where the setting assignment will be stored
     * @param   identifier  a string containing the setting identifier
     * @param   value       a string containing the assigned value
     * @param   continued   a boolean telling if the setting assignement is continued on multiple line
     */
    private void processSettingIncrement( Doxyfile doxyfile, String identifier, String value, boolean continued ) throws IOException {
        // Rertieves the setting from the doxyfile.
        Setting setting = doxyfile.getSetting( identifier );
        if( setting != null ) {
            // Updates the continued setting's value.
            setting.setValue( setting.getValue() + " " + value );
            // Remembers if the setting assignment is continued or not.
            continuedSetting = ( continued == true ) ? setting : null;
        }
        else {
        		Plugin.getDefault().logWarning( "At line " + lineNumber + ": the setting was not declared before." );
            processSettingAssignment( doxyfile, identifier, value, continued );
        }
    }
    
    /**
     * Processes a setting assignment line.
     * 
     * @param   doxyfile    a doxyfile where the setting assignment will be stored
     * @param   value       a string containing the assigned value
     * @param   continued   a boolean telling if the setting assignement is continued on multiple line
     */
    private void processContinuedSettingAssignment( Doxyfile doxyfile, String value, boolean continued ) throws IOException {
        // Ensures that a continued setting has been remembered.
        if( continuedSetting == null ) {
            Plugin.getDefault().logWarning( "At line " + lineNumber + ": value delcared without a setting name." );
        }
        else {
            // Updates the continued setting's value.
            continuedSetting.setValue( continuedSetting.getValue() + " " + value );
            // Remembers if the setting assignment is continued or not.
            continuedSetting = ( continued == true ) ? continuedSetting : null;
        }
    }
}
