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


package eclox.build;

import java.util.EventObject;

/**
 * Implements the build event class.
 * 
 * @author gbrocker
 */
public class BuildEvent extends EventObject {
	/**
	 * The builder that is concerned by the event. 
	 */
	public Builder builder;
	
	/**
	 * The value carried by the event.
	 */
	public final Object value;
	
	/**
	 * Constructor.
	 *
	 * @param	source	The bulder that is concerned by the event. 
	 */
	public BuildEvent( Builder source ) {
		super( source );
		this.builder = source;
		this.value = null;
	}
	
	/**
	 * Constructor.
	 *
	 * @param	source	The bulder that is concerned by the event. 
	 * @param	value	The value that is carried by the event. The object type depends on the event name.
	 */
	public BuildEvent( Builder source, Object value ) {
		super( source );
		this.builder = source;
		this.value = value;
	}
}
