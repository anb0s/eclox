/*******************************************************************************
 * Copyright (C) 2003, 2004, 2007, 2013, Guillaume Brocker
 * Copyright (C) 2015-2017, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *     Andre Bossert - #215: add support for line separator
 *                   - #212: add support for multiple lines (lists) concatenated by backslash (\)
 *
 ******************************************************************************/

package eclox.core.doxyfiles;

import eclox.core.ListSeparateMode;

/**
 * Implements the default item that can be contained in a doxyfile.
 *
 * A chunk is a piece of text extracted from e doxyfile. It can represent comments,
 * empty lines or whatever.
 *
 * @author Guillaume Brocker
 */
public abstract class Chunk {

    /**
     * the chunk owner (aka the doxyfile)
     */
    private Doxyfile owner;

    /**
     * Retrieves the chunk owner.
     *
     * @return	the chunk owner
     */
    public Doxyfile getOwner() {
        return owner;
    }

    /**
     * Updates the chunk owner
     *
     * @param	owner	the new owner
     */
    public void setOwner(Doxyfile owner) {
        this.owner = owner;
    }

    /**
     * Converts the chunk into a string representing its content.
     *
     * @return	a string containing the chunk content
     */
    public abstract String getString(String lineSeparator, ListSeparateMode listSepMode);

}
