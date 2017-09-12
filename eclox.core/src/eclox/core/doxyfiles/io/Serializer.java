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
 *     Andre Bossert -
 *
 ******************************************************************************/

package eclox.core.doxyfiles.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import eclox.core.doxyfiles.Chunk;
import eclox.core.doxyfiles.Doxyfile;

/**
 * Implements a doxyfile content serializer.
 *
 * @author willy
 */
public class Serializer extends InputStream {

    /**
     * an iterator on the doxyfile chunks
     */
    private Iterator<?> chunkIterator;

    /**
     * a string buffer containing the next character to red
     */
    private StringBuffer stringBuffer;

    /**
     * Constructor
     *
     * @param	doxyfile	a doxyfile to serialize
     */
    public Serializer(Doxyfile doxyfile) {
        this.chunkIterator = doxyfile.iterator();
        this.stringBuffer = getNextStringBuffer();
    }

    public int read() throws IOException {
        int result;
        if (stringBuffer != null) {
            // Retrieves the next character from the current string buffer.
            result = stringBuffer.charAt(0);
            stringBuffer.deleteCharAt(0);

            // If the current string buffer has been entierly read, gets the next string buffer.
            if (stringBuffer.length() == 0) {
                stringBuffer = getNextStringBuffer();
            }
        } else {
            result = -1;
        }
        return result;
    }

    /**
     * Retrieves the next string buffer to use for reading operations or null
     * if no more chunk is left in the doxyfile.
     *
     * @return	a string buffer or null of none
     */
    private StringBuffer getNextStringBuffer() {
        // Pre-condition
        assert chunkIterator != null;

        // Retrieves the next string buffer.
        StringBuffer result = null;
        if (this.chunkIterator.hasNext() == true) {
            Chunk chunk = (Chunk) this.chunkIterator.next();
            result = new StringBuffer(chunk.toString());
        }
        return result;
    }

}
