/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
 * @version $Id$
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
