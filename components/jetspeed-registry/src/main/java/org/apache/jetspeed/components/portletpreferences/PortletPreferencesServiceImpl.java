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

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PreferencesValidator;
import javax.portlet.ValidatorException;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.internal.InternalPortletPreference;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.spi.optional.PortletPreferencesService;
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
        implements PortletPreferencesProvider // TODO: 2.2 extend this interface
{
    protected static final String DISCRIMINATOR_PORTLET = "portlet";
    protected static final String DISCRIMINATOR_ENTITY = "entity";
    protected static final String DISCRIMINATOR_USER = "user";
    protected static final String KEY_SEPARATOR = ":";
    
    /**
     * Cache elements are stored as element type JetspeedPreferencesMap
     */
    private JetspeedCache preferenceCache;
    private List<String> preloadedApplications = null;
    private boolean preloadEntities = false;    

    public PortletPreferencesServiceImpl(JetspeedCache preferenceCache)
            throws ClassNotFoundException
    {
        this.preferenceCache = preferenceCache;
    }
    
    public PortletPreferencesServiceImpl(JetspeedCache preferenceCache, List<String> apps, boolean preloadEntities)
    throws ClassNotFoundException
    {
        this(preferenceCache);
        this.preloadedApplications = apps;
        this.preloadEntities = preloadEntities;
    }
    
    public void destroy()
    {
        preferenceCache = null;
        preloadedApplications = null;
        preloadEntities = false;
    }
    
    public Map<String, InternalPortletPreference> getDefaultPreferences(
            PortletWindow window, PortletRequest request)
            throws PortletContainerException
    {
        String appName = window.getPortletEntity().getPortletDefinition().getApplication().getName();
        String portletName = window.getPortletEntity().getPortletDefinition().getPortletName();
        
        // // TODO: 2.2 - Ate, is this your intention, to always go to the DB and avoid cache in a process action?
        // I am removing this check as I don't think default preferences can ever be "for Update"
        // boolean forUpdate = (request instanceof ActionRequest); 
        String cacheKey = getPorletPreferenceKey(appName, portletName);
        // first search in cache, but only if we are not in update mode        
        CacheElement cachedElement = preferenceCache.get(cacheKey);        
        if (cachedElement != null)
        {
            JetspeedPreferencesMap map = (JetspeedPreferencesMap)cachedElement.getContent();
            return map;
        }            
        // TODO: 2.2 this api also supports getting default preferences via "entity" preferences
        // if we were to look up first with a dtype of "entity" and add the entityId to the query,
        // then if not found, fallback to dtype of "portlet"
        // c.addEqualTo("entityId", window.getId()); // TODO: 2.2 need an API to get the entity id from the entity object
        
        // not found in cache, lookup in database
        // TODO: 2.2 I want to avoid storing all this on the map to conserve memory (DISCRIMATOR_PORTLET, appName, portletName), 
        // maybe we can get fields from cache key or from calling params
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
            map.put(preference.getName(), value);
        }
        preferenceCache.put(preferenceCache.createElement(cacheKey, map));
        return map;
    }

    public Map<String, InternalPortletPreference> getStoredPreferences(
            PortletWindow window, PortletRequest request)
            throws PortletContainerException
    {
        String appName = window.getPortletEntity().getPortletDefinition().getApplication().getName();
        String portletName = window.getPortletEntity().getPortletDefinition().getPortletName();
        String entityId = window.getId().getStringId(); // TODO: 2.2 - FIXME: think we need to add entity.getId()
        String userName = request.getRemoteUser();
        if (userName == null)
        {
            userName = "guest"; // TODO: 2.2 might not wanna do this, might wanna throw exception
        }
        boolean forUpdate = (request instanceof ActionRequest); // TODO: 2.2 - Ate, is this your intention, to always go to the DB and avoid cache in a process action?
        String cacheKey = getUserPreferenceKey(appName, portletName, entityId, userName);
        // first search in cache, but only if we are not in update mode        
        if (!forUpdate)
        {
            CacheElement cachedElement = preferenceCache.get(cacheKey);        
            if (cachedElement != null)
            {
                JetspeedPreferencesMap map = (JetspeedPreferencesMap)cachedElement.getContent();
                return map;
            }            
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
            map.put(preference.getName(), value);
        }
        preferenceCache.put(preferenceCache.createElement(cacheKey, map));
        return map;        
    }

    public void store(PortletWindow window, PortletRequest request,
            Map<String, InternalPortletPreference> map)
            throws PortletContainerException
    {
        String appName = window.getPortletEntity().getPortletDefinition().getApplication().getName();
        String portletName = window.getPortletEntity().getPortletDefinition().getPortletName();
        String entityId = window.getId().getStringId(); // TODO: 2.2 - FIXME: think we need to add entity.getId()
        String userName = request.getRemoteUser();
        if (userName == null)
        {
            userName = "guest"; // TODO: 2.2 might not wanna do this, might wanna throw exception
        }
        // Merge: going through all this to keep down prefs memory footprint
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
        List<InternalPortletPreference> inserts = new LinkedList<InternalPortletPreference>();        
        Iterator<DatabasePreference> preferences = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        while (preferences.hasNext())
        {
            DatabasePreference preference = preferences.next();
            InternalPortletPreference found = map.get(preference.getName());
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
        for (InternalPortletPreference preference : map.values())
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
        for (InternalPortletPreference preference : inserts)
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
                index++;
            }
            getPersistenceBrokerTemplate().store(dbPref);
        }
        for (DatabasePreference dbPref : updates)
        {
            dbPref.getPreferenceValues().clear();
            InternalPortletPreference preference = map.get(dbPref.getName());
            short index = 0;
            for (String value : preference.getValues())
            {
                DatabasePreferenceValue dbValue = new DatabasePreferenceValue();
                dbValue.setIndex(index);
                dbValue.setValue(value);
                index++;
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
        // TODO: 2.2 go get the preferences validator
        return null;
    }

    private String getPorletPreferenceKey(String applicationName, String portletName)
    {
        return DISCRIMINATOR_PORTLET + KEY_SEPARATOR + applicationName + KEY_SEPARATOR + portletName;        
    }

    private String getUserPreferenceKey(String applicationName, String portletName, String entityId, String userName)
    {
        return DISCRIMINATOR_USER + KEY_SEPARATOR + applicationName + KEY_SEPARATOR + portletName + KEY_SEPARATOR + entityId + KEY_SEPARATOR + userName;        
    }
    
    public void preloadApplicationPreferences(String portletApplicationName)
    {
        // TODO: 2.2 implement
    }
    
    public void preloadAllEntities()
    {
        // TODO: 2.2 implement        
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
    
}
