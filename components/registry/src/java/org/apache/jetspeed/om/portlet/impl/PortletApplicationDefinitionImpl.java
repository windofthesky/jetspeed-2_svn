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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.impl.GenericMetadataImpl;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
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
public class PortletApplicationDefinitionImpl implements MutablePortletApplication, Serializable, PersistenceBrokerAware
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
    
    /** DublinCore property */
    private GenericMetadata metadata = new GenericMetadataImpl();
    /** PK of this DublinCore */
    protected long metadataId;

    /** Description */
    private String description;

    private Collection portlets;
    
    private PortletDefinitionListImpl listWrapper = new PortletDefinitionListImpl();

    private int applicationType = MutablePortletApplication.WEBAPP;
    
    /** Creates a new instance of BaseApplication */
    public PortletApplicationDefinitionImpl()
    {
        portlets = new ArrayList();
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
    public GenericMetadata getMetadata() {
        return metadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.MutablePortletApplication#setMetadata(org.apache.jetspeed.om.common.GenericMetadata)
     */
    public void setMetadata(GenericMetadata metadata) {
        this.metadata = metadata;        
    }

    

    /** 
     * <p>
     * afterDelete
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterDelete(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * afterInsert
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterInsert(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * afterLookup
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterLookup(PersistenceBroker arg0) throws PersistenceBrokerException
    {
		if (webApplication == null)
		{
			// log.warn("Initial PortletDefintion materialization failed to retreive associated PortletApplicationDefinition.");
			// TODO: NASTY HACK ALERT!!!  OJB should be doing this automatically
			Criteria c = new Criteria();
			c.addEqualTo("id", new Long(webApplicationId));
			Query q = QueryFactory.newQuery(WebApplicationDefinitionImpl.class, c);
			webApplication = (WebApplicationDefinitionImpl) arg0.getObjectByQuery(q);

		}

    }

    /** 
     * <p>
     * afterUpdate
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterUpdate(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void afterUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * beforeDelete
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void beforeDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * beforeInsert
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void beforeInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        // TODO Auto-generated method stub

    }

    /** 
     * <p>
     * beforeUpdate
     * </p>
     * 
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     * @param arg0
     * @throws org.apache.ojb.broker.PersistenceBrokerException
     */
    public void beforeUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        // TODO Auto-generated method stub

    }

}
