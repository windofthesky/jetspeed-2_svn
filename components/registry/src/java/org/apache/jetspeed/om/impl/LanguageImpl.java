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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.common.Language;

/**
 * 
 * LanguageImpl <br>
 * Okay, base Language really has nothing to really do at all with language per
 * se. It actually represents the locallized <code>title</code> and
 * <code>short-title</code> attributes of a portlet's definition. It also
 * contains a resource bundle for the specifc locale. <br>
 * TODO: org.apache.pluto.om.common.Language should be seperated into TODO a
 * Language class that just contains the resource bundle and TODO a Title class
 * that contains a localized title and short title.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class LanguageImpl implements MutableLanguage, Serializable
{

    public static final String JAVAX_PORTLET_KEYWORDS = "javax.portlet.keywords";
    public static final String JAVAX_PORTLET_SHORT_TITLE = "javax.portlet.short-title";
    public static final String JAVAX_PORTLET_TITLE = "javax.portlet.title";
    private Locale locale; // new Locale("en");
    private String title;
    private String shortTitle;
    private Collection keywords;
    private ResourceBundle resourceBundle;

    /**
     * This field can be used by persistence tools for storing PK info Otherwise
     * it has no effect on the functioning of the portal.
     */
    protected long id;

    protected long portletId;

    public LanguageImpl()
    {
        this(Locale.getDefault(), null, "", "", "");
    }

    public LanguageImpl( Locale locale, String title )
    {
        this(locale, null, title, "", "");
    }

    public LanguageImpl( Locale locale, ResourceBundle bundle, String defaultTitle, String defaultShortTitle,
            String defaultKeyWords )
    {

        HashMap defaults = new HashMap(3);
        defaults.put(JAVAX_PORTLET_TITLE, defaultTitle);
        defaults.put(JAVAX_PORTLET_SHORT_TITLE, defaultShortTitle);
        defaults.put(JAVAX_PORTLET_KEYWORDS, defaultKeyWords);
        this.resourceBundle = new DefaultsResourceBundle(bundle, defaults);

        this.locale = locale;
        setTitle(this.resourceBundle.getString(JAVAX_PORTLET_TITLE));
        setShortTitle(this.resourceBundle.getString(JAVAX_PORTLET_SHORT_TITLE));
        setKeywords(this.resourceBundle.getString(JAVAX_PORTLET_KEYWORDS));
    }

    public LanguageImpl( Locale locale, ResourceBundle bundle )
    {
        this.resourceBundle = new DefaultsResourceBundle(bundle, new HashMap());
        this.locale = locale;
    }

    /**
     * @see org.apache.pluto.om.common.Language#getLocale()
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * @see org.apache.pluto.om.common.Language#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @see org.apache.pluto.om.common.Language#getShortTitle()
     */
    public String getShortTitle()
    {
        return shortTitle;
    }

    /**
     * @see org.apache.pluto.om.common.Language#getKeywords()
     */
    public Iterator getKeywords()
    {
        if (keywords != null)
        {
            return keywords.iterator();
        }

        return null;

    }

    /**
     * @see org.apache.pluto.om.common.Language#getResourceBundle()
     */
    public ResourceBundle getResourceBundle()
    {

        return resourceBundle;
    }

    /**
     * @see org.apache.pluto.om.common.LanguageCtrl#setLocale(java.util.Locale)
     */
    public void setLocale( Locale locale )
    {
        this.locale = locale;

    }

    /**
     * @see org.apache.pluto.om.common.LanguageCtrl#setTitle(java.lang.String)
     */
    public void setTitle( String title )
    {
        this.title = title;
    }

    /**
     * @see org.apache.pluto.om.common.LanguageCtrl#setShortTitle(java.lang.String)
     */
    public void setShortTitle( String title )
    {
        this.shortTitle = title;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if (obj != null && obj instanceof Language)
        {
            return obj.hashCode() == this.hashCode();
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(19, 79);
        hasher.append(locale.getCountry()).append(locale.getLanguage()).append(locale.getVariant());
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.jetspeed.om.common.LanguageComposite#setKeywords(java.util.Collection)
     */
    public void setKeywords( Collection keywords )
    {
        this.keywords = keywords;
    }

    /**
     * 
     * <p>
     * setKeywords
     * </p>
     * 
     * A comma delimited list of keywords
     * 
     * @param keywords
     *  
     */
    public void setKeywords( String keywordStr )
    {
        if (keywords == null)
        {
            keywords = new ArrayList();
        }
        StringTokenizer tok = new StringTokenizer(keywordStr, ",");
        while (tok.hasMoreTokens())
        {
            keywords.add(tok.nextToken());
        }
    }

    private static class DefaultsResourceBundle extends ResourceBundle
    {
        private ResourceBundle baseBundle;
        private Map defaultValues;

        public DefaultsResourceBundle( ResourceBundle baseBundle, Map defaultValues )
        {
            this.baseBundle = baseBundle;
            this.defaultValues = defaultValues;
        }

        /**
         * <p>
         * getKeys
         * </p>
         * 
         * @see java.util.ResourceBundle#getKeys()
         * @return
         */
        public Enumeration getKeys()
        {
            if (baseBundle != null)
            {
                Enumeration baseKeys = baseBundle.getKeys();
                HashSet mergedKeys = new HashSet(defaultValues.keySet());
                while (baseKeys.hasMoreElements())
                {
                    mergedKeys.add(baseKeys.nextElement());
                }
                return Collections.enumeration(mergedKeys);
            }
            else
            {
                return Collections.enumeration(defaultValues.keySet());
            }
        }

        /**
         * <p>
         * handleGetObject
         * </p>
         * 
         * @see java.util.ResourceBundle#handleGetObject(java.lang.String)
         * @param key
         * @return
         */
        protected Object handleGetObject( String key )
        {

            try
            {
                if(baseBundle != null)
                {
                    return baseBundle.getObject(key);
                }
                else
                {
                    return getDefaultValue(key);
                }
            }
            catch (MissingResourceException e)
            {
                return getDefaultValue(key);
            }
        }

        /**
         * <p>
         * getDefaultValue
         * </p>
         *
         * @param key
         * @return
         */
        protected Object getDefaultValue( String key )
        {
            Object value = defaultValues.get(key);
            if (value != null)
            {
                return value;
            }
            else
            {
                return "";
            }
        }
    }
    
    

}
