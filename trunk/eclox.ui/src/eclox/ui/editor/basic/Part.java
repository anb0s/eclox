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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.core.doxyfiles.Doxyfile;
import eclox.ui.editor.editors.IEditor;

public class Part extends SectionPart {
	
	/**
	 * The content of the section control
	 */
	private Composite content;
	
	/**
	 * The toolkit used for control creation 
	 */
	private FormToolkit toolkit;
	
	/**
	 * The accumulation of separation spaces
	 */
	private int spacer = 0;

	/**
	 * the doxyfile being edited
	 */
	protected Doxyfile doxyfile;
	
	/**
	 * Contructor
	 * 
	 * @param	parent		the parent control
	 * @param	tk			the toolit used to create controls
	 * @param	title		the title of the part
	 * @param	doxyfile	the doxyfile being edited
	 */
	public Part(Composite parent, FormToolkit tk, String title, Doxyfile doxyfile) {
		super( parent, tk, Section.TITLE_BAR );
		
		this.doxyfile = doxyfile;
		
		// Initializes the section and its client component.
		Section		section	= getSection();
		GridLayout	layout = new GridLayout();
		
		toolkit = tk;
		content	= toolkit.createComposite(section);
		layout.numColumns		= 2;
		layout.marginTop		= 0;
		layout.marginRight		= 0;
		layout.marginBottom		= 0;
		layout.marginLeft		= 0;
		layout.marginHeight		= 0;
		layout.marginWidth		= 0;
		layout.verticalSpacing	= 2;
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
		Label		label	= toolkit.createLabel(content, text, SWT.WRAP);
		GridData	data	= new GridData(SWT.FILL, SWT.FILL, true, false);
		
		data.horizontalSpan = 2;
		data.verticalIndent = spacer;
		label.setLayoutData(data);
		spacer = 0;		
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
		GridData	labelData		= new GridData(SWT.FILL, SWT.CENTER, false, false); 
		GridData	containerData	= new GridData(SWT.FILL, SWT.FILL, true, false);
		
		labelData.verticalIndent = spacer;
		containerData.verticalIndent = spacer;
		label.setLayoutData(labelData);
		container.setLayoutData(containerData);
		editor.createContent(container, toolkit);
		spacer = 0;
	}
	
	/**
	 * Adds the given editor instance to the part
	 * 
	 * @param	editor	an editor instance
	 */
	protected void addEditor( IEditor editor ) {
		Composite	container	= toolkit.createComposite(content);
		GridData	data		= new GridData(SWT.FILL, SWT.FILL, true, false);
		
		data.verticalIndent = spacer;
		data.horizontalSpan = 2;
		editor.createContent(container, toolkit);
		container.setLayoutData(data);
		spacer = 0;
	}
	
	/**
	 * Adds a new separator to the part
	 */
	protected void addSperator() {
		spacer += 8;
	}

}
