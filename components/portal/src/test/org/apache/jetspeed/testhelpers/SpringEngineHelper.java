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
package org.apache.jetspeed.testhelpers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalTestConstants;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngine;
import org.apache.jetspeed.mocks.ResourceLocatingServletContext;

import com.mockrunner.mock.web.MockServletConfig;

public class SpringEngineHelper extends AbstractTestHelper
{
    public static final String ENGINE_ATTR = "Engine";     
    
    protected JetspeedTestJNDIComponent jndiDS;
    
    public SpringEngineHelper(Map context)
    {
        super(context);
    }
    
    private Engine engine;

    public void setUp() throws Exception
    {
        jndiDS = new JetspeedTestJNDIComponent();
        jndiDS.setup();

        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load(new FileInputStream(PortalTestConstants.JETSPEED_PROPERTIES_PATH));
                
        String appRoot = PortalTestConstants.JETSPEED_APPLICATION_ROOT;
        
        MockServletConfig servletConfig = new MockServletConfig();        
        ResourceLocatingServletContext servletContent = new ResourceLocatingServletContext(new File(appRoot));        
        servletConfig.setServletContext(servletContent);
        ServletConfigFactoryBean.setServletConfig(servletConfig);
        
        SpringComponentManager scm = new SpringComponentManager(new String[] {"/WEB-INF/assembly/boot/datasource.xml"}, new String[] {"/WEB-INF/assembly/*.xml"}, servletContent, appRoot );
       
        engine = new JetspeedEngine(config, appRoot, servletConfig, scm );
        Jetspeed.setEngine(engine);
        engine.start();
        getContext().put(ENGINE_ATTR, engine );
    }

    public void tearDown() throws Exception
    {
        engine.shutdown();
        jndiDS.tearDown();
    }
}
