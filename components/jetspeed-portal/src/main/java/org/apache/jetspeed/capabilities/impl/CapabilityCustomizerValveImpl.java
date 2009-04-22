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

package org.apache.jetspeed.capabilities.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.Client;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Invokes the capability customizer in the request pipeline
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 */
public class CapabilityCustomizerValveImpl extends AbstractValve
{

    private static final Logger log = LoggerFactory.getLogger(CapabilityCustomizerValveImpl.class);

    private Capabilities capabilities;
    private Map clientToMediaTypeMap;

    public CapabilityCustomizerValveImpl( Capabilities capabilities, Map clientToMediaTypeMap )
    {
        this.capabilities = capabilities;
        this.clientToMediaTypeMap = clientToMediaTypeMap;
    }

    /**
     * Initialize the valve before using in a pipeline.
     */
    public void initialize() throws PipelineException
    {

    }

    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        CapabilityMap cm = request.getCapabilityMap();

        if (cm != null && this.clientToMediaTypeMap != null)
        {
            Client client = cm.getClient();
            String mediaTypeName = (String) this.clientToMediaTypeMap.get(client.getName());
            
            if (mediaTypeName != null)
            {
                MediaType mediaType = this.capabilities.getMediaType(mediaTypeName);
                cm.setPreferredMediaType(mediaType);
                request.setMediaType(mediaTypeName);
            }
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }

    public String toString()
    {
        return "CapabilityCustomizerValveImpl";
    }
}
