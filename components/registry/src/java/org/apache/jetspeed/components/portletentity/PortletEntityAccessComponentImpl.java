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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.Storeable;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.impl.StoreablePortletDefinitionDelegate;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.picocontainer.Startable;

/**
 * <p>
 * PortletEntityAccessComponentImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletEntityAccessComponentImpl implements PortletEntityAccessComponent, PersistenceStoreEventListener, Startable
{
    protected final static Log log = LogFactory.getLog(PortletEntityAccessComponentImpl.class);

    private boolean autoCreateNewEntities;

    // TODO: this should eventually use a system cach like JCS
    private HashMap entityCache = new HashMap();

    private PersistenceStore persistenceStore;

    private PortletRegistryComponent registry;
    

    /**
     * 
     * @param persistenceStore
     * @param registry
     */
    public PortletEntityAccessComponentImpl(PersistenceStore persistenceStore, PortletRegistryComponent registry)
    {
        this.persistenceStore = persistenceStore;
        this.registry = registry;
        
    }
    

    /**
     * <p>
     * generateEntityFromFragment
     * </p>
     *
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#generateEntityFromFragment(org.apache.jetspeed.om.page.Fragment, java.security.Principal)
     * @param fragment
     * @param principal
     * @return
     */
    public MutablePortletEntity generateEntityFromFragment( Fragment fragment, String principal ) throws PortletEntityNotGeneratedException
    {       
        PortletDefinition pd = registry.getPortletDefinitionByUniqueName(fragment.getName());
        ObjectID entityKey = generateEntityKey(fragment, principal);
        
        if (pd == null)
        {
            throw new PortletEntityNotGeneratedException("Failed to retrieve Portlet Definition for " + fragment.getName());
        }
        MutablePortletEntity portletEntity = newPortletEntityInstance(pd);
        if (portletEntity == null)
        {
            throw new PortletEntityNotGeneratedException("Failed to create Portlet Entity for " + fragment.getName());
        }
        portletEntity.setId(entityKey.toString());
              
        return portletEntity;
    }
    
    /**
     * <p>
     * generateEntityFromFragment
     * </p>
     *
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#generateEntityFromFragment(org.apache.jetspeed.om.page.Fragment)
     * @param fragment
     * @return
     * @throws PortletEntityNotGeneratedException
     */
    public MutablePortletEntity generateEntityFromFragment( Fragment fragment )
            throws PortletEntityNotGeneratedException
    {
        return generateEntityFromFragment(fragment, null);
    }
    
    /**
     * <p>
     * getPortletEntityForFragment
     * </p>
     *
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#getPortletEntityForFragment(org.apache.jetspeed.om.page.Fragment, java.lang.String)
     * @param fragment
     * @param principal
     * @return
     */
    public MutablePortletEntity getPortletEntityForFragment( Fragment fragment, String principal )
    {
        return getPortletEntity(generateEntityKey(fragment, principal));
    }
    
    /**
     * <p>
     * getPortletEntityForFragment
     * </p>
     *
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#getPortletEntityForFragment(org.apache.jetspeed.om.page.Fragment)
     * @param fragment
     * @return
     */
    public MutablePortletEntity getPortletEntityForFragment( Fragment fragment )
    {
        return getPortletEntity(generateEntityKey(fragment, null));
    }
    
    /**
     * 
     * <p>
     * generateEntityKey
     * </p>
     *
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#generateEntityKey(org.apache.jetspeed.om.page.Fragment, java.lang.String)
     * @param fragment
     * @param principal
     * @return
     */
    public ObjectID generateEntityKey(Fragment fragment, String principal)
    {
        StringBuffer key = new StringBuffer();
        if (principal != null && principal.length() > 0)
        {
            key.append(principal);
            key.append("/");
        }
        key.append(fragment.getId());
        return JetspeedObjectID.createFromString(key.toString());
    }
    
    
    public MutablePortletEntity getPortletEntity(String entityId)
    {
        ObjectID oid = JetspeedObjectID.createFromString(entityId);
        return getPortletEntity(oid);
    }
    
    
    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#getPortletEntity(org.apache.pluto.om.common.ObjectID)
     * 
     */
    public MutablePortletEntity getPortletEntity(ObjectID entityId)
    {
        if (entityCache.get(entityId) != null)
        {
            PortletEntityImpl entity = (PortletEntityImpl) entityCache.get(entityId);
            entity.setStore(persistenceStore);
            return entity;
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
                ((Storeable) portletEntity).setStore(persistenceStore);
                entityCache.put(entityId, portletEntity);
                return (PortletEntityImpl) portletEntity;
            }
        }
    }

    
    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#newPortletEntityInstance(org.apache.pluto.om.portlet.PortletDefinition)
     */
    public MutablePortletEntity newPortletEntityInstance(PortletDefinition portletDefinition)
    {
        PortletEntityImpl portletEntity = new PortletEntityImpl();
        if (portletDefinition instanceof StoreablePortletDefinitionDelegate)
        {
            portletEntity.setPortletDefinition(((StoreablePortletDefinitionDelegate) portletDefinition).getPortlet());
        } 
        else
        {
            portletEntity.setPortletDefinition(portletDefinition);
        }
        portletEntity.setStore(persistenceStore);
        
        return (PortletEntityImpl)  portletEntity;

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
            
            entityCache.remove(portletEntity.getId());
            
            store.deletePersistent(portletEntity);            

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
    
    public void removeFromCache(PortletEntity entity)
    {
       entityCache.remove(entity.getId());
    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#storePortletEntity(org.apache.pluto.om.entity.PortletEntity)
     */
    public void storePortletEntity(PortletEntity portletEntity) throws PortletEntityNotStoredException
    {

        try
        {
            ((PortletEntityCtrl)portletEntity).store();
        }
        catch (Exception e)
        {
            log.error(e.toString(), e);     
            throw new PortletEntityNotStoredException(e.toString(), e);
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
        return persistenceStore;
    }
    /**
     * <p>
     * afterClose
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterClose(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterClose( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * afterDeletePersistent
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterDeletePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterDeletePersistent( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * afterLookup
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterLookup(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterLookup( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * afterMakePersistent
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#afterMakePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterMakePersistent( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * beforeClose
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeClose(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeClose( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * beforeDeletePersistent
     * </p>
     * Removes the entity being deleted from the internal entity cache.
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeDeletePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeDeletePersistent( PersistenceStoreEvent event )
    {
        Object target = event.getTarget();
        if(target != null && target instanceof MutablePortletEntity)
        {
            MutablePortletEntity entity = (MutablePortletEntity) target;
            removeFromCache(entity);            
        }

    }
    /**
     * <p>
     * beforeLookup
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeLookup(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeLookup( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * beforeMakePersistent
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener#beforeMakePersistent(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeMakePersistent( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * afterBegin
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterBegin( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * afterCommit
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterCommit( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * afterRollback
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#afterRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void afterRollback( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * beforeBegin
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeBegin(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeBegin( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * beforeCommit
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeCommit(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeCommit( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * beforeRollback
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.TransactionEventListener#beforeRollback(org.apache.jetspeed.components.persistence.store.PersistenceStoreEvent)
     * @param event
     */
    public void beforeRollback( PersistenceStoreEvent event )
    {
        // TODO Auto-generated method stub

    }
    /**
     * <p>
     * start
     * </p>
     *
     * @see org.picocontainer.Startable#start()
     * 
     */
    public void start()
    {
        persistenceStore.addEventListener(this);

    }
    /**
     * <p>
     * stop
     * </p>
     *
     * @see org.picocontainer.Startable#stop()
     * 
     */
    public void stop()
    {
        // TODO Auto-generated method stub

    }
}
