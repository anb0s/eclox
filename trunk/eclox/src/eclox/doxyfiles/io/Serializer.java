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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import eclox.doxyfiles.Chunk;
import eclox.doxyfiles.Doxyfile;

/**
 * Implements a doxyfile content serializer.
 * 
 * @author willy
 */
public class Serializer extends InputStream {
	
	/**
	 * a doxyfile that being serialized
	 */
	private Doxyfile doxyfile;
	
	/**
	 * an iterator on the doxyfile chunks
	 */
	private Iterator chunkIterator;
	
	/**
	 * a string buffer containing the next character to red
	 */
	private StringBuffer stringBuffer;
	
	/**
	 * Constructor
	 * 
	 * @param	doxyfile	a doxyfile to serialize
	 */
	public Serializer( Doxyfile doxyfile ) {
		this.doxyfile = doxyfile;
		this.chunkIterator = doxyfile.iterator();
		this.stringBuffer = getNextStringBuffer();
	}

	public int read() throws IOException {
		int	result;
		if( stringBuffer != null ) {
			// Retrieves the next character from the current string buffer.
			result = stringBuffer.charAt( 0 );
			stringBuffer.deleteCharAt( 0 );
			
			// If the current string buffer has been entierly read, gets the next string buffer.
			if( stringBuffer.length() == 0 ) {
				stringBuffer = getNextStringBuffer();
			}
		}
		else {
			result = -1;
		}
		return result;
	}
	
	/**
	 * Retrieves the next string buffer to use for reading operations or null
	 * if no more chunk is left in the doxyfile.
	 * 
	 * @return	a string buffer or null of none
	 */
	private StringBuffer getNextStringBuffer() {
		StringBuffer result = null;
		if( this.chunkIterator.hasNext() == true ) {
			Chunk	chunk = (Chunk) this.chunkIterator.next();
			result = new StringBuffer( chunk.toString() );
		}
		return result;
	}

}
