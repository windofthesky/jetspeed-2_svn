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
package org.apache.jetspeed.components.portletpreferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.PreferencesValidator;
import javax.portlet.ValidatorException;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.pluto.container.PortletContainerException;
import org.apache.pluto.container.PortletPreference;
import org.apache.pluto.container.om.portlet.PortletDefinition;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * Pluto Preferences Service. This service is designed to work with an existing
 * JPA service, in anticipation of deprecating OJB for JPA
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletPreferencesServiceImpl extends PersistenceBrokerDaoSupport
        implements PortletPreferencesProvider 
{
    protected static final String DISCRIMINATOR_PORTLET = "portlet";
    protected static final String DISCRIMINATOR_ENTITY = "entity";
    protected static final String DISCRIMINATOR_USER = "user";
    protected static final String KEY_SEPARATOR = ":";
    protected static final String EMPTY_VALUE = "_";
    
    private PortletFactory portletFactory;

    /**
     * Cache elements are stored as element type JetspeedPreferencesMap
     */
    private JetspeedCache preferenceCache;
    private List<String> preloadedApplications = null;
    private boolean preloadEntities = false;
    private boolean useEntityPreferences = true;

    
    public boolean isUseEntityPreferences()
    {
        return useEntityPreferences;
    }

    
    public void setUseEntityPreferences(boolean useEntityPreferences)
    {
        this.useEntityPreferences = useEntityPreferences;
    }

    public PortletPreferencesServiceImpl(PortletFactory portletFactory, JetspeedCache preferenceCache)
    throws ClassNotFoundException
    {
        this.portletFactory = portletFactory;
        this.preferenceCache = preferenceCache;
    }
    
    public PortletPreferencesServiceImpl(PortletFactory portletFactory, JetspeedCache preferenceCache, List<String> apps, boolean preloadEntities)
    throws ClassNotFoundException
    {
        this(portletFactory, preferenceCache);
        this.preloadedApplications = apps;
        this.preloadEntities = preloadEntities;
    }
    
    public void init()
    {
    }

    public void destroy()
    {
        preferenceCache = null;
        preloadedApplications = null;
        preloadEntities = false;
    }
    
    /**
     * PLUTO: PortletPreferencesService
     */   
    public Map<String, PortletPreference> getDefaultPreferences(
            org.apache.pluto.container.PortletWindow pw, PortletRequest request)
            throws PortletContainerException
    {
        PortletWindow window = (PortletWindow)pw;
        org.apache.jetspeed.om.portlet.PortletDefinition pd = window.getPortletDefinition();
        String entityId = window.getPortletEntityId();
        Map<String, PortletPreference> defaultsMap = this.retrieveDefaultPreferences(pd);
        // retrieve entity preferences
        if (useEntityPreferences)
        {
            JetspeedPreferencesMap entityMap = null;
            String appName = pd.getApplication().getName();
            String portletName = pd.getPortletName();
            String entityCacheKey = this.getEntityPreferenceKey(appName, portletName, entityId);
            CacheElement cachedEntity = preferenceCache.get(entityCacheKey);        
            if (cachedEntity != null)
            {
                entityMap = (JetspeedPreferencesMap)cachedEntity.getContent();
            }            
            else
            {
                entityMap = (JetspeedPreferencesMap)retrieveEntityPreferences(window);                
            }
            // merge default with entity preferences
            if (entityMap != null && entityMap.size() > 0)
            {
                JetspeedPreferencesMap mergedMap = new JetspeedPreferencesMap(defaultsMap);                 
                for (Entry<String, PortletPreference> entry : entityMap.entrySet())
                {
                    mergedMap.put(entry.getKey(), entry.getValue());
                }
                return mergedMap;
            }
        }
        return defaultsMap;
    }

    /**
     * PLUTO: PortletPreferencesService
     */       
    public Map<String, PortletPreference> getStoredPreferences(
            org.apache.pluto.container.PortletWindow pw, PortletRequest request)
            throws PortletContainerException
    {
        PortletWindow window = (PortletWindow)pw;
        if (request.getPortletMode().equals(JetspeedActions.EDIT_DEFAULTS_MODE))
        {
            return retrieveEntityPreferences(window);
        }
        String userName = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (userName == null)
        {
            userName = SubjectHelper.getPrincipal(window.getRequestContext().getSubject(), User.class).getName();
        }
        return retrieveUserPreferences(window, userName);
    }

    /**
     * Jetspeed: PortletPreferencesProvider
     * 
     * @param appName
     * @param portletName
     * @param entityId
     * @param userName
     * @return
     * @throws PortletContainerException
     */
    public Map<String, PortletPreference> retrieveUserPreferences(PortletWindow window, String userName)
    {
        String appName = window.getPortletDefinition().getApplication().getName();
        String portletName = window.getPortletDefinition().getPortletName();
        String entityId = window.getPortletEntityId();        
        String cacheKey = getUserPreferenceKey(appName, portletName, entityId, userName);
        // first search in cache        
        CacheElement cachedElement = preferenceCache.get(cacheKey);        
        if (cachedElement != null)
        {
            JetspeedPreferencesMap map = (JetspeedPreferencesMap)cachedElement.getContent();
            return map;
        }            
        // not found in cache, lookup in database
        JetspeedPreferencesMap map = new JetspeedPreferencesMap(); 
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_USER);
        c.addEqualTo("applicationName", appName);
        c.addEqualTo("portletName", portletName);
        c.addEqualTo("entityId", entityId);
        c.addEqualTo("userName", userName);
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        while (preferences.hasNext())
        {
            DatabasePreference preference = preferences.next();            
            map.put(preference.getName(), new JetspeedPreferenceImpl(preference.getName(), preference.getValues(), preference.isReadOnly()));
        }
        preferenceCache.put(preferenceCache.createElement(cacheKey, map));
        return map;                
    }
    
    /**
     * PLUTO: PortletPreferencesService
     */       
    public void store(org.apache.pluto.container.PortletWindow pw, PortletRequest request,
            Map<String, PortletPreference> map)
            throws PortletContainerException
    {
        PortletWindow window = (PortletWindow)pw;
        if (request.getPortletMode().equals(JetspeedActions.EDIT_DEFAULTS_MODE))
        {
            try
            {
                storeEntityPreferences(map, window);
            }
            catch (PreferencesException e)
            {
                throw new PortletContainerException(e);
            }
            return;
        }        
        String userName = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (userName == null)
        {
            userName = SubjectHelper.getPrincipal(window.getRequestContext().getSubject(), User.class).getName();
        }
        try
        {
            storeUserPreferences(map, window, userName);
        }
        catch (PreferencesException e)
        {
            throw new PortletContainerException(e);
        }
    }

    /**
     * Jetspeed: PortletPreferencesProvider
     */
    public void storeUserPreferences(Map<String, PortletPreference> map, PortletWindow window, String userName)
    throws PreferencesException
    {
        try
        {
            String appName = window.getPortletDefinition().getApplication().getName();
            String portletName = window.getPortletDefinition().getPortletName();
            String entityId = window.getPortletEntityId();        
            // always read in to get a fresh copy for merge
            Criteria c = new Criteria();
            c.addEqualTo("dtype", DISCRIMINATOR_USER);
            c.addEqualTo("applicationName", appName);
            c.addEqualTo("portletName", portletName);
            c.addEqualTo("entityId", entityId);
            c.addEqualTo("userName", userName);
            QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
            Map<String, DatabasePreference> mergeMap = new HashMap<String, DatabasePreference>();
            List<DatabasePreference> deletes = new LinkedList<DatabasePreference>();
            List<DatabasePreference> updates = new LinkedList<DatabasePreference>();
            List<PortletPreference> inserts = new LinkedList<PortletPreference>();        
            Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
            while (preferences.hasNext())
            {
                DatabasePreference preference = preferences.next();
                PortletPreference found = map.get(preference.getName());
                if (found == null)
                {
                    deletes.add(preference);
                }
                else
                {
                    if (isModified(preference, found))                    
                        updates.add(preference);
                }
                mergeMap.put(preference.getName(), preference); 
                
            }
            for (PortletPreference preference : map.values())
            {
                DatabasePreference dbPref = mergeMap.get(preference.getName());
                if (dbPref == null)
                {
                    inserts.add(preference);
                }                
            }
            // perform database manipulations
            for (DatabasePreference dbPref : deletes)
            {
                getPersistenceBrokerTemplate().delete(dbPref);
            }
            for (PortletPreference preference : inserts)
            {
                DatabasePreference dbPref = new DatabasePreference();
                dbPref.setDtype(DISCRIMINATOR_USER);
                dbPref.setApplicationName(appName);
                dbPref.setPortletName(portletName);
                dbPref.setEntityId(entityId);
                dbPref.setUserName(userName);
                dbPref.setName(preference.getName());
                dbPref.setReadOnly(preference.isReadOnly());
                short index = 0;
                for (String value : preference.getValues())
                {
                    DatabasePreferenceValue dbValue = new DatabasePreferenceValue();
                    dbValue.setIndex(index);
                    dbValue.setValue(value);
                    dbPref.getPreferenceValues().add(dbValue);                
                    index++;
                }
                getPersistenceBrokerTemplate().store(dbPref);
            }
            for (DatabasePreference dbPref : updates)
            {
                dbPref.getPreferenceValues().clear();
                PortletPreference preference = map.get(dbPref.getName());
                short index = 0;
                for (String value : preference.getValues())
                {
                    DatabasePreferenceValue dbValue = new DatabasePreferenceValue();
                    dbValue.setIndex(index);
                    dbValue.setValue(value);
                    index++;
                    dbPref.getPreferenceValues().add(dbValue);
                }            
                getPersistenceBrokerTemplate().store(dbPref);            
            }        
            // remove from cache to send distributed notification
            String cacheKey = getUserPreferenceKey(appName, portletName, entityId, userName);
            preferenceCache.remove(cacheKey);
        }
        catch (Throwable t)
        {
            throw new PreferencesException(t);
        }
    }

    /**
     * PLUTO: PortletPreferencesService
     */       
    public PreferencesValidator getPreferencesValidator(PortletDefinition pd)
            throws ValidatorException
    {
        return portletFactory.getPreferencesValidator((org.apache.jetspeed.om.portlet.PortletDefinition)pd);
    }

    private String getPortletPreferenceKey(String applicationName, String portletName)
    {
        return DISCRIMINATOR_PORTLET + KEY_SEPARATOR + applicationName + KEY_SEPARATOR + portletName;        
    }

    private String getEntityPreferenceKey(String applicationName, String portletName, String entityId)
    {
        return DISCRIMINATOR_PORTLET + KEY_SEPARATOR + applicationName + KEY_SEPARATOR + portletName + KEY_SEPARATOR + entityId;        
    }
    
    private String getUserPreferenceKey(String applicationName, String portletName, String entityId, String userName)
    {
        return DISCRIMINATOR_USER + KEY_SEPARATOR + applicationName + KEY_SEPARATOR + portletName + KEY_SEPARATOR + entityId + KEY_SEPARATOR + userName;        
    }
    
    /**
     * Jetspeed: PortletPreferencesProvider
     */
    public void preloadApplicationPreferences(String portletApplicationName)
    {
        JetspeedPreferencesMap map = new JetspeedPreferencesMap();
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", portletApplicationName);
        
        String previousPortletName = "";
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        query.addOrderByAscending("dtype");
        query.addOrderByAscending("applicationName");
        query.addOrderByAscending("portletName");        
        Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        while (preferences.hasNext())
        {
            DatabasePreference preference = preferences.next();
            if (preference.getPortletName().equals(previousPortletName))
            {
                map = new JetspeedPreferencesMap();
                String defaultsCacheKey = getPortletPreferenceKey(portletApplicationName, preference.getPortletName());                
                preferenceCache.put(preferenceCache.createElement(defaultsCacheKey, map));
                previousPortletName = preference.getPortletName();
            }
            map.put(preference.getName(), new JetspeedPreferenceImpl(preference.getName(), preference.getValues(), preference.isReadOnly()));
        }
    }
    
    /**
     * Jetspeed: PortletPreferencesProvider
     */    
    public void preloadUserPreferences()
    {
        JetspeedPreferencesMap map = new JetspeedPreferencesMap();
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_USER);        
        String previousKey = "";
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        query.addOrderByAscending("dtype");
        query.addOrderByAscending("applicationName");
        query.addOrderByAscending("portletName");        
        query.addOrderByAscending("entityId");        
        query.addOrderByAscending("userName");                        
        Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        while (preferences.hasNext())
        {
            DatabasePreference preference = preferences.next();
            String cacheKey = getUserPreferenceKey(preference.getApplicationName(), preference.getPortletName(), preference.getEntityId(), preference.getUserName());                            
            if (!cacheKey.equals(previousKey))
            {
                map = new JetspeedPreferencesMap();
                preferenceCache.put(preferenceCache.createElement(cacheKey, map));
                previousKey = cacheKey;
            }
            map.put(preference.getName(), new JetspeedPreferenceImpl(preference.getName(), preference.getValues(), preference.isReadOnly()));
        }
    }

    public void preload() throws Exception
    {
        if (preloadedApplications != null)
        {
            Iterator<String> apps = this.preloadedApplications.iterator();
            while (apps.hasNext())
            {
                String appName = (String)apps.next();
                preloadApplicationPreferences(appName);
            }
        }
        if (preloadEntities)
        {
            preloadUserPreferences();
        }
    }    

    /**
     * Jetspeed: PortletPreferencesProvider
     */    
    public void storeDefaults(org.apache.jetspeed.om.portlet.PortletApplication app)
    {
        for (org.apache.jetspeed.om.portlet.PortletDefinition pd : app.getPortlets())
        {
            storeDefaults(pd, (Preferences)null);
        }
    }
    
    /**
     * Jetspeed: PortletPreferencesProvider
     */    
    public void storeDefaults(org.apache.jetspeed.om.portlet.PortletDefinition pd, Preferences newprefs)
    {
        Preferences preferences = (newprefs == null) ? pd.getDescriptorPreferences() : newprefs; 
        JetspeedPreferencesMap map = new JetspeedPreferencesMap();
        for (Preference preference : preferences.getPortletPreferences())
        {
            map.put(preference.getName(), new JetspeedPreferenceImpl(preference.getName(), preference.getValues().toArray(new String[preference.getValues().size()]), preference.isReadOnly()));
        }
        this.storePortletPreference(pd, null, null, map);
    }

    public void storeDefaults(org.apache.jetspeed.om.portlet.PortletDefinition pd, Preference preference)
    {
        String appName = pd.getApplication().getName();
        String portletName = pd.getPortletName();
        String preferenceName = preference.getName();
        
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", appName);
        c.addEqualTo("portletName", portletName);
        c.addEqualTo("entityId", EMPTY_VALUE);
        c.addEqualTo("userName", EMPTY_VALUE);
        c.addEqualTo("name", preferenceName);
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        DatabasePreference dbPref = (DatabasePreference) getPersistenceBrokerTemplate().getObjectByQuery(query);
        
        if (dbPref == null)
        {
            dbPref = new DatabasePreference();
            dbPref.setDtype(DISCRIMINATOR_PORTLET);
            dbPref.setApplicationName(appName);
            dbPref.setPortletName(portletName);
            dbPref.setEntityId(EMPTY_VALUE);
            dbPref.setUserName(EMPTY_VALUE);
            dbPref.setName(preferenceName);
        }
        
        dbPref.setReadOnly(preference.isReadOnly());

        dbPref.getPreferenceValues().clear();
        short index = 0;
        for (String value : preference.getValues())
        {
            DatabasePreferenceValue dbValue = new DatabasePreferenceValue();
            dbValue.setIndex(index);
            dbValue.setValue(value);
            dbPref.getPreferenceValues().add(dbValue);
            index++;
        }

        getPersistenceBrokerTemplate().store(dbPref);

        String defaultsCacheKey = getPortletPreferenceKey(appName, portletName);
        CacheElement cacheElement = preferenceCache.get(defaultsCacheKey);
        JetspeedPreferencesMap map = (cacheElement != null ? (JetspeedPreferencesMap) cacheElement.getContent() : new JetspeedPreferencesMap());
        map.put(preferenceName, new JetspeedPreferenceImpl(preferenceName, dbPref.getValues(), dbPref.isReadOnly()));
        preferenceCache.put(preferenceCache.createElement(defaultsCacheKey, map));
    }
    
    /**
     * Jetspeed: PortletPreferencesProvider
     */        
    public Map<String, PortletPreference> retrieveEntityPreferences(PortletWindow window)
    {
        JetspeedPreferencesMap entityMap = new JetspeedPreferencesMap();
        List<FragmentPreference> fragmentPrefs = window.getFragment().getPreferences();
        if (fragmentPrefs != null && fragmentPrefs.size() > 0)
        {
            entityMap = new JetspeedPreferencesMap();                 
            for (FragmentPreference fragmentPref : fragmentPrefs)
            {                  
                String[] entityValues = new String[fragmentPref.getValueList().size()];
                int ix = 0;
                for (Object value : fragmentPref.getValueList())
                {
                    entityValues[ix] = (String)value;
                    ix++;
                }
                entityMap.put(fragmentPref.getName(), new JetspeedPreferenceImpl(fragmentPref.getName(), entityValues, fragmentPref.isReadOnly()));                    
            }
        }
        return entityMap;
    }
    
    /**
     * Jetspeed: PortletPreferencesProvider
     */        
    @SuppressWarnings("unchecked")
    public void storeEntityPreferences(Map<String, PortletPreference> map, PortletWindow window)
            throws PreferencesException
    {
        for (Entry<String, PortletPreference> entry : map.entrySet())
        {
            org.apache.jetspeed.om.portlet.PortletDefinition pd = window.getPortletDefinition();
            String entityId = window.getPortletEntityId();            
            String appName = pd.getApplication().getName();
            String portletName = pd.getPortletName();
            String entityCacheKey = this.getEntityPreferenceKey(appName, portletName, entityId);
            preferenceCache.remove(entityCacheKey);
        }
        try
        {
            ContentFragment fragment = window.getFragment();
            fragment.updatePreferences(map);
        }
        catch (Exception e)
        {
            throw new PreferencesException(e);
        }
    }

    /**
     * Jetspeed: PortletPreferencesProvider
     */    
    public void removeDefaults(org.apache.jetspeed.om.portlet.PortletDefinition pd)
    {
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", pd.getApplication().getName());
        c.addEqualTo("portletName", pd.getPortletName());                
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        getPersistenceBrokerTemplate().deleteByQuery(query);
        String defaultsCacheKey = getPortletPreferenceKey(pd.getApplication().getName(), pd.getPortletName());            
        preferenceCache.remove(defaultsCacheKey);
    }

    public void removeDefaults(org.apache.jetspeed.om.portlet.PortletDefinition pd, String preferenceName)
    {
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", pd.getApplication().getName());
        c.addEqualTo("portletName", pd.getPortletName());
        c.addEqualTo("name", preferenceName);
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        getPersistenceBrokerTemplate().deleteByQuery(query);
        
        String defaultsCacheKey = getPortletPreferenceKey(pd.getApplication().getName(), pd.getPortletName());
        JetspeedPreferencesMap map = (JetspeedPreferencesMap) preferenceCache.get(defaultsCacheKey).getContent();
        map.remove(preferenceName);
        preferenceCache.put(preferenceCache.createElement(defaultsCacheKey, map));
    }
    
    /**
     * Jetspeed: PortletPreferencesProvider
     */    
    public void removeDefaults(PortletApplication app)
    {
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", app.getName());
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        getPersistenceBrokerTemplate().deleteByQuery(query);
        for (PortletDefinition pd : app.getPortlets())
        {
            String defaultsCacheKey = getPortletPreferenceKey(pd.getApplication().getName(), pd.getPortletName());            
            preferenceCache.remove(defaultsCacheKey);            
        }
    }

    /**
     * Jetspeed: PortletPreferencesProvider
     */        
    public Map<String, PortletPreference> retrieveDefaultPreferences(org.apache.jetspeed.om.portlet.PortletDefinition pd)
    {
        String appName = pd.getApplication().getName();
        String portletName = pd.getPortletName();        
        String defaultsCacheKey = getPortletPreferenceKey(appName, portletName);
        JetspeedPreferencesMap defaultsMap;         
        // first search in cache        
        CacheElement cachedDefaults = preferenceCache.get(defaultsCacheKey);
        if (cachedDefaults != null)
        {
            defaultsMap = (JetspeedPreferencesMap)cachedDefaults.getContent();
        }            
        else
        {
            // not found in cache, lookup in database
            JetspeedPreferencesMap map = new JetspeedPreferencesMap(); 
            Criteria c = new Criteria();
            c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
            c.addEqualTo("applicationName", appName);
            c.addEqualTo("portletName", portletName);                
            QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
            Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
            while (preferences.hasNext())
            {
                DatabasePreference preference = preferences.next();
                map.put(preference.getName(), new JetspeedPreferenceImpl(preference.getName(), preference.getValues(), preference.isReadOnly()));
            }
            preferenceCache.put(preferenceCache.createElement(defaultsCacheKey, map));
            defaultsMap = map;
        }
        return defaultsMap;
    }
    
    public Set<String> getPortletWindowIds(org.apache.jetspeed.om.portlet.PortletDefinition portletdefinition)
    {
        Set<String> windowsId = new TreeSet<String>();
        String appName = portletdefinition.getApplication().getName();
        String portletName = portletdefinition.getPortletName();
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_USER);
        c.addEqualTo("applicationName", appName);
        c.addEqualTo("portletName", portletName);
        c.addNotEqualTo("entityId", EMPTY_VALUE);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(DatabasePreference.class, c);
        query.setAttributes(new String[] { "entityId", "id" });
        Iterator<Object[]> ObjectwindowsId = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (ObjectwindowsId.hasNext())
        {
            windowsId.add((String) ObjectwindowsId.next()[0]);
        }
        return windowsId;
    }

    public Map<String, PortletPreference> getUserPreferences(org.apache.jetspeed.om.portlet.PortletDefinition portletdefinition, String windowId,
                                                             String userName)
    {
        JetspeedPreferencesMap userPreferences = new JetspeedPreferencesMap();
        String appName = portletdefinition.getApplication().getName();
        String portletName = portletdefinition.getPortletName();
        
        String userCacheKey = getUserPreferenceKey(appName, portletName,windowId, userName);
        CacheElement cachedDefaults = preferenceCache.get(userCacheKey);
        if (cachedDefaults != null)
        {
            userPreferences = (JetspeedPreferencesMap) cachedDefaults.getContent();
        }
        else
        {
            Criteria c = new Criteria();
            c.addEqualTo("dtype", DISCRIMINATOR_USER);
            c.addEqualTo("applicationName", appName);
            c.addEqualTo("portletName", portletName);
            c.addEqualTo("userName", userName);
            c.addEqualTo("entityId", windowId);
            QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
            Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
            while (preferences.hasNext())
            {
                DatabasePreference preference = preferences.next();
                userPreferences.put(preference.getName(), new JetspeedPreferenceImpl(preference.getName(), preference.getValues(), preference.isReadOnly()));
            }
            preferenceCache.put(preferenceCache.createElement(userCacheKey, userPreferences));
        }
        return userPreferences;
    }

    public Set<String> getUserNames(org.apache.jetspeed.om.portlet.PortletDefinition portletdefinition, String windowId)
    {
        Set<String> userNames = new TreeSet<String>();
        String appName = portletdefinition.getApplication().getName();
        String portletName = portletdefinition.getPortletName();
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_USER);
        c.addEqualTo("applicationName", appName);
        c.addEqualTo("portletName", portletName);
        c.addEqualTo("entityId", windowId);
        c.addNotEqualTo("userName", EMPTY_VALUE);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(DatabasePreference.class, c);
        query.setAttributes(new String[] { "userName", "id" });
        query.setDistinct(true);
        Iterator<Object[]> userObjects = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (userObjects.hasNext())
        {
            userNames.add((String) userObjects.next()[0]);
        }
        return userNames;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider#storePortletPreference(org.apache.jetspeed.om.portlet.PortletDefinition, java.lang.String, java.lang.String, java.util.Map)
     */
    public void storePortletPreference(org.apache.jetspeed.om.portlet.PortletDefinition portletdefinition, String windowId, String userName, Map<String, PortletPreference> map)
    {
        String appName = portletdefinition.getApplication().getName();
        String portletName = portletdefinition.getPortletName();
        storePortletPreference(appName, portletName, windowId, userName, map);
    }

    private boolean isModified(DatabasePreference dbPref, PortletPreference pref)
    {
        String[] dbValues = dbPref.getValues();
        String[] values = pref.getValues();
        if (dbValues == null || values == null)
            return true;
        if (dbValues.length != values.length)
            return true;
        for (int ix = 0; ix < values.length; ix++)
        {
            if (!values[ix].equals(dbValues[ix]))
                return true;
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider#storePortletPreference(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map)
     */
    public void storePortletPreference(String appName, String portletName, String windowId, String userName, Map<String, PortletPreference> map)
    {
        // always read in to get a fresh copy for merge
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", appName);
        c.addEqualTo("portletName", portletName);         
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        Map<String, DatabasePreference> mergeMap = new HashMap<String, DatabasePreference>();
        List<DatabasePreference> deletes = new LinkedList<DatabasePreference>();
        List<DatabasePreference> updates = new LinkedList<DatabasePreference>();
        List<PortletPreference> inserts = new LinkedList<PortletPreference>();
        Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        while (preferences.hasNext())
        {
            DatabasePreference preference = preferences.next();
            PortletPreference found = map.get(preference.getName());
            if (found == null)
            {
                deletes.add(preference);
            }
            else
            {
                if (isModified(preference, found))
                    updates.add(preference);
            }
            mergeMap.put(preference.getName(), preference);
        }
        for (PortletPreference preference : map.values())
        {
            DatabasePreference dbPref = mergeMap.get(preference.getName());
            if (dbPref == null)
            {
                inserts.add(preference);
            }
        }
        // perform database manipulations
        for (DatabasePreference dbPref : deletes)
        {
            getPersistenceBrokerTemplate().delete(dbPref);
        }
        for (PortletPreference preference : inserts)
        {
            DatabasePreference dbPref = new DatabasePreference();
            dbPref.setDtype(DISCRIMINATOR_PORTLET);
            dbPref.setApplicationName(appName);
            dbPref.setPortletName(portletName);
            dbPref.setEntityId(windowId);
            dbPref.setUserName(userName);
            dbPref.setName(preference.getName());
            dbPref.setReadOnly(preference.isReadOnly());
            short index = 0;
            for (String value : preference.getValues())
            {
                DatabasePreferenceValue dbValue = new DatabasePreferenceValue();
                dbValue.setIndex(index);
                dbValue.setValue(value);
                dbPref.getPreferenceValues().add(dbValue);
                index++;
            }
            getPersistenceBrokerTemplate().store(dbPref);
        }
        for (DatabasePreference dbPref : updates)
        {
            dbPref.getPreferenceValues().clear();
            PortletPreference preference = map.get(dbPref.getName());
            short index = 0;
            for (String value : preference.getValues())
            {
                DatabasePreferenceValue dbValue = new DatabasePreferenceValue();
                dbValue.setIndex(index);
                dbValue.setValue(value);
                index++;
                dbPref.getPreferenceValues().add(dbValue);
            }
            getPersistenceBrokerTemplate().store(dbPref);
        }
        // remove from cache to send distributed notification
        String cacheKey = this.getPortletPreferenceKey(appName, portletName); //getUserPreferenceKey(appName, portletName, windowId, userName);
        preferenceCache.remove(cacheKey);
    }

}
