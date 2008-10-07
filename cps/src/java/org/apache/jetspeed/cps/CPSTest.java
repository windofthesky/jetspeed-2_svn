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
package org.apache.jetspeed.cps;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;


/**
 * Base class for CPS tests.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.0
 * @version $Id$
 */
public abstract class CPSTest
    extends TestCase
    implements CPSConstants
{
    Configuration configuration = null;
    
    /**
     * Creates a new instance.
     */
    public CPSTest(String testName) 
    {
        super(testName);
    }

    /**
     * Return the Test
     */
    public static Test suite() 
    {
        return new TestSuite(CPSTest.class);
    }

    protected CommonPortletServices cps = null;

    /**
     * Setup the test.
     */
    public void setUp() 
    {
        try
        {
            if (cps != null)
            {
                return;
            }
            String propertiesFilename = getPropertiesFile();
            String applicationRoot = getApplicationRoot();
            configuration = (Configuration) 
                new PropertiesConfiguration(propertiesFilename);
            
            configuration.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
            //properties.setProperty(WEBAPP_ROOT_KEY, null);

            cps = CommonPortletServices.getInstance();
            cps.init(configuration, applicationRoot);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }
   
    /**
     * Override to set your own properties file
     *
     */
    public String getPropertiesFile()
    {
        return getApplicationRoot() + "/WEB-INF/conf/cps.properties";
    }

    public Configuration getConfiguration()
    {
        return this.configuration;
    }
    
    /**
     * Override to set your own application root
     * If the default directory does not exist, then look in
     * the cps directory.  If the directory exist in the cps directory,
     * then return this directory.  Yes this is a hack, but it works.
     */
    public String getApplicationRoot()
    {
        String applicationRoot = "test";
        File testPath = new File(applicationRoot);
        if (!testPath.exists())
        {
            testPath = new File( "cps" + File.separator + applicationRoot);
            if (testPath.exists())
            {
                applicationRoot = testPath.getAbsolutePath();
            }
        }
        return applicationRoot;
    }

    /**
     * Tear down the test.
     */
    public void tearDown() 
    {
        try
        {
            if (cps != null)
            {
                cps.shutdown();
            }
        }
        catch (CPSException e)
        {
            e.printStackTrace();
        }
        finally
        {
            cps = null;
        }
    }

}