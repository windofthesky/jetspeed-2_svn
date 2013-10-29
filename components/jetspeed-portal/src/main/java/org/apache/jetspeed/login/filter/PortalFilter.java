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
package org.apache.jetspeed.login.filter;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.cache.UserContentCacheManager;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.container.session.PortalSessionValidationFilter;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.AuthenticatedUserImpl;
import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

public class PortalFilter implements Filter
{
    protected String guest = "guest";
    
    public void init(FilterConfig filterConfig) throws ServletException
    {
        PortalConfiguration config = Jetspeed.getConfiguration();
        if (config != null)
            guest = config.getString("default.user.principal");                
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
            HttpSession httpSession = PortalSessionValidationFilter.getValidSession(request);
            if (username != null)
            {
                ComponentManager cm = Jetspeed.getComponentManager();
                UserManager userManager = cm.lookupComponent("org.apache.jetspeed.security.UserManager");
                AuditActivity audit = cm.lookupComponent("org.apache.jetspeed.audit.AuditActivity");
                AuthenticationProvider authProvider = cm.lookupComponent("org.apache.jetspeed.security.AuthenticationProvider");
                
                // Commenting out for the using latest securty API's
                //boolean success = userManager.authenticate(username, password);
                //if (success)
                AuthenticatedUser authUser = null;
                try{
                	authUser = authProvider.authenticate(username, password);	
                }
                catch (SecurityException e) 
                {
                    audit.logUserActivity(username, request.getRemoteAddr(), AuditActivity.AUTHENTICATION_FAILURE, "PortalFilter");                    
                    request.getSession().setAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_INVALID_PASSWORD);
				}
                if (authUser != null)
                {
                    audit.logUserActivity(username, request.getRemoteAddr(), AuditActivity.AUTHENTICATION_SUCCESS, "PortalFilter");
                    PortalAuthenticationConfiguration authenticationConfiguration =
                        cm.lookupComponent("org.apache.jetspeed.administration.PortalAuthenticationConfiguration");
                    if (authenticationConfiguration.isCreateNewSessionOnLogin() && httpSession != null && !httpSession.isNew())
                    {
                        request.getSession().invalidate();
                    }
                    else
                    {
                        UserContentCacheManager userContentCacheManager = cm.lookupComponent("userContentCacheManager");
                        userContentCacheManager.evictUserContentCache(username, request.getSession().getId());
                    }
                    if (authUser.getUser() == null)
                    {
                        try
                        {
                            // load the user principals (roles, groups, credentials)
                            User user = userManager.getUser(username);
                            if ( user != null )
                            {
                            	authUser = new AuthenticatedUserImpl(user, authUser.getPublicCredentials(), authUser.getPrivateCredentials() );
                            }
                        }
                        catch (SecurityException sex)
                        {
                        	// TODO: maybe some better handling required here
                        	throw new ServletException(sex);
                        }       
                    }
                    Subject subject;
					try
					{
						// default solution using the build-in UserManager
						subject = userManager.getSubject(authUser);
						
						// alternate DIY solution not using the build-in UserManager:
                    	//subject = JetspeedSubjectFactory.createSubject(authUser.getUser(),authUser.getPrivateCredentials(),authUser.getPublicCredentials(),null);
					}
					catch (SecurityException e)
					{
                    	// TODO: maybe some better handling required here
                    	throw new ServletException(e);
					}
                    sRequest = wrapperRequest(request, subject, authUser.getUser());
                    request.getSession().removeAttribute(LoginConstants.ERRORCODE);
                    HttpSession session = request.getSession(true);
                    session.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
                    //System.out.println("*** login session = " + session);
                }
                else
                {
                    audit.logUserActivity(username, request.getRemoteAddr(), AuditActivity.AUTHENTICATION_FAILURE, "PortalFilter");                    
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
                    Principal principal = SubjectHelper.getPrincipal(subject, User.class);
                    if (principal != null && principal.getName().equals(this.guest))
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
