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
package org.apache.jetspeed.demo.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * SSODemoServlet - looks for username, password in the URL for single
 * signon to this servlet from a SSO portlet.
 * Username request parameter: ssouser
 * Password request parameter: ssopw
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SSODemoServlet extends HttpServlet
{
    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String user = request.getParameter("ssouser");
        String password = request.getParameter("ssopw");
        response.getWriter().println("User = " + user);
        response.getWriter().println(" PW = " + password);
    }

    public final void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doGet(req, res);
    }
    
}
