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

package eclox.ui.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;

import eclox.core.Plugin;

/**
 * Define all icon names for the plugin.
 * 
 * @author gbrocker
 */
public class Icons {
	/**
	 * A string containing the icon repository path.
	 */
	static private final String repositoryPath = "/icons/";
	
	/**
	 * The big question mark.
	 */
	public static final String LOGO_BIG = "logo-big.gif";

	/**
	 * The stop icon key.
	 */
	static public final String STOP = "stop.gif";

	/**
	 * Initialize the specified image registry with all icons.
	 * 
	 * @param	reg	The image registry to initialize.
	 */
	public static void initializeImageRegistry(ImageRegistry reg) {
		// Initialize the icon names to create.
		Collection iconNames = new ArrayList();
		
		iconNames.add(Icons.LOGO_BIG);
		iconNames.add(Icons.STOP);
		
		// Create all these icons.
		Bundle		bundle = Plugin.getDefault().getBundle();
		Iterator	iconNameIt = iconNames.iterator();

		while(iconNameIt.hasNext()) {
			String			iconName = (String) iconNameIt.next();
			ImageDescriptor iconDescriptor = null;

			iconDescriptor = ImageDescriptor.createFromURL(
				bundle.getEntry(
					Icons.repositoryPath + iconName
				)
			);
			reg.put(iconName, iconDescriptor);
		}
	}
}
