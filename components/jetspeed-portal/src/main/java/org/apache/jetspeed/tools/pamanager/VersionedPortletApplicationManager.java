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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.descriptor.JetspeedDescriptorService;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
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



public class VersionedPortletApplicationManager extends PortletApplicationManager
{
    private static final Logger    log = LoggerFactory.getLogger("deployment");
        
    public VersionedPortletApplicationManager(PortletFactory portletFactory, PortletRegistry registry, 
            PermissionManager permissionManager, SearchEngine searchEngine,  RoleManager roleManager,
            List<String> permissionRoles, /* node manager, */ String appRoot, JetspeedDescriptorService descriptorService)
    {
        super(portletFactory, registry, permissionManager, 
                searchEngine, roleManager, permissionRoles, null, appRoot, descriptorService); 
               
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
    protected void startPA(String contextName, String contextPath, FileSystemHelper warStruct,
            ClassLoader paClassLoader, int paType, long checksum)
    throws RegistryException
    {
        PortletApplicationWar paWar = null;
        try
        {
            paWar = new PortletApplicationWar(warStruct, contextName, contextPath, checksum, this.descriptorService);
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
                log.error(msg, e);
                if ( paClassLoader == null )
                {
                    // nothing to be done about it anymore: this pa is beyond repair :(
                    throw new RegistryException(e);
                }
                //register = false;
            }
            
            PortletApplication regPA = registry.getPortletApplication(contextName); 
            PortletApplication newPA = paWar.createPortletApp();
            if (regPA == null)
            {
                System.out.println("**** New portlet app found - registration required..." + contextName);
                regPA = this.registerPortletApplication(paWar, null, paType, paClassLoader);
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
                    regPA = this.registerPortletApplication(paWar, regPA, paType, paClassLoader);                    
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
            log.error(msg, e);
            throw new RegistryException(msg, e);
        }
    }
    
    protected String getVersion(PortletApplication pa)
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