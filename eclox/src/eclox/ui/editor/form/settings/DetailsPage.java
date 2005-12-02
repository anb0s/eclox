/*
 * eclox : Doxygen plugin for Eclipse.
 * Copyright (C) 2003-2004 Guillaume Brocker
 * 
 * This file is part of eclox.
 * 
 * eclox is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 * 
 * eclox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with eclox; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
 */

package eclox.ui.editor.form.settings;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.core.Services;
import eclox.doxyfiles.Setting;
import eclox.ui.editor.form.settings.editors.EditorClassRegister;
import eclox.ui.editor.form.settings.editors.IEditor;


/**
 * Implements the generic details node page.
 * 
 * @author gbrocker
 */
public class DetailsPage implements IDetailsPage {
    
    /**
     * The static setting editor class register.
     */
    private static EditorClassRegister editorClassRegister = new EditorClassRegister();
    
    /**
     * The setting editor instance.
     */
    private IEditor editor;

    /**
     * The section that contains all our controls.
     */
    private Section section;
    
    /**
     * The editor content container widget
     */
    private Composite editorContent;
    
    /**
     * The control containing all controls of the section.
     */
    private Composite sectionContent;
    
    /**
     * The managed form the page is attached to.
     */
    protected IManagedForm managedForm;
    
    /**
     * The label displaying the note text.
     */
    private Label noteLabel;

    /**
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents(Composite parent) {
        FormToolkit toolkit = this.managedForm.getToolkit();
        
        // Initializes the parent control.
        parent.setLayout(new FillLayout());
        
        // Creates the section
        this.section = toolkit.createSection(parent, Section.TITLE_BAR);
        this.section.marginHeight = 5;
        this.section.marginWidth = 10;
        this.section.setText("Setting Details");
        
        // Createst the section content and its layout
        this.sectionContent = toolkit.createComposite(section);
        this.section.setClient(this.sectionContent);
        GridLayout layout = new GridLayout(1, true);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        this.sectionContent.setLayout(layout);
        
        // Creates the editor content.
        this.editorContent = managedForm.getToolkit().createComposite(sectionContent);
        this.editorContent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                
        // Creates controls displaying the setting note.
        this.noteLabel = this.managedForm.getToolkit().createLabel( sectionContent, "", org.eclipse.swt.SWT.WRAP);
        this.noteLabel.setLayoutData( new GridData(GridData.FILL_BOTH) );
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit(boolean onSave) {
    	if( editor != null ) {
    		editor.commit();
    	}
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose() {}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form) {
        this.managedForm = form;
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#isDirty()
     */
    public boolean isDirty() {
    	return editor != null ? editor.isDirty() : false;
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#isStale()
     */
    public boolean isStale() {
        return false;
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh() {}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus() {
        this.editor.setFocus();
	}
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input) {
        return false;
    }
    
    /**
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IFormPart part, ISelection selection) {
    	// Retreieves the node that is provided by the selection.
    	Setting setting = null;
        if(selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            Object object = structuredSelection.getFirstElement();
            if(object instanceof Setting) {
                setting = (Setting) object;
            } 
        }
        
        // Updates the form controls.
        this.selectNote(setting);
        this.selectEditor(setting);
        this.sectionContent.layout(true);
    }
    
    /**
     * Disposes the current editor.
     */
    private void disposeEditor() {
        if(editor != null) {
        	editor.dispose();
            editor = null;
        }
    }
        
    /**
     * Selects the editor for the specified setting.
     * 
     * @param	input	the setting that is the new input
     */
    private void selectEditor(Setting input) {
        try
        {
	        // Retrieves the editor class for the input.
	        Class editorClass = editorClassRegister.find(input);
	        
	        // Perhaps should we remove the current editor.
	        if(editor != null && editor.getClass() != editorClass) {
	            disposeEditor();
	        }
	        
	        // Perhaps, we should create a new editor instance.
	        if(editor == null) {
		        editor = (IEditor) editorClass.newInstance();
		        editor.createContent(editorContent, managedForm.getToolkit());
	        }
	        
	        // Assigns the input to the editor.
	        editor.setInput(input);
        }
        catch(Throwable throwable) {
            Services.logWarning(throwable.getMessage());
        }
    }    

    /**
     * Updates the UI controls for the specified node.
     * 
     * @param	setting	a setting instance to use to refresh the UI controls.
     */
    private void selectNote(Setting setting) {
        String text = setting.getProperty( Setting.NOTE );
        if(text == null) {
        	text = new String("Not available.");
        }
        this.noteLabel.setText(text);
    }
}
