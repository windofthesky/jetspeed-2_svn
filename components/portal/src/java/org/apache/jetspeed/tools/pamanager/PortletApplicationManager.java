/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.tools.pamanager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotDeletedException;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;

import org.apache.pluto.om.common.SecurityRole;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;

import java.io.IOException;

import java.util.Collection;
import java.util.Iterator;

/**
 * PortletApplicationManager
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: PortletApplicationManager.java,v 1.21 2005/04/09 00:24:44 shinsuke Exp $
 */
public class PortletApplicationManager implements PortletApplicationManagement
{
    private static final Log    log = LogFactory.getLog("deployment");
    private static final String PORTLET_XML = "WEB-INF/portlet.xml";

    protected PortletEntityAccessComponent entityAccess;
    protected PortletFactory        portletFactory;
    protected PortletRegistry       registry;
    protected PortletWindowAccessor windowAccess;
    protected SearchEngine          searchEngine;
    protected RoleManager           roleManager;
    protected boolean               autoCreateRoles;

    /**
	 * Creates a new PortletApplicationManager object.
	 */
	public PortletApplicationManager(PortletFactory portletFactory, PortletRegistry registry,
		PortletEntityAccessComponent entityAccess, PortletWindowAccessor windowAccess)
	{
		this.portletFactory     = portletFactory;
		this.registry		    = registry;
		this.entityAccess	    = entityAccess;
		this.windowAccess	    = windowAccess;
	}
    
    public void setRoleManager(RoleManager roleManager)
    {
        this.roleManager = roleManager;
    }
    
    public void setAutoCreateRoles(boolean autoCreateRoles)
    {
        this.autoCreateRoles = autoCreateRoles;
    }

	public void setSearchEngine(SearchEngine searchEngine)
	{
		this.searchEngine = searchEngine;
	}

	public void startLocalPortletApplication(String contextName, FileSystemHelper warStruct,
		ClassLoader paClassLoader)
		throws RegistryException
	{
        checkValidContextName(contextName, true);
        startPA(contextName, warStruct, paClassLoader, true);
	}

	public void startPortletApplication(String contextName, FileSystemHelper warStruct,
		ClassLoader paClassLoader)
		throws RegistryException
	{
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try
        {
            checkValidContextName(contextName, false);
            startPA(contextName, warStruct, paClassLoader, false);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
	}

	public void stopLocalPortletApplication(String contextName)
		throws RegistryException
	{
		checkValidContextName(contextName, true);
		stopPA(contextName);
	}

	public void stopPortletApplication(String contextName)
		throws RegistryException
	{
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try
        {
            checkValidContextName(contextName, false);
            stopPA(contextName);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
	}

	public void unregisterPortletApplication(String paName)
		throws RegistryException
	{
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try
        {
            MutablePortletApplication pa = null;
            
            try
            {
                pa = (MutablePortletApplication) registry.getPortletApplication(paName);
            }
            catch (Exception e)
            {
                // ignore errors during portal shutdown
            }

            if (pa != null)
            {
                if (portletFactory.getPortletApplicationClassLoader(pa) != null)
                {
                    throw new RegistryException("Portlet Application " + paName + " still running");
                }

                unregisterPortletApplication(pa, true);
            }
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
	}

	protected void checkValidContextName(String contextName, boolean local)
		throws RegistryException
	{
		int prefixLength = LOCAL_PA_PREFIX.length();

		if ((contextName.length() >= prefixLength)
			&& contextName.substring(0, prefixLength).equalsIgnoreCase(LOCAL_PA_PREFIX))
		{
			if (!local)
			{
				throw new RegistryException("Prefix \"" + LOCAL_PA_PREFIX
					+ "\" is reserved for Local Portlet Applications only.");
			}
		}
		else if (local)
		{
			throw new RegistryException("Prefix \"" + LOCAL_PA_PREFIX
				+ "\" is required for Local Portlet Applications.");
		}
	}

	protected MutablePortletApplication registerPortletApplication(PortletApplicationWar paWar,
		MutablePortletApplication oldPA, boolean local, ClassLoader paClassLoader)
		throws RegistryException
	{
		if (oldPA != null)
		{
			unregisterPortletApplication(oldPA, false);
			oldPA = null;
		}

		MutablePortletApplication pa		 = null;
		boolean					  registered = false;
		String					  paName     = paWar.getPortletApplicationName();

		try
		{
			log.info("Loading portlet.xml...." + paName);
			pa = paWar.createPortletApp(paClassLoader);

			if (local)
			{
				pa.setApplicationType(MutablePortletApplication.LOCAL);
			}
			else
			{
				pa.setApplicationType(MutablePortletApplication.WEBAPP);
			}

			// load the web.xml
			log.info("Loading web.xml...." + paName);
			MutableWebApplication wa = paWar.createWebApp();
			paWar.validate();

			if (local)
			{
				wa.setContextRoot("<portal>");
			}

			pa.setWebApplicationDefinition(wa);
            
            // Make sure existing entities are refreshed with the most
            // recent PortletDefintion.
            Collection portletDefs = pa.getPortletDefinitions();
            if(portletDefs != null && portletDefs.size() > 0)
            {
                Iterator pdItr = portletDefs.iterator();
                while(pdItr.hasNext())
                {
                    PortletDefinition pd = (PortletDefinition) pdItr.next();
                    Collection portletEntites = entityAccess.getPortletEntities(pd);
                    if(portletEntites != null && portletEntites.size() > 0)
                    {
                        Iterator peItr = portletEntites.iterator();
                        while(peItr.hasNext())
                        {
                            PortletEntityCtrl portletEntity = (PortletEntityCtrl) peItr.next();
                            portletEntity.setPortletDefinition(pd);
                        }
                    }
                }
            }
		}
		catch (Exception e)
		{
			String msg = "Failed to load portlet application for "
				+ paWar.getPortletApplicationName();
			log.error(msg);
			throw new RegistryException(msg);
		}

		// register the portlet application
		try
		{
			registry.registerPortletApplication(pa);
			registered = true;
			log.info("Registered the portlet application " + paName);

			// add to search engine result
			if (searchEngine != null)
			{
				searchEngine.add(pa);
				searchEngine.add(pa.getPortletDefinitions());
				log.info("Registered the portlet application in the search engine... " + paName);
			}
            
            if ( autoCreateRoles && roleManager != null && pa.getWebApplicationDefinition().getSecurityRoles() != null )
            {
                try
                {
                    Iterator rolesIter = pa.getWebApplicationDefinition().getSecurityRoles().iterator();
                    SecurityRole sr;
                    while ( rolesIter.hasNext() )
                    {
                        sr = (SecurityRole)rolesIter.next();
                        if ( !roleManager.roleExists(sr.getRoleName()) )
                        {
                            roleManager.addRole(sr.getRoleName());
                            log.info("AutoCreated role: "+sr.getRoleName()+" from portlet application "+paName+" its web definition");
                        }
                    }
                }
                catch (SecurityException sex)
                {
                    log.warn("Failed to autoCreate roles for portlet application " + paName+": "+sex.getMessage(), sex);
                }
            }

			return pa;
		}
		catch (Exception e)
		{
			String msg = "Failed to register portlet application, " + paName;
			log.error(msg, e);

			if (registered)
			{
				try
				{
					unregisterPortletApplication(pa, local);
				}
				catch (Exception re)
				{
					log.error("Failed to rollback registration of portlet application" + paName, re);
				}
			}

			throw new RegistryException(msg, e);
		}
	}

	protected void startPA(String contextName, FileSystemHelper warStruct,
		ClassLoader paClassLoader, boolean local)
		throws RegistryException
	{
		PortletApplicationWar paWar = null;

		try
		{
			try
			{
				paWar = new PortletApplicationWar(warStruct, contextName, "/" + contextName);

				if (paClassLoader == null)
				{
					paClassLoader = paWar.createClassloader(getClass().getClassLoader());
				}
			}
			catch (IOException e)
			{
				String msg = "Failed to create PA WAR for " + contextName;
				log.error(msg, e);
				throw new RegistryException(msg, e);
			}

			MutablePortletApplication pa = (MutablePortletApplication) registry
				.getPortletApplication(contextName);

			if ((pa != null) && (paWar.getPortletApplicationChecksum() == pa.getChecksum()))
			{
                portletFactory.unregisterPortletApplication(pa);
			}
			else
			{
				pa = registerPortletApplication(paWar, pa, local, paClassLoader);
			}
            portletFactory.registerPortletApplication(pa, paClassLoader);
		}
		finally
		{
			if (paWar != null)
			{
				try
				{
					paWar.close();
				}
				catch (IOException e)
				{
					log.error("Failed to close PA WAR for " + contextName, e);
				}
			}
		}
	}

	protected void stopPA(String contextName)
		throws RegistryException
	{
		MutablePortletApplication pa = null;
        
        try
        {
            pa = (MutablePortletApplication) registry.getPortletApplication(contextName);
        }
        catch (Exception e)
        {
            // ignore errors during portal shutdown
        }
		if (pa != null)
		{
			portletFactory.unregisterPortletApplication(pa);
		}
	}

	protected void unregisterPortletApplication(MutablePortletApplication pa,
		boolean purgeEntityInfo)
		throws RegistryException
	{
		if (searchEngine != null)
		{
			searchEngine.remove(pa);
			searchEngine.remove(pa.getPortletDefinitions());
		}

		log.info("Remove all registry entries defined for portlet application " + pa.getName());

		Iterator portlets = pa.getPortletDefinitions().iterator();

		while (portlets.hasNext())
		{
			PortletDefinition portletDefinition = (PortletDefinition) portlets.next();
			Iterator		  entities = entityAccess.getPortletEntities(portletDefinition)
													 .iterator();

			while (entities.hasNext())
			{
				PortletEntity entity = (PortletEntity) entities.next();

				if (purgeEntityInfo)
				{
					try
					{
						entityAccess.removePortletEntity(entity);
					}
					catch (PortletEntityNotDeletedException e)
					{
						String msg = "Failed to delete Portlet Entity " + entity.getId();
						log.error(msg, e);
						throw new RegistryException(msg, e);
					}
				}

				entityAccess.removeFromCache(entity);
				windowAccess.removeWindows(entity);
			}
		}

		// todo keep (User)Prefs?
		registry.removeApplication(pa);
	}
}
