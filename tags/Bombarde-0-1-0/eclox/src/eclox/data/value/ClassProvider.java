/*
	eclox
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

package eclox.data.value;

/**
 * Provide type objects for tags.
 * 
 * @author gbrocker
 */
public class ClassProvider {
	/**
	 * Associate tag names to types.
	 */
	private java.util.HashMap m_types = new java.util.HashMap();
	
	/**
	 * Constructor.
	 * 
	 * @todo	Exception handling.
	 */
	public ClassProvider() {
		// Get all value classes attached to the tag names.
		try {
			java.io.InputStream			input = getClass().getResourceAsStream( "classes" );
			java.io.InputStreamReader	inputReader = new java.io.InputStreamReader( input );
			java.io.BufferedReader		bufferedReader = new java.io.BufferedReader( inputReader );
			java.lang.String			line = null;
			java.lang.String			packageName = getClass().getPackage().getName();

			while( (line = bufferedReader.readLine()) != null ) {
				// Skip comment lines
				if( line.indexOf('#') == 0 ){
					continue;
				}
				// Process non empty lines
				else {
					java.lang.String[]	tokens;
				
					tokens = line.split( "[ \t]+" );				
					if( tokens.length == 2 ) {
						java.lang.String	tagName = tokens[0];
						java.lang.String	valueClassName = packageName + "." + tokens[1];
						java.lang.Class		valueClass = java.lang.Class.forName( valueClassName );
	 
						m_types.put( tagName, valueClass );
					}
				}
			}
		}
		catch( ClassNotFoundException classNotFound ) {
		}
		catch( Exception exception ) {
		}
	}
	
	/**
	 * Retrieve a value class for the specified tag.
	 *
	 * @param	tag	A string containing the name of the tag for which the value class must be retrieved.
	 * 
	 * @todo	Log all tags that were not found.
	 */
	public java.lang.Class getValueClass( java.lang.String tag ) {
		java.lang.Class	result;
		
		// Look-up in the loaded value types.
		result = (java.lang.Class) m_types.get( tag );
		// If not found, set the default value.
		if( result == null ) {
			result = eclox.data.value.String.class;
		}
		
		return result;
	}
}
