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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public final static String DEFAULT_DOXYGEN_COMMAND = "doxygen";
	
	
	/**
	 * Retrieves the version string of the configured doxygen
	 * 
	 * @param	path	a string containing the command path to use or null to use the configured doxygen
	 * 
	 * @return	a string containing the doxygen version string
	 */
	public static String getVersion( String path ) throws RuntimeException {
		try {
			// Builds the command.
			String[]	command = new String[2];
			
			command[0] = path != null ? path : getCommand();
			command[1] = "--help";
			
			// Runs the command and retrieves the version string.
			Process				doxygen = Runtime.getRuntime().exec( command );
			InputStreamReader	reader = new InputStreamReader( doxygen.getInputStream() );
			char[]				buffer = new char[512];
			int					count;
			
			count = reader.read( buffer );
			
			// Matches the doxygen welcome message.
			Pattern	pattern	= Pattern.compile( "^doxygen\\s+version\\s+([\\d\\.]+).*", Pattern.CASE_INSENSITIVE|Pattern.DOTALL );
			Matcher	matcher	= pattern.matcher( new String(buffer, 0, count) );
			
			if( matcher.matches() ) {
				return matcher.group( 1 ); 
			}
			else {
				throw new RuntimeException( "The given path does not point to doxygen." );
			}
		}
		catch( Throwable t ) {
			throw new RuntimeException( t.getMessage(), t );
		}
	}
	
	/**
	 * Launch a documentation build.
	 * 
	 * @param	file	the file representing the doxygne configuration to use for the build
	 * 
	 * @return	The process that run the build.
	 */
	public static Process build( IFile file ) throws RuntimeException {
		try {
			String[]	command = new String[2];
			
			command[0] = getCommand();
			command[1] = file.getLocation().makeAbsolute().toOSString();

// TODO This is code only supported by jre >= 1.5
//			ProcessBuilder	processBuilder = new ProcessBuilder( command );
//			processBuilder.directory( getDir(file).toFile() );
//			processBuilder.redirectErrorStream( true );
//			return processBuilder.start();
			
			return Runtime.getRuntime().exec( command, null, getDir(file).toFile() );
		}
		catch(Throwable throwable) {
			throw new RuntimeException("Unable to launch Doxygen. Please check your path environment variable or edit the preferences.", throwable);
		}
	}
	
	/**
	 * Generate an empty configuration file.
	 * 
	 * @param	file	the configuration file to generate.
	 */	
	public static void generate( IFile file ) throws RuntimeException, IOException, InterruptedException, CoreException {
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
			throw new RuntimeException( errorMsg );
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
	 * 
	 * @return	a string containing the configured doxygen command
	 */
	private static String getCommand()
	{
		String command = Plugin.getDefault().getPluginPreferences().getString( IPreferences.DOXYGEN_COMMAND );
		
		return command.length() != 0 ? command : DEFAULT_DOXYGEN_COMMAND;
	}
	
}
