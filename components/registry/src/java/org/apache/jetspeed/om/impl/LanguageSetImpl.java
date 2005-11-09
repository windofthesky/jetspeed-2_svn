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

import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;

/**
 * 
 * LanguageSetImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
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
    public LanguageSetImpl( Collection collection )
    {
        super();
        this.innerCollection = collection;
    }

    public LanguageSetImpl()
    {
        this(new ArrayList());
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
    public Language get( Locale locale )
    {
        LanguageImpl fallback = null;
        Iterator searchItr = innerCollection.iterator();
        while (searchItr.hasNext())
        {
            LanguageImpl lang = (LanguageImpl)searchItr.next();

            if (lang.getLocale().equals(locale))
            {
                if (resources != null && lang.getParentResourceBundle() == null)
                {
                    lang.setResourceBundle(loadResourceBundle(lang.getLocale()));
                }
                return lang;
            }
            else if (lang.getLocale().getLanguage().equals(locale.getLanguage()))
            {
                fallback = lang;
            }

        }
        
        if ( fallback == null )
        {
            if ( getDefaultLocale().equals(locale) )
            {
                // no default language stored yet
                LanguageImpl defaultLanguage = new LanguageImpl();
                defaultLanguage.setLocale(locale);
                
                if ( resources != null )
                {
                    defaultLanguage.setResourceBundle(loadResourceBundle(locale));
                    defaultLanguage.loadDefaults();
                    innerCollection.add(defaultLanguage);
                    return defaultLanguage;
                }
            }
            else
            {
                fallback = (LanguageImpl)get(getDefaultLocale());
            }
        }
        
        LanguageImpl language = new LanguageImpl();
        language.setLocale(locale);
        language.setTitle(fallback.getTitle());
        language.setShortTitle(fallback.getShortTitle());
        language.setKeywords(fallback.getKeywordStr());
        if ( resources != null )
        {
          language.setResourceBundle(loadResourceBundle(locale));
        }
        language.loadDefaults();
        innerCollection.add(language);
        return language;
    }

    /**
     * @see org.apache.pluto.om.common.LanguageSet#getDefaultLocale()
     */
    public Locale getDefaultLocale()
    {        
        return JetspeedLocale.getDefaultLocale();
    }

    public boolean add( Object o )
    {
        if (o instanceof Language)
        {
            Language language = (Language) o;
            if (language.getLocale() == null)
            {
                ((MutableLanguage) o).setLocale(getDefaultLocale());
            }

            Iterator ite = innerCollection.iterator();
            while (ite.hasNext())
            {
                Language lang = (Language) ite.next();
                if (lang.equals(language))
                {
                    innerCollection.remove(lang);
                    return innerCollection.add(o);
                }
            }
            return innerCollection.add(o);
        }
        return false;
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
    public void setInnerCollection( Collection collection )
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
    public void setResources( String string )
    {
        resources = string;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.common.Support#postLoad(java.lang.Object)
     */
    public void postLoad( Object parameter ) throws Exception
    {
        Iterator iter = ((Collection) parameter).iterator();
        while (iter.hasNext())
        {
            LanguageImpl language = (LanguageImpl)get((Locale)iter.next());
            language.loadDefaults();
        }
        // ensure default locale language is created
        get(getDefaultLocale());
    }

    protected ResourceBundle loadResourceBundle( Locale locale )
    {
        ResourceBundle resourceBundle = null;
        try
        {
            if (resources != null)
            {
                if (classLoader != null)
                {
                    resourceBundle = ResourceBundle.getBundle(resources, locale, classLoader);
                }
                else
                {
                    resourceBundle = ResourceBundle.getBundle(resources, locale, Thread.currentThread()
                            .getContextClassLoader());
                }
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
    public void setClassLoader( ClassLoader loader )
    {
        classLoader = loader;
    }
}
