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
import java.rmi.server.UID;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
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
 * @version $Id: PersistenceBrokerPortletEntityAccess.java,v 1.5 2005/04/29 13:59:08 weaver Exp $
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
        PortletEntityImpl.registry = registry;
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
    public MutablePortletEntity generateEntityFromFragment( ContentFragment fragment, String principal )
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
            portletEntity = new PortletEntityImpl(fragment);
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
    public MutablePortletEntity generateEntityFromFragment( ContentFragment fragment )
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

    public MutablePortletEntity getPortletEntity( ObjectID id )
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

    protected MutablePortletEntity getPortletEntity(ObjectID id, ContentFragment fragment) throws PortletEntityNotStoredException
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
                //Indication that the fragment has changed the portlet it references.
                if(fragment != null && !portletUniqueName.equals(fragment.getName()))
                {
                    parentPortletDef = registry.getPortletDefinitionByUniqueName(fragment.getName());
                    ((PortletEntityCtrl)portletEntity).setPortletDefinition(parentPortletDef);
                    storePortletEntity(portletEntity);
                }
                else
                {
                    ((PortletEntityCtrl)portletEntity).setPortletDefinition(parentPortletDef);
                }
            }
            else if(fragment != null && parentPortletDef == null)
            {
                // If we have no porlet definition but have a fragment, we see if the
                // unique name has changed and access the portlet definition
                // using that unique name.
                parentPortletDef = registry.getPortletDefinitionByUniqueName(fragment.getName());
                ((PortletEntityCtrl)portletEntity).setPortletDefinition(parentPortletDef);
                storePortletEntity(portletEntity);
            }
            
            
            
            if(parentPortletDef == null)
            {
                final String msg = "No parent portlet definition could be located using unique name: "+portletUniqueName+
                            ".  Unless you plan on redploying this portlet definition, it is highly recommended "+
                            "that you delete the orphaned portlet entity with the id: "+portletEntity.getId();
                fragment.overrideRenderedContent(msg);
                logger.warn(msg);                
            }           
            
            return (MutablePortletEntity) portletEntity;                
        }
    }

    public MutablePortletEntity getPortletEntity( String id )
    {
        ObjectID oid = JetspeedObjectID.createFromString(id);
        return getPortletEntity(oid);
    }

    public MutablePortletEntity getPortletEntityForFragment( ContentFragment fragment, String principal ) throws PortletEntityNotStoredException
    {
        return getPortletEntity(generateEntityKey(fragment, principal), fragment);
    }

    public MutablePortletEntity getPortletEntityForFragment( ContentFragment fragment ) throws PortletEntityNotStoredException
    {
        return getPortletEntity(generateEntityKey(fragment, null), fragment);
    }

    public MutablePortletEntity newPortletEntityInstance( PortletDefinition portletDefinition )
    {
        return newPortletEntityInstance(portletDefinition, autoGenerateID(portletDefinition));
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
    
    protected String autoGenerateID(PortletDefinition pd)
    {
        String appName = ((MutablePortletApplication)pd.getPortletApplicationDefinition()).getName();
        String portletName = pd.getName();
        return appName+"::"+portletName+"::"+new UID().toString();
    }
}