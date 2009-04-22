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
package org.apache.jetspeed.security.impl;

import java.security.Principal;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;
import org.apache.jetspeed.pipeline.valve.SecurityValve;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JetspeedSubjectFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserSubjectPrincipal;
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
    private static Logger log = LoggerFactory.getLogger(SecurityValveImpl.class);
    
    private UserManager userMgr;
    private PortalStatistics statistics;

    public SecurityValveImpl(Profiler profiler, UserManager userMgr, PortalStatistics statistics, 
                            PortalAuthenticationConfiguration authenticationConfiguration)
    {
        this.userMgr = userMgr;
        this.statistics = statistics;
        this.authenticationConfiguration = authenticationConfiguration;
    }
    
    public SecurityValveImpl( Profiler profiler, UserManager userMgr, PortalStatistics statistics )
    {
        this.userMgr = userMgr;
        this.statistics = statistics;
    }

    public SecurityValveImpl(Profiler profiler, UserManager userMgr)
    {
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
        Principal userPrincipal = getUserPrincipal(request);
        
        Subject subject = getSubjectFromSession(request);
        if (subject != null)
        {
            Principal subjectUserPrincipal = SubjectHelper.getPrincipal(subject, User.class);
            if ((subjectUserPrincipal == null) || !subjectUserPrincipal.getName().equals(userPrincipal.getName()))
            {
                subject = null;
            }
        }
        
        if (subject == null)
        {
            subject = resolveSubjectFromContext(request, userPrincipal);
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
                    subject = userMgr.getSubject(user);                   
                }
            }
            catch (SecurityException sex)
            {
                if (userPrincipal.getName().equals(userMgr.getAnonymousUser()))
                {
                    throw sex;
                }
                else
                {
                    log.error("Unknown user Principal "+userPrincipal.getName()+": creating a default subject without any roles", sex);
                    subject = JetspeedSubjectFactory.createSubject(userMgr.newTransientUser(userPrincipal.getName()), null, null, null);
                }
            }       
        }    
        if(statistics!=null && request.getSessionAttribute(IP_ADDRESS)==null)
        {
                //create a new statistics *user* session
                statistics.logUserLogin(request, 0);
                request.setSessionAttribute(IP_ADDRESS, request.getRequest().getRemoteAddr());
                // put IP address in session for logout
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
            userPrincipal = userMgr.newTransientUser(userMgr.getAnonymousUser());
        }        
        return userPrincipal;
    }
    
    protected Subject resolveSubjectFromContext(RequestContext request, Principal userPrincipal)
    {
        if (userPrincipal.getName().equals(userMgr.getAnonymousUser()))
        {
            return null;
        }
        if (userPrincipal instanceof UserSubjectPrincipal)
        {
            return ((UserSubjectPrincipal)userPrincipal).getSubject();
        }
        return resolveSubjectFromContainerPrincipal(request, userPrincipal);
    }
    
    protected Subject resolveSubjectFromContainerPrincipal(RequestContext request, Principal userPrincipal)
    {
        return null;
    }
}
