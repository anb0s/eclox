/*
	eclox : Doxygen plugin for Eclipse.
	Copyright (C) 2003-2004 Guillaume Brocker

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

package eclox.doxyfile;

import java.io.InputStream;

import eclox.doxyfile.node.Comment;
import eclox.doxyfile.node.Doxyfile;
import eclox.doxyfile.node.Group;
import eclox.doxyfile.node.Node;
import eclox.doxyfile.node.Section;
import eclox.doxyfile.node.Tag;

/**
 * Creates doxygen setting.
 * 
 * @author gbrocker
 */
public class Loader {
	/**
	 * The input object for the loader.
	 */
	private Tokenizer m_tokenizer;
	
	/**
	 * The class of the next node to create with the text buffer.
	 */
	private Class m_nextNodeClass = null;
	
	/**
	 * A string containing the text of the next node.
	 */
	private String m_nextNodeText = new String();

	/**
	 * The root node of the whole configuration file.
	 */
	private Doxyfile m_doxyfile = new Doxyfile();
	
	/**
	 * The object that is current group in the doxyfile.
	 */
	private Group m_currentGroup = null;
	
	/**
	 * Constructor.
	 * 
	 * @param	input	The input stream from which the settings must be constructed.
	 *  
	 * @author gbrocker
	 */
	public Loader( InputStream input ) throws LoaderException {
		m_tokenizer = new Tokenizer( input );
		load();
	}
	
	/**
	 * Retrieve the loaded doxyfile.
	 * 
	 * @return	The doxyfile node containing the loaded project.		 
	 */
	public Doxyfile getDoxyfile() {
		return m_doxyfile;
	}
	
	/**
	 * Load a doxygen file.
	 */
	private void load() throws LoaderException {
		try {
			int	tokenType;
			
			m_currentGroup = m_doxyfile;
			
			do {
				m_tokenizer.readToken();
				tokenType = m_tokenizer.getTokenType(); 
			
				switch( tokenType ) {
					case Tokenizer.NONE:
						createNextNode();
						break;
					
					case Tokenizer.EMPTY_LINE:
						createNextNode();
						break;
					
					case Tokenizer.COMMENT:
						processComment();
						break;
				
					case Tokenizer.TAG:
						processTag();
						break;

					case Tokenizer.TAG_INCREMENT:
						processTagIncrement();
						break;
						
					case Tokenizer.TAG_LIST_START:
						processTagListStart();
						break;
						
					case Tokenizer.TAG_LIST_ITEM:
						processTagListItem();
						break;

					case Tokenizer.TAG_LIST_END:
						processTagListEnd();
						break;

					case Tokenizer.SECTION_BORDER:
						processSectionBorder();
						break;
						
					default:
						throw new LoaderException(
							"Internal error. Unknown token type: " + String.valueOf(tokenType) + ".",
							null );
				}
			}
			while( tokenType != Tokenizer.NONE );
			m_doxyfile.setClean();
		}
		catch( LoaderException loaderException ) {
			throw loaderException;
		}
		catch( Throwable throwable ) {
			throw new LoaderException(
				"Error while parsing doxyfile. " + throwable.getMessage(),
				throwable ); 
		}
	}
	
	/**
	 * Create the next node from the owned next node class and text
	 * and store it in the current group.
	 *
	 * @todo	Throw a relevant exception when class not found.
	 */
	private void createNextNode() {
		// Create the next node.
		Node	result;
		
		if( m_nextNodeClass == Section.class ) {
			result = new Section( m_nextNodeText );
		}
		else if( m_nextNodeClass == Comment.class ) {
			result = new Comment( m_nextNodeText );
		}
		else if( m_nextNodeClass == Tag.class ) {
			result = new Tag( m_nextNodeText );
		}
		else {
			result = null;
		}
		
		// Store the next node.
		if( result != null ) {
			if( result.getClass() == Section.class ) {
				m_doxyfile.addChild( result );
				m_currentGroup = (Group) result;
			}
			else {
				m_currentGroup.addChild( result );				
			}
		}
		
		// Reset the next node class and text.
		m_nextNodeClass = null;
		m_nextNodeText = null;
	}
	
	/**
	 * Process the comment token waiting in the toknizer.
	 */
	private void processComment() {
		if( m_nextNodeClass == null ) {
			m_nextNodeClass = Comment.class;
			m_nextNodeText = m_tokenizer.getTokenText();
		}
		else if( m_nextNodeClass == Section.class ) {
			m_nextNodeText = m_nextNodeText.concat( m_tokenizer.getTokenText() );
		}
		else if( m_nextNodeClass == Comment.class ) {
			m_nextNodeText = m_nextNodeText.concat( m_tokenizer.getTokenText() );	
		}
		else {
			createNextNode();
			m_nextNodeClass = Comment.class;
			m_nextNodeText = m_tokenizer.getTokenText();
		}
	}
	
	/**
	 * Process the section border tag waiting in the tokenizer.
	 */
	private void processSectionBorder() {
		if( m_nextNodeClass == null ) {
			m_nextNodeClass = Section.class;
			m_nextNodeText = m_tokenizer.getTokenText();
		}
		else if( m_nextNodeClass == Section.class ) {
			m_nextNodeText = m_nextNodeText.concat( m_tokenizer.getTokenText() );
		}
		else {
			createNextNode();
			m_nextNodeClass = Section.class;
			m_nextNodeText = m_tokenizer.getTokenText();
		}
	}
	
	/**
	 * Process the tag token waiting in the toeknizer.
	 */
	private void processTag() {
		if( m_nextNodeClass == null ) {
			m_nextNodeClass = Tag.class;
			m_nextNodeText = m_tokenizer.getTokenText();
		}
		else {
			createNextNode();
			m_nextNodeClass = Tag.class;
			m_nextNodeText = m_tokenizer.getTokenText();
		}
	}
	
	/**
	 * Process the tag increment token waiting in the tokenizer.
	 */
	private void processTagIncrement() throws LoaderException {
		if( m_nextNodeClass == Tag.class ) {
			m_nextNodeText = m_nextNodeText.concat( m_tokenizer.getTokenText() );
		}
		else {
			throw new LoaderException(
				"Parse error at line " + m_tokenizer.getLine() + ": unexpected '" + m_tokenizer.getTokenText() + "'.",
				null );
		}
	}
	
	/**
	 * Process a tag list start token waiting in the toeknizer.
	 */
	private void processTagListStart() {
		if( m_nextNodeClass != null ) {
			createNextNode();
		}
		m_nextNodeClass = Tag.class;
		m_nextNodeText = m_tokenizer.getTokenText().replaceAll("[\\\\\r\n]", "");
	}
	
	/**
	 * Process a tag list item token waiting the tokenizer.
	 */
	private void processTagListItem() throws LoaderException {
		if( m_nextNodeClass != Tag.class ) {
			throw new LoaderException(
				"Parse error at line " + m_tokenizer.getLine() + ": unexpected '" + m_tokenizer.getTokenText() + "'.",
				null );
		}
		m_nextNodeText = m_nextNodeText.concat( " " + m_tokenizer.getTokenText().replaceAll("\\A\\s+|\\s+\\z|[\\\\\r\n]", "") );
	}
	
	/**
	 * Process a tag list end token waiting the tokenizer.
	 */
	private void processTagListEnd() throws LoaderException {
		if( m_nextNodeClass != Tag.class ) {
			throw new LoaderException(
				"Parse error at line " + m_tokenizer.getLine() + ": unexpected '" + m_tokenizer.getTokenText() + "'.",
				null );
		}
		m_nextNodeText = m_nextNodeText.concat( " " + m_tokenizer.getTokenText().replaceAll("\\A\\s+|\\s+\\z", "") );
	}
}
