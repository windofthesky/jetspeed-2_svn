/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.simpleregistry.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.deployment.simpleregistry.Entry;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistry;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistryException;

/**
 * <p>
 * InMemoryRegistryImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: InMemoryRegistryImpl.java 185531 2004-01-14 19:32:57Z weaver $
 *
 */
public class InMemoryRegistryImpl implements SimpleRegistry
{
	protected Map registry;
	
	public InMemoryRegistryImpl()
	{
		super();
		registry = new HashMap();
	}

    /**
     * @see org.apache.jetspeed.cps.simpleregistry.SimpleRegistry#register(org.apache.jetspeed.cps.simpleregistry.Entry)
     */
    public void register(Entry entry) throws SimpleRegistryException
    {
    	checkArguments(entry);	
        if(!isRegistered(entry))
        {
        	registry.put(entry.getId(), entry);
        }
        else
        {
        	throw new SimpleRegistryException(entry.getId()+" is already registered.");
        }

    }

    /**
     * @see org.apache.jetspeed.cps.simpleregistry.SimpleRegistry#deRegister(org.apache.jetspeed.cps.simpleregistry.Entry)
     */
    public void deRegister(Entry entry)
    {
        checkArguments(entry);
        registry.remove(entry.getId());

    }

    /**
     * @see org.apache.jetspeed.cps.simpleregistry.SimpleRegistry#isRegistered(org.apache.jetspeed.cps.simpleregistry.Entry)
     */
    public boolean isRegistered(Entry entry)
    {        
    	checkArguments(entry);
        return registry.containsKey(entry.getId());
    }

    /**
     * @see org.apache.jetspeed.cps.simpleregistry.SimpleRegistry#getRegistry()
     */
    public Collection getRegistry()
    {
        return registry.values();
    }
    
    protected void checkArguments(Entry entry)
    {
    	if(entry == null )
    	{
    		throw new IllegalArgumentException("Entry cannot be null.");
    	}
    	
		if(entry.getId() == null )
		{
			throw new IllegalArgumentException("Entry.getId() cannot be null.");
		}
    }

}
