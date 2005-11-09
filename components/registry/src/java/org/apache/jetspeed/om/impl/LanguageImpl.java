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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedLocale;
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
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *  
 */
public class LanguageImpl extends ResourceBundle implements MutableLanguage, Serializable
{
    public static final String JAVAX_PORTLET_KEYWORDS = "javax.portlet.keywords";
    public static final String JAVAX_PORTLET_SHORT_TITLE = "javax.portlet.short-title";
    public static final String JAVAX_PORTLET_TITLE = "javax.portlet.title";

    private HashSet keys;
    private String title;
    private String shortTitle;
    private Locale locale;
    private String keywordStr;
    private Collection keywords;

    /**
     * This field can be used by persistence tools for storing PK info Otherwise
     * it has no effect on the functioning of the portal.
     */
    protected long id;

    protected long portletId;

    public LanguageImpl()
    {
        keys = new HashSet(3);
        keys.add(JAVAX_PORTLET_TITLE);
        keys.add(JAVAX_PORTLET_SHORT_TITLE);
        keys.add(JAVAX_PORTLET_KEYWORDS);
        this.locale = JetspeedLocale.getDefaultLocale();
    }
    
    public Enumeration getKeys()
    {
        return Collections.enumeration(keys);
    }
    
    protected Object handleGetObject(String key)
    {
        if (key.equals(JAVAX_PORTLET_TITLE))
        {
            return getTitle();
        }
        else if (key.equals(JAVAX_PORTLET_SHORT_TITLE))
        {
            return getShortTitle();
        }
        else if (key.equals(JAVAX_PORTLET_KEYWORDS))
        {
            return getKeywordStr();
        }
        return null;
    }
    
    private String getStringValue(ResourceBundle bundle, String key, String defaultValue)
    {
        String value = defaultValue;
        try
        {
            value = (String)bundle.getObject(key);
        }
        catch (MissingResourceException mre)
        {            
        }
        catch (ClassCastException cce)
        {            
        }
        return value;
    }
    
    public void setResourceBundle(ResourceBundle bundle)
    {
        if ( parent == null && bundle != null )
        {
            Enumeration parentKeys = bundle.getKeys();
            while ( parentKeys.hasMoreElements() )
            {
                keys.add(parentKeys.nextElement());
            }
            setParent(bundle);
        }
    }
    
    public void loadDefaults()
    {
        ResourceBundle bundle = getParentResourceBundle();
        if ( bundle != null )
        {
            setTitle(getStringValue(bundle, JAVAX_PORTLET_TITLE, getTitle()));
            setShortTitle(getStringValue(bundle, JAVAX_PORTLET_SHORT_TITLE, getShortTitle()));
            setKeywords(getStringValue(bundle, JAVAX_PORTLET_TITLE, getKeywordStr()));
        }
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
        if ( keywords == null )
        {
            return Collections.EMPTY_LIST.iterator();
        }
        return keywords.iterator();
    }

    /**
     * @see org.apache.pluto.om.common.Language#getResourceBundle()
     */
    public ResourceBundle getResourceBundle()
    {

        return this;
    }
    
    public ResourceBundle getParentResourceBundle()
    {
        return parent;
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
    public void setShortTitle( String shortTitle )
    {
        this.shortTitle = shortTitle;
    }

    /**
     * @see org.apache.jetspeed.om.common.LanguageComposite#setKeywords(java.util.Collection)
     */
    public void setKeywords( Collection keywords )
    {
        this.keywords = keywords;
    }
    
    public void setKeywords(String keywordStr)
    {
        if (keywords == null)
        {
            keywords = new ArrayList();
        }
        else
        {
            keywords.clear();
        }
        if ( keywordStr == null )
        {
            keywordStr = "";
        }
        StringTokenizer tok = new StringTokenizer(keywordStr, ",");
        while (tok.hasMoreTokens())
        {
            keywords.add(tok.nextToken());
        }
        this.keywordStr = keywordStr;
    }
    
    public String getKeywordStr()
    {
        if ( keywordStr == null )
        {
            keywordStr = StringUtils.join(getKeywords(),",");
        }
        return keywordStr;
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
        Locale locale = getLocale();
        hasher.append(locale.getCountry()).append(locale.getLanguage()).append(locale.getVariant());
        return hasher.toHashCode();
    }
}
