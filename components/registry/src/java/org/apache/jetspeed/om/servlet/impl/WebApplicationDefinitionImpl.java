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

    private long id;
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
        return new JetspeedObjectID((int)id);
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
        id = JetspeedObjectID.createFromString(oid).longValue();
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
