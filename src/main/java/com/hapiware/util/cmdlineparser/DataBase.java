package com.hapiware.util.cmdlineparser;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * A base class for data objects for delivering command line information from the parser.
 * 
 * @author <a href="http://www.hapiware.com" target="_blank">hapi</a>
 *
 */
public abstract class DataBase
{
	private final String _name;
	private final String _id;
	private final Set<String> _alternatives;
	
	/**
	 * Initialises the {@code DataBase} object. Can only be called by subclass.
	 *  
	 * @param name
	 * 		A name of the data element.
	 * 
	 * @param id
	 * 		An id for the data element. The {@code id} is used for matching annotated fields.
	 * 
	 * @param alternatives
	 * 		Alternative names of the data element.
	 */
	protected DataBase(String name, String id, Set<String> alternatives)
	{
		if(name == null || name.trim().length() == 0)
			throw new NullPointerException("'name' must have a value.");
		if(id == null || id.trim().length() == 0)
			throw new NullPointerException("'id' must have a value.");
		if(alternatives == null)
			throw new NullPointerException("'alternatives' must have a value.");
		
		_name = name;
		_id = id;
		
		Set<String> safeSet = new HashSet<String>();
		for(String alternative : alternatives)
			safeSet.add(alternative);
		_alternatives = Collections.unmodifiableSet(safeSet);
	}

	
	/**
	 * Returns the id of the given data element.  The {@code id} is used for matching annotated
	 * fields.
	 * 
	 * @return
	 * 		Data element id.
	 */
	public String getId()
	{
		return _id;
	}

	
	/**
	 * Returns the name of the given data element.
	 * 
	 * @return
	 * 		Data element name.
	 */
	public String getName()
	{
		return _name;
	}

	
	/**
	 * Returns alternative names for the data element.
	 * 
	 * @return
	 * 		Set of alternative names.
	 */
	protected Set<String> getAlternatives()
	{
		return _alternatives;
	}
}
