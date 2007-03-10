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

package eclox.ui.editor.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

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
	 * Constructor.
	 */
	public Page(Editor editor) {
		super(editor, Page.ID, "Basic");
	}
	
	
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}



	/**
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit	toolkit = managedForm.getToolkit();
		Composite	parent	= managedForm.getForm().getBody();
		
		managedForm.getForm().setText(this.getTitle());
		
		Section	projectSection	= toolkit.createSection( parent, Section.TITLE_BAR );
		Section	modeSection		= toolkit.createSection( parent, Section.TITLE_BAR );
		Section	outputSection	= toolkit.createSection( parent, Section.TITLE_BAR );
		Section	diagramsSection	= toolkit.createSection( parent, Section.TITLE_BAR );
		
		projectSection.setText("Project");
		projectSection.setLayoutData( new GridData(GridData.FILL_BOTH) );
		modeSection.setText("Mode");
		modeSection.setLayoutData( new GridData(GridData.FILL_BOTH) );
		outputSection.setText("Output");
		outputSection.setLayoutData( new GridData(GridData.FILL_BOTH) );
		diagramsSection.setText("Diagrams");
		diagramsSection.setLayoutData( new GridData(GridData.FILL_BOTH) );
		
		Composite	projectContent	 = toolkit.createComposite(projectSection);
		
		projectSection.setClient(projectContent);
		toolkit.createLabel(projectContent, "Provide some documentation about the project you are documenting.", SWT.WRAP);
		projectContent.setLayout( new FillLayout(SWT.VERTICAL) );
		
		GridLayout	layout = new GridLayout(2, true);
		parent.setLayout(layout);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		
		super.createFormContent(managedForm);
	}
}
