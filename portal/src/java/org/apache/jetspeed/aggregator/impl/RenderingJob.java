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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;

/**
 * The RenderingJob is responsible for storing all necessary objets for
 * asynchronous portlet rendering as well as implementing the rendering logic
 * in its Runnable method.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class RenderingJob implements Runnable
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(RenderingJob.class);

    /** WorkerMonitor used to flush the queue */
    private PortletWindow window = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private ContentDispatcherCtrl dispatcher = null;
    private PortletContainer container = null;

    public void setWindow(PortletWindow window)
    {
        this.window = window;
    }

    public PortletWindow getWindow()
    {
        return this.window;
    }

    public void setRequest(HttpServletRequest request)
    {
        this.request = request;
    }

    public HttpServletRequest getRequest()
    {
        return this.request;
    }

    public void setResponse(HttpServletResponse response)
    {
        this.response = response;
    }

    public HttpServletResponse getResponse()
    {
        return this.response;
    }

    public void setDispatcher(ContentDispatcherCtrl dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    public ContentDispatcherCtrl getDispatcher()
    {
        return this.dispatcher;
    }

    public void setContainer(PortletContainer container)
    {
        this.container = container;
    }

    public PortletContainer getContainer()
    {
        return this.container;
    }

    /**
     * Checks if queue is empty, if not try to empty it by calling
     * the WorkerMonitor. When done, pause until next scheduled scan.
     */
    public void run()
    {
        try
        {
            log.debug("Rendering OID "+this.window.getId()+" "+ this.request +" "+this.response);
            container.renderPortlet(this.window, this.request, this.response);
            log.debug("Notifying dispatcher OID "+this.window.getId());
          
        }
        catch (Throwable t)
        {
            // this will happen is request is prematurely aborted
            log.error("Error rendering portlet OID " + this.window.getId(), t);
			try
            {
                t.printStackTrace(this.response.getWriter());
            }
            catch (IOException e)
            {
                // not important
            }
        }
        finally
        {
			try
            {            	
                this.response.flushBuffer();
                dispatcher.notify(this.window.getId());
            }
            catch (Exception e)
            {
                log.error("Error flushing response buffer: "+e.toString(), e);
            }
        }
    }
}