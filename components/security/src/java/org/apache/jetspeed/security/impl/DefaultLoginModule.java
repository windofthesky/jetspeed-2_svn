/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.jetspeed.security.LoginModuleProxy;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;

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

    /** <p>LoginModule debug mode is turned off by default.</p> */
    private boolean debug;

    /** <p>The authentication status.</p> */
    private boolean success;

    /** <p>The commit status.</p> */
    private boolean commitSuccess;

    /** <p>The Subject to be authenticated.</p> */
    private Subject subject;

    /** <p>A CallbackHandler for communicating with the end user (prompting for usernames and passwords, for example).</p> */
    private CallbackHandler callbackHandler;

    /** <p>State shared with other configured LoginModules.</p> */
    private Map sharedState;

    /** <p>Options specified in the login Configuration for this particular LoginModule.</p> */
    private Map options;

    /** <p>InternalUserPrincipal manager service.</p> */
    private UserManager ums;

    /** <p>The user name.</p> */
    private String username;

    /**
     * <p>The default login module constructor.</p>
     */
    public DefaultLoginModule()
    {
        LoginModuleProxy loginModuleProxy = LoginModuleProxyImpl.loginModuleProxy;
        this.ums = loginModuleProxy.getUserManager();
        debug = false;
        success = false;
        commitSuccess = false;
        username = null;
    }

    /**
     * Create a new login module that uses the given user manager.
     * @param userManager the user manager to use
     */
    protected DefaultLoginModule (UserManager userManager) {
        ums = userManager;
        debug = false;
        success = false;
        commitSuccess = false;
        username = null;
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
                commitPrincipals(subject, ums.getUser(username));

                username = null;
                commitSuccess = true;

                if (callbackHandler instanceof PassiveCallbackHandler)
                {
                    ((PassiveCallbackHandler) callbackHandler).clearPassword();
                }

            }
            catch (Exception ex)
            {
                ex.printStackTrace(System.out);
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

            success = ums.authenticate(this.username, password);

            callbacks[0] = null;
            callbacks[1] = null;
            if (!success)
            {
                throw new FailedLoginException("Authentication failed: Password does not match");
            }

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
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
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

    
    protected Principal getUserPrincipal(User user)
    {
        return SecurityHelper.getPrincipal(user.getSubject(),UserPrincipal.class);
    }
    
    protected List getUserRoles(User user)
    {
        return SecurityHelper.getPrincipals(user.getSubject(),RolePrincipal.class);
    }
    
    /**
     * Default setup of the logged on Subject Principals for Tomcat
     * @param subject
     * @param user
     */
    protected void commitPrincipals(Subject subject, User user)
    {
        subject.getPrincipals().add(getUserPrincipal(user));
        subject.getPrincipals().addAll(getUserRoles(user));
    }
}
