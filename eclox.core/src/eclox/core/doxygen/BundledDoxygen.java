/*******************************************************************************
 * Copyright (C) 2003-2006, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
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

package eclox.core.doxygen;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import eclox.core.Plugin;

public final class BundledDoxygen extends Doxygen {

	private URL	url;

    /**
     * Constructor
     */
    public BundledDoxygen() {
    }

    /**
     * Constructor
     */
    public BundledDoxygen(URL url) {
        assert( url != null );
        this.url = url;
    }

	/**
	 * Retrieves all available bundled doxygen binaries.
	 *
	 * @return	a collection with all collected bundled doxygen wrappers
	 */
	public static Collection<BundledDoxygen> getAll() {
		Collection<BundledDoxygen>			doxygens	= new Vector<BundledDoxygen>();
		IExtensionRegistry	registry	= Platform.getExtensionRegistry();
		IExtensionPoint		point		= registry.getExtensionPoint("org.gna.eclox.core.doxygen");		
		IExtension[]		extensions	= point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IExtension				extension	= extensions[i];
			IConfigurationElement[] elements	= extension.getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				final String	arch	= elements[j].getAttribute("arch");
				final String	os		= elements[j].getAttribute("os");
				if( Platform.getOS().equals(os) && Platform.getOSArch().equals(arch) ) {
					String	path	= elements[j].getAttribute("path");
					URL url;
                    try {
                        url = FileLocator.toFileURL(Platform.getBundle("org.gna.eclox.doxygen.core").getEntry(path));
                        doxygens.add(new BundledDoxygen(url));
                    } catch (IOException e) {
                        //Plugin.getDefault().logError( path + ": not a valid doxygen path." );
                        Plugin.log(e);
                    }
				}
			}
		}
		return doxygens;
	}

	@Override
	public String getCommand() {
		try {
			return FileLocator.resolve(url).getPath();
		}
		catch(Throwable t) {
			Plugin.log(t);
			return null;
		}
	}

	@Override
	public String getDescription() {
		return new String();
	}

	@Override
	public String getIdentifier() {
		return this.getClass().getName() + " " + url;
	}

    @Override
    public void setLocation(String location) {
        assert( location != null );
        assert( location.length() != 0 );
        try {
            this.url = new URL(location);
        }
        catch( Throwable t ) {
            Plugin.log( t );
        }
    }

}
