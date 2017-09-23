/*******************************************************************************
 * Copyright (C) 2003-2004, 2013, Guillaume Brocker
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

import java.util.ArrayList;
import java.util.List;

import eclox.core.ListSeparateMode;
import eclox.core.doxyfiles.Chunk;

/**
 * Implements a raw text chunk.
 *
 * @author willy
 */
public class RawText extends Chunk {

    /**
     * a string array containing the raw text piece
     */
    List<String> content = new ArrayList<String>();

    /**
     * Appends a new piece of text to the raw text chunk.
     *
     * @param	text	a string containing a piece of text to append
     */
    public void addLine(String text) {
        content.add(text);
    }

    /**
     * Retrieves the raw text content as a string.
     *
     * @return	a string containing the raw text content
     */
    public String getString(String lineSeparator, ListSeparateMode listSepMode) {
        String ret = new String();
        for(String line : content) {
            ret += line + lineSeparator;
        }
        return ret;
    }

}
