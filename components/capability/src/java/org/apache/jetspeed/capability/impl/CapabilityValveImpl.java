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

package org.apache.jetspeed.capability.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.CapabilityValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.capability.Capabilities;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.MediaType;
import org.apache.jetspeed.capability.MimeType;

/**
 * Invokes the capability mapper in the request pipeline
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class CapabilityValveImpl implements CapabilityValve
{
    private static final Log log = LogFactory.getLog(CapabilityValveImpl.class);
    String resourceDefault;        // the default name for a resource
    
    /**
      * Initialize the valve before using in a pipeline.
      */
    public void initialize() throws PipelineException
    {

    }
    
    protected Capabilities getComponent()
    {
        return (Capabilities)Jetspeed.getComponentManager().getComponent(Capabilities.class);        
    }

    public void invoke(RequestContext request, ValveContext context)
        throws PipelineException
    {
        try
        {
            String requestMediaType = request.getRequestParameter(ProfilingRule.STANDARD_MEDIATYPE);
            String agent = request.getRequest().getHeader("User-Agent"); 
            
            // Connect to CapabilityService
            Capabilities component = getComponent();  
            
            // Get capability map
            CapabilityMap cm = component.getCapabilityMap(agent);
            
            if ( cm == null)
            {
                log.debug("Couldn't create capability map for agent: " + agent);
            }
            else
            {
                log.debug("Created Capability map for agent: " + agent);
            }
            
            MediaType mediaType = cm.getPreferredMediaType();                          
            MimeType mimeType = cm.getPreferredType();  
            
            String encoding = request.getRequest().getCharacterEncoding();
            
            
            if (encoding == null)
            {
                if (mediaType != null && mediaType.getCharacterSet() != null)
                {
                    encoding = mediaType.getCharacterSet();
                }
            }
            
            if (log.isDebugEnabled())
            {
                log.debug("MediaType: " + mediaType.getName());
                log.debug("Encoding: "  + encoding);
                log.debug("Mimetype: "  + mimeType.getName());
            }
                                                                            
            // Put the encoding in the request
            request.setCharacterEncoding(encoding);
    
            // Put the CapabilityMap into the request
            request.setCapabilityMap(cm);
            
            // Put the Media Type into the request
            request.setMediaType(mediaType.getName());
            
            // Put the Mime Type into the request
            request.setMimeType(mimeType.getName());
            request.getResponse().setContentType(mimeType.getName());

        } 
        catch (Exception e)
        {
            e.printStackTrace();
            throw new PipelineException(e);
        }
        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }

    public String toString()
    {
        return "CapabilityValveImpl";
    }
}
