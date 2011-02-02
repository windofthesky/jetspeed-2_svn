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

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * TestPipelineMapper
 *
 * @version $Id$
 */
public class TestPipelineMapper extends TestCase
{
    private PipelineMapper pipelineMapper;
    private String defaultPipelineId = "jetspeed-pipeline";
    
    @Override
    public void setUp() throws Exception
    {
        Map<String, String> pipelinesMap = new LinkedHashMap<String, String>();
        
        pipelinesMap.put("/portlet", 
                         "portlet-pipeline");
        pipelinesMap.put("/portal", 
                         "jetspeed-pipeline");
        pipelinesMap.put("/login", 
                         "jetspeed-pipeline");
        pipelinesMap.put("/fileserver", 
                         "fileserver-pipeline");
        pipelinesMap.put("/ajaxapi", 
                         "ajax-pipeline");
        pipelinesMap.put("/ajax", 
                         "ajax-direct-pipeline");
        pipelinesMap.put("/services", 
                         "restful-services-pipeline");
        pipelinesMap.put("/configure", 
                         "configure-pipeline");
        pipelinesMap.put("/healthcheck", 
                         "healthcheck-pipeline");
        pipelinesMap.put("/ui", 
                         "jetui-pipeline");
        
        pipelineMapper = new JetspeedPipelineMapper(pipelinesMap);
    }
    
    /**
     * Tests PipelineMapper
     *
     * @throws Exception
     */
    public void testPipelineMapper() throws Exception
    {
        String mappedPathOfDefaultPipeline = pipelineMapper.getMappedPathByPipelineId(defaultPipelineId);
        assertEquals("/portal", mappedPathOfDefaultPipeline);
        
        String [] mappedPaths = pipelineMapper.getMappedPathsByPipelineId(defaultPipelineId);
        assertNotNull(mappedPaths);
        assertEquals(2, mappedPaths.length);
        assertEquals("/portal", mappedPaths[0]);
        assertEquals("/login", mappedPaths[1]);
        
        assertEquals("/portlet", pipelineMapper.getMappedPathByPipelineId("portlet-pipeline"));
        mappedPaths = pipelineMapper.getMappedPathsByPipelineId("portlet-pipeline");
        assertNotNull(mappedPaths);
        assertEquals(1, mappedPaths.length);
        
        assertEquals("/fileserver", pipelineMapper.getMappedPathByPipelineId("fileserver-pipeline"));
        mappedPaths = pipelineMapper.getMappedPathsByPipelineId("fileserver-pipeline");
        assertNotNull(mappedPaths);
        assertEquals(1, mappedPaths.length);
        
        assertNull(pipelineMapper.getMappedPathByPipelineId("nonexisting-pipeline"));
        mappedPaths = pipelineMapper.getMappedPathsByPipelineId("nonexisting-pipeline");
        assertNotNull(mappedPaths);
        assertEquals(0, mappedPaths.length);
    }
    
}
