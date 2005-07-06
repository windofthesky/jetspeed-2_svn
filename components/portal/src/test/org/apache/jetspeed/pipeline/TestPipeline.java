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
package org.apache.jetspeed.pipeline;

import junit.framework.Test;

import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * TestPipeline
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestPipeline extends JetspeedTest
{

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestPipeline(String name)
    {
        super(name);
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestPipeline.class.getName()});
    }

    public void setup()
    {
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new JetspeedTestSuite(TestPipeline.class);
    }

    /**
     * Tests
     *
     * @throws Exception
     */
    public void testPipeline() throws Exception
    {
        assertNotNull(engine);
        Pipeline pipeline = engine.getPipeline();
        assertNotNull(pipeline);
        Valve[] valves = pipeline.getValves();
        assertTrue(valves[0].toString().equals("LocalizationValve"));
        assertTrue(valves[1].toString().equals("CapabilityValveImpl"));
        assertTrue(valves[2].toString().equals("PortalURLValveImpl"));     
        assertTrue(valves[3].toString().equals("SecurityValve"));
        assertTrue(valves[4].toString().equals("PasswordCredentialValve"));                
        assertTrue(valves[5].toString().equals("LoginValidationValve"));                
        assertTrue(valves[6].toString().equals("ProfilerValve"));        
        assertTrue(valves[7].toString().equals("ContainerValve"));
        assertTrue(valves[8].toString().equals("ActionValveImpl"));     
        assertTrue(valves[9].toString().equals("AggregatorValve"));
        assertTrue(valves[10].toString().equals("CleanupValveImpl"));
        assertNotNull(engine.getPipeline("action-pipeline"));
        assertNotNull(engine.getPipeline("portlet-pipeline"));
    }
}
