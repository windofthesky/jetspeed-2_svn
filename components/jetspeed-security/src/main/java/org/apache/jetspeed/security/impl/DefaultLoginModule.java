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

import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.LoginModuleProxy;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.UserSubjectPrincipal;

/**
 * <p>LoginModule implementation that authenticates a user
 * against a relational database. OJB based implementation.</p>
 * <p>When a user is successfully authenticated, the user principal
 * are added to the current subject.</p>
 * <p>The LoginModule also recognizes the debug option.</p>
 * <p>Configuration files should provide:</p>
 * <pre><code>
 * Jetspeed {
 *   org.apache.jetspeed.security.impl.DefaultLoginModule required debug=true;
 * };
 * </code></pre>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class DefaultLoginModule implements LoginModule
{
    private static final Logger log = LoggerFactory.getLogger(DefaultLoginModule.class);
    
    /** <p>LoginModule debug mode is turned off by default.</p> */
    protected boolean debug;

    /** <p>The authentication status.</p> */
    protected boolean success;

    /** <p>The commit status.</p> */
    protected boolean commitSuccess;

    /** <p>The Subject to be authenticated.</p> */
    protected Subject subject;

    /** <p>A CallbackHandler for communicating with the end user (prompting for usernames and passwords, for example).</p> */
    protected CallbackHandler callbackHandler;

    /** <p>State shared with other configured LoginModules.</p> */
    protected Map<String,?> sharedState;

    /** <p>Options specified in the login Configuration for this particular LoginModule.</p> */
    protected Map<String,?> options;

    /** <p>The authentication provider service.</p> */
    protected AuthenticationProvider authProvider;

    /** <p>InternalUserPrincipal manager service.</p> */
    protected UserManager ums;
    
    /** The portal user role. */
    protected String portalUserRole;

    /** <p>The user name.</p> */
    protected String username;
    
    protected AuthenticatedUser user;

    
    /**
     * <p>The default login module constructor.</p>
     */
    public DefaultLoginModule()
    {
        LoginModuleProxy loginModuleProxy = LoginModuleProxyImpl.loginModuleProxy;
        if (loginModuleProxy != null)
        {
            this.authProvider = loginModuleProxy.getAuthenticationProvider();
            this.ums = loginModuleProxy.getUserManager();
            this.portalUserRole = loginModuleProxy.getPortalUserRole();
        }
        debug = false;
        success = false;
        commitSuccess = false;
        username = null;
    }

    
    /**
     * Create a new login module that uses the given user manager.
     * @param userManager the user manager to use
     * @param portalUserRole the portal user role to use
     */
    protected DefaultLoginModule (AuthenticationProvider authProvider, UserManager userManager, String portalUserRole) 
    {
        this.authProvider = authProvider;
        this.ums = userManager;
        this.portalUserRole = portalUserRole;
        debug = false;
        success = false;
        commitSuccess = false;
        username = null;
    }
    protected DefaultLoginModule (AuthenticationProvider authProvider, UserManager userManager) 
    {
        this(authProvider, userManager, LoginModuleProxy.DEFAULT_PORTAL_USER_ROLE_NAME);
    }
    
    /**
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    public boolean abort() throws LoginException
    {
        // Clean out state
        success = false;
        commitSuccess = false;
        username = null;
        if (callbackHandler instanceof PassiveCallbackHandler)
        {
            ((PassiveCallbackHandler) callbackHandler).clearPassword();
        }
        logout();
        return true;
    }

    protected void refreshProxy()
    {
        if (this.ums == null)
        {
            LoginModuleProxy loginModuleProxy = LoginModuleProxyImpl.loginModuleProxy;
            if (loginModuleProxy != null)
            {
                this.authProvider = loginModuleProxy.getAuthenticationProvider();
                this.ums = loginModuleProxy.getUserManager();
            }
        }        
    }
    
    /**
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    public boolean commit() throws LoginException
    {
        if (success)
        {
            if (subject.isReadOnly())
            {
                throw new LoginException("Subject is Readonly");
            }
            try
            {
                // TODO We should get the user profile here and had it in cache so that we do not have to retrieve it again.
                // TODO Ideally the User should be available from the session.  Need discussion around this.
                refreshProxy();
                commitSubject(subject, ums.getSubject(user), user);

                username = null;
                user = null;
                commitSuccess = true;

                if (callbackHandler instanceof PassiveCallbackHandler)
                {
                    ((PassiveCallbackHandler) callbackHandler).clearPassword();
                }

            }
            catch (Exception ex)
            {
                log.error(ex.getMessage(), ex);
                throw new LoginException(ex.getMessage());
            }
        }

        return commitSuccess;
    }

    /**
     * @see javax.security.auth.spi.LoginModule#login()
     */
    public boolean login() throws LoginException
    {
        if (callbackHandler == null)
        {
            throw new LoginException("Error: no CallbackHandler available " + "to garner authentication information from the user");
        }
        try
        {
            // Setup default callback handlers.
            Callback[] callbacks = new Callback[] { new NameCallback("Username: "), new PasswordCallback("Password: ", false)};

            callbackHandler.handle(callbacks);

            username = ((NameCallback) callbacks[0]).getName();
            String password = new String(((PasswordCallback) callbacks[1]).getPassword());

            ((PasswordCallback) callbacks[1]).clearPassword();

            refreshProxy();

            success = false;
            
            try
            {
                user = authProvider.authenticate(this.username, password);
            }
            catch (SecurityException se)
            {
                if (se.getCause() != null)
                {
                    log.error(se.getLocalizedMessage(),se.getCause());
                }
                else
                {
                    log.warn(se.getLocalizedMessage());
                }
                throw new FailedLoginException("Authentication failed");
            }

            success = true;
            callbacks[0] = null;
            callbacks[1] = null;
            
            return (true);
        }
        catch (LoginException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            success = false;
            throw new LoginException(ex.getMessage());
        }
    }

    /**
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    public boolean logout() throws LoginException
    {
        // TODO Can we set subject to null?
        user = null;
        subject.getPrincipals().clear();
        subject.getPrivateCredentials().clear();
        subject.getPublicCredentials().clear();
        success = false;
        commitSuccess = false;

        return true;
    }

    /**
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String,?> sharedState, Map<String,?> options)
    {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        // Initialize debug mode if configure option.
        if (options.containsKey("debug"))
        {
            debug = "true".equalsIgnoreCase((String) options.get("debug"));
        }
    }

    /**
     * Default setup of the logged on Subject Principals for Tomcat
     * @param subject
     * @param user
     */
    protected void commitSubject(Subject containerSubject, Subject jetspeedSubject, AuthenticatedUser user)
    {
        // add user specific portal user name and roles
        subject.getPrincipals().add(SubjectHelper.getPrincipal(jetspeedSubject, UserSubjectPrincipal.class));
        subject.getPrincipals().add(SubjectHelper.getPrincipal(jetspeedSubject, User.class));
        boolean hasPortalUserRole = false;
        for (Principal role : SubjectHelper.getPrincipals(jetspeedSubject, Role.class))
        {
            subject.getPrincipals().add(role);
            if (role.getName().equals(portalUserRole))
            {
                hasPortalUserRole = true;
            }
        }
        if (!hasPortalUserRole)
        {
            // add portal user role: used in web.xml authorization to
            // detect authenticated portal users
            subject.getPrincipals().add(new RoleImpl(portalUserRole));        
        }
    }
}
