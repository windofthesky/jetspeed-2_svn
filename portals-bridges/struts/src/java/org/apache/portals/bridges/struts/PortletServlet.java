/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.portals.bridges.struts;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;

/**
 * PortletServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public class PortletServlet extends ActionServlet
{
    public PortletServlet()
    {
        super();
    }
    public void init(ServletConfig config) throws ServletException
    {
        super.init(new PortletServletConfigImpl(config));
    }
    public ServletContext getServletContext()
    {
        return getServletConfig().getServletContext();
    }
    public boolean performActionRenderRequest(HttpServletRequest request,
            HttpServletResponse response, ActionMapping mapping)
            throws IOException, ServletException
    {
        if (!request.getAttribute(StrutsPortlet.REQUEST_TYPE).equals(
                StrutsPortlet.ACTION_REQUEST))
        {
            StrutsPortletRenderContext context = null;
        	
            String portletName = (String) request.getAttribute(StrutsPortlet.PORTLET_NAME);
        	
        		String contextKey = StrutsPortlet.RENDER_CONTEXT + "_" + portletName;
            context = (StrutsPortletRenderContext) request
                    .getSession(true)
                    .getAttribute(contextKey);
            if (context != null)
            {
                if (log.isDebugEnabled())
                {
                    log.debug("render context path: " + context.getPath());
                }
                request.getSession().removeAttribute(contextKey);
                if (context.getActionForm() != null) {
                	String attribute = mapping.getAttribute();
                	if (attribute != null) {
                	    if (log.isDebugEnabled())
                	    {
                	        log.debug("Putting form " + context.getActionForm().getClass().getName() + 
                	                " into request as " + attribute + " for mapping " + mapping.getName());
                	    }
                    	request.setAttribute(mapping.getAttribute(), context
                                .getActionForm());
                	} 
                	else if (log.isWarnEnabled())
                	{
                	    log.warn("Attribute is null for form " + context.getActionForm().getClass().getName() + 
                	            ", won't put it into request for mapping " + mapping.getName());
                	}
                }
                if (context.isRequestCancelled())
                    request.setAttribute(Globals.CANCEL_KEY, Boolean.TRUE);
                if (context.getMessages() != null)
                    request.setAttribute(Globals.MESSAGE_KEY, context
                            .getMessages());
                if (context.getErrors() != null)
                    request
                            .setAttribute(Globals.ERROR_KEY, context
                                    .getErrors());
                RequestDispatcher dispatcher = null;
                if (context.getDispatchNamed())
                    dispatcher = getServletContext().getNamedDispatcher(
                            context.getPath());
                else
                    dispatcher = getServletContext().getRequestDispatcher(
                            context.getPath());
                dispatcher.include(request, response);
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPortletRequest(ServletRequest request)
    {
        return request.getAttribute("javax.portlet.request") != null;
    }    
}