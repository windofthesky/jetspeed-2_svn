/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.exception.JetspeedException;

/**
 * Tests the Jetspeed Engine.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.0
 * @version $Id$
 */
public abstract class JetspeedTest
    extends TestCase
    implements JetspeedEngineConstants
{
    /**
     * Creates a new instance.
     */
    public JetspeedTest(String testName) 
    {
        super(testName);
    }

    /**
     * Return the Test
     */
    public static Test suite() 
    {
        return new TestSuite(JetspeedTest.class);
    }

    protected Engine engine = null;

    /**
     * Setup the test.
     */
    public void setUp() 
    {
        try
        {
            if (engine != null)
            {
                return;
            }
            String propertiesFilename = getPropertiesFile();
            String applicationRoot = getApplicationRoot();
            Configuration properties = (Configuration) 
                new PropertiesConfiguration(propertiesFilename);
            
            properties.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
            //properties.setProperty(WEBAPP_ROOT_KEY, null);
            overrideProperties(properties);
            engine = Jetspeed.createEngine(properties, applicationRoot, null);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue("Failed to setup JetspeedTest", false);
        }
    }

   
    /**
     * Override to set your own properties file
     *
     */
    public String getPropertiesFile()
    {
        return "./src/webapp/WEB-INF/conf/jetspeed.properties";
    }

    /**
     * Override to set your own application root
     *
     */
    public String getApplicationRoot()
    {
        return "./src/webapp";
    }

    /*
     * Implement this method to override any properties in your test.
     * If you override this method in a derived class, call super.overrideProperties to get these settings
     * 
     * @param properties The base configuration properties for the Jetspeed system.
     */
    public void overrideProperties(Configuration properties)
    {        
        String testPropsPath = getApplicationRoot() + "/WEB-INF/conf/test/jetspeed.properties";
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
                    Entry entry = (Entry)it.next();
                    //if (entry.getValue() != null && ((String)entry.getValue()).length() > 0)
                    properties.setProperty((String)entry.getKey(), (String)entry.getValue());                    
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            assertTrue("Failed to read Test-specific properties", false);            
        }
    }
    
    /**
     * Tear down the test.
     */
    public void tearDown() 
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

}