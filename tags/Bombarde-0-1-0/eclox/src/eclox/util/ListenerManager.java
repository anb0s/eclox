/*
	eclox
	Copyright (C) 2003 Guillaume Brocker

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

package eclox.util;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Implement a generic listenable object class.
 * 
 * @author gbrocker
 */
public class ListenerManager {
	/**
	 * All registered listeners.
	 */
	private Set m_listeners = new HashSet();
	
	/**
	 * The listener class.
	 */
	private Class m_listenerClass;
	
	/**
	 * Constructor.
	 * 
	 * @param	listenerClass	The class of listener objects.
	 */
	protected ListenerManager( Class listenerClass ) {
		m_listenerClass = listenerClass;
	}
	
	/**
	 * Register a new listener.
	 * 
	 * @param	listener	The new listener to register.
	 */
	protected void addListener( EventListener listener ) {
		m_listeners.add( listener );
	}
 
	/**
	 * Remove a listener.
	 * 
	 * @param	listener	The listener to remove.
	 */
	protected void removeListener( EventListener listener ) {
		m_listeners.remove( listener );
	}
	 
	/**
	 * Fire the specified event among registered listeners, using the
	 * method specified by ots name.
	 * 
	 * @param event		The event to fire.
	 * @param methodName	A string containing the name of the method to call
	 * 					for notification.
	 */
	protected void fireEvent( EventObject event, String methodName ) {
		try {
			java.lang.reflect.Method	method;
			Class[]						args = new Class[1];
 	
			args[0] = event.getClass();
			method = m_listenerClass.getMethod( methodName, args );
			fireEvent( event, method );
		}
		catch( NoSuchMethodException noSuchMethod ) {
		}
	}
	 
	/**
	 * Fire the specified event among all registered listeners, calling the specified method.
	 * 
	 * @param event	The event to fire.
	 * @param method	The method to call for the event notification.
	 * 
	 * @todo	Exception handling.
	 */
	private void fireEvent( EventObject event, Method method ) {
		java.util.Iterator	listenerPointer = m_listeners.iterator();
		Object[]			args = new Object[1];

		args[0] = event;
		while( listenerPointer.hasNext() ) {
			try {
				method.invoke( listenerPointer.next(), args );
			}
			catch( Exception exception ){
			}
		}
	}
}
