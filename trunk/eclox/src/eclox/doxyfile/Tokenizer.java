package eclox.doxyfile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @brief	Implement the doxyfile tokenizer.
 * 
 * @author	Guillaume Brocker
 */
class Tokenizer {
	/**
	 * Define the empty token constant.
	 */
	public static final int NONE = 0;
	
	/**
	 * Define the tag token constant.
	 */
	public static final int TAG = 1;
	 
	/**
	 * Define the tag increment token constant.
	 */
	public static final int TAG_INCREMENT = 2;
	
	/**
	 * Define the comment token constant.
	 */
	public static final int COMMENT = 3;
	 
	/**
	 * Define the section border constant.
	 */
	public static final int SECTION_BORDER = 4;
	
	/**
	 * The input stream to split into tokens.
	 */
	private InputStream m_input;
	
	/**
	 * The next token text.
	 */
	private String m_tokenText = null;
	
	/**
	 * The next token type.
	 */
	private int m_tokenType = NONE;
	
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
		m_input = input;
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
	 * @return	An integer representing the current token type.
	 */
	public int getTokenType() {
		return m_tokenType;
	}
	
	/**
	 * Read the input and get the next token.
	 */
	public void readToken() throws IOException {
		for(;;) {
			String	text = readLine();
			
			if( text != null && text != "\r\n" ) {
				m_tokenText = text;
				if( text.matches("\\w+\\s+=.*") ) {
					m_tokenType = TAG;
				}
				else if( text.matches("\\w+\\s+\\+=.*")) {
					m_tokenType = TAG_INCREMENT;
				}
				else if( text.matches("#-+.*") ) {
					m_tokenType = SECTION_BORDER;
				}
				else if( text.matches("#.*") ) {
					m_tokenType = COMMENT;
				}
				m_lineNumber++;
				break;
			}
			else if( text == null ){
				m_tokenText = null;
				m_tokenType = NONE;
				break;
			}
		}
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
			int	nextChar = m_input.read();
			
			if( nextChar == -1 ) {
				break;
			}
			else {
				line = line.concat( String.valueOf( (char) nextChar ) );
				if( line.endsWith("\r\n") ) {
					break;
				}
			}
		}
		return line.length() != 0 ? line : null;
	}
}