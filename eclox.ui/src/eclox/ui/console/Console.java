/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2006 Guillaume Brocker
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

package eclox.ui.console;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.AbstractConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;

import eclox.core.doxygen.BuildJob;
import eclox.core.doxygen.IBuildJobListener;


/**
 * Implements the doxygen output console
 * 
 * @author Guillaume Brocker
 */
public class Console extends AbstractConsole {

	/**
	 * Implements a build job listener that will maintain the console up-to-date
	 * with the job log.
	 * 
	 * @author gbrocker
	 */
	private class MyBuildJobListener implements IBuildJobListener
	{
		/* (non-Javadoc)
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobLogCleared(eclox.core.doxygen.BuildJob)
		 */
		public void buildJobLogCleared(BuildJob job) {
			ConsolePlugin.getStandardDisplay().asyncExec(
					new Runnable()
					{
						public void run()
						{
							clearConsole();		
						}
					}
				);
		}

		/* (non-Javadoc)
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobLogUpdated(eclox.core.doxygen.BuildJob, java.lang.String)
		 */
		public void buildJobLogUpdated(BuildJob job, String output) {
			final String	text = new String( output );
			ConsolePlugin.getStandardDisplay().asyncExec(
					new Runnable()
					{
						public void run()
						{
							append( text );		
						}
					}
				);
		}

		/* (non-Javadoc)
		 * @see eclox.core.doxygen.IBuildJobListener#buildJobRemoved(eclox.core.doxygen.BuildJob)
		 */
		public void buildJobRemoved(BuildJob job) {
			if( job == getJob() ) {
				ConsolePlugin.getStandardDisplay().asyncExec(
						new Runnable() {
							public void run() {
								setJob( null );		
							}
						}
					);
			}
		}
		
		
		
	}

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

		public void awake(IJobChangeEvent event) {
		}

		public void done(IJobChangeEvent event) {
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

		public void running(IJobChangeEvent event) {
		}

		public void scheduled(IJobChangeEvent event) {
		}

		public void sleeping(IJobChangeEvent event) {
		}		
				
	}
	
	
	/**
	 * the base build console name
	 */
	private static String BASE_NAME = "[Doxygen Build]";
	
	/**
	 * the current build job
	 */
	private BuildJob job;
	
	/**
	 * the current console page
	 */
	private ConsolePage page;
	
	/**
	 * a boolean telling if the console scrolling is locked or not
	 */
	private boolean scrollLocked = false;
	
	/**
	 * the current build job listener
	 */
	private MyBuildJobListener jobListener = new MyBuildJobListener();
	
	/**
	 * the current job change listener
	 */
	private MyJobChangedListener jobChangedListener = new MyJobChangedListener();
	
	/**
	 * Constructor
	 */
	public Console( BuildJob job )
	{
		super( BASE_NAME, null );		
		page = new ConsolePage( this );
	}
	
	public IPageBookViewPage createPage(IConsoleView view) {
		return page;
	}
	
	/**
	 * Appends text to the console.
	 * 
	 * @param	text	a string containing the text to append
	 */
	public void append( String text ) {
		StyledText	styledText = page.getStyledText();
		if( styledText != null ) {
			styledText.append( text );
			scroll();
		}
	}
	
	/**
	 * Clears the console content.
	 */
	public void clearConsole() {
		StyledText	styledText = page.getStyledText();
		if( styledText != null ) {
			styledText.setText( new String() );
		}
	}
	
	/**
	 * Retrieves the job currently monitored by the console
	 * 
	 * @return	a build job or null if none
	 */
	public BuildJob getJob()
	{
		return job;
	}

	/**
	 * Makes the console display the log from the given build job. If the given
	 * job is a null, then the console will reset its state.
	 * 
	 * @param	job	a given build job instance or null
	 */
	public void setJob( BuildJob job )
	{
		// Skips the job if it is already current.
		if( this.job == job ) {
			return;
		}
		
		// Expurges the old job.
		if( this.job != null ) {
			this.job.removeBuidJobListener( this.jobListener );
			this.job.removeJobChangeListener( this.jobChangedListener );
			this.job = null;
		}
		
		// References the new job.
		this.job = job;
		
		// Updates the console contents.
		if( this.job != null ) {
			this.job.addBuidJobListener( this.jobListener );
			this.job.addJobChangeListener( this.jobChangedListener );
		
			setName( BASE_NAME + " " + job.getDoxyfile().getFullPath().toString() );
			showJobLog();
			updateActionStates();
		}
		else {
			setName( BASE_NAME );
			clearConsole();
			updateActionStates();
		}
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
	 * Shows the current job's log in the console.
	 */
	private void showJobLog()
	{
		StyledText	styledText = page.getStyledText();
		if( job != null && page != null && styledText != null ) {
			styledText.setRedraw( false );
			styledText.setText( this.job.getLog() );
			scroll();
			styledText.setRedraw( true );
		}
	}
	
	/**
	 * Scolls the console to the end of the log
	 */
	private void scroll()
	{
		StyledText	styledText = page.getStyledText();
		if( scrollLocked == false && styledText != null) {
			styledText.setSelection( styledText.getCharCount() );
			styledText.showSelection();
		}
	}
	
	/**
	 * Updates the state of some action according to the current job's state
	 */
	private void updateActionStates() {
		page.getCancelJobAction().setEnabled( job != null && job.getState() == Job.RUNNING );
	}
	
}
