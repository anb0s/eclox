package eclox.doxyfile.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @brief	Implement the doxyfile tokenizer.
 * 
 * @author	Guillaume Brocker
 */
class Tokenizer {
	/**
	 * Define the end token constant.
	 */
	public static final String END = "End";
	
	/**
	 * Define the version token constant.
	 */
	public static final String VERSION = "Version";
	
	/**
	 * Define the version token constant.
	 */
	public static final String DESCRIPTION = "Description";
	
	/**
	 * Define the tag token constant.
	 */
	public static final String TAG = "Tag";
	 
	/**
	 * Define the tag increment token constant.
	 */
	public static final String TAG_INCREMENT = "TagIncrement";
	
	/**
	 * Define the section border constant.
	 */
	public static final String SECTION_BORDER = "SectionBorder";
	
	/**
	 * The buffered reader from which text will be retrieved and parsed.
	 */
	private BufferedReader m_reader;
	
	/**
	 * The next token text.
	 */
	private String m_tokenText;
	
	/**
	 * The next token type.
	 */
	private String m_tokenType;
	
	/**
	 * An integer containing the current line number.
	 */
	private int m_lineNumber = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param	input	The input stream to split into token.	
	 */
	public Tokenizer( InputStream input ) {
		m_reader = new BufferedReader( new InputStreamReader( input ) );
	}
	
	/**
	 * Retrieve the current tokenizer line.
	 * 
	 * @return	An integer containing the line number.
	 */
	public int getLine() {
		return m_lineNumber;
	}
	
	/**
	 * Expurge the current token.
	 */
	public void expurgeToken() {
		this.m_tokenType = null;
		this.m_tokenText = null;
	}
	
	/**
	 * Retrieves the next token.
	 * 
	 * @throws	IOException	an error occured while retrieving the next token.
	 */
	public void getNextToken() throws IOException {
		if(m_tokenType == null) {
			this.readToken();
		}
	}
	
	/**
	 * Retrieve the current token text.
	 * 
	 * @return	A string containing the token text, or null if the
	 * 			end of the stream has been reached.
	 */
	public String getTokenText() {
		return m_tokenText;
	}
	
	/**
	 * Retrieve the current token type.
	 * 
	 * @return	the current token type.
	 */
	public String getTokenType() {
		return m_tokenType;
	}
	
	/**
	 * Read the input and get the next token.
	 * 
	 * @return	the type of the token that has been read.
	 */
	private Object readToken() throws SyntaxError, IOException {
		for(;;) {
			String	text = readLine();
			
			if( text != null ) {
				m_tokenText = text;
				if( text.matches("# Doxyfile.+\r?\n") ) {
					m_tokenType = VERSION;
				}
				else if( text.matches("\\w+\\s*=.+\r?\n") ) {
					m_tokenType = TAG;
				}
				else if( text.matches("\\w+\\s+\\+=.+\r?\n")) {
					m_tokenType = TAG_INCREMENT;
				}
				else if( text.matches("#-+\r?\n") ) {
					m_tokenType = SECTION_BORDER;
				}
				else if( text.matches("#.*\r?\n") ) {
					m_tokenType = DESCRIPTION;
				}
				else if( text.matches("\r?\n") ) {
					continue;
				}
				else {
					throw new SyntaxError("Syntax error.", this.getLine());
				}
				break;
			}
			else if( text == null ){
				m_tokenText = null;
				m_tokenType = END;
				break;
			}
		}
		return this.m_tokenType;
	}
	
	/**
	 * Helper that reads the next line of text in the input stream.
	 * 
	 * @return	A string containing the next line of text or null if the 
	 * 			end of the stream has been reached.
	 */
	private String readLine() throws IOException {
		String line = new String();
		
		for(;;)
		{
			int	nextChar = m_reader.read();
			
			if( nextChar == -1 ) {
				break;
			}
			else {
				line = line.concat( String.valueOf( (char) nextChar ) );
				if( line.matches(".*\r?\n") ) {
					m_lineNumber++;
					break;
				}
			}
		}
		return line.length() != 0 ? line : null;
	}
}