/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2005 Guillaume Brocker
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

package eclox.ui.editor.form.settings.filters;

import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import eclox.doxyfiles.Doxyfile;
import eclox.doxyfiles.ISettingPropertyListener;
import eclox.doxyfiles.Setting;
import eclox.ui.editor.form.Editor;

/**
 * Implements a filter that will shows only modified settings
 * (setting having the dirty flag).
 * 
 * @author gbrocker
 */
public class Modified implements IFilter {
	
	/**
	 * The structured viewer to filter.
	 */
	private StructuredViewer	 viewer;
	
	/**
	 * the viewer filter installed in the managed viewer
	 */
	private MyViewerFiler viewerFilter;
	
	/**
	 * the setting property listener instance
	 */
	private MySettingPropertyListener settingPropertyListener;
	
	/**
	 * Implements a viewer filter that will only show 
	 * setting having the dirty property.
	 */
	private class MyViewerFiler extends ViewerFilter {

		public boolean select(Viewer viewer, Object parentElement, Object element) {
			// Pre-condition
			assert element instanceof Setting;
			
			Setting	setting = (Setting) element;
			return setting.hasProperty( Editor.PROP_SETTING_DIRTY );
		}

		public boolean isFilterProperty(Object element, String property) {
			if( element instanceof Setting ) {
				return property.equals( Editor.PROP_SETTING_DIRTY );
			}
			else {
				return super.isFilterProperty(element, property);
			}
		}
		
	}
	
	/**
	 * Implements a setting property listener that will trigger
	 * the viewer refresh as soon as a setting dirty property changed.
	 */
	private class MySettingPropertyListener implements ISettingPropertyListener {
		
		/**
		 * the doxyfile being listened
		 */
		private Doxyfile		doxyfile;
		
		/**
		 * Attaches the listener to the specified doxyfile. Before being
		 * destroyed, the listener should be explicitely detached.
		 * 
		 * @param	doxyfile		a doxyfile the listener will attach to
		 * 
		 * @see	detach
		 */
		public void attach( Doxyfile doxyfile ) {
			// Pre-condition
			assert this.doxyfile == null;
			
			// References the doxyfile for later use.
			this.doxyfile = doxyfile;
			// Attaches to all settings.
			Iterator		i = doxyfile.settingIterator();
			while( i.hasNext() ) {
				Setting	setting = (Setting) i.next();
				setting.addSettingListener( this );
			}
			
			// Post-condition
			assert this.doxyfile != null;
		}
		
		/**
		 * Detaches the listener from the previously attached doxyfile.
		 * 
		 * @see	attach
		 */
		public void detach() {
			// Pre-condition
			assert this.doxyfile != null;
			
			// Detaches the listener instace from all settings
			Iterator		i = this.doxyfile.settingIterator();
			while( i.hasNext() ) {
				Setting	setting = (Setting) i.next();
				setting.removeSettingListener( this );
			}
			// Unreferences the managed doxyfile.
			this.doxyfile = null;
			
			// Post-condition
			assert this.doxyfile == null;
		}

		public void settingPropertyChanged(Setting setting, String property) {
			// Pre-condition
			assert viewer != null;
			
			viewer.update( setting, new String[]{Editor.PROP_SETTING_DIRTY} );			
		}

		public void settingPropertyRemoved(Setting setting, String property) {
			// Pre-condition
			assert viewer != null;
			
			viewer.update( setting, new String[]{Editor.PROP_SETTING_DIRTY} );
		}
		
	}

	public void setDoxyfile(Doxyfile doxyfile) {
		if( doxyfile != null ) {
			this.settingPropertyListener = new MySettingPropertyListener();
			this.settingPropertyListener.attach( doxyfile );
		}
		else {
			this.settingPropertyListener.detach();
			this.settingPropertyListener = null;
		}
	}

	public void createControls(IManagedForm managedForm, Composite parent) {
		// Nothing to do.
	}

	public void createViewerFilters(StructuredViewer viewer) {
		// Pre-condition
		assert this.viewer == null;
		assert this.viewerFilter == null;
		
		// Create relevant object instances.
		this.viewer = viewer;
		this.viewerFilter = new MyViewerFiler();
		this.viewer.addFilter( this.viewerFilter );
		
		// Post-condition
		assert this.viewer != null;
		assert this.viewerFilter != null;
	}

	public void disposeControls() {
		// Nothing to do.
	}

	public void disposeViewerFilers(StructuredViewer viewer) {
		// Pre-condition
		assert this.viewer != null;
		assert this.viewerFilter != null;
		
		// Remove references on out-dated objects.
		this.viewer.removeFilter( this.viewerFilter );
		this.viewerFilter = null;
		this.viewer = null;
		
		// Post-condition
		assert this.viewer == null;
		assert this.viewerFilter == null;
	}

	public String getName() {
		return "Modified";
	}

}
