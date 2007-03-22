// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2007 Guillaume Brocker
//
// This file is part of eclox.
//
// eclox is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
//
// eclox is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with eclox; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	

package eclox.core.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxygen.BuildJob;

public class DoxyfileDecorator implements ILightweightLabelDecorator {
	
	/**
	 * Implements a job change listener
	 */
	/**
	 * @author gbrocker
	 *
	 */
	private class MyJobListener extends JobChangeAdapter {

		/**
		 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
		 */
		public void done(IJobChangeEvent event) {
			Job	job = event.getJob();
			
			if( job.belongsTo(BuildJob.FAMILY) ) {
				BuildJob	buildJob = (BuildJob) job;
				
				fireProviderChangedEvent( buildJob.getDoxyfile() );
			}
		}

		/**
		 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
		 */
		public void running(IJobChangeEvent event) {
			Job	job = event.getJob();
			
			if( job.belongsTo(BuildJob.FAMILY) ) {
				BuildJob	buildJob = (BuildJob) job;
				
				fireProviderChangedEvent( buildJob.getDoxyfile() );
			}
		}
		
		
	}
	
	/**
	 * the collection of attached listeners
	 */
	private Collection listeners = new Vector();
	
	/**
	 * the job listener
	 */
	private MyJobListener jobManagerListener = new MyJobListener();
	
	/**
	 * Constructor
	 */
	public DoxyfileDecorator() {
		Platform.getJobManager().addJobChangeListener(jobManagerListener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
		if( Doxyfile.isDoxyfile(element) ) {
			IFile		doxyfile	= (IFile) element;
			BuildJob	job			= BuildJob.findJob(doxyfile);
			
			if( job != null && job.getState() == BuildJob.RUNNING ) {
				decoration.addOverlay( ImageDescriptor.createFromFile(this.getClass(), "build_co.gif"), IDecoration.BOTTOM_LEFT );
				decoration.addSuffix(" (building...)");
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		listeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		Platform.getJobManager().removeJobChangeListener(jobManagerListener);
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Notifies listerners that the given resource'l decoration need to be updated
	 * 
	 * @param	resource	a resource to refresh
	 */
	private void fireProviderChangedEvent( Object resource ) {
		Iterator					i		= listeners.iterator();
		LabelProviderChangedEvent	event	= new LabelProviderChangedEvent(this, resource);
		
		while( i.hasNext() ) {
			ILabelProviderListener	listener = (ILabelProviderListener) i.next();
			
			listener.labelProviderChanged(event);
		}
	}

}
