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
package org.apache.jetspeed.security.util.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.prefs.util.test.AbstractPrefsSupportedTestCase;
import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.SecurityProviderImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityAccess;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 *  
 */
public class AbstractSecurityTestcase extends AbstractPrefsSupportedTestCase
{
    /** SPI Common Queries. */
    protected SecurityAccess securityAccess;
    
    /** SPI Default Credential Handler. */
    protected CredentialHandler ch;
    
    /** SPI Default User Security Handler. */
    protected UserSecurityHandler ush;
    
    /** SPI Default Role Security Handler. */
    protected RoleSecurityHandler rsh;
    
    /** SPI Default Group Security Handler. */
    protected GroupSecurityHandler gsh;
    
    /** SPI Default Security Mapping Handler. */
    protected SecurityMappingHandler smh;
    
    /** The security provider. */
    protected SecurityProvider securityProvider;
    
    /** The user manager. */
    protected UserManager ums;

    /** The group manager. */
    protected GroupManager gms;

    /** The role manager. */
    protected RoleManager rms;

    /** The permission manager. */
    protected PermissionManager pms;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {

        super.setUp();

        // SPI Security handlers.
        securityAccess = (SecurityAccess) scm.getComponent("org.apache.jetspeed.security.spi.SecurityAccess");
        ch =  (CredentialHandler) scm.getComponent("org.apache.jetspeed.security.spi.CredentialHandler");
        ush = (UserSecurityHandler) scm.getComponent("org.apache.jetspeed.security.spi.UserSecurityHandler");
        rsh = (RoleSecurityHandler) scm.getComponent("org.apache.jetspeed.security.spi.RoleSecurityHandler");
        gsh = (GroupSecurityHandler) scm.getComponent("org.apache.jetspeed.security.spi.GroupSecurityHandler");
        smh = (SecurityMappingHandler) scm.getComponent("org.apache.jetspeed.security.spi.SecurityMappingHandler");
        
        // Security Providers.        
        AuthenticationProvider atnProvider = (AuthenticationProvider) scm.getComponent("org.apache.jetspeed.security.AuthenticationProvider");
        List atnProviders = new ArrayList();
        atnProviders.add(atnProvider);
        
       
        AuthenticationProviderProxy atnProviderProxy = (AuthenticationProviderProxy) scm.getComponent("org.apache.jetspeed.security.AuthenticationProviderProxy");
        securityProvider = new SecurityProviderImpl(atnProviderProxy, rsh, gsh, smh);
        
        securityProvider = (SecurityProvider) scm.getComponent("org.apache.jetspeed.security.SecurityProvider");
        
        ums = (UserManager) scm.getComponent("org.apache.jetspeed.security.UserManager");
        gms = (GroupManager) scm.getComponent("org.apache.jetspeed.security.GroupManager");
        rms = (RoleManager) scm.getComponent("org.apache.jetspeed.security.RoleManager");
                
        // Authorization.
        pms = (PermissionManager) scm.getComponent("org.apache.jetspeed.security.PermissionManager");
        
        new JetspeedActions(new String[] {"secure"}, new String[] {});
    }

    /**
     * Returns subject's principals of type claz
     * 
     * @param subject
     * @param claz
     * @return Returns subject's principals of type claz
     */
    protected Collection getPrincipals(Subject subject, Class claz)
    {
        List principals = new ArrayList();
        for (Iterator iter = subject.getPrincipals().iterator(); iter.hasNext();)
        {
            Object element = iter.next();
            if (claz.isInstance(element))
                principals.add(element);

        }
        return principals;
    }

    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("security-atn.xml");
        confList.add("security-atz.xml");
        confList.add("security-managers.xml");
        confList.add("security-providers.xml");
        confList.add("security-spi.xml");
        confList.add("security-spi-atn.xml");
        confList.add("security-spi-atz.xml");
        return (String[]) confList.toArray(new String[1]);
    }

}