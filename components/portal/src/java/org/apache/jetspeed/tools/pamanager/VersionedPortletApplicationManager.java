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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;

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



public class VersionedPortletApplicationManager extends PortletApplicationManager implements PortletApplicationManagement
{
    private static final Log    log = LogFactory.getLog("deployment");
        
    public VersionedPortletApplicationManager(PortletFactory portletFactory, PreferencesProvider prefs, PortletRegistry registry, 
            PortletEntityAccessComponent entityAccess, PortletWindowAccessor windowAccess,
            PermissionManager permissionManager, SearchEngine searchEngine,  RoleManager roleManager,
            List permissionRoles, /* node manager, */ String appRoot)
    {
        super(portletFactory, prefs, registry, entityAccess, windowAccess, permissionManager, 
                searchEngine, roleManager, permissionRoles, null, appRoot); 
               
    }
    
    public boolean isStarted()
    {
        return started;
    }
    
    public void start()
    {
        started = true;
    }

    public void stop()
    {
        started = false;
    }
        
    // override to implement versioning logic
    protected void attemptStartPA(String contextName, String contextPath, FileSystemHelper warStruct,
            ClassLoader paClassLoader, int paType, long checksum, boolean silent)
    throws RegistryException
    {
        PortletApplicationWar paWar = null;
        try
        {
            paWar = new PortletApplicationWar(warStruct, contextName, contextPath, checksum);
            try
            {
                if (paClassLoader == null)
                {
                    paClassLoader = paWar.createClassloader(getClass().getClassLoader());
                }
            }
            catch (IOException e)
            {
                String msg = "Invalid PA WAR for " + contextName;
                if (!silent || log.isDebugEnabled())
                {
                    log.error(msg, e);
                }
                if ( paClassLoader == null )
                {
                    // nothing to be done about it anymore: this pa is beyond repair :(
                    throw new RegistryException(e);
                }
                //register = false;
            }
            
            MutablePortletApplication regPA = registry.getPortletApplication(contextName);
            MutablePortletApplication newPA = paWar.createPortletApp(false);
            if (regPA == null)
            {
                System.out.println("**** New portlet app found - registration required..." + contextName);
                regPA = registerPortletApplication(paWar, null, paType, paClassLoader, silent);
            }
            else
            {
                String regVersion = getVersion(regPA);
                String newVersion = getVersion(newPA);
                System.out.print("Reg version is " + regVersion);
                System.out.print(", New version is " + newVersion);
                if (newVersion.compareTo(regVersion) > 0)
                {
                    System.out.println(" - **** New Version is greater: registration required... " + contextName);
                    regPA = registerPortletApplication(paWar, regPA, paType, paClassLoader, silent);                    
                }
                else
                {
                    System.out.println(" - New Version is NOT greater: registration not required ... " + contextName);                    
                }
            }
            if (portletFactory.isPortletApplicationRegistered(regPA))
            {
                portletFactory.unregisterPortletApplication(regPA);
            }
            portletFactory.registerPortletApplication(regPA, paClassLoader);
        }
        catch (Exception e)
        {
            String msg = "Error starting portlet application " + contextName;            
            if (!silent || log.isDebugEnabled())
            {
                log.error(msg, e);
            }
            throw new RegistryException(msg, e);
        }
    }
    
    protected String getVersion(MutablePortletApplication pa)
    {
        String version = "";
        Collection versionList = pa.getMetadata().getFields("pa-version");
        if (versionList != null)
        {
            Iterator it = versionList.iterator();
            if (it.hasNext())
            {
                LocalizedField field = (LocalizedField)it.next();
                version = field.getValue();
            }
        }
        return version;
    }
}
