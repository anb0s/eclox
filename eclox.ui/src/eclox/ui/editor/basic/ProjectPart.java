/*******************************************************************************
 * Copyright (C) 2003-2008, 2013, Guillaume Brocker
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

package eclox.ui.editor.basic;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import eclox.core.doxyfiles.Doxyfile;
import eclox.ui.editor.editors.CheckBoxEditor;
import eclox.ui.editor.editors.DirectoryEditor;
import eclox.ui.editor.editors.DirectoryListEditor;
import eclox.ui.editor.editors.TextEditor;

/**
 * Implements the part that will provide editior for the project settings.
 * 
 * @author gbrocker
 */
public class ProjectPart extends Part {

    /**
     * the project name setting editor
     */
    private TextEditor nameEditor = new TextEditor();

    /**
     * the project version or identifier editor
     */
    private TextEditor versionEditor = new TextEditor();

    /**
     * the project input editor
     */
    private DirectoryListEditor inputEditor = new DirectoryListEditor();

    /**
     * the recursive scan editor
     */
    private CheckBoxEditor recursiveEditor = new CheckBoxEditor("Scan recursively");

    /**
     * the project output editor
     */
    private DirectoryEditor outputEditor = new DirectoryEditor();

    /**
     * Constructor
     * 
     * @param	parent		the parent composite of the part content
     * @param	toolkit		the toolkit to use for control creations
     * @param	doxyfile	the doxyfile being edited
     */
    ProjectPart(Composite parent, FormToolkit toolkit, Doxyfile doxyfile) {
        super(parent, toolkit, "Project", doxyfile);

        addEditor("Name:", nameEditor);
        addEditor("Version or Identifier:", versionEditor);
        addSperator();
        addLabel("Input directories:");
        addEditor(inputEditor);
        addEditor(recursiveEditor);
        addSperator();
        addEditor("Output Directory:", outputEditor);

        nameEditor.setInput(doxyfile.getSetting("PROJECT_NAME"));
        versionEditor.setInput(doxyfile.getSetting("PROJECT_NUMBER"));
        inputEditor.setInput(doxyfile.getSetting("INPUT"));
        outputEditor.setInput(doxyfile.getSetting("OUTPUT_DIRECTORY"));
        recursiveEditor.setInput(doxyfile.getSetting("RECURSIVE"));

        nameEditor.setEnabled(nameEditor.hasInput());
        versionEditor.setEnabled(versionEditor.hasInput());
        inputEditor.setEnabled(inputEditor.hasInput());
        outputEditor.setEnabled(outputEditor.hasInput());
        recursiveEditor.setEnabled(recursiveEditor.hasInput());
    }

}
