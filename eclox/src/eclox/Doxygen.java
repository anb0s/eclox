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

package eclox;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Implement the doxygen frontend.
 * 
 * @author gbrocker
 */
public final class Doxygen {
	/**
	 * Launch a documentation build.
	 * 
	 * @param	file	The file representing the doxygne configuration to use for the build.
	 * 
	 * @return	The process that run the build.
	 */
	public static Process build( IFile file ) throws IOException {
		IPath		workDir = getDir( file ); 
		String[]	command = new String[2];

		command[0] = getBuilderCommand();
		command[1] = file.getLocation().makeAbsolute().toOSString();
		return Runtime.getRuntime().exec( command, null, workDir.toFile() );
	}
	
	/**
	 * Generate an empty configuration file.
	 * 
	 * @param	file	The configuration file to generate.
	 */	
	public static void generate( IFile file ) throws IOException, InterruptedException, CoreException {
		Process	process;
		String	command;
		
		command = getBuilderCommand() + " -g \"" + file.getLocation().toOSString() + "\"";
		process = Runtime.getRuntime().exec( command );
		process.waitFor();
		file.refreshLocal( 0, null );
	}
	
	/**
	 * Retrieves the command for the doxygen builder.
	 * 
	 * @return	A string containing the bulder command.
	 */
	private static String getBuilderCommand() {
		IPreferenceStore	store = eclox.ui.Plugin.getDefault().getPreferenceStore();
		String				command = store.getString( Preferences.COMPILER_PATH );
	
		if( command.length() == 0 ) {
			command = "doxygen";
		}
	
		return command;
	}
	
	/**
	 * Retrieve the diretory of the specified file.
	 * 
	 * @param	file	The file for which the directory must be retrieved.
	 * 
	 * @return	The path of the containing directory.
	 */
	private static IPath getDir( IFile file ) {
		return file.getLocation().makeAbsolute().removeLastSegments( 1 );
	}
}
