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
import java.util.Vector;
import java.util.prefs.Preferences;

import javolution.xml.XMLBinding;

import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.preference.impl.PrefsPreference;
import org.apache.jetspeed.om.preference.impl.PrefsPreferenceSetImpl;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.serializer.objects.JSApplication;
import org.apache.jetspeed.serializer.objects.JSApplications;
import org.apache.jetspeed.serializer.objects.JSEntities;
import org.apache.jetspeed.serializer.objects.JSEntity;
import org.apache.jetspeed.serializer.objects.JSEntityPreference;
import org.apache.jetspeed.serializer.objects.JSEntityPreferences;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPortlet;
import org.apache.jetspeed.serializer.objects.JSPortlets;
import org.apache.jetspeed.serializer.objects.JSSecondaryData;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;

/**
 * Jetspeed Serializer - Secondary Data
 * <p>
 * The Serializer is capable of reading and writing additional content of the
 * Jetspeed environment such as entities and preferences to and from XML files.
 * The component can be used from a standalone java application for seeding a
 * new database or from a running portal as an administrative backup/restore
 * function.
 * <p>
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JetspeedSerializerSecondaryImpl extends JetspeedSerializerBase
		implements
			JetspeedSerializer
{

	boolean overwrite = true;
	int refCouter = 0;

	private PortletEntityAccessComponent entityAccess = null;

	private PortletRegistry registry;

	private PreferencesProvider prefProvider;

	protected Class getSerializerDataClass()
	{
		return JSSecondaryData.class;
	}

	protected String getSerializerDataTag()
	{
		return TAG_SECONDARYSNAPSHOT;
	}

	public JetspeedSerializerSecondaryImpl()
	{
		super();
	}

	/**
	 * hand over existing component manager
	 * 
	 * @param cm
	 */
	public JetspeedSerializerSecondaryImpl(ComponentManager cm)
	{
		super(cm);
	}

	/**
	 * This constructor takes the application root, the search path for the boot
	 * component configuration files and the search path for the application
	 * component configuration files.
	 * <p>
	 * For example: new JetspeedSerializerImpl("./", "assembly/boot/*.xml",
	 * "assembly/*.xml") will establish the current directory as the root,
	 * process all xml files in the assembly/boot directory before processing
	 * all xml files in the assembly directory itself.
	 * 
	 * @param appRoot
	 *            working directory
	 * @param bootConfig
	 *            boot (primary) file or files (wildcards are allowed)
	 * @param appConfig
	 *            application (secondary) file or files (wildcards are allowed)
	 */
	public JetspeedSerializerSecondaryImpl(String appRoot, String[] bootConfig,
			String[] appConfig) throws SerializerException
	{
		super(appRoot, bootConfig, appConfig);
	}

	/**
	 * reset instruction flags to default settings (all true)
	 * 
	 */
	protected void resetSettings()
	{
		setSetting(JetspeedSerializer.KEY_PROCESS_USERS, false);
		setSetting(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, false);
		setSetting(JetspeedSerializer.KEY_PROCESS_PROFILER, false);
		setSetting(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, true);
		setSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING, true);
		setSetting(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, true);
	}

	/**
	 * On import, get the basic SnapShot data
	 * 
	 */
	protected void getSnapshotData()
	{
		logMe("date created : "
				+ ((JSSecondaryData) getSnapshot()).getDateCreated());
		logMe("software Version : "
				+ ((JSSecondaryData) getSnapshot()).getSavedVersion());
		logMe("software SUbVersion : "
				+ ((JSSecondaryData) getSnapshot()).getSavedSubversion());
	}

	/**
	 * On export, set the basic SnapShot data
	 * 
	 */
	protected void setSnapshotData()
	{
		super.setSnapshotData();
	}

	private JSPortlet exportPD(PortletDefinition pd) throws SerializerException
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
			} catch (Exception e)
			{
				throw new SerializerException(
						SerializerException.GET_EXISTING_OBJECTS
								.create(new String[]
								{"entityAccess", e.getMessage()}));
			}
			JSEntities entities = new JSEntities();

			while (list.hasNext())
			{
				MutablePortletEntity entity = (MutablePortletEntity) list
						.next();
				JSEntity jsEntity = exportEntityPref(entity);
				if (jsEntity != null)
					entities.add(jsEntity);

			}
			System.out.println("-----processedAnyEntities for PD="
					+ pd.getName());
			portlet.setEntities(entities);
			return portlet;

		} catch (Exception e)
		{
			throw new SerializerException(
					SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
							.create(new String[]
							{"Entity", e.getMessage()}));
		}
	}

	JSEntity exportEntityPref(MutablePortletEntity entity)
	{
		JSEntity jsEntity = new JSEntity();
		jsEntity.setId(entity.getId().toString());
		String rootForEntity = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/"
				+ entity.getId();
		try
		{
			if (!(Preferences.userRoot().nodeExists(rootForEntity)))
			{
				// System.out.println("No preferences exist for entity "+
				// entity.getId());
				return jsEntity;
			}

			Preferences prefNode = Preferences.userRoot().node(rootForEntity);
			String[] children = prefNode.childrenNames();
			if ((children != null) && (children.length > 0))
			{
				JSEntityPreferences permissions = new JSEntityPreferences();

				for (int i = 0; i < children.length; i++)
				{
					JSEntityPreference permission = processPreferenceNode(
							entity, children[i]);
					if (permission != null)
						permissions.add(permission);
				}
				System.out.println("processed preferences for entity="
						+ entity.getId());
				jsEntity.setEntityPreferences(permissions);
				return jsEntity;
				// processPreferenceNode(entity,prefNode,null);
			}
			return jsEntity;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}

	JSEntityPreference processPreferenceNode(MutablePortletEntity entity,
			String child)
	{
		String prefNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/"
				+ entity.getId() + "/" + child + "/"
				+ PrefsPreference.PORTLET_PREFERENCES_ROOT;
		Preferences prefNode = Preferences.userRoot().node(prefNodePath);

		if (prefNode == null)
			return null;
		JSEntityPreference permission = new JSEntityPreference();
		permission.setName(child);

		try
		{
			PrefsPreferenceSetImpl preferenceSet = new PrefsPreferenceSetImpl(
					prefNode);
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
				permission.setPreferences(v);
				return permission;
			}
			return null;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;

		}

	}

	private JSApplication exportPA(MutablePortletApplication pa)
			throws SerializerException
	{

		JSApplication app = new JSApplication();
		System.out.println("--processed PA " + pa.getName() + " with id="
				+ pa.getId());
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
				JSPortlet p = exportPD(pd);
				if (p != null)
				{
					System.out.println("--processed PA " + pa.getName()
							+ " with pd=" + pd.getName());
					portlets.add(p);
				} else
					System.out.println("--processed PA " + pa.getName()
							+ " with NULL pd=" + pd.getName());

			} catch (Exception e)
			{
				throw new SerializerException(
						SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
								.create(new String[]
								{"PortletDefinition", e.getMessage()}));
			}
		}
		app.setPortlets(portlets);
		return app;
	}

	private JSApplications exportEntities() throws SerializerException
	{
		registry = (PortletRegistry) getCM()
				.getComponent(
						"org.apache.jetspeed.components.portletregistry.PortletRegistry");
		if (registry == null)
			throw new SerializerException(
					SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
							.create("org.apache.jetspeed.components.portletregistry.PortletRegistry"));
		Object o = getCM()
				.getComponent(
						"org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent");
		this.entityAccess = (PortletEntityAccessComponent) o;
		if (entityAccess == null)
			throw new SerializerException(
					SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
							.create("org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent"));

		JSApplications applications = new JSApplications();

		Collection col = registry.getPortletApplications();
		if ((col == null) || (col.size() == 0))
			return applications;
		Iterator list = null;
		try
		{
			list = col.iterator();
		} catch (Exception e)
		{
			throw new SerializerException(
					SerializerException.GET_EXISTING_OBJECTS
							.create(new String[]
							{"registry", e.getMessage()}));
		}
		while (list.hasNext())
		{
			try
			{
				MutablePortletApplication pa = (MutablePortletApplication) list
						.next();
				// PortletApplicationDefinition pa =
				// (PortletApplicationDefinition)list.next();
				applications.add(exportPA(pa));
			} catch (Exception e)
			{
				throw new SerializerException(
						SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
								.create(new String[]
								{"PortletApplicationDefinition", e.getMessage()}));
			}
		}

		return applications;
	}

	/**
	 * The workhorse for importing data
	 * 
	 * @param binding
	 *            established XML binding
	 * @return
	 * @throws SerializerException
	 */
	protected void processImport() throws SerializerException
	{
		this.logMe("*********reinstalling data*********");

		logMe("creating entities");
		importEntities();
	}

	/**
	 * The workhorse for exporting data
	 * 
	 * @param binding
	 *            established XML binding
	 * @return
	 * @throws SerializerException
	 */
	protected void processExport(String name, XMLBinding binding)
			throws SerializerException
	{
		this.logMe("*********collecting data*********");
		/** first create the snapshot file */

		this.setSnapshot(new JSSecondaryData(name));

		setSnapshotData();

		JSApplications apps = exportEntities();
		((JSSecondaryData) this.getSnapshot()).setApplications(apps);
		/**
		 * 
		 * if (this.getSetting(JetspeedSerializer.KEY_PROCESS_ENTITIES)) {
		 * logMe("collecting entities"); exportEntities(); } else
		 * logMe("entities skipped");
		 * 
		 * if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PREFERENCES)) {
		 * logMe("collecting preferences"); exportPreferences(); } else
		 * logMe("preferences skipped");
		 */

	}

	/**
	 * Setup the binding for the different classes, mapping each extracted class
	 * to a unique tag name in the XML
	 * 
	 * @param binding
	 */
	protected void setupAliases(XMLBinding binding)
	{
		binding.setAlias(JSApplication.class, "PortletApplication");
		binding.setAlias(JSApplications.class, "PortletApplications");
		binding.setAlias(JSPortlet.class, "Portlet");
		binding.setAlias(JSPortlets.class, "Portlets");
		binding.setAlias(JSEntity.class, "Entity");
		binding.setAlias(JSEntities.class, "Entities");
		binding.setAlias(JSEntityPreference.class, "Principal");
		binding.setAlias(JSEntityPreferences.class, "Settings");
		binding.setAlias(JSSecondaryData.class, "RegistryData");
		binding.setAlias(JSNVPElements.class, "preferences");

		binding.setAlias(String.class, "String");
		binding.setAlias(Integer.class, "int");
		binding.setClassAttribute(null);

	}

	private void importEntities() throws SerializerException
	{
		overwrite = getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING);

		registry = (PortletRegistry) getCM()
				.getComponent(
						"org.apache.jetspeed.components.portletregistry.PortletRegistry");
		if (registry == null)
			throw new SerializerException(
					SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
							.create("org.apache.jetspeed.components.portletregistry.PortletRegistry"));
		Object o = getCM()
				.getComponent(
						"org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent");
		this.entityAccess = (PortletEntityAccessComponent) o;
		if (entityAccess == null)
			throw new SerializerException(
					SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
							.create("org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent"));
		
		JSApplications applications = ((JSSecondaryData)this.getSnapshot()).getApplications();

		if (applications == null)
		{
			System.out.println("NO DATA!!!!!!");
			return;
		}
		Iterator it = applications.iterator();
		while (it.hasNext())
		{
			JSApplication app = (JSApplication)it.next();
			MutablePortletApplication portletApp = registry.getPortletApplication(app.getName());
			if (portletApp != null)
			{
				importPA(app,portletApp);
			}
		}
	}

	void importPA(JSApplication app, MutablePortletApplication pa)
	throws SerializerException
	{

		
		System.out.println("--processed PA " + pa.getName() + " with id="
		+ pa.getId());
		/**
		 * while more PAs for each portletDef
		 * list:entityMan:getPortletEntity(pd)
		 */
		
		Iterator pi = app.getPortlets().iterator();
		while (pi.hasNext())
		{
			JSPortlet portlet = (JSPortlet)pi.next();
			PortletDefinition pd  = pa.getPortletDefinitionByName(portlet.getName());
			if (pd != null)
			{
				importPD(portlet,pd); 
			}
		}
	}
	
	private void importPD(JSPortlet portlet, PortletDefinition pd) throws SerializerException
	{

		JSEntities entities = portlet.getEntities();
		Iterator it = entities.iterator();
		while (it.hasNext())
		{
			JSEntity entity = (JSEntity)it.next();
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
					e.printStackTrace();
				}
			}
			// check preferences
			
			importEntityPref(entity , portletEntity);
		}
	}

	private void importEntityPref(JSEntity entity , MutablePortletEntity portletEntity)
	{

		// do I carry any preferences?
		JSEntityPreferences preferences = entity.getEntityPreferences();
		if ((preferences == null) || (preferences.size() == 0))
			return;
		
		
		//since I do have preferences let us make sure we have a root node
		
		String rootForEntity = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/"
				+ portletEntity.getId();
		try
		{
			Preferences prefNode = Preferences.userRoot().node(rootForEntity); // will create it if it doesn't exist
			
			
			Iterator it = preferences.iterator();
			while (it.hasNext())
			{
				JSEntityPreference preference = (JSEntityPreference)it.next();
				
				// do we have preferences for this one?
				importPreferenceNode(preference,portletEntity);
			}
			
		
		} catch (Exception e)
		{
			e.printStackTrace();
			return;
		}

	}

	private void importPreferenceNode(JSEntityPreference preference, MutablePortletEntity entity)
	{

		String child = preference.getName();
		
		String prefNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/"
				+ entity.getId() + "/" + child + "/"
				+ PrefsPreference.PORTLET_PREFERENCES_ROOT;
		Preferences prefNode = Preferences.userRoot().node(prefNodePath);

		if (prefNode == null)
			return ;

		JSNVPElements prefList = preference.getPreferences();
		try
		{
			PrefsPreferenceSetImpl preferenceSet = new PrefsPreferenceSetImpl(
					prefNode);
			
			Iterator it = prefList.getMyMap().keySet().iterator();
			
			while (it.hasNext())
			{
				String key = (String)it.next();
				String value = (String)prefList.getMyMap().get(key);
				Preference p = preferenceSet.get(key);
				if ((p == null) || (overwrite))
				{
					
					Vector v = new Vector();
					v.add(value);
					preferenceSet.add(key, v);
System.out.println("Entity " + entity.getId() + " updated with preference " + key + "=" + value);					
				}
			}
			preferenceSet.flush();
			return;
		} catch (Exception e)
		{
			e.printStackTrace();
			return;

		}

	}
	
	
}
