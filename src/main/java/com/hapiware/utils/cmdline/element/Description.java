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
		if(_description.size() > 0) {
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
	
	public Description strong(String descriptionElement)
	{
		if(_description.size() > 0) {
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
