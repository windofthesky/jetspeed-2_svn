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
package org.apache.jetspeed.security.impl.cas;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.RegistrationException;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.login.filter.PortalRequestWrapper;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.RoleManager;
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
import java.util.List;


public class CASPortalFilter implements Filter
{
    public static String CAS_FILTER_USER = "edu.yale.its.tp.cas.client.filter.user";
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
        
    	HttpServletRequest request = null;
        if (sRequest instanceof HttpServletRequest)
        {
            request = (HttpServletRequest)sRequest;
            ComponentManager cm = Jetspeed.getComponentManager();
            UserManager userManager = cm.lookupComponent("org.apache.jetspeed.security.UserManager");
            HttpSession session = request.getSession(true);
    		String userName = (String) session.getAttribute(CAS_FILTER_USER);	
    		System.out.println("user: " + userName); 
    		
    		RoleManager roleManager = cm.lookupComponent("org.apache.jetspeed.security.RoleManager");
    		GroupManager groupManager = cm.lookupComponent("org.apache.jetspeed.security.GroupManager");
     		
    		User user = null;
    		try {
				user = userManager.getUser(userName);
			} 
    		catch (SecurityException e) 
    		{
				System.out.println("user: " + userName + " not in j2 db"); 
				PortalAdministration portalAdministration = cm.lookupComponent("PortalAdministration");
				try {
					List roles = roleManager.getRoles("user");
					List groups = groupManager.getGroups("");
					portalAdministration.registerUser(userName, portalAdministration.generatePassword());
					
				} catch (RegistrationException e1) {
					// TODO Auto-generated catch block
					System.out.println("user: " + userName + " not created"); 
				} catch (SecurityException e1) {
					// TODO Auto-generated catch block
					System.out.println("user: " + userName + " not created"); 
				}
				
				// initialize the user here
			}
            Subject subject;
			try
			{
				// default solution using the build-in UserManager
				subject = userManager.getSubject(user);
				
				// alternate DIY solution not using the build-in UserManager:
            	//subject = JetspeedSubjectFactory.createSubject(authUser.getUser(),authUser.getPrivateCredentials(),authUser.getPublicCredentials(),null);
			}
			catch (SecurityException e)
			{
            	// TODO: maybe some better handling required here
            	throw new ServletException(e);
			}
            sRequest = wrapperRequest(request, subject, user);
            request.getSession().removeAttribute(LoginConstants.ERRORCODE);
            session.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
            System.out.println("*** login session = " + session);
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
