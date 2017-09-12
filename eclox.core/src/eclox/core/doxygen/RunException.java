/*******************************************************************************
 * Copyright (C) 2007, 2008, 2013, Guillaume Brocker
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

/**
 * Signals that something got wrong while running doxygen.
 * 
 * @author gbrocker
 */
public class RunException extends Exception {

    private static final long serialVersionUID = -694030339432059044L;

    /**
     * Constructor
     * 
     * @param	message	a string containing the reson of the exception
     */
    RunException(String message) {
        super(message);
    }
}
