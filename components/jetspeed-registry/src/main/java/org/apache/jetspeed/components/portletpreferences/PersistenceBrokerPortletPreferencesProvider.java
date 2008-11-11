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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.preference.impl.PreferenceImpl;
import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;
import org.apache.jetspeed.om.preference.impl.PreferenceValueImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * PersistenceBrokerPortletPreferencesProvider
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PersistenceBrokerPortletPreferencesProvider extends PersistenceBrokerDaoSupport implements
        PortletPreferencesProvider
{
    private static final Long UNDEFINED_ENTITY_OID = new Long(-1);
    private static final String UNDEFINED_USER_NAME = "<no-user>";
    
    private JetspeedCache preferenceCache;
    private List<String> preloadedApplications = null;
    private boolean preloadEntities = false;    
    
    public PersistenceBrokerPortletPreferencesProvider()
            throws ClassNotFoundException
    {
    }

    public PersistenceBrokerPortletPreferencesProvider(JetspeedCache preferenceCache)
            throws ClassNotFoundException
    {
        this.preferenceCache = preferenceCache;
    }

    public PersistenceBrokerPortletPreferencesProvider(JetspeedCache preferenceCache, List<String> apps, boolean preloadEntities)
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
    
    private String getPreferenceSetKey(String applicationName, String portletName, Long entityOid, String userName)
    {
        return applicationName + ":" + portletName + ":" + entityOid.toString() + ":" + userName;        
    }

    public PreferenceSetComposite getPreferenceSet(PortletDefinitionComposite pd)
    {
        return getPreferenceSet(((MutablePortletApplication)pd.getPortletApplicationDefinition()).getName(), pd.getName(), null, null, false);
    }

    public PreferenceSetComposite getPreferenceSet(MutablePortletEntity pe)
    {
        return getPreferenceSet(pe, null);
    }

    public PreferenceSetComposite getPreferenceSet(MutablePortletEntity pe, String userName)
    {
        PortletDefinitionComposite pd = (PortletDefinitionComposite)pe.getPortletDefinition();
        return getPreferenceSet(((MutablePortletApplication)pd.getPortletApplicationDefinition()).getName(), pd.getName(), pe.getOid(), userName, false);
    }

    private PreferenceSetImpl getPreferenceSet(String applicationName, String portletName, Long entityOid, String userName, boolean forUpdate)
    {
        if (entityOid == null)
        {
            entityOid = UNDEFINED_ENTITY_OID;
            userName = UNDEFINED_USER_NAME;
        }
        else if (userName == null)
        {
            userName = UNDEFINED_USER_NAME;
        }
        
        String cacheKey = forUpdate ? null : getPreferenceSetKey(applicationName, portletName, entityOid, userName);
        PreferenceSetImpl prefs = null;
        CacheElement cachedElement = forUpdate ? null : preferenceCache.get(cacheKey);        
        if (cachedElement != null)
        {
            prefs = (PreferenceSetImpl)cachedElement.getContent();
        }
        else
        {
            prefs = new PreferenceSetImpl();
            
            Criteria c = new Criteria();
            c.addEqualTo("applicationName", applicationName);
            c.addEqualTo("portletName", portletName);
            QueryByCriteria query = QueryFactory.newQuery(PreferenceImpl.class, c);

            HashMap<Long, String> prefsMap = new HashMap<Long, String>();
            Iterator<PreferenceImpl> prefsIter = getPersistenceBrokerTemplate().getIteratorByQuery(query);
            while (prefsIter.hasNext())
            {
                PreferenceImpl pref = prefsIter.next();
                prefsMap.put(new Long(pref.getId()), pref.getName());
            }
            
            c = new Criteria();
            c.addEqualTo("preference.applicationName", applicationName);
            c.addEqualTo("preference.portletName", portletName);
            c.addEqualTo("entityOid", entityOid);
            c.addEqualTo("userName", userName);
            
            query = QueryFactory.newQuery(PreferenceValueImpl.class, c);
            
            query.addOrderByAscending("prefId");
            query.addOrderByAscending("index");
            
            Iterator<PreferenceValueImpl> prefsValueIter = getPersistenceBrokerTemplate().getIteratorByQuery(query);
            
            Long prefId = null;
            PreferenceComposite preference = null;
            while (prefsValueIter.hasNext())
            {
                PreferenceValueImpl value = prefsValueIter.next();
                if (prefId == null || prefId.longValue() != value.getPrefId())
                {
                    prefId = new Long(value.getPrefId());
                    preference = prefs.add(value.getPrefId(), prefsMap.get(prefId), null);
                    preference.setReadOnly(Boolean.toString(value.isReadOnly()));
                }
                if (preference != null)
                {
                    preference.addValue(value.getValue());
                }
            }
            if (forUpdate)
            {
                for (Map.Entry<Long,String> entry : prefsMap.entrySet())
                {
                    if (prefs.get(entry.getValue()) == null)
                    {
                        // ensure preferences without *any* value are still loaded
                        prefs.add(entry.getKey().longValue(), entry.getValue(),null);
                    }
                }
            }
            else
            {
                preferenceCache.put(preferenceCache.createElement(cacheKey, prefs));
            }
        }
        return new PreferenceSetImpl(prefs);
    }
    
    public Collection<String> getUserNames(MutablePortletEntity pe)
    {
        Collection<String> userNames = new LinkedList<String>();
        
        Criteria c = new Criteria();
        c.addEqualTo("entityId", pe.getId());
        final ReportQueryByCriteria q = QueryFactory.newReportQuery(PreferenceValueImpl.class, c, true);
        q.setAttributes(new String[]{"userName"});

        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        
        while (iterator.hasNext())
        {
            userNames.add((String)iterator.next()[0]);
        }
        
        return userNames;
    }
    
    public void savePreferenceSet(PortletDefinitionComposite pd, PreferenceSetComposite preferenceSet)
    {
        savePreferenceSet(((MutablePortletApplication)pd.getPortletApplicationDefinition()).getName(), pd.getName(), null, null, null, preferenceSet);
    }

    public void savePreferenceSet(MutablePortletEntity pe, PreferenceSetComposite preferenceSet)
    {
        savePreferenceSet(pe, null, preferenceSet);
    }
    
    public void savePreferenceSet(MutablePortletEntity pe, String userName, PreferenceSetComposite preferenceSet)
    {
        PortletDefinitionComposite pd = (PortletDefinitionComposite)pe.getPortletDefinition();
        savePreferenceSet(((MutablePortletApplication)pd.getPortletApplicationDefinition()).getName(), pd.getName(), pe.getOid(), pe.getId().toString(), userName, preferenceSet);
    }
    
    private void savePreferenceSet(String applicationName, String portletName, Long entityOid, String entityId, String userName, PreferenceSetComposite preferenceSet)
    {
        if (entityOid == null)
        {
            // TODO: *** what Oid is provided for "on the fly" portletWindows without a page fragment/entityId??? ***
            //       need to check if that doesn't end up potentially modifying the portlet defaults or otherwise wreck havoc ...
            entityOid = UNDEFINED_ENTITY_OID;
            userName = UNDEFINED_USER_NAME;
        }
        else if (userName == null)
        {
            userName = UNDEFINED_USER_NAME;
        }
        PreferenceSetImpl current = getPreferenceSet(applicationName, portletName, entityOid, userName, true);

        Criteria c;
        PreferenceSetImpl.PreferenceImpl currentPref;
        PreferenceImpl prefImpl;
        PreferenceValueImpl valueImpl;
        
        // check and synchronize provided PreferenceSet with persistent state
        
        Iterator<PreferenceComposite> prefSetIter = preferenceSet.iterator();
        while (prefSetIter.hasNext())
        {
            PreferenceComposite pref = prefSetIter.next();
            currentPref = current.remove(pref.getName());
            boolean saveValues = false;
            long prefId = -1;
            if (currentPref != null && !pref.equals(currentPref))
            {
                c = new Criteria();
                c.addEqualTo("prefId", new Long(currentPref.getId()));
                c.addEqualTo("entityOid", entityOid);
                c.addEqualTo("userName", userName);
                getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PreferenceValueImpl.class, c));
                prefId = currentPref.getId();
                saveValues = true;
            }
            if (currentPref == null)
            {
                prefImpl = new PreferenceImpl();
                prefImpl.setApplicationName(applicationName);
                prefImpl.setPortletName(portletName);
                prefImpl.setName(pref.getName());
                getPersistenceBrokerTemplate().store(prefImpl);
                saveValues = true;
                prefId = prefImpl.getId();
            }
            if (saveValues)
            {
                Iterator<String> valuesIter = pref.getValues();
                short index = 0;
                while (valuesIter.hasNext())
                {
                    valueImpl = new PreferenceValueImpl();
                    valueImpl.setPrefId(prefId);
                    valueImpl.setIndex(index);
                    valueImpl.setEntityOid(entityOid);
                    valueImpl.setEntityId(entityId);
                    valueImpl.setReadOnly(pref.isReadOnly());
                    valueImpl.setUserName(userName);
                    valueImpl.setValue(valuesIter.next());
                    getPersistenceBrokerTemplate().store(valueImpl);
                    index++;
                }
                if (index == 0 && pref.isReadOnly())
                {
                    // special case: to be able to store the readOnly state without values
                    // one empty value entry is needed
                    valueImpl = new PreferenceValueImpl();
                    valueImpl.setPrefId(prefId);
                    valueImpl.setIndex(index);
                    valueImpl.setEntityOid(entityOid);
                    valueImpl.setEntityId(entityId);
                    valueImpl.setReadOnly(true);
                    valueImpl.setUserName(userName);
                    getPersistenceBrokerTemplate().store(valueImpl);
                }
            }
        }
        
        for (String name : current.getNames())
        {
            currentPref = current.get(name);
            c = new Criteria();
            c.addEqualTo("prefId", new Long(currentPref.getId()));
            c.addEqualTo("entityOid", entityOid);
            c.addEqualTo("userName", userName);
            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PreferenceValueImpl.class, c));

// Commenting out for now, This model will not be valid in new Pluto 2.0 preferences solutions            
//            c = new Criteria();
//            c.addEqualTo("prefId", new Long(currentPref.getId()));
//            Criteria c2 = new Criteria();
//            c2.addEqualTo("id", new Long(currentPref.getId()));
//            c2.addNotExists(QueryFactory.newQuery(PreferenceValueImpl.class, c));
//            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PreferenceImpl.class, c2));
        }
        String cacheKey = getPreferenceSetKey(applicationName, portletName, entityOid, userName);
        preferenceCache.remove(cacheKey);
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

    public void preloadApplicationPreferences(String portletApplicationName)
    {
    }
    
    public void preloadAllEntities()
    {
    }
}