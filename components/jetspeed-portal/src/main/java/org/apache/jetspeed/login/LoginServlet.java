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
package org.apache.jetspeed.login;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * LoginServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id$
 */
public class LoginServlet extends HttpServlet
{
    private static final Logger log = LoggerFactory.getLogger(LoginServlet.class);

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession(true);

        if (request.getUserPrincipal() != null)
        {
            String destination = (String) session
                    .getAttribute(LoginConstants.DESTINATION);
            if (destination == null) {
                destination = request.getContextPath() + "/";
            }
            response.sendRedirect(response.encodeURL(destination));
        }

        if (Jetspeed.getEngine() != null)
        {
            request.setAttribute(PortalReservedParameters.PIPELINE, PortalReservedParameters.LOGIN_PIPELINE);
            Engine engine = Jetspeed.getEngine();
            RequestContextComponent contextComponent = null;
            RequestContext context = null;
            try
            {
                String jetuiMode = Jetspeed.getConfiguration().getString(PortalConfigurationConstants.JETUI_CUSTOMIZATION_METHOD, PortalConfigurationConstants.JETUI_CUSTOMIZATION_SERVER);
                boolean redirectHomeSpace = Jetspeed.getConfiguration().getBoolean(PortalConfigurationConstants.JETUI_REDIRECT_HOME_SPACE, true);
                if (redirectHomeSpace && jetuiMode.equals(PortalConfigurationConstants.JETUI_CUSTOMIZATION_AJAX)) {
                    String destination = (String)session.getAttribute(LoginConstants.DESTINATION);
                    if (destination == null) destination = "/";
                    String username = (String)session.getAttribute(LoginConstants.USERNAME);
                    if (username != null) {

                        if (!destination.endsWith("/"))
                            destination += "/";
                        destination += (Folder.RESERVED_USER_FOLDER_NAME + "/" + username);
                        session.setAttribute(LoginConstants.DESTINATION, destination);
                    }
                }
                contextComponent = Jetspeed.getComponentManager().lookupComponent(RequestContextComponent.class);
                context = contextComponent.create(request, response, getServletConfig());
                engine.service(context);
            }            
            catch (JetspeedException e)
            {
                log.warn("Jetspeed engine does not work properly.", e);
                // forward to JetspeedServlet 
                response.sendRedirect(response.encodeURL(request.getContextPath() + "/"));
            }
            finally
            {
                if (contextComponent != null)
                {
                    contextComponent.setRequestContext(null);
                }
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
