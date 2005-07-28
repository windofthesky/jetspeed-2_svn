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
package org.apache.portals.bridges.struts;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ActionConfig;

/**
 * PortletServletRequestDispatcher
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PortletServletRequestDispatcher implements RequestDispatcher
{
    private static final Log log = LogFactory.getLog(PortletServletRequestDispatcher.class);
    private RequestDispatcher dispatcher;
    private String path;
    private boolean named;
    
    public PortletServletRequestDispatcher(RequestDispatcher dispatcher,
            String path, boolean named)
    {
        this.dispatcher = dispatcher;
        this.path = path;
        this.named = named;
    }
    
    private void invoke(ServletRequest request, ServletResponse response,
            boolean include) throws ServletException, IOException
    {
        String request_type = (String) request
                .getAttribute(StrutsPortlet.REQUEST_TYPE);
        if (request_type != null
                && request_type.equals(StrutsPortlet.ACTION_REQUEST))
        {
            if (log.isDebugEnabled())
            {
                log.debug("saving " + (named ? "named " : " ")
                        + "dispatch to :" + path + ", from "
                        + request_type + " "
                        + StrutsPortletURL.getPageURL(request));
            }
            HttpServletRequest req = (HttpServletRequest) request;
            StrutsPortletRenderContext context = new StrutsPortletRenderContext();
            context.setPath(path);
            context.setDispatchNamed(named);
            ActionConfig actionConfig = (ActionConfig) request
                    .getAttribute(Globals.MAPPING_KEY);
            if (actionConfig != null)
            {
                if (actionConfig.getAttribute() != null
                        && actionConfig.getScope().equals("request"))
                {
                    ActionForm actionForm = (ActionForm) request
                            .getAttribute(actionConfig.getAttribute());
                    context.setActionForm(actionForm);
                    Boolean requestCancelled = (Boolean) request
                            .getAttribute(Globals.CANCEL_KEY);
                    if (requestCancelled != null
                            && requestCancelled.booleanValue())
                        context.setRequestCancelled(true);
                }
            }
            context.setMessages((ActionMessages) request
                    .getAttribute(Globals.MESSAGE_KEY));
            context.setErrors((ActionMessages) request
                    .getAttribute(Globals.ERROR_KEY));
            if (context.getErrors() != null)
            {
                String originURL = StrutsPortletURL.getOriginURL(request);
                if (originURL != null)
                {
                    request.setAttribute(StrutsPortlet.REDIRECT_PAGE_URL,
                            originURL);
                }
            }
            String portletName = (String) req.getAttribute(StrutsPortlet.PORTLET_NAME);
            try
            {
                req.getSession(true).setAttribute(StrutsPortlet.RENDER_CONTEXT + "_" + portletName, context);
            }
            catch (IllegalStateException ise)
            {
                // catch Session already invalidated Exception
                if (log.isDebugEnabled())
                {
                    log.debug("Session invalidated: redirecting to: "+path+" instead.");
                }
                ((HttpServletResponse)response).sendRedirect(path);
            }
        } 
        else
        {
            if (log.isDebugEnabled())
            {
                log.debug("invoking " + (named ? "named " : " ")
                        + " dispatch to :" + path + ", from "
                        + request_type + " "
                        + StrutsPortletURL.getPageURL(request));
            }
            dispatcher.include(request, response);
        }
    }

    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if ( PortletServlet.isPortletRequest(request) )
        {
            invoke(request, response, false);
        }
        else
        {
            dispatcher.forward(request,response);
        }
    }

    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if ( PortletServlet.isPortletRequest(request) )
        {
            invoke(request, response, true);
        }
        else
        {
            dispatcher.include(request,response);
        }
    }

    public String toString() {
        return dispatcher.toString();
    }
}
