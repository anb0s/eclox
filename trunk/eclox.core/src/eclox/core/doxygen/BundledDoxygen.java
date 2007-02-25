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

package eclox.core.doxygen;

import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import eclox.core.Plugin;

public final class BundledDoxygen extends Doxygen {
	
	private URL	location;
	
	public static Collection getAll() {
		Collection			doxygens	= new Vector();
		IExtensionRegistry	registry	= Platform.getExtensionRegistry();
		IExtensionPoint		point		= registry.getExtensionPoint("org.gna.eclox.core.doxygen");
		IExtension[]		extensions	= point.getExtensions();
		
		for (int i = 0; i < extensions.length; i++) {
			IExtension				extension	= extensions[i];
			IConfigurationElement[] elements	= extension.getConfigurationElements();
			
			for (int j = 0; j < elements.length; j++) {
				Path	path	= new Path( elements[j].getAttribute("path") );
				URL		url		= Plugin.getDefault().find( path );
				
				if( url != null ) {
					doxygens.add( new BundledDoxygen(url) );
				}
				else {
					Plugin.getDefault().logError( path.toString() + ": not a valid doxygen path." );
				}
			}
		}
		return doxygens;		
	}
	
	public static BundledDoxygen createFromIdentifier( final String identifier ) {
		BundledDoxygen	doxygen = null;
		
		if( identifier.startsWith( BundledDoxygen.class.getName() ) ) {
			final String	location =  identifier.substring( identifier.indexOf(' ') + 1 );
			
			try {
				doxygen = new BundledDoxygen( new URL(location) );
			}
			catch( Throwable t ) {
				Plugin.log( t );
				doxygen = null;
			}
		}
		return doxygen;
	}

	public String getCommand() {
		try {
			return Platform.resolve( location ).getPath();
		}
		catch( Throwable t ) {
			Plugin.log(t);
			return null;
		}
	}

	public String getDescription() {
		return new String();
	}

	public String getIdentifier() {
		return this.getClass().getName() + " " + location;
	}
	
	private BundledDoxygen( URL location ) {
		this.location = location;

		assert( location != null );
	}

}
