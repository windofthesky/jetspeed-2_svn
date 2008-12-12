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
package org.apache.jetspeed.ajax;

import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * This should eventually replace the AJAX ServletFilter.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver </a>
 * 
 */
public class AJAXValve extends AbstractValve
{
    private AJAXService ajaxService;
    private PortletActionSecurityBehavior securityBehavior;
    
    public AJAXValve(AJAXService service, PortletActionSecurityBehavior securityBehavior)
    {
        super();
        this.ajaxService = service;
        this.securityBehavior = securityBehavior;
    }
        
    public void invoke( RequestContext request, ValveContext context )
        throws PipelineException
    {
        HttpServletResponse response = request.getResponse(); 
        try
        {
            response.setContentType("text/xml");  
            if (!securityBehavior.checkAccess(request, "edit"))
            {
                throw new AJAXException("Access Denied.");
            }
            AJAXRequest ajaxRequest = new AJAXRequestImpl(request.getRequest(), response, request.getConfig().getServletContext());
            ajaxService.processRequest(ajaxRequest);
        }
        catch (AJAXException e)
        {
            try
            {
                response.sendError(500, e.getMessage());
            }
            catch (Exception e2)
            {
                throw new PipelineException(e2.getMessage(), e2);
            }
        }
        catch(Exception e)
        {
            throw new PipelineException(e.getMessage(), e);
        }
        
        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);
    }

    public String toString()
    {
        return "AJAXValve";
    }

    
    
}
