// eclox : Doxygen plugin for Eclipse.
// Copyright (C) 2003-2005 Guillaume Brocker
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

package eclox.ui.editor.form.pages.settings;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.core.Services;
import eclox.doxyfiles.nodes.Node;
import eclox.doxyfiles.nodes.Setting;
import eclox.ui.editor.form.pages.settings.editors.EditorClassRegister;
import eclox.ui.editor.form.pages.settings.editors.IEditor;

/**
 * Implements the setting detail page.
 * 
 * @author gbrocker
 */
public class SettingDetailsPage extends NodeDetailsPage {

    /**
     * The static setting editor class register.
     */
    private static EditorClassRegister editorClassRegister = new EditorClassRegister(); 
    
    /**
     * The setting editor instance.
     */
    private IEditor editor;
    
    /**
     * The section widget.
     */
    private Section section;
    
    /**
     * The editor content container widget
     */
    private Composite editorContent;
    
    // Override
    public void createContents(Composite parent) {
        // Base class treatement.
        super.createContents(parent);
        
        FormToolkit toolkit = this.managedForm.getToolkit();
        
        // Creates the section
        section = toolkit.createSection(parent, Section.TITLE_BAR);
        section.marginHeight = 5;
        section.marginWidth = 10;
        section.setText("Value");
    }
    
    /**
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IFormPart part, ISelection selection) {
        super.selectionChanged(part, selection);
        selectEditor(getSettingFromSelection(selection));
    }
    
    /**
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input) {
        super.setFormInput(input);
        if(input instanceof Setting) {
            selectEditor((Setting) input);
        }
        return false;
    }
    
    /**
     * Disposes the current editor.
     */
    private void disposeEditor() {
        if(editor != null) {
            editorContent.dispose();
            editor = null;
            editorContent = null;
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
		        editorContent = managedForm.getToolkit().createComposite(section);
		        editor.createContent(editorContent, managedForm.getToolkit());
		        section.setClient(editorContent);
		        section.layout();
	        }
	        
	        // Assigns the input to the editor.
	        editor.setInput(input);
        }
        catch(Throwable throwable) {
            Services.logWarning(throwable.getMessage());
        }
    }
    
    /**
     * Retrieves the setting instance from the specified selection.
     * 
     * @param	selection	a selection instance
     * 
     * @return	a setting instance or null when none
     */
    private static Setting getSettingFromSelection(ISelection selection) {
        Setting result = null;
        Node node = getNodeFromSelection(selection);
        if(node instanceof Setting) {
            result = (Setting) node;
        }
        return result;
    }
    
}
