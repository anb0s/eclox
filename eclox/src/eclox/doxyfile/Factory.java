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
public class Factory {

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
	public Factory( java.io.InputStream input ) {
		m_in = new java.io.BufferedReader( new java.io.InputStreamReader( input ) );
	}
	
	/**
	 * Creates the doxygen settings.
	 * 
	 * @return	The doxygen settings.
	 * 
	 * @author gbrocker
	 */
	public eclox.data.Root createSettings() throws SettingsCreationError {
		eclox.data.Root		settings;
		String				nextItemText;
		eclox.data.Group	curParent;
		eclox.data.Comment	curComment;
		
		settings = new eclox.data.Root(); 
		curParent = settings;
		curComment = null;
		
		for( nextItemText = getNextItemText(); nextItemText != null; nextItemText = getNextItemText() ) {
			String	head = nextItemText.substring( 0, 2 );
						
			if( head.equals( "# " ) ) {
				eclox.data.Comment	comment = new eclox.data.Comment( nextItemText );
				
				curComment = comment; 
				curParent.addChild( comment );
			}
			else if( head.equals( "#-" ) ) {
				eclox.data.Section	section = new eclox.data.Section( nextItemText );
				 
				settings.addChild( section );
				curParent = section;
				curComment = null;
			}
			else {
				curParent.addChild( new eclox.data.Tag( nextItemText, curComment ) );
				curComment = null;
			}
		}
		
		return settings;
	}
	
	/**
	 * Retrieves the text block for the next doxygen configuration item.
	 * 
	 * @return	A string containing the next item text block, or null if the end of the doxygen
	 * 			oncifguration has been reached.
	 */
	private String getNextItemText() throws SettingsCreationError {
		String	result = null;
		
		try
		{
			String	itemText = new String();
			
			for(;;)
			{
				String	line = m_in.readLine();
	
				if( line == null )
				{
					break;
				}
				else if( line.length() == 0 && itemText.length() != 0 )
				{
					break;
				}
				else if( line.length() != 0 )
				{
					if( itemText.length() != 0 )
					{
						itemText += "\r\n";
					}
					itemText += line;
				}
			}
			if( itemText.length() != 0 )
			{
				result = itemText;
			}
		}
		catch( java.io.IOException ioException )
		{
			throw new SettingsCreationError( ioException );
		}
		
		return result;
	}
}
