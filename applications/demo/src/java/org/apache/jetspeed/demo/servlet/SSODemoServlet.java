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
    public final static String DEMO_SSO_PRINCIPAL_PARAM = "sso-principal";
    public final static String DEMO_SSO_CREDENTIAL_PARAM = "sso-credential";
    public final static String DEMO_SSO_CREDENTIAL = "secret-password";
    
    public final void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException
    {
        String principal = request.getParameter(DEMO_SSO_PRINCIPAL_PARAM);
        String credential = request.getParameter(DEMO_SSO_CREDENTIAL_PARAM);
        String authenticatedPrincipal = "007";
        
        /*
         * this is not working on Tomcat 5.0.30
         
        Principal userPrincipal = request.getUserPrincipal();
        if (userPrincipal == null)
        {
            authenticatedPrincipal = "guest";    
        }
        else
        {
            authenticatedPrincipal = userPrincipal.toString();
        }
        */
        if (principal == null)
        {
            error403(request, response, "SSO Principal is not valid. Please provide a valid SSO principal.");
            return;
        }
        
        if (credential == null)
        {
            error403(request, response, "SSO Credential is not valid. Please provide a valid SSO credential.");
            return;
        }
        if (!principal.equals(authenticatedPrincipal))
        {
            error403(request, response, "SSO Principal not found on SSO Server. Please provide a valid SSO principal.");
            return;
        }
        if (!credential.equals(DEMO_SSO_CREDENTIAL))
        {
            error403(request, response, "SSO Credential does not match. Please provide a valid SSO credential.");
            return;
        }

        // authenticated
        response.getWriter().println("<b>Welcome to the SSO Gateway!</b><br/>");
        response.getWriter().println("Remote Principal has been authenticated.<br/>");
        response.getWriter().println("Remote User  = " + authenticatedPrincipal + "<br/>");
    }

    private void error403(HttpServletRequest request, HttpServletResponse response, String message)
    throws IOException, ServletException
    {
        response.getWriter().println("<b>HTTP Status 403: Access to SSO Demo Site not permitted.<br/>");            
        response.getWriter().println(message + "<br/>");
        response.getWriter().println("To configure the SSO Principal, switch to Edit Mode.<br/>");
        return;
        
    }
    
    public final void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doGet(req, res);
    }
    
}
