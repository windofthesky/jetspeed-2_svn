/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.simpleregistry;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Entry
 * </p>
 * Simple data type representing some regitered resource.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class Entry
{
	private String id;
	private Map attributes;
	
	public Entry()
	{
		super();
		attributes = new HashMap();
	}

    /**
     * @return
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param string
     */
    public void setId(String string)
    {
        id = string;
    }
    
    public Object getAttribute(String key)
    {
    	return attributes.get(key);
    }
    
    public void setAttribute(String key, Object value)
    {
    	attributes.put(key, value);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {        
        if(obj != null && obj instanceof Entry)
        {
        	Entry entry = (Entry) obj;
        	return entry.getId() != null && getId() != null && getId().equals(entry.getId());
        }
        
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {        
        return toString().hashCode();
    }

    public String toString()
    {
        return getClass().toString().toString()+":"+getId();
    }

   

}
