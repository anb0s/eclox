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

import org.eclipse.ui.ide.IDE;


/**
 * Implements the label provider for doxygen settings elements.
 * 
 * @author gbrocker
 */
public class LabelProvider extends org.eclipse.jface.viewers.LabelProvider {
	/**
	 * The map containing images for items class.
	 */
	private java.util.HashMap m_images = new java.util.HashMap();
	
	/**
	 * Constructor.
	 */
	public LabelProvider() {
		org.eclipse.ui.ISharedImages	sharedImages = org.eclipse.ui.PlatformUI.getWorkbench().getSharedImages();
		org.eclipse.swt.graphics.Image	image;
				 
		// Load images for the items classes.
		m_images.put(
			eclox.resource.content.Section.class,
			sharedImages.getImage( org.eclipse.ui.ISharedImages.IMG_OBJ_FOLDER ) );
		m_images.put(
			eclox.resource.content.Tag.class,
			sharedImages.getImage( IDE.SharedImages.IMG_OPEN_MARKER) );
	}
	
	/**
	 * Retrieves the image for the specified element.
	 * 
	 * @param	element	The element for which the image must be retrieved.
	 * 
	 * @return	The image for the specified element.
	 */
	public org.eclipse.swt.graphics.Image getImage( Object element ) {
		return (org.eclipse.swt.graphics.Image) m_images.get( element.getClass() );
	}
	
	/**
	 * Retrieves the text for the specified element.
	 *
	 * @param	element	The element for which the text must be provided.
	 * 
	 * @return	A string containing the object text. 
	 */
	public String getText( Object element ) {
		String	text = null;
		
		if( element instanceof eclox.resource.content.Section ) {
			eclox.resource.content.Section	section = (eclox.resource.content.Section) element;
				
			text = section.getName();
		}
		else if( element instanceof eclox.resource.content.Tag ) {
			eclox.resource.content.Tag	tag = (eclox.resource.content.Tag) element;
			
			text = tag.getName();
		}
		else {
			text = super.getText( element );
		}
		
		return text; 
	}
}
