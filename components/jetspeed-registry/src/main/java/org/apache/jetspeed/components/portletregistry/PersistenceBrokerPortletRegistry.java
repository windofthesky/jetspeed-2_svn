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
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.cache.JetspeedCacheEventListener;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
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
    private List<RegistryEventListener> listeners = new ArrayList<RegistryEventListener>();
    private PortletPreferencesProvider preferenceService;
    
    // for testing purposes only: no need for the portletFactory then
    public PersistenceBrokerPortletRegistry(String repositoryPath, PortletPreferencesProvider preferenceService)
    {
        this(repositoryPath, null, null, null, null, preferenceService);
    }
    
    /**
     *  
     */
    public PersistenceBrokerPortletRegistry(String repositoryPath,
            JetspeedCache applicationOidCache, JetspeedCache portletOidCache, 
            JetspeedCache applicationNameCache, JetspeedCache portletNameCache, PortletPreferencesProvider preferenceService)
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
        this.preferenceService = preferenceService;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<PortletDefinition> getAllPortletDefinitions()
    {
        Criteria c = new Criteria();
        Collection<PortletDefinition> list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }

    public PortletApplication getPortletApplication(String name)
    {
        return getPortletApplication(name, false);
    }
    
    public PortletApplication getPortletApplication(String name, boolean fromCache)
    {
        if (fromCache)
        {
            CacheElement cacheElement = applicationNameCache.get(name);
            if (cacheElement != null)
            {
                cacheElement = applicationOidCache.get(((RegistryCacheObjectWrapper)cacheElement.getContent()).getId());
                if (cacheElement != null)
                {
                    return (PortletApplication)cacheElement.getContent();
                }
            }
        }
        Criteria c = new Criteria();
        c.addEqualTo("name", name);
        PortletApplication app = (PortletApplication) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoad(app);
        return app;
    }

    @SuppressWarnings("unchecked")
    public Collection<PortletApplication> getPortletApplications()
    {
        Criteria c = new Criteria();
        Collection<PortletApplication> list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }
    
    public PortletDefinition getPortletDefinitionByUniqueName( String name )
    {
        return getPortletDefinitionByUniqueName(name, false);
    }

    public PortletDefinition getPortletDefinitionByUniqueName( String name, boolean fromCache )
    {
        if (fromCache)
        {
            CacheElement cacheElement = portletNameCache.get(name);
            if (cacheElement != null)
            {
                cacheElement = portletOidCache.get(((RegistryCacheObjectWrapper)cacheElement.getContent()).getId());
                if (cacheElement != null)
                {
                    return (PortletDefinition)cacheElement.getContent();
                }
            }
        }
        String appName = PortletRegistryHelper.parseAppName(name);
        String portletName = PortletRegistryHelper.parsePortletName(name);

        Criteria c = new Criteria();
        c.addEqualTo("app.name", appName);
        c.addEqualTo("portletName", portletName);
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
        return getPortletApplication(name, true) != null;
    }
    
    public boolean portletDefinitionExists( String portletName, PortletApplication app )
    {
        return getPortletDefinitionByUniqueName(app.getName() + "::" + portletName) != null;
    }

    public void registerPortletApplication(PortletApplication newApp) throws RegistryException
    {
        getPersistenceBrokerTemplate().store(newApp);
        this.preferenceService.storeDefaults(newApp);
    }

    public void removeApplication(PortletApplication app) throws RegistryException
    {
        getPersistenceBrokerTemplate().delete(app);
        this.preferenceService.removeDefaults(app);
    }

    public void updatePortletApplication(PortletApplication app) throws RegistryException
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
            portlet.storeChildren();
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
            if (listeners != null && !listeners.isEmpty())
            {
                PortletDefinition pd = this.getPortletDefinitionByUniqueName((String)key);
                for (int ix=0; ix < listeners.size(); ix++)
                {
                    RegistryEventListener listener = listeners.get(ix);
                    listener.portletRemoved(pd);
                }        
            }           
        }
        else
        {
            //System.out.println("%%% PA remote removed " + key);
            RegistryApplicationCache.cacheRemoveQuiet((String) key, (RegistryCacheObjectWrapper)element);
            if (listeners != null && !listeners.isEmpty())
            {
                PortletApplication pa = this.getPortletApplication((String)key);
                for (int ix=0; ix < listeners.size(); ix++)
                {
                    RegistryEventListener listener = listeners.get(ix);
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
 
    public void clonePortletDefinition(PortletDefinition source, String newPortletName) throws FailedToStorePortletDefinitionException
    {
        if (this.portletDefinitionExists(newPortletName, source.getApplication()))
        {
            throw new FailedToStorePortletDefinitionException("Cannot clone to portlet named " + newPortletName + ", name already exists"); 
        }
        PortletDefinitionImpl copy = new PortletDefinitionImpl();
        copy.setApplication(source.getApplication());
        copy.setPortletName(newPortletName);
        copy.setPortletClass(source.getPortletClass());
        copy.setResourceBundle(source.getResourceBundle());
        copy.setPreferenceValidatorClassname(source.getPreferenceValidatorClassname());
        copy.setExpirationCache(source.getExpirationCache());
        copy.setCacheScope(source.getCacheScope());
        // TODO: Metadata
        
        copy.setJetspeedSecurityConstraint(source.getJetspeedSecurityConstraint());
        copy.getDescriptions().addAll(source.getDescriptions());
        copy.getDisplayNames().addAll(source.getDisplayNames());
        
    }
    
    /*

    private Collection<LocalizedField> metadataFields = null;

    
    private List<InitParam> initParams;
    private List<EventDefinitionReference> supportedProcessingEvents;
    private List<EventDefinitionReference> supportedPublishingEvents;
    private List<SecurityRoleRef> securityRoleRefs;
    private List<Supports> supports;
    private List<String> supportedLocales;
    private List<Language> languages;
    private List<ContainerRuntimeOption> containerRuntimeOptions;    
    private List<String> supportedPublicRenderParameters;
    private Preferences descriptorPreferences = new PreferencesImpl();    
    
    private transient Map<Locale,InlinePortletResourceBundle> resourceBundles = new HashMap<Locale, InlinePortletResourceBundle>();
    
    protected List portletEntities;     
     */
    
}