/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.tools.registration;

import java.io.File;
import java.io.FileReader;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.util.descriptor.PortletApplicationDescriptor;
import org.apache.jetspeed.util.descriptor.WebApplicationDescriptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p>
 * OjbPortletRegistry
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: PersistenceBrokerPortletRegistry.java 225622 2005-07-27 20:39:14Z weaver $
 *  
 */
public class RegistrationTool 
{
    private PortletRegistry registry;
    private boolean overwriteApps = true;
    
    public static void main(String args[])
    {
        String fileName = System.getProperty("org.apache.jetspeed.portletregistry.configuration", "registration.properties");
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        try
        {
            File appRootDir = new File("./src/webapp");            
            System.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, appRootDir.getAbsolutePath());            
            configuration.load(fileName);        
            String [] bootAssemblies = configuration.getStringArray("boot.assemblies");
            String [] assemblies = configuration.getStringArray("assemblies");
            ClassPathXmlApplicationContext ctx;            
            
            if (bootAssemblies != null)
            {
                ApplicationContext bootContext = new ClassPathXmlApplicationContext(bootAssemblies, true);
                ctx = new ClassPathXmlApplicationContext(assemblies, true, bootContext);
            }
            else
            {
                ctx = new ClassPathXmlApplicationContext(assemblies, true);
            }
            
            boolean overwriteApps = configuration.getBoolean("overwrite.apps", true);
            String registryBean = configuration.getString("registry.component", "");
            String[] appNames = configuration.getStringArray("apps");
            String[] appDescriptors = configuration.getStringArray("descriptors");
            String[] webappDescriptors = configuration.getStringArray("webapp.descriptors");
            
            PortletRegistry registry = (PortletRegistry)ctx.getBean(registryBean);
            RegistrationTool tool = new RegistrationTool(registry, overwriteApps);
            
            for (int ix=0; ix < appNames.length; ix++)
            {
                if (overwriteApps)
                {
                    tool.unregister(appNames[ix]);
                }
                tool.register(appNames[ix], appDescriptors[ix], webappDescriptors[ix]);
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to import: " + e);
            e.printStackTrace();
        }
        
    }
    
    public RegistrationTool(PortletRegistry registry, boolean overwriteApps)
    {
        this.registry = registry;
        this.overwriteApps = overwriteApps;
    }
    
    public void unregister(String appName)
    throws Exception
    {
        if (registry.portletApplicationExists(appName))
        {
            PortletApplication app = registry.getPortletApplication(appName);
            if (app != null)
            {
                registry.removeApplication(app);
            }
        }
    }
    
    public void register(String appName, String appDescriptor, String webappDescriptor)
    throws Exception
    {
        WebApplicationDescriptor wad = new WebApplicationDescriptor(new FileReader(webappDescriptor), "/" + appName);
        MutableWebApplication webapp = wad.createWebApplication();
        PortletApplicationDescriptor pad = new PortletApplicationDescriptor(new FileReader(appDescriptor), appName);        
        MutablePortletApplication app = pad.createPortletApplication();        
        app.setWebApplicationDefinition(webapp);
        registry.registerPortletApplication(app);
    }
}