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

/**
 * LoginProxyServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public class LoginProxyServlet extends HttpServlet
{

    public final void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        String parameter;

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

        response.sendRedirect(response.encodeURL(request.getContextPath()
                + "/login/redirector"));
    }

    public final void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }

}
