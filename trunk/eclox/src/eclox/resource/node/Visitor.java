/*
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

package eclox.resource.node;

/**
 * Defines the node visitor interface.
 * 
 * @author gbrocker
 */
public interface Visitor {
	/**
	 * Process the specified doxyfile.
	 * 
	 * @param	doxyfile	the doxyfile to process
	 */
	public void process( Doxyfile doxyfile );
	
	/**
	 * Process the specified section.
	 * 
	 * @param	section	the section to process
	 */
	public void process( Section section );
	
	/**
	 * Process the specified tag.
	 * 
	 * @param	tag	the tag to process
	 */
	public void process( Tag tag );
	
	/**
	 * Process the specified note.
	 * 
	 * @param	note	the note to process
	 */
	public void process( Note note );
	
	/**
	 * Process the specified value.
	 * 
	 * @param	value	the value to process
	 */
	public void process( Value value );
}
