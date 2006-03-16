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


/**
 * Implements the default item that can be containined in a doxyfile.
 * 
 * A chunk is a piece of text extracted from e doxyfile. It can represent comments,
 * empty lines or whatever.
 * 
 * @author willy
 */
public abstract class Chunk {
	
	/**
	 * the chunk owner (aka the doxyfile)
	 */
	private Doxyfile owner;
	
	/**
	 * Retrieves the chunk owner.
	 * 
	 * @return	the chunk owner
	 */
	public Doxyfile getOwner() {
		return owner;
	}
	
	/**
	 * Updates the chunck owner
	 * 
	 * @param	owner	the new owner
	 */
	public void setOwner( Doxyfile owner ) {
		this.owner = owner; 
	}
	
	/**
	 * Converts the chunk into a string representing its content.
	 * 
	 * @return	a string containing the chunk content
	 */
	public abstract String toString();
	
}
