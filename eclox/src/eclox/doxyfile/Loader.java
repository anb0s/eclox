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

package eclox.doxyfile;

/**
 * Creates doxygen setting.
 * 
 * @author gbrocker
 */
public class Loader {

	/**
	 * The input stream used for the settings construction.
	 */
	private java.io.BufferedReader m_in;
	
	/**
	 * Constructor.
	 * 
	 * @param	input	The input stream from which the settings must be constructed.
	 *  
	 * @author gbrocker
	 */
	public Loader( java.io.InputStream input ) {
		m_in = new java.io.BufferedReader( new java.io.InputStreamReader( input ) );
	}
	
	/**
	 * Load a doxygen file.
	 * 
	 * @return	The doxygen settings.
	 * 
	 * @author gbrocker
	 */
	public eclox.doxyfile.node.Doxyfile load() throws SettingsCreationError {
		eclox.doxyfile.node.Doxyfile	doxyfile;
		String							nextItemText;
		eclox.doxyfile.node.Group		curParent;
		
		doxyfile = new eclox.doxyfile.node.Doxyfile(); 
		curParent = doxyfile;
		
		for( nextItemText = getNextItemText(); nextItemText != null; nextItemText = getNextItemText() ) {
			String	head = nextItemText.substring( 0, 2 );
						
			if( head.equals( "# " ) ) {
				eclox.doxyfile.node.Comment	comment = new eclox.doxyfile.node.Comment( nextItemText );
				
				curParent.addChild( comment );
			}
			else if( head.equals( "#-" ) ) {
				eclox.doxyfile.node.Section	section = new eclox.doxyfile.node.Section( nextItemText );
				 
				doxyfile.addChild( section );
				curParent = section;
			}
			else {
				curParent.addChild( new eclox.doxyfile.node.Tag( nextItemText ) );
			}
		}
		
		return doxyfile;
	}
	
	/**
	 * Retrieves the text block for the next doxyfile item.
	 * 
	 * @return	A string containing the next item item, or null if the end of the doxyfile
	 *			has been reached.
	 */
	private String getNextItemText() throws SettingsCreationError {
		String	itemText = null;

		for(;;)
		{
			String line = readLine();
						
			// We have reached the file end.
			if( line == null )
			{
				break;
			}
			// We are on an empty line, so the text item has been retrieved.	
			else if( line.length() == 0 )
			{
				break;
			}
			else
			{
				break;
			}
		}
			
		return itemText;
	}
	
	/**
	 * @brief	Retrieve a line of text from the input stream.
	 *
	 * @return	A string containing the text line or null if the stream end
	 * 			has been reached.
	 */
	private String readLine() throws SettingsCreationError {
		try
		{
			String line = m_in.readLine();
			
			if( line != null )
			{
				line = line.concat( "\r\n" );
			}
			return line;
		}
		catch( java.io.IOException ioException )
		{
			throw new SettingsCreationError( ioException );
		}
	}
}
