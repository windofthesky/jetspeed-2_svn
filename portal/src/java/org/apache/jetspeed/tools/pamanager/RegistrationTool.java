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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent;
import org.apache.jetspeed.components.portletregistry.PersistenceBrokerPortletRegistry;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.deployment.impl.StandardDeploymentObject;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;

/**
 * PortletApplicationManager
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
/**
 * This is the interface that defines the Lifecycle-related methods to control
 * Portlet Applications.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class RegistrationTool implements Registration
{
    private PortletRegistry registry;
    private static final Log log = LogFactory.getLog("deployment");
    private static final String PORTLET_XML = "WEB-INF/portlet.xml";
    
    public static void main(String args[])
    {
        String portletApplicationName = "demo";
        String warFile = "./target/demo.war";
        
        
        try
        {
            JNDIComponent jndi = new TyrexJNDIComponent();
            
            System.setProperty("org.apache.jetspeed.database.url", "jdbc:mysql://j2-server/xxxxx?autoReconnect=true");
            System.setProperty("org.apache.jetspeed.database.driver", "com.mysql.jdbc.Driver");
            System.setProperty("org.apache.jetspeed.database.user", "j2");
            System.setProperty("org.apache.jetspeed.database.password", "xxxxxx");
            
            String url = System.getProperty("org.apache.jetspeed.database.url");
            String driver = System.getProperty("org.apache.jetspeed.database.driver");
            String user = System.getProperty("org.apache.jetspeed.database.user");
            String password = System.getProperty("org.apache.jetspeed.database.password");
            
            BoundDBCPDatasourceComponent datasourceComponent = 
                new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000,
                    GenericObjectPool.WHEN_EXHAUSTED_GROW, true, "jetspeed", jndi);
            datasourceComponent.start();
                        
            PortletRegistry registry = new PersistenceBrokerPortletRegistry("META-INF/registry_repository.xml");
            ((InitablePersistenceBrokerDaoSupport)registry).init();
            Registration registrator = new RegistrationTool(registry);
            System.out.println("Registering Portlet Application [" + portletApplicationName + "]...");  
            
            StandardDeploymentObject deploymentObject = new StandardDeploymentObject(new File(warFile));
            registrator.register(new PortletApplicationWar(deploymentObject.getFileObject(),  
                                          portletApplicationName, "/"+portletApplicationName ) );
            System.out.println("...PAM Register done");   
            deploymentObject.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public RegistrationTool(PortletRegistry registry)
    {
        this.registry = registry;        
    }
    
    public void register(PortletApplicationWar paWar) 
        throws PortletApplicationException
    {
        String paName = paWar.getPortletApplicationName();
        MutablePortletApplication app = (MutablePortletApplication) registry.getPortletApplication(paName);
        try
        {
            if (app == null)
            {
                app = paWar.createPortletApp();
                if (app == null)
                {
                    String msg = "Error loading portlet.xml: ";
                    log.error(msg);
                    throw new PortletApplicationException(msg);
                }
                app.setApplicationType(MutablePortletApplication.WEBAPP);                                
                app.setChecksum(paWar.getFileSystem().getChecksum(PORTLET_XML));
                
                // load the web.xml
                log.info("Loading web.xml into memory...." + paName);
                MutableWebApplication webapp = paWar.createWebApp();
                paWar.validate();
                app.setWebApplicationDefinition(webapp);
                
                // register the portlet application
                if (registry.getPortletApplication(paName) == null)
                {
                    registry.registerPortletApplication(app);
                    log.info("Registered the portlet app... " + paName);
                }
//                if (searchEngine != null)
//                {
//                    searchEngine.add(app);
//                    searchEngine.add(app.getPortletDefinitions());
//                    log.info("Registered portlet app in the search engine... " + paName);
//                }
                
            }
        }
        catch (Exception e)
        {
            String msg = "Unable to register portlet application, " + paName + ", through the portlet registry: "
                + e.toString();
            log.error(msg, e);
            
            //throw new RegistryException(msg, e);
        }
        
    }
    
    public void unregister(String paName) 
        throws PortletApplicationException
    {
    
    }
    
}