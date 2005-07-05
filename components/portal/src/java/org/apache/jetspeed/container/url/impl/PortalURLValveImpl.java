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

package org.apache.jetspeed.container.url.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Creates the PortalURL for the current Request
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: PortalURLValveImpl.java 187758 2004-10-17 14:02:38Z ate $
 */
public class PortalURLValveImpl extends AbstractValve
{
    private static final Log log = LogFactory.getLog(PortalURLValveImpl.class);
    
    public void invoke(RequestContext request, ValveContext context)
        throws PipelineException
    {
        try
        {  
            if ( request.getPortalURL() == null )
            {
                NavigationalStateComponent navComponent = (NavigationalStateComponent)Jetspeed.getComponentManager()
                    .getComponent(NavigationalStateComponent.class);
                request.setPortalURL(navComponent.createURL(request.getRequest(), request.getCharacterEncoding()));
            }
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
        return "PortalURLValveImpl";
    }
}
