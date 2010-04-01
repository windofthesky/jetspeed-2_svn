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

import java.util.Arrays;
import java.util.List;

import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.UserCredential;

/**
 * PasswordCredentialValve
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PasswordCredentialValveImpl extends AbstractValve implements org.apache.jetspeed.pipeline.valve.PasswordCredentialValve
{
    private static final Logger log = LoggerFactory.getLogger(PasswordCredentialValveImpl.class);
    
    private static final String CHECKED_KEY = PasswordCredentialValveImpl.class.getName() + ".checked";
    //private PageManager pageManager;
    private int[] expirationWarningDays;
    
    private String passwordResetPage = "/my-account.psml";
    /**
     * Create a PasswordCredentialValveImpl which only checks and handles PasswordCredential.isUpdateRequired().
     *
     */
    public PasswordCredentialValveImpl()
    {     
        expirationWarningDays = new int[]{};
    }

    public PasswordCredentialValveImpl(String passwordResetPage)
    {     
    	this();
        this.passwordResetPage = passwordResetPage;        
    }

    public PasswordCredentialValveImpl(List expirationWarningDays, String passwordResetPage)
    {     
    	this(expirationWarningDays);
        this.passwordResetPage = passwordResetPage;        
    }
    
    /**
     * <p>
     * Creates a PasswordCredentialValveImpl which, besides checking and handling PasswordCredential.isUpdateRequired(),
     * also provides a warning when a password is about to be expired according to the provided list of
     * expirationWarningDays.</p>
     * @param expirationWarningDays the list of days before password expiration when a warning should be presented 
     */
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
            if ( request.getRequest().getUserPrincipal() != null )
            {
                Subject subject = request.getSubject();
                UserCredential userCredential = SubjectHelper.getUserCredential(subject);
                Integer passwordDaysValid = null;
                
                // check for an existing password credential
                if ( userCredential != null )
                {
                    if ( userCredential.isUpdateRequired() )
                    {
                        passwordDaysValid = new Integer(0); // required change
                    }
                    if ( request.getSessionAttribute(CHECKED_KEY) == null  )
                    {
                        request.setSessionAttribute(CHECKED_KEY,Boolean.TRUE);
                        if ( userCredential.getPreviousAuthenticationDate() != null && 
                                userCredential.getLastAuthenticationDate() != null &&
                                userCredential.getExpirationDate() != null )
                        {
                            long expirationTime = userCredential.getExpirationDate().getTime();
                            long lastAuthTime = userCredential.getLastAuthenticationDate().getTime();
                            int lastAuthDaysBeforeExpiration = (int)((expirationTime-lastAuthTime)/(24*60*60*1000));
                            if (  lastAuthDaysBeforeExpiration < 1 )
                            {
                                passwordDaysValid = new Integer(1);
                            }
                            else if (expirationWarningDays.length > 0)
                            {
                                long prevAuthTime = Long.MIN_VALUE;
                                if (userCredential.getPreviousAuthenticationDate() != null )
                                {
                                    prevAuthTime = userCredential.getPreviousAuthenticationDate().getTime();
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
                if (passwordDaysValid != null)
                {
                	if (Jetspeed.getConfiguration().getString(PortalConfigurationConstants.JETUI_CUSTOMIZATION_METHOD).equals(PortalConfigurationConstants.JETUI_CUSTOMIZATION_SERVER))
                	{                	
	                    // enforce the SECURITY_LOCATOR to be used to redirect to a change password portlet page
	                    request.setAttribute(PageProfilerValve.PROFILE_LOCATOR_REQUEST_ATTR_KEY,ProfileLocator.SECURITY_LOCATOR);
                	}
                	else
                	{
                		request.setAttribute(PortalReservedParameters.PATH_ATTRIBUTE, passwordResetPage); 
                	}
                    // inform the change password portlet why it is invoked
                    request.setAttribute(PasswordCredential.PASSWORD_CREDENTIAL_DAYS_VALID_REQUEST_ATTR_KEY, passwordDaysValid);
                }
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
