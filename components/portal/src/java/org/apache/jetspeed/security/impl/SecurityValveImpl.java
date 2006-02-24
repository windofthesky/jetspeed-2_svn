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
package org.apache.jetspeed.security.impl;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.pipeline.valve.SecurityValve;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.statistics.PortalStatistics;

/**
 * SecurityValve
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:rwatler@finali.com">Randy Walter </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class SecurityValveImpl extends AbstractSecurityValve implements SecurityValve
{
    private static final Log log = LogFactory.getLog(SecurityValveImpl.class);
    private Profiler profiler;
    private UserManager userMgr;
    private PortalStatistics statistics;

    public SecurityValveImpl( Profiler profiler, UserManager userMgr, PortalStatistics statistics )
    {
        this.profiler = profiler;
        this.userMgr = userMgr;
        this.statistics = statistics;
    }

    public SecurityValveImpl(Profiler profiler, UserManager userMgr)
    {
        this.profiler = profiler;
        this.userMgr = userMgr;
        this.statistics = null;
    }
    
    public String toString()
    {
        return "SecurityValve";
    }
    
    /**
     * 
     * <p>
     * getSubject
     * </p>
     * Check for previously established session subject and
     * invalidate if subject and current user principals do
     * not match
     * @param request
     * @return 
     * @throws Exception
     */
    protected final Subject getSubject(RequestContext request) throws Exception
    {
        HttpSession session = request.getRequest().getSession();
        Principal userPrincipal = getUserPrincipal(request);
        
        Subject subject = getSubjectFromSession(request);
        if (subject != null)
        {
            Principal subjectUserPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
            if ((subjectUserPrincipal == null) || !subjectUserPrincipal.getName().equals(getUserPrincipal(request).getName()))
            {
                subject = null;
            }
        }
        
        // create new session subject for user principal if required
        if (subject == null)
        {
            // attempt to get complete subject for user principal
            // from user manager
            try
            {
                User user = userMgr.getUser(userPrincipal.getName());
                if ( user != null )
                {
                    subject = user.getSubject();
                }
            }
            catch (SecurityException sex)
            {
                subject = null;
            }       
            
            // if subject not available, generate default subject using
            // request or default profiler anonymous user principal
            if (subject == null)
            {
                Set principals = new HashSet();
                principals.add(userPrincipal);
                subject = new Subject(true, principals, new HashSet(), new HashSet());
            } 
            
            // create a new statistics *user* session
            if (statistics != null)
            {
                statistics.logUserLogin(request, 0);
            }
            // put IP address in session for logout
            request.setSessionAttribute(IP_ADDRESS, request.getRequest().getRemoteAddr());
        }
        
        return subject;
    }
        
    /**
     * 
     * <p>
     * getUserPrincipal
     * </p>
     * Aaccess request user principal if defined or default
     * to profiler anonymous user
     * @param request
     * @return
     */
    protected Principal getUserPrincipal(RequestContext request) throws Exception
    {
        Principal userPrincipal = request.getRequest().getUserPrincipal();
        if (userPrincipal == null)
        {
            userPrincipal = new UserPrincipalImpl(userMgr.getAnonymousUser());
        }
        return userPrincipal;
    }

}
