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
package org.apache.jetspeed.security.impl.shibboleth;

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
import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.login.filter.PortalRequestWrapper;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PrincipalsSet;
import org.apache.jetspeed.security.impl.UserSubjectPrincipalImpl;

public class ShibbolethPortalFilter implements Filter
{
    protected String userNameHeader;
    protected Object sem = new Object();
       
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
            if (userNameHeader == null)
            {
                synchronized (sem)
                {
                    ShibbolethConfiguration config = (ShibbolethConfiguration)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");
                    userNameHeader = config.getHeaderMapping().get(ShibbolethConfiguration.USERNAME);
                }
            }
            String username = request.getHeader(userNameHeader);            
            if (username != null)
            {
                Subject subject = (Subject)request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
                if (subject != null)
                {
                    Principal principal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
                    if (principal != null)
                    {
                        if (principal.getName().equals(username))
                        {
                            sRequest = wrapperRequest(request, subject, principal);
                            if (filterChain != null)
                            {
                                filterChain.doFilter(sRequest, sResponse);
                                return;
                            }
                        }
                    }
                }
                UserManager userManager = (UserManager)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.security.UserManager");
                AuditActivity audit = (AuditActivity)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.audit.AuditActivity");
                ShibbolethConfiguration config = (ShibbolethConfiguration)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");
                boolean success = false;
                if (config.isAuthenticate())
                {
                    success = userManager.authenticate(username, username); // TODO: this is bogus, need to login with a real password.
                }
                else
                {
                    try
                    {
                        // load the user principals (roles, groups, credentials)
                        User user = userManager.getUser(username);
                        if (user != null)
                        {
                            subject = user.getSubject();
                        }
                        success = true;
                    }
                    catch (SecurityException sex)
                    {
                        success = false;
                    }                           
                }
                if (success)
                {
                    audit.logUserActivity(username, request.getRemoteAddr(), AuditActivity.AUTHENTICATION_SUCCESS, "ShibbolethFilter");
                    PortalAuthenticationConfiguration authenticationConfiguration = (PortalAuthenticationConfiguration)
                        Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.administration.PortalAuthenticationConfiguration");
                    if (authenticationConfiguration.isCreateNewSessionOnLogin())
                    {
                        request.getSession().invalidate();
                    }
                    subject = null;
                    try
                    {
                        // load the user principals (roles, groups, credentials)
                        User user = userManager.getUser(username);
                        if ( user != null )
                        {
                            subject = user.getSubject();
                        }
                    }
                    catch (SecurityException sex)
                    {
                    }       
                    if (subject == null)
                    {
                        Set principals = new PrincipalsSet();
                        UserSubjectPrincipalImpl userPrincipal = new UserSubjectPrincipalImpl(username);
                        principals.add(userPrincipal);
                        subject = new Subject(true, principals, new HashSet(), new HashSet());
                        userPrincipal.setSubject(subject);
                    }
                    Principal principal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
                    sRequest = wrapperRequest(request, subject, principal);
                    request.getSession().removeAttribute(LoginConstants.ERRORCODE);
                    HttpSession session = request.getSession(true);
                    session.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
                }
                else
                {
                    audit.logUserActivity(username, request.getRemoteAddr(), AuditActivity.AUTHENTICATION_FAILURE, "ShibbolethFilter");                    
                    request.getSession().setAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_INVALID_PASSWORD);                    
                }
            }
            else
            {
                Subject subject = (Subject)request.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
                if (subject != null)
                {
                    Principal principal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
                    ShibbolethConfiguration config = (ShibbolethConfiguration)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.security.shibboleth.ShibbolethConfiguration");                    
                    if (principal != null && principal.getName().equals(config.getGuestUser()))
                    {                        
                    }
                    else
                    {
                        sRequest = wrapperRequest(request, subject, principal);
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
    
    private ServletRequest wrapperRequest(HttpServletRequest request, Subject subject, Principal principal)
    {
        PortalRequestWrapper wrapper = new PortalRequestWrapper(request, subject, principal);
        return wrapper;
    }

    public void destroy()
    {
    }
}
