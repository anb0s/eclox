/**
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2004 Guillaume Brocker

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eclox.doxyfile.node.Group;
import eclox.doxyfile.node.Visitor;
import eclox.doxyfile.node.VisitorException;


/**
 * Implements the doxygen settings root node.
 * 
 * @author gbrocker
 */
public class Doxyfile extends Group {
	/**
	 * Tell if the specified file name is a valid doxyfile name.
	 * 
	 * @param	name	The doxyfile name to test.
	 * 
	 * @return	true or alse.
	 */
	public static boolean isFileNameValid(String name) {
		Pattern pattern = Pattern.compile(".*doxyfile.*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(name);
					
		return matcher.matches();
	}
	
	/**
	 * Implement the visitor acceptence.
	 * 
	 * @param	visitor	The visitor to accept.
	 */
	public void accept(Visitor visitor) throws VisitorException {
		visitor.process( this );
	}
	
	/**
	 * Retrieve the node text representation.
	 * 
	 * @return	A string containing the node text representation.
	 */
	public String toString() {
		return new String();
	}
}
