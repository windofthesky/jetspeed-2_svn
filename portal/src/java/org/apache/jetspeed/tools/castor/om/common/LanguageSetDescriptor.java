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
package org.apache.jetspeed.tools.castor.om.common;

import java.util.Vector;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import org.apache.pluto.om.common.Language;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.impl.LanguageSetImpl;

/**
 * Used to help Castor in mapping XML preferences to Java objects 
 * Merges portlet-info and other language declarations into a
 * a single LanguageSet of one or more Language objects
 * 
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class LanguageSetDescriptor extends LanguageSetImpl
{
    private String shortTitle;
    private String title;
    private String castorKeywords = null;

    /**
     * contains Locale objects for locales supported by the portlet
     */
    private Vector locales = new Vector();
    private boolean resourceBundleInitialized = false;
    private String resources = null;

    public LanguageSetDescriptor()
    {
    }

    public void setShortTitle(String shortTitle)
    {
        this.shortTitle = shortTitle;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getShortTitle()
    {
        return this.shortTitle;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getCastorKeywords()
    {
        return this.castorKeywords;
    }

    public void setCastorKeywords(String keywords)
    {
        this.castorKeywords = keywords;
    }

    // create Language object with data from this class (title, short-title, description, keywords)
    private Language createLanguage(Locale locale, String title, String shortTitle, String keywordString)
    {
        if (locale != null && title != null)
        {

            MutableLanguage lang = new LanguageImpl(locale, title);

            lang.setLocale(locale);
            lang.setTitle(title);
            lang.setShortTitle(shortTitle);

            ArrayList keywords = new ArrayList();
            StringTokenizer tokenizer = new StringTokenizer(keywordString, ",");
            while (tokenizer.hasMoreTokens())
            {
                keywords.add(tokenizer.nextToken());
            }
            ((LanguageImpl) lang).setKeywords(keywords);

            return (Language) lang;
        }

        return null;
    }

    // creates a locale object from a string representation
    private Locale createLocale(String locale)
    {

        // parse locale String
        StringTokenizer tokenizer = new StringTokenizer(locale, "_");
        String[] localeDef = new String[5]; // just in case we have more than one variant
        for (int i = 0; i < localeDef.length; i++)
        {
            if (tokenizer.hasMoreTokens())
            {
                localeDef[i] = tokenizer.nextToken();
            }
            else
            {
                localeDef[i] = "";
            }
        }

        return new java.util.Locale(localeDef[0], localeDef[1], localeDef[2] + localeDef[3] + localeDef[4]);
    }

    public Locale getDefaultLocale()
    {
        Locale defLoc = null;

        if (locales != null && locales.size() > 0)
        {
            defLoc = (Locale) locales.firstElement();

            if (defLoc == null)
            {

                defLoc = new Locale("en", "");
                locales.add(defLoc);
            }
        }
        else
        {

            defLoc = new Locale("en", "");
            locales.add(defLoc);
        }

        return defLoc;
    }

    private void initResourceBundle()
    {
        Iterator iter = locales.iterator();
        while (iter.hasNext())
        {
            Locale locale = (Locale) iter.next();
            ResourceBundle bundle = null;
            bundle = loadResourceBundle(locale);
            if (bundle != null)
            {
                String title;
                String shortTitle;
                String keywords;
                int found = 0;
                try
                {
                    title = bundle.getString("javax.portlet.title");
                    found++;
                }
                catch (MissingResourceException x)
                {
                    title = "";
                }
                try
                {
                    shortTitle = bundle.getString("javax.portlet.short-title");
                    found++;
                }
                catch (MissingResourceException x)
                {
                    shortTitle = "";
                }
                try
                {
                    keywords = bundle.getString("javax.portlet.keywords");
                    found++;
                }
                catch (MissingResourceException x)
                {
                    keywords = "";
                }
                if (found > 0)
                {
                    Language language = this.createLanguage(locale, title, shortTitle, keywords);
                    if (this.add(language) == false)
                    {
                        boolean removed = this.remove(language);
                        this.add(language);
                    }
                }
            }
        }
    }

    // loads resource bundle files from WEB-INF/classes directory
    protected ResourceBundle loadResourceBundle(Locale locale)
    {
        ResourceBundle resourceBundle = null;
        try
        {
            /*
            if (getClassLoader() != null)
            {
                resourceBundle=ResourceBundle.getBundle(resources, locale, classLoader);
            } else
            */ {
                resourceBundle = ResourceBundle.getBundle(resources, locale, Thread.currentThread().getContextClassLoader());
            }
        }
        catch (MissingResourceException x)
        {
            return null;
        }
        return resourceBundle;
    }

    // try to match the given locale to a supported locale
    private Locale matchLocale(Locale locale)
    {

        String variant = locale.getVariant();
        if (variant != null && variant.length() > 0)
        {

            locale = new Locale(locale.getLanguage(), locale.getCountry());
        }

        if (!locales.contains(locale))
        {

            String country = locale.getCountry();
            if (country != null && country.length() > 0)
            {

                locale = new Locale(locale.getLanguage(), "");
            }
        }

        if (!locales.contains(locale))
            locale = getDefaultLocale();

        return locale;
    }

    public Language get(Locale locale)
    {

        if (resources != null && resourceBundleInitialized == false)
        {
            initResourceBundle();
            this.resourceBundleInitialized = true;
        }

        if (!locales.contains(locale))
            locale = matchLocale(locale);

        Iterator iterator = this.iterator();
        while (iterator.hasNext())
        {
            Language language = (Language) iterator.next();
            if (language.getLocale().equals(locale) || size() == 1)
            {
                return language;
            }
        }

        return null;
    }

    public void postLoad(Object parameter) throws Exception
    {
        initInlinedInfos();
    }

    private void initInlinedInfos() throws Exception
    {
        // if resource-bundle is given
        // must be initialized later when classloader is known by initResourceBundle()            
        if (locales.size() == 0)
        {
            locales.add(getDefaultLocale());
        }
        if (castorKeywords == null)
        {
            castorKeywords = "";
        }
        if (shortTitle == null)
        {
            shortTitle = "";
        }
        if (title == null)
        {
            title = "";
        }
        boolean added = add(createLanguage(getDefaultLocale(), title, shortTitle, castorKeywords));
    }

}
