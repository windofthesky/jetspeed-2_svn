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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * LoginValidationValve
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class LoginValidationValveImpl extends AbstractValve implements org.apache.jetspeed.pipeline.valve.LoginValidationValve
{
    private static final Log log = LogFactory.getLog(LoginValidationValveImpl.class);
    
    // This value should be in sync with that of InternalPasswordCredentialStateHandlingInterceptor (if used)
    // to make any sense.
    // Providing value < 2 will disable the LoginConstants.ERROR_FINAL_LOGIN_ATTEMPT warning 
    private int maxNumberOfAuthenticationFailures;
    
    public LoginValidationValveImpl(int maxNumberOfAuthenticationFailures)
    {
        this.maxNumberOfAuthenticationFailures = maxNumberOfAuthenticationFailures;
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            if ( request.getRequest().getUserPrincipal() == null )
            {
                if ( request.getSessionAttribute(LoginConstants.RETRYCOUNT) != null )
                {
                    // we have a login attempt failure
                    String userName = (String)request.getSessionAttribute(LoginConstants.USERNAME);
                    if ( userName != null )
                    {
                        UserManager um = (UserManager)Jetspeed.getComponentManager().getComponent(UserManager.class);
                        if ( um != null )
                        {
                            User user = null;
                            try
                            {
                                user = um.getUser(userName);
                                UserPrincipal userPrincipal = (UserPrincipal)SecurityHelper.getPrincipal(user.getSubject(), UserPrincipal.class);
                                if ( !userPrincipal.isEnabled() )
                                {
                                    request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_USER_DISABLED);
                                }
                                else
                                {
                                    PasswordCredential pwdCredential = SecurityHelper.getPasswordCredential(user.getSubject());
                                    if ( pwdCredential == null || !pwdCredential.isEnabled() )
                                    {
                                        request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_CREDENTIAL_DISABLED);
                                    }
                                    else if ( pwdCredential.isExpired() )
                                    {
                                        request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_CREDENTIAL_EXPIRED);
                                    }
                                    else if ( maxNumberOfAuthenticationFailures > 1 && pwdCredential.getAuthenticationFailures() == maxNumberOfAuthenticationFailures -1  )
                                    {
                                        request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_FINAL_LOGIN_ATTEMPT);
                                    }
                                    else
                                    {
                                        request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_INVALID_PASSWORD);
                                    }
                                }
                            }
                            catch (SecurityException sex)
                            {
                                request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_UNKNOWN_USER);
                            }
                        }
                    }
                    else
                    {
                        request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_UNKNOWN_USER);
                    }
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
        return "LoginValidationValve";
    }

}
