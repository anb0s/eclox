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
package eclox.core.doxyfiles;

import eclox.core.doxyfiles.Chunk;



/**
 * Implements a raw text chunk.
 * 
 * @author willy
 */
public class RawText extends Chunk {

	/**
	 * a string containing the raw text piece
	 */
	private String content = new String();
	
	/**
	 * Appends a new piece of text to the raw text chunk.
	 * 
	 * @param	text	a string containing a piece of text to append
	 */
	public void append( String text ) {
		content += text;
	}
	
	/**
	 * Retrieves the raw text content as a string.
	 * 
	 * @return	a string containing the raw text content
	 */
	public String toString() {
		return new String( content );
	}
	
}
