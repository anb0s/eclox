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
	 * the recursvie scan editor
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
	ProjectPart( Composite parent, FormToolkit toolkit, Doxyfile doxyfile ) {
		super( parent, toolkit, "Project", doxyfile );
		
		addLabel("Provide some documentation about the project you are documenting.");
		addEditor("Name:", nameEditor);
		addEditor("Version or Identifier:", versionEditor);
		addSperator();
		addLabel("Specify the directories to scan for source coce.");
		addEditor(inputEditor);
		addEditor(recursiveEditor);
		addSperator();
		addLabel("Specify the directory where doxygen should put the generated documentation.");
		addEditor(outputEditor);

		nameEditor     .setInput( doxyfile.getSetting("PROJECT_NAME")		);
		versionEditor  .setInput( doxyfile.getSetting("PROJECT_NUMBER")		);
		inputEditor    .setInput( doxyfile.getSetting("INPUT")				);
		outputEditor   .setInput( doxyfile.getSetting("OUTPUT_DIRECTORY")	);
		recursiveEditor.setInput( doxyfile.getSetting("RECURSIVE")			);
	}
	
}
