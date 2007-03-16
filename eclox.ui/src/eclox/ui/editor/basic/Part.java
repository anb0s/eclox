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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.ui.editor.editors.IEditor;

public class Part extends SectionPart {
	
	/**
	 * The content of the section control
	 */
	Composite content;
	
	/**
	 * The latest control being added to the part
	 */
	Control latestControl;
	
	/**
	 * The toolkit used for control creation 
	 */
	FormToolkit toolkit;

	/**
	 * Contructor
	 * 
	 * @param	parent	the parent control
	 * @param	tk		the toolit used to create controls
	 * @param	title	the title of the part
	 */
	public Part(Composite parent, FormToolkit tk, String title) {
		super( parent, tk, Section.TITLE_BAR );
		
		// Initializes the section and its client component.
		Section		section	= getSection();
		FormLayout	layout = new FormLayout();
		
		toolkit = tk;
		content	= toolkit.createComposite(section);
		layout.spacing = 1;
		content.setLayout( layout );
		section.setText(title);
		section.setClient(content);
	}
	
	/**
	 * Adds a new label to the part
	 * 
	 * @param	text	the text of the label
	 */
	protected void addLabel( String text ) {
		Label		label = toolkit.createLabel(content, text, SWT.WRAP);
		
		label.setLayoutData(getFillFormData());
		latestControl = label;
	}
	
	/**
	 * Adds the given editor instance to the part, with the given label.
	 * 
	 * @param	text	a string containing a label text
	 * @param	editor	an editor instance
	 */
	protected void addEditor( String text, IEditor editor ) {
		Label		label			= toolkit.createLabel(content, text);
		Composite	container		= toolkit.createComposite(content);
		FormData	labelData		= getFillFormData();
		FormData	containerData	= getFillFormData();
		
		labelData.right = null;
		containerData.left = new FormAttachment(label, 5);
		label.setLayoutData(labelData);
		container.setLayoutData(containerData);
		editor.createContent(container, toolkit);
		latestControl = container;
	}
	
	/**
	 * Adds the given editor instance to the part
	 * 
	 * @param	editor	an editor instance
	 */
	protected void addEditor( IEditor editor ) {
		Composite	container = toolkit.createComposite(content);
		
		editor.createContent(container, toolkit);
		container.setLayoutData(getFillFormData());
		latestControl = container;
	}
	
	/**
	 * Adds a new separator to the part
	 */
	protected void addSperator() {
		Control		separator	= toolkit.createSeparator(content, 0);
		FormData	data		= getFillFormData();
		
		data.height = 4;
		separator.setLayoutData(data);
		latestControl = separator;
	}
	
	private FormData getFillFormData() {
		FormData	data = new FormData();
		
		data.top = latestControl != null ? new FormAttachment(latestControl, 0, SWT.BOTTOM) : new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);

		return data;
	}
	
}
