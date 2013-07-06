/*******************************************************************************
 * Copyright (C) 2003-2007, 2013 Guillaume Brocker
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

package eclox.ui.console;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.part.Page;

import eclox.core.doxygen.BuildJob;
import eclox.core.doxygen.IBuildJobListener;
import eclox.ui.console.action.CancelJob;
import eclox.ui.console.action.ClearLog;
import eclox.ui.console.action.LockScroll;
import eclox.ui.console.action.RemoveConsole;


/**
 * Implements the page for the doxygen console.
 * 
 * @author gbrocker
 */
public class ConsolePage extends Page {
	
	/**
	 * Implements a build job listener that will maintain the console up-to-date
	 * with the job log.
	 * 
	 * @author gbrocker
	 */
	private class MyBuildJobListener implements IBuildJobListener
	{
		/**
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobLogCleared(eclox.core.doxygen.BuildJob)
		 */
		public void buildJobLogCleared(BuildJob job) {
			ConsolePlugin.getStandardDisplay().asyncExec(
					new Runnable() {
						public void run() {
							clear();		
						}
					}
				);
		}

		/**
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobLogUpdated(eclox.core.doxygen.BuildJob, java.lang.String)
		 */
		public void buildJobLogUpdated(BuildJob job, String output) {
			final String	text = new String( output );
			ConsolePlugin.getStandardDisplay().asyncExec(
					new Runnable() {
						public void run() {
							append( text );		
						}
					}
				);
		}

		/**
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobRemoved(eclox.core.doxygen.BuildJob)
		 */
		public void buildJobRemoved(BuildJob job) {}		
	}

	/**
	 * @brief	Implements a job change listener used to update the interface state according to the
	 * 			build job events.
	 * 
	 * @author gbrocker
	 */
	private class MyJobChangedListener implements IJobChangeListener {
	
		public void aboutToRun(IJobChangeEvent event) {
			ConsolePlugin.getStandardDisplay().syncExec(
					new Runnable()
					{
						public void run()
						{
							updateActionStates();		
						}
					}
				);
		}

		public void awake(IJobChangeEvent event) {}

		public void done(final IJobChangeEvent event) {
			ConsolePlugin.getStandardDisplay().syncExec(
					new Runnable() {
						public void run() {
							updateActionStates();
							
							IStatus	status = event.getResult();
							if( status.isOK() ) {
								append( "*** Build finished!" );
							}
							else {
								append( "*** Build aborted! " );
								append( status.getMessage() );
							}
						}
					}
				);
		}

		public void running(IJobChangeEvent event) {}

		public void scheduled(IJobChangeEvent event) {}

		public void sleeping(IJobChangeEvent event) {}		
				
	}

	private boolean					scrollLocked = false;	///< a boolean telling if the console scrolling is locked or not
	private StyledText				styledText;				///< the control used to display text
	private Console					console;				///< the console the page is attached to
	private CancelJob				cancelJobAction;		///< the action that cancels the build job
	private MyBuildJobListener		jobListener;			///< the build job listener
	private MyJobChangedListener	jobChangedListener;		///< the build job change listener
	
	/**
	 * Constructor
	 * 
	 * @param	console	the console the page is attached to
	 */
	public ConsolePage( Console console ) {
		this.console = console;
	}

	/**
	 * Appends text to the console.
	 * 
	 * @param	text	a string containing the text to append
	 */
	private void append( String text ) {
		assert styledText != null;
		
		styledText.append( text );
		scroll();
	}

	/**
	 * Clears the console content.
	 */
	private void clear() {
		assert styledText != null;
		
		styledText.setText( new String() );
	}
	
	/**
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose() {
		// Removes job listeners.
		console.getJob().removeBuidJobListener(jobListener);
		console.getJob().removeJobChangeListener(jobChangedListener);
		
		// Release some references.
		console = null;
		styledText = null;
		cancelJobAction = null;
		
		// Base treatment.
		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		assert console != null;
		assert styledText == null;
		assert cancelJobAction == null;
	
		// Creates the text control.
		styledText = new StyledText( parent, SWT.READ_ONLY|SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL  );
		styledText.setFont( JFaceResources.getTextFont() );
		
		// Creates the cancel job action.
		cancelJobAction = new CancelJob( console );
		
		// Creates job listeners;
		jobListener = new MyBuildJobListener();
		jobChangedListener = new MyJobChangedListener();
		console.getJob().addBuidJobListener(jobListener);
		console.getJob().addJobChangeListener(jobChangedListener);
		
		// Creates the actions
		IActionBars	actionBars = getSite().getActionBars();
		actionBars.getToolBarManager().appendToGroup( IConsoleConstants.LAUNCH_GROUP, cancelJobAction );
		actionBars.getToolBarManager().appendToGroup( IConsoleConstants.LAUNCH_GROUP, new RemoveConsole(console) );
		actionBars.getToolBarManager().appendToGroup( IConsoleConstants.OUTPUT_GROUP, new ClearLog(console) );
		actionBars.getToolBarManager().appendToGroup( IConsoleConstants.OUTPUT_GROUP, new LockScroll(this) );
		actionBars.updateActionBars();
	}
	
	/**
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl() {
		return styledText;
	}

	/**
	 * Scrolls the console to the end of the log
	 */
	private void scroll() {
		assert styledText != null;
		
		if( scrollLocked == false ) {
			styledText.setSelection( styledText.getCharCount() );
			styledText.showSelection();
		}
	}

	/**
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	public void setFocus() {
		assert styledText != null;
		
		styledText.setFocus();
	}
	
	/**
	 * Updates the lock of the console scroll.
	 * 
	 * @param	locked	a boolean giving the new lock state
	 */
	public void setScrollLocked( boolean locked ) {
		scrollLocked = locked;
	}
	
	/**
	 * Updates the state of some action according to the current job's state
	 */
	private void updateActionStates() {
		assert cancelJobAction != null;
		assert console != null;
		
		BuildJob	job = console.getJob();
		cancelJobAction.setEnabled( job != null && job.getState() == Job.RUNNING );
	}

}
