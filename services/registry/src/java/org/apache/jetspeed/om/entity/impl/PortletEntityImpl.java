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
package org.apache.jetspeed.om.entity.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;
import org.apache.jetspeed.om.window.impl.PortletWindowListImpl;
import org.apache.jetspeed.entity.PortletEntityAccess;
import org.apache.jetspeed.entity.PortletEntityNotStoredException;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.util.StringUtils;

/**
 * Portlet Entity default implementation. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class PortletEntityImpl implements PortletEntity, PortletEntityCtrl, PersistenceBrokerAware
{

    private JetspeedObjectID oid;

    public ObjectID getId()
    {
        return oid;
    }

    public void setId(String id)
    {
        this.oid = JetspeedObjectID.createFromString(id);
    }

    private static final Log log = LogFactory.getLog(PortletEntityImpl.class);

    protected PreferenceSetComposite preferences = new PreferenceSetImpl();
    protected PreferenceSetComposite originalPreferences;

    private PortletApplicationEntity applicationEntity = null;

    private PortletWindowList portletWindows = new PortletWindowListImpl();

    private PortletEntity modifiedObject = null;

    private PortletDefinitionComposite portletDefinition = null;

    public PreferenceSet getPreferenceSet()
    {
        return preferences;
    }

    public PortletDefinition getPortletDefinition()
    {
        return this.portletDefinition;
    }

    public PortletApplicationEntity getPortletApplicationEntity()
    {
        return applicationEntity;
    }

    public PortletWindowList getPortletWindowList()
    {
        return portletWindows;
    }

    public void store() throws java.io.IOException
    {
        try
        {
            PortletEntityAccess.storePortletEntity(this);
        }
        catch (PortletEntityNotStoredException e)
        {
            throw new IOException("Unable to store Portlet Entity. "+e.toString());
        }

    }

    public void reset() throws java.io.IOException
    {
        ((Collection) preferences).clear();
        if (originalPreferences != null)
        {
            ((Collection) preferences).addAll((Collection) originalPreferences);
        }
    }

    // internal methods used for debugging purposes only

    public String toString()
    {
        return toString(0);
    }

    public String toString(int indent)
    {
        StringBuffer buffer = new StringBuffer(1000);
        StringUtils.newLine(buffer, indent);
        buffer.append(getClass().toString());
        buffer.append(":");
        StringUtils.newLine(buffer, indent);
        buffer.append("{");
        StringUtils.newLine(buffer, indent);
        buffer.append("id='");
        buffer.append(oid);
        buffer.append("'");
        StringUtils.newLine(buffer, indent);
        buffer.append("definition-id='");
        buffer.append(portletDefinition.getId().toString());
        buffer.append("'");

        StringUtils.newLine(buffer, indent);
        //buffer.append(((PreferenceSetImpl)preferences).toString(indent));

        StringUtils.newLine(buffer, indent);
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * @see org.apache.pluto.om.entity.PortletEntity#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        return portletDefinition.getDescription(arg0);
    }

    /**
     * <p>
     * setPortletDefinition
     * </p>
     * 
     * @param composite
     * 
     */
    public void setPortletDefinition(PortletDefinition composite)
    {
        portletDefinition = (PortletDefinitionComposite) composite;
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
    	if(originalPreferences == null)
    	{
			originalPreferences = new PreferenceSetImpl();
    	}
        ((Collection) originalPreferences).addAll((Collection) preferences);
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
      
    }

}
