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
package org.apache.jetspeed.security.spi.ldap;

import java.util.Random;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.test.AbstractSpringTestCase;
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
import org.apache.jetspeed.security.spi.impl.ldap.InitLdapSchema;
import org.apache.jetspeed.security.spi.impl.ldap.LdapBindingConfig;
import org.apache.jetspeed.security.spi.impl.ldap.LdapGroupDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapMemberShipDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao;
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
public abstract class AbstractLdapTest extends AbstractSpringTestCase
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(AbstractLdapTest.class);
    
	private static final String LDAP_CONFIG = "openldap/setup2";
	
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
    
    LdapMembershipDao ldapMembershipDao;

    /** Random seed. */
    Random rand = new Random(System.currentTimeMillis());

    /** Group uid. */
    protected String gpUid1 = "group1";

    /** Group uid. */
    protected String gpUid2 = "group2";
    
    /** Role uid. */
    protected String roleUid1 = "role1";

    /** Role uid. */
    protected String roleUid2 = "role2";    

    /** User uid. */
    protected String uid1 = "user1";

    /** User uid. */
    protected String uid2 = "user2";

    /** The test password. */
    protected String password = "fred";
    

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        LdapBindingConfig ldapConfig = (LdapBindingConfig)scm.getComponent(LdapBindingConfig.class.getName());
        InitLdapSchema ldapSchema = new InitLdapSchema(ldapConfig);
        try
        {
            // make sure standard test case schema exists
            ldapSchema.initOu("OrgUnit1");
            ldapSchema.initOu("People");
            ldapSchema.initOu("Roles");
            ldapSchema.initOu("People","ou=OrgUnit1");
            ldapSchema.initOu("Groups","ou=OrgUnit1");
            ldapSchema.initOu("Roles","ou=OrgUnit1");

        }
        catch (NamingException se)
        {
            logger.error("Initializing the LDAP directory failed:", se);
            throw se;
        }

        ldapCredDao = new LdapUserCredentialDaoImpl(ldapConfig);
        ldapPrincipalDao = new LdapUserPrincipalDaoImpl(ldapConfig);

        userHandler = new LdapUserSecurityHandler(ldapPrincipalDao);
        crHandler = new LdapCredentialHandler(ldapCredDao);
        LdapDataHelper.setUserSecurityHandler(userHandler);
        LdapDataHelper.setCredentialHandler(crHandler);
        
        ldapGroupDao = new LdapGroupDaoImpl(ldapConfig);
        ldapRoleDao = new LdapRoleDaoImpl(ldapConfig);
        ldapMembershipDao = new LdapMemberShipDaoImpl(ldapConfig);
        grHandler = new LdapGroupSecurityHandler(ldapGroupDao);
        roleHandler = new LdapRoleSecurityHandler(ldapRoleDao);
        LdapDataHelper.setGroupSecurityHandler(grHandler);
        LdapDataHelper.setRoleSecurityHandler(roleHandler);
        
        secHandler = new LdapSecurityMappingHandler(ldapPrincipalDao, ldapGroupDao, ldapRoleDao);
    }

    protected String[] getConfigurations()
    {
        return new String[] {"JETSPEED-INF/directory/config/" + LDAP_CONFIG + "/security-spi-ldap.xml" };
    }    
}