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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.jetspeed.om.common.MutableDescriptionSet;
import org.apache.pluto.om.common.Description;

/**
 * BaseDescriptionSet
 * 
 * Supports 
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DescriptionSetImpl  implements MutableDescriptionSet, Serializable
{
    /** Specifies the type Description we are storing */
    protected String descriptionType;
    protected Collection innerCollection;

    /**
     * 
     */
    public DescriptionSetImpl()
    {
        super();
		this.innerCollection = new ArrayList();        
    }

    /**
     * @param c
     */
    public DescriptionSetImpl(Collection c)
    {
        this.innerCollection = c;  
    }




    public DescriptionSetImpl(String descriptionType)
    {
        super();
        this.descriptionType = descriptionType;
    }

    /**
     * @see org.apache.pluto.om.common.DescriptionSet#get(java.util.Locale)
     */
    public Description get(Locale arg0)
    {
        if (arg0 == null)
        {
            throw new IllegalArgumentException("The Locale argument cannot be null");
        }

        // TODO: This may cause concurrent modification exceptions
        Iterator itr = iterator();
        Description fallBack = null;
        while (itr.hasNext())
        {
            Description desc = (Description) itr.next();
            if (desc.getLocale().equals(arg0))
            {
                return desc;
            }
            // set fall back if we have a Locale that only has
            // language set.
            if (desc.getLocale().getLanguage().equals(arg0.getLanguage()))
            {
                fallBack = desc;
            }
        }
        return fallBack;
    }

    /**
     * @see org.apache.jetspeed.om.common.MutableDescriptionSet#addDescription(java.lang.String)
     */
    public void addDescription(Description description)
    {        
        innerCollection.add(description);
    }

    /**
     * @see org.apache.pluto.om.common.DescriptionSet#iterator()
     */
    public Iterator iterator()
    {        
        return innerCollection.iterator();
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
