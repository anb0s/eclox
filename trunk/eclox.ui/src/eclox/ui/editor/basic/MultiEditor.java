// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2007 Guillaume Brocker
//
// This file is part of eclox.
//
// eclox is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// any later version.
//
// eclox is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with eclox; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	

package eclox.ui.editor.basic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import eclox.core.doxyfiles.Setting;
import eclox.ui.editor.editors.IEditor;

/**
 * Base implementation of for multi editors.
 * 
 * @author Guillaume Brocker
 */
public abstract class MultiEditor implements IEditor {
	
	/**
	 * symbolic constant value for yes
	 */
	private static final String YES = "YES";
	
	/**
	 * symbolic constant value for no
	 */
	private static final String NO = "NO";
	
	protected class State { 
		private String 	name;
		private Set		selectedSettings = new HashSet();
		private Set		deselectedSettings = new HashSet();
		
		State( String name ) {
			this.name = name;
		}
		
		String getName() {
			return name;
		}
		
		boolean wantsSelection() {
			boolean		wanted = true;
			Iterator	i;
			
			// Updates the selection according to the value of settings owned by the state.
			i = selectedSettings.iterator();
			while( i.hasNext() ) {
				Setting	setting = (Setting) i.next();
				
				wanted = wanted && setting.getValue().equals(YES);
			}
			
			// Updates the selection according to the value of settings owned by the state.
			i = deselectedSettings.iterator();
			while( i.hasNext() ) {
				Setting	setting	= (Setting) i.next();
				
				wanted = wanted && setting.getValue().equals(NO);
			}
			
			// Job's done.
			return wanted;
		}
		
		void setSelection( boolean selection ) {
			Iterator i;
			
			i = selectedSettings.iterator();
			while( i.hasNext() ) {
				Setting	setting = (Setting) i.next();
				
				setting.setValue(selection ? YES : NO);
			}

			i = deselectedSettings.iterator();
			while( i.hasNext() ) {
				Setting	setting = (Setting) i.next();
				
				setting.setValue(selection ? NO : YES);
			}
		}
		
	}
	
	/**
	 * the collection of managed states
	 */
	protected State [] states;
	
	/**
	 * the state being selected
	 */
	private State selection;
	
	/**
	 * a boolean telling if the editor is dirty
	 */
	private boolean dirty = false;
	
	/**
	 * Creates a new multi editor instance
	 * 
	 * @param states	an array containing the name of the states to create
	 */
	public MultiEditor( String [] states ) {
		this.states = new State[states.length];
		for( int i = 0; i != states.length; ++i ) {
			this.states[i] = new State(states[i]);
		}
	}
	
	/**
	 * Adds the given setting to a given state
	 * 
	 * @param	state	the name of a state
	 * @param	setting	the setting to add to the given state
	 */
	public void addSetting( String state, Setting setting ) {
		// Pre-condition
		assert setting != null;
		
		for( int i = 0; i != states.length; ++i ) {
			if( states[i].name.equals(state) ) {
				states[i].selectedSettings.add(setting);
			}
			else {
				states[i].deselectedSettings.add(setting);
			}
		}
	}	
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#commit()
	 */
	public void commit() {
		// Commits all states.
		for( int i = 0; i != states.length; ++i ) {
			states[i].setSelection( states[i] == selection );
		}
		dirty = false;
	}
	
	/**
	 * @see eclox.ui.editor.editors.IEditor#isDirty()
	 */
	public boolean isDirty() {
		return dirty;
	}

	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Sub-classes must call this method first, in order to refresh the current state
	 * and then refresh the user interface controls, according to the current state.
	 * 
	 * @see eclox.ui.editor.editors.IEditor#refresh()
	 */
	public void refresh() {
		selection = null;
		dirty = false;
		
		// Searches the states that wants to be selected
		for( int i = 0; i != states.length; ++i ) {
			if( states[i].wantsSelection() ) {
				selection = states[i];
				break;
			}
		}
	}
	
	/**
	 * Retrieves the selected state of the multi editor.
	 * 
	 * @return	a state or null when none
	 */
	protected State getSelection() {
		return selection;
	}
	
	/**
	 * Retrieves the state for the given name
	 * 
	 * @name	a string containing a state name
	 * 
	 * @return	the matching state or null when none
	 */
	protected State getState( String name ) {
		State	found = null;
		
		for( int i = 0; i != states.length; ++i ) {
			if( states[i].getName() .equals(name) ) {
				found = states[i];
				break;
			}
		}
		return found;
	}

	/**
	 * Selectes the given state
	 * 
	 * @param	state	a string containing a state name
	 */
	protected void selectState( String state ) {
		State	candidate = getState(state);		
		if( candidate != null ) {
			selection = candidate;
			dirty = true;
		}
	}

}
