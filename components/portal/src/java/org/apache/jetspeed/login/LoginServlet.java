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
package org.apache.jetspeed.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;

/**
 * LoginServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id$
 */
public class LoginServlet extends HttpServlet
{
    private static final Log log = LogFactory.getLog(LoginServlet.class);

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession(true);

        if (request.getUserPrincipal() != null)
        {
            String destination = (String) session
                    .getAttribute(LoginConstants.DESTINATION);
            if (destination == null)
                    destination = request.getContextPath() + "/";

            response.sendRedirect(response.encodeURL(destination));
        }

        if (Jetspeed.getEngine() != null)
        {
            request.setAttribute(PortalReservedParameters.PIPELINE, PortalReservedParameters.LOGIN_PIPELINE);
            Engine engine = Jetspeed.getEngine();
            try
            {
                RequestContextComponent contextComponent = (RequestContextComponent) Jetspeed.getComponentManager()
                        .getComponent(RequestContextComponent.class);
                RequestContext context = contextComponent.create(request, response, getServletConfig());
                engine.service(context);
                contextComponent.release(context);
            }
            catch (JetspeedException e)
            {
                log.warn("Jetspeed engine does not work properly.", e);
                // forward to JetspeedServlet 
                response.sendRedirect(response.encodeURL(request.getContextPath() + "/"));
            }
        }
        else
        {
            // forward to JetspeedServlet to create Engine
            response.sendRedirect(response.encodeURL(request.getContextPath() + "/"));
        }
    }

    public final void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }
}
