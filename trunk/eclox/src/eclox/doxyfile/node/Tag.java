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

package eclox.doxyfile.node;

/**
 * Implements a doxygen setting tag.
 * A tag is of the form NAME = VALUE.
 * 
 * @author gbrocker
 */
public class Tag extends Leaf {
	/**
	 * Implement a value listener.
	 */
	private class ValueListener implements eclox.doxyfile.node.value.Listener {
		/**
		 * Process a value change event.
		 * 
		 * @param	event	The event to process.
		 */	
		public void valueChanged( eclox.doxyfile.node.value.Event event ) {
			setDirtyInternal();
		}
	}
	
	/**
	 * The value class provider.
	 */
	private static eclox.doxyfile.node.value.ClassProvider m_valueClassProvider = new eclox.doxyfile.node.value.ClassProvider();
	
	/**
	 * The maximum '=' character offset over all tag lines.
	 * This is used to reprodure a doxygen configuration file output as close as possible
	 * to the original file (generated by doxygen itself). 
	 */
	private static int m_equalOffset = 0;
	 
	/**
	 * The tag name.
	 */
	private String m_name;
	
	/**
	 * The tag value.
	 */
	private eclox.doxyfile.node.value.Abstract m_value;
	
	/**
	 * Constructor.
	 * 
	 * @param	text	The text of the tag.
	 */
	public Tag( final String text ){
		String	tokens[] = text.split( "[\t =]+", 2 );
		
		m_name = tokens[0];
		m_value = createValueInstance( m_name, tokens.length == 2 ? tokens[1] : "" );
		m_value.addListener( new ValueListener() );
		updateEqualOffset( text );	
	}

	/**
	 * Make the tag accept the specified visitor.
	 * 
	 * @param	visitor	The visitor to accept.
	 */
	public void accept( Visitor visitor ) throws VisitorException {
		visitor.process( this );
	}
	
	/**
	 * Retrieves the tag name.
	 * 
	 * @return	A string containing the tag name.
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Retrieves the tag value.
	 * 
	 * @return	A string containing the tag value.
	 */
	public eclox.doxyfile.node.value.Abstract getValue() {
		return m_value;
	}
	
	/**
	 * Retrieve the tag text representation.
	 * 
	 * @return	A string containing the tag text representation.
	 */
	public String toString() {
		String	result;
		int		spaceCount;
		
		spaceCount = m_equalOffset - m_name.length();
		result = new String();
		result += m_name;
		while( spaceCount > 0 ) {
			result += " ";
			spaceCount--;
		} 
		result += "= ";
		result += m_value.toString();
		result += "\r\n";
		return result;
	}
	
	/**
	 * Retrieve the value instance for the specified tag name and value text.
	 * 
	 * @param name	A string containing the tag name.
	 * @param value	A string containing the value text.
	 *  
	 * @return	An value instance.
	 */
	private static eclox.doxyfile.node.value.Abstract createValueInstance( String name, String value ) {
		eclox.doxyfile.node.value.Abstract	result;
	
		try {
			Class	valueClass = m_valueClassProvider.getValueClass( name );
		
			result = (eclox.doxyfile.node.value.Abstract) valueClass.newInstance();
			result.fromString( value.replaceAll("[\r\n]*", "") );	
		}
		catch( java.lang.Exception exception ) {
			result = null;
		}
		return result;
	}
	
	/**
	 * The update the '=' character offset.
	 * 
	 * @param	text	The tag text to parse.
	 */
	private static void updateEqualOffset( String text ) {
		int	offset;
		
		offset = text.indexOf( '=' );
		if( offset != -1 && offset > m_equalOffset ) {
			m_equalOffset = offset; 
		}
	}
}
