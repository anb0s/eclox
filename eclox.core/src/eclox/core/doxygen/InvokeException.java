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
 * Signals that something got wrong while trying to execute doxygen program.
 * 
 * @author gbrocker
 */
public class InvokeException extends Exception {

	private static final long serialVersionUID = -4531804107557361202L;

	/**
	 * Constructor
	 * 
	 * @param	cause	references the cause 
	 */
	InvokeException( Throwable cause ) {
		super( cause );
	}
}
