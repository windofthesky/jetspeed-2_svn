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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.exception.JetspeedException;

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
            engine = Jetspeed.createEngine(properties, applicationRoot, null);

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
        String applicationRoot = System.getProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, "./src/webapp");
        return applicationRoot;
    }

    /**
     * Override to set your own properties file
     *
     */
    public String getPropertiesFile()
    {
        String jetspeedProperties = System.getProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, "./src/webapp") + "/WEB-INF/conf/jetspeed.properties";        
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
