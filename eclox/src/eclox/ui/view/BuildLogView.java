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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import eclox.build.BuildEvent;
import eclox.build.BuildListener;
import eclox.build.Builder;
import eclox.doxyfile.DoxyfileSelectionProvider;
import eclox.ui.Plugin;
import eclox.ui.action.StopAction;

import org.eclipse.ui.IActionBars;

/**
 * Implements a view displaying the doxygen build log.
 * 
 * @author gbrocker
 */
public class BuildLogView extends ViewPart {
	/**
	 * Implement a builder listener class.
	 */
	private class BuilderListener  implements BuildListener {
		/**
		 * Process the specified build event.
		 * 
		 * @param	event	The build event to process.
		 */
		public void buildStarted( BuildEvent event ) {
			m_text.setText( "" );
			stopAction.setEnabled(true);
			appendText( "Running doxygen...\r\n", new Color( m_text.getDisplay(), 0, 0, 255 ) );
			setDoxyfile(event.doxyfile);
		}
		
		public void buildOutputChanged( BuildEvent event ) {
			appendText( (String) event.value, null );
		}
			
		public void buildEnded( BuildEvent event ) {
			stopAction.setEnabled(false);
			appendText( "Doxygen work done !\r\n", new Color( m_text.getDisplay(), 0, 0, 255 ) );
		}

		public void buildStopped( BuildEvent event ) {			
			stopAction.setEnabled(false);
			appendText( "Doxygen work stopped by user !\r\n", new Color( m_text.getDisplay(), 0, 0, 255 ) );
		}
	}
		
	/**
	 * The text control that will receive the log text.
	 */
	private StyledText m_text = null;
	
	/**
	 * The builder listener.
	 */
	private BuilderListener builderListener = new BuilderListener();
	
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
	}

	/**
	 * Create the control of the view.
	 * 
	 * @param	parent	The parent widget.
	 */
	public void createPartControl( Composite parent ) {
		m_text = new StyledText( parent, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL  );
		createActions();
	}
	
	/**
	 * The part is being destroyed.
	 */
	public void dispose() {
		Builder.getDefault().removeListener( this.builderListener );
		super.dispose();
	}
	
	/**
	 * The part will take the focus within the part.
	 */
	public void setFocus() {
		Builder.getDefault().addListener( this.builderListener );
	}
	
	/**
	 * Appends some text to log view.
	 * 
	 * @param text	The text to append.
	 * @param color	The color to set for the text, or null if none.
	 */
	private void appendText( String text, Color color ) {
		int	previousEndOffset = m_text.getCharCount();

		m_text.append( text );
		if( color != null ) {
			m_text.setStyleRange(
				new StyleRange( previousEndOffset, text.length(), color, null ) );
		}
		m_text.setSelection( m_text.getCharCount() );
		m_text.showSelection();
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
	
	/**
	 * Set the current doxyfile
	 * 
	 * @param	doxyfile	The new current doxyfile.
	 */
	private void setDoxyfile(IFile doxyfile) {
		DoxyfileSelectionProvider selectionProvider = ((DoxyfileSelectionProvider)getViewSite().getSelectionProvider());
		
		selectionProvider.setDoxyfile(doxyfile);
		this.setTitle("Doxygen Build Log - " + doxyfile.getFullPath().toString());
	}
}
