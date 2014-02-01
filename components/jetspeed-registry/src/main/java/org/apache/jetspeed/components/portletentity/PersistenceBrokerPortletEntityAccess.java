/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

import java.rmi.server.UID;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>
 * PersistenceStorePortletEntityAccess
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: PersistenceBrokerPortletEntityAccess.java,v 1.5 2005/04/29 13:59:08 weaver Exp $
 * @obsolete
 *  
 */
public class PersistenceBrokerPortletEntityAccess extends PersistenceBrokerDaoSupport
        implements
            PortletEntityAccessComponent
{
    private PortletRegistry registry;
    
    // 2006-08-22: by default, do not merge preferences from the shared preferences area 
    // up until this point, all preferences were shared. With JS2-449, preferences are now
    // stored 'per user'. The username is stored in the preferences FULL_PATH
    // To turn on mergeSharedPreferences configure this property to true 
    // in your Spring configuration
    boolean mergeSharedPreferences = false;
    
    /**
     * 
     * @param registry
     */
    public PersistenceBrokerPortletEntityAccess( PortletRegistry registry )
    {
        this(registry, false);
    }

    public PersistenceBrokerPortletEntityAccess(PortletRegistry registry, boolean mergeSharedPreferences)
    {
        super();
        this.registry = registry;
        this.mergeSharedPreferences = mergeSharedPreferences;
    }
    
    /**
     * 
     * <p>
     * generateEntityFromFragment
     * </p>
     * 
     * @param fragment
     * @return @throws
     *         PortletEntityNotGeneratedException
     */
    public PortletEntity generateEntityFromFragment( ContentFragment fragment)
            throws PortletEntityNotGeneratedException
    {
        PortletDefinition pd = registry.getPortletDefinitionByUniqueName(fragment.getName());
        String entityKey = generateEntityKey(fragment);
        PortletEntity portletEntity = null;

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
            portletEntity = new PortletEntityImpl(fragment);
            fragment.overrideRenderedContent(msg);
        }

        portletEntity.setId(entityKey);

        return portletEntity;
    }

    /**
     * 
     * <p>
     * generateEntityKey
     * </p>
     * 
     * @param fragment
     * @return
     */
    public String generateEntityKey( ContentFragment fragment)
    {
        return fragment.getId();
    }

    /**
     * 
     * <p>
     * getPortletEntities
     * </p>
     * 
     * @param portletDefinition
     * @return
     */
    public Collection getPortletEntities( PortletDefinition portletDefinition )
    {
        Criteria c = new Criteria();
        String appName = portletDefinition.getApplication().getName();
        String portletName = portletDefinition.getPortletName();
        c.addEqualTo("appName", appName);
        c.addEqualTo("portletName", portletName);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PortletEntityImpl.class, c));
    }
    
    public Collection getPortletEntities( String portletUniqueName )
    {        
        String[] split = portletUniqueName.split("::");
        String appName = split[0];
        String portletName = split[1];
        Criteria c = new Criteria();
        c.addEqualTo("appName", appName);
        c.addEqualTo("portletName", portletName);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(PortletEntityImpl.class, c));
    }

    public PortletEntity getPortletEntity( String id )
    {
        try
        {
            return getPortletEntity(id, null);
        }
        // This exception is only thrown if a Fragment has been passed into the
        // getPortletEntity() method.  This should never happen.
        catch (PortletEntityNotStoredException e)
        {
            IllegalStateException ise = new IllegalStateException("Unexepected error while retrieving portlet entity "+id);
            ise.initCause(e);
            throw ise;
        }
    }

    protected PortletEntity getPortletEntity(String id, ContentFragment fragment) throws PortletEntityNotStoredException
    {
        Criteria c = new Criteria();
        c.addEqualTo("id", id.toString());
        Query q = QueryFactory.newQuery(PortletEntityImpl.class, c);
        PortletEntity portletEntity = (PortletEntity) getPersistenceBrokerTemplate().getObjectByQuery(q);
        if (portletEntity == null)
        {
            return null;
        }
        else
        {
            String portletUniqueName = portletEntity.getPortletUniqueName();
            PortletDefinition parentPortletDef = registry.getPortletDefinitionByUniqueName(portletUniqueName);
            if(parentPortletDef != null)
            {
                //Indication that the fragment has changed the portlet it references.
                if(fragment != null && !portletUniqueName.equals(fragment.getName()))
                {
                    parentPortletDef = registry.getPortletDefinitionByUniqueName(fragment.getName());
                    portletEntity.setPortletDefinition(parentPortletDef);
                    storePortletEntity(portletEntity);
                }
                else
                {
                    portletEntity.setPortletDefinition(parentPortletDef);
                }
            }
            else if(fragment != null && parentPortletDef == null)
            {
                // If we have no porlet definition but have a fragment, we see if the
                // unique name has changed and access the portlet definition
                // using that unique name.
                parentPortletDef = registry.getPortletDefinitionByUniqueName(fragment.getName());
                if ( parentPortletDef != null)
                {
                    portletEntity.setPortletDefinition(parentPortletDef);
                    storePortletEntity(portletEntity);
                }
            }
            
            if(parentPortletDef == null)
            {
                final String msg = "Portlet "+portletUniqueName+" not found";
                String content = fragment.getOverriddenContent();
                if (content == null || !content.equals(msg))
                {
                    fragment.overrideRenderedContent(msg);
                    logger.error(msg);
                }
            }           
            
            return portletEntity;                
        }
    }

    public PortletEntity getPortletEntityForFragment( ContentFragment fragment ) throws PortletEntityNotStoredException
    {
        return getPortletEntity(generateEntityKey(fragment), fragment);
    }

    public PortletEntity newPortletEntityInstance( PortletDefinition portletDefinition )
    {
        return newPortletEntityInstance(portletDefinition, autoGenerateID(portletDefinition));
    }

    public PortletEntity newPortletEntityInstance(PortletDefinition portletDefinition, String id)
    {
        PortletEntityImpl portletEntity = new PortletEntityImpl();
        portletEntity.setPortletDefinition(portletDefinition);
        portletEntity.setId(id);
        return portletEntity;
    }
    
    
    public void removeFromCache(PortletEntity entity)
    {
    }

    public void removePortletEntities( PortletDefinition portletDefinition ) throws PortletEntityNotDeletedException
    {
        Iterator<PortletEntity> entities = getPortletEntities(portletDefinition).iterator();
        while (entities.hasNext())
        {
            PortletEntity entity =  entities.next();
            removePortletEntity(entity);
        }

    }

    public void removePortletEntity( PortletEntity portletEntity ) throws PortletEntityNotDeletedException
    {
        getPersistenceBrokerTemplate().delete(portletEntity);
        removeFromCache(portletEntity);
    }

    /**
     * <p>
     * updatePortletEntity
     * </p>
     *
     * Updates portlet definition associated with the portlet
     * entity to match the fragment configuration 
     *
     * @param portletEntity
	 * @param fragment
	 * @throws PortletEntityNotStoredException 
     */
    public void updatePortletEntity(PortletEntity portletEntity, ContentFragment fragment) throws PortletEntityNotStoredException
    {
        // validate portlet entity id
        if (!fragment.getId().equals(portletEntity.getId().toString()))
        {
            throw new PortletEntityNotStoredException("Fragment and PortletEntity ids do not match, update skipped: " + fragment.getId() + " != " + portletEntity.getId() );
        }

        // update portlet definition from fragment
        PortletDefinition pd = registry.getPortletDefinitionByUniqueName(fragment.getName());
        if (pd != null)
        {
            ((PortletEntityImpl)portletEntity).setPortletDefinition(pd);
        }
        else
        {
            throw new PortletEntityNotStoredException("Fragment PortletDefinition not found: " + fragment.getName() );
        }
    }

    public void storePortletEntity( PortletEntity portletEntity ) throws PortletEntityNotStoredException
    {
        try
        {
            getPersistenceBrokerTemplate().store(portletEntity);
        }
        catch (Exception e)
        {
            throw new PortletEntityNotStoredException("Failed to store portlet Entity: "+e.toString(), e);
        }

    }

    protected String autoGenerateID(PortletDefinition pd)
    {
        String appName = pd.getApplication().getName();
        String portletName = pd.getPortletName();
        return appName+"::"+portletName+"::"+new UID().toString();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent#isMergeSharedPreferences()
     */
    public boolean isMergeSharedPreferences()
    {
        return this.mergeSharedPreferences;
    }

    
}
