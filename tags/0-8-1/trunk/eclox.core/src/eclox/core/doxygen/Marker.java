/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2006, 2008, Guillaume Brocker
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

package eclox.core.doxygen;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * Defines some constant used with markers resulting from a build.
 * 
 * @author gbrocker
 */
public class Marker {

	private static final String SEVERITY_WARNING = "warning";	///< Defines the warning severity string.
	private static final String SEVERITY_ERROR = "error";		///< Defines the warning severity string.

	public static final String DOXYGEN_MARKER = "org.gna.eclox.core.doxygen.marker";	///< Defines the doxygen marker type attribute name
	public static final String SETTING = "org.gna.org.core.doxygen.marker.setting";		///< Defines the optional attribute name that hold the name of a setting 
	
	
	/**
	 * Creates a single marker for the given file.
	 * 
	 * If @c file is null, no marker will be created.
	 * 
	 * @param	file		a resource file to create a marker for
	 * @param	line		a line number
	 * @param	message		a message explaining the problem
	 * @param	severity	a severity level
	 */
	public static IMarker create( IFile file, int line, String message, int severity ) throws CoreException
	{
		IMarker	marker = null;
		
		if( file != null )
		{
			marker = file.createMarker( Marker.DOXYGEN_MARKER );
			
			marker.setAttribute( IMarker.MESSAGE, message );
			marker.setAttribute( IMarker.LINE_NUMBER, line );
			marker.setAttribute( IMarker.LOCATION, file.getProjectRelativePath().toPortableString() );
			marker.setAttribute( IMarker.PRIORITY, IMarker.PRIORITY_NORMAL );
			marker.setAttribute( IMarker.SEVERITY, severity );
		}
		
		return marker;
	}
	
	/**
	 * Creates a single marker for the given file.
	 * 
	 * If @c file is null, no marker will be created.
	 * 
	 * @param	file		a resource file to create a marker for
	 * @param	setting		a string containing the name of a setting
	 * @param	line		a line number
	 * @param	message		a message explaining the problem
	 * @param	severity	a severity level
	 */
	public static IMarker create( IFile file, String setting, int line, String message, int severity ) throws CoreException
	{
		IMarker	marker = create( file, line, message, severity );
		
		if( marker != null )
		{
			marker.setAttribute( SETTING, setting);
		}
		
		return marker;
	}
	
	
	/**
	 * Retrieves the marker severity from the given text. The given
	 * text may be warning or error.
	 * 
	 * @param severity	a string to convert to a marker severity
	 * 
	 * @return the marker severity value
	 */
	public static int toMarkerSeverity( String severity )
	{
		if( severity.compareToIgnoreCase(SEVERITY_ERROR) == 0 ) {
			return IMarker.SEVERITY_ERROR;
		}
		else if( severity.compareToIgnoreCase(SEVERITY_WARNING) == 0 ) {
			return IMarker.SEVERITY_WARNING;
		}
		else {
			return IMarker.SEVERITY_ERROR;
		}
	}


	
}
