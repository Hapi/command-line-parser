package com.hapiware.utils.cmdline.element;

import java.util.LinkedList;
import java.util.List;

import com.hapiware.utils.cmdline.writer.Writer;


/**
 * {@code Description} class is used to create descriptive texts for command line arguments
 * and options. {@link #description(String)}, {@link #strong(String)} and {@link #paragraph()}
 * methods are used to add different text elements to create the whole description. Each of
 * the methods returns {@code Description} object thus allowing a programmer to create a chain
 * of method calls. For example:
 * <pre>
 * 	new Description()
 * 		.description("This is the first paragraph of the description.")
 * 		.description("And this continues the first part.")
 *	 	.paragraph()
 * 		.description("Here is the second paragraph of the description")
 * 		.description("which continues here.");
 * </pre>
 * 
 * Calling {@link #toParagraphs()} after the previous example returns a list which contains two
 * strings each representing a separate paragraph. Returned strings are:
 * <ul>
 * 	<li>This is the first paragraph of the description. And this continues the first part.</li>
 *	<li>Here is the second paragraph of the description which continues here.</li>
 * </ul>
 * 
 * Notice the added space characters between text elements in each {@link #description(String)}
 * method call. All (white) spaces in the beginning or at the end of {@code descriptionElement}
 * of {@link #description(String)} call are trimmed. So the next example produces exactly the
 * same result than the example above:
 * <pre>
 * 	new Description()
 * 		.description("   This is the first paragraph of the description.  ")
 * 		.description(" And this continues the first part.    ")
 *	 	.paragraph()
 * 		.description(" Here is the second paragraph of the description   ")
 * 		.description("                               which continues here.");
 * </pre>
 * 
 * {@link #strong(String)} method call behaves a little bit differently. In general spaces
 * at the end of text element in {@link #description(String)} call right before {@link #strong(String)}
 * call are retained. The same is true for the space characters in the beginning of the
 * text element in {@link #description(String)} right after {@link #strong(String)} call.
 * For example:
 *  
 * <pre>
 * 	new Description()
 * 		.description("   This is the ")
 * 		.strong("first")
 * 		.description(" paragraph of the description.       ")
 * 		.description("    And this continues the first part.    ")
 *	 	.paragraph()
 * 		.description("           Here is the ")
 * 		.strong("second")
 * 		.description(" paragraph of the description   ")
 * 		.description("                               which continues here.");
 * </pre>
 * 
 * In the example above the space characters right before and after {@link #strong(String)} calls
 * are retained but all the other space characters are trimmed. 
 * 
 * Notice that empty paragraphs cannot be created (i.e. calling {@link #paragraph()} multiple
 * times one after another is useless).
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public class Description
{
	public final static String END_PARAGRAPH = "\n\n";
	private final static String STRONG_TAG_NAME = "com-hapiware-strong";
	public final static String STRONG_BEGIN_TAG = "<" + STRONG_TAG_NAME + ">";
	public final static String STRONG_END_TAG = "</" + STRONG_TAG_NAME + ">";
	
	private List<String> _description = new LinkedList<String>();
	
	
	/**
	 * Adds a new text element to a description. In general white space characters in
	 * the beginning and at the end are trimmed. However, using {@link #strong(String)}
	 * creates an exception to that. For further details see {@link Description}.
	 * <p>
	 * {@code description(String)} has a shorter alias {@link #d(String)}.
	 *  
	 * @param descriptionElement
	 * 		A new text element for description.
	 *  
	 * @return
	 * 		{@code Description} object thus allowing method call chaining.
	 * 
	 * @see Description
	 * @see #strong(String)
	 * @see #d(String)
	 * @see #toParagraphs()
	 */
	public Description description(String descriptionElement)
	{
		if(descriptionElement != null && _description.size() > 0) {
			String lastDescriptionElement = ((LinkedList<String>)_description).getLast();
			if(lastDescriptionElement.endsWith(STRONG_END_TAG))
				((LinkedList<String>)_description).set(
					_description.size() - 1,
					lastDescriptionElement + descriptionElement
				);
			else
				_description.add(descriptionElement);
		}
		else
			_description.add(descriptionElement);
		return this;
	}
	
	/**
	 * Adds a new text element to a description. The text element is surrounded with a strong
	 * tag to mark the text element for {@link Writer}. In general white space characters in
	 * the beginning and at the end are trimmed. However, using {@code #strong(String)}
	 * creates an exception to that. For further details see {@link Description}.
	 * <p>
	 * {@code strong(String)} has a shorter alias {@link #b(String)}.
	 *  
	 * @param descriptionElement
	 * 		A new text element for description.
	 *  
	 * @return
	 * 		{@code Description} object thus allowing method call chaining.
	 * 
	 * @see Description
	 * @see #description(String)
	 * @see #b(String)
	 * @see #toParagraphs()
	 */
	public Description strong(String descriptionElement)
	{
		if(descriptionElement != null && _description.size() > 0) {
			String lastDescriptionElement = ((LinkedList<String>)_description).getLast();
			if(lastDescriptionElement == END_PARAGRAPH)
				_description.add(STRONG_BEGIN_TAG + descriptionElement + STRONG_END_TAG);
			else {
				lastDescriptionElement += STRONG_BEGIN_TAG + descriptionElement + STRONG_END_TAG;
				((LinkedList<String>)_description).set(
					_description.size() - 1,
					lastDescriptionElement
				);
			}
		}
		else
			_description.add(STRONG_BEGIN_TAG + descriptionElement + STRONG_END_TAG);
		return this;
	}
	
	/**
	 * Ends the paragraph. For further details see {@link Description}.
	 * <p>
	 * {@code paragraph()} has a shorter alias {@link #p()}.
	 *  
	 * @return
	 * 		{@code Description} object thus allowing method call chaining.
	 * 
	 * @see Description
	 * @see #p(String)
	 * @see #toParagraphs()
	 */
	public Description paragraph()
	{
		_description.add(END_PARAGRAPH);
		return this;
	}

	/**
	 * Alias for {@link #description(String)}.
	 * 
	 * @see #description(String)
	 */
	public Description d(String descriptionElement)
	{
		return description(descriptionElement);
	}
	
	/**
	 * Alias for {@link #strong(String)}.
	 *
	 * @see #strong(String)
	 */
	public Description b(String descriptionElement)
	{
		return strong(descriptionElement);
	}
	
	
	/**
	 * Alias for {@link #paragraph()}.
	 * 
	 * @see #paragraph()
	 */
	public Description p()
	{
		return paragraph();
	}
	
	/**
	 * Returns a list of paragraphs. Each string represents one paragraph. See {@link Description}
	 * for more information about how paragraphs are formed.
	 * 
	 * @return
	 * 		List of paragraphs.
	 */
	public List<String> toParagraphs()
	{
		List<String> retVal = new LinkedList<String>();
		String descriptionParagraph = "";
		for(String descriptionElement : _description) {
			if(descriptionElement == END_PARAGRAPH) {
				descriptionParagraph = descriptionParagraph.trim();
				if(descriptionParagraph.length() > 0)
					retVal.add(descriptionParagraph);
				descriptionParagraph = "";
			}
			else
				descriptionParagraph += descriptionElement.trim() + " ";
		}
		descriptionParagraph = descriptionParagraph.trim();
		if(descriptionParagraph.length() > 0)
			retVal.add(descriptionParagraph);
		
		return retVal;
	}
}
