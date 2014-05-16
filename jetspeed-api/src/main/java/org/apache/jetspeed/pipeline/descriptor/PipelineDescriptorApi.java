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

package org.apache.jetspeed.pipeline.descriptor;

import java.util.List;


@Deprecated
public interface PipelineDescriptorApi
{
    /**
     * Add a ValveDescriptor to the Pipeline
     * descriptor
     *
     * @param valveDescriptor
     */    
    public void addValveDescriptor(ValveDescriptorApi valveDescriptor);

    /**
     * Return a list of ValveDesccriptors
     *
     * @return List of ValveDesccriptors
     */
    public List getValveDescriptors();

    /**
     * Sets the name attribute
     *
     * @param name the new name value
     */
    public void setName(String name);
    /**
     * Gets the name attribute
     *
     * @return the name attribute 
     */
    public String getName();
    
    /**
     * Sets the id attribute of the BaseDescriptor object
     *
     * @param id the new id value
     */
    public void setId(String id);
    
    /**
     * Gets the id attribute
     *
     * @return the id attribute
     */
    public String getId();
    
}    