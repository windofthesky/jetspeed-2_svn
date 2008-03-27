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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.preference.impl.PrefsPreference;
import org.apache.jetspeed.om.preference.impl.PrefsPreferenceSetImpl;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.serializer.objects.JSApplication;
import org.apache.jetspeed.serializer.objects.JSApplications;
import org.apache.jetspeed.serializer.objects.JSEntities;
import org.apache.jetspeed.serializer.objects.JSEntity;
import org.apache.jetspeed.serializer.objects.JSEntityPreference;
import org.apache.jetspeed.serializer.objects.JSEntityPreferences;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPortlet;
import org.apache.jetspeed.serializer.objects.JSPortlets;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;

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
    protected SearchEngine searchEngine;

    
    /**
     * @param registry
     * @param entityAccess
     * @param searchEngine
     */
    public JetspeedRegistrySerializer(PortletRegistry registry, PortletEntityAccessComponent entityAccess,
            SearchEngine searchEngine)
    {
        this.registry = registry;
        this.entityAccess = entityAccess;
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
            MutablePortletApplication portletApp = registry.getPortletApplication(app.getName());
            if (portletApp != null)
            {
                importPA(app, portletApp, settings, log);
            }
        }
    }

    void importPA(JSApplication app, MutablePortletApplication pa, Map settings, Log log) throws SerializerException
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
            MutablePortletEntity portletEntity = entityAccess.getPortletEntity(entity.getId());
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

    private void importEntityPref(JSEntity entity, MutablePortletEntity portletEntity, Map settings, Log log)
    {
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PREFERENCES) && isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES))
        {
            // do I carry any preferences?
            JSEntityPreferences preferences = entity.getEntityPreferences();
            if ((preferences == null) || (preferences.size() == 0))
                return;

            // since I do have preferences let us make sure we have a root node

            String rootForEntity = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + portletEntity.getId();
            try
            {
                Preferences.userRoot().node(rootForEntity); // will create it if it
                // doesn't exist

                Iterator it = preferences.iterator();
                while (it.hasNext())
                {
                    JSEntityPreference preference = (JSEntityPreference) it.next();

                    // do we have preferences for this one?
                    importPreferenceNode(preference, portletEntity, settings, log);
                }

            }
            catch (Exception e)
            {
                log.error(e);
                return;
            }
        }
    }

    private void importPreferenceNode(JSEntityPreference preference, MutablePortletEntity entity, Map settings, Log log)
    {

        String child = preference.getName();

        String prefNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + entity.getId() + "/" + child + "/"
                + PrefsPreference.PORTLET_PREFERENCES_ROOT;
        Preferences prefNode = Preferences.userRoot().node(prefNodePath);

        if (prefNode == null)
            return;

        JSNVPElements prefList = preference.getPreferences();
        try
        {
            PrefsPreferenceSetImpl preferenceSet = new PrefsPreferenceSetImpl(prefNode);

            Iterator it = prefList.getMyMap().keySet().iterator();

            while (it.hasNext())
            {
                String key = (String) it.next();
                String value = (String) prefList.getMyMap().get(key);
                Preference p = preferenceSet.get(key);
                if ((p == null) || isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING))
                {

                    Vector v = new Vector();
                    v.add(value);
                    preferenceSet.add(key, v);
                    log.debug("Entity " + entity.getId() + " updated with preference " + key + "=" + value);
                }
            }
            preferenceSet.flush();
            return;
        }
        catch (Exception e)
        {
            log.error(e);
            return;

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
                MutablePortletApplication pa = (MutablePortletApplication) list.next();
                // PortletApplicationDefinition pa =
                // (PortletApplicationDefinition)list.next();
                snapshot.getApplications().add(exportPA(pa, settings, log));
            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "PortletApplicationDefinition", e.getMessage() }));
            }
        }
    }

    private JSApplication exportPA(MutablePortletApplication pa, Map settings, Log log) throws SerializerException
    {

        JSApplication app = new JSApplication();
        log.debug("--processed PA " + pa.getName() + " with id=" + pa.getId());
        app.setID(pa.getId().toString());
        app.setName(pa.getName());
        /**
         * while more PAs for each portletDef
         * list:entityMan:getPortletEntity(pd)
         */
        PortletDefinitionList portletList = pa.getPortletDefinitionList(); // .get(JetspeedObjectID.createFromString(TEST_PORTLET));
        Iterator pi = portletList.iterator();
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
                    log.debug("--processed PA " + pa.getName() + " with pd=" + pd.getName());
                    portlets.add(p);
                }
                else
                    log.debug("--processed PA " + pa.getName() + " with NULL pd=" + pd.getName());

            }
            catch (Exception e)
            {
                throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                        "PortletDefinition", e.getMessage() }));
            }
        }
        app.setPortlets(portlets);
        return app;
    }

    private JSPortlet exportPD(PortletDefinition pd, Map settings, Log log) throws SerializerException
    {

        try
        {
            Collection col = entityAccess.getPortletEntities(pd);
            if ((col == null) || (col.size() == 0))
                return null;
            JSPortlet portlet = new JSPortlet();
            portlet.setName(pd.getName());
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
                MutablePortletEntity entity = (MutablePortletEntity) list.next();
                JSEntity jsEntity = exportEntityPref(entity, settings, log);
                if (jsEntity != null)
                    entities.add(jsEntity);

            }
            log.debug("-----processedAnyEntities for PD=" + pd.getName());
            portlet.setEntities(entities);
            return portlet;

        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create(new String[] {
                    "Entity", e.getMessage() }));
        }
    }

    JSEntity exportEntityPref(MutablePortletEntity entity, Map settings, Log log)
    {
        JSEntity jsEntity = null;
        
        if (isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_PREFERENCES) && isSettingSet(settings, JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES))
        {
            String rootForEntity = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + entity.getId();
            try
            {
                if (Preferences.userRoot().nodeExists(rootForEntity))
                {
                    Preferences prefNode = Preferences.userRoot().node(rootForEntity);
                    String[] children = prefNode.childrenNames();
                    if ((children != null) && (children.length > 0))
                    {
                        jsEntity = new JSEntity();
                        jsEntity.setId(entity.getId().toString());
                        JSEntityPreferences permissions = new JSEntityPreferences();

                        for (int i = 0; i < children.length; i++)
                        {
                            JSEntityPreference permission = exportPreferenceNode(entity, children[i], settings, log);
                            if (permission != null)
                                permissions.add(permission);
                        }
                        log.debug("processed preferences for entity=" + entity.getId());
                        jsEntity.setEntityPreferences(permissions);
                    }
                }
            }
            catch (Exception e)
            {
                log.error(e);
                jsEntity = null;
            }
        }
        return jsEntity;
    }

    JSEntityPreference exportPreferenceNode(MutablePortletEntity entity, String child, Map settings, Log log)
    {
        String prefNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + entity.getId() + "/" + child + "/"
                + PrefsPreference.PORTLET_PREFERENCES_ROOT;
        Preferences prefNode = Preferences.userRoot().node(prefNodePath);

        if (prefNode == null)
            return null;
        JSEntityPreference preference = new JSEntityPreference();
        preference.setName(child);

        try
        {
            PrefsPreferenceSetImpl preferenceSet = new PrefsPreferenceSetImpl(prefNode);
            if (preferenceSet.size() == 0)
                return null;
            Iterator it = preferenceSet.iterator();
            JSNVPElements v = new JSNVPElements();

            while (it.hasNext())
            {
                Preference pref = (Preference) it.next();
                String name = pref.getName();
                Iterator ii = pref.getValues();
                while (ii.hasNext())
                {
                    Object o = ii.next();
                    v.add(name, o.toString());
                }
            }
            if (v.size() > 0)
            {
                preference.setPreferences(v);
                return preference;
            }
            return null;
        }
        catch (Exception e)
        {
            log.error(e);
            return null;
        }
    }
}
