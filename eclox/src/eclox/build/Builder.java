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
	 * Implements the build monitor.
	 * 
	 * It is reponsible to watch and notify output and build process changes. 
	 */
	private class BuildProcessMonitor implements Runnable {
		/**
		 * The build process.
		 */
		private Process buildProcess;
		
		/**
		 * Constructor.
		 * 
		 * @param	process	The build process to watch. 
		 */
		public BuildProcessMonitor(Process buildProcess) {
			setState(Build.STATE_RUNNING);
			this.buildProcess = buildProcess;
			fireBuildStarted();
			
			this.launch();
		}
		
		/**
		 * Run the build process monitor routine.
		 */
		public void run() {
			try {
				this.watchBuildProcessOutput(this.buildProcess.getInputStream());
				this.watchBuildProcessOutput(this.buildProcess.getErrorStream());
				this.watchBuildProcessLife();
			}
			catch(IllegalThreadStateException exp) {
				this.launch();
			}
			catch(Throwable throwable) {
				eclox.ui.Plugin.getDefault().showError(throwable);
				this.stop();
			}
		}
		
		/**
		 * Stop the current build.
		 */
		public void stop() {
			setState(Build.STATE_STOPPED);
			this.buildProcess.destroy();
			fireBuilStopped();
		}
		
		/**
		 * Launch a runnable object into the workbensh.
		 * 
		 * @param	runnable	The runnable object to run.
		 */
		private void launch() {
			Display.getDefault().asyncExec(this);
		}
		
		/**
		 * Watch the specified build process output.
		 * 
		 * @param	input	The input stream containing the build process
		 * 					output to watch.
		 */
		private void watchBuildProcessOutput(InputStream input) throws IOException {
			int	available = input.available();
			
			if(available != 0) {
				byte buffer[] = new byte[available];
				
				input.read(buffer);
				fireBuildOutputChanged(new String(buffer));
			}				
		}
		
		/**
		 * Watch the build process life status.
		 */
		private void watchBuildProcessLife() throws IllegalThreadStateException {
			if(isRunning() == true) {
				this.buildProcess.exitValue();
				setState(Build.STATE_ENDED);
				fireBuildEnded();					
			}
		}
	}
	
	/**
	 * The current instance of the builder.
	 */
	static private Builder currentBuilder = null;
	
	/**
	 * The current builder state.
	 */
	private int state = Build.STATE_READY;
	
	/**
	 * The current doxyfile being built.
	 */
	private IFile doxyfile;
	
	/**
	 * The current build monitor.
	 */
	private BuildProcessMonitor buildProcessMonitor;
	
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
		if( Builder.currentBuilder == null ) {
			Builder.currentBuilder = new Builder();
		}
		return Builder.currentBuilder;
	}
	
	/**
	 * Tell if the builder is running or not.
	 * 
	 * @return	true if the builder is running, false otherwise.
	 */
	public boolean isRunning() {
		return this.state == Build.STATE_RUNNING;
	}
	
	/**
	 * Start the build process.
	 * 
	 * @param	file	The doxygen configuration to use for the build.
	 *
	 * @exception	BuildFaildException	The build could not be started.
	 */
	public void start( IFile file ) throws BuildFailedException {
		if(isRunning() == false) {
			try {
				this.doxyfile = file;
				this.buildProcessMonitor = new BuildProcessMonitor(Doxygen.build( file ));
				
				BuildHistory.getDefault().log(file);
			}
			catch( IOException ioException ) {
				throw new BuildFailedException( "Unable to launch the documentation compilation. Ensure that doxygen is available in the system path or check the compiler path in the preferences.", ioException );
			}
		}
		else {
			throw new BuildFailedException("A build is already in progress.");
		}
	}
	
	/**
	 * Stop the build process, even if it is under progress.
	 */
	public void stop() {
		if(this.isRunning()) {
			this.buildProcessMonitor.stop();
		}
	}
	
	/**
	 * Notify listeners that a build as started.
	 */
	private void fireBuildStarted() {
		super.fireEvent(new BuildEvent(this, this.doxyfile), "buildStarted");
	}
	
	/**
	 * Notify the listeners that a build has been stopped.
	 */
	private void fireBuilStopped() {
		super.fireEvent(new BuildEvent(this, this.doxyfile), "buildStopped");
	}
	
	/**
	 * Notify listeners that a build has been ended.
	 */
	private void fireBuildEnded() {
		super.fireEvent(new BuildEvent(this, this.doxyfile), "buildEnded");
	}
	
	private void fireBuildOutputChanged( String output ) {
		super.fireEvent(new BuildEvent(this, this.doxyfile, new String( output )), "buildOutputChanged" );
	}
	
	/**
	 * Set the builder state to the specified value.
	 * 
	 * @param state	The new bulder state.
	 */
	private void setState( int state ) {
		this.state = state;
	}
}
