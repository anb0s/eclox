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


package eclox.build;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;

import eclox.Doxygen;
import eclox.util.ListenerManager;

/**
 * Implements the build wrapper class.
 * 
 * @author gbrocker
 */
public class Builder extends ListenerManager {
	/**
	 * Implements a build monitor that wtahc for the build process lifetime.
	 * 
	 * @author gbrocker
	 */
	private class LifeMonitor implements Runnable {
		/**
		 * Constructor.
		 */
		public LifeMonitor() {
			fireBuildStarted();
			launch( this );
		}
	
		/**
		 */
		public void run() {
			try {
				int		exitValue = m_buildProcess.exitValue();
			
				if( m_state == Build.STATE_STOPPED ) {
					fireBuilStopped();
				}
				else {
					setState( Build.STATE_ENDED );
					fireBuildEnded();
				}
			}
			catch( IllegalThreadStateException illegalThreadException ) {
				// Relaunch this runnable
				launch( this );
			}
			catch( Throwable throwable ) {
			}
		}
	}
	
	/**
	 * Implements build monitor responsible to monitor build process output text.
	 *
	 * @author gbrocker
	 */
	private class OutputMonitor implements Runnable {
		/**
		 * The input stream to monitor.
		 */
		private InputStream		m_in = null;
	
		/**
		 * The input stream reader.
		 */
		private	BufferedReader	m_reader = null;
	
		/**
		 * The monitored output type.
		 */
		private int m_outputType;
	 
		/**
		 * Constructor.
		 *  
		 * @param outputType	The output to monitor.
		 */
		public OutputMonitor( int outputType ) {
		
			// Get the relevant input stream to monitor.
			switch( outputType ){
			case Build.ERROR_OUTPUT:
				m_in = m_buildProcess.getInputStream();
				break;
			case Build.STANDARD_OUTPUT:
				m_in = m_buildProcess.getErrorStream();
				break;
			}
			m_reader = new BufferedReader( new InputStreamReader( m_in ) );
			m_outputType = outputType;
			launch( this );
		}
	
		/**
		 * Perform the runtime treatement.
		 */
		public void run() {
			try {
				if( m_in.available() != 0 && m_reader.ready() ) {
					String	line = m_reader.readLine();
				
					fireBuildOutputChanged( new String( line + "\r\n" ) );
				}
			
				// Check if the monitor should relaunch itself.
				int	builderState = m_state;
		
				if( builderState == Build.STATE_READY || builderState == Build.STATE_RUNNING ) {
					launch( this );
				}
			}
			catch( Throwable throwable ) {
				eclox.ui.Plugin.getDefault().showError( throwable );
			}
		}
	}

	/**
	 * The current instance of the builder.
	 */
	static private Builder m_currentBuilder = null;
	
	/**
	 * The build process. Set to null if none is running. 
	 */
	private Process	m_buildProcess = null;
	
	/**
	 * The current builder state.
	 */
	private int m_state = Build.STATE_READY;
	
	/**
	 * Constructor.
	 */
	private Builder() {
		super( BuildListener.class );		
	}
	
	/**
	 * Registers the specified listener.
	 * 
	 * @param	listener	The listener to register.
	 */
	public void addListener( BuildListener listener ) throws BuildInProgressError {
		super.addListener( listener );
	}
	
	/**
	 * Unregister the specified listener.
	 * 
	 * @param	listener	The listener to remove.
	 */
	public void removeListener( BuildListener listener ) throws BuildInProgressError {
		super.removeListener( listener );
	}
	
	/**
	 * Retrieves the current bulder instance.
	 * 
	 * @return	The current builder instance.
	 */
	public static Builder getDefault() {
		if( m_currentBuilder == null ) {
			m_currentBuilder = new Builder();
		}
		return m_currentBuilder;
	}
	
	/**
	 * Tell if the builder is running or not.
	 * 
	 * @return	true if the builder is running, false otherwise.
	 */
	public boolean isRunning() {
		return this.m_state == Build.STATE_RUNNING;
	}
	
	/**
	 * Start the build process.
	 * 
	 * @param	file	The doxygen configuration to use for the build.
	 *
	 * @exception	BuildFaildException	The build could not be started.
	 */
	public void start( IFile file ) throws BuildFailedException {
		if( m_state != Build.STATE_RUNNING ) {
			try {
				m_buildProcess = Doxygen.build( file );
				m_state = Build.STATE_RUNNING;
				
				new OutputMonitor( Build.ERROR_OUTPUT );
				new OutputMonitor( Build.STANDARD_OUTPUT );
				new LifeMonitor();
				
				BuildHistory.getDefault().log(file);
			}
			catch( IOException ioException ) {
				throw new BuildFailedException( "Unable to launch the documentation compilation. Ensure that doxygen is available in the system path or check the compiler path in the preferences.", ioException );
			}
		}
	}
	
	/**
	 * Stop the build process, even if it is under progress.
	 */
	public void stop() {
		if( m_state == Build.STATE_RUNNING ) {
			m_state = Build.STATE_STOPPED;
			m_buildProcess.destroy();
		}
	}
	
	/**
	 * Notify listeners that a build as started.
	 */
	private void fireBuildStarted() {
		super.fireEvent( new BuildEvent( this ), "buildStarted" );
	}
	
	/**
	 * Notify the listeners that a build has been stopped.
	 */
	private void fireBuilStopped() {
		super.fireEvent( new BuildEvent( this ), "buildStopped" );
	}
	
	/**
	 * Notify listeners that a build has been ended.
	 */
	private void fireBuildEnded() {
		super.fireEvent( new BuildEvent( this ), "buildEnded" );
	}
	
	private void fireBuildOutputChanged( String output ) {
		super.fireEvent( new BuildEvent( this, new String( output )), "buildOutputChanged" );
	}
	
	/**
	 * Set the builder state to the specified value.
	 * 
	 * @param state	The new bulder state.
	 */
	private void setState( int state ) {
		m_state = state;
	}
	
	/**
	 * Launch a runnable object into the workbensh.
	 * 
	 * @param	runnable	The runnable object to run.
	 */
	private static void launch( Runnable runnable ) {
		Display.getDefault().asyncExec( runnable );
	}
}
