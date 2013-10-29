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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;
import org.apache.jetspeed.security.activeauthentication.ActiveAuthenticationIdentityProvider;
import org.apache.jetspeed.security.activeauthentication.IdentityToken;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * LoginProxyServlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public class LoginProxyServlet extends HttpServlet
{
    private boolean credentialsFromRequest = true;
    
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        String s = config.getInitParameter("credentialsFromRequest");
        if (s != null)
        {
            credentialsFromRequest = s.equalsIgnoreCase("true");
        }
    }

    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        String parameter;
        String username;
        request.setCharacterEncoding( "UTF-8" );
                
        HttpSession session = request.getSession(true);

        parameter = request.getParameter(LoginConstants.DESTINATION);
        if (parameter != null)
        {
            parameter = StringEscapeUtils.escapeHtml(parameter);
            session.setAttribute(LoginConstants.DESTINATION, parameter);
        }
        else
            session.removeAttribute(LoginConstants.DESTINATION);
        if (credentialsFromRequest)
        {
            username = request.getParameter(LoginConstants.USERNAME);
            if (username != null)
            {
                username = StringEscapeUtils.escapeHtml(username);
                session.setAttribute(LoginConstants.USERNAME, username);
            }
            else
                session.removeAttribute(LoginConstants.USERNAME);
            parameter = request.getParameter(LoginConstants.PASSWORD);
            if (parameter != null)
            {
                parameter = StringEscapeUtils.escapeHtml(parameter);
                session.setAttribute(LoginConstants.PASSWORD, parameter);
            }
            else
                session.removeAttribute(LoginConstants.PASSWORD);
        }
        else
        {
            username = (String)session.getAttribute(LoginConstants.USERNAME);
            parameter = (String)session.getAttribute(LoginConstants.PASSWORD);            
        }
        
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

        Boolean portalFiltered = Boolean.valueOf((String)request.getAttribute(PortalReservedParameters.PORTAL_FILTER_ATTRIBUTE));
        PortalAuthenticationConfiguration authenticationConfiguration =
            Jetspeed.getComponentManager().lookupComponent("org.apache.jetspeed.administration.PortalAuthenticationConfiguration");
        if (!portalFiltered && authenticationConfiguration.isCreateNewSessionOnLogin())
        {
    
            ActiveAuthenticationIdentityProvider identityProvider =
                Jetspeed.getComponentManager().lookupComponent("org.apache.jetspeed.security.activeauthentication.ActiveAuthenticationIdentityProvider");
            IdentityToken token = identityProvider.createIdentityToken(username);
            saveState(session, token, identityProvider.getSessionAttributeNames());
            request.getSession().invalidate();
            HttpSession newSession = request.getSession(true);
            restoreState(newSession, token);
            response.sendRedirect(response.encodeURL(request.getContextPath()
                    + "/login/redirector?token=") + token.getToken());
            
        }
        else
        {
            response.sendRedirect(response.encodeURL(request.getContextPath()
                    + "/login/redirector"));
        }
    }

    protected void saveState(HttpSession session, IdentityToken token, List sessionAttributes)
    {
        Iterator sessionNames = sessionAttributes.iterator();
        while (sessionNames.hasNext())
        {
            String name = (String)sessionNames.next();
            token.setAttribute(name, session.getAttribute(name));
        }
    }

    protected void restoreState(HttpSession session, IdentityToken token)
    {
        Iterator names = token.getAttributeNames();
        while (names.hasNext())
        {
            String name = (String)names.next();
            Object attribute = token.getAttribute(name);
            session.setAttribute(name, attribute);
        }        
    }
    
    public final void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException
    {
        doGet(request, response);
    }

}
