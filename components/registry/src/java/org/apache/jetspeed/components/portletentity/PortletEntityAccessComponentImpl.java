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
package org.apache.jetspeed.components.portletentity;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.om.portlet.impl.StoreablePortletDefinitionDelegate;
import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletEntityAccessComponentImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletEntityAccessComponentImpl implements PortletEntityAccessComponent
{
    private PersistenceStoreContainer pContainer;

    private String storeName;

    protected final static Log log = LogFactory.getLog(PortletEntityAccessComponentImpl.class);

    private boolean autoCreateNewEntities;

    // TODO: this should eventually use a system cach like JCS
    private HashMap entityCache = new HashMap();

    

    public PortletEntityAccessComponentImpl(PersistenceStoreContainer pContainer, String storeId)
    {
        this.pContainer = pContainer;
        this.storeName = storeId;
    }
    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#getPortletEntity(org.apache.pluto.om.common.ObjectID)
     * 
     */
    public StoreablePortletEntityDelegate getPortletEntity(ObjectID entityId)
    {
        if (entityCache.get(entityId) != null)
        {
            return wrapEntity((PortletEntityImpl) entityCache.get(entityId));
        }
        else
        {
            PersistenceStore store = getPersistenceStore();
            prepareTransaction(store);
            
            Filter filter = store.newFilter();
            filter.addEqualTo("oid", entityId.toString());
            Object q = store.newQuery(PortletEntityImpl.class, filter);
            PortletEntity portletEntity = (PortletEntity) store.getObjectByQuery(q);
            if(portletEntity == null)
            {
                return null;
            } 
            else
            {
                entityCache.put(entityId, portletEntity);
                return wrapEntity((PortletEntityImpl) portletEntity);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#getPortletEntity(org.apache.pluto.om.portlet.PortletDefinition, java.lang.String)
     */
    public StoreablePortletEntityDelegate getPortletEntity(PortletDefinition portletDefinition, String entityName)
    {
        ObjectID entityId = JetspeedObjectID.createPortletEntityId(portletDefinition, entityName);
        PortletEntity portletEntity = getPortletEntity(entityId);
        if (portletEntity == null)
        {
            portletEntity = newPortletEntityInstance(portletDefinition);
            ((PortletEntityCtrl) portletEntity).setId(entityId.toString());
        }
        return (StoreablePortletEntityDelegate)portletEntity; //wrapEntity(portletEntity);
    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#newPortletEntityInstance(org.apache.pluto.om.portlet.PortletDefinition)
     */
    public StoreablePortletEntityDelegate newPortletEntityInstance(PortletDefinition portletDefinition)
    {
        PortletEntityCtrl portletEntity = new PortletEntityImpl();
        if (portletDefinition instanceof StoreablePortletDefinitionDelegate)
        {
            portletEntity.setPortletDefinition(((StoreablePortletDefinitionDelegate) portletDefinition).getPortlet());
        } 
        else
        {
            portletEntity.setPortletDefinition(portletDefinition);
        }
        return wrapEntity((PortletEntityImpl)  portletEntity);

    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#removePortletEntity(org.apache.pluto.om.entity.PortletEntity)
     */
    public void removePortletEntity(PortletEntity portletEntity) throws PortletEntityNotDeletedException
    {
        if (entityCache.containsKey(portletEntity.getId()))
        {
            entityCache.remove(entityCache.get(portletEntity.getId()));
        }

        PersistenceStore store = getPersistenceStore();
        try
        {
            prepareTransaction(store);
            if (portletEntity instanceof StoreablePortletEntityDelegate)
            {
				store.deletePersistent(((StoreablePortletEntityDelegate)portletEntity).getPortletEntity());
            }            
            else
            {
                store.deletePersistent(portletEntity);
            }

            store.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to delete Portlet Entity.";
            log.error(msg, e);

            store.getTransaction().rollback();

            throw new PortletEntityNotDeletedException(msg, e);
        }

    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#storePortletEntity(org.apache.pluto.om.entity.PortletEntity)
     */
    public void storePortletEntity(PortletEntity portletEntity) throws PortletEntityNotStoredException
    {
        PersistenceStore store = getPersistenceStore();
        try
        {
            prepareTransaction(store);
            
			if (portletEntity instanceof StoreablePortletEntityDelegate)
			{
			    PortletEntity realEntity = ((StoreablePortletEntityDelegate)portletEntity).getPortletEntity();
				store.lockForWrite(realEntity);
			}            
			else
			{
				store.lockForWrite(portletEntity);
			}
            store.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to store Portlet Entity.";
            log.error(msg, e);
            store.getTransaction().rollback();

            throw new PortletEntityNotStoredException(msg, e);
        }

    }

    /**
     * Checks to see if the <code>store</code>'s current transaction
     * needs to be started or not.
     * @param store
     */
    protected void prepareTransaction(PersistenceStore store)
    {
        Transaction tx = store.getTransaction();
        if (!tx.isOpen())
        {
            tx.begin();
        }
    }

    protected PersistenceStore getPersistenceStore()
    {
        return pContainer.getStoreForThread(storeName);
    }
    
    protected StoreablePortletEntityDelegate wrapEntity(PortletEntityImpl entity)
    {
		List list =(List) ((PreferenceSetImpl)entity.getPreferenceSet()).getInnerCollection();
		return new StoreablePortletEntityDelegate(entity, entity, list, getPersistenceStore());
    }

}
