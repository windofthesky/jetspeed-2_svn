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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.ComponentManager;

import java.util.HashMap;
import java.util.Map;


/**
 * JetspeedPipelineMapper
 * @version $Id$
 */
public class JetspeedPipelineMapper implements PipelineMapper
{
    
    private Map<String, String> pipelineNamesMap;
    private Map<String, String[]> pipelineIdPathsMap;
    
    public JetspeedPipelineMapper(Map<String, String> pipelineNamesMap) 
    {
        this.pipelineNamesMap = pipelineNamesMap;
        
        pipelineIdPathsMap = new HashMap<String, String[]>();
        
        for (Map.Entry<String, String> entry : this.pipelineNamesMap.entrySet())
        {
            String path = entry.getKey();
            String pipelineId = entry.getValue();
            
            if (!pipelineIdPathsMap.containsKey(pipelineId))
            {
                pipelineIdPathsMap.put(pipelineId, new String[] { path });
            }
            else
            {
                String [] paths = pipelineIdPathsMap.get(pipelineId);
                String [] mappedPaths = new String[paths.length + 1];
                System.arraycopy(paths, 0, mappedPaths, 0, paths.length);
                mappedPaths[mappedPaths.length - 1] = path;
                pipelineIdPathsMap.put(pipelineId, mappedPaths);
            }
        }
    }
    
    public Pipeline getPipelineByMappedPath(String mappedPath)
    {
        if (pipelineNamesMap == null)
        {
            return null;
        }
        
        String pipelineId = pipelineNamesMap.get(mappedPath);
        
        if (pipelineId != null)
        {
            return getPipelineById(pipelineId);
        }
        
        return null;
    }
    
    public Pipeline getPipelineById(String pipelineId)
    {
        ComponentManager componentManager = Jetspeed.getComponentManager();
        
        if (componentManager == null)
        {
            return null;
        }
        
        return componentManager.lookupComponent(pipelineId);
    }
    
    public String getMappedPathByPipelineId(String pipelineId)
    {
        if (pipelineIdPathsMap == null)
        {
            return null;
        }
        
        String [] paths = pipelineIdPathsMap.get(pipelineId);
        
        if (paths == null)
        {
            return null;
        }
        
        return (paths.length > 0 ? paths[0] : null);
    }
    
    public String[] getMappedPathsByPipelineId(String pipelineId)
    {
        if (pipelineIdPathsMap == null)
        {
            return null;
        }
        
        String [] paths = pipelineIdPathsMap.get(pipelineId);
        
        if (paths == null)
        {
            return new String[0];
        }
        
        String [] mappedPaths = new String[paths.length];
        System.arraycopy(paths, 0, mappedPaths, 0, paths.length);
        return mappedPaths;
    }
    
}