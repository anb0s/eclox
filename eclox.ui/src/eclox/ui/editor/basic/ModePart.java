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

/**
 * Implements the form part that provides edition of mode related settings.
 * 
 * @author Guillaume Brocker
 */
public class ModePart extends Part {
	
	/**
	 * state for the entities editor
	 */
	private static final String ENTITIES_DOCUMENTED = "documented entities only";

	/**
	 * state for the entities editor
	 */
	private static final String ENTITIES_ALL = "all entities";
	
//	/**
//	 * state for the optimized langage editor
//	 */
//	private static final String CXX = "C++";
//	
//	/**
//	 * state for the optimized langage editor
//	 */
//	private static final String JAVA = "Java";
//	
//	/**
//	 * state for the optimized langage editor
//	 */
//	private static final String C = "C";
//	
//	/**
//	 * state for the optimized langage editor
//	 */
//	private static final String C_SHARP = "C#";
	
	/**
	 * the editor used to configure entities whose documentation will be extracted
	 */
	private RadioMultiEditor entities = new RadioMultiEditor( new String[] {ENTITIES_DOCUMENTED, ENTITIES_ALL} );
	
	/**
	 * the editor used to configure cross-referenced source code inclusion
	 */
	private CheckMultiEditor crossReferenced = new CheckMultiEditor( "Include cross-referenced source code in the output" );
	
//	/**
//	 * the editor used to selected the optimized ouput langage
//	 */
//	private RadioMultiEditor optimizedLangage = new RadioMultiEditor( new String[] {CXX, JAVA, C, C_SHARP} );

	/**
	 * Constructor
	 * 
	 * @param parent	the parent control for the part control's
	 * @param toolkit	the toolkit to use for control creation
	 * @param doxyfile	the doxyfile being edited
	 */
	public ModePart( Composite parent, FormToolkit toolkit, Doxyfile doxyfile ) {
		super( parent, toolkit, "Mode", doxyfile );
		
		// Creates the content.
		addLabel("Select the desired extraction mode:");
		addEditor(entities);
		addEditor(crossReferenced);
//		addSperator();
//		addLabel("Optimize results for:");
//		addEditor( optimizedLangage );
		
		// Initialies the editors.
		entities.addSetting(ENTITIES_DOCUMENTED, doxyfile.getSetting("HIDE_UNDOC_MEMBERS") );
		entities.addSetting(ENTITIES_DOCUMENTED, doxyfile.getSetting("HIDE_UNDOC_CLASSES") );
		entities.addSetting(ENTITIES_ALL, doxyfile.getSetting("EXTRACT_ALL") );
		entities.addSetting(ENTITIES_ALL, doxyfile.getSetting("EXTRACT_PRIVATE") );
		entities.addSetting(ENTITIES_ALL, doxyfile.getSetting("EXTRACT_STATIC") );
		
		crossReferenced.addSetting(CheckMultiEditor.SELECTED, doxyfile.getSetting("SOURCE_BROWSER") );
		crossReferenced.addSetting(CheckMultiEditor.SELECTED, doxyfile.getSetting("REFERENCED_BY_RELATION") );
		crossReferenced.addSetting(CheckMultiEditor.SELECTED, doxyfile.getSetting("REFERENCES_RELATION") );
		crossReferenced.addSetting(CheckMultiEditor.SELECTED, doxyfile.getSetting("VERBATIM_HEADERS") );
		
//		optimizedLangage.addSetting(JAVA, doxyfile.getSetting("OPTIMIZE_OUTPUT_JAVA"));
//		optimizedLangage.addSetting(C, doxyfile.getSetting("OPTIMIZE_OUTPUT_FOR_C"));
//		optimizedLangage.addSetting(C_SHARP, doxyfile.getSetting("OPTIMIZE_OUTPUT_JAVA"));
//		optimizedLangage.addSetting(C_SHARP, doxyfile.getSetting("EXTRACT_STATIC"));
	}
}
