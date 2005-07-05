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

package org.apache.jetspeed.capabilities.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.MimeType;
import org.apache.jetspeed.capabilities.UnableToBuildCapabilityMapException;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.CapabilityValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;

/**
 * Invokes the capability mapper in the request pipeline
 * 
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann </a>
 * @version $Id: CapabilityValveImpl.java 188143 2005-01-04 16:06:21Z weaver $
 */
public class CapabilityValveImpl implements CapabilityValve
{
    private static final Log log = LogFactory.getLog(CapabilityValveImpl.class);
    String resourceDefault; // the default name for a resource
    private Capabilities capabilities;

    public CapabilityValveImpl( Capabilities capabilities )
    {
        this.capabilities = capabilities;
    }

    /**
     * Initialize the valve before using in a pipeline.
     */
    public void initialize() throws PipelineException
    {

    }

    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {

        String requestMediaType = request.getRequestParameter(ProfilingRule.STANDARD_MEDIATYPE);
        String agent = request.getRequest().getHeader("User-Agent");

        // Get capability map
        CapabilityMap cm;
        try
        {
            cm = capabilities.getCapabilityMap(agent);
        }
        catch (UnableToBuildCapabilityMapException e)
        {
           throw new PipelineException("Falied to create capabilitied:  "+e.getMessage(), e);
        }
        
        MediaType mediaType = cm.getPreferredMediaType();
        MimeType mimeType = cm.getPreferredType();

        if (mediaType == null)
        {
            log.error("CapabilityMap returned a null media type");
            throw new PipelineException("CapabilityMap returned a null media type");
        }

        if (mimeType == null)
        {
            log.error("CapabilityMap returned a null mime type");
            throw new PipelineException("CapabilityMap returned a null mime type");
        }

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
            log.debug("Encoding: " + encoding);
            log.debug("Mimetype: " + mimeType.getName());
        }

        // Put the encoding in the request
        request.setCharacterEncoding(encoding);

        // Put the CapabilityMap into the request
        request.setCapabilityMap(cm);

        // Put the Media Type into the request
        request.setMediaType(mediaType.getName());

        // Put the Mime Type into the request
        request.setMimeType(mimeType.getName());

        // Put the Mime Type and Charset into the response
        StringBuffer contentType = new StringBuffer(mimeType.getName());
        if (encoding != null)
        {
            contentType.append("; charset=" + encoding);
        }
        request.getResponse().setContentType(contentType.toString());

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }

    public String toString()
    {
        return "CapabilityValveImpl";
    }
}
