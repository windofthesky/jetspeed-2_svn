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
package org.apache.jetspeed.pipeline.valve.impl;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ActionValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>
 * ActionValveImpl
 * </p>
 * 
 * Default implementation of the ActionValve interface.  Expects to be
 * called after the ContainerValve has set up the appropriate action window
 * within the request context.  This should come before ANY rendering takes
 * place.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ActionValveImpl extends AbstractValve implements ActionValve
{

    private static final Log log = LogFactory.getLog(ActionValveImpl.class);

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        PortletContainer container;
        boolean responseCommitted = false;
        try
        {
            // TODO: deprecate this when valves are components
            container = (PortletContainer)Jetspeed.getComponentManager().getComponent(PortletContainer.class);
            PortletWindow actionWindow = request.getActionWindow();
            if (actionWindow != null)
            {
                HttpServletResponse response = request.getResponseForWindow(actionWindow);
                container.processPortletAction(
                    actionWindow,
                    request.getRequestForWindow(actionWindow),
                    response);
                // The container redirects the client after PortletAction processing
                // so there is no need to continue the pipeline
                responseCommitted = response.isCommitted();
                request.setAttribute(PortalReservedParameters.PIPELINE, null); // clear the pipeline
            }
            else
            {
                log.info("No action window defined for this request");
            }
        }
        catch (PortletContainerException e)
        {
            log.fatal("Unable to retrieve portlet container!", e);
            throw new PipelineException("Unable to retrieve portlet container!", e);
        }
        catch (PortletException e)
        {
            log.warn("Unexpected PortletException in ActionValveImpl", e);
            //  throw new PipelineException("Unexpected PortletException in ActionValveImpl", e);

        }
        catch (IOException e)
        {
            log.error("Unexpected IOException in ActionValveImpl", e);
            // throw new PipelineException("Unexpected IOException in ActionValveImpl", e);
        }
        finally
        {
            // Check if an action was processed and if its response has been committed
            // (Pluto will redirect the client after PorletAction processing)
            if ( responseCommitted )
            {
                log.info("Action processed and response committed (pipeline processing stopped)");
            }
            else
            {
                // Pass control to the next Valve in the Pipeline
                context.invokeNext(request);
            }
        }

    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        // TODO Auto-generated method stub
        return "ActionValveImpl";
    }

}
