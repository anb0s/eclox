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

package eclox.ui.editor.advanced;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.core.doxyfiles.Doxyfile;
import eclox.core.doxyfiles.Setting;
import eclox.ui.Plugin;
import eclox.ui.editor.advanced.editors.EditorClassRegister;
import eclox.ui.editor.advanced.editors.IEditor;


/**
 * Implements the generic details node page.
 * 
 * @author gbrocker
 */
public class DetailsPage implements IDetailsPage {
    
	/**
	 * Symbolic name for emphasis font.
	 */
	private static final String EMPHASIS = "em";
	
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
    private Composite editorContainer;
    
    /**
     * The control containing all controls of the section.
     */
    private Composite sectionContent;
    
    /**
     * The managed form the page is attached to.
     */
    protected IManagedForm managedForm;
    
    /**
     * the control displaying the setting's note text
     */
    private FormText noteLabel;
    
    /**
     * the font registry used for note text formatting.
     */
    private FontRegistry fontRegistry;
    
    /**
     * Defines the listeners that will managed activation of hyper-links in the
     * setting's note.
     */
    private class MyHyperlinkListener implements IHyperlinkListener {

    	private DetailsPage owner;
    	
    	/**
    	 * Constructor
    	 * 
    	 * @param owner	the owner of the instance
    	 */
    	MyHyperlinkListener( DetailsPage owner ) {
    		this.owner = owner;
    	}
    	
		/**
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		public void linkActivated(HyperlinkEvent e) {
			Doxyfile	doxyfile	= (Doxyfile) managedForm.getInput();
			Setting		setting		= doxyfile.getSetting( e.getHref().toString() ); 
			
			if( setting != null ) {
				managedForm.fireSelectionChanged( owner, new StructuredSelection(setting)  );
			}
		}

		/**
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		public void linkEntered(HyperlinkEvent e) {}

		/**
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		public void linkExited(HyperlinkEvent e) {}
    	
    }

    /**
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents(Composite parent) {
        FormToolkit toolkit = this.managedForm.getToolkit();
        
        fontRegistry = new FontRegistry(parent.getDisplay());
        
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
        this.editorContainer = managedForm.getToolkit().createComposite(sectionContent);
        this.editorContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                
        // Creates controls displaying the setting note.
        this.noteLabel = this.managedForm.getToolkit().createFormText( sectionContent, false );
        this.noteLabel.setLayoutData( new GridData(GridData.FILL_HORIZONTAL) );
        this.noteLabel.setFont( EMPHASIS, fontRegistry.getItalic("") );
        this.noteLabel.addHyperlinkListener( new MyHyperlinkListener(this) );
        
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
    	// Pre-condition
    	assert this.editor != null;
    		
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
    	// Pre-condition
    	assert (selection instanceof IStructuredSelection);
    	
		// Retreieves the node that is provided by the selection.
    	if( part != this ) {
	    	IStructuredSelection	stSelection	= (IStructuredSelection) selection;
	        Object					object		= stSelection.getFirstElement();
		    Setting					setting		= (object instanceof Setting) ? (Setting) object : null;
	        
	        // Updates the form controls.
	        this.selectNote(setting);
	        this.selectEditor(setting);
	        this.sectionContent.layout( true, true );
    	}
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
        try {
	        // Retrieves the editor class for the input.
	        Class editorClass = editorClassRegister.find(input);
	        
	        // Perhaps should we remove the current editor.
	        if(editor != null && editor.getClass() != editorClass) {
	            disposeEditor();
	        }
	        
	        // Perhaps, we should create a new editor instance.
	        if(editor == null) {
		        editor = (IEditor) editorClass.newInstance();
		        editor.createContent(editorContainer, managedForm.getToolkit());
		        editorContainer.setLayoutData( new GridData(editor.fillVertically() ? GridData.FILL_BOTH : GridData.FILL_HORIZONTAL) );
	        }
	        
	        // Assigns the input to the editor.
	        editor.setInput(input);
        }
        catch(Throwable throwable) {
            Plugin.showError(throwable);
        }
    }    

    /**
     * Updates the UI controls for the specified node.
     * 
     * @param	setting	a setting instance to use to refresh the UI controls.
     */
    private void selectNote(Setting setting) {
    	// Retrieves the setting's note text.
        String text = setting.getProperty( Setting.NOTE );
        
        // If there is none, build a default one.
        if(text == null) {
        	text = "Not available.";
        }
        // Else do some parsing and replacements for layout and style. 
        else {
        	Doxyfile	doxyfile = setting.getOwner();
        	
        	text = text.startsWith("<p>") ? text : "<p>"+text+"</p>";
        	Matcher			matcher = Pattern.compile("([A-Z_]{2,}|Note:|@[a-z]+)").matcher(text);
        	StringBuffer	buffer = new StringBuffer();
        	while( matcher.find() ) {
        		String	match = matcher.group(1);
        		
        		if( match.equals("YES") || match.equals("NO") ) {
        			matcher.appendReplacement( buffer, "<span font=\""+EMPHASIS+"\">"+match+"</span>");
        		}
        		else if( match.equals("Note:") ) {
        			matcher.appendReplacement( buffer, "<b>"+match+"</b>");
        		}
        		else if( match.startsWith("@") ) {
        			matcher.appendReplacement( buffer, "<span font=\""+EMPHASIS+"\">"+match+"</span>");
        		}
        		else {
        			Setting	matchSetting	= doxyfile.getSetting(match);
        			
        			if( matchSetting != null ) {
	        			String	settingText		= matchSetting.getProperty(Setting.TEXT);
	        			
	        			if( matchSetting == setting ) {
	        				matcher.appendReplacement( buffer, "<span font=\""+EMPHASIS+"\">"+settingText+"</span>");
	        			}
	        			else {
	        				matcher.appendReplacement( buffer, "<a href=\""+matchSetting.getIdentifier()+"\">"+settingText+"</a>");
	        			}
        			}
        		}
        	}
        	matcher.appendTail( buffer );
        	text = buffer.toString();
        }
        
        // Finally, assignes the text to the user interface control.
        this.noteLabel.setText("<form>"+text+"</form>", true, false);
    }
}
