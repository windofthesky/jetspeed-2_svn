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
package org.apache.jetspeed.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.security.spi.SecurityDomainAccessManager;
import org.apache.jetspeed.security.spi.SecurityDomainStorageManager;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 *  
 */
public class AbstractSecurityTestcase extends DatasourceEnabledSpringTestCase
{
    /** The user manager. */
    protected UserManager ums;

    /** The group manager. */
    protected GroupManager gms;

    /** The role manager. */
    protected RoleManager rms;

    /** The permission manager. */
    protected PermissionManager pms;

    protected SecurityDomainStorageManager domainStorageManager;
    protected SecurityDomainAccessManager domainAccessManager;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {

        super.setUp();

        ums = (UserManager) scm.getComponent("org.apache.jetspeed.security.UserManager");
        gms = (GroupManager) scm.getComponent("org.apache.jetspeed.security.GroupManager");
        rms = (RoleManager) scm.getComponent("org.apache.jetspeed.security.RoleManager");
                
        // Authorization.
        pms = (PermissionManager) scm.getComponent("org.apache.jetspeed.security.PermissionManager");
        
        domainStorageManager = (SecurityDomainStorageManager) scm.getComponent(SecurityDomainStorageManager.class.getName());
        domainAccessManager = (SecurityDomainAccessManager) scm.getComponent("org.apache.jetspeed.security.spi.SecurityDomainAccessManager");

        // TODO: remove when default seed contains the default domain        
        SecurityDomain domain = domainAccessManager.getDomainByName(SecurityDomain.SYSTEM_NAME); 
        if (domain == null){
            
            SecurityDomainImpl newDomain = new SecurityDomainImpl();
            newDomain.setName(SecurityDomain.SYSTEM_NAME);
            domainStorageManager.addDomain(newDomain);
        } 
        domain = domainAccessManager.getDomainByName(SecurityDomain.DEFAULT_NAME); 
        if (domain == null){
            
            SecurityDomainImpl newDomain = new SecurityDomainImpl();
            newDomain.setName(SecurityDomain.DEFAULT_NAME);
            domainStorageManager.addDomain(newDomain);
        }

        new JetspeedActions(new String[] {"secure"}, new String[] {});
        
        destroyPrincipals();
        destroyPermissions();
        

    }

    protected void tearDown() throws Exception
    {
        destroyPrincipals();
        destroyPermissions();
        super.tearDown();
    }
    
    /**
     * Returns subject's principals of type claz
     * 
     * @param subject
     * @param claz
     * @return Returns subject's principals of type claz
     */
    protected Collection<Principal> getPrincipals(Subject subject, Class<? extends Principal> claz)
    {
        List<Principal> principals = new ArrayList<Principal>();
        for (Iterator<Principal> iter = subject.getPrincipals().iterator(); iter.hasNext();)
        {
            Principal element = iter.next();
            if (claz.isInstance(element))
                principals.add(element);

        }
        return principals;
    }
    
    protected User addUser(String name, String password) throws SecurityException
    {
        User user = ums.addUser(name);            
        PasswordCredential credential = ums.getPasswordCredential(user);
        credential.setPassword(password, false);
        ums.storePasswordCredential(credential);
        return user;
    }
    
    protected String getBeanDefinitionFilterCategories()
    {
        return "security,dbSecurity,transaction,cache,jdbcDS";
    }

    protected String[] getConfigurations()
    {
        //String[] confs = super.getConfigurations();
        List<String> confList = new ArrayList<String>(); //Arrays.asList(confs));
        confList.add("security-atn.xml");
        confList.add("security-atz.xml");
        confList.add("security-managers.xml");
        confList.add("security-providers.xml");
        confList.add("security-spi.xml");
        confList.add("security-spi-atn.xml");
        confList.add("transaction.xml");
        confList.add("cache-test.xml");
        confList.add("static-bean-references.xml");
        // override the JetspeedPrincipalManagerProvider bean to get rid of
        // dependency on SSO
        confList.add("JETSPEED-INF/spring/JetspeedPrincipalManagerProviderOverride.xml");
        return (String[]) confList.toArray(new String[1]);
    }

    /**
     * <p>
     * Destroy group test objects.
     * </p>
     */
    protected void destroyPrincipals() throws Exception
    {
        for (String name : ums.getUserNames(null))
        {
            ums.removeUser(name);
        }
        for (String name : rms.getRoleNames(null))
        {
            // because of possible dependent roles already been deleted through a parent deletion,
            // first check if it still exists
            if (rms.roleExists(name))
            {
                rms.removeRole(name);
            }
        }
        for (String name : gms.getGroupNames(null))
        {
            // because of possible dependent groups already been deleted through a parent deletion,
            // first check if it still exists
            if (gms.groupExists(name))
            {
                gms.removeGroup(name);
            }
        }
    }
    
    protected void destroyPermissions() throws Exception
    {
        for (JetspeedPermission p : pms.getPermissions())
        {
            pms.removePermission(p);
        }
    }
}