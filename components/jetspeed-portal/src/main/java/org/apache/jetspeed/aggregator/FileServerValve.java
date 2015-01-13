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
package org.apache.jetspeed.aggregator;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * FileServerValve
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class FileServerValve extends AbstractValve
{
    private String portletName;
    private String portletEntity;

    public FileServerValve(String portletName, String portletEntity)
    {
        this.portletName = portletName;
        this.portletEntity = portletEntity;
    }
        
    public void invoke( RequestContext request, ValveContext context )
        throws PipelineException
    {
        try
        {
            String entity = request.getRequestParameter(PortalReservedParameters.PORTLET_ENTITY);
            if (entity == null)
            {
                entity = (String)request.getAttribute(PortalReservedParameters.PORTLET_ENTITY);
            }
            if (entity == null)
            {
                request.setAttribute(PortalReservedParameters.PORTLET_ENTITY, portletEntity);
            }        

            String name = request.getRequestParameter(PortalReservedParameters.PORTLET);
            if (name == null)
            {
                name = (String)request.getAttribute(PortalReservedParameters.PORTLET);
            }
            if (name == null)
            {
                request.setAttribute(PortalReservedParameters.PORTLET, portletName);
            }
            request.setAttribute(PortalReservedParameters.PATH_ATTRIBUTE, request.getPortalURL().getPath());
        }
        catch (Exception e)
        {
            throw new PipelineException(e);
        }
        // Pass control to the next Valve in the Pipeline
        context.invokeNext( request );
    }

    public String toString()
    {
        return "FileServerValve";
    }
}
