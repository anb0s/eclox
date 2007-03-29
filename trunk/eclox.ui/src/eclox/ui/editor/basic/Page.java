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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.core.doxyfiles.Doxyfile;
import eclox.ui.editor.Editor;


/**
 * Implements the overview page.
 * 
 * @author gbrocker
 */
public class Page extends FormPage {
	
    /**
     * The page identifier.
     */
    public static final String ID = "basic";
    
    /**
     * the doxyfile being edited
     */
    private Doxyfile doxyfile;
    
    /**
	 * Constructor.
	 */
	public Page(Editor editor) {
		super(editor, Page.ID, "Basic");
		
		doxyfile = editor.getDoxyfile();
	}
	

	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit	toolkit = managedForm.getToolkit();
		Composite	parent	= managedForm.getForm().getBody();
		
		managedForm.getForm().setText(this.getTitle());

		ProjectPart		projectPart		= new ProjectPart( parent, toolkit, doxyfile );
		OutputPart		outputPart		= new OutputPart( parent, toolkit, doxyfile );
		ModePart		modePart		= new ModePart( parent, toolkit, doxyfile );
		DiagramsPart	diagramsPart	= new DiagramsPart( parent, toolkit, doxyfile );
		
		managedForm.addPart(projectPart);
		managedForm.addPart(outputPart);
		managedForm.addPart(modePart);
		managedForm.addPart(diagramsPart);
		
		projectPart.getSection().setLayoutData( new GridData(GridData.FILL_BOTH) );
		outputPart.getSection().setLayoutData( new GridData(GridData.FILL_BOTH) );
		modePart.getSection().setLayoutData( new GridData(GridData.FILL_BOTH) );
		diagramsPart.getSection().setLayoutData( new GridData(GridData.FILL_BOTH) );
		
		GridLayout	layout = new GridLayout(2, true);
		parent.setLayout(layout);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 0;
		layout.marginWidth = 10;
		layout.marginHeight = 10;

		super.createFormContent(managedForm);
	}
}
