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
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.PreferencesValidator;
import javax.portlet.ValidatorException;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
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
    
    public void destroy()
    {
        preferenceCache = null;
        preloadedApplications = null;
        preloadEntities = false;
    }
    
    public Map<String, PortletPreference> getDefaultPreferences(
            org.apache.pluto.container.PortletWindow pw, PortletRequest request)
            throws PortletContainerException
    {
        PortletWindow window = (PortletWindow)pw;
        org.apache.jetspeed.om.portlet.PortletDefinition pd = window.getPortletEntity().getPortletDefinition();
        String entityId = window.getPortletEntity().getId();
        Map<String, PortletPreference> defaultsMap = this.getDefaultPreferences(pd);
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
                List<FragmentPreference> fragmentPrefs = window.getPortletEntity().getFragment().getPreferences();
                if (fragmentPrefs.size() > 0)
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
                        JetspeedPreferenceImpl preference = new JetspeedPreferenceImpl(fragmentPref.getName(), entityValues);
                        preference.setReadOnly(fragmentPref.isReadOnly());                    
                        entityMap.put(fragmentPref.getName(), preference);                    
                    }
                }
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

    public Map<String, PortletPreference> getStoredPreferences(
            org.apache.pluto.container.PortletWindow pw, PortletRequest request)
            throws PortletContainerException
    {
        PortletWindow window = (PortletWindow)pw;
        if (request.getPortletMode().equals(JetspeedActions.EDIT_DEFAULTS_MODE))
        {
            return retrieveEntityPreferences(window, request);
        }
        String appName = window.getPortletEntity().getPortletDefinition().getApplication().getName();
        String portletName = window.getPortletEntity().getPortletDefinition().getPortletName();
        String entityId = window.getPortletEntity().getId();
        String userName = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (userName == null)
        {
            RequestContext rc = (RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            userName = SubjectHelper.getPrincipal(rc.getSubject(), User.class).getName();
        }
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
            JetspeedPreferenceImpl value = new JetspeedPreferenceImpl(preference.getName(), preference.getValues());
            value.setReadOnly(preference.isReadOnly());
            map.put(preference.getName(), value);
        }
        preferenceCache.put(preferenceCache.createElement(cacheKey, map));
        return map;        
    }

    public void store(org.apache.pluto.container.PortletWindow pw, PortletRequest request,
            Map<String, PortletPreference> map)
            throws PortletContainerException
    {
        PortletWindow window = (PortletWindow)pw;
        if (request.getPortletMode().equals(JetspeedActions.EDIT_DEFAULTS_MODE))
        {
            storeEntityPreferences(window, request, map);
            return;
        }        
        String appName = window.getPortletEntity().getPortletDefinition().getApplication().getName();
        String portletName = window.getPortletEntity().getPortletDefinition().getPortletName();
        String entityId = window.getPortletEntity().getId();
        String userName = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (userName == null)
        {
            RequestContext rc = (RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            userName = SubjectHelper.getPrincipal(rc.getSubject(), User.class).getName();
        }
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

    public PreferencesValidator getPreferencesValidator(PortletDefinition pd)
            throws ValidatorException
    {
        return portletFactory.getPreferencesValidator((org.apache.jetspeed.om.portlet.PortletDefinition)pd);
    }

    private String getPorletPreferenceKey(String applicationName, String portletName)
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
                String defaultsCacheKey = getPorletPreferenceKey(portletApplicationName, preference.getPortletName());                
                preferenceCache.put(preferenceCache.createElement(defaultsCacheKey, map));
                previousPortletName = preference.getPortletName();
            }
            JetspeedPreferenceImpl value = new JetspeedPreferenceImpl(preference.getName(), preference.getValues());
            value.setReadOnly(preference.isReadOnly());
            map.put(preference.getName(), value);
        }
    }
    
    public void preloadAllEntities()
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
            JetspeedPreferenceImpl value = new JetspeedPreferenceImpl(preference.getName(), preference.getValues());
            value.setReadOnly(preference.isReadOnly());
            map.put(preference.getName(), value);
        }
    }

    public void init() throws Exception
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
            preloadAllEntities();
        }
    }    

    public void storeDefaults(org.apache.jetspeed.om.portlet.PortletApplication app)
    {
        for (org.apache.jetspeed.om.portlet.PortletDefinition pd : app.getPortlets())
        {
            storeDefaults(pd);
        }
    }
    
    public void storeDefaults(org.apache.jetspeed.om.portlet.PortletDefinition pd)
    {
        Preferences preferences = pd.getDescriptorPreferences();
        String defaultsCacheKey = getPorletPreferenceKey(pd.getApplication().getName(), pd.getPortletName());            
        JetspeedPreferencesMap map = new JetspeedPreferencesMap(); 
        for (Preference preference : preferences.getPortletPreferences())
        {
            DatabasePreference dbPref = new DatabasePreference();
            dbPref.setDtype(DISCRIMINATOR_PORTLET);
            dbPref.setApplicationName(pd.getApplication().getName());
            dbPref.setPortletName(pd.getPortletName());
            dbPref.setEntityId(EMPTY_VALUE);
            dbPref.setUserName(EMPTY_VALUE);
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
            JetspeedPreferenceImpl cached = new JetspeedPreferenceImpl(dbPref.getName(), dbPref.getValues());
            cached.setReadOnly(dbPref.isReadOnly());
            map.put(preference.getName(), cached);
            getPersistenceBrokerTemplate().store(dbPref);
        }
        preferenceCache.put(preferenceCache.createElement(defaultsCacheKey, map));                    
    }

    public  Map<String, PortletPreference>  retrieveEntityPreferences(PortletWindow window, PortletRequest request)
    {
        // TODO: 2.2 implement - need to better look at use cases for edit defaults mode
        // we are currently not storing entity preferences in the database. 
        throw new UnsupportedOperationException();
    }
    
    public void storeEntityPreferences(PortletWindow pw, PortletRequest request,
            Map<String, PortletPreference> map)
            throws PortletContainerException
    {
        // TODO: 2.2 implement - need to better look at use cases for edit defaults mode
        // we are currently not storing entity preferences in the database. 
        throw new UnsupportedOperationException();
    }


    public void removeDefaults(org.apache.jetspeed.om.portlet.PortletDefinition pd)
    {
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", pd.getApplication().getName());
        c.addEqualTo("portletName", pd.getPortletName());                
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        getPersistenceBrokerTemplate().deleteByQuery(query);
        String defaultsCacheKey = getPorletPreferenceKey(pd.getApplication().getName(), pd.getPortletName());            
        preferenceCache.remove(defaultsCacheKey);
    }


    public void removeDefaults(PortletApplication app)
    {
        Criteria c = new Criteria();
        c.addEqualTo("dtype", DISCRIMINATOR_PORTLET);
        c.addEqualTo("applicationName", app.getName());
        QueryByCriteria query = QueryFactory.newQuery(DatabasePreference.class, c);
        getPersistenceBrokerTemplate().deleteByQuery(query);
        for (PortletDefinition pd : app.getPortlets())
        {
            String defaultsCacheKey = getPorletPreferenceKey(pd.getApplication().getName(), pd.getPortletName());            
            preferenceCache.remove(defaultsCacheKey);            
        }
    }

    public Map<String, PortletPreference> getDefaultPreferences(org.apache.jetspeed.om.portlet.PortletDefinition pd)
    {
        String appName = pd.getApplication().getName();
        String portletName = pd.getPortletName();        
        String defaultsCacheKey = getPorletPreferenceKey(appName, portletName);
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
                JetspeedPreferenceImpl value = new JetspeedPreferenceImpl(preference.getName(), preference.getValues());
                value.setReadOnly(preference.isReadOnly());
                map.put(preference.getName(), value);
            }
            preferenceCache.put(preferenceCache.createElement(defaultsCacheKey, map));
            defaultsMap = map;
        }
        return defaultsMap;
    }
}
