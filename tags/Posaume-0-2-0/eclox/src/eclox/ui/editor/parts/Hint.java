/*
	eclox
	Copyright (C) 2003 Guillaume Brocker

	This file is part of eclox.

	eclox is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	any later version.

	eclox is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with eclox; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA	
*/

package eclox.ui.editor.parts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import eclox.doxyfile.node.Comment;
import eclox.doxyfile.node.Node;
import eclox.doxyfile.node.Section;
import eclox.doxyfile.node.Tag;

/**
 * Implement the tag detail editor part.
 * 
 * @author gbrocker
 */
public class Hint extends Part {
	/**
	 * Implement a selection listener for the tags management part.
	 * 
	 * @author gbrocker
	 */
	private class TagsSelectionListener implements SelectionListener {
		/**
		 * Process the tags selection changes.
		 * 
		 * @param	event	The selection change event to process.
		 */
		public void selectionChanged( SelectionEvent event ) {
			Tag		tag = (Tag) event.selection.getSelected();
			String	hint = null;
			
			if( tag != null ) {			
				Comment	comment = (Comment) m_comments.get( tag );
				
				hint = comment != null ? comment.getText() : "No hint available.";
			}
			else {
				hint = "No tag selected.";
			}
			m_hintCtrl.setText( hint );
		}
	}
	
	/**
	 * Implement a selection listener for the sections management part.
	 * 
	 * @author gbrocker
	 */
	private class SectionsSelectionListener implements SelectionListener {
		/**
		 * Process the section selection changes.
		 * 
		 * @param	event	The selection change event to process.
		 */
		public void selectionChanged( SelectionEvent event ) {
			Section	section = (Section) event.selection.getSelected();
			
			if( section != null ) {
				m_comments.clear();
				
				// Get all comments from teh current section.
				Iterator	nodeIterator = section.getChildren().iterator();
				Comment		comment = null;
				while( nodeIterator.hasNext() ) {
					Node	curNode = (Node) nodeIterator.next();
					
					if( curNode.getClass() == Comment.class ) {
						comment = (Comment) curNode;
					}
					else if( curNode.getClass() == Tag.class && comment != null ) {
						m_comments.put( curNode, comment );
						comment = null;
					}
				}
			}
		}
	}
	
	/**
	 * The control that displays the tag hint.
	 */
	private Text m_hintCtrl;
	
	/**
	 * The map containing comments index by tag for the selected section.
	 */
	private Map m_comments = new HashMap();
	
	/**
	 * Constructor.
	 * 
	 * @param	sections	The section part to attach to.
	 * @param	tags		The tag part to attach to.
	 */
	public Hint( Sections sections, Tags tags ) {
		sections.selection.addSelectionListener( new SectionsSelectionListener() );
		tags.selection.addSelectionListener( new TagsSelectionListener() );
	}
	
	/**
	 * Ask the part to create its control.
	 */
	public void createContent() {
		m_hintCtrl = new Text( getContentParent(), SWT.MULTI|SWT.WRAP|SWT.V_SCROLL|SWT.READ_ONLY );
		setContent( m_hintCtrl );
		//setImage( new Image( Display.getCurrent(), getClass().getResourceAsStream("help.gif")));
		setTitle( "Hint" );		
	}
}
