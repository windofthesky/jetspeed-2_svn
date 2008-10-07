/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.jetspeed.om.common.MutableDisplayName;
import org.apache.jetspeed.om.common.MutableDisplayNameSet;
import org.apache.pluto.om.common.DisplayName;
/**
 * DisplayNameSetImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DisplayNameSetImpl  implements MutableDisplayNameSet
{

    /** Specifies the type Description we are storing */
    protected String displayNameType;
    
	protected Collection innerCollection;




    public DisplayNameSetImpl()
    {
        super();        
        this.innerCollection = new ArrayList();
    }
    
	public DisplayNameSetImpl(Collection collection)
	{
		super();		
		this.innerCollection = collection;
	}

    /**
     * @see org.apache.pluto.om.common.DisplayNameSet#get(java.util.Locale)
     */
    public DisplayName get(Locale arg0)
    {

        DisplayName fallBack = null;
        Iterator searchItr = innerCollection.iterator();
        while(searchItr.hasNext())
        {
        	DisplayName aDName = (DisplayName) searchItr.next();
        	if(aDName.getLocale().equals(arg0))
        	{
        		return aDName;
        	}
        	else if(aDName.getLocale().getLanguage().equals(arg0.getLanguage()))
        	{
        		fallBack = aDName;
        	}
        	
        }        

        return fallBack;
    }

    public void addDisplayName(DisplayName name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("DisplayName argument cannot be null");
        }

        add(name);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        MutableDisplayName name = (MutableDisplayName) o;

        return innerCollection.add(o);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        DisplayName name = (DisplayName) o;
        
        return innerCollection.remove(o);
    }

    /**
     * @see org.apache.pluto.om.common.DisplayNameSet#iterator()
     */
    public Iterator iterator()
    {        
        return this.innerCollection.iterator();
    }

    /**
     * @return
     */
    public Collection getInnerCollection()
    {
        return innerCollection;
    }

    /**
     * @param collection
     */
    public void setInnerCollection(Collection collection)
    {
        innerCollection = collection;
    }

}
