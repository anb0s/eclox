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


/**
 * Defines all constants for the buld subsystem.
 * 
 * @author gbrocker
 */
public final class Build {
	/**
	 * Defines the ready state constant.
	 */
	public static final int STATE_READY = 0;

	/**
	 * Defines the running state constant.
	 */
	public static final int STATE_RUNNING = 1;

	/**
	 * Defines the stopped state constant.
	 */
	public static final int STATE_STOPPED = 2;

	/**
	 * Defines the ended state constant.
	 */
	public static final int STATE_ENDED = 3;
 
	/**
	 * Defines the STANDARD output contant.
	 */
	public static final int STANDARD_OUTPUT = 4;

	/**
	 * Defines the ERROR output contant.
	 */
	public static final int ERROR_OUTPUT = 5;
}
