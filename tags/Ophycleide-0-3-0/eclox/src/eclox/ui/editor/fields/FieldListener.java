/*
	eclox
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

package eclox.ui.editor.fields;

import java.util.EventListener;

/**
 * Define the interface for any field listener.
 * 
 * @author gbrocker
 */
public interface FieldListener extends EventListener {
	
	/**
	 * Notify the listener that the field edition has been canceled.
	 * 
	 * @param	event	The event to process.
	 */
	public void fieldEditionCanceled( FieldEvent event );
	
	/**
	 * Notify the listener that the field edition has been completed.
	 * 
	 * @param	event	The event to process.
	 */
	public void fieldEditionCompleted( FieldEvent event );
}
