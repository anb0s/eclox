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

package eclox.core.doxygen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import eclox.core.IPreferences;
import eclox.core.Plugin;


/**
 * Implement the doxygen frontend.
 * 
 * @author gbrocker
 */
public final class Doxygen {
	
	/**
	 * a string containing defining the default doxygen command to use
	 */
	private final static String DEFAULT_DOXYGEN_COMMAND = "doxygen";
	
	
	/**
	 * Launch a documentation build.
	 * 
	 * @param	file	the file representing the doxygne configuration to use for the build
	 * 
	 * @return	The process that run the build.
	 */
	public static Process build( IFile file ) throws DoxygenException {
		try {
			String[]	command = new String[2];
			
			command[0] = getCommand();
			command[1] = file.getLocation().makeAbsolute().toOSString();
			
			ProcessBuilder	processBuilder = new ProcessBuilder( command );
			processBuilder.directory( getDir(file).toFile() );
			processBuilder.redirectErrorStream( true );
			return processBuilder.start();
		}
		catch(Throwable throwable) {
			throw new DoxygenException("Unable to launch Doxygen. Please check your path environment variable or edit the preferences.", throwable);
		}
	}
	
	/**
	 * Generate an empty configuration file.
	 * 
	 * @param	file	the configuration file to generate.
	 */	
	public static void generate( IFile file ) throws DoxygenException, IOException, InterruptedException, CoreException {
		Process		process;
		String[]	command = new String[3];
		
		// Build the command. 
		command[0] = getCommand();
		command[1] =  "-g";
		command[2] = file.getLocation().toOSString();
		
		// Run the command and check for errors.
		process = Runtime.getRuntime().exec( command, null );
		if(process.waitFor() != 0) {
			BufferedReader	reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String			errorMsg = new String();
			String			line;
			
			for(line=reader.readLine(); line != null; line=reader.readLine()) {
				errorMsg = errorMsg.concat(line);
			}
			throw new DoxygenException(errorMsg);
		}
		
		// Force some refresh to display the file.
		file.refreshLocal( 0, null );
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
	
	
	/**
	 * Retrieves the doxygen command to use.
	 */
	private static String getCommand()
	{
		String command = Plugin.getDefault().getPluginPreferences().getString( IPreferences.DOXYGEN_COMMAND );
		
		return command.length() != 0 ? command : DEFAULT_DOXYGEN_COMMAND;
	}
	
}
