/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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

    protected static final String RESOURCE_BUNDLE_NAME = "portalResources";

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
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, getLocale());
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
