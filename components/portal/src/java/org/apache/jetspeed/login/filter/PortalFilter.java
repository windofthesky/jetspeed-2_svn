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
package org.apache.jetspeed.login.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PrincipalsSet;
import org.apache.jetspeed.security.impl.UserSubjectPrincipalImpl;

public class PortalFilter implements Filter
{
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    public void doFilter(ServletRequest sRequest,
            ServletResponse sResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        if (sRequest instanceof HttpServletRequest)
        {
            HttpServletRequest request = (HttpServletRequest)sRequest;
            String username = request.getParameter(LoginConstants.USERNAME);
            String password = request.getParameter(LoginConstants.PASSWORD);            
            if (username != null)
            {
                UserManager userManager = (UserManager)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.security.UserManager");
                boolean success = userManager.authenticate(username, password);
                if (success)
                {
                    Set principals = new PrincipalsSet();
                    Subject subject = new Subject(true, principals, new HashSet(), new HashSet());
                    UserPrincipal userPrincipal = new UserSubjectPrincipalImpl(username, subject);
                    principals.add(userPrincipal);
                    sRequest = wrapperRequest((HttpServletRequest)request, userPrincipal);
                    request.getSession().removeAttribute(LoginConstants.ERRORCODE);
                    HttpSession session = request.getSession(true);
                    session.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
                    //System.out.println("*** login session = " + session);
                }
                else
                {
                    request.getSession().setAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_INVALID_PASSWORD);                    
                }
            }
            else
            {
                //HttpSession session = request.getSession();
                //System.out.println("*** session = " + session);
                Subject subject = (Subject)request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
                if (subject != null)
                {
                    Principal principal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
                    if (principal != null && principal.getName().equals("guest"))
                    {                        
                    }
                    else
                    {
                        sRequest = wrapperRequest((HttpServletRequest)request, principal);
                    }
                }                
            }              

            sRequest.setAttribute(PortalReservedParameters.PORTAL_FILTER_ATTRIBUTE, "true");
        }
        
        if (filterChain != null)
        {
            filterChain.doFilter(sRequest, sResponse);
        }
    }

    private ServletRequest wrapperRequest(HttpServletRequest request, Principal principal)
    {
        PortalRequestWrapper wrapper = new PortalRequestWrapper(request, principal);
        return wrapper;
    }

    public void destroy()
    {
    }
}
