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

package eclox.doxyfile.adapter;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import eclox.doxyfile.node.Tag;

/**
 * Implement a tag node property source.
 * 
 * @author Guillaume Brocker
 */
public class TagPropertySource implements IPropertySource {
	/**
	 * The tag for which properties are retrieved.
	 */
	private Tag tag;
	
	/**
	 * Name property key
	 */
	public static final String NAME_PROPERTY = "NAME";
	
	/**
	 * Type property key.
	 */
	public static final String TYPE_PROPERTY = "TYPE";
	
	/**
	 * Constructor.
	 * 
	 * @param	tag	the tag instance for which properties must be retrieved.
	 */
	public TagPropertySource(Tag tag) {
		this.tag = tag;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	public Object getEditableValue() {
		return null;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor result[] = new IPropertyDescriptor[2];
		
		result[0] = new PropertyDescriptor(NAME_PROPERTY, "name");
		result[1] = new PropertyDescriptor(TYPE_PROPERTY, "type");
		return result;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object	result = null;
		
		if(id == NAME_PROPERTY) {
			result = this.tag.getName();
		}
		else if(id == TYPE_PROPERTY) {
			result = new String("unknown");
		}
		return result;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	public boolean isPropertySet(Object id) {
		if(id == NAME_PROPERTY || id == TYPE_PROPERTY) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	public void resetPropertyValue(Object id) {}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) {}
}
