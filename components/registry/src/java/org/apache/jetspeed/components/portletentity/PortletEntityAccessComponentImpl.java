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
            filter.addEqualTo("id", entityId.toString());
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
     *
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
    */
    
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
                store.getTransaction().checkpoint();                
               // autoCreateEntityId(realEntity, store);
			}            
			else
			{
				store.lockForWrite(portletEntity);
                store.getTransaction().checkpoint();
              //  autoCreateEntityId(portletEntity, store);                
			}
                        
        }
        catch (Exception e)
        {
            String msg = "Unable to store Portlet Entity.";
            log.error(msg, e);
            store.getTransaction().rollback();

            throw new PortletEntityNotStoredException(msg, e);
        }

    }

    
    private void autoCreateEntityId(PortletEntity realEntity, PersistenceStore store)
        throws Exception
    {
        if (realEntity instanceof PortletEntityImpl)
        {
            PortletEntityImpl impl = (PortletEntityImpl)realEntity;
            if (impl.getId() == null)
            {
                System.out.println("setting oid = " + impl.getOid());                    
                impl.setId(new Long(impl.getOid()).toString());
                store.lockForWrite(realEntity);
                store.getTransaction().checkpoint();                        
            }
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
