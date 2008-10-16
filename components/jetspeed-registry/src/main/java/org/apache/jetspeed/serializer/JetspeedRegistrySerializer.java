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
package org.apache.jetspeed.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.serializer.objects.JSApplication;
import org.apache.jetspeed.serializer.objects.JSApplications;
import org.apache.jetspeed.serializer.objects.JSEntities;
import org.apache.jetspeed.serializer.objects.JSEntity;
import org.apache.jetspeed.serializer.objects.JSEntityPreference;
import org.apache.jetspeed.serializer.objects.JSEntityPreferences;
import org.apache.jetspeed.serializer.objects.JSNVPElement;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPortlet;
import org.apache.jetspeed.serializer.objects.JSPortlets;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.pluto.om.portlet.Preference;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * JetspeedRegistrySerializer - Registry component serializer
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedRegistrySerializer extends AbstractJetspeedComponentSerializer
{
    protected PortletEntityAccessComponent entityAccess;

    protected PortletRegistry registry;
    protected PortletPreferencesProvider prefsProvider;
    protected SearchEngine searchEngine;

    
    /**
     * @param registry
     * @param entityAccess
     * @param searchEngine
     */
    public JetspeedRegistrySerializer(PortletRegistry registry, PortletEntityAccessComponent entityAccess, PortletPreferencesProvider prefsProvider,
            SearchEngine searchEngine)
    {
        this.registry = registry;
        this.entityAccess = entityAccess;
        this.prefsProvider = prefsProvider;
        this.searchEngine = searchEngine;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processExport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processExport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_ENTITIES))
        {
            log.info("collecting applications and entities");
            exportEntities(snapshot, settings, log);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedComponentSerializer#processImport(org.apache.jetspeed.serializer.objects.JSSnapshot,
     *      java.util.Map, org.apache.commons.logging.Log)
     */
    protected void processImport(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_ENTITIES))
        {
            log.info("creating entities");
            importEntities(snapshot, settings, log);
        }
    }

    protected void deleteData(Map settings, Log log) throws SerializerException
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_ENTITIES))
        {
            log.info("deleting applications and entities");
            try
            {
                Iterator _it = registry.getPortletApplications().iterator();
                while (_it.hasNext())
                {
                    PortletApplication pa = (PortletApplication)_it.next();
                    Collection portlets = pa.getPortletDefinitions();
                    
                    if (searchEngine != null)
                    {
                        searchEngine.remove(pa);
                        searchEngine.remove(portlets);
                    }
                    Iterator _pdIter = portlets.iterator();
                    while ( _pdIter.hasNext() )
                    {
                        entityAccess.removePortletEntities((PortletDefinition)_pdIter.next());
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

    private void importEntities(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
    {
        JSApplications applications = snapshot.getApplications();

        if (applications == null)
        {
            System.out.println("NO DATA!!!!!!");
            return;
        }
        Iterator it = applications.iterator();
        while (it.hasNext())
        {
            JSApplication app = (JSApplication) it.next();
            PortletApplication portletApp = registry.getPortletApplication(app.getName());
            if (portletApp != null)
            {
                importPA(app, portletApp, settings, log);
            }
        }
    }

    void importPA(JSApplication app, PortletApplication pa, Map settings, Log log) throws SerializerException
    {

        System.out.println("--processed PA " + pa.getName() + " with id=" + pa.getId());
        /**
         * while more PAs for each portletDef
         * list:entityMan:getPortletEntity(pd)
         */

        Iterator pi = app.getPortlets().iterator();
        while (pi.hasNext())
        {
            JSPortlet portlet = (JSPortlet) pi.next();
            PortletDefinition pd = pa.getPortletDefinitionByName(portlet.getName());
            if (pd != null)
            {
                importPD(portlet, pd, settings, log);
            }
        }
    }

    private void importPD(JSPortlet portlet, PortletDefinition pd, Map settings, Log log) throws SerializerException
    {

        JSEntities entities = portlet.getEntities();
        Iterator it = entities.iterator();
        while (it.hasNext())
        {
            JSEntity entity = (JSEntity) it.next();
            PortletEntity portletEntity = entityAccess.getPortletEntity(entity.getId());
            if (portletEntity == null)
            {
                portletEntity = entityAccess.newPortletEntityInstance(pd, entity.getId());
                try
                {
                    entityAccess.storePortletEntity(portletEntity);
                }
                catch (Exception e)
                {
                    log.error(e);
                }
            }
            // check preferences

            importEntityPref(entity, portletEntity, settings, log);
        }
    }

    private void importEntityPref(JSEntity entity, PortletEntity portletEntity, Map settings, Log log)
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES))
        {
            // do I carry any preferences?
            JSEntityPreferences preferences = entity.getEntityPreferences();
            if ((preferences == null) || (preferences.size() == 0))
                return;

            try
            {

                Iterator it = preferences.iterator();
                while (it.hasNext())
                {
                    JSEntityPreference preference = (JSEntityPreference) it.next();
                    
                    String userName = preference.getName();
                    PreferenceSetComposite preferenceSet = prefsProvider.getPreferenceSet(portletEntity, userName);
                    for (Object name : preferenceSet.getNames())
                    {
                        preferenceSet.remove((String)name);
                    }
                    String name = null;
                    ArrayList<String> values = null;
                    for ( JSNVPElement element : preference.getPreferences().getValues() )
                    {
                        if (!element.getKey().equals(name))
                        {
                            if (name != null)
                            {
                                preferenceSet.add(name, values);
                            }
                            values = new ArrayList<String>();
                        }
                        values.add(element.getValue());
                    }
                    if (name != null)
                    {
                        preferenceSet.add(name, values);
                    }
                    prefsProvider.savePreferenceSet(portletEntity, userName, preferenceSet);
                }
            }
            catch (Exception e)
            {
                log.error(e);
                return;
            }
        }
    }

    private void exportEntities(JSSnapshot snapshot, Map settings, Log log) throws SerializerException
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
            throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] { "registry",
                    e.getMessage() }));
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
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "PortletApplicationDefinition", e.getMessage() }));
            }
        }
    }

    private JSApplication exportPA(PortletApplication pa, Map settings, Log log) throws SerializerException
    {
        /**
         * while more PAs for each portletDef
         * list:entityMan:getPortletEntity(pd)
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
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "PortletDefinition", e.getMessage() }));
            }
        }
        if (!portlets.isEmpty())
        {
            JSApplication app = new JSApplication();
            log.debug("--exporting PA " + pa.getName() + " with id=" + pa.getId());
            app.setID(pa.getId().toString());
            app.setName(pa.getName());
            app.setPortlets(portlets);
            return app;
        }
        return null;
    }

    private JSPortlet exportPD(PortletDefinition pd, Map settings, Log log) throws SerializerException
    {
        try
        {
            Collection col = entityAccess.getPortletEntities(pd);
            if ((col == null) || (col.size() == 0))
                return null;
            Iterator list = null;
            try
            {
                list = col.iterator();
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.GET_EXISTING_OBJECTS.create(new String[] {
                        "entityAccess", e.getMessage() }));
            }
            JSEntities entities = new JSEntities();

            while (list.hasNext())
            {
                PortletEntity entity = (PortletEntity) list.next();
                JSEntity jsEntity = exportEntityPref(entity, settings, log);
                if (jsEntity != null)
                    entities.add(jsEntity);

            }
            if (!entities.isEmpty())
            {
                JSPortlet portlet = new JSPortlet();
                portlet.setName(pd.getPortletName());
                log.debug("-----exporting for PD=" + pd.getPortletName());
                portlet.setEntities(entities);
                return portlet;
            }
            return null;
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                    "Entity", e.getMessage() }));
        }
    }

    JSEntity exportEntityPref(PortletEntity entity, Map settings, Log log)
    {
        JSEntity jsEntity = null;
        
        jsEntity = new JSEntity();
        jsEntity.setId(entity.getId().toString());
        
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES))
        {
            JSEntityPreferences entityPreferences = new JSEntityPreferences();
            Iterator<String> userNames = prefsProvider.getUserNames(entity);
            while (userNames.hasNext())
            {
                String userName = userNames.next();
                PreferenceSetComposite preferenceSet = prefsProvider.getPreferenceSet(entity, userNames.next());
                JSEntityPreference userPreference = new JSEntityPreference();
                userPreference.setName(userName);
                Iterator<Preference> preferences = preferenceSet.iterator();
                JSNVPElements v = new JSNVPElements();
                while (preferences.hasNext())
                {
                    Preference p = preferences.next();
                    Iterator<String> values = p.getValues();
                    while (values.hasNext())
                    {
                        JSNVPElement element = new JSNVPElement();
                        element.setKey(p.getName());
                        element.setValue(values.next());
                        v.add(element);
                    }
                }
                if (v.size() > 0)
                {
                    userPreference.setPreferences(v);
                    entityPreferences.add(userPreference);
                }
                if (!entityPreferences.isEmpty())
                {
                    log.debug("processed preferences for entity=" + entity.getId());
                    jsEntity.setEntityPreferences(entityPreferences);
                }
            }
        }
        return jsEntity;
    }
}
