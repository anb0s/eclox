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

package eclox.resource.content.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;

import eclox.resource.content.DoxyfileContent;
import eclox.resource.content.Description;
import eclox.resource.content.Section;
import eclox.resource.content.Tag;

/**
 * @author Guillaume Brocker
 */
public class Deserializer {
	/**
	 * The file to parse.
	 */
	private IFile file;
	
	/**
	 * the file tokenizer.
	 */
	private Tokenizer tokenizer;
				
	/**
	 * Constructor.
	 * 
	 * @param	file	the file to parse
	 * 
	 * @throws	ParseException	an error occured while parsing
	 */
	public Deserializer(IFile file) throws ParseException {
		try {
			this.file = file;
			this.tokenizer = new Tokenizer(file.getContents());
		}
		catch(Throwable throwable) {
			throw new ParseException("Unexpected error.", throwable); 
		}
	}
	
	/**
	 * Run the deserialization.
	 * 
	 * @return	a doxyfile instance
	 * 
	 * @throws	SyntaxError		error in the doxyfile syntax
	 * @throws	ParseException	error while parsing
	 */
	public DoxyfileContent createDoxyfile() throws SyntaxError, ParseException {
		DoxyfileContent result = this.parseDoxyfile();
		
		result.setClean();
		return result;
	}
	
	/**
	 * Parses a description comming from a doxyfile.
	 * 
	 * @return	a description instance
	 * 
	 * @throws ParseException
	 */
	private Description parseDescription() throws ParseException {
		try {
			Description	description = null;
			
			this.tokenizer.getNextToken();
			if(this.tokenizer.getTokenType() == Tokenizer.DESCRIPTION) {
				String line;
				line = this.tokenizer.getTokenText();
				this.tokenizer.expurgeToken();
				
				Description	bloc;
				bloc = this.parseDescription();		
				
				description = new Description(bloc != null ? line + bloc.toString() : line);
			}
			return description;
		}
		catch(IOException ioException) {
			throw new ParseException("Unexpected error.", ioException);
		}
	}
	
	/**
	 * Parses a doxyfile.
	 * 
	 * @return	the parsed doxyfile.
	 * 
	 * @throws	SyntaxError		bad syntax in the doxyfile. 
	 * @throws	ParseException	error while parsing
	 */
	private DoxyfileContent parseDoxyfile() throws SyntaxError, ParseException {
		try {
			Description	version = null;
			Description description = null;
			Collection sections = null;
			
			// Parses the doxyfile content.
			version = this.parseVersion();
			description = this.parseDescription();
			sections = this.parseSections();
			if(sections == null) {
				throw new SyntaxError("Syntax error.", this.tokenizer.getLine());
			}

			// Creation of the doxyfile.
			DoxyfileContent	doxyfile = new DoxyfileContent();
			doxyfile.setVersion(version);
			doxyfile.setDescription(description);
			doxyfile.addChildren(sections);
			return doxyfile;
		}
		catch(IOException ioException) {
			throw new ParseException("Unexpected error. " + ioException.getMessage(), ioException);
		}
	}
	
	/**
	 * Parses a docyfile version header.
	 * 
	 * @return	a description instance containing the doxyfile version.
	 * 
	 * @throws ParseException
	 */
	private Description parseVersion() throws ParseException, IOException {
		Description	version = null;
		
		this.tokenizer.getNextToken();
		if(this.tokenizer.getTokenType() == Tokenizer.VERSION) {
			version = new Description(this.tokenizer.getTokenText());
			this.tokenizer.expurgeToken();
		}
		return version;		
	}
	
	/**
	 * Parses a single section of the doxyfile.
	 * 
	 * @return	a section instance
	 * 
	 * @throws	ParseException	an error occured while parsing
	 */
	private Section parseSection() throws ParseException, IOException {
		Section section = null;
		Description	description = null;
		
		description = this.parseSectionDescription();
		if(description != null) {
			Collection tags = null;
			
			tags = this.parseTags();
			if(tags != null) {
				section = new Section();
				section.setDescription(description);
				section.addChildren(tags);
			}
		}
		return section;
	}
	
	/**
	 * Parses a section description.
	 * 
	 * @return	a description of a section
	 * 
	 * @throws ParseException	an error occured while parsing
	 */
	private Description parseSectionDescription() throws ParseException, IOException {
		Description description = null;
		
		this.tokenizer.getNextToken();
		if(this.tokenizer.getTokenType() == Tokenizer.SECTION_BORDER) {
			String	startLine;
			
			startLine = this.tokenizer.getTokenText();
			this.tokenizer.expurgeToken();
			
			Description subDescription;
			subDescription = this.parseDescription();
			
			this.tokenizer.getNextToken();
			if(this.tokenizer.getTokenType() == Tokenizer.SECTION_BORDER) {
				String	buffer = new String();
				
				buffer += startLine;
				buffer += subDescription.toString();
				buffer += this.tokenizer.getTokenText();
				
				this.tokenizer.expurgeToken();
				description = new Description(buffer);				
			}			
		}
		return description;
	}
	
	/**
	 * Parses all sections of a doxyfile.
	 * 
	 * @return	a collection containing section instances
	 * 
	 * @throws	ParseException	an error occured while parsing
	 */
	private Collection parseSections() throws ParseException, IOException {
		Collection sections = null;
		Section section = null;
				
		section = this.parseSection();
		if(section != null) {
			sections = new ArrayList();
			sections.add(section);
			
			Collection nextSections = this.parseSections();
			if(nextSections != null) {
				sections.addAll(nextSections);
			}
		}
		return sections;
	}
	
	/**
	 * Parses a single tag for the doxyfile
	 * 
	 * @return	a tag instance
	 * 
	 * @throws ParseException	an error occured while parsing
	 */
	private Tag parseTag() throws ParseException, IOException {
		Tag	tag = null;
		
		Description description = null;
		description = this.parseDescription();
		
		this.tokenizer.getNextToken();
		if(this.tokenizer.getTokenType() == Tokenizer.TAG) {
			tag = new Tag(this.tokenizer.getTokenText());
			this.tokenizer.expurgeToken();
			tag.setDescription(description);
		}		
		return tag;
	}
	
	/**
	 * Parses all tags of the doxyfile
	 * 
	 * @return a collection containing tag instances
	 * 
	 * @throws ParseException	an error occured while parsing
	 */
	private Collection parseTags() throws ParseException, IOException {
		Collection	tags = null;
		
		Tag	tag;
		tag = this.parseTag();
		if(tag != null) {
			tags = new ArrayList();
			tags.add(tag);
			
			Collection	nextTags;
			nextTags = this.parseTags();
			if(nextTags != null) {
				tags.addAll(nextTags);
			}
		}
		return tags;
	}
}
