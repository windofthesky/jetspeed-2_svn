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
 * LoginErrorServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $id: $
 */
public class LoginErrorServlet extends HttpServlet
{

    public final void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        String destination = (String) session
                .getAttribute(LoginConstants.DESTINATION);
        if (destination == null)
            destination = request.getContextPath() + "/";
        else
            session.removeAttribute(LoginConstants.DESTINATION);

        Integer retryCount = (Integer) session
                .getAttribute(LoginConstants.RETRYCOUNT);
        if (retryCount == null)
            retryCount = new Integer(1);
        else
            retryCount = new Integer(retryCount.intValue() + 1);
        session.setAttribute(LoginConstants.RETRYCOUNT, retryCount);

        response.sendRedirect(response.encodeURL(destination));
    }

    public final void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }
}
