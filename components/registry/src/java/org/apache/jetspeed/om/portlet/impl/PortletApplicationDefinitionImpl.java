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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
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
    private long id;

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
    
    /** Metadata property */
    private Collection metadataFields = null;
    
    /** Description */
    private String description;

    private Collection portlets;
    
    /** User attributes collection. */
    private Collection userAttributes;
    
    private PortletDefinitionListImpl listWrapper = new PortletDefinitionListImpl();

    private int applicationType = MutablePortletApplication.WEBAPP;
    
    /** Creates a new instance of BaseApplication */
    public PortletApplicationDefinitionImpl()
    {
        portlets = new ArrayList();
        userAttributes = new ArrayList();
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
        return new PortletDefinitionListImpl(portlets);
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
        id = JetspeedObjectID.createFromString(objectID).longValue();
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
       ((PortletDefinitionComposite) pd).setPortletApplicationDefinition(this);
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
    	listWrapper.setInnerCollection(portlets);
        return listWrapper.get(name);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#setPortletDefinitionList(org.apache.pluto.om.portlet.PortletDefinitionList)
     */
    public void setPortletDefinitionList(PortletDefinitionList portlets)
    {
        this.portlets = ((PortletDefinitionListImpl) portlets).getInnerCollection();
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#addUserAttribute(java.lang.String, java.lang.String)
     */
    public void addUserAttribute(String name, String description)
    {
        userAttributes.add(new UserAttributeImpl(name, description));
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#addUserAttribute(org.apache.jetspeed.om.common.UserAttribute)
     */
    public void addUserAttribute(UserAttribute userAttribute)
    {
        userAttributes.add(userAttribute);
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#setUserAttributes(java.util.Collection)
     */
    public void setUserAttributes(Collection userAttributes)
    {
        this.userAttributes = userAttributes;
    }

    /** 
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#getUserAttributes()
     */
    public Collection getUserAttributes()
    {
        return this.userAttributes;
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setApplicationType(int)
     */
    public void setApplicationType(int type)
    {
        this.applicationType = type;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getApplicationType()
     */
    public int getApplicationType()
    {
        return applicationType;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
    	if(metadataFields == null)
        {
            metadataFields = new ArrayList();
        }
    	
    	GenericMetadata metadata = new PortletApplicationMetadataImpl();
    	metadata.setFields(metadataFields);
        
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setMetadata(org.apache.jetspeed.om.common.GenericMetadata)
     */
    public void setMetadata(GenericMetadata metadata)
    {
        this.metadataFields = metadata.getFields();     
    }

    /**
     * @return
     */
    protected Collection getMetadataFields()
    {
        return metadataFields;
    }

    /**
     * @param collection
     */
    protected void setMetadataFields(Collection metadataFields)
    {
        this.metadataFields = metadataFields;
    }

}
