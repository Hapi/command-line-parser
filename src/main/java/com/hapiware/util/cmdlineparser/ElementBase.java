package com.hapiware.util.cmdlineparser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


class ElementBase
{
	private String _name;
	private Set<String> _alternatives = new HashSet<String>();
	private String _id;
	private Description _description = new Description();
	

	public ElementBase()
	{
		// Does nothing.
	}
	
	public ElementBase(ElementBase elementBase)
	{
		// Description is not needed when copy constructor is used.
		
		_name = elementBase._name;
		_id = elementBase._id;
		_alternatives.addAll(elementBase._alternatives);
	}
	
	public void name(String name)
	{
		_name = name;
	}
	
	/**
	 * 
	 * @param alternatives
	 * 
	 * @return
	 * 		{@code null} if there were no duplicates. Otherwise the duplicate alternative
	 * 		name is returned.
	 */
	public String alternatives(String...alternatives)
	{
		for(String alternative : alternatives)
			if(!_alternatives.add(alternative))
				return alternative;
		
		return null;
	}
	
	public void id(String id)
	{
		_id = id;
	}
	
	/**
	 * For further details see {@link Description#description(String)}
	 */
	public void description(String description)
	{
		_description.description(description);
	}
	
	/**
	 * For further details see {@link Description#d(String)}
	 */
	public void d(String description)
	{
		description(description);
	}
	
	/**
	 * For further details see {@link Description#strong(String)}
	 */
	public void strong(String text)
	{
		_description.strong(text);
	}

	/**
	 * For further details see {@link Description#b(String)}
	 */
	public void b(String text)
	{
		strong(text);
	}
	
	/**
	 * For further details see {@link Description#paragraph()}
	 */
	public void paragraph()
	{
		_description.paragraph();
	}
	
	/**
	 * For further details see {@link Description#p()}
	 */
	public void p()
	{
		paragraph();
	}
	
	public String name()
	{
		return _name;
	}
	
	public boolean checkAlternative(String name)
	{
		return _alternatives.contains(name);
	}
	
	public Set<String> alternatives()
	{
		return _alternatives;
	}
	
	public String id()
	{
		return _id == null ? name() : _id;
	}
	
	public List<String> description()
	{
		return _description.toParagraphs();
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
			return true;

		if(!(obj instanceof ElementBase))
			return false;
		ElementBase elementBase = (ElementBase)obj;
		
		return _name.equals(elementBase._name);
	}

	@Override
	public int hashCode()
	{
		int resultHash = 17;
		resultHash = 31 * resultHash + (_name == null ? 0 : _name.hashCode());
		return resultHash;
	}
}
