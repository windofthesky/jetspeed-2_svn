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
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * LoginServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public class LoginServlet extends HttpServlet
{

    public final void doGet(HttpServletRequest request,
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

        PrintWriter out = response.getWriter();

        out.print("<html>");
        out.print("<body onLoad='document.forms[\"login\"].submit();'>");
        out.print("<form id='login' method='POST' action='"
                + response.encodeURL("j_security_check") + "'>");
        out.print("<input type='hidden' name='j_username' value='"
                + session.getAttribute(LoginConstants.USERNAME) + "'>");
        out.print("<input type='hidden' name='j_password' value='"
                + session.getAttribute(LoginConstants.PASSWORD) + "'>");
        out.print("</form>");
        out.print("</body>");
        out.print("</html>");
        out.close();
    }

    public final void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }
}
