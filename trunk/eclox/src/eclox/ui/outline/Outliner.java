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


package eclox.ui.outline;


/**
 * Implements the content outliner class.
 * 
 * @author gbrocker
 */
public class Outliner extends org.eclipse.ui.views.contentoutline.ContentOutlinePage {
	/**
	 * The setting to outline.
	 */
	private eclox.doxyfile.node.Doxyfile m_settings;
	
	/**
	 * Constructor.
	 * 
	 * @param	settings	The settings to outline. 
	 */
	public Outliner( eclox.doxyfile.node.Doxyfile settings ) {
		m_settings = settings;
	}
	
	public void createControl( org.eclipse.swt.widgets.Composite parent ) {
		super.createControl( parent );
		getTreeViewer().setContentProvider( new ContentProvider() );
		getTreeViewer().setLabelProvider( new LabelProvider() );
		getTreeViewer().setInput( m_settings );
	}
}
