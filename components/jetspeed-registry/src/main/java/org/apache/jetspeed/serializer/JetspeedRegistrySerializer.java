/*
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
package org.apache.jetspeed.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.components.portletpreferences.JetspeedPreferenceImpl;
import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.serializer.objects.JSApplication;
import org.apache.jetspeed.serializer.objects.JSApplications;
import org.apache.jetspeed.serializer.objects.JSEntities;
import org.apache.jetspeed.serializer.objects.JSEntity;
import org.apache.jetspeed.serializer.objects.JSEntityPreference;
import org.apache.jetspeed.serializer.objects.JSEntityPreferenceCompat;
import org.apache.jetspeed.serializer.objects.JSEntityPreferences;
import org.apache.jetspeed.serializer.objects.JSNVPElement;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPortlet;
import org.apache.jetspeed.serializer.objects.JSPortlets;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.pluto.container.PortletPreference;
import org.slf4j.Logger;

/**
 * JetspeedRegistrySerializer - Registry component serializer
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedRegistrySerializer extends AbstractJetspeedComponentSerializer
{
    protected PortletRegistry registry;
    protected PortletPreferencesProvider prefsProvider;
    protected SearchEngine searchEngine;

    /**
     * @param registry
     * @param entityAccess
     * @param searchEngine
     */
    public JetspeedRegistrySerializer(PortletRegistry registry, PortletPreferencesProvider prefsProvider,
                                      SearchEngine searchEngine)
    {
        this.registry = registry;
        this.prefsProvider = prefsProvider;
        this.searchEngine = searchEngine;
    }

    /*
     * (non-Javadoc)
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processExport(org.apache.jetspeed.serializer.objects.JSSnapshot, java.util.Map,
     * org.apache.commons.logging.Log)
     */
    protected void processExport(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES))
        {
            log.info("collecting applications and entities");
            exportEntities(snapshot, settings, log);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processImport(org.apache.jetspeed.serializer.objects.JSSnapshot, java.util.Map,
     * org.apache.commons.logging.Log)
     */
    protected void processImport(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES))
        {
            log.info("creating entities");
            importEntities(snapshot, settings, log);
        }
    }

    protected void deleteData(Map<String,Object> settings, Logger log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES))
        {
            log.info("deleting applications");
            try
            {
                for (PortletApplication pa : registry.getPortletApplications())
                {
                    List<PortletDefinition> portlets = pa.getPortlets();
                    List<PortletDefinition> clones = pa.getClones();
                    if (searchEngine != null)
                    {
                        List<Object> list = new ArrayList<Object>(portlets.size() + clones.size() + 1);
                        list.add(pa);
                        list.addAll(portlets);
                        list.addAll(clones);
                        searchEngine.remove(list);
                    }
                    registry.removeApplication(pa);
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(e);
            }
        }
    }

    private void importEntities(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        JSApplications applications = snapshot.getApplications();
        if (applications == null)
        {
            return;
        }

		// import preferences for registered portlets if applications
		// have been registered; otherwise, import all preferences
		boolean importAll = registry.getPortletApplications().isEmpty();
		if (!applications.isEmpty())
		{
            Iterator it = applications.iterator();
            while (it.hasNext())
            {
                JSApplication app = (JSApplication) it.next();
                PortletApplication portletApp = registry.getPortletApplication(app.getName());
		        if ((portletApp != null) || importAll)
                {
                    importPA(app, portletApp, settings, log);
                }
            }
        }
    }

    void importPA(JSApplication app, PortletApplication pa, Map<String,Object> settings, Logger log) throws SerializerException
    {
        /**
         * while more PAs for each portletDef list:entityMan:getPortletEntity(pd)
         */
        Iterator pi = app.getPortlets().iterator();
        while (pi.hasNext())
        {
            JSPortlet portlet = (JSPortlet) pi.next();
			if (pa != null)
			{
                PortletDefinition pd = pa.getPortlet(portlet.getName());
                if (pd != null)
                {
                    importPD(app, portlet, pd, settings, log);
                }
            }
			else
			{
                importPD(app, portlet, null, settings, log);			    
			}
        }
    }

    private void importPD(JSApplication app, JSPortlet portlet, PortletDefinition pd, Map<String,Object> settings, Logger log) throws SerializerException
    {
        Iterator it = portlet.getEntities().iterator();
        while (it.hasNext())
        {
            JSEntity entity = (JSEntity) it.next();
            importEntityPref(app, portlet, entity, pd, settings, log);
        }
    }

    private void importEntityPref(JSApplication app, JSPortlet portlet, JSEntity entity, PortletDefinition pd, Map<String,Object> settings, Logger log)
    {
        JSEntityPreferences preferences = entity.getEntityPreferences();
        if ((preferences == null) || (preferences.size() == 0))
            return;

        try
        {
            Iterator it = preferences.iterator();
            while (it.hasNext())
            {
                Map<String, PortletPreference> portletPreference = new HashMap<String, PortletPreference>();
                Object preference = it.next();
                String userName = null;
                JSNVPElements preferenceElements = null;
                if (preference instanceof JSEntityPreference)
                {
                    JSEntityPreference pref = (JSEntityPreference)preference;
                    userName = pref.getPrincapalName();
                    preferenceElements = pref.getPreferences();
                }
                else if (preference instanceof JSEntityPreferenceCompat)
                {
                    JSEntityPreferenceCompat pref = (JSEntityPreferenceCompat)preference;
                    userName = pref.getName();
                    preferenceElements = pref.getPreferences();                    
                }
                for (JSNVPElement element : preferenceElements.getValues())
                {
                    if (element.getValues() == null)
                    {
                        portletPreference.put(element.getKey(), new JetspeedPreferenceImpl(element.getKey(), new String[] { element.getValue() }, element.isReadOnly()));
                    }
                    else
                    {
                        portletPreference.put(element.getKey(), new JetspeedPreferenceImpl(element.getKey(), element.getValues(), element.isReadOnly()));
                    }
                }
                if (portletPreference.size() > 0)
                {
                    if (pd != null)
                    {
                        prefsProvider.storePortletPreference(pd, entity.getId(), userName, portletPreference);
                    }
                    else
                    {
                        prefsProvider.storePortletPreference(app.getName(), portlet.getName(), entity.getId(), userName, portletPreference);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error in importing ");
            return;
        }
    }

    private void exportEntities(JSSnapshot snapshot, Map<String,Object> settings, Logger log) throws SerializerException
    {
        Collection col = registry.getPortletApplications();
        if ((col == null) || (col.size() == 0))
            return;
        Iterator list = null;
        try
        {
            list = col.iterator();
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "registry", e.getMessage() }));
        }
        while (list.hasNext())
        {
            try
            {
                PortletApplication pa = (PortletApplication) list.next();
                // PortletApplicationDefinition pa =
                // (PortletApplicationDefinition)list.next();
                JSApplication app = exportPA(pa, settings, log);
                if (app != null)
                {
                    snapshot.getApplications().add(app);
                }
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] { "PortletApplicationDefinition",
                                                                                                                       e.getMessage() }), e);
            }
        }
    }

    private JSApplication exportPA(PortletApplication pa, Map<String,Object> settings, Logger log) throws SerializerException
    {
        JSApplication jsApplication = null;
        /**
         * while more PAs for each portletDef list:entityMan:getPortletEntity(pd)
         */
        Iterator pi = pa.getPortlets().iterator();
        PortletDefinition pd = null;
        JSPortlets portlets = new JSPortlets();
        while (pi.hasNext())
        {
            try
            {
                pd = (PortletDefinition) pi.next();
                JSPortlet p = exportPD(pd, settings, log);
                if (p != null)
                {
                    log.debug("--processed PA " + pa.getName() + " with pd=" + pd.getPortletName());
                    portlets.add(p);
                }
                else
                    log.debug("--processed PA " + pa.getName() + " with NULL pd=" + pd.getPortletName());
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] { "PortletDefinition", e.getMessage() }),
                                              e);
            }
        }
        if (!portlets.isEmpty())
        {
            jsApplication = new JSApplication();
            log.debug("--exporting PA " + pa.getName());
            // jsApplication.setID(pa.getName().toString());
            jsApplication.setName(pa.getName());
            jsApplication.setPortlets(portlets);
        }
        return jsApplication;
    }

    private JSPortlet exportPD(PortletDefinition pd, Map<String,Object> settings, Logger log) throws SerializerException
    {
        JSPortlet jsPortlet = null;
        try
        {
            Set<String> windowIds = prefsProvider.getPortletWindowIds(pd);
            if ((windowIds == null) || (windowIds.size() == 0))
                return null;
            JSEntities entities = new JSEntities();
            for (String windowId : windowIds)
            {
                JSEntity jsEntity = exportEntityPref(pd, windowId, settings, log);
                if (jsEntity != null)
                    entities.add(jsEntity);
            }
            if (!entities.isEmpty())
            {
                jsPortlet = new JSPortlet();
                jsPortlet.setName(pd.getPortletName());
                jsPortlet.setEntities(entities);
                log.debug("-----exporting for PD=" + pd.getPortletName());
                // jsPortlet.setEntities(entities);
            }
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] { "Entity", e.getMessage() }), e);
        }
        return jsPortlet;
    }

    JSEntity exportEntityPref(PortletDefinition definition, String windowId, Map<String,Object> settings, Logger log)
    {
        JSEntity jsEntity = null;
        jsEntity = new JSEntity();
        jsEntity.setId(windowId);
        JSEntityPreferences entityPreferences = new JSEntityPreferences();
        Set<String> userNames = prefsProvider.getUserNames(definition, windowId);
        for (String userName : userNames)
        {
            Map<String, PortletPreference> userPreferences = prefsProvider.getUserPreferences(definition, windowId, userName);
            JSEntityPreference userPreference = new JSEntityPreference();
            userPreference.setPrincapalName(userName);
            Iterator<String> preferences = userPreferences.keySet().iterator();
            JSNVPElements v = new JSNVPElements("preference");
            while (preferences.hasNext())
            {
                String pKey = preferences.next();
                PortletPreference portletPreference = userPreferences.get(pKey);
                JSNVPElement element = new JSNVPElement();
                element.setKey(pKey);
                element.setValues(portletPreference.getValues());
                element.setReadOnly(portletPreference.isReadOnly());
                v.add(element);
            }
            if (v.size() > 0)
            {
                userPreference.setPreferences(v);
                entityPreferences.add(userPreference);
            }
            if (!entityPreferences.isEmpty())
            {
                log.debug("processed preferences for entity=" + windowId);
                jsEntity.setEntityPreferences(entityPreferences);
            }
        }
        return jsEntity;
    }
}
