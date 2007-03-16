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

import eclox.ui.editor.editors.DirectoryEditor;
import eclox.ui.editor.editors.DirectoryListEditor;
import eclox.ui.editor.editors.TextEditor;

public class ProjectPart extends Part {

	ProjectPart( Composite parent, FormToolkit toolkit ) {
		super( parent, toolkit, "Project" );
		
		addLabel("Provide some documentation about the project you are documenting.");
		addEditor("Project Name:", new TextEditor());
		addEditor("Project Version or Identifer:", new TextEditor());
		addSperator();
		addLabel("Specify the directories to scan for source coce.");
		addEditor(new DirectoryListEditor());
		addSperator();
		addLabel("Specify the directory where doxygen should put the generated documentation.");
		addEditor(new DirectoryEditor());
	}
}
