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

package org.apache.jetspeed.aggregator.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.window.PortletWindow;

/**
 * The RenderingJob is responsible for storing all necessary objets for
 * asynchronous portlet rendering as well as implementing the rendering logic
 * in its Runnable method.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id: RenderingJob.java 188558 2005-04-29 14:02:35Z weaver $
 */
public class RenderingJob implements Runnable
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(RenderingJob.class);

    /** WorkerMonitor used to flush the queue */
    private PortletWindow window = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    
    private PortletContainer container = null;
    private ContentFragment fragment = null;
    private RequestContext requestContext = null;

    private PortletContent portletContent;
    
    public RenderingJob(PortletContainer container, PortletContent portletContent, ContentFragment fragment, HttpServletRequest request, HttpServletResponse response, RequestContext requestContext, PortletWindow window)
    {
        this.container = container;
        
        this.fragment = fragment;
        this.request = request;
        this.response = response;
        this.requestContext = requestContext; 
        this.window = window;
        this.portletContent = portletContent;     
        
    }

    /**
     * Checks if queue is empty, if not try to empty it by calling
     * the WorkerMonitor. When done, pause until next scheduled scan.
     */
    public void run()
    {       
        try
        {
            // A little baby hack to make sure the worker thread has PortletContent to write too.
            fragment.setPortletContent(portletContent);
            execute();                     
        }
        finally
        {
            
            synchronized (portletContent)
            {
               log.debug("Notifying completion of rendering job for fragment " + fragment.getId());                
               portletContent.notifyAll();
            }
        }
    }
    
    /**
     * <p>
     * execute
     * </p>
     *
     * 
     */
    protected void execute()
    {
        
        try
        {           
            log.debug("Rendering OID "+this.window.getId()+" "+ this.request +" "+this.response);            
            this.request.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, fragment);
            this.request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, requestContext.getPage());
            this.request.setAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
          //  this.request.setAttribute(PortalReservedParameters.CONTENT_DISPATCHER_ATTRIBUTE,dispatcher);
            container.renderPortlet(this.window, this.request, this.response);               
            this.response.flushBuffer();                           
        }
        catch (Throwable t)
        {
            // this will happen is request is prematurely aborted            
            log.error("Error rendering portlet OID " + this.window.getId(), t);
            fragment.overrideRenderedContent(t.getMessage());
        }
        finally
        {
            portletContent.complete();
        }

    }

    /**
     * 
     * <p>
     * getWindow
     * </p>
     *
     * @return The window this job is in charge of rendering
     */
    public PortletWindow getWindow()
    {
        return window;
    }
}