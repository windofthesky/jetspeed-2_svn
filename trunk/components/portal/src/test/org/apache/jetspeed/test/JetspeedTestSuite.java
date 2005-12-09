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
package org.apache.jetspeed.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalTestConstants;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;
import org.jmock.Mock;

import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

/**
 * <p>
 * JetspeedTestSuite
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedTestSuite extends TestSuite
{
    protected static Engine engine = null;
    private static SpringEngineHelper engineHelper;    

    /**
     * 
     */
    public JetspeedTestSuite()
    {
        super();
		startEngine(getApplicationRoot(), getPropertiesFile());
        
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JetspeedTestSuite(Class arg0, String arg1)
    {
        super(arg0, arg1);
		startEngine(getApplicationRoot(), getPropertiesFile());
        
    }

    /**
     * @param arg0
     */
    public JetspeedTestSuite(Class arg0)
    {
        super(arg0);
		startEngine(getApplicationRoot(), getPropertiesFile());
        
    }

    /**
     * @param arg0
     */
    public JetspeedTestSuite(String arg0)
    {
        super(arg0);
		startEngine(getApplicationRoot(), getPropertiesFile());
        
    }

    protected static void startEngine(String applicationRoot, String propertiesFilename)
    {
        try
        {
            if (engine != null)
            {
                return;
            }
            
            Configuration properties = (Configuration) new PropertiesConfiguration(propertiesFilename);

            properties.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, applicationRoot);
            //properties.setProperty(WEBAPP_ROOT_KEY, null);
            initializeConfiguration(properties, applicationRoot);
            Mock servletConfigMock = new Mock(ServletConfig.class);
            MockServletConfig msc = new MockServletConfig();
            msc.setServletContext(new MockServletContext());
            HashMap context = new HashMap();
            engineHelper = new SpringEngineHelper(context);
            engineHelper.setUp();
            engine = (Engine) context.get(SpringEngineHelper.ENGINE_ATTR);

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
    }
    protected static void stopEngine()
    {
        try
        {
            if (engine != null)
            {
                Jetspeed.shutdown();
            }
        }
        catch (JetspeedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            engine = null;
        }
    }

    /**
     * Override to set your own application root
     *
     */
    public String getApplicationRoot()
    {
        String applicationRoot = System.getProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, PortalTestConstants.PORTAL_WEBAPP_PATH);
        return applicationRoot;
    }

    /**
     * Override to set your own properties file
     *
     */
    public String getPropertiesFile()
    {
        String jetspeedProperties = System.getProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, PortalTestConstants.PORTAL_WEBAPP_PATH) + "/WEB-INF/conf/jetspeed.properties";        
        return jetspeedProperties;
    }

    /*
     * Implement this method to override any properties in your TestSuite.
     * If you override this method in a derived class, call super.overrideProperties to get these settings
     * 
     * @param properties The base configuration properties for the Jetspeed system.
     */
    protected static void initializeConfiguration(Configuration properties, String appRoot)
    {
        String testPropsPath = appRoot + "/WEB-INF/conf/test/jetspeed.properties";
        try
        {
            File testFile = new File(testPropsPath);
            if (testFile.exists())
            {
                FileInputStream is = new FileInputStream(testPropsPath);
                Properties props = new Properties();
                props.load(is);

                Iterator it = props.entrySet().iterator();
                while (it.hasNext())
                {
                    Entry entry = (Entry) it.next();
                    //if (entry.getValue() != null && ((String)entry.getValue()).length() > 0)
                    properties.setProperty((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @see junit.framework.Test#run(junit.framework.TestResult)
     */
    public void run(TestResult arg0)
    {
        try
        {            
            super.run(arg0);
        }
        finally
        {
            stopEngine();
        }
    }

    /**
     * @see junit.framework.TestSuite#runTest(junit.framework.Test, junit.framework.TestResult)
     */
    public void runTest(Test arg0, TestResult arg1)
    {
        if(arg0 instanceof JetspeedTest)
        {
        	JetspeedTest jtest = (JetspeedTest) arg0;
        	jtest.engine = engine;
        	jtest.jsuite = this;
        }
        super.runTest(arg0, arg1);
    }

}
