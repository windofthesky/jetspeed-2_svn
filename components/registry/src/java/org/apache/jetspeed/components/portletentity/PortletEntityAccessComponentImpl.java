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

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.preference.impl.PrefsPreferenceSetImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletEntityAccessComponentImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: PortletEntityAccessComponentImpl.java,v 1.21 2004/07/02
 *          13:30:24 weaver Exp $
 *  
 */
public class PortletEntityAccessComponentImpl implements PortletEntityAccessComponent
{
    protected final static Log log = LogFactory.getLog(PortletEntityAccessComponentImpl.class);

    private boolean autoCreateNewEntities;

    // TODO: this should eventually use a system cach like JCS
    private HashMap entityCache = new HashMap();

    private PersistenceStore persistenceStore;

    private PortletRegistry registry;

    protected Principal principal;

    /**
     * 
     * @param persistenceStore
     * @param registry
     */
    public PortletEntityAccessComponentImpl( PersistenceStore persistenceStore, PortletRegistry registry )
    {
        this.persistenceStore = persistenceStore;
        this.registry = registry;
        PortletEntityImpl.pac = this;

    }

    /**
     * <p>
     * generateEntityFromFragment
     * </p>
     * 
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#generateEntityFromFragment(org.apache.jetspeed.om.page.Fragment,
     *      java.security.Principal)
     * @param fragment
     * @param principal
     * @return
     */
    public MutablePortletEntity generateEntityFromFragment( Fragment fragment, String principal )
            throws PortletEntityNotGeneratedException
    {
        PortletDefinition pd = registry.getPortletDefinitionByUniqueName(fragment.getName());
        ObjectID entityKey = generateEntityKey(fragment, principal);

        if (pd == null)
        {
            throw new PortletEntityNotGeneratedException("Failed to retrieve Portlet Definition for "
                    + fragment.getName());
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
     * @return @throws
     *         PortletEntityNotGeneratedException
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
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#getPortletEntityForFragment(org.apache.jetspeed.om.page.Fragment,
     *      java.lang.String)
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
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#generateEntityKey(org.apache.jetspeed.om.page.Fragment,
     *      java.lang.String)
     * @param fragment
     * @param principal
     * @return
     */
    public ObjectID generateEntityKey( Fragment fragment, String principal )
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

    public MutablePortletEntity getPortletEntity( String entityId )
    {
        ObjectID oid = JetspeedObjectID.createFromString(entityId);
        return getPortletEntity(oid);
    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#getPortletEntity(org.apache.pluto.om.common.ObjectID)
     *  
     */
    public MutablePortletEntity getPortletEntity( ObjectID entityId )
    {
        if (entityCache.get(entityId) != null)
        {
            PortletEntityImpl entity = (PortletEntityImpl) entityCache.get(entityId);            
            return entity;
        }
        else
        {
            PersistenceStore store = getPersistenceStore();
            prepareTransaction(store);

            Filter filter = store.newFilter();
            filter.addEqualTo("id", entityId.toString());
            Object q = store.newQuery(PortletEntityImpl.class, filter);
            MutablePortletEntity portletEntity = (MutablePortletEntity) store.getObjectByQuery(q);
            if (portletEntity == null)
            {
                return null;
            }
            else
            {
                
                String portletUniqueName = portletEntity.getPortletUniqueName();
                PortletDefinitionComposite parentPortletDef = registry
                        .getPortletDefinitionByUniqueName(portletUniqueName);
                ((PortletEntityCtrl) portletEntity).setPortletDefinition(parentPortletDef);
                entityCache.put(entityId, portletEntity);
                return (PortletEntityImpl) portletEntity;
            }
        }
    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#newPortletEntityInstance(org.apache.pluto.om.portlet.PortletDefinition)
     */
    public MutablePortletEntity newPortletEntityInstance( PortletDefinition portletDefinition )
    {
        PortletEntityImpl portletEntity = new PortletEntityImpl();

        portletEntity.setPortletDefinition(portletDefinition);

        return (PortletEntityImpl) portletEntity;
    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#removePortletEntity(org.apache.pluto.om.entity.PortletEntity)
     */
    public void removePortletEntity( PortletEntity portletEntity ) throws PortletEntityNotDeletedException
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

    public void removeFromCache( PortletEntity entity )
    {
        entityCache.remove(entity.getId());
        try
        {
            persistenceStore.invalidate(entity);
        }
        catch (LockFailedException e)
        {
            log.error("Unable to remove entity from PersistenceStoreCache " + e.toString(), e);
        }
    }

    /**
     * @see org.apache.jetspeed.entity.PortletEntityAccessComponent#storePortletEntity(org.apache.pluto.om.entity.PortletEntity)
     */
    public void storePortletEntity( PortletEntity portletEntity ) throws PortletEntityNotStoredException
    {

        try
        {
            ((PortletEntityCtrl) portletEntity).store();
        }
        catch (Exception e)
        {
            log.error(e.toString(), e);
            throw new PortletEntityNotStoredException(e.toString(), e);
        }

    }

    public Collection getPortletEntities( PortletDefinition portletDefinition )
    {
        prepareTransaction(persistenceStore);
        Filter filter = persistenceStore.newFilter();
        String appName = ((MutablePortletApplication) portletDefinition.getPortletApplicationDefinition()).getName();
        String portletName = portletDefinition.getName();
        filter.addEqualTo("appName", appName);
        filter.addEqualTo("portletName", portletName);

        return persistenceStore.getCollectionByQuery(persistenceStore.newQuery(PortletEntityImpl.class, filter));
    }

    public void removePortletEntities( PortletDefinition portletDefinition ) throws PortletEntityNotDeletedException
    {
        Iterator entities = getPortletEntities(portletDefinition).iterator();
        while (entities.hasNext())
        {
            PortletEntity entity = (PortletEntity) entities.next();
            removePortletEntity(entity);
        }
    }

    private void autoCreateEntityId( PortletEntity realEntity, PersistenceStore store ) throws Exception
    {
        if (realEntity instanceof PortletEntityImpl)
        {
            PortletEntityImpl impl = (PortletEntityImpl) realEntity;
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
     * Checks to see if the <code>store</code>'s current transaction needs to
     * be started or not.
     * 
     * @param store
     */
    protected void prepareTransaction( PersistenceStore store )
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

    public void storePreferenceSet( PreferenceSet prefSet, PortletEntity entity ) throws IOException
    {
        PrefsPreferenceSetImpl preferenceSet = (PrefsPreferenceSetImpl) prefSet;
        try
        {
            prepareTransaction(persistenceStore);
            persistenceStore.lockForWrite(entity);
            if (preferenceSet != null)
            {
                preferenceSet.flush();
            }
            persistenceStore.getTransaction().checkpoint();

        }
        catch (Exception e)
        {
            String msg = "Failed to store portlet entity:" + e.toString();
            IOException ioe = new IOException(msg);
            ioe.initCause(e);
            persistenceStore.getTransaction().rollback();
            throw ioe;
        }

    }
}