package org.apache.jetspeed.testhelpers;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.PortalTestConstants;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngine;
import org.apache.jetspeed.mocks.ResourceLocatingServletContext;
import org.springframework.context.ApplicationContext;

import com.mockrunner.mock.web.MockServletConfig;

public class SpringEngineHelper extends OJBHelper
{

    public static final String ENGINE_ATTR = "Engine";     
    
    
    public SpringEngineHelper(Map context)
    {
        super(context);     
    }
    
    private Engine engine;

    public void setUp() throws Exception
    {
        super.setUp();
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load(new FileInputStream(PortalTestConstants.JETSPEED_PROPERTIES_PATH));
                
        HashMap context = new HashMap();
        OJBHelper ojbHelper = new OJBHelper(context);
        ojbHelper.setUp();
        String appRoot = PortalTestConstants.JETSPEED_APPLICATION_ROOT;
        
        MockServletConfig servletConfig = new MockServletConfig();        
        ResourceLocatingServletContext servletContent = new ResourceLocatingServletContext(new File(appRoot));        
        servletConfig.setServletContext(servletContent);
        ServletConfigFactoryBean.setServletConfig(servletConfig);
        ApplicationContext bootCtx = (ApplicationContext) context.get(AbstractTestHelper.APP_CONTEXT);
        
        SpringComponentManager scm = new SpringComponentManager(null, new String[] {"/WEB-INF/assembly/*.xml"}, servletContent, appRoot );
       
        engine = new JetspeedEngine(config, appRoot, servletConfig, scm );
        engine.start();
        getContext().put(ENGINE_ATTR, engine );
    }

    public void tearDown() throws Exception
    {
        engine.shutdown();  
        super.tearDown();
    }



}
