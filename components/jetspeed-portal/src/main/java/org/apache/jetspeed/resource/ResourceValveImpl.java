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
package org.apache.jetspeed.resource;

import java.io.IOException;
import java.util.HashMap;

import javax.portlet.PortletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentFragmentImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletContainerException;
import org.apache.jetspeed.container.PortletWindow;

/**
 * <p>
 * ResourceValveImpl
 * </p>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *
 */
public class ResourceValveImpl extends AbstractValve
{

    private static final Log log = LogFactory.getLog(ResourceValveImpl.class);
    private PortletContainer container;
    private PortletWindowAccessor windowAccessor;

    public ResourceValveImpl(PortletContainer container, PortletWindowAccessor windowAccessor)
    {
        this.container = container;
        this.windowAccessor = windowAccessor;
    }
    
    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {     
        PortletWindow resourceWindow = request.getPortalURL().getNavigationalState().getPortletWindowOfResource();
        
        if ( resourceWindow != null )
        {
            try
            {            
                Page page = request.getPage();
                Fragment fragment = page.getFragmentById(resourceWindow.getId().toString());
                // If portlet entity is null, try to refresh the resourceWindow.
                // Under some clustered environments, a cached portlet window could have null entity.
                if (null == resourceWindow.getPortletEntity())
                {
                    try 
                    {
                        ContentFragment contentFragment = new ContentFragmentImpl(fragment, new HashMap());
                        resourceWindow = this.windowAccessor.getPortletWindow(contentFragment);
                    } 
                    catch (Exception e)
                    {
                        log.error("Failed to refresh resource window.", e);
                    }
                }
                resourceWindow.getPortletEntity().setFragment(fragment);
                HttpServletResponse response = request.getResponse();
                HttpServletRequest requestForWindow = request.getRequestForWindow(resourceWindow);
                requestForWindow.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, request);
                requestForWindow.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, request.getPage());
                requestForWindow.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
                request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_OBJECTS, request.getObjects());                        
                request.setAttribute(PortalReservedParameters.PATH_ATTRIBUTE, request.getAttribute(PortalReservedParameters.PATH_ATTRIBUTE));
                request.setAttribute(PortalReservedParameters.PORTLET_WINDOW_ATTRIBUTE, resourceWindow);
                request.setAttribute(PortalReservedParameters.PORTLET_CONTAINER_INVOKER_USE_FORWARD, Boolean.TRUE);
                if (resourceWindow.getPortletEntity().getPortletDefinition().getApplication().getVersion().equals("1.0"))
                {
                    container.doRender(resourceWindow, requestForWindow, response);
                }
                else
                {
                    container.doServeResource(resourceWindow, requestForWindow, response);
                }
            }
            catch (PortletContainerException e)
            {
                log.fatal("Unable to retrieve portlet container!", e);
                throw new PipelineException("Unable to retrieve portlet container!", e);
            }
            catch (PortletException e)
            {
                log.warn("Unexpected PortletException", e);

            }
            catch (IOException e)
            {
                log.error("Unexpected IOException", e);
            }
            catch (IllegalStateException e)
            {
                log.error("Unexpected IllegalStateException.", e);
            }
            catch (Exception t)
            {
                log.error("Unexpected Exception", t);
            }
        }
        else
        {
            // Pass control to the next Valve in the Pipeline
            context.invokeNext(request);
        }
    }

    public String toString()
    {
        return "ResourceValveImpl";
    }
}
