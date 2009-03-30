/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.util.HashCodeBuilder;

/**
 * 
 * LanguageImpl <br>
 * Okay, base Language really has nothing to really do at all with language per
 * se. It actually represents the locallized <code>title</code> and
 * <code>short-title</code> attributes of a portlet's definition.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *  
 */
public class LanguageImpl implements Language, Serializable
{
    private static final long serialVersionUID = 3817645806723304558L;
    
    private String title;
    private String shortTitle;
    private Locale locale;
    private boolean supportedLocale;
    private String keywords;
    private transient List<String> keywordList;

    public LanguageImpl()
    {
    }

    public Locale getLocale()
    {
        return locale;
    }
    
    public boolean isSupportedLocale()
    {
        return supportedLocale;
    }
    
    public void setSupportedLocale(boolean supportedLocale)
    {
        this.supportedLocale = supportedLocale;
    }

    public String getTitle()
    {
        return title;
    }

    public String getShortTitle()
    {
        return shortTitle;
    }

    public List<String> getKeywordList()
    {
        if ( keywordList == null )
        {
            if (keywords != null)
            {
                keywordList = new ArrayList<String>();
                StringTokenizer tok = new StringTokenizer(keywords, ", \t");
                while (tok.hasMoreTokens())
                {
                    keywordList.add(tok.nextToken());
                }
                keywordList = Collections.unmodifiableList(keywordList);
                
            }
            else
            {
                keywordList = Collections.emptyList();
            }
        }
        return keywordList;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public void setShortTitle( String shortTitle )
    {
        this.shortTitle = shortTitle;
    }

    public void setKeywords( String keywords )
    {
        this.keywords = keywords;
        this.keywordList = null;
    }
    
    public String getKeywords()
    {
        return keywords;
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
