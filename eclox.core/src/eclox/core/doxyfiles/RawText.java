/*******************************************************************************
 * Copyright (C) 2003-2004, 2013, Guillaume Brocker
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/ 

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
