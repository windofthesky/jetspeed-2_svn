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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
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

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            Profiler profiler = (Profiler) Jetspeed.getComponentManager().getComponent(Profiler.class);
            UserManager userMgr = (UserManager) Jetspeed.getComponentManager().getComponent(UserManager.class);

            Principal principal = request.getRequest().getUserPrincipal();
            Subject subject = (Subject) request.getRequest().getSession().getAttribute(this.getClass().toString() + ".subject");
            if (null == principal)
            {
                principal = new UserPrincipalImpl(profiler.getAnonymousUser());
            }
            if (null == subject)
            {
                Set principals = new HashSet();
                principals.add(principal);
                subject = new Subject(true, principals, new HashSet(), new HashSet());
                request.getRequest().getSession().setAttribute(this.getClass().toString() + ".subject", subject);
            }
            else
            {
                Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
                if ((userPrincipal.getName()).equals(profiler.getAnonymousUser())
                    && (!(principal.getName()).equals(profiler.getAnonymousUser())))
                {
                    subject = userMgr.getUser(principal.getName()).getSubject();
                    request.getRequest().getSession().setAttribute(this.getClass().toString() + ".subject", subject);
                }
            }
            request.setSubject(subject);
        }
        catch (Throwable t)
        {
            // TODO: valve exception handling formalized
            t.printStackTrace();
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);

    }

    public String toString()
    {
        return "SecurityValve";
    }

}
