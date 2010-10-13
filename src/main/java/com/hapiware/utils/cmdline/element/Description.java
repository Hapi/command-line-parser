package com.hapiware.utils.cmdline.element;

import java.util.LinkedList;
import java.util.List;

public class Description
{
	public final static String END_PARAGRAPH = "\n\n";
	private final static String STRONG_TAG_NAME = "com-hapiware-strong";
	public final static String STRONG_BEGIN_TAG = "<" + STRONG_TAG_NAME + ">";
	public final static String STRONG_END_TAG = "</" + STRONG_TAG_NAME + ">";
	
	private List<String> _description = new LinkedList<String>();
	
	
	public Description description(String descriptionElement)
	{
		_description.add(descriptionElement);
		return this;
	}
	
	public Description strong(String descriptionElement)
	{
		_description.add(STRONG_BEGIN_TAG + descriptionElement + STRONG_END_TAG);
		return this;
	}
	
	public Description p()
	{
		_description.add(END_PARAGRAPH);
		return this;
	}
	
	public List<String> toParagraphs()
	{
		List<String> retVal = new LinkedList<String>();
		String descriptionParagraph = "";
		for(String descriptionElement : _description) {
			if(descriptionElement == END_PARAGRAPH) {
				retVal.add(descriptionParagraph);
				descriptionParagraph = "";
			}
			else
				descriptionParagraph += descriptionElement.trim() + " ";
		}
		if(descriptionParagraph.trim().length() > 0)
			retVal.add(descriptionParagraph);
		
		return retVal;
	}
}
