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


/**
 * Jetspeed PipelineMapper
 *
 * @version $Id$
 */
public interface PipelineMapper
{
    
    /**
     * Returns pipeline mapped by the specified path.
     * @param mappedPath
     * @return
     */
    Pipeline getPipelineByMappedPath(String mappedPath);
    
    /**
     * Returns pipeline by the pipeline component ID. 
     * @param id
     * @return
     */
    Pipeline getPipelineById(String id);
    
    /**
     * Returns mapped base path found first for the pipeline ID.
     * If nothing found, returns null.
     * @param pipelineId
     * @return
     */
    String getMappedPathByPipelineId(String pipelineId);
    
    /**
     * Returns all mapped base paths for the pipeline ID.
     * If nothing found, returns an empty array.
     * @param pipelineId
     * @return
     */
    String [] getMappedPathsByPipelineId(String pipelineId);
    
}