package org.apache.jetspeed.testhelpers;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
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
        engine.start();
        getContext().put(ENGINE_ATTR, engine );
    }

    public void tearDown() throws Exception
    {
        engine.shutdown();
        jndiDS.tearDown();
    }
}
