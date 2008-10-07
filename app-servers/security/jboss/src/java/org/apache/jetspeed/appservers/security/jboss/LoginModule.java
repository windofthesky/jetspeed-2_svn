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
package org.apache.jetspeed.appservers.security.jboss;

import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.ext.JBossLoginModule;

/**
 * A login module that uses the JetspeedSecurityService MBean for authentication and role assignment.
 */
public class LoginModule implements javax.security.auth.spi.LoginModule
{

    private String securityService = null;

    private JBossLoginModule delegee = null;

    /**
     * Helper for delaying the creation of the JBossLoginModule. We cannot access the security service MBean before
     * <code>initialize</code> has been called, but need the user manager in the constructor of JBossLoginModule. The
     * constructor that takes the user manager as argument is protected (and right so), so we need this helper.
     */
    private class LoginModuleDelegee extends JBossLoginModule
    {
        public LoginModuleDelegee(UserManager userManager)
        {
            super(userManager);
        }
    }

    /**
     * Create a new login module. The module looks up the JetspeedSecurityService MBean and uses it to actually perform
     * authentication and role lookup.
     * <P>
     * Note that the MBean must be available when this login module is instantiated. Therefore, if the MBean (the SAR)
     * is deployed after JBoss has been started, this login module must be created lazily by using the JBoss login
     * module proxy in login-config.xml.
     * 
     * <pre>
     *  &lt;application-policy name = &quot;sample&quot;&gt;
     *    &lt;authentication&gt;
     *      &lt;login-module code = &quot;org.jboss.security.auth.spi.ProxyLoginModule&quot;
     *        flag = &quot;required&quot;&gt;
     *        &lt;module-option name = &quot;moduleName&quot;&gt;
     *          org.apache.jetspeed.appservers.security.jboss.LoginModule
     *        &lt;/module-option&gt;
     *        &lt;!-- The name of the security service MBean. Must match
     *             the name in jboss-service.xml --&gt;
     *        &lt;module-option name = &quot;securityService&quot;&gt;
     *          org.apache.jetspeed:service=JetspeedSecurityService         
     *        &lt;/module-option&gt;
     *      &lt;/login-module&gt;
     *    &lt;/authentication&gt;
     *  &lt;/application-policy&gt;
     * </pre>
     */
    public LoginModule()
    {
    }

    private UserManager getUserManager()
    {
        try
        {
            MBeanServer server = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
            ObjectName serviceName = new ObjectName(securityService);
            return (UserManager) server.invoke(serviceName, "getUserManager", null, null);
        }
        catch (MalformedObjectNameException e)
        {
            throw (IllegalStateException) ((new IllegalStateException(e.getMessage())).initCause(e));
        }
        catch (InstanceNotFoundException e)
        {
            throw (IllegalStateException) ((new IllegalStateException(e.getMessage())).initCause(e));
        }
        catch (ReflectionException e)
        {
            throw (IllegalStateException) ((new IllegalStateException(e.getMessage())).initCause(e));
        }
        catch (MBeanException e)
        {
            throw (IllegalStateException) ((new IllegalStateException(e.getMessage())).initCause(e));
        }
    }

    /**
     * @see javax.security.auth.spi.LoginModule#initialize()
     */
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options)
    {
        securityService = (String) options.get("securityService");
        delegee = new LoginModuleDelegee(getUserManager());
        delegee.initialize(subject, callbackHandler, sharedState, options);

    }

    /** 
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    public boolean abort() throws LoginException
    {
        return delegee.abort();
    }

    /** 
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    public boolean commit() throws LoginException
    {
        return delegee.commit();
    }

    /** 
     * @see javax.security.auth.spi.LoginModule#login()
     */
    public boolean login() throws LoginException
    {
        return delegee.login();
    }

    /** 
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    public boolean logout() throws LoginException
    {
        return delegee.logout();
    }
}
