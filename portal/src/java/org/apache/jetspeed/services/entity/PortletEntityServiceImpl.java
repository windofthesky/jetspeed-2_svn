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
package org.apache.jetspeed.services.entity;

import java.util.HashMap;

import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.entity.impl.PortletEntityImpl;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletEntityServiceImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletEntityServiceImpl extends BaseCommonService implements PortletEntityService
{

    // TODO: this should eventually use a system cach like JCS
    private HashMap entityCache = new HashMap();
    private PersistencePlugin plugin;
    private boolean autoCreateNewEntities;

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");
            autoCreateNewEntities = getConfiguration().getBoolean("autocreate", false);
            plugin = ps.getPersistencePlugin(pluginName);
            setInit(true);
        }

    }

    /**
     * @see org.apache.jetspeed.services.entity.PortletEntityService#getPortletEntity(org.apache.pluto.om.common.ObjectID)
     */
    public PortletEntity getPortletEntity(ObjectID entityId)
    {
        if (entityCache.containsKey(entityId))
        {
            return (PortletEntity) entityCache.get(entityId);
        }
        else
        {

            LookupCriteria c = plugin.newLookupCriteria();
            c.addEqualTo("oid", entityId);
            Object q = plugin.generateQuery(PortletEntityImpl.class, c);
            PortletEntity portletEntity = (PortletEntity) plugin.getObjectByQuery(PortletEntityImpl.class, q);

            entityCache.put(entityId, portletEntity);
            return portletEntity;
        }
    }

    /**
     * @see org.apache.jetspeed.services.entity.PortletEntityService#getPortletEntity(org.apache.jetspeed.request.RequestContext)
     */
    public PortletEntity getPortletEntity(PortletDefinition portletDefinition, String entityName)
    {
        ObjectID entityId = JetspeedObjectID.createPortletEntityId(portletDefinition, entityName);
        PortletEntity portletEntity = getPortletEntity(entityId);
        if (portletEntity == null)
        {
            portletEntity = newPortletEntityInstance(portletDefinition);
            ((PortletEntityCtrl) portletEntity).setId(entityId.toString());
        }
        return portletEntity;
    }

    /**
     * @see org.apache.jetspeed.services.entity.PortletEntityService#storePortletEntity(org.apache.pluto.om.entity.PortletEntity)
     */
    public void storePortletEntity(PortletEntity portletEntity)
    {
        plugin.update(portletEntity);

    }

    /**
     * @see org.apache.jetspeed.services.entity.PortletEntityService#removePortletEntity(org.apache.pluto.om.entity.PortletEntity)
     */
    public void removePortletEntity(PortletEntity portletEntity)
    {
        plugin.delete(portletEntity);
    }

    /**
     * @see org.apache.jetspeed.services.entity.PortletEntityService#newPortletEntityInstance()
     */
    public PortletEntity newPortletEntityInstance(PortletDefinition portletDefinition)
    {
        // TODO: need to be made configurable
        PortletEntityImpl portletEntity = new PortletEntityImpl();
        portletEntity.setPortletDefinition(portletDefinition);
        return portletEntity;
    }

}
