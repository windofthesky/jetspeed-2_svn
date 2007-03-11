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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.PortalReservedParameters;

/**
 * LoginProxyServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public class LoginProxyServlet extends HttpServlet
{

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        String parameter;

        request.setCharacterEncoding( "UTF-8" );
        
        HttpSession session = request.getSession(true);

        parameter = request.getParameter(LoginConstants.DESTINATION);
        if (parameter != null)
            session.setAttribute(LoginConstants.DESTINATION, parameter);
        else
            session.removeAttribute(LoginConstants.DESTINATION);
        parameter = request.getParameter(LoginConstants.USERNAME);
        if (parameter != null)
            session.setAttribute(LoginConstants.USERNAME, parameter);
        else
            session.removeAttribute(LoginConstants.USERNAME);
        parameter = request.getParameter(LoginConstants.PASSWORD);
        if (parameter != null)
            session.setAttribute(LoginConstants.PASSWORD, parameter);
        else
            session.removeAttribute(LoginConstants.PASSWORD);

        // Globaly override all psml themes
        if (request
                .getParameter(PortalReservedParameters.PAGE_THEME_OVERRIDE_ATTRIBUTE) != null)
        {
            String decoratorName = request
                    .getParameter(PortalReservedParameters.PAGE_THEME_OVERRIDE_ATTRIBUTE);
            session.setAttribute(
                    PortalReservedParameters.PAGE_THEME_OVERRIDE_ATTRIBUTE,
                    decoratorName);
        }

        response.sendRedirect(response.encodeURL(request.getContextPath()
                + "/login/redirector"));
    }

    public final void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }

}
