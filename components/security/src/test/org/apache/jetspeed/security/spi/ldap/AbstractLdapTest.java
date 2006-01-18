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
package org.apache.jetspeed.security.spi.ldap;

import java.util.Random;

import junit.framework.TestCase;

import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.LdapCredentialHandler;
import org.apache.jetspeed.security.spi.impl.LdapGroupSecurityHandler;
import org.apache.jetspeed.security.spi.impl.LdapRoleSecurityHandler;
import org.apache.jetspeed.security.spi.impl.LdapSecurityMappingHandler;
import org.apache.jetspeed.security.spi.impl.LdapUserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapBindingConfig;
import org.apache.jetspeed.security.spi.impl.ldap.LdapGroupDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapRoleDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDaoImpl;

/**
 * <p>
 * Abstract test case for LDAP providers.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * 
 */
public abstract class AbstractLdapTest extends TestCase
{
    /** The {@link UserSecurityHandler}. */
    UserSecurityHandler userHandler;

    /** The {@link CredentialHandler}. */
    CredentialHandler crHandler;

    /** The {@link GroupSecurityHandler}. */
    GroupSecurityHandler grHandler;
    
    /** The {@link RoleSecurityHandler}. */
    RoleSecurityHandler roleHandler;    
    
    /** The {@link SecurityMappingHandler}. */
    SecurityMappingHandler secHandler;
    
    /** The {@link LdapUserPrincipalDao}. */
    LdapUserPrincipalDao ldapPrincipalDao;
    
    /** The {@link LdapUserCredentialDao}. */
    LdapUserCredentialDao ldapCredDao;
    
    /** The {@link LdapGroupDao}. */
    LdapPrincipalDao ldapGroupDao;
    
    /** The {@link LdapGroupDao}. */
    LdapPrincipalDao ldapRoleDao;    

    /** Random seed. */
    Random rand = new Random(System.currentTimeMillis());

    /** Group uid. */
    protected String gpUid1;

    /** Group uid. */
    protected String gpUid2;
    
    /** Role uid. */
    protected String roleUid1;

    /** Role uid. */
    protected String roleUid2;    

    /** User uid. */
    protected String uid1;

    /** User uid. */
    protected String uid2;

    /** The test password. */
    protected String password = "fred";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        LdapBindingConfig ldapConfig = new LdapBindingConfig();
        ldapCredDao = new LdapUserCredentialDaoImpl(ldapConfig);
        ldapPrincipalDao = new LdapUserPrincipalDaoImpl(ldapConfig);

        userHandler = new LdapUserSecurityHandler(ldapPrincipalDao);
        crHandler = new LdapCredentialHandler(ldapCredDao);
        LdapDataHelper.setUserSecurityHandler(userHandler);
        LdapDataHelper.setCredentialHandler(crHandler);
        uid1 = Integer.toString(rand.nextInt());
        uid2 = Integer.toString(rand.nextInt());
        
        ldapGroupDao = new LdapGroupDaoImpl(ldapConfig);
        ldapRoleDao = new LdapRoleDaoImpl(ldapConfig);
        grHandler = new LdapGroupSecurityHandler(ldapGroupDao);
        roleHandler = new LdapRoleSecurityHandler(ldapRoleDao);
        LdapDataHelper.setGroupSecurityHandler(grHandler);
        LdapDataHelper.setRoleSecurityHandler(roleHandler);
        gpUid1 = Integer.toString(rand.nextInt());
        gpUid2 = Integer.toString(rand.nextInt());
        
        roleUid1 = Integer.toString(rand.nextInt());
        roleUid2 = Integer.toString(rand.nextInt());        
        
        secHandler = new LdapSecurityMappingHandler(ldapPrincipalDao, ldapGroupDao, ldapRoleDao);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

}