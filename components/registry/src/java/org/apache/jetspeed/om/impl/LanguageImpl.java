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
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.common.Language;

/**
 * 
 * LanguageImpl
 * <br>
 * Okay, base Language really has nothing to really do at all with language 
 * per se.  It actually represents the locallized <code>title</code> and
 * <code>short-title</code> attributes of a portlet's definition.  It
 * also contains a resource bundle for the specifc locale.
 * <br>
 * TODO: org.apache.pluto.om.common.Language should be seperated  into 
 * TODO a Language class that just contains the resource bundle and
 * TODO a Title class that contains a localized title and short title.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class LanguageImpl implements MutableLanguage, Serializable
{

    private Locale locale = new Locale("en");
    private String title;
    private String shortTitle;
    private Collection keywords;

    /**
     * This field can be used by persistence tools for storing PK info
     * Otherwise it has no effect on the functioning of the portal.
     */
    protected long id;

    protected long portletId;

    protected static final String RESOURCE_BUNDLE_NAME = "portlet";

    public LanguageImpl()
    {
    }

    public LanguageImpl(Locale locale, String title)
    {
        this.locale = locale;
        this.title = title;
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
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, getLocale(), Thread.currentThread().getContextClassLoader());
    }

    /**
     * @see org.apache.pluto.om.common.LanguageCtrl#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;

    }

    /**
     * @see org.apache.pluto.om.common.LanguageCtrl#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @see org.apache.pluto.om.common.LanguageCtrl#setShortTitle(java.lang.String)
     */
    public void setShortTitle(String title)
    {
        this.shortTitle = title;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
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
    public void setKeywords(Collection keywords)
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
    public void setKeywords(String keywordStr)
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

}
