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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.Collection;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 *
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 * @since 1.0
 */
public class PortletApplicationDefinitionImpl implements MutablePortletApplication, Serializable
{ 
    /**
     * Unique id of the application.  This serves as the primary key in database
     * and in any caching of this object.
     */
    // private ObjectID id;
    private int id;

    /** Holds value of property name. */
    private String name;

    /** Holds value of property version. */
    private String version;

    /** Holds the optional application identifier from the portlet.xml */
    private String applicationIdentifier;

    /** WebApplication property */
    private WebApplicationDefinition webApplication;
    /** PK of this Portlet Application's Web Application */
    protected long webApplicationId;

    /** Description */
    private String description;

    private PortletDefinitionListImpl portlets;

    /** Creates a new instance of BaseApplication */
    public PortletApplicationDefinitionImpl()
    {
        portlets = new PortletDefinitionListImpl();
    }

    /**
     * Getter for the applicationId
     *
     * @return applicationId
     * @see #applicationId
     */
    public ObjectID getId()
    {
        
        return new JetspeedObjectID(id);
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#setName(String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#getVersion()
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#setVersion(String)
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return
     */
    public WebApplicationDefinition getWebApplicationDefinition()
    {
        return webApplication;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getPortletDefinitionList()
     */
    public PortletDefinitionList getPortletDefinitionList()
    {
        return portlets;
    }

    /**
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param string
     */
    public void setDescription(String string)
    {
        description = string;
    }

    /**
     * @param objectID
     */
    public void setId(String objectID)
    {
        //((ObjectIDImpl) id).setValue(objectID);
        id = JetspeedObjectID.createFromString(objectID).intValue();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#setWebApplicationDefinition(org.apache.pluto.om.servlet.WebApplicationDefinition)
     */
    public void setWebApplicationDefinition(WebApplicationDefinition wad)
    {
        this.webApplication = wad;

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#addPortletDefinition(org.apache.pluto.om.portlet.PortletDefinition)
     */
    public void addPortletDefinition(PortletDefinition pd)
    {
        //((PortletDefinitionComposite) pd).setPortletApplicationDefinition(this);
        portlets.add(pd);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#getPortletDefinitions()
     */
    public Collection getPortletDefinitions()
    {
        return portlets;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#getPortletDefinitionByName(java.lang.String)
     */
    public PortletDefinition getPortletDefinitionByName(String name)
    {
        return portlets.get(name);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#setPortletDefinitionList(org.apache.pluto.om.portlet.PortletDefinitionList)
     */
    public void setPortletDefinitionList(PortletDefinitionList portlets)
    {
        this.portlets = (PortletDefinitionListImpl) portlets;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#setApplicationIdentifier(java.lang.String)
     */
    public void setApplicationIdentifier(String applicationIdentifier)
    {
        this.applicationIdentifier = applicationIdentifier;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#getApplicationIdentifier()
     */
    public String getApplicationIdentifier()
    {
        return this.applicationIdentifier;
    }

}
