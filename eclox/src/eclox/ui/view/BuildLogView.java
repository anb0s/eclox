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


package eclox.ui.view;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import eclox.build.BuildJob;
import eclox.build.BuildOutputEvent;
import eclox.build.BuildOutputListener;
import eclox.doxyfile.DoxyfileSelectionProvider;
import eclox.ui.Plugin;
import eclox.ui.action.StopAction;

/**
 * Implements a view displaying the doxygen build log.
 * 
 * @author gbrocker
 */
public class BuildLogView extends ViewPart {
	/**
	 * Implement an UI task that appends text to the log.
	 */
	private class AppendLogTextTask implements Runnable {
		/**
		 * The text to append.
		 */
		private String logText;
		
		/**
		 * Tell if the text is special
		 */
		boolean special;
		
		/**
		 * Constructor.
		 * 
		 * 
		 * @param	text	The text to append
		 * @param	special	true if the text is special.
		 */
		public AppendLogTextTask(String text, boolean special) {
			this.logText = text;
			this.special = special;
		}
		
		public void run() {
			int	previousEndOffset = text.getCharCount();

			text.append(this.logText);
			if(this.special == true) {
				text.setStyleRange(
					new StyleRange(
						previousEndOffset,
						this.logText.length(),
						new Color(text.getDisplay(), 0, 0, 255),
						null
					)
				);
			}
			text.setSelection(text.getCharCount());
			text.showSelection();
		}
	}
	
	/**
	 * Implement an UI task that change the enable state of the stop action.
	 */
	private class EnableStopActionTask implements Runnable {
		/**
		 * The enable state to set.
		 */
		private boolean enable;
		
		/**
		 * Constructor.
		 * 
		 * @param enable	The new enable state for the stop action.
		 */
		public EnableStopActionTask(boolean enable) {
			this.enable = enable;
		}
		
		public void run() {
			stopAction.setEnabled(this.enable);
		}
	}
	
	/**
	 * Implement an UI task that resets the log text.
	 */
	private class LogResetTask implements Runnable {
		public void run() {
			text.setText("");
		}
	}
	
	/**
	 * Implement an UI task that set the doxyfile to the view.
	 */
	private class DoxyfileUpdateTask implements Runnable {
		/**
		 * The doxyfile to set.
		 */
		private IFile doxyfile;
		
		/**
		 * Constructor.
		 * 
		 * @param	doxyfile	The doxyfile to set.
		 */
		public DoxyfileUpdateTask(IFile doxyfile) {
			this.doxyfile = doxyfile;
		}
		
		public void run() {
			DoxyfileSelectionProvider selectionProvider = ((DoxyfileSelectionProvider)getViewSite().getSelectionProvider());
			
			selectionProvider.setDoxyfile(doxyfile);
			setTitle("Doxygen Build Log - " + doxyfile.getFullPath().toString());
		}
	}
	
	private class OutputListener implements BuildOutputListener {
		/**
		 * Notify that a build job output has changed.
		 * 
		 * @param	event	The build output event to process.
		 */
		public void buildOutputChanged(BuildOutputEvent event) {
			runUITask(new AppendLogTextTask(event.text, false));
		}
	}
	
	private class JobListener extends JobChangeAdapter {
		/**
		 * Notification that a job has completed execution, either due to cancelation, successful completion, or failure.
		 * The event status object indicates how the job finished, and the reason for failure, if applicable.
		 * 
		 * @param	event	the event details
		 */
		public void done(IJobChangeEvent event) {
			String message = null;
			
			switch(event.getResult().getSeverity()) {
				case Status.CANCEL:
					message = "Build canceled!";
					break;
					
				case Status.ERROR:
					message = "Error while building!";
					break;
					
				case Status.OK:
					message = "Build done.";
					break;
			}
			runUITask(new AppendLogTextTask(message + "\r\n", true));
			runUITask(new EnableStopActionTask(false));
		}

		/**
		 * Notification that a job has started running.
		 * 
		 * @param	event	the event details
		 */
		public void running(IJobChangeEvent event) {	
			runUITask(new LogResetTask());
			runUITask(new AppendLogTextTask("Running doxygen...\r\n", true));
			runUITask(new EnableStopActionTask(true));
			runUITask(
				new DoxyfileUpdateTask(
					((BuildJob)event.getJob()).getDoxyfile()
				)
			);
		}
	}
		
	/**
	 * The text control that will receive the log text.
	 */
	private StyledText text = null;
	
	/**
	 * The build output listener.
	 */
	private OutputListener outputListener = new OutputListener();
	
	/**
	 * The build job listener.
	 */
	private JobListener jobListener = new JobListener();
	
	/**
	 * The stop action.
	 */
	private StopAction stopAction = new StopAction();
	
	/**
	 * Shows the build log view in the specified workbench page.
	 * 
	 * @param page	The workbench page in which the build log view will be shown.
	 * 
	 * @return	The shown build log view.
	 * 
	 * @throws PartInitException	The build log view creation failed.
	 */
	public static void show( IWorkbenchPage page ) throws PartInitException {
		page.showView( "eclox.ui.BuildLogView" );		
	}
	
	/**
	 * Show the build log view in the default workbensh page.
	 * 
	 * @throws PartInitException	The buld log view creation failed.
	 */
	public static void show() throws PartInitException {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("eclox.ui.views.buildLog");
	}
	
	/**
	 * Initializes this view with the given view site.
	 * 
	 * @param	site	the view site
	 * 
	 * @throws	PartInitException	if this view was not initialized successfully
	 */
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.setSelectionProvider(new DoxyfileSelectionProvider());
		BuildJob.getDefault().addJobChangeListener(this.jobListener);
		BuildJob.getDefault().addBuildOutputListener(this.outputListener);
	}

	/**
	 * Create the control of the view.
	 * 
	 * @param	parent	The parent widget.
	 */
	public void createPartControl( Composite parent ) {
		this.text = new StyledText( parent, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL  );
		createActions();
	}
	
	/**
	 * The part is being destroyed.
	 */
	public void dispose() {
		BuildJob.getDefault().removeJobChangeListener(this.jobListener);
		BuildJob.getDefault().removeBuildOutputListener(this.outputListener);
		super.dispose();
	}
	
	/**
	 * The part will take the focus within the part.
	 */
	public void setFocus() {
	}
	
	/**
	 * Run the specified UI task.
	 * 
	 * @param	task	The UI task to run.
	 */
	private void runUITask(Runnable task) {
		text.getDisplay().syncExec(task);
	}
	
	/**
	 * Initialize the view actions for the specified site.
	 */
	private void createActions() {
		try {
			IActionBars		actionBars = getViewSite().getActionBars();
		
			actionBars.setGlobalActionHandler("eclox.ui.action.stop", this.stopAction);
			actionBars.getToolBarManager().add(this.stopAction);
		}
		catch( Throwable throwable ) {
			Plugin.getDefault().showError(throwable);
		}
	}
}