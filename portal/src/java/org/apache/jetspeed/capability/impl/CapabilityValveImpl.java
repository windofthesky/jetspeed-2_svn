/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jetspeed.capability.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.CapabilityValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.capability.CapabilityService;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.capability.MediaType;
import org.apache.jetspeed.capability.MimeType;
import org.apache.jetspeed.cps.CommonPortletServices;

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
    
    protected CapabilityService getService()
    {
        return (CapabilityService) CommonPortletServices.getPortalService(CapabilityService.SERVICE_NAME);
    }

    public void invoke(RequestContext request, ValveContext context)
        throws PipelineException
    {
        try
        {
            String requestMediaType = request.getRequestParameter(ProfilingRule.STANDARD_MEDIATYPE);
            String agent = request.getRequest().getHeader("User-Agent"); 
            
            // Connect to CapabilityService
            CapabilityService service = getService();  
            
            // Get capability map
            CapabilityMap cm = service.getCapabilityMap(agent);
            
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
