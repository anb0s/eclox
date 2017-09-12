/*******************************************************************************
 * Copyright (C) 2003-2004, 2013, Guillaume Brocker
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Brocker - Initial API and implementation
 *
 ******************************************************************************/

package eclox.ui.editor.advanced;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.viewers.ISelection;
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
import eclox.ui.editor.editors.SettingEditor;

/**
 * Implements the generic details node page.
 *
 * @author gbrocker
 */
public class DetailsPage implements IDetailsPage {

    /** symbolic name for emphasis font */
    private static final String EMPHASIS = "em";

    /** the static setting editor class register */
    private static EditorClassRegister editorClassRegister = new EditorClassRegister();

    /** the setting editor instance */
    private SettingEditor editor;

    /** the section that contains all our controls */
    private Section section;

    /** he editor content container widget */
    private Composite editorContainer;

    /** the control containing all controls of the section */
    private Composite sectionContent;

    /** the managed form the page is attached to */
    protected IManagedForm managedForm;

    /** the current selection */
    private NavigableSelection selection = new NavigableSelection();

    /** the control displaying the setting's note text */
    private FormText noteLabel;

    /** the font registry used for note text formatting */
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
        MyHyperlinkListener(DetailsPage owner) {
            this.owner = owner;
        }

        /**
         * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
         */
        public void linkActivated(HyperlinkEvent e) {
            // Pre-condition
            assert editor != null;

            Doxyfile doxyfile = editor.getInput().getOwner();
            Setting setting = doxyfile.getSetting(e.getHref().toString());

            if (setting != null) {
                managedForm.fireSelectionChanged(owner, selection.select(setting));
            }
        }

        /**
         * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
         */
        public void linkEntered(HyperlinkEvent e) {
        }

        /**
         * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
         */
        public void linkExited(HyperlinkEvent e) {
        }

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
        this.noteLabel = this.managedForm.getToolkit().createFormText(sectionContent, false);
        this.noteLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.noteLabel.setFont(EMPHASIS, fontRegistry.getItalic(""));
        this.noteLabel.addHyperlinkListener(new MyHyperlinkListener(this));

    }

    /**
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit(boolean onSave) {
        if (editor != null) {
            editor.commit();
        }
    }

    /**
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose() {
    }

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
        return editor != null ? editor.isStale() : false;
    }

    /**
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh() {
        if (editor != null) {
            editor.refresh();
        }
    }

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
    public void selectionChanged(IFormPart part, ISelection newSelection) {
        // Pre-condition
        assert (newSelection instanceof NavigableSelection);

        // Retreieves the node that is provided by the selection.
        /*if( part != this )*/ {
            selection = (NavigableSelection) newSelection;
            Object object = selection.getFirstElement();
            Setting setting = (object instanceof Setting) ? (Setting) object : null;
            String text = setting.getProperty(Setting.TEXT);

            // Checks that the setting has a text property.
            if (text == null) {
                Plugin.log(setting.getIdentifier() + ": missing TEXT property.");
                text = new String(setting.getIdentifier());
            }

            // Updates the form controls.
            this.selectNote(setting);
            this.selectEditor(setting);
            this.section.setText(text);
            this.section.layout(true, true);
        }
    }

    /**
     * Disposes the current editor.
     */
    private void disposeEditor() {
        if (editor != null) {
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
            Class<?> editorClass = editorClassRegister.find(input);

            // Perhaps should we remove the current editor.
            if (editor != null && editor.getClass() != editorClass) {
                disposeEditor();
            }

            // Perhaps, we should create a new editor instance.
            if (editor == null) {
                editor = (SettingEditor) editorClass.newInstance();
                editor.createContent(editorContainer, managedForm.getToolkit());
                editorContainer.setLayoutData(
                        new GridData(editor.grabVerticalSpace() ? GridData.FILL_BOTH : GridData.FILL_HORIZONTAL));
            }

            // Assigns the input to the editor.
            editor.setInput(input);
            editor.refresh();
        } catch (Throwable throwable) {
            MessageDialog.openError(this.managedForm.getForm().getShell(), "Unexpected Error", throwable.toString());
        }
    }

    /**
     * Updates the UI controls for the specified node.
     *
     * @param	setting	a setting instance to use to refresh the UI controls.
     */
    private void selectNote(Setting setting) {
        // Retrieves the setting's note text.
        String text = setting.getProperty(Setting.NOTE);

        // If there is none, build a default one.
        if (text == null) {
            text = "Not available.";
        }
        // Else do some parsing and replacements for layout and style.
        else {
            Doxyfile doxyfile = setting.getOwner();

            text = text.startsWith("<p>") ? text : "<p>" + text + "</p>";
            Matcher matcher = Pattern.compile("([A-Z_]{2,}|Warning:|Note:|@[a-z]+)").matcher(text);
            StringBuffer buffer = new StringBuffer();
            while (matcher.find()) {
                String match = matcher.group(1);

                if (match.equals("YES") || match.equals("NO")) {
                    matcher.appendReplacement(buffer, "<span font=\"" + EMPHASIS + "\">" + match + "</span>");
                } else if (match.equals("Note:") || match.equals("Warning:")) {
                    matcher.appendReplacement(buffer, "<b>" + match + "</b>");
                } else if (match.startsWith("@")) {
                    matcher.appendReplacement(buffer, "<span font=\"" + EMPHASIS + "\">" + match + "</span>");
                } else {
                    Setting matchSetting = doxyfile.getSetting(match);

                    if (matchSetting != null) {
                        String settingText = matchSetting.getProperty(Setting.TEXT);

                        if (matchSetting == setting) {
                            matcher.appendReplacement(buffer,
                                    "<span font=\"" + EMPHASIS + "\">" + settingText + "</span>");
                        } else {
                            matcher.appendReplacement(buffer,
                                    "<a href=\"" + matchSetting.getIdentifier() + "\">" + settingText + "</a>");
                        }
                    }
                }
            }
            matcher.appendTail(buffer);
            text = buffer.toString();
        }

        // Finally, assignes the text to the user interface control.
        this.noteLabel.setText("<form>" + text + "</form>", true, false);
    }
}
