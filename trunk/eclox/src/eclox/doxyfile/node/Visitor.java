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


package eclox.doxyfile.node;


/**
 * @author gbrocker
 * 
 * Interface for all visitors.
 */
public interface Visitor {
	/**
	 * Process a comment item.
	 * 
	 * @param comment	The comment to process.
	 */
	public void process( Comment comment ) throws VisitorException;
	
	/**
	 * Process a group item.
	 * 
	 * @param group	The group to process.
	 */
	public void process( Section group ) throws VisitorException;
	
	/**
	 * Process a tag item.
	 * 
	 * @param tag	The tag to process.
	 */
	public void process( Tag tag ) throws VisitorException;
	
	/**
	 * Process a settings item.
	 *
	 * @param	settings	The settings to process.
	 */
	public void process( Doxyfile settings ) throws VisitorException;
}
