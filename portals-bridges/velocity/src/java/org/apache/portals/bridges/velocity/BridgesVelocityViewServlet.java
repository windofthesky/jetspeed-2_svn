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
package org.apache.portals.bridges.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.apache.velocity.util.SimplePool;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class BridgesVelocityViewServlet extends VelocityViewServlet
{
    public final static String PORTLET_REQUEST = "javax.portlet.request";
    public final static String PORTLET_RESPONSE = "javax.portlet.response";
    public final static String PORTLET_CONFIG = "javax.portlet.config";
	
	public static final String VELOCITY_WRITER_ATTR = "org.apache.velocity.io.VelocityWriter";
    /** Cache of writers */
    private static SimplePool writerPool = new SimplePool(40);
	
    public static final String VELOCITY_CONTEXT_ATTR = "org.apache.velocity.Context";
    /**
     * Adds the RenderRequest, RenderResponse and PortletConfig to the context
     * 
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.velocity.context.Context)
     */
    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) throws Exception
    {
        PortletRequest renderRequest = (PortletRequest) request.getAttribute(PORTLET_REQUEST);
        RenderResponse renderResponse = (RenderResponse) request.getAttribute(PORTLET_RESPONSE);
        PortletConfig portletConfig = (PortletConfig) request.getAttribute(PORTLET_CONFIG);
        
        if (renderRequest != null)
        {
            renderRequest.setAttribute(VELOCITY_CONTEXT_ATTR, ctx);
        }

        // standard render request and response also available in context
        ctx.put(PORTLET_REQUEST, renderRequest);
        ctx.put(PORTLET_RESPONSE, renderResponse);
        
        return super.handleRequest(request, response, ctx);
    }

    /**
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet#mergeTemplate(org.apache.velocity.Template, org.apache.velocity.context.Context, javax.servlet.http.HttpServletResponse)
     */
    protected void mergeTemplate(Template template, Context context, HttpServletResponse response)
        throws
            ResourceNotFoundException,
            ParseErrorException,
            MethodInvocationException,
            IOException,
            UnsupportedEncodingException,
            Exception
    {
        PrintWriter pw = response.getWriter();
        VelocityWriter vw = null;

        try
        {
            vw = (VelocityWriter) writerPool.get();

            if (vw == null)
            {
                vw = new VelocityWriter(pw, 4 * 1024, true);
            }
            else
            {
                vw.recycle(pw);
            }
			
			// Place the VelocityWriter into the Context
			context.put(VELOCITY_WRITER_ATTR, vw);
            template.merge(context, vw);
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            try
            {
                if (vw != null)
                {
                    // flush and put back into the pool
                    // don't close to allow us to play
                    // nicely with others.
                    vw.flush();
                    /* This hack sets the VelocityWriter's internal ref to the 
                     * PrintWriter to null to keep memory free while
                     * the writer is pooled. See bug report #18951 */
                    vw.recycle(null);
                    writerPool.put(vw);
                }
            }
            catch (Exception e)
            {
                // do nothing
            }
        }
    }
}
