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
import org.apache.jetspeed.request.RequestContext;


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

    public void invoke(RequestContext request, ValveContext context)
        throws PipelineException
    {
        System.out.println("Capability Valve Invoked");

        try
        {
            // TODO: support request based selection of media type instead of using User-Agent header
            //       mediaType = request.getRequest().getQueryString(Profiler.PARAM_MEDIA_TYPE);
      /*      
            String agent = request.getRequest().getHeader("User-Agent");                              
            CapabilityMap cm = CapabilityMapFactory.getCapabilityMap(agent);
                    
            String mediaType = cm.getPreferredMediaType();
                    
            String mimeType = cm.getPreferredType().getCode();
      */      
            String encoding = request.getRequest().getCharacterEncoding();
    /*            
            if (encoding == null)
            {           
                Configuration configuration = Jetspeed.getContext().getConfiguration();
                encoding = configuration.getString(CapabilityMapFactory.CONTENT_ENCODING_KEY, 
                                                   CapabilityMapFactory.DEFAULT_CONTENT_ENCODING_KEY);
                if (mimeType != null)
                {
                
                    MediaTypeEntry media = (MediaTypeEntry) JetspeedRegistry.getEntry(
                            RegistryService.MEDIA_TYPE,
                            mimeType);
                    if (media != null && media.getCharacterSet() != null)
                    {
                        encoding = media.getCharacterSet();
                    }

                }                                                      
            }
        */    
                                                                            
            // Put the encoding in the request
            request.setCharacterEncoding(encoding);
    
            // Put the CapabilityMap into the request
            // request.setCapabilityMap(cm);
            
            // Put the Media Type into the request
            request.setMediaType("HTML");
            
            // Put the Mime Type into the request
            request.setMimeType("text/html");
            request.getResponse().setContentType("text/html");

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
