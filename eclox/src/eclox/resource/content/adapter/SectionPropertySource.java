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

package eclox.resource.content.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import eclox.resource.content.Section;

/**
 * Implements a property source for sections.
 * 
 * @author Guillaume Brocker
 */
public class SectionPropertySource extends NodePropertySource {
	/**
	 * The section for which property will retrieved.
	 */
	private Section section;
	
	/**
	 * The name property key.
	 */
	public static final String NAME_PROPERTY = "NAME";
	
	/**
	 * The tag number property key.
	 */
	public static final String TAG_NUMBER_PROPERTY = "TAG_NUMBER";
	
	/**
	 * Constructor.
	 * 
	 * @param	section	a section instance for which properties will be retrieved.
	 */
	public SectionPropertySource(Section section) {
		super(section);
		this.section = section;
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		Collection propertyDescriptors = new ArrayList();
		
		propertyDescriptors.addAll(Arrays.asList(super.getPropertyDescriptors()));
		propertyDescriptors.add(new PropertyDescriptor(NAME_PROPERTY, "name"));
		propertyDescriptors.add(new PropertyDescriptor(TAG_NUMBER_PROPERTY, "number of tags"));
		return (IPropertyDescriptor[]) propertyDescriptors.toArray(new IPropertyDescriptor[0]);
	}
	
	/**
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) {
		Object	result = null;
		
		if(id == NAME_PROPERTY) {
			result = this.section.getName();
		}
		else if(id == TAG_NUMBER_PROPERTY) {
			result = String.valueOf(this.section.getChildren().size());
		}
		else {
			result = super.getPropertyValue(id);
		}
		return result;
	}
}
