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
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * SecurityValve
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SecurityValveImpl extends AbstractValve implements org.apache.jetspeed.pipeline.valve.SecurityValve
{
    private static final Log log = LogFactory.getLog(SecurityValveImpl.class);
    private Profiler profiler;
    private UserManager userMgr;
    
    public SecurityValveImpl(Profiler profiler, UserManager userMgr)
    {
        this.profiler = profiler;
        this.userMgr = userMgr;
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            // initialize/validate security subject

            // access request user principal if defined or default
            // to profiler anonymous user
            Principal userPrincipal = request.getRequest().getUserPrincipal();
            if (userPrincipal == null)
            {
                userPrincipal = new UserPrincipalImpl(userMgr.getAnonymousUser());
            }

            // check for previously established session subject and
            // invalidate if subject and current user principals do
            // not match
            HttpSession session = request.getRequest().getSession();
            Subject subject = (Subject) session.getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
            if (subject != null)
            {
                Principal subjectUserPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
                if ((subjectUserPrincipal == null) || !subjectUserPrincipal.getName().equals(userPrincipal.getName()))
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

                // establish session subject
                session.setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
            }

            // set request context subject
            request.setSubject(subject);
            
            // Pass control to the next Valve in the Pipeline and execute under the current subject
            final ValveContext vc = context;
            final RequestContext rc = request;            
            Subject.doAsPrivileged(subject, new PrivilegedAction()
            {
                public Object run()
                {
                    try 
                    {
                        vc.invokeNext(rc);
                    }
                    catch (PipelineException e)
                    {                        
                    }
                    return null;                    
                }
            }, null);
            
        }
        catch (Throwable t)
        {
            // TODO: valve exception handling formalized
            t.printStackTrace();
        }

    }

    public String toString()
    {
        return "SecurityValve";
    }

}
