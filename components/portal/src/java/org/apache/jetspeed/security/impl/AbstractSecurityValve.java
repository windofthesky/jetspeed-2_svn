/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security.impl;

import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.SecurityValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JSSubject;

/**
 * <p>
 * AbstractSecurityValve
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class AbstractSecurityValve extends AbstractValve implements SecurityValve
{
    protected PortalAuthenticationConfiguration authenticationConfiguration = null;
    
    /**
     * 
     * <p>
     * getSubject
     * </p>
     *  Should build and return a <code>javax.security.Subject</code>
     * @param request
     * @return Subject
     */
    protected abstract Subject getSubject(RequestContext request) throws Exception;
    
    /**
     * 
     * <p>
     * getUserPrincipal
     * </p>
     * Should build and return a <code>java.security.Principal</code> that represents the user name
     * the Subject returned from <code>getSubject()</code> 
     * @param request
     * @return Principal
     * @throws Exception
     */
    protected abstract Principal getUserPrincipal(RequestContext request) throws Exception;
        
    /**
     * 
     * <p>
     * getSubjectFromSession
     * </p>
     * 
     * @param request
     * @return javax.security.Subject or <code>null</code> if there is no servlet session attribute defined
     * for the key <code>org.apache.jetspeed.PortalReservedParameters.SESSION_KEY_SUBJECT</code>.
     */
    protected final Subject getSubjectFromSession(RequestContext request) throws Exception
    {
        return (Subject) request.getRequest().getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
    }

    /**
     * <p>
     * invoke
     * </p>
     * 
     * <p>
     * Uses <code>getSubject()</code> to call <code>ValveContext.invokeNext()</code> via 
     * <code>JSSubjectdoAsPrivileged()</code>.  This method also takes care of setting the value of
     * the <code>RequestContext.subject</code> property and the session attribute 
     * <code>org.apache.jetspeed.PortalReservedParameters.SESSION_KEY_SUBJECT</code>
     * </p>
     *
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     * @param request
     * @param context
     * @throws PipelineException if the is an error encountered during any security operations.
     */
    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        if (isSessionExpired(request))
        {
            return; // short circuit processing and redirect
        }
    
        // initialize/validate security subject
        Subject subject;
        try
        {
            subject = getSubject(request);
        }
        catch (Exception e1)
        {
           throw new PipelineException(e1.getMessage(), e1);
        }
        request.getRequest().getSession().setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);            
        
        // set request context subject
        request.setSubject(subject);
        
        // Pass control to the next Valve in the Pipeline and execute under
        // the current subject
        final ValveContext vc = context;
        final RequestContext rc = request;            
        PipelineException pe = (PipelineException) JSSubject.doAsPrivileged(subject, new PrivilegedAction()
        {
            public Object run() 
            {
                 try
                {
                    vc.invokeNext(rc);                 
                    return null;
                }
                catch (PipelineException e)
                {
                    return e;
                }                    
            }
        }, null);
        
        if(pe != null)
        {
            throw pe;
        }           
    }
    
    /**
     * Check for hard limit session expiration time out
     * 
     * @param request
     * @return
     * @throws PipelineException
     */
    protected boolean isSessionExpired(RequestContext request) throws PipelineException    
    {
        if (authenticationConfiguration != null && authenticationConfiguration.isMaxSessionHardLimitEnabled())
        {
            HttpSession session = request.getRequest().getSession();
            long sessionCreationTime = session.getCreationTime();
            long currentTime = System.currentTimeMillis();
            if ((currentTime - sessionCreationTime) > authenticationConfiguration.getMsMaxSessionHardLimit())
            {
                session.invalidate();
                String redirector = request.getRequest().getContextPath() + authenticationConfiguration.getTimeoutRedirectLocation();
                // System.out.println("logging user out " + redirector + ", " + (currentTime - sessionCreationTime) + ", " + this.msMaxSessionHardLimit);
                try
                {
                    request.getResponse().sendRedirect(redirector);
                }
                catch (IOException e)
                {
                    throw new PipelineException(e);
                }
                return true;
            }
            else
            {
                // System.out.println("Not logging user out: " + (currentTime - sessionCreationTime) + ", " + this.msMaxSessionHardLimit);
            }
        }
        return false;        
    }
    
}
