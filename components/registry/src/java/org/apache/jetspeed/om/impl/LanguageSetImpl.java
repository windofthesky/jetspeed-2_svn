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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;


import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;

/**
 * 
 * LanguageSetImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class LanguageSetImpl implements LanguageSet, Serializable
{

    

    protected Collection innerCollection;

    /**
     * 
     * @param wrappedSet
     */
    public LanguageSetImpl(Collection collection)
    {
        super();
        this.innerCollection = collection;
    }

    public LanguageSetImpl()
    {
        super();
        this.innerCollection = new ArrayList();
    }

    /**
     * @see org.apache.pluto.om.common.LanguageSet#iterator()
     */
    public Iterator iterator()
    {
        return innerCollection.iterator();
    }

    /**
     * @see org.apache.pluto.om.common.LanguageSet#getLocales()
     */
    public Iterator getLocales()
    {
        HashSet localSet = new HashSet();
        Iterator itr = innerCollection.iterator();
        while (itr.hasNext())
        {
            Language lang = (Language) itr.next();
            localSet.add(lang.getLocale());
        }

        return localSet.iterator();
    }

    /**
     * @see org.apache.pluto.om.common.LanguageSet#get(java.util.Locale)
     */
    public Language get(Locale locale)
    {
		Language fallBack = null;
        Iterator searchItr = innerCollection.iterator();
        while (searchItr.hasNext())
        {
			Language lang = (Language) searchItr.next();
            if (lang.getLocale().equals(locale))
            {
                return lang;
            }
            else if (lang.getLocale().getLanguage().equals(locale.getLanguage()))
            {
                fallBack = lang;
            }

        }

        return fallBack;
    }

    /**
     * @see org.apache.pluto.om.common.LanguageSet#getDefaultLocale()
     */
    public Locale getDefaultLocale()
    {
        return Locale.getDefault();
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        Language language = (Language) o;
        if (language.getLocale() == null)
        {
            ((MutableLanguage) o).setLocale(JetspeedLocale.getDefaultLocale());
        }
        
        return innerCollection.add(o);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        Language language = (Language) o;        
        return innerCollection.remove(language);
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
    
    public int size()
    {
    	return innerCollection.size();
    }

}
