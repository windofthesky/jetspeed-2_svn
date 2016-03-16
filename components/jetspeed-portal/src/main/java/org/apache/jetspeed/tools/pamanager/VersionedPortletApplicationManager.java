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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

    private Boolean useNumericVersions = true;

    public VersionedPortletApplicationManager(PortletFactory portletFactory, PortletRegistry registry,
            PermissionManager permissionManager, SearchEngine searchEngine,  RoleManager roleManager,
            List<String> permissionRoles, /* node manager, */ String appRoot, JetspeedDescriptorService descriptorService)
    {
        super(portletFactory, registry, permissionManager,
                searchEngine, roleManager, permissionRoles, null, appRoot, descriptorService); 
               
    }

    public Boolean getUseNumericVersions() {
        return useNumericVersions;
    }

    public void setUseNumericVersions(Boolean useNumericVersions) {
        this.useNumericVersions = useNumericVersions;
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
            
            PortletApplication regPA = null;
            lockRegistry(RegistryLock.READ);
            try
            {
                regPA = registry.getPortletApplication(contextName);
            }
            finally
            {
                unlockRegistry(RegistryLock.READ);                
            }
            
            PortletApplication newPA = paWar.createPortletApp();
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
                if (newVersion.equals("")) 
                {
                    System.out.println(" - New Version was NOT provided: registration not required ... " + contextName);                                    	
                }
                else if (compareVersions(newVersion, regVersion, useNumericVersions) > 0)
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

    public static int compareVersions(String newVersion, String regVersion, boolean useNumericVersions) throws NumberFormatException {
        if (!useNumericVersions) {
            return newVersion.compareTo(regVersion);
        }
        String[] v1 = newVersion.split("\\.");
        String[] v2 = regVersion.split("\\.");

        int ix = 0;
        while(ix < v1.length || ix < v2.length){
            if(ix < v1.length && ix < v2.length){
                if(Integer.parseInt(v1[ix]) < Integer.parseInt(v2[ix])){
                    return -1;
                }else if(Integer.parseInt(v1[ix]) > Integer.parseInt(v2[ix])){
                    return 1;
                }
            } else if(ix < v1.length){
                if(Integer.parseInt(v1[ix]) != 0){
                    return 1;
                }
            } else if(ix < v2.length){
                if(Integer.parseInt(v2[ix]) != 0){
                    return -1;
                }
            }

            ix++;
        }
        return 0;
    }

}