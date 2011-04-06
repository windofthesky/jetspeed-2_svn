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
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.EventDefinitionReference;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.SecurityRoleRef;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.util.JetspeedLocale;
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
    static public final String PORTLET_UNIQUE_NAME_SEPARATOR = "::";

    protected JetspeedCache applicationOidCache = null;
    protected JetspeedCache portletOidCache = null;
    protected JetspeedCache applicationNameCache = null;
    protected JetspeedCache portletNameCache = null;
    protected List<RegistryEventListener> listeners = new ArrayList<RegistryEventListener>();
    protected PortletPreferencesProvider preferenceService;
    protected SearchEngine searchEngine;
    
    // for testing purposes only: no need for the portletFactory then
    public PersistenceBrokerPortletRegistry(String repositoryPath, PortletPreferencesProvider preferenceService)
    {
        this(repositoryPath, null, null, null, null, preferenceService, null);
    }

    public PersistenceBrokerPortletRegistry(String repositoryPath,
            JetspeedCache applicationOidCache, JetspeedCache portletOidCache,
            JetspeedCache applicationNameCache, JetspeedCache portletNameCache,
            PortletPreferencesProvider preferenceService)
    {
        this(repositoryPath, applicationOidCache, portletOidCache, applicationNameCache, portletNameCache, preferenceService, null);
    }
    
    public PersistenceBrokerPortletRegistry(String repositoryPath,
            JetspeedCache applicationOidCache, JetspeedCache portletOidCache, 
            JetspeedCache applicationNameCache, JetspeedCache portletNameCache, 
            PortletPreferencesProvider preferenceService,
            SearchEngine search)
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
        this.searchEngine = search;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<PortletDefinition> getAllPortletDefinitions()
    {
        Criteria c = new Criteria();
        c.addIsNull("cloneParent");
        Collection<PortletDefinition> list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public Collection<PortletDefinition> getAllDefinitions()
    {
        Criteria c = new Criteria();
        Collection<PortletDefinition> list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }

    @SuppressWarnings("unchecked")
    public Collection<PortletDefinition> getAllCloneDefinitions()
    {
        Criteria c = new Criteria();
        c.addNotNull("cloneParent");
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
        this.restoreClones(newApp);
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
                if (pd != null)
                {
                    for (int ix=0; ix < listeners.size(); ix++)
                    {
                        RegistryEventListener listener = listeners.get(ix);
                        listener.portletRemoved(pd);
                    }        
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
                if (pa != null)
                {
                    for (int ix=0; ix < listeners.size(); ix++)
                    {
                        RegistryEventListener listener = listeners.get(ix);
                        listener.applicationRemoved(pa);
                    }        
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
 
    public PortletDefinition clonePortletDefinition(PortletDefinition source, String newPortletName) throws FailedToStorePortletDefinitionException
    {
        if (this.portletDefinitionExists(newPortletName, source.getApplication()))
        {
            throw new FailedToStorePortletDefinitionException("Cannot clone to portlet named " + newPortletName + ", name already exists");
        }
        // create new portlet in source portlet application
        PortletDefinition copy = source.getApplication().addClone(newPortletName);
        PortletApplication destApp = source.getApplication();

        // First set display name

        DisplayName displayName = copy.addDisplayName(JetspeedLocale.getDefaultLocale().getLanguage());
        displayName.setDisplayName(newPortletName);

        // And, then, copy all attributes

        copy.setPortletClass(source.getPortletClass());
        copy.setResourceBundle(source.getResourceBundle());
        copy.setPreferenceValidatorClassname(source.getPreferenceValidatorClassname());
        copy.setExpirationCache(source.getExpirationCache());
        copy.setCacheScope(source.getCacheScope());

        for (LocalizedField field : source.getMetadata().getFields())
        {
            copy.getMetadata().addField(field.getLocale(), field.getName(), field.getValue());
        }

        copy.setJetspeedSecurityConstraint(source.getJetspeedSecurityConstraint());

        for (Description desc : source.getDescriptions())
        {
            Description copyDesc = copy.addDescription(desc.getLang());
            copyDesc.setDescription(desc.getDescription());
        }

        for (InitParam initParam : source.getInitParams())
        {
            InitParam copyInitParam = copy.addInitParam(initParam.getParamName());
            copyInitParam.setParamValue(initParam.getParamValue());

            for (Description desc : initParam.getDescriptions())
            {
                Description copyDesc = copyInitParam.addDescription(desc.getLang());
                copyDesc.setDescription(desc.getDescription());
            }
        }
        
        InitParam parentPortlet = copy.getInitParam(PortletDefinition.CLONE_PARENT_INIT_PARAM);
        
        if (parentPortlet == null)
        {
            parentPortlet = copy.addInitParam(PortletDefinition.CLONE_PARENT_INIT_PARAM);
        }
        
        parentPortlet.setParamValue(source.getPortletName());

        for (EventDefinitionReference eventDefRef : source.getSupportedProcessingEvents())
        {
            copy.addSupportedProcessingEvent(eventDefRef.getQName());
        }

        for (EventDefinitionReference eventDefRef : source.getSupportedPublishingEvents())
        {
            copy.addSupportedPublishingEvent(eventDefRef.getQName());
        }

        for (SecurityRoleRef secRoleRef : source.getSecurityRoleRefs())
        {
            SecurityRoleRef copySecRoleRef = copy.addSecurityRoleRef(secRoleRef.getRoleName());
            copySecRoleRef.setRoleLink(secRoleRef.getRoleLink());

            for (Description desc : secRoleRef.getDescriptions())
            {
                Description copyDesc = copySecRoleRef.addDescription(desc.getLang());
                copyDesc.setDescription(desc.getDescription());
            }
        }

        for (Supports supports : source.getSupports())
        {
            Supports copySupports = copy.addSupports(supports.getMimeType());

            for (String portletMode : supports.getPortletModes())
            {
                copySupports.addPortletMode(portletMode);
            }

            for (String windowState : supports.getWindowStates())
            {
                copySupports.addWindowState(windowState);
            }
        }

        for (Language language : source.getLanguages())
        {
            Language copyLanguage = copy.addLanguage(language.getLocale());
            copyLanguage.setTitle(language.getTitle());
            copyLanguage.setShortTitle(language.getShortTitle());
            copyLanguage.setKeywords(language.getKeywords());
            copyLanguage.setSupportedLocale(language.isSupportedLocale());
        }

        for (ContainerRuntimeOption runtimeOption : source.getContainerRuntimeOptions())
        {
            ContainerRuntimeOption copyRuntimeOption = copy.addContainerRuntimeOption(runtimeOption.getName());

            for (String value : runtimeOption.getValues())
            {
                copyRuntimeOption.addValue(value);
            }
        }

        copy.getSupportedPublicRenderParameters().addAll(source.getSupportedPublicRenderParameters());

        //savePortletDefinition(copy);
        try
        {
            updatePortletApplication(destApp);
        }
        catch (RegistryException e)
        {
            throw new FailedToStorePortletDefinitionException(e);
        }
        for (Preference pref : source.getPortletPreferences().getPortletPreferences())
        {
            Preference copyPref = copy.addDescriptorPreference(pref.getName());
            copyPref.setReadOnly(pref.isReadOnly());

            for (String value : pref.getValues())
            {
                copyPref.addValue(value);
            }
        }
        try
        {
            preferenceService.storeDefaults(copy, (Preferences)null);
        }
        catch (Throwable e)
        {
            destApp.getClones().remove(copy);
            throw new FailedToStorePortletDefinitionException(e);
        }
        PortletDefinition pd = getPortletDefinitionByUniqueName(PortletRegistryHelper.makeUniqueName(source.getApplication().getName(), newPortletName));
        PortletApplication pa = pd.getApplication();
        // reindex
        if (searchEngine != null)
        {
            searchEngine.remove(pa);
            searchEngine.remove(pa.getPortlets());
            searchEngine.remove(pa.getClones());
            searchEngine.add(pa);
            searchEngine.add(pa.getPortlets());
            searchEngine.add(pa.getClones());
        }
        return pd;
    }

    public int restoreClones(PortletApplication pa)
            throws RegistryException
    {
        int count = 0;
        Criteria criteria = new Criteria();
        criteria.addEqualTo("cloneParent", pa.getName());
        Collection<PortletDefinitionImpl> clones = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, criteria));
        for (PortletDefinitionImpl pd : clones)
        {
            if (pd.isClone())
            {
                if (pa.getName().equals(pd.getCloneParent()))
                {
                    // Restore Clone
                    pd.setApplication(pa);
                    pa.getClones().add(pd);
                    count++;
                }
            }
        }
        if (count > 0)
        {
            updatePortletApplication(pa);
        }
        return count;
    }

    public void removeClone(PortletDefinition clone)
            throws RegistryException
    {
        if (!clone.isClone())
        {
            throw new IllegalArgumentException("The portlet is not a cloned one: " + clone.getUniqueName());
        }
        
        PortletApplication pa = clone.getApplication();
        getPersistenceBrokerTemplate().delete(clone);
        pa.getClones().remove(clone);
        this.updatePortletApplication(pa);
    }
    
    public void removeAllClones(PortletApplication pa)
            throws RegistryException
    {
        Criteria c = new Criteria();
        c.addEqualTo("cloneParent", pa.getName());
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        pa.getClones().clear();
        this.updatePortletApplication(pa);
    }

}