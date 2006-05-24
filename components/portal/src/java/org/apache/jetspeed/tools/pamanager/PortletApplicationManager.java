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
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.MultiFileChecksumHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;
import org.apache.jetspeed.security.SecurityException;
import org.apache.pluto.om.common.SecurityRole;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;

import java.io.File;
import java.io.IOException;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * PortletApplicationManager
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: PortletApplicationManager.java,v 1.21 2005/04/09 00:24:44 shinsuke Exp $
 */
public class PortletApplicationManager implements PortletApplicationManagement
{
    private static int DEFAULT_DESCRIPTOR_CHANGE_MONITOR_INTERVAL = 10*1000; // 10 seconds
    private static final Log    log = LogFactory.getLog("deployment");

    protected PortletEntityAccessComponent entityAccess;
    protected PortletFactory        portletFactory;
    protected PortletRegistry       registry;
    protected PortletWindowAccessor windowAccess;
    protected SearchEngine          searchEngine;
    protected RoleManager           roleManager;
    protected PermissionManager     permissionManager;
    protected boolean               autoCreateRoles;
    protected List                  permissionRoles;
    protected int  descriptorChangeMonitorInterval = DEFAULT_DESCRIPTOR_CHANGE_MONITOR_INTERVAL;
    protected DescriptorChangeMonitor monitor;
    protected boolean started;

    /**
	 * Creates a new PortletApplicationManager object.
	 */
	public PortletApplicationManager(PortletFactory portletFactory, PortletRegistry registry,
		PortletEntityAccessComponent entityAccess, PortletWindowAccessor windowAccess,
        PermissionManager permissionManager, SearchEngine searchEngine,
        RoleManager roleManager, List permissionRoles)
	{
		this.portletFactory     = portletFactory;
		this.registry		    = registry;
		this.entityAccess	    = entityAccess;
		this.windowAccess	    = windowAccess;
        this.permissionManager  = permissionManager;
        this.searchEngine       = searchEngine;
        this.roleManager        = roleManager;        
        this.permissionRoles    = permissionRoles;
	}
    
    public void start()
    {
        if ( descriptorChangeMonitorInterval > 0 )
        {
            try
            {
                monitor = new DescriptorChangeMonitor(Thread.currentThread().getThreadGroup(),
                                                "PortletApplicationManager Descriptor Change Monitor Thread", this, descriptorChangeMonitorInterval);

                monitor.setContextClassLoader(getClass().getClassLoader());
                monitor.start();
                log.info("PortletApplicationManager Descriptor Change Monitor started!");
            }
            catch (Exception e)
            {
                log.warn("Unable to start PortletApplicationManager Descriptor Change Monitor: "+ e.toString(), e);
                monitor.safeStop();
                monitor = null;
            }
        }
        started = true;
    }
    
    public void stop()
    {
        started = false;
        if (monitor != null)
        {
            monitor.safeStop();
            monitor = null;
        }
    }
    
    public boolean isStarted()
    {
        return started;
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
    
    private void checkStarted()
    {
        if (!started)
        {
            throw new IllegalStateException("Not started yet");
        }
    }

	public void startLocalPortletApplication(String contextName, FileSystemHelper warStruct,
		ClassLoader paClassLoader)
		throws RegistryException
	{
        checkStarted();
        checkValidContextName(contextName, true);
        startPA(contextName, warStruct, paClassLoader, true);
	}

	public void startPortletApplication(String contextName, FileSystemHelper warStruct,
		ClassLoader paClassLoader)
		throws RegistryException
	{
        checkStarted();
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
                if (portletFactory.isPortletApplicationRegistered(pa))
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
			log.error(msg, e);
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
            
            // grant default permissions to portlet application
			grantDefaultPermissions(paName);
            
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
	    startPA(contextName, warStruct, paClassLoader, local, 0);
	}
	
	protected void startPA(String contextName, FileSystemHelper warStruct,
	        ClassLoader paClassLoader, boolean local, long checksum)
	throws RegistryException
	{
        PortletApplicationWar paWar = null;
		try
		{
            boolean register = true;
            boolean monitored = checksum != 0;
            paWar = new PortletApplicationWar(warStruct, contextName, "/" + contextName, checksum);
            try
            {
                if (paClassLoader == null)
                {
                    paClassLoader = paWar.createClassloader(getClass().getClassLoader());
                }                
                checksum = paWar.getPortletApplicationChecksum();                
            }
            catch (IOException e)
            {
                String msg = "Invalid PA WAR for " + contextName;
                log.error(msg, e);
                if ( paClassLoader == null )
                {
                    // nothing to be done about it anymore: this pa is beyond repair :(
                    throw new RegistryException(e);
                }
                register = false;
            }

			MutablePortletApplication pa = (MutablePortletApplication) registry.getPortletApplication(contextName);

            if (pa != null)
            {
                DescriptorChangeMonitor changeMonitor = this.monitor;
                if (!monitored && changeMonitor != null)
                {
                    changeMonitor.remove(contextName);
                }
                portletFactory.unregisterPortletApplication(pa);                        
            }
            if (register && (pa == null || checksum != pa.getChecksum()))
            {
                try
                {
                    pa = registerPortletApplication(paWar, pa, local, paClassLoader);
                }
                catch (Exception e)
                {
                    // don't register the pa
                    register = false;
                }
            }
            if (register)
            {
                portletFactory.registerPortletApplication(pa, paClassLoader);
            }
            
            DescriptorChangeMonitor changeMonitor = this.monitor;
            if (!monitored && changeMonitor != null)
            {
                changeMonitor.monitor(contextName,paClassLoader, local, warStruct.getRootDirectory(), checksum);
            }
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
        DescriptorChangeMonitor monitor = this.monitor;
        if ( monitor != null )
        {
            monitor.remove(contextName);
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
        revokeDefaultPermissions(pa.getName());
	}
    
    protected void grantDefaultPermissions(String paName)
    {
        try
        {
            // create a default permission for this portlet app, granting configured roles to the portlet application 
            Iterator roles = permissionRoles.iterator();
            while (roles.hasNext())
            {
                String roleName = (String)roles.next();
                Role userRole = roleManager.getRole(roleName);
                if (userRole != null)
                {
                    Permission permission = new PortletPermission(paName + "::*", "view, edit");
                    if (!permissionManager.permissionExists(permission))
                    {
                        permissionManager.addPermission(permission);
                        permissionManager.grantPermission(userRole.getPrincipal(), permission);
                    }                    
                }
            }
        }
        catch (SecurityException e)
        {
            log.error("Error granting default permissions for " + paName, e);
        }        
    }
    
    protected void revokeDefaultPermissions(String paName)
    {
        try
        {
            Iterator roles = permissionRoles.iterator();
            while (roles.hasNext())
            {
                String roleName = (String)roles.next();
                Role userRole = roleManager.getRole(roleName);
                if (userRole != null)
                {
                    Permission permission = new PortletPermission(paName + "::*", "view, edit");
                    if (permissionManager.permissionExists(permission))
                    {
                        permissionManager.removePermission(permission);
                    }                    
                    
                }
            }
        }
        catch (SecurityException e)
        {
            log.error("Error revoking default permissions for " + paName, e);
        }
    }

    public int getDescriptorChangeMonitorInterval()
    {
        return descriptorChangeMonitorInterval/1000;
    }

    public void setDescriptorChangeMonitorInterval(int descriptorChangeMonitorInterval)
    {
        this.descriptorChangeMonitorInterval = descriptorChangeMonitorInterval*1000;
    }    
    
    private static class DescriptorChangeMonitor extends Thread
    {
        private static class DescriptorChangeMonitorInfo
        {
            private String contextName;
            private ClassLoader paClassLoader;
            private boolean local;
            private File paDir;
            private File[] descriptors;
            private long descriptorModificationTime;
            private long extendedDescriptorModificationTime;
            private long checksum;
            private boolean obsolete;
                        
            /*
             * Constructor only used for looking up the matching registered one in monitorsInfo
             */
            public DescriptorChangeMonitorInfo(String contextName)
            {
                this.contextName = contextName;
            }
            
            public DescriptorChangeMonitorInfo(String contextName, ClassLoader paClassLoader, boolean local, File paDir, long checksum)
            {
                this.contextName = contextName;
                this.paClassLoader = paClassLoader;
                this.local = local;
                this.paDir = paDir.isAbsolute() ? paDir : paDir.getAbsoluteFile();
                this.checksum = checksum;
                
                this.descriptors = new File[] { 
                        new File(paDir, PortletApplicationWar.WEB_XML_PATH),
                        new File(paDir, PortletApplicationWar.PORTLET_XML_PATH),
                        new File(paDir, PortletApplicationWar.EXTENDED_PORTLET_XML_PATH) };

                descriptorModificationTime = descriptors[1].lastModified();
                extendedDescriptorModificationTime = descriptors[2].lastModified();
            }
            
            public String getContextName()
            {
                return contextName;
            }
            
            public ClassLoader getPAClassLoader()
            {
                return paClassLoader;
            }
            
            public boolean isLocal()
            {
                return local;
            }
            
            public File getPADir()
            {
                return paDir;
            }

            public long getChecksum()
            {
                return checksum;
            }
            
            public boolean isChanged()
            {
                if ( !obsolete)
                {
                    long newDescriptorModificationTime = descriptors[1].lastModified();
                    long newExtendedDescriptorModificationTime = descriptors[2].lastModified();
                    if ( descriptorModificationTime != newDescriptorModificationTime ||
                            extendedDescriptorModificationTime != newExtendedDescriptorModificationTime )
                    {
                        descriptorModificationTime = newDescriptorModificationTime;
                        extendedDescriptorModificationTime = newExtendedDescriptorModificationTime;
                        long newChecksum = MultiFileChecksumHelper.getChecksum(descriptors);
                        if ( checksum != newChecksum )
                        {
                            checksum = newChecksum;
                            return true;
                        }
                    }
                }
                return false;
            }
            
            public void setObsolete()
            {
                obsolete = true;
            }
            
            public boolean isObsolete()
            {
                return obsolete;
            }
        }        

        private PortletApplicationManager pam;
        private long interval;
        private boolean started = true;
        private ArrayList monitorInfos;

        public DescriptorChangeMonitor(ThreadGroup group, String name, PortletApplicationManager pam, long interval)
        {
            super(group, name);
            this.pam = pam;
            this.interval = interval;
            monitorInfos = new ArrayList();
            setPriority(MIN_PRIORITY);
            setDaemon(true);
        }
        
        public void run()
        {
            try
            {
                sleep(interval);
            }
            catch (InterruptedException e)
            {
            }
            while (started)
            {
                checkDescriptorChanges();

                try
                {
                    sleep(interval);
                }
                catch (InterruptedException e)
                {

                }
            }
        }

        /**
         * notifies a switch variable that exits the watcher's montior loop started in the <code>run()</code> method.
         */
        public synchronized void safeStop()
        {
            started = false;
            monitorInfos.clear();
        }
        
        public synchronized void monitor(String contextName, ClassLoader paClassLoader, boolean local, File paDir, long checksum)
        {
            monitorInfos.add(new DescriptorChangeMonitorInfo(contextName, paClassLoader, local, paDir, checksum));
        }
        
        public synchronized void remove(String contextName)
        {
            DescriptorChangeMonitorInfo monitorInfo;
            for ( int i = monitorInfos.size()-1; i > -1; i-- )
            {
                monitorInfo = (DescriptorChangeMonitorInfo)monitorInfos.get(i);
                if (contextName.equals(monitorInfo.getContextName()))
                {
                    // will be removed by checkDescriptorChanges on next iteration
                    monitorInfo.setObsolete();
                    break;
                }
            }
        }
        
        private void checkDescriptorChanges()
        {
            int size;
            synchronized (this)
            {
                size = monitorInfos.size();
            }
            for (int i = size-1; i > -1; i--)
            {
                DescriptorChangeMonitorInfo monitorInfo;
                synchronized (this)
                {
                    if ( started )
                    {
                        monitorInfo = (DescriptorChangeMonitorInfo)monitorInfos.get(i);
                        if (monitorInfo.isObsolete())
                        {
                            monitorInfos.remove(i);
                        }
                        else
                        {
                            try
                            {
                                if (monitorInfo.isChanged())
                                {
                                    try
                                    {
                                        pam.startPA(monitorInfo.getContextName(), new DirectoryHelper(monitorInfo.getPADir()),
                                                monitorInfo.getPAClassLoader(), monitorInfo.isLocal(), monitorInfo.getChecksum());
                                    }
                                    catch (Exception e)
                                    {
                                        log.error("Failed to restart PortletApplication "+monitorInfo.getContextName(),e);
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                // ignore filesystem and/or descriptor errors, maybe next time round they'll be fixed again
                                log.error("Descriptor Change check failure for PortletApplication "+monitorInfo.getContextName(),e);
                            }
                        }
                    }
                }
            }
        }        
    }    
}
