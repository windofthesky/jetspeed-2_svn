/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;

/**
 * TestPipeline
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestPipeline extends JetspeedTestCase
{
     private Engine engine;
    private SpringEngineHelper engineHelper;

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
        HashMap valvesMap = new HashMap(valves.length);
        for (int i = 0; i < valves.length; i++)
        {
            valvesMap.put(valves[i].toString(), valves[i]);
        }
        assertNotNull("CapabilityValveImpl", valvesMap.get("CapabilityValveImpl"));
        assertNotNull("PortalURLValveImpl", valvesMap.get("PortalURLValveImpl"));
        assertNotNull("SecurityValve", valvesMap.get("SecurityValve"));
        assertNotNull("LocalizationValve", valvesMap.get("LocalizationValve"));
        assertNotNull("PasswordCredentialValve", valvesMap.get("PasswordCredentialValve"));
        assertNotNull("LoginValidationValve", valvesMap.get("LoginValidationValve"));
        assertNotNull("ProfilerValve", valvesMap.get("ProfilerValve"));
        assertNotNull("ContainerValve", valvesMap.get("ContainerValve"));
        assertNotNull("ActionValveImpl", valvesMap.get("ActionValveImpl"));
        assertNotNull("ResourceValveImpl", valvesMap.get("ResourceValveImpl"));
        assertNotNull("DecorationValve", valvesMap.get("DecorationValve"));
        assertNotNull("HeaderAggregatorValve", valvesMap.get("HeaderAggregatorValve"));
        assertNotNull("AggregatorValve", valvesMap.get("AggregatorValve"));
        Valve[] cleanupValves = pipeline.getCleanupValves();
        HashMap cleanupValvesMap = new HashMap(cleanupValves.length);
        for (int i = 0; i < cleanupValves.length; i++)
        {
            cleanupValvesMap.put(cleanupValves[i].toString(), cleanupValves[i]);
        }
        assertNotNull("CleanupValveImpl", cleanupValvesMap.get("CleanupValveImpl"));
        
        assertNotNull(engine.getPipeline("action-pipeline"));
        assertNotNull(engine.getPipeline("portlet-pipeline"));
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        Map context = new HashMap();
        engineHelper = new SpringEngineHelper(context);
        engineHelper.setUp(getBaseDir());
        this.engine = (Engine)context.get(SpringEngineHelper.ENGINE_ATTR);
    }

    protected void tearDown() throws Exception
    {
        engineHelper.tearDown();
        super.tearDown();
    }
}
