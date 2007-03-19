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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import eclox.core.doxyfiles.Doxyfile;

public class OutputPart extends Part {

	OutputPart( Composite parent, FormToolkit toolkit, Doxyfile doxyfile ) {
		super( parent, toolkit, "Output", doxyfile );
		
		Section		section	= getSection();
		Composite	content	= toolkit.createComposite(section);
		
		toolkit.createLabel(content, "Provide some documentation about the project you are documenting.", SWT.WRAP);

		content.setLayout( new FillLayout(SWT.VERTICAL) );
		
				
	}
}
