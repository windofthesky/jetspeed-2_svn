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
package org.apache.jetspeed.components.portletregistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.springframework.dao.DataAccessException;

/**
 * <p>
 * OjbPortletRegistry
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class PersistenceBrokerPortletRegistry 
    extends InitablePersistenceBrokerDaoSupport 
    implements PortletRegistry, JetspeedCacheEventListener
{
    /**
     * The separator used to create a unique portlet name as
     * {portletApplication}::{portlet}
     */
    static final String PORTLET_UNIQUE_NAME_SEPARATOR = "::";

    private JetspeedCache applicationOidCache = null;
    private JetspeedCache portletOidCache = null;
    private JetspeedCache applicationNameCache = null;
    private JetspeedCache portletNameCache = null;
    private Map nameCache = new HashMap(); // work in progress (switch to JetspeedCache)
    private List<RegistryEventListener> listeners = new ArrayList<RegistryEventListener>();
    
    // for testing purposes only: no need for the portletFactory then
    public PersistenceBrokerPortletRegistry(String repositoryPath)
    {
        this(repositoryPath, null, null, null, null);
    }
    
    /**
     *  
     */
    public PersistenceBrokerPortletRegistry(String repositoryPath,
            JetspeedCache applicationOidCache, JetspeedCache portletOidCache, 
            JetspeedCache applicationNameCache, JetspeedCache portletNameCache)
    {
        super(repositoryPath);
        this.applicationOidCache = applicationOidCache;
        this.portletOidCache = portletOidCache;
        this.applicationNameCache = applicationNameCache;
        this.portletNameCache = portletNameCache;
        PortletApplicationProxyImpl.setRegistry(this);
        RegistryApplicationCache.cacheInit(this, applicationOidCache, applicationNameCache, listeners);
        RegistryPortletCache.cacheInit(this, portletOidCache, portletNameCache, listeners);
        this.applicationNameCache.addEventListener(this, false);
        this.portletNameCache.addEventListener(this, false);        
    }
    
    public Collection getAllPortletDefinitions()
    {
        Criteria c = new Criteria();
        Collection list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }

    public PortletApplication getPortletApplication(String name)
    {
        Criteria c = new Criteria();
        c.addEqualTo("name", name);
        PortletApplication app = (PortletApplication) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoad(app);
        return app;
    }

    public Collection getPortletApplications()
    {
        Criteria c = new Criteria();
        Collection list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }

    public PortletDefinition getPortletDefinitionByUniqueName( String name )
    {
        String appName = PortletRegistryHelper.parseAppName(name);
        String portletName = PortletRegistryHelper.parsePortletName(name);

        Criteria c = new Criteria();
        c.addEqualTo("app.name", appName);
        c.addEqualTo("name", portletName);

        PortletDefinition def = (PortletDefinition) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        if (def != null && def.getApplication() == null)
        {
            final String msg = "getPortletDefinitionByIdentifier() returned a PortletDefinition that has no parent PortletApplication.";
            throw new IllegalStateException(msg);
        }

        postLoad(def);
        return def;
    }

    public boolean portletApplicationExists( String name )
    {
        return getPortletApplication(name) != null;
    }
    
    public boolean portletDefinitionExists( String portletName, PortletApplication app )
    {
        return getPortletDefinitionByUniqueName(app.getName() + "::" + portletName) != null;
    }

    public void registerPortletApplication( PortletApplicationDefinition newApp ) throws RegistryException
    {
        getPersistenceBrokerTemplate().store(newApp);
    }

    public void removeApplication( PortletApplicationDefinition app ) throws RegistryException
    {
        getPersistenceBrokerTemplate().delete(app);
        
        // TODO: remove PortletPreferences and then what scope: everything (default, global and entity prefs) or only default (portlet scope)?
    }

    public void updatePortletApplication( PortletApplicationDefinition app ) throws RegistryException
    {
        getPersistenceBrokerTemplate().store(app);

    }

    private void postLoad( Object obj )
    {
        if (obj != null)
        {

            if (obj instanceof Support)
            {
                try
                {
                    ((Support) obj).postLoad(obj);
                }
                catch (Exception e)
                {
                }
            }
        }

    }

    private void postLoadColl( Collection coll )
    {

        if (coll != null && !coll.isEmpty())
        {
            Iterator itr = coll.iterator();
            Object test = itr.next();
            if (test instanceof Support)
            {
                Support testSupport = (Support) test;
                try
                {
                    testSupport.postLoad(testSupport);
                }
                catch (Exception e1)
                {

                }
                while (itr.hasNext())
                {
                    Support support = (Support) itr.next();
                    try
                    {
                        support.postLoad(support);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }

        }

    }

    public void savePortletDefinition( PortletDefinition portlet ) throws FailedToStorePortletDefinitionException
    {
        try
        {
            getPersistenceBrokerTemplate().store(portlet);
            ((PortletDefinition)portlet).storeChildren();
        }
        catch (DataAccessException e)
        {
            
           throw new FailedToStorePortletDefinitionException(portlet, e);
        }
    }

    public void notifyElementAdded(JetspeedCache cache, boolean local, Object key, Object element)
    {
    }

    public void notifyElementChanged(JetspeedCache cache, boolean local, Object key, Object element)
    {
    }

    public void notifyElementEvicted(JetspeedCache cache, boolean local, Object key, Object element)
    {
        //notifyElementRemoved(cache,local,key,element);
    }

    public void notifyElementExpired(JetspeedCache cache, boolean local, Object key, Object element)
    {
        //notifyElementRemoved(cache,local,key,element);           
    }

    public void notifyElementRemoved(JetspeedCache cache, boolean local, Object key, Object element)
    {    
       
        if (cache == this.portletNameCache)
        {
            //System.out.println("%%% portlet remote removed " + key);            
            RegistryPortletCache.cacheRemoveQuiet((String)key, (RegistryCacheObjectWrapper)element);
            PortletDefinition pd = this.getPortletDefinitionByUniqueName((String)key);
            if (listeners != null)
            {
                for (int ix=0; ix < listeners.size(); ix++)
                {
                    RegistryEventListener listener = (RegistryEventListener)listeners.get(ix);
                    listener.portletRemoved(pd);
                }        
            }           
        }
        else
        {
            //System.out.println("%%% PA remote removed " + key);
            RegistryApplicationCache.cacheRemoveQuiet((String) key, (RegistryCacheObjectWrapper)element);
            PortletApplication pa = this.getPortletApplication((String)key);
            if (listeners != null)
            {
                for (int ix=0; ix < listeners.size(); ix++)
                {
                    RegistryEventListener listener = (RegistryEventListener)listeners.get(ix);
                    listener.applicationRemoved(pa);
                }        
            }
            
        }
    }
        
    public void addRegistryListener(RegistryEventListener listener)
    {
        this.listeners.add(listener);
    }

    public void removeRegistryEventListener(RegistryEventListener listener)
    {
        this.listeners.remove(listener);
    }
    
}