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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PasswordCredential;

/**
 * SecurityValve
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PasswordCredentialValveImpl extends AbstractValve implements org.apache.jetspeed.pipeline.valve.PasswordCredentialValve
{
    private static final Log log = LogFactory.getLog(PasswordCredentialValveImpl.class);
    
    private static final String CHECKED_KEY = PasswordCredentialValveImpl.class.getName() + ".checked";
    //private PageManager pageManager;
    private int[] expirationWarningDays;
    
    public PasswordCredentialValveImpl(List expirationWarningDays)
    {
        if ( expirationWarningDays != null )
        {
            this.expirationWarningDays = new int[expirationWarningDays.size()];
            for ( int i = 0; i < this.expirationWarningDays.length; i++ )
            {
                this.expirationWarningDays[i] = Integer.parseInt((String)expirationWarningDays.get(i));
            }
            Arrays.sort(this.expirationWarningDays);
        }
        else
        {
            this.expirationWarningDays = new int[0];
        }
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            Subject subject = request.getSubject();
            Iterator credentialsIter = subject.getPrivateCredentials().iterator();
            PasswordCredential pwdCredential = null;
            while ( credentialsIter.hasNext() )
            {
                Object credential = credentialsIter.next();
                if ( credential instanceof PasswordCredential )
                {
                    pwdCredential = (PasswordCredential)credential;
                    break;
                }
            }
            Integer passwordDaysValid = null;
            
            // check for an existing password credential
            // The only expected subject without a password credential is the anonymous user!
            if ( pwdCredential != null )
            {
                if ( pwdCredential.isUpdateRequired() )
                {
                    passwordDaysValid = new Integer(0); // required change
                }
                if ( request.getSessionAttribute(CHECKED_KEY) == null  )
                {
                    request.setSessionAttribute(CHECKED_KEY,Boolean.TRUE);
                    if ( pwdCredential.getPreviousAuthenticationDate() != null )
                    {
                        long expirationTime = pwdCredential.getExpirationDate().getTime();
                        long lastAuthTime = pwdCredential.getLastAuthenticationDate().getTime();
                        int lastAuthDaysBeforeExpiration = (int)((expirationTime-lastAuthTime)/(24*60*60*1000));
                        if (  lastAuthDaysBeforeExpiration < 1 )
                        {
                            passwordDaysValid = new Integer(1);
                        }
                        else if (expirationWarningDays.length > 0)
                        {
                            long prevAuthTime = Long.MIN_VALUE;
                            if (pwdCredential.getPreviousAuthenticationDate() != null )
                            {
                                prevAuthTime = pwdCredential.getPreviousAuthenticationDate().getTime();
                            }
                            int prevAuthDaysBeforeExpiration = (int)((expirationTime-prevAuthTime)/(24*60*60*1000));
                            if ( prevAuthDaysBeforeExpiration > lastAuthDaysBeforeExpiration )
                            {
                                for ( int i = 0; i < expirationWarningDays.length; i++ )
                                {
                                    int daysBefore = expirationWarningDays[i]-1;
                                    if ( lastAuthDaysBeforeExpiration == daysBefore ||
                                            (lastAuthDaysBeforeExpiration < daysBefore &&
                                                    prevAuthDaysBeforeExpiration > daysBefore ) )
                                    {
                                        passwordDaysValid = new Integer(lastAuthDaysBeforeExpiration+1);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if ( passwordDaysValid != null )
            {
                // enforce the SECURITY_LOCATOR to be used to redirect to a change password portlet page
                request.setAttribute(PageProfilerValve.PROFILE_LOCATOR_REQUEST_ATTR_KEY,ProfileLocator.SECURITY_LOCATOR);
                // inform the change password portlet why it is invoked
                request.setAttribute(PasswordCredential.PASSWORD_CREDENTIAL_DAYS_VALID_REQUEST_ATTR_KEY, passwordDaysValid);
            }
            context.invokeNext(request);
        }
        catch (Exception e)
        {
            log.error("Exception in request pipeline: " + e.getMessage(), e);
            throw new PipelineException(e.toString(), e);
        }
    }

    public String toString()
    {
        return "PasswordCredentialValve";
    }

}
