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
package org.apache.jetspeed.om.servlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDisplayName;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.om.impl.DescriptionImpl;
import org.apache.jetspeed.om.impl.DescriptionSetImpl;
import org.apache.jetspeed.om.impl.DisplayNameSetImpl;
import org.apache.jetspeed.om.impl.WebAppDescriptionImpl;
import org.apache.jetspeed.om.impl.WebAppDisplayNameImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.servlet.ServletDefinitionList;

/**
 * 
 * WebApplicationDefinitionImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class WebApplicationDefinitionImpl implements MutableWebApplication, Serializable
{

    private int id;
    private Collection displayNames = new ArrayList();
    private DisplayNameSetImpl DNCollWrapper = new DisplayNameSetImpl();

    private Collection descriptions = new ArrayList();
    private DescriptionSetImpl descCollWrapper = new DescriptionSetImpl(DescriptionImpl.TYPE_WEB_APP);

    private String contextRoot;
    private ParameterSet initParameters;

    private static final Log log = LogFactory.getLog(WebApplicationDefinitionImpl.class);

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinition#getId()
     */
    public ObjectID getId()
    {
        return new JetspeedObjectID(id);
    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinition#getDisplayName()
     */
    public DisplayName getDisplayName(Locale locale)
    {

        if (displayNames != null)
        {
            DNCollWrapper.setInnerCollection(displayNames);
            return DNCollWrapper.get(locale);
        }
        return null;

    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinition#getDescription()
     */
    public Description getDescription(Locale locale)
    {
        if (descriptions != null)
        {
            descCollWrapper.setInnerCollection(descriptions);
            return descCollWrapper.get(locale);
        }
        return null;

    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinition#getInitParameterSet()
     */
    public ParameterSet getInitParameterSet()
    {
        return initParameters;
    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinition#getServletDefinitionList()
     */
    public ServletDefinitionList getServletDefinitionList()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinition#getServletContext(javax.servlet.ServletContext)
     */
    public ServletContext getServletContext(ServletContext servletContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinition#getContextRoot()
     */
    public String getContextRoot()
    {
        return contextRoot;
    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinitionCtrl#setId(java.lang.String)
     */
    public void setId(String oid)
    {
        id = JetspeedObjectID.createFromString(oid).intValue();
    }

    /**
     * @see org.apache.pluto.om.servlet.WebApplicationDefinitionCtrl#setDisplayName(java.lang.String)
     */
    public void setDisplayNameSet(DisplayNameSet displayNames)
    {
        this.displayNames = ((DisplayNameSetImpl) displayNames).getInnerCollection();
    }

    /**
     * @see org.apache.jetspeed.om.common.servlet.WebApplicationComposite#setContextRoot(java.lang.String)
     */
    public void setContextRoot(String contextRoot)
    {
        this.contextRoot = contextRoot;
    }

    /**
     * @see org.apache.jetspeed.om.common.servlet.MutableWebApplication#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String description)
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList();
        }
        descCollWrapper.setInnerCollection(descriptions);
        try
        {
            MutableDescription descObj = new WebAppDescriptionImpl();
                
            descObj.setLocale(locale);
            descObj.setDescription(description);
            descCollWrapper.addDescription(descObj);
        }
        catch (Exception e)
        {
            String msg = "Unable to instantiate Description implementor, " + e.toString();
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }
    }

    /**
     * @see org.apache.jetspeed.om.common.servlet.MutableWebApplication#addDisplayName(java.util.Locale, java.lang.String)
     */
    public void addDisplayName(Locale locale, String name)
    {
        if (displayNames == null)
        {
            displayNames = new ArrayList();
        }
        DNCollWrapper.setInnerCollection(displayNames);
        try
        {
            MutableDisplayName dn = new WebAppDisplayNameImpl();
               
            dn.setLocale(locale);
            dn.setDisplayName(name);
            DNCollWrapper.addDisplayName(dn);
        }
        catch (Exception e)
        {
            String msg = "Unable to instantiate DisplayName implementor, " + e.toString();
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }

    }

    /**
     * @see org.apache.jetspeed.om.common.servlet.MutableWebApplication#setDescriptionSet(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptionSet(DescriptionSet descriptions)
    {
        this.descriptions = ((DescriptionSetImpl) descriptions).getInnerCollection();
    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @return
     */
    public String getDescription()
    {
        Description desc = getDescription(Locale.getDefault());
        if (desc != null)
        {
            return desc.getDescription();
        }
        return null;
    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @param desc
     */
    public void setDescription(String desc)
    {
        addDescription(Locale.getDefault(), desc);
    }

}
