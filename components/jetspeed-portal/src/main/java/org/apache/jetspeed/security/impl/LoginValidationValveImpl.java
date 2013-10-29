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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.login.LoginConstants;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserCredential;
import org.apache.jetspeed.security.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * LoginValidationValve
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class LoginValidationValveImpl extends AbstractValve implements org.apache.jetspeed.pipeline.valve.LoginValidationValve
{
    private static final Logger log = LoggerFactory.getLogger(LoginValidationValveImpl.class);
    
    private int maxNumberOfAuthenticationFailures;
    private List sessionAttributes; 
    
    /**
     * Creates a LoginValidationValveImpl instance which doesn't evaluate the maxNumberOfAuthenticationFailures 
     * for LoginConstant.ERROR_FINAL_LOGIN_ATTEMPT error reporting.
     */
    public LoginValidationValveImpl(List sessionAttributes)
    {
        this.sessionAttributes = sessionAttributes;
    }

    /**
     * <p>
     * Creates a LoginValidationValveImpl instance which can evaluate {@link PasswordCredential#getAuthenticationFailures()}
     * to determine if a user only has one login attempt available before the maxNumberOfAuthenticationFailures parameter
     * value is reached and the credential will be disabled.</p>
     * <p>
     * The provided maxNumberOfAuthenticationFailures value should be equal to the value configured for the
     * MaxPasswordAuthenticationFailuresInterceptor (and > 2 to be useful).</p>
     */
    public LoginValidationValveImpl(int maxNumberOfAuthenticationFailures)
    {
        this.maxNumberOfAuthenticationFailures = maxNumberOfAuthenticationFailures;
        this.sessionAttributes = new LinkedList();
    }

    /**
     * <p>
     * Creates a LoginValidationValveImpl instance which can evaluate {@link PasswordCredential#getAuthenticationFailures()}
     * to determine if a user only has one login attempt available before the maxNumberOfAuthenticationFailures parameter
     * value is reached and the credential will be disabled.</p>
     * <p>
     * The provided maxNumberOfAuthenticationFailures value should be equal to the value configured for the
     * MaxPasswordAuthenticationFailuresInterceptor (and > 2 to be useful).</p>
     */
    public LoginValidationValveImpl(int maxNumberOfAuthenticationFailures, List sessionAttributes)
    {
        this.maxNumberOfAuthenticationFailures = maxNumberOfAuthenticationFailures;
        this.sessionAttributes = sessionAttributes;
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
                    if ( userName != null && !userName.equals(""))
                    {
                        UserManager um = Jetspeed.getComponentManager().lookupComponent(UserManager.class);
                        if ( um != null )
                        {
                            User user = null;
                            try
                            {
                                user = um.getUser(userName);
                                if ( !user.isEnabled() )
                                {
                                    request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_USER_DISABLED);
                                }
                                else
                                {
                                    UserCredential userCredential = SubjectHelper.getUserCredential(um.getSubject(user));
                                    if ( userCredential == null || !userCredential.isEnabled() )
                                    {
                                        request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_CREDENTIAL_DISABLED);
                                    }
                                    else if ( userCredential.isExpired() )
                                    {
                                        request.setSessionAttribute(LoginConstants.ERRORCODE, LoginConstants.ERROR_CREDENTIAL_EXPIRED);
                                    }
                                    else if ( maxNumberOfAuthenticationFailures > 1 && userCredential.getAuthenticationFailures() == maxNumberOfAuthenticationFailures -1  )
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
            else
            {
                if (request.getSessionAttribute(LoginConstants.LOGIN_CHECK) == null)
                {
                    clearSessionAttributes(request);
                    request.getRequest().getSession().setAttribute(LoginConstants.LOGIN_CHECK, "true");
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
    
    private void clearSessionAttributes(RequestContext request)
    {       
        Iterator attributes = this.sessionAttributes.iterator();
        while (attributes.hasNext())
        {
            String attribute = (String)attributes.next();
            request.getRequest().getSession().removeAttribute(attribute);
        }
    }

    public String toString()
    {
        return "LoginValidationValve";
    }

}
