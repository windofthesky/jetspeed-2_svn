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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;

/**
 * TestPipeline
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestPipeline extends TestCase
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
        assertEquals("CapabilityValveImpl", valves[0].toString());
        assertEquals("PortalURLValveImpl", valves[1].toString());
        assertEquals("SecurityValve", valves[2].toString());
        assertEquals("LocalizationValve", valves[3].toString());
        assertEquals("PasswordCredentialValve", valves[4].toString());
        assertEquals("LoginValidationValve", valves[5].toString());
        assertEquals("ProfilerValve", valves[6].toString());
        assertEquals("DecorationValve", valves[7].toString());
        assertEquals("ContainerValve", valves[8].toString());
        assertEquals("ActionValveImpl", valves[9].toString());
        assertEquals("AggregatorValve", valves[10].toString());
        assertEquals("CleanupValveImpl", valves[11].toString());
        
        
        assertNotNull(engine.getPipeline("action-pipeline"));
        assertNotNull(engine.getPipeline("portlet-pipeline"));
    }

    protected void setUp() throws Exception
    {
        Map context = new HashMap();
        engineHelper = new SpringEngineHelper(context);
        engineHelper.setUp();
        this.engine = (Engine)context.get(SpringEngineHelper.ENGINE_ATTR);
    }

    protected void tearDown() throws Exception
    {
        engineHelper.tearDown();
    }
}
