/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003 Guillaume Brocker

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

package eclox.data;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author GBrocker
 *
 * This class provides names for sections.
 * 
 * @todo Read section names from a file.
 */
public class SectionNameProvider {
	/**
	 * Contain all section names.
	 */
	private java.util.HashMap m_names = new java.util.HashMap();
	
	/**
	 * Constructor.
	 */
	public SectionNameProvider() {
		// Get all value classes attached to the tag names.
		try {
			InputStream			input = getClass().getResourceAsStream( "sectionNames" );
			InputStreamReader	inputReader = new InputStreamReader( input );
			BufferedReader		bufferedReader = new BufferedReader( inputReader );
			String				line = null;
			
			while( (line = bufferedReader.readLine()) != null ) {
				if( line.indexOf('#') == 0 ){
					continue;
				}
				else {
					parseSectionNameLine( line );
				}
			}
		}
		catch( Exception exception ) {
		}
	}
	
	/**
	 * Retrieve a section name for the given section header.
	 * 
	 * @param sectionHeader	A string containing the section header to match.
	 * 
	 * @return	A string containing the section name for the specified section
	 * 			header. This string can be empty if no relevant name has been 
	 * 			found.
	 */
	public final String getName( String sectionHeader ) {
		String					lowerCaseHeader = sectionHeader.toLowerCase();
		java.util.Collection	keys = m_names.keySet();
		java.util.Iterator		keyPointer = keys.iterator();
		String					result = new String();
		
		while( keyPointer.hasNext() ) {
			String	currentKey = (String) keyPointer.next();
			
			if( lowerCaseHeader.indexOf( currentKey ) != -1 ) {
				result = (String) m_names.get( currentKey );
				break;
			}
		}
		return result;
	}
	
	/**
	 * Parse the specified section name line.
	 * 
	 * @param line	A string containing the section name definition.
	 */
	private void parseSectionNameLine( String line ) {
		String	key = new String();
		String	name = new String();
		
		// Get the key.
		while( line.length() != 0 ) {
			char	head[] = new char[1];
			
			head[0] = line.charAt(0);
			if( head[0] != ' ' && head[0] != '\t' ) {
				key = key.concat( new String( head, 0, 1 ) );
				line = line.substring( 1 );
			}
			else {
				break;
			}
		}
		
		// Skip the separators.
		while( line.length() != 0 ) {
			char	head[] = new char[1];
			
			head[0] = line.charAt(0);
			if( head[0] == ' ' || head[0] == '\t' ) {
				line = line.substring( 1 );
			}
			else {
				break;
			}
		}
		
		// Get the name
		while( line.length() != 0 ) {
			char	head[] = new char[1];
	
			head[0] = line.charAt(0);
			if( head[0] != '"' ) {
				name = name.concat( new String( head, 0, 1 ) );
			}
			line = line.substring( 1 );			
		}
		
		// Update the name definition table.
		if( key.length() != 0 && name.length() != 0 ) {
			m_names.put( key, name );
		}
	}
}