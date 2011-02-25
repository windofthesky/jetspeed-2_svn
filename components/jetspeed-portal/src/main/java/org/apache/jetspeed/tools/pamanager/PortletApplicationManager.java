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
package org.apache.jetspeed.tools.pamanager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.jetspeed.cluster.NodeManager;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.descriptor.JetspeedDescriptorService;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.SecurityRole;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.MultiFileChecksumHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PortletApplicationManager
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PortletApplicationManager implements PortletApplicationManagement
{
    private static int DEFAULT_DESCRIPTOR_CHANGE_MONITOR_INTERVAL = 10*1000; // 10 seconds
    private static int DEFAULT_MAX_RETRIED_STARTS = 10; // 10 times retry PA
    private static final Logger    log = LoggerFactory.getLogger("deployment");

    protected PortletFactory             portletFactory;
    protected PortletRegistry            registry;
    protected ReadWriteLock              registryLock;
    protected SearchEngine               searchEngine;
    protected RoleManager                roleManager;
    protected PermissionManager          permissionManager;
    protected boolean                    autoCreateRoles;
    protected List<String>               permissionRoles;
    protected int                        descriptorChangeMonitorInterval = DEFAULT_DESCRIPTOR_CHANGE_MONITOR_INTERVAL;
    /**
     * holds the max number of retries in case of unsuccessful PA start
     * this addresses possible startup errors in clustered environments
     */
    protected int  maxRetriedStarts = DEFAULT_MAX_RETRIED_STARTS;
    protected DescriptorChangeMonitor monitor = null;
    protected boolean started;
    protected String appRoot;
    protected NodeManager nodeManager;
    protected JetspeedDescriptorService descriptorService;
    
    protected PortletApplicationManagement pamProxy;
    protected boolean startOnSetPAMProxy;
    
    /**
     * Creates a new PortletApplicationManager object.
     */
    public PortletApplicationManager(PortletFactory portletFactory, PortletRegistry registry,
        PermissionManager permissionManager, SearchEngine searchEngine,
        RoleManager roleManager, List<String> permissionRoles, NodeManager nodeManager, String appRoot,
        JetspeedDescriptorService descriptorService)
    {
        this.portletFactory     = portletFactory;
        this.registry           = registry;
        this.permissionManager  = permissionManager;
        this.searchEngine       = searchEngine;
        this.roleManager        = roleManager;        
        this.permissionRoles    = permissionRoles;
        this.nodeManager        = nodeManager;
        this.appRoot            = appRoot;
        this.descriptorService  = descriptorService;
        
        // utilize read/write locked access to registry by default
        setLockRegistryAccess(true);
    }
    
    public void setPAMProxy(PortletApplicationManagement pamProxy)
    {
        this.pamProxy = pamProxy;
        if (!started && startOnSetPAMProxy)
        {
            start();
        }
    }
    
    public void start()
    {
        if ( pamProxy == null)
        {
            startOnSetPAMProxy = true;
            return;
        }
        if ( descriptorChangeMonitorInterval > 0 )
        {
            try
            {
                monitor = new DescriptorChangeMonitor(Thread.currentThread().getThreadGroup(),
                                                "PortletApplicationManager Descriptor Change Monitor Thread", pamProxy, descriptorChangeMonitorInterval, maxRetriedStarts);

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
    
    protected void checkStarted()
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
        retryStartPortletApplication(contextName, "/"+contextName, warStruct, paClassLoader, PortletApplication.LOCAL);
    }

    public void startPortletApplication(String contextName, FileSystemHelper warStruct,
        ClassLoader paClassLoader)
        throws RegistryException
    {
         startPortletApplication(contextName, "/"+contextName, warStruct, paClassLoader);
    }
    
    public void startPortletApplication(String contextName, String contextPath, FileSystemHelper warStruct,
            ClassLoader paClassLoader) throws RegistryException
    {
        checkStarted();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try
        {
            retryStartPortletApplication(contextName, contextPath, warStruct, paClassLoader, PortletApplication.WEBAPP);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }        
    }    

    protected void retryStartPortletApplication(String contextName, String contextPath, FileSystemHelper warStruct, ClassLoader paClassLoader, int paType) throws RegistryException
    {
        // Retry to start application according to configuration. Note
        // that this method is not declared transactional to allow clean
        // retries within a single transaction.
        RegistryException tryStartException = null;
        for (int i = 0; (i < maxRetriedStarts+1); i++)
        {
            try
            {
                // try to start portlet application
                pamProxy.tryStartPortletApplication(contextName, contextPath, warStruct, paClassLoader, paType, 0, true);
                // continue on success
                tryStartException = null;
                break;
            }
            catch (RegistryException re)
            {
                // save exception
                tryStartException = re;
            }
            // brief pause between retries to let portlet application
            // state settle
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException ie)
            {
            }
        }
        // throw try start exception
        if (tryStartException != null)
        {
            log.error("Unable to start portlet application after "+maxRetriedStarts+" retries: "+tryStartException, tryStartException);
            throw tryStartException;
        }
    }

    public void tryStartPortletApplication(String contextName, String contextPath, FileSystemHelper warStruct, ClassLoader paClassLoader, int paType, long checksum, boolean silent) throws RegistryException
    {
        attemptStartPA(contextName, contextPath, warStruct, paClassLoader, paType, checksum, silent);
    }

    public void stopLocalPortletApplication(String contextName)
        throws RegistryException
    {
        stopPA(contextName, PortletApplication.LOCAL);
    }

    public void stopPortletApplication(String contextName)
        throws RegistryException
    {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try
        {
            stopPA(contextName, PortletApplication.WEBAPP);
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
            PortletApplication pa = null;
            lockRegistry(RegistryLock.READ);
            try
            {
                pa = registry.getPortletApplication(paName);
            }
            catch (Exception e)
            {
                // ignore errors during portal shutdown
            }
            finally
            {
                unlockRegistry(RegistryLock.READ);
            }
            
            if (pa != null)
            {
                if (portletFactory.isPortletApplicationRegistered(pa))
                {
                    throw new RegistryException("Portlet Application " + paName + " still running");
                }

                unregisterPortletApplication(pa, true);
                try
                {
                    if (nodeManager != null)                    
                        nodeManager.removeNode(paName);
                }
                catch (Exception ee)
                {
                    // we actually do not care about an exception in the remove operation...
                }
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

    protected PortletApplication registerPortletApplication(PortletApplicationWar paWar,
        PortletApplication oldPA, int paType, ClassLoader paClassLoader, boolean silent)
        throws RegistryException
    {
        long revision = 0;
        if (oldPA != null)
        {
            revision = oldPA.getRevision();
            unregisterPortletApplication(oldPA, false);
            oldPA = null;
        }

        PortletApplication pa        = null;
        boolean                   registered = false;
        String                    paName     = paWar.getPortletApplicationName();

        try
        {
            log.info("Loading deployment descriptors for "+paName+" ....");
            pa = paWar.createPortletApp(paClassLoader);
            pa.setApplicationType(paType);
            if (revision > 0)
            {
                pa.setRevision(revision);
            }

            if (paType == PortletApplication.LOCAL)
            {
                pa.setContextPath("<portal>");
            }
        }
        catch (Exception e)
        {
            String msg = "Failed to load portlet application for "
                + paWar.getPortletApplicationName();
            if (!silent || log.isDebugEnabled())
            {
                log.error(msg, e);
            }
            throw new RegistryException(msg, e);
        }

        // register the portlet application
        try
        {
            lockRegistry(RegistryLock.WRITE);
            try
            {
                registry.registerPortletApplication(pa);
            }
            finally
            {
                unlockRegistry(RegistryLock.WRITE);                
            }
            registered = true;
            log.info("Registered the portlet application " + paName);

            // add to search engine result
            this.updateSearchEngine(false, pa);
            
            // and add to the current node info
            if (nodeManager != null)
            {            
                nodeManager.addNode(new Long(pa.getRevision()), pa.getName());
            }
            // grant default permissions to portlet application
            grantDefaultPermissions(paName);
            
            if ( autoCreateRoles && roleManager != null && pa.getSecurityRoles() != null )
            {
                try
                {
                    for (SecurityRole sr : pa.getSecurityRoles())
                        if ( !roleManager.roleExists(sr.getName()) )
                        {
                            roleManager.addRole(sr.getName());
                            log.info("AutoCreated role: "+sr.getName()+" from portlet application "+paName+" its web definition");
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
            if (!silent || log.isDebugEnabled())
            {
                log.error(msg, e);
            }

            if (registered)
            {
                try
                {
                    unregisterPortletApplication(pa, (paType == PortletApplication.LOCAL));
                }
                catch (Exception re)
                {
                    if (!silent || log.isDebugEnabled())
                    {
                        log.error("Failed to rollback registration of portlet application " + paName, re);
                    }
                }
            }

            throw new RegistryException(msg, e);
        }
    }

    protected void attemptStartPA(String contextName, String contextPath, FileSystemHelper warStruct,
            ClassLoader paClassLoader, int paType, long checksum, boolean silent)
    throws RegistryException
    {
        boolean register = true;
        boolean monitored = false;
        DescriptorChangeMonitor changeMonitor = this.monitor;
        if (changeMonitor != null)
        {
            monitored = changeMonitor.isMonitored(contextName);
        }
        if (log.isDebugEnabled())
        {
            log.debug("Is portlet application " + contextName + " monitored? -> " + monitored);
        }
        PortletApplicationWar paWar = null;
        try
        {
            if (log.isDebugEnabled())
            {
                log.debug("Try to start portlet application " + contextName + ".");
            }
            // create PA  from war (file) structure
            // paWar = new PortletApplicationWar(warStruct, contextName, "/" + contextName, checksum);
            paWar = new PortletApplicationWar(warStruct, contextName, contextPath, checksum, this.descriptorService);
            try
            {
                if (paClassLoader == null)
                {
                    paClassLoader = paWar.createClassloader(this.getClass().getClassLoader());
                }                
                // create checksum from PA descriptors
                checksum = paWar.getPortletApplicationChecksum();                
                
                if (log.isDebugEnabled())
                {
                    log.debug("New checksum for portlet application " + contextName + " is " + checksum);
                }
            }
            catch (IOException e)
            {
                String msg = "Invalid PA WAR for " + contextName;
                //if (!silent || log.isDebugEnabled())
                {
                    e.printStackTrace();
                    log.error(msg, e);
                }
                if ( paClassLoader == null )
                {
                    // nothing to be done about it anymore: this pa is beyond repair :(
                    throw new RegistryException(e);
                }
                register = false;
            }

            // try to get the PA from database by context name
            PortletApplication pa = null;
            lockRegistry(RegistryLock.READ);
            try
            {
                pa = registry.getPortletApplication(contextName);
            }
            finally
            {
                unlockRegistry(RegistryLock.READ);                
            }

            if (pa != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Portlet Application " + contextName + " found in registry.");
                }
                if ( pa.getApplicationType() != paType )
                {
                    throw new RegistryException("Cannot start portlet application "+contextName+": as Application Types don't match: " + pa.getApplicationType() + " != " + paType);
                }
                if (!monitored && changeMonitor != null)
                {
                    changeMonitor.remove(contextName);
                }
                if (log.isDebugEnabled())
                {
                    log.debug("unregistering portlet application " + contextName + "...");
                }
                portletFactory.unregisterPortletApplication(pa);                        
            }
//            if (register && (pa == null || checksum != pa.getChecksum()))
            if (register)
            {
                if (pa == null)
                { 
                    // new
                    try
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Register new portlet application " + contextName + ".");
                        }
                        
                        pa = registerPortletApplication(paWar, pa, paType, paClassLoader, silent);
                    }
                    catch (RegistryException e)
                    {
                        throw e;
                    }
                    catch (Exception e)
                    {
                        String msg = "Error register new portlet application " + contextName + ".";
                        
                        if (log.isDebugEnabled())
                        {
                            log.debug(msg);
                        }
                        
                        throw new RegistryException(msg);
                    }
                }
                else
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Re-register existing portlet application " + contextName + ".");
                    }
                    int status = nodeManager.checkNode(new Long(pa.getRevision()), pa.getName());
                    boolean reregister = false;
                    boolean deploy = false;
                    switch (status)
                    {
                        case  NodeManager.NODE_NEW:
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug("Node for Portlet application " + contextName + " is NEW.");
                            }
                            //only reason is that the file got somehow corrupted 
                            // so we really do not know what is going on here...
                            // the best chance at this point is to reregister (which might be the absolute wrong choice)
                            log.warn("The portlet application " + pa.getName() + " is registered in the database but not locally .... we will reregister");
                            reregister = true;
                            if (checksum != pa.getChecksum())
                            {
                                log.warn("The provided portlet application " + pa.getName() + " is a different version than in the database (db-checksum=" + pa.getChecksum() + ", local-checksum=: " + checksum + ") .... we will redeploy (also to the database)");
                                deploy = true;
                            }
                            break;
                        }
                        case  NodeManager.NODE_SAVED:
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug("Node for Portlet application " + contextName + " is SAVED.");
                            }
                            if (checksum != pa.getChecksum())
                            {   
                                log.warn("The provided portlet application " + pa.getName() + " is a different version than in the local node info and the database (db-checksum=" + pa.getChecksum() + ", local-checksum=: " + checksum + ") .... we will reregister AND redeploy (also to the database)");
                                //database and local node info are in synch, so we assume that this is a brand new
                                // war .... let's deploy
                                reregister = true;
                                deploy = true;
                            }
                            break;
                        }
                        case  NodeManager.NODE_OUTDATED:
                        {
                            // new version in database, maybe changed by a different cluster node
                            if (log.isDebugEnabled())
                            {
                                log.debug("Node for Portlet application " + contextName + " is OUTDATED (local PA.id < DB PA.id).");
                            }
                            //database version is older (determined by id) than the database 
                            //let's deploy and reregister
                            if (checksum != pa.getChecksum())
                            {
                                log.error("The portlet application " + pa.getName() + " provided for the upgrade IS WRONG. The database checksum= " + pa.getChecksum() + ", but the local=" + checksum + "....THIS NEEDS TO BE CORRECTED");
                                // if the checksums do not match make sure the database is updated with the new PA from file system
                                // I've observed "unavailable PA" in clustered env for the cluster node that reported OUTDATED state
                                deploy = true;
                            }
                            reregister = true;
                            break;
                        }
                    }
                    if (deploy)
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Register (deploy=true) Portlet application " + contextName + " in database.");
                        }
                        pa = registerPortletApplication(paWar, pa, paType, paClassLoader, silent);
                    }
                    else
                        if (reregister)
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug("Re-Register (reregister=true) Portlet application " + contextName + ".");
                            }
                            // add to search engine result
                            this.updateSearchEngine(true, pa);
                            this.updateSearchEngine(false, pa);
                            
                            // and add to the current node info
                            try
                            {
                                nodeManager.addNode(new Long(pa.getRevision()), pa.getName());
                            } catch (Exception e)
                            {
                                log.error("Adding node for portlet application " + pa.getName() + " caused exception" , e);
                            }
                        }
                        
                
                }
            }
            if (register)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Register Portlet application " + contextName + " in portlet factory.");
                }
                portletFactory.registerPortletApplication(pa, paClassLoader);
            }
            
            if (!monitored && changeMonitor != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Add change monitor for application " + contextName + " with checksum " + checksum + ".");
                }
                changeMonitor.monitor(contextName, contextPath, paClassLoader, paType, warStruct.getRootDirectory(), checksum);
            }
        }
        catch (Exception e)
        {
            String msg = "Error starting portlet application " + contextName;
            
            if (!silent || log.isDebugEnabled())
            {
                log.error(msg, e);
            }
            // monitor PA for changes
            // do not add monitor if a monitor already exists
            if (!monitored && changeMonitor != null)
            {
                // this code should be hit only during startup process
                if (log.isDebugEnabled())
                {
                    log.debug("Add change monitor for application " + contextName + " and set unsuccessful starts to 1.");
                }
                changeMonitor.monitor(contextName, contextPath, paClassLoader, paType, warStruct.getRootDirectory(), checksum);
                changeMonitor.get(contextName).setUnsuccessfulStarts(1);
            }
            throw new RegistryException(msg);
        }
    }

    protected void stopPA(String contextName, int paType)
        throws RegistryException
    {
        PortletApplication pa = null;
        lockRegistry(RegistryLock.READ);
        try
        {
            pa = registry.getPortletApplication(contextName);
        }
        catch (Exception e)
        {
            // ignore errors during portal shutdown
        }
        finally
        {
            unlockRegistry(RegistryLock.READ);                
        }
        
        if  (pa != null && pa.getApplicationType() != paType) 
        {
            throw new RegistryException("Cannot stop portlet application "+contextName+": as Application Types don't match: " + pa.getApplicationType() + " != " + paType);
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

    
    protected void updateSearchEngine(boolean remove,PortletApplication pa )
    {
        if (searchEngine != null)
        {
            List<PortletDefinition> portletDefList = pa.getPortlets();
            List<PortletDefinition> cloneDefList = pa.getClones();
            List<Object> list = new ArrayList<Object>(portletDefList.size() + cloneDefList.size() + 1);
            if (remove)
            {
                list.addAll(portletDefList);
                list.addAll(cloneDefList);
                list.add(pa);
                searchEngine.remove(list);
                log.info("Un-Registered the portlet application in the search engine... " + pa.getName());
            }
            else
            {
                list.add(pa);
                list.addAll(portletDefList);
                list.addAll(cloneDefList);
                searchEngine.add(list);
                log.info("Registered the portlet application in the search engine... " + pa.getName());
            }
        }
    }
    
    protected void unregisterPortletApplication(PortletApplication pa,
        boolean purgeEntityInfo)
        throws RegistryException
    {

        updateSearchEngine(true,pa);

        // todo keep (User)Prefs?
        lockRegistry(RegistryLock.WRITE);
        try
        {
            registry.removeApplication(pa);
        }
        finally
        {
            unlockRegistry(RegistryLock.WRITE);                
        }
        
        revokeDefaultPermissions(pa.getName());
    }
    
    protected void grantDefaultPermissions(String paName)
    {
        try
        {
            // create a default permission for this portlet app, granting configured roles to the portlet application 
            for (String roleName : permissionRoles)
            {
                Role userRole = roleManager.getRole(roleName);
                if (userRole != null)
                {
                    JetspeedPermission permission = permissionManager.newPermission(permissionManager.PORTLET_PERMISSION, paName + "::*", "view, edit");
                    if (!permissionManager.permissionExists(permission))
                    {
                        permissionManager.addPermission(permission);
                        permissionManager.grantPermission(permission, userRole);
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
            for (String roleName : permissionRoles)
            {
                Role userRole = roleManager.getRole(roleName);
                if (userRole != null)
                {
                    JetspeedPermission permission = permissionManager.newPermission(permissionManager.PORTLET_PERMISSION, paName + "::*", "view, edit");
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
            private String contextPath;
            private ClassLoader paClassLoader;
            private int  paType;
            private File paDir;
            private File[] descriptors;
            private long descriptorModificationTime;
            private long extendedDescriptorModificationTime;
            private long checksum;
            private boolean obsolete;
            
            /**
             * holds the number of unsuccessful starts of the monitored PA
             */
            private int unsuccessfulStarts;
                        
            /*
             * Constructor only used for looking up the matching registered one in monitorsInfo
             */
            public DescriptorChangeMonitorInfo(String contextName)
            {
                this.contextName = contextName;
            }
            
            public DescriptorChangeMonitorInfo(String contextName, String contextPath, ClassLoader paClassLoader, int paType, File paDir, long checksum)
            {
                this.contextName = contextName;
                this.contextPath = contextPath;
                this.paClassLoader = paClassLoader;
                this.paType = paType;
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
            
            public int getPortletApplicationType()
            {
                return paType;
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
                        if (log.isDebugEnabled())
                        {
                            log.debug("checksum check for descriptors for application " + contextName + ": old (" + checksum + ") new (" + newChecksum + ").");
                        }
                        if ( checksum != newChecksum )
                        {
                            if (log.isDebugEnabled())
                            {
                                log.debug("portlet descriptors for application " + contextName + " have changed.");
                            }
                            checksum = newChecksum;
                            // reset this to restart unsuccessful PA start handling for evers PA descriptor change
                            unsuccessfulStarts = 0;
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

            public int getUnsuccessfulStarts()
            {
                return unsuccessfulStarts;
            }
            
            public void setUnsuccessfulStarts(int unsuccessfulStarts)
            {
                this.unsuccessfulStarts = unsuccessfulStarts;
            }
            
            public String getContextPath()
            {
                return contextPath;
            }
        }        

        private PortletApplicationManagement pam;
        private long interval;
        private boolean started = true;
        private ArrayList monitorInfos;
        private int maxRetriedStarts;

        public DescriptorChangeMonitor(ThreadGroup group, String name, PortletApplicationManagement pam, long interval, int maxretriedStarts)
        {
            super(group, name);
            this.pam = pam;
            this.interval = interval;
            monitorInfos = new ArrayList();
            setPriority(MIN_PRIORITY);
            setDaemon(true);
            this.maxRetriedStarts = maxretriedStarts;
        }
        
        public synchronized void run()
        {
            try
            {
                wait(interval);
            }
            catch (InterruptedException e)
            {
            }
            while (started)
            {
                checkDescriptorChanges();
                try
                {
                    wait(interval);
                }
                catch (InterruptedException e)
                {
                }
            }
        }

        /**
         * notifies a switch variable that exits the watcher's montior loop started in the <code>run()</code> method.
         */
        public void safeStop()
        {
            // stop this monitor thread
            synchronized (this)
            {
                started = false;
                monitorInfos.clear();
                notifyAll();
            }
            // wait for monitor thread stop
            try
            {
                join(interval);
            }
            catch (InterruptedException ie)
            {
            }
        }
        
        public synchronized void monitor(String contextName, String contextPath, ClassLoader paClassLoader, int paType, File paDir, long checksum)
        {
            monitorInfos.add(new DescriptorChangeMonitorInfo(contextName, contextPath, paClassLoader, paType, paDir, checksum));
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

        public synchronized DescriptorChangeMonitorInfo get(String contextName)
        {
            DescriptorChangeMonitorInfo monitorInfo;
            for ( int i = monitorInfos.size()-1; i > -1; i-- )
            {
                monitorInfo = (DescriptorChangeMonitorInfo)monitorInfos.get(i);
                if (contextName.equals(monitorInfo.getContextName()))
                {
                    return monitorInfo;
                }
            }
            return null;
        }
        
        public boolean isMonitored(String contextName)
        {
            DescriptorChangeMonitorInfo monitorInfo = this.get(contextName);
            if (monitorInfo != null && !monitorInfo.isObsolete())
            {
                return true;
            }
            return false;
        }
        
        private synchronized void checkDescriptorChanges()
        {
            int size = monitorInfos.size();

            if (log.isDebugEnabled())
            {
                log.debug("check for portlet application descriptor changes.");
            }
            
            for (int i = size-1; i > -1; i--)
            {
                DescriptorChangeMonitorInfo monitorInfo;
                
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
                                int unsuccessfulStarts = monitorInfo.getUnsuccessfulStarts();
                                // try to restart PA if the PA-descriptors have change
                                // OR (if we encountered an exception while starting the PA)
                                // keep on trying to restart PA until maxRetriedStarts is reached
                                // This ensures we finally startup in a clustered enviroment, where parallel registration
                                // of PAs could lead to contraint violation eceptions in DB.
                                // see https://issues.apache.org/jira/browse/JS2-666
                                // Note: monitorInfo.isChanged() will reset unsuccessfulStarts to 0 if a PA descriptor change 
                                // has been detected (monitorInfo.isChanged() == true).
                                if (monitorInfo.isChanged() || (unsuccessfulStarts > 0 && unsuccessfulStarts <= maxRetriedStarts))
                                {
                                    try
                                    {
                                        pam.tryStartPortletApplication(monitorInfo.getContextName(), monitorInfo.getContextPath(), new DirectoryHelper(monitorInfo.getPADir()),
                                                                       monitorInfo.getPAClassLoader(), monitorInfo.getPortletApplicationType(), monitorInfo.getChecksum(), true);
                                        // great! we have a successful start. set unsuccessful starts to 0
                                        monitorInfo.setUnsuccessfulStarts(0);
                                    }
                                    catch (Exception e)
                                    {
                                        if (monitorInfo.isChanged())
                                        {
                                            log.error("Failed to restart PortletApplication "+monitorInfo.getContextName(),e);
                                        }
                                        else if (log.isWarnEnabled())
                                        {
                                            log.warn("Failed to restart PortletApplication "+monitorInfo.getContextName(),e);
                                        }
                                        // we encountered an error while starting the PA
                                        // this could result from clustered environments problems (see above)
                                        // increase unsuccessfulStarts until the maxRetriedStarts is reached
                                        monitorInfo.setUnsuccessfulStarts(unsuccessfulStarts + 1);
                                        if (log.isDebugEnabled())
                                        {
                                            log.debug("Number of unsuccessful PA starts is " + monitorInfo.getUnsuccessfulStarts() + ".");
                                        }
                                        if (monitorInfo.getUnsuccessfulStarts() > maxRetriedStarts)
                                        {
                                            log.error("Max number of retries (" + maxRetriedStarts +") reached. Ignoring Monitor for " + monitorInfo.getContextName());
                                        }
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

    public void setMaxRetriedStarts(int maxRetriedStarts)
    {
        this.maxRetriedStarts = maxRetriedStarts;
    }

    public int getMaxRetriedStarts()
    {
        return maxRetriedStarts;
    }    

    /**
     * RegistryLock access type enumeration.
     */
    protected enum RegistryLock {READ, WRITE};
    
    /**
     * Lock specified access to registry.
     * 
     * @param lockAccess lock access type
     */
    protected void lockRegistry(RegistryLock lockAccess)
    {
        if (registryLock != null)
        {
            switch (lockAccess)
            {
                case READ : registryLock.readLock().lock(); break;
                case WRITE : registryLock.writeLock().lock(); break;
            }
        }
    }

    /**
     * Unlock specified access to registry.
     * 
     * @param lockAccess lock access type
     */
    protected void unlockRegistry(RegistryLock lockAccess)
    {
        if (registryLock != null)
        {
            switch (lockAccess)
            {
                case READ : registryLock.readLock().unlock(); break;
                case WRITE : registryLock.writeLock().unlock(); break;
            }
        }
    }
    
    /**
     * Get read/write lock registry access configuration.
     * 
     * @return lock registry access configuration.
     */
    public boolean getLockRegistryAccess()
    {
        return (registryLock != null);
    }
    
    /**
     * Set read/write lock registry access configuration.
     * 
     * @param lockRegistryAccess lock registry access configuration.
     */
    public void setLockRegistryAccess(boolean lockRegistryAccess)
    {
        if ((registryLock != null) != lockRegistryAccess)
        {
            registryLock = (lockRegistryAccess ? new ReentrantReadWriteLock(true) : null);
        }
    }
}
