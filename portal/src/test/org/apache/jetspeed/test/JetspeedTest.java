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

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngineConstants;


/**
 * Tests the Jetspeed Engine.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.0
 * @version $Id$
 */
public abstract class JetspeedTest extends TestCase implements JetspeedEngineConstants
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
        return new JetspeedTestSuite(JetspeedTest.class);
    }

    protected Engine engine = null;
	protected JetspeedTestSuite jsuite;

    /**
     * Setup the test.
     */
    public void setUp() throws Exception
    {
  
        super.setUp();
    }

    /**
     * Override to set your own properties file
     *
     */
//    public String getPropertiesFile()
//    {
//        return jsuite.getPropertiesFile();
//    }

    /**
     * Override to set your own application root
     *
     */
    public String getApplicationRoot()
    {
        return jsuite.getApplicationRoot();
    }

    /**
     * Tear down the test.
     */
    public void tearDown() throws Exception
    {

    }

   



}