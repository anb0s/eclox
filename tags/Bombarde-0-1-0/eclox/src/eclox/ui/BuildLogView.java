/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003 Guillaume Brocker

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


package eclox.ui;


import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import eclox.build.BuildEvent;
import eclox.build.BuildListener;
import eclox.build.Builder;

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
			m_stopBuildAction.setEnabled( true );
			m_text.setText( "" );
			appendText( "Running doxygen...\r\n", new Color( m_text.getDisplay(), 0, 0, 255 ) );
		}
		
		public void buildOutputChanged( BuildEvent event ) {
			appendText( (String) event.value, null );
		}
			
		public void buildEnded( BuildEvent event ) {
			m_stopBuildAction.setEnabled( false );
			appendText( "Doxygen work done !\r\n", new Color( m_text.getDisplay(), 0, 0, 255 ) );
		}

		public void buildStopped( BuildEvent event ) {			
			m_stopBuildAction.setEnabled( false );
			appendText( "Doxygen work stopped by user !\r\n", new Color( m_text.getDisplay(), 0, 0, 255 ) );
		}
	}
	
	/**
	 * The text control that will receive the log text.
	 */
	private StyledText m_text = null;
	
	/**
	 * The stop buld action.
	 */
	private StopBuildAction m_stopBuildAction = new StopBuildAction();
	
	/**
	 * The builder listener.
	 */
	private BuilderListener m_builderListener = new BuilderListener();
	
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
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( "eclox.ui.BuildLogView" );
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
		Builder.getDefault().removeListener( m_builderListener );
		super.dispose();
	}
	
	/**
	 * The part will take the focus within the part.
	 */
	public void setFocus() {
		Builder.getDefault().addListener( m_builderListener );
	}
	
	/**
	 * Initialize the view actions for the specified site.
	 * 
	 * @param	viewSite	The view where the action should be initialized.
	 */
	private void createActions() {
		IActionBars		actionBars = getViewSite().getActionBars();
		IToolBarManager	toolBarManager = actionBars.getToolBarManager();
		
		toolBarManager.add( m_stopBuildAction );
		actionBars.updateActionBars();
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
}
