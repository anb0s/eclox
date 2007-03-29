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

public class OutputPart extends Part {

	/**
	 * Implements an editor listener that will update
	 * enabled states of some editors according to the value
	 * of some other editors.
	 */
	private class MyEditorListener implements IEditorListener {
		
		/**
		 * @see eclox.ui.editor.editors.IEditorListener#editorDirtyChanged(eclox.ui.editor.editors.IEditor)
		 */
		public void editorChanged(IEditor editor) {
			if( editor == html ) {
				htmlFlavour.setEnabled( ((CheckBoxEditor)editor).getValue() );
				htmlSearchEngine.setEnabled( ((CheckBoxEditor)editor).getValue() );
			}
			else if( editor == latex ) {
				latexFlavour.setEnabled( ((CheckBoxEditor)editor).getValue() );
			}
		}
		
	}
	
	/**
	 * html flavour state
	 */
	private static final String PLAIN_HTML = "plain HTML";
	
	/**
	 * html flavour state
	 */
	private static final String FRAME_HTML = "with frames and navigation tree";
	
	/**
	 * html flavour state
	 */
	private static final String CHM_HTML = "prepared for comporessed HTML (.chm)";
	
	/**
	 * latex flavour for hyperlinked pdf state
	 */
	private static final String LATEX_HYPEDLINKED_PDF = "as itermediate format for hypedlinked PDF";
	
	/**
	 * latex flavour for pdf state
	 */
	private static final String LATEX_PDF = "as itermediate format for PDF";
	
	/**
	 * latex flavour for PostScript state
	 */
	private static final String LATEX_POSTSCRIPT = "as itermediate format for PostScript";
	
	/**
	 * html editor
	 */
	CheckBoxEditor html = new CheckBoxEditor("HTML");
	
	/**
	 * html flavour editor
	 */
	RadioMultiEditor htmlFlavour = new RadioMultiEditor( new String[] {PLAIN_HTML, FRAME_HTML, CHM_HTML} );
	
	/**
	 * html search engine editor
	 */
	CheckBoxEditor htmlSearchEngine = new CheckBoxEditor( "with search function (requires PHP enabled server)" );
	
	/**
	 * latex editor
	 */
	CheckBoxEditor latex = new CheckBoxEditor("LaTeX");
	
	/**
	 * latex flavour editor
	 */
	RadioMultiEditor latexFlavour = new RadioMultiEditor( new String[] {LATEX_HYPEDLINKED_PDF, LATEX_PDF, LATEX_POSTSCRIPT} );
	
	CheckBoxEditor man = new CheckBoxEditor("Man Pages");
	CheckBoxEditor rtf = new CheckBoxEditor("Rich Text Format");
	CheckBoxEditor xml = new CheckBoxEditor("XML");				

	public OutputPart( Composite parent, FormToolkit toolkit, Doxyfile doxyfile ) {
		super( parent, toolkit, "Output Formats", doxyfile );
		
		addEditor( html );
		addEditor( htmlFlavour, 16 );
		addEditor( htmlSearchEngine, 16 );
		
		addEditor( latex );
		addEditor( latexFlavour, 16 );
		
		addEditor( man );
		addEditor( rtf );
		addEditor( xml );
		
		html.addListener(new MyEditorListener());
		html.setInput( doxyfile.getSetting("GENERATE_HTML") );
		
		htmlFlavour.addSetting(FRAME_HTML, doxyfile.getSetting("GENERATE_TREEVIEW") );
		htmlFlavour.addSetting(CHM_HTML, doxyfile.getSetting("GENERATE_HTMLHELP") );
		
		htmlSearchEngine.setInput( doxyfile.getSetting("SEARCHENGINE") );
		
		latex.addListener(new MyEditorListener());
		latex.setInput( doxyfile.getSetting("GENERATE_LATEX") );
		
		latexFlavour.addSetting(LATEX_HYPEDLINKED_PDF, doxyfile.getSetting("PDF_HYPERLINKS") );
		latexFlavour.addSetting(LATEX_HYPEDLINKED_PDF, doxyfile.getSetting("USE_PDFLATEX") );
		latexFlavour.addSetting(LATEX_PDF, doxyfile.getSetting("USE_PDFLATEX") );
		
		man.setInput( doxyfile.getSetting("GENERATE_MAN") );
		rtf.setInput( doxyfile.getSetting("GENERATE_RTF") );
		xml.setInput( doxyfile.getSetting("GENERATE_XML") );
	}
}
