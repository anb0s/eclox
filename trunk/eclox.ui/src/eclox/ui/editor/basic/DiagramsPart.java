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
import eclox.ui.editor.editors.IEditor;
import eclox.ui.editor.editors.IEditorListener;

/**
 * Implements a art that provides editors for diagram generation controlling.
 * 
 * @author Guillaume Brocker
 */
public class DiagramsPart extends Part {

	/**
	 * Implements an editor listener that will trigger enabled state updates.
	 */
	private class MyEditorListener implements IEditorListener {

		/**
		 * @see eclox.ui.editor.editors.IEditorListener#editorChanged(eclox.ui.editor.editors.IEditor)
		 */
		public void editorChanged(IEditor editor) {
			// Pre-condition
			assert editor == diagrams;
			
			final boolean	enabled	= diagrams.getSelection().equals(DIAGRAM_DOT_TOOL); 
			
			classGraph.setEnabled(enabled);
			collaborationGraph.setEnabled(enabled);
			includeGraph.setEnabled(enabled);
			includedByGraph.setEnabled(enabled);
			graphicalHierarcy.setEnabled(enabled);
			callGraph.setEnabled(enabled);
		}
		
	}
	
	/**
	 * symbolic constant for diagram activation's state
	 */
	private static final String DIAGRAM_NONE = "No diagrams";

	/**
	 * symbolic constant for diagram activation's state
	 */
	private static final String DIAGRAM_BUILT_IN = "Use built-in diagram generator";
	
	/**
	 * symbolic constant for diagram activation's state
	 */
	private static final String DIAGRAM_DOT_TOOL = "Use dot tool from the GraphViz package to generate:";
	
	/**
	 * the editor for diagram activation
	 */
	private MultiEditor diagrams = new RadioMultiEditor( new String[] {DIAGRAM_NONE, DIAGRAM_BUILT_IN, DIAGRAM_DOT_TOOL} );
	
	/**
	 * the editor for the dot class graph generation
	 */
	private CheckBoxEditor classGraph = new CheckBoxEditor("class diagrams");

	/**
	 * the editor for the dot collaboration graph generation
	 */
	private CheckBoxEditor collaborationGraph = new CheckBoxEditor("collaboration diagrams");

	/**
	 * the editor for the dot include graph generation
	 */
	private CheckBoxEditor includeGraph = new CheckBoxEditor("include dependency graph");

	/**
	 * the editor for the dot included by graph generation
	 */
	private CheckBoxEditor includedByGraph = new CheckBoxEditor("included by dependency graph");

	/**
	 * the editor for the dot hierarchy graph generation
	 */
	private CheckBoxEditor graphicalHierarcy = new CheckBoxEditor("overall class hierarchy");

	/**
	 * the editor for the dot call graph generation
	 */
	private CheckBoxEditor callGraph = new CheckBoxEditor("call graphs");
	
	
	/**
	 * Constructor
	 * 
	 * @param parent	the parent composite
	 * @param toolkit	the toolkit to use for control creations
	 * @param doxyfile	the doxyfile being edited
	 */
	DiagramsPart( Composite parent, FormToolkit toolkit, Doxyfile doxyfile ) {
		super( parent, toolkit, "Diagrams to Generate", doxyfile );
	
		// Creates all editors.
		addEditor( diagrams );
		addEditor( classGraph, 16 );
		addEditor( collaborationGraph, 16 );
		addEditor( includeGraph, 16 );
		addEditor( includedByGraph, 16 );
		addEditor( graphicalHierarcy, 16 );
		addEditor( callGraph, 16 );
		
		// Setup all editors.
		diagrams.addListener(new MyEditorListener());
		diagrams.addSetting(DIAGRAM_BUILT_IN, doxyfile.getSetting("CLASS_DIAGRAMS"));
		diagrams.addSetting(DIAGRAM_DOT_TOOL, doxyfile.getSetting("HAVE_DOT"));
		
		classGraph.setInput(doxyfile.getSetting("CLASS_GRAPH"));
		
		collaborationGraph.setInput(doxyfile.getSetting("COLLABORATION_GRAPH"));
		
		includeGraph.setInput(doxyfile.getSetting("INCLUDE_GRAPH"));
		
		includedByGraph.setInput(doxyfile.getSetting("INCLUDED_BY_GRAPH"));
		
		graphicalHierarcy.setInput(doxyfile.getSetting("GRAPHICAL_HIERARCHY"));
		
		callGraph.setInput(doxyfile.getSetting("CALL_GRAPH"));
	}
}
