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
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * SSOBasicDemoServlet - this will only run in Tomcat 4 and 5
 * where there is a tomcat user name tomcat with the password tomcat
 * and with a role named tomcat.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SSOBasicDemoServlet extends HttpServlet
{
    public final static String DEMO_SSO_PRINCIPAL_PARAM = "sso-principal";
    public final static String DEMO_SSO_CREDENTIAL_PARAM = "sso-credential";
    public final static String DEMO_SSO_CREDENTIAL = "secret-password";
    
    public final void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        String authenticatedPrincipal = "";
        
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null)
        {
            authenticatedPrincipal = "guest";    
        }
        else
        {
            authenticatedPrincipal = userPrincipal.toString();
        }
        
        // create the session
        request.getSession(true);
        
        // authenticated
        response.getWriter().println("<b>Welcome to the Basic Authentication SSO Gateway!</b><br/>");
        response.getWriter().println("Remote Principal has been authenticated.<br/>");
        response.getWriter().println("Remote User  = " + authenticatedPrincipal + "<br/>");
    }

    
    public final void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doGet(req, res);
    }

}
