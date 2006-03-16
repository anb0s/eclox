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

package eclox.core.build;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import eclox.core.doxygen.Doxygen;
import eclox.core.doxygen.DoxygenException;

/**
 * Implement a build job.
 * 
 * @author gbrocker
 */
public class BuildJob extends Job {
	/**
	 * Implement an internal watch exception class.
	 */
	private class WatchException extends Throwable {
		/**
		 * The watch status
		 */
		public IStatus status;
		
		/**
		 * Constructor.
		 * 
		 * @param	The watch status.
		 */
		public WatchException(IStatus status) {
			this.status = status;
		}
	}
	
	/**
	 * Implement a build output listener manager.
	 */
// TODO eclox split refactoring
//	private class BuildOutputListenerManager extends ListenerManager {
//		/**
//		 * Constructor.
//		 */
//		public BuildOutputListenerManager() {
//			super(BuildOutputListener.class);
//		}
//		
//		/**
//		 * Add a new listener.
//		 * 
//		 * @param	listener	The listener to add.
//		 */
//		public void addListener(BuildOutputListener listener) {
//			super.addListener(listener);
//		}
//		
//		/**
//		 * Remove a listener.
//		 * 
//		 * @param	listener	The listener to remove.
//		 */
//		public void removeListener(BuildOutputListener listener) {
//			super.removeListener(listener);
//		}
//		
//		/**
//		 * Fire the build output changed event to the registered listeners.
//		 * 
//		 * @param	event	The notification event. 
//		 */
//		public void fireBuildOutputChangedEvent(BuildOutputEvent event) {
//			super.fireEvent(event, "buildOutputChanged");
//		}
//	}
	
	/**
	 * The global build job instance.
	 */
	private static BuildJob defaultInstance = new BuildJob();
	
	/**
	 * The current doxyfile to build.
	 */
	private IFile doxyfile;
	
	/**
	 * The build output listener manager.
	 */
//	 TODO eclipse split refactoring
//	private BuildOutputListenerManager buildOutputListenerManager = new BuildOutputListenerManager();
	
	/**
	 * Constructor.
	 */
	private BuildJob() {
		super("Doxygen Build");
		this.setPriority(Job.BUILD);
	}
	
	/**
	 * Add a new build output listener.
	 * 
	 * @param	listener	A new build output listener to add.
	 */
//	 TODO eclipse split refactoring
//	public void addBuildOutputListener(BuildOutputListener listener) {
//		this.buildOutputListenerManager.addListener(listener);
//	}
	
	/**
	 * Launches the build of the specified file.
	 *
	 * @param	doxyfile	a file resource to build
	 * 
	 * @throws PartInitException	the build log view could not be displayed
	 * @throws BuildInProgreeError	a build is already in progrees
	 * 
	 * @author gbrocker
	 */
	public static void buildDoxyfile( IFile doxyfile ) throws /*PartInitException,*/ BuildInProgressError {
		Preferences preferences = eclox.core.Plugin.getDefault().getPluginPreferences();
		String		autoSaveValue = preferences.getString( eclox.core.preferences.Names.AUTO_SAVE );
		
//		 TODO eclipse split refactoring
//		if( autoSaveValue == eclox.core.preferences.Values.AUTO_SAVE_ASK ) {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( true );
//		}
//		else if( autoSaveValue == eclox.core.preferences.Values.AUTO_SAVE_ALWAYS ) {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors( false );
//		}
		
		BuildJob.getDefault().setDoxyfile(doxyfile);
		BuildJob.getDefault().schedule();
	}

	
	/**
	 * Retrieve the current build job instance.
	 * 
	 * @return	The current build job instance.
	 */
	public static BuildJob getDefault() {
		return BuildJob.defaultInstance;
	}
	
	/**
	 * Retrieve the current doxyfile.
	 * 
	 * @return	The current doxyfile.	
	 */
	public IFile getDoxyfile() {
		return this.doxyfile;
	}
	
	/**
	 * Remove a build output listener
	 * 
	 * @param	listener	The build output listener to remove.
	 */
//	 TODO eclipse split refactoring

//	public void removeBuildOutputListener(BuildOutputListener listener) {
//		this.buildOutputListenerManager.removeListener(listener);
//	}
	
	/**
	 * Run the job.
	 * 
	 * @param	monitor	The progress monitor to use to report work progression
	 * 					and handle cancelation requests.
	 * 
	 * @return	The job status.
	 */
	protected IStatus run(IProgressMonitor monitor) {
		IStatus	result = null;
		try {				
			Process buildProcess = Doxygen.build(this.doxyfile);

			monitor.beginTask(this.doxyfile.getFullPath().toString(), 100);
			BuildHistory.getDefault().log(this.doxyfile);
			for(;;) {
				this.watchBuildProcessOutput(buildProcess.getInputStream());
				this.watchBuildProcessOutput(buildProcess.getErrorStream());
				this.watchBuildProcessLife(buildProcess);
				this.watchProgressMonitor(monitor);
				Thread.yield();
			}
		}
		catch(WatchException watchException) {
			result = watchException.status;
		}
		catch(DoxygenException doxygenException) {
			result = new Status(Status.ERROR, eclox.core.Plugin.getDefault().toString(), 0, doxygenException.getMessage(), null);
		}
		catch(Throwable throwable) {
			result = new Status(Status.ERROR, eclox.core.Plugin.getDefault().toString(), 0, "Unexpected error while building.", throwable);
		}
		monitor.done();
		return result;
	}
	
	/**
	 * Set the next doxyfile to build.
	 * 
	 * @param	doxyfile	The next doxyfile to build.
	 */
	public void setDoxyfile(IFile doxyfile) throws BuildInProgressError {
		if(this.getState() == Job.NONE) {
			this.doxyfile = doxyfile;
		}
		else {
			throw new BuildInProgressError();
		}
	}
	
	/**
	 * Watch for the build process life.
	 * 
	 * @param	process	The build process to watch.
	 * 
	 * @throws	WatchException	the specified process has terminated.
	 */
	private void watchBuildProcessLife(Process process) throws WatchException {
		try {
			process.exitValue();
			throw new WatchException(Status.OK_STATUS);
		}
		catch(IllegalThreadStateException e) {
			// Nothing to do, the process is still alive.
		}
	}
	
	/**
	 * Watch for data on the specified input stream. When data has been found,
	 * it raises a build output changed event.
	 * 
	 * @param	input	The intput stream to watch.
	 * 
	 * @throws	IOException	an error occured while reding the input.
	 */
	private void watchBuildProcessOutput(InputStream input) throws IOException {
		int	available = input.available();
		
		if(available != 0) {
			byte buffer[] = new byte[available];
			
			input.read(buffer);
// TODO eclipse split refactoring
//			this.buildOutputListenerManager.fireBuildOutputChangedEvent(
//				new BuildOutputEvent(this, new String(buffer))
//			);
		}
	}
	
	/**
	 * Watch the progress monitor for user cancel request.
	 * 
	 * @param	monitor	The progress monitor to watch.
	 * 
	 * @throws	WatchException	the user has requestred the job to terminate.
	 */
	private void watchProgressMonitor(IProgressMonitor monitor) throws WatchException {
		if(monitor.isCanceled() == true) {
			throw new WatchException(Status.CANCEL_STATUS);
		}
	}
}
