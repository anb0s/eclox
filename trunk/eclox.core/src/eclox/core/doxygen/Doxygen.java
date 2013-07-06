/*******************************************************************************
 * Copyright (C) 2003-2008, 2013, Guillaume Brocker
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/ 

package eclox.core.doxygen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import eclox.core.IPreferences;
import eclox.core.Plugin;

/**
 * Implements the abstract doxygen frontend. Sub-classes provides concret
 * doxygen access.
 * 
 * @author gbrocker
 */
public abstract class Doxygen {
	
	/**
	 * a string containing defining the default doxygen command to use
	 */
	protected final static String COMMAND_NAME = "doxygen";
		
	
	/**
	 * Retrieves the default doxygen instance to use.
	 */
	public static Doxygen getDefault() {
		final String	identifier	= Plugin.getDefault().getPluginPreferences().getString( IPreferences.DEFAULT_DOXYGEN );
		Doxygen			doxygen		= null;
		
		// Try the creation of a default doxygen instance.
		doxygen = DefaultDoxygen.createFromIdentifier(identifier);
		if( doxygen != null ) {
			return doxygen;
		}

		// Try the creation of a custom doxygen instance.
		doxygen = CustomDoxygen.createFromIdentifier(identifier);
		if( doxygen != null ) {
			return doxygen;
		}
		
		// Try the creation of a bundled doxygen instance.
		doxygen = BundledDoxygen.createFromIdentifier(identifier);
		if( doxygen != null ) {
			return doxygen;
		}
		
		// No doxygen could be created.
		return null;	
	}
	
	
	/**
	 * Retrieves the version string of wrapped doxygen.
	 * 
	 * @return	a string containing the doxygen version string
	 */
	public String getVersion() {
		try {
			// Builds the command.
			String[]	command = new String[2];
			
			command[0] = getCommand();
			command[1] = "--help";
			
			// Runs the command and retrieves the version string.
			Process				doxygen	= Runtime.getRuntime().exec( command );
			BufferedReader		input	= new BufferedReader( new InputStreamReader(doxygen.getInputStream()) );
			BufferedReader		error	= new BufferedReader( new InputStreamReader(doxygen.getErrorStream()) );
			
			// Matches the doxygen welcome message.
			Pattern	pattern	= Pattern.compile( "^doxygen\\s+version\\s+([\\d\\.]+).*", Pattern.CASE_INSENSITIVE|Pattern.DOTALL );
			Matcher	matcher	= pattern.matcher( input.readLine() );
			
			if( matcher.matches() ) {
				return matcher.group( 1 ); 
			}
			else {
				String	errorMessage = new String();
				String	line;
				while( (line = error.readLine()) != null ) {
					errorMessage = errorMessage.concat(line);
				}
				
				throw new RuntimeException( "Unable to get doxygen version. " + errorMessage );
			}
		}
		catch( Throwable t ) {
			Plugin.log( t );
			return null;
		}
	}
	
	/**
	 * Launch a documentation build.
	 * 
	 * @param	file	the file representing the doxygen configuration to use for the build
	 * 
	 * @return	The process that run the build.
	 */
	public Process build( IFile file ) throws InvokeException, RunException {
		if( file.exists() == false ) {
			throw new RunException("Missing or bad doxyfile");
		}
		
		try {
			String[]	command = new String[3];
			
			command[0] = getCommand();
			command[1] = "-b";
			command[2] = file.getLocation().makeAbsolute().toOSString();

// TODO This is code only supported by jre >= 1.5
//			ProcessBuilder	processBuilder = new ProcessBuilder( command );
//			processBuilder.directory( getDir(file).toFile() );
//			processBuilder.redirectErrorStream( true );
//			return processBuilder.start();
			
			return Runtime.getRuntime().exec( command, null, getDir(file).toFile() );
		}
		catch(IOException ioException) {
			throw new InvokeException(ioException);
		}
	}
	
	/**
	 * Generate an empty configuration file.
	 * 
	 * @param	file	the configuration file to generate.
	 */	
	public void generate( IFile file ) throws InvokeException, RunException {
		try
		{
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
				throw new RunException( errorMsg );
			}
			
			// Force some refresh to display the file.
			file.refreshLocal( 0, null );
		}
		catch( RunException runException ) {
			throw runException;
		}
		catch( SecurityException securityException ) {
			throw new InvokeException(securityException);
		}
		catch( IOException ioException ) {
			throw new InvokeException(ioException);
		}
		catch( Throwable throwable ) {
			Plugin.log(throwable);
		}
	}
	
	
	/**
	 * Retrieves the string containing the command line to the doxygen binary.
	 * Sub-classes must implement this method.
	 * 
	 * @return	a string containing the path to the doxygen binary file
	 */
	public abstract String getCommand();
	
	
	/**
	 * Retrieves the description string of the doxygen wrapper instance.
	 * 
	 * @return	a string containing the description of the dowygen wrapper
	 */
	public abstract String getDescription();
	
	
	/**
	 * Retrieves the identifier of the doxygen wrapper.
	 * 
	 * @return	a string containing the doxygen wrapper identifier
	 */
	public abstract String getIdentifier();
	
	
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
