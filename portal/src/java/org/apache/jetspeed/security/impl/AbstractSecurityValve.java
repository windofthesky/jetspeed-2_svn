/*
 * Created on Nov 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.security.impl;

import java.security.Principal;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.SecurityValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

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
     * <code>Subject.doAsPrivileged()</code>.  This method also takes care of setting the value of
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
            request.getRequest().setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
            // set request context subject
            request.setSubject(subject);
            
            // Pass control to the next Valve in the Pipeline and execute under
            // the current subject
            final ValveContext vc = context;
            final RequestContext rc = request;            
            PipelineException pe = (PipelineException) Subject.doAsPrivileged(subject, new PrivilegedAction()
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
}
