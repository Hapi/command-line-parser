package com.hapiware.util.cmdlineparser.writer;

import com.hapiware.util.cmdlineparser.constraint.Enumeration;


/**
 * {@code Writer} is an interface to format the output of the help texts and error messages. It
 * can be used for outputting the result of the command line utility. Then the requirement is
 * to start a printing sequence with {@link #header()} and to end it with {@link #footer()}.
 * The most proper method for output is {@link #line(Level, String)} using level
 * {@link Writer.Level#NONE}.
 * 
 * See also
 * <a href="../CommandLineParser.html#cmdlineparser-writer">CommandLineParser, chapter Writer</a>
 *  
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public interface Writer
{
	/**
	 * Indent levels (or headings) for {@link Writer} elements.
	 */
	public enum Level {
		/**
		 * Indicates that there is no indent level. In practice this can be interpreted as having
		 * an indent level of zero (0).
		 */
		NONE,
		L1,
		L2,
		L3,
		L4,
		L5
	};
	
	/**
	 * Creates a header for the output. For example XML and HTML writers use this to create
	 * their headers.
	 */
	public void header();
	
	/**
	 * Beginning of the level 1.
	 * 
	 * @param text
	 * 		The heading text for level 1.
	 */
	public void level1Begin(String text);
	
	/**
	 * Ending of the level 1.
	 */
	public void level1End();
	
	/**
	 * Beginning of the level 2.
	 * 
	 * @param text
	 * 		The heading text for level 2.
	 */
	public void level2Begin(String text);

	/**
	 * Ending of the level 2.
	 */
	
	public void level2End();
	/**
	 * Beginning of the level 3.
	 * 
	 * @param text
	 * 		The heading text for level 3.
	 */
	public void level3Begin(String text);
	
	/**
	 * Ending of the level 3.
	 */
	public void level3End();
	
	/**
	 * Beginning of the level 4.
	 * 
	 * @param text
	 * 		The heading text for level 4.
	 */
	public void level4Begin(String text);
	
	/**
	 * Ending of the level 4.
	 */
	public void level4End();
	
	/**
	 * Beginning of the level 5.
	 * 
	 * @param text
	 * 		The heading text for level 5.
	 */
	public void level5Begin(String text);
	
	/**
	 * Ending of the level 5.
	 */
	public void level5End();
	
	/**
	 * This method is designed to write long text paragraphs. The difference between this method
	 * and {@link #line(Level, String)} is that {@link #line(Level, String)} is designed to print
	 * a single line whereas {@link #paragraph(Level, String)} is designed for multiline printing.
	 * This distinction is mainly meaningful for terminal (on-screen) output. For HTML writer
	 * the implementation for {@link #paragraph(Level, String)} and {@link #line(Level, String)}
	 * would be exactly the same.
	 * <p>
	 * This method is called by the help system for outputting utility descriptions as well as
	 * option and argument descriptions.
	 * 
	 * @param headingLevel
	 * 		The heading level for the paragraph.
	 * 
	 * @param text
	 * 		Text to be printed.
	 * 
	 * @see #line(Level, String)
	 */
	public void paragraph(Level headingLevel, String text);
	
	/**
	 * {@link #line(Level, String)} is used to create error messages and notifications. This method
	 * is also a proper choice to call if the normal output is wanted to write via {@link Writer}.
	 * This method is used to
	 * 
	 * @param headingLevel
	 * 		The heading level for the line to be printed. Help system uses {@link Level#L1} and
	 * 		for the normal output {@link Level#NONE} would be the proper choice.
	 * 
	 * @param text
	 * 		Text to be printed.
	 * 
	 * @see #paragraph(Level, String)
	 */
	public void line(Level headingLevel, String text);
	
	/**
	 * Creates the beginning of the (unordered) list block. List block is used to list constraints
	 * and enumerated constraint ({@link Enumeration}) values.
	 * 
	 * @param headingLevel
	 * 		The heading level.
	 * 
	 * @see #listItem(String)
	 * @see #listEnd()
	 */
	public void listBegin(Level headingLevel);
	
	/**
	 * Creates listed item within a list block.
	 *  
	 * @param text
	 * 		A listed item.
	 */
	public void listItem(String text);
	
	/**
	 * Creates the ending of the (unordered) list block. 
	 * 
	 * @see #listBegin(Level)
	 * @see #listItem(String)
	 */
	public void listEnd();
	
	/**
	 * Creates the beginning of the code block. Code block is used to create output for usage
	 * and examples. 
	 * 
	 * @param headingLevel
	 * 		The heading level.
	 * 
	 * @see #codeLine(String)
	 * @see #codeEnd()
	 */
	public void codeBegin(Level headingLevel);
	
	/**
	 * Creates a code line within a code block.
	 * 
	 * @param code
	 * 		A line of code to be printed.
	 * 
	 * @see #codeBegin(Level)
	 * @see #codeEnd()
	 */
	public void codeLine(String code);
	
	/**
	 * Creates the ending of the code block.
	 *
	 * @see #codeBegin(Level)
	 * @see #codeLine(String)
	 */
	public void codeEnd();
	
	/**
	 * Returns the beginning markup element or tag for the text decorated as strong (i.e. bold).
	 * For example, for HTML writer this would return {@code <b>} (or {@code <strong>}).
	 * 
	 * @return
	 * 		A markup element for indicating the beginning of the bold text.
	 */
	public String strongBegin();
	
	/**
	 * Returns the ending markup element or tag for the text decorated as strong (i.e. bold).
	 * For example, for HTML writer this would return {@code </b>} (or {@code </strong>}).
	 * 
	 * @return
	 * 		A markup element for indicating the ending of the bold text.
	 */
	public String strongEnd();
	
	/**
	 * Creates a footer for the output.  For example XML and HTML writers use this to create
	 * their footers.
	 */
	public void footer();
}
