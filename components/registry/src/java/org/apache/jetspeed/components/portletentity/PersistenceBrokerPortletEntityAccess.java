/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.preference.impl.PrefsPreferenceSetImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * PersistenceStorePortletEntityAccess
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class PersistenceBrokerPortletEntityAccess extends PersistenceBrokerDaoSupport
        implements
            PortletEntityAccessComponent
{
    private PortletRegistry registry;

    /**
     * 
     * @param registry
     */
    public PersistenceBrokerPortletEntityAccess( PortletRegistry registry )
    {
        super();
        this.registry = registry;
        PortletEntityImpl.pac = this;
    }

    /**
     * 
     * <p>
     * generateEntityFromFragment
     * </p>
     * 
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#generateEntityFromFragment(org.apache.jetspeed.om.page.Fragment,
     *      java.lang.String)
     * @param fragment
     * @param principal
     * @return @throws
     *         PortletEntityNotGeneratedException
     */
    public MutablePortletEntity generateEntityFromFragment( Fragment fragment, String principal )
            throws PortletEntityNotGeneratedException
    {
        PortletDefinition pd = registry.getPortletDefinitionByUniqueName(fragment.getName());
        ObjectID entityKey = generateEntityKey(fragment, principal);
        MutablePortletEntity portletEntity = null;

        if (pd != null)
        {
            portletEntity = newPortletEntityInstance(pd);
            if (portletEntity == null)
            {
                throw new PortletEntityNotGeneratedException("Failed to create Portlet Entity for "
                        + fragment.getName());
            }
        }
        else
        {
            String msg = "Failed to retrieve Portlet Definition for " + fragment.getName();
            logger.warn(msg);
            portletEntity = new PortletEntityImpl();
            fragment.overrideRenderedContent(msg);
        }

        portletEntity.setId(entityKey.toString());

        return portletEntity;
    }

    /**
     * 
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

    /**
     * 
     * <p>
     * getPortletEntities
     * </p>
     * 
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#getPortletEntities(org.apache.pluto.om.portlet.PortletDefinition)
     * @param portletDefinition
     * @return
     */
    public Collection getPortletEntities( PortletDefinition portletDefinition )
    {
        Criteria c = new Criteria();
        String appName = ((MutablePortletApplication) portletDefinition.getPortletApplicationDefinition()).getName();
        String portletName = portletDefinition.getName();
        c.addEqualTo("appName", appName);
        c.addEqualTo("portletName", portletName);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PortletEntityImpl.class, c));
    }

    public MutablePortletEntity getPortletEntity( ObjectID id )
    {
        Criteria c = new Criteria();
        c.addEqualTo("id", id.toString());
        Query q = QueryFactory.newQuery(PortletEntityImpl.class, c);
        MutablePortletEntity portletEntity = (MutablePortletEntity) getPersistenceBrokerTemplate().getObjectByQuery(q);
        if (portletEntity == null)
        {
            return null;
        }
        else
        {

            String portletUniqueName = portletEntity.getPortletUniqueName();
            PortletDefinitionComposite parentPortletDef = registry.getPortletDefinitionByUniqueName(portletUniqueName);
            if(parentPortletDef != null)
            {
                ((PortletEntityCtrl) portletEntity).setPortletDefinition(parentPortletDef);
                return (MutablePortletEntity) portletEntity;
            }
            else
            {
                logger.warn("No parent portlet definition could be located using unique name: "+portletUniqueName+
                            ".  Unless you plan on redploying this portlet definition, it is highly recommended "+
                            "that you delete the orphaned portlet entity with the id: "+portletEntity.getId());
                return (MutablePortletEntity) portletEntity;
            }
                
        }
    }

    public MutablePortletEntity getPortletEntity( String id )
    {
        ObjectID oid = JetspeedObjectID.createFromString(id);
        return getPortletEntity(oid);
    }

    public MutablePortletEntity getPortletEntityForFragment( Fragment fragment, String principal )
    {
        return getPortletEntity(generateEntityKey(fragment, principal));
    }

    public MutablePortletEntity getPortletEntityForFragment( Fragment fragment )
    {
        return getPortletEntity(generateEntityKey(fragment, null));
    }

    public MutablePortletEntity newPortletEntityInstance( PortletDefinition portletDefinition )
    {
        PortletEntityImpl portletEntity = new PortletEntityImpl();
        portletEntity.setPortletDefinition(portletDefinition);
        return (PortletEntityImpl) portletEntity;
    }

    public MutablePortletEntity newPortletEntityInstance(PortletDefinition portletDefinition, String id)
    {
        PortletEntityImpl portletEntity = new PortletEntityImpl();
        portletEntity.setPortletDefinition(portletDefinition);
        portletEntity.setId(id);
        return (PortletEntityImpl) portletEntity;
    }
    
    
    public void removeFromCache( PortletEntity entity )
    {
        // TODO Auto-generated method stub

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

    public void removePortletEntity( PortletEntity portletEntity ) throws PortletEntityNotDeletedException
    {
        PrefsPreferenceSetImpl prefsSet  = (PrefsPreferenceSetImpl) portletEntity.getPreferenceSet();
        getPersistenceBrokerTemplate().delete(portletEntity);
        try
        {
            prefsSet.clear();
        }
        catch (BackingStoreException e)
        {
            throw new PortletEntityNotDeletedException("Failed to remove preferences for portlet entity "+portletEntity.getId()+".  "+e.getMessage(), e);
        }
    }

    public void storePortletEntity( PortletEntity portletEntity ) throws PortletEntityNotStoredException
    {
        try
        {
            ((PortletEntityCtrl) portletEntity).store();
        }
        catch (Exception e)
        {
            throw new PortletEntityNotStoredException(e.toString(), e);
        }

    }

    /**
     * <p>
     * storePreferenceSet
     * </p>
     * 
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#storePreferenceSet(org.apache.pluto.om.common.PreferenceSet)
     * @param prefSet
     * @throws IOException
     */
    public void storePreferenceSet( PreferenceSet prefSet, PortletEntity entity ) throws IOException
    {
        PrefsPreferenceSetImpl preferenceSet = (PrefsPreferenceSetImpl) prefSet;
        try
        {            
            getPersistenceBrokerTemplate().store(entity);
            if (preferenceSet != null)
            {
                preferenceSet.flush();
            }            

        }
        catch (Exception e)
        {
            String msg = "Failed to store portlet entity:" + e.toString();
            IOException ioe = new IOException(msg);
            ioe.initCause(e);            
            throw ioe;
        }

    }
}