/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.security.impl;

import java.security.Principal;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.UserManagerService;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.JetspeedUserPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedUserPrincipalImpl;

/**
 * <p>LoginModule implementation that authenticates a user
 * against a relational database. OJB based implementation.</p>
 * <p>When a user is successfully authenticated, the user principal
 * are added to the current subject.</p>
 * <p>The LoginModule also recognizes the debug option.</p>
 * <p>Configuration files should provide:</p>
 * <pre><code>
 * Jetspeed {
 *   org.apache.jetspeed.security.auth.RdbmsLoginModule required debug=true;
 * };
 * </code></pre>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RdbmsLoginModule implements LoginModule
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

    /** <p>JetspeedUserPrincipal manager service.</p> */
    private UserManagerService ums = null;

    /** <p>The user name.</p> */
    private String username;

    /**
     * <p>The default login module constructor.</p>
     */
    public RdbmsLoginModule()
    {
        getUserManagerService();
        debug = false;
        success = false;
        commitSuccess = false;
        username = null;
    }

    /**
     * <p>Returns the {@link UserManagerService}.</p>
     * TODO This should be improved.
     */
    protected void getUserManagerService()
    {
        if (ums == null)
        {
            ums = (UserManagerService) CommonPortletServices.getPortalService(UserManagerService.SERVICE_NAME);
        }
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
                subject.getPrincipals().addAll(ums.getUser(username).getSubject().getPrincipals());

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
                throw new LoginException("Authentication failed: Password does not match");
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

}
