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
package org.apache.jetspeed.pipeline.valve.impl;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Stack;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.CleanupValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * CleanupValveImpl
 * </p>
 * 
 * All this valve does right now is look for JSP pages that were
 * pushed onto the <code>org.apache.jetspeed.renderStack</code>
 * request attribute, and attempts to includde them.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class CleanupValveImpl extends AbstractValve implements CleanupValve
{

    public static final String RENDER_STACK_ATTR = "org.apache.jetspeed.renderStack";

    private static final Logger log = LoggerFactory.getLogger(CleanupValveImpl.class);

    private PageManager pageManager;
    
    /**
     * Create cleanup valve with specified page manager component.
     * 
     * @param pageManager active page manager component
     */
    public CleanupValveImpl(PageManager pageManager)
    {
        this.pageManager = pageManager;
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {

        // Complete any renderings that are on the rendering stack 

        // TODO: we should abstract the rendering as we will
        // want to eventually support other types of templates
        // other than JSPs.
        HttpServletRequest httpRequest = request.getRequest();
        Stack renderStack = (Stack) httpRequest.getAttribute(RENDER_STACK_ATTR);
        String fragment = null;
        try
        {
            if (renderStack != null)
            {
                while (!renderStack.empty())
                {
                    fragment = (String) renderStack.pop();
                    RequestDispatcher rd = httpRequest.getRequestDispatcher(fragment);
                    rd.include(httpRequest, request.getResponse());
                }
            }
            // this.dumpSession(request);            
        }
        catch (Exception e)
        {
            log.error("CleanupValveImpl: failed while trying to render fragment " + fragment);
            log.error("CleanupValveImpl: Unable to complete all renderings", e);
        }
        
        // Cleanup PageManager caches per request
        try
        {
            pageManager.cleanupRequestCache();
        }
        catch (Exception e)
        {
            log.error("CleanupValveImpl: Unexpected exception caught", e);
        }
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "CleanupValveImpl";
    }



    public void dumpSession(RequestContext context)
    {
        try
        {
            int count = 0;
            ByteArrayOutputStream bout =  new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream (bout);
            Enumeration e = context.getRequest().getSession().getAttributeNames();
            while (e.hasMoreElements())
            {
                String name = (String)e.nextElement();
                Object o = context.getSessionAttribute(name);
                serializeObject(name, o);
                out.writeObject(o); 
                count++;
            }
            out.close();
            log.info("Session object: " + count);
            log.info("Session footprint: " + bout.size());
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
    
    public void serializeObject(String name, Object o)
    {
        try
        {
            ByteArrayOutputStream bout =  new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream (bout);
            out.writeObject(o);
            out.close();
            log.info("o = " + name + ", " + o + ", size = " + bout.size() );            
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }            
}
