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
import java.util.MissingResourceException;
import java.util.ResourceBundle;


import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.Support;
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
public class LanguageSetImpl implements LanguageSet, Serializable, Support
{

    private ClassLoader classLoader = null;
    
    private String resources;
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
                if (resources != null)
                {
                    return createLanguage(
                        lang.getLocale(),
                        loadResourceBundle(lang.getLocale()),
                        lang.getTitle(),
                        lang.getShortTitle(),
                        StringUtils.join(lang.getKeywords(), ","));
                }
                else
                {
                return lang;
            }
            }
            else if (lang.getLocale().getLanguage().equals(locale.getLanguage()))
            {
                fallBack = lang;
            }

        }
        if (fallBack == null)
        {
            ResourceBundle bundle = null;
            if (resources != null)
            {
                bundle = loadResourceBundle(locale);
            }
            fallBack = createLanguage(locale, bundle);
        }
        else
        {
            if (resources != null)
            {
                fallBack =
                    createLanguage(
                        fallBack.getLocale(),
                        loadResourceBundle(fallBack.getLocale()),
                        fallBack.getTitle(),
                        fallBack.getShortTitle(),
                        StringUtils.join(fallBack.getKeywords(), ","));
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

    /**
     * @param string
     */
    public void setResources(String string)
    {
        resources = string;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.Support#postLoad(java.lang.Object)
     */
    public void postLoad(Object parameter) throws Exception
    {
        Iterator iter = ((Collection) parameter).iterator();
        while (iter.hasNext())
        {
            Locale locale = (Locale) iter.next();
            Language language = createLanguage(locale, null);
            remove(language);
            add(language);
        }
    }

    protected ResourceBundle loadResourceBundle(Locale locale)
    {
        ResourceBundle resourceBundle = null;
        try
        {
            if (classLoader != null)
            {
                resourceBundle=ResourceBundle.getBundle(resources, locale, classLoader);
            }
            else
            {
                resourceBundle=ResourceBundle.getBundle(resources, locale, Thread.currentThread().getContextClassLoader());
            }
        }
        catch (MissingResourceException x)
        {
            return null;
        }
        return resourceBundle;
    }

    /**
     * 
     * Sets Portlet Class Loader
     * 
     * @param loader
     */
    public void setClassLoader(ClassLoader loader)
    {
        classLoader = loader;
    }

    /**
     * Creates Language instance from a default Language
     * 
     * @param locale
     * @param bundle
     * @return
     */
    private Language createLanguage(Locale locale, ResourceBundle bundle)
    {
        String title = "";
        String shortTitle = "";
        String keywords = "";
        Language defaultLang = get(getDefaultLocale());

        if (defaultLang != null)
        {
            title = defaultLang.getTitle();
            shortTitle = defaultLang.getShortTitle();
            keywords = StringUtils.join(defaultLang.getKeywords(), ",");
        }
        return createLanguage(locale, bundle, title, shortTitle, keywords);
    }
    
    /**
     * Creates Language instance.
     * 
     * @param locale
     * @param bundle
     * @param title
     * @param shortTitle
     * @param keywords
     * @return
     */
    private Language createLanguage(
        Locale locale,
        ResourceBundle bundle,
        String title,
        String shortTitle,
        String keywords)
    {
        LanguageImpl lang = new LanguageImpl(locale, bundle, title, shortTitle, keywords);
        return (Language) lang;
    }
}
