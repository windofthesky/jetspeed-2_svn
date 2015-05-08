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
package org.apache.jetspeed.security.spi.cache;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.security.AbstractSecurityTestcase;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.GroupImpl;
import org.apache.jetspeed.security.impl.PersistentJetspeedPrincipal;
import org.apache.jetspeed.security.impl.RoleImpl;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.security.impl.UserImpl;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;
import org.apache.jetspeed.security.spi.impl.JetspeedSecurityPersistenceManager;
import org.apache.jetspeed.security.spi.impl.PasswordCredentialImpl;
import org.apache.jetspeed.security.spi.impl.PersistentJetspeedPermissionImpl;
import org.apache.jetspeed.security.spi.impl.cache.JSPMCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ojb.PersistenceBrokerTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * TestJSPMCache - test JetspeedSecurityPersistenceManager cache
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class TestJSPMCache extends AbstractSecurityTestcase
{
    private static final Logger log = LoggerFactory.getLogger(TestJSPMCache.class);

    private static final String TEST_DOMAIN_1 = "TEST-DOMAIN-1";
    private static final String TEST_USER_1 = "TEST-USER-1";
    private static final String TEST_USER_2 = "TEST-USER-2";
    private static final String TEST_ROLE_1 = "TEST-ROLE-1";
    private static final String TEST_ROLE_2 = "TEST-ROLE-2";
    private static final String TEST_GROUP_1 = "TEST-GROUP-1";
    private static final String TEST_GROUP_2 = "TEST-GROUP-2";
    private static final String TEST_PERMISSION_TYPE = "TEST-PERMISSION";
    private static final String TEST_PERMISSION_1 = "TEST-PERMISSION-1";
    private static final String TEST_PERMISSION_2 = "TEST-PERMISSION-2";

    private JetspeedSecurityPersistenceManager jspm;
    private JSPMCache jspmCache;
    private JetspeedPrincipalType userType;
    private JetspeedPrincipalType roleType;
    private JetspeedPrincipalType groupType;
    private PersistenceBrokerTransactionManager txnManager;
    private TransactionStatus txn;

    private SecurityDomain defaultDomain;
    private SecurityDomain testDomain1;
    private User testUser1;
    private User testUser2;
    private JetspeedPrincipal testRole1;
    private JetspeedPrincipal testRole2;
    private JetspeedPrincipal testGroup1;
    private JetspeedPrincipal testGroup2;
    private JetspeedPermission testPermission1;
    private JetspeedPermission testPermission2;

    public static Test suite()
    {
        return new TestSuite(TestJSPMCache.class);
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        // lookup test components
        jspm = scm.lookupComponent("org.apache.jetspeed.security.spi.impl.JetspeedSecurityPersistenceManager");
        assertNotNull(jspm);
        jspmCache = scm.lookupComponent("org.apache.jetspeed.security.spi.impl.cache.JSPMCache");
        assertNotNull(jspmCache);
        userType = scm.lookupComponent("org.apache.jetspeed.security.JetspeedPrincipalType.user");
        assertNotNull(userType);
        roleType = scm.lookupComponent("org.apache.jetspeed.security.JetspeedPrincipalType.role");
        assertNotNull(roleType);
        groupType = scm.lookupComponent("org.apache.jetspeed.security.JetspeedPrincipalType.group");
        assertNotNull(groupType);
        txnManager = scm.lookupComponent("transactionManager");
        assertNotNull(txnManager);
    }

    @Override
    public void tearDown() throws Exception
    {
        // remove test domain
        if (testDomain1 != null)
        {
            try
            {
                domainStorageManager.removeDomain(testDomain1);
            }
            catch (Exception e)
            {
            }
            testDomain1 = null;
        }
        // remove all principals and permissions
        super.tearDown();
    }

    /**
     * Test read cache operation for security domains, principals,
     * password credentials, permissions, and associations.
     *
     * @throws Exception
     */
    public void testJSPMCache() throws Exception
    {
        try
        {
            // begin transaction
            beginTransaction();

            // create domains
            defaultDomain = jspm.getDomainByName(SecurityDomain.DEFAULT_NAME);
            assertNotNull(defaultDomain);
            testDomain1 = new SecurityDomainImpl();
            ((SecurityDomainImpl) testDomain1).setName(TEST_DOMAIN_1);
            ((SecurityDomainImpl) testDomain1).setOwnerDomainId(defaultDomain.getDomainId());
            jspm.addDomain(testDomain1);
            testDomain1 = jspm.getDomainByName(TEST_DOMAIN_1);
            assertNotNull(testDomain1);

            // create principals
            testUser1 = new UserImpl(TEST_USER_1);
            ((UserImpl) testUser1).setDomainId(testDomain1.getDomainId());
            testUser1.getSecurityAttributes().getAttribute("user.name", true).setStringValue(TEST_USER_1);
            jspm.addPrincipal(testUser1, null);
            testUser1 = (User) jspm.getPrincipal(TEST_USER_1, userType, testDomain1.getDomainId());
            assertNotNull(testUser1);
            testUser2 = new UserImpl(TEST_USER_2);
            ((UserImpl) testUser2).setDomainId(testDomain1.getDomainId());
            testUser2.getSecurityAttributes().getAttribute("user.name", true).setStringValue(TEST_USER_2);
            jspm.addPrincipal(testUser2, null);
            testUser2 = (User) jspm.getPrincipal(TEST_USER_2, userType, testDomain1.getDomainId());
            assertNotNull(testUser2);
            testRole1 = new RoleImpl(TEST_ROLE_1);
            ((RoleImpl) testRole1).setDomainId(testDomain1.getDomainId());
            jspm.addPrincipal(testRole1, null);
            testRole1 = jspm.getPrincipal(TEST_ROLE_1, roleType, testDomain1.getDomainId());
            assertNotNull(testRole1);
            testRole2 = new RoleImpl(TEST_ROLE_2);
            ((RoleImpl) testRole2).setDomainId(testDomain1.getDomainId());
            jspm.addPrincipal(testRole2, null);
            testRole2 = jspm.getPrincipal(TEST_ROLE_2, roleType, testDomain1.getDomainId());
            assertNotNull(testRole2);
            testGroup1 = new GroupImpl(TEST_GROUP_1);
            ((GroupImpl) testGroup1).setDomainId(testDomain1.getDomainId());
            jspm.addPrincipal(testGroup1, null);
            testGroup1 = jspm.getPrincipal(TEST_GROUP_1, groupType, testDomain1.getDomainId());
            assertNotNull(testGroup1);
            testGroup2 = new GroupImpl(TEST_GROUP_2);
            ((GroupImpl) testGroup2).setDomainId(testDomain1.getDomainId());
            jspm.addPrincipal(testGroup2, null);
            testGroup2 = jspm.getPrincipal(TEST_GROUP_2, groupType, testDomain1.getDomainId());
            assertNotNull(testGroup2);

            // create password credentials
            PasswordCredential testUser1Password = new PasswordCredentialImpl(testUser1, TEST_USER_1);
            jspm.storePasswordCredential(testUser1Password);
            testUser1Password = jspm.getPasswordCredential(testUser1);
            assertNotNull(testUser1Password);
            PasswordCredential testUser1HistoricalPassword = new PasswordCredentialImpl(testUser1, TEST_USER_1);
            setPasswordCredentialType(testUser1HistoricalPassword, PasswordCredential.TYPE_HISTORICAL);
            jspm.storePasswordCredential(testUser1HistoricalPassword);
            List<PasswordCredential> testUser1HistoricalPasswords = jspm.getHistoricPasswordCredentials(testUser1,
                    testDomain1.getDomainId());
            assertNotNull(testUser1HistoricalPasswords);
            assertEquals(1, testUser1HistoricalPasswords.size());
            testUser1HistoricalPassword = testUser1HistoricalPasswords.get(0);
            assertNotNull(testUser1HistoricalPassword);

            // create permissions
            testPermission1 = new PersistentJetspeedPermissionImpl(TEST_PERMISSION_TYPE, TEST_PERMISSION_1);
            ((PersistentJetspeedPermission) testPermission1).setActions(TEST_PERMISSION_1);
            jspm.addPermission((PersistentJetspeedPermission) testPermission1);
            testPermission1 = jspm.getPermissions(TEST_PERMISSION_TYPE, TEST_PERMISSION_1).get(0);
            assertNotNull(testPermission1);
            testPermission2 = new PersistentJetspeedPermissionImpl(TEST_PERMISSION_TYPE, TEST_PERMISSION_2);
            ((PersistentJetspeedPermission) testPermission2).setActions(TEST_PERMISSION_2);
            jspm.addPermission((PersistentJetspeedPermission) testPermission2);
            testPermission2 = jspm.getPermissions(TEST_PERMISSION_TYPE, TEST_PERMISSION_2).get(0);
            assertNotNull(testPermission2);

            // grant principal permissions
            jspm.grantPermission((PersistentJetspeedPermission) testPermission1, testUser1);
            jspm.grantPermission((PersistentJetspeedPermission) testPermission2, testGroup1);
            jspm.grantPermission((PersistentJetspeedPermission) testPermission1, testRole2);
            jspm.grantPermission((PersistentJetspeedPermission) testPermission2, testUser2);

            // create principal associations
            jspm.addAssociation(testGroup1, testRole1, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
            jspm.addAssociation(testGroup2, testRole2, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
            jspm.addAssociation(testUser1, testRole1, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
            jspm.addAssociation(testUser2, testRole2, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
            jspm.addAssociation(testUser1, testGroup1, JetspeedPrincipalAssociationType.IS_MEMBER_OF);
            jspm.addAssociation(testUser2, testGroup2, JetspeedPrincipalAssociationType.IS_MEMBER_OF);

            // commit transaction
            commitTransaction();

            // clear cache after creates
            jspmCache.clear();

            // begin transaction
            beginTransaction();

            // test principal cache queries
            int cacheSize = jspmCache.size();
            jspm.getPrincipalId(TEST_USER_1, userType.getName(), testDomain1.getDomainId());
            try
            {
                jspm.getPrincipalId("NOT-A-USER", userType.getName(), testDomain1.getDomainId());
                fail("Expected SecurityException for missing principalId");
            }
            catch (SecurityException se)
            {
            }
            assertEquals(2, jspmCache.size() - cacheSize);
            jspm.getPrincipalId(TEST_USER_1, userType.getName(), testDomain1.getDomainId());
            try
            {
                jspm.getPrincipalId("NOT-A-USER", userType.getName(), testDomain1.getDomainId());
                fail("Expected SecurityException for missing principalId");
            }
            catch (SecurityException se)
            {
            }
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            assertTrue(jspm.principalExists(testGroup1));
            PersistentJetspeedPrincipal notAGroup = new GroupImpl("NOT-A-GROUP");
            notAGroup.setDomainId(testDomain1.getDomainId());
            setPrincipalId(notAGroup, new Long(-1L));
            assertFalse(jspm.principalExists(notAGroup));
            assertEquals(2, jspmCache.size() - cacheSize);
            assertTrue(jspm.principalExists(testGroup1));
            assertFalse(jspm.principalExists(notAGroup));
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            assertNotNull(jspm.getPrincipal(testUser1.getId()));
            assertNull(jspm.getPrincipal(new Long(-1L)));
            assertEquals(2, jspmCache.size() - cacheSize);
            assertNotNull(jspm.getPrincipal(testUser1.getId()));
            assertNull(jspm.getPrincipal(new Long(-1L)));
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            assertNotNull(jspm.getPrincipal(TEST_USER_1, userType, testDomain1.getDomainId()));
            assertNull(jspm.getPrincipal("NOT-A-USER", userType, testDomain1.getDomainId()));
            assertEquals(2, jspmCache.size() - cacheSize);
            assertNotNull(jspm.getPrincipal(TEST_USER_1, userType, testDomain1.getDomainId()));
            assertNull(jspm.getPrincipal("NOT-A-USER", userType, testDomain1.getDomainId()));
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            List<String> principalNames = jspm.getPrincipalNames("", userType, testDomain1.getDomainId());
            assertNotNull(principalNames);
            assertEquals(2, principalNames.size());
            principalNames = jspm.getPrincipalNames("NOT-", userType, testDomain1.getDomainId());
            assertNotNull(principalNames);
            assertEquals(0, principalNames.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            principalNames = jspm.getPrincipalNames("", userType, testDomain1.getDomainId());
            assertNotNull(principalNames);
            assertEquals(2, principalNames.size());
            principalNames = jspm.getPrincipalNames("NOT-", userType, testDomain1.getDomainId());
            assertNotNull(principalNames);
            assertEquals(0, principalNames.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            List<JetspeedPrincipal> principals = jspm.getPrincipals("", userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(2, principals.size());
            principals = jspm.getPrincipals("NOT-", userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(0, principals.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            principals = jspm.getPrincipals("", userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(2, principals.size());
            principals = jspm.getPrincipals("NOT-", userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(0, principals.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            principals = jspm.getPrincipalsByAttribute("user.name", TEST_USER_1, userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(1, principals.size());
            principals = jspm.getPrincipalsByAttribute("user.name", "NOT-A-USER", userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(0, principals.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            principals = jspm.getPrincipalsByAttribute("user.name", TEST_USER_1, userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(1, principals.size());
            principals = jspm.getPrincipalsByAttribute("user.name", "NOT-A-USER", userType, testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(0, principals.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            assertTrue(jspm.principalExists(TEST_GROUP_2, groupType, testDomain1.getDomainId()));
            assertFalse(jspm.principalExists("NOT-A-GROUP", groupType, testDomain1.getDomainId()));
            assertEquals(3, jspmCache.size() - cacheSize);
            assertTrue(jspm.principalExists(TEST_GROUP_2, groupType, testDomain1.getDomainId()));
            assertFalse(jspm.principalExists("NOT-A-GROUP", groupType, testDomain1.getDomainId()));
            assertEquals(3, jspmCache.size() - cacheSize);

            // test association cache queries
            cacheSize = jspmCache.size();
            List<JetspeedPrincipal> associatedFrom = jspm.getAssociatedFrom(TEST_USER_1, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(1, associatedFrom.size());
            associatedFrom = jspm.getAssociatedFrom(TEST_USER_1, userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(0, associatedFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            associatedFrom = jspm.getAssociatedFrom(TEST_USER_1, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(1, associatedFrom.size());
            associatedFrom = jspm.getAssociatedFrom(TEST_USER_1, userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(0, associatedFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            List<JetspeedPrincipal> associatedTo = jspm.getAssociatedTo(TEST_ROLE_2, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(1, associatedTo.size());
            associatedTo = jspm.getAssociatedTo(TEST_ROLE_2, roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(0, associatedTo.size());
            assertEquals(3, jspmCache.size() - cacheSize);
            associatedTo = jspm.getAssociatedTo(TEST_ROLE_2, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(1, associatedTo.size());
            associatedTo = jspm.getAssociatedTo(TEST_ROLE_2, roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(0, associatedTo.size());
            assertEquals(3, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            associatedFrom = jspm.getAssociatedFrom(testUser2.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(1, associatedFrom.size());
            associatedFrom = jspm.getAssociatedFrom(testUser2.getId(), userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(0, associatedFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            associatedFrom = jspm.getAssociatedFrom(testUser2.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(1, associatedFrom.size());
            associatedFrom = jspm.getAssociatedFrom(testUser2.getId(), userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedFrom);
            assertEquals(0, associatedFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            associatedTo = jspm.getAssociatedTo(testRole1.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(1, associatedTo.size());
            associatedTo = jspm.getAssociatedTo(testRole1.getId(), roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(0, associatedTo.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            associatedTo = jspm.getAssociatedTo(testRole1.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(1, associatedTo.size());
            associatedTo = jspm.getAssociatedTo(testRole1.getId(), roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedTo);
            assertEquals(0, associatedTo.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            List<String> associatedNamesFrom = jspm.getAssociatedNamesFrom(TEST_USER_1, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(1, associatedNamesFrom.size());
            associatedNamesFrom = jspm.getAssociatedNamesFrom(TEST_USER_1, userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(0, associatedNamesFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            associatedNamesFrom = jspm.getAssociatedNamesFrom(TEST_USER_1, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(1, associatedNamesFrom.size());
            associatedNamesFrom = jspm.getAssociatedNamesFrom(TEST_USER_1, userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(0, associatedNamesFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            List<String> associatedNamesTo = jspm.getAssociatedNamesTo(TEST_ROLE_2, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(1, associatedNamesTo.size());
            associatedNamesTo = jspm.getAssociatedNamesTo(TEST_ROLE_2, roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(0, associatedNamesTo.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            associatedNamesTo = jspm.getAssociatedNamesTo(TEST_ROLE_2, userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(1, associatedNamesTo.size());
            associatedNamesTo = jspm.getAssociatedNamesTo(TEST_ROLE_2, roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(0, associatedNamesTo.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            associatedNamesFrom = jspm.getAssociatedNamesFrom(testUser2.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(1, associatedNamesFrom.size());
            associatedNamesFrom = jspm.getAssociatedNamesFrom(testUser2.getId(), userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(0, associatedNamesFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            associatedNamesFrom = jspm.getAssociatedNamesFrom(testUser2.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(1, associatedNamesFrom.size());
            associatedNamesFrom = jspm.getAssociatedNamesFrom(testUser2.getId(), userType, userType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesFrom);
            assertEquals(0, associatedNamesFrom.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            associatedNamesTo = jspm.getAssociatedNamesTo(testRole1.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(1, associatedNamesTo.size());
            associatedNamesTo = jspm.getAssociatedNamesTo(testRole1.getId(), roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(0, associatedNamesTo.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            associatedNamesTo = jspm.getAssociatedNamesTo(testRole1.getId(), userType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(1, associatedNamesTo.size());
            associatedNamesTo = jspm.getAssociatedNamesTo(testRole1.getId(), roleType, roleType,
                    JetspeedPrincipalAssociationType.IS_MEMBER_OF, testDomain1.getDomainId(), testDomain1.getDomainId());
            assertNotNull(associatedNamesTo);
            assertEquals(0, associatedNamesTo.size());
            assertEquals(2, jspmCache.size() - cacheSize);

            // test password credential cache queries
            cacheSize = jspmCache.size();
            testUser1Password = jspm.getPasswordCredential(testUser1);
            assertNotNull(testUser1Password);
            PasswordCredential testUser2Password = jspm.getPasswordCredential(testUser2);
            assertNotNull(testUser2Password);
            assertEquals(1, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            testUser1Password = jspm.getPasswordCredential(TEST_USER_1, testDomain1.getDomainId());
            assertNotNull(testUser1Password);
            testUser2Password = jspm.getPasswordCredential(TEST_USER_2, testDomain1.getDomainId());
            assertNull(testUser2Password);
            assertEquals(2, jspmCache.size() - cacheSize);
            testUser1Password = jspm.getPasswordCredential(TEST_USER_1, testDomain1.getDomainId());
            assertNotNull(testUser1Password);
            testUser2Password = jspm.getPasswordCredential(TEST_USER_2, testDomain1.getDomainId());
            assertNull(testUser2Password);
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            testUser1HistoricalPasswords = jspm.getHistoricPasswordCredentials(testUser1, testDomain1.getDomainId());
            assertNotNull(testUser1HistoricalPasswords);
            assertEquals(1, testUser1HistoricalPasswords.size());
            List<PasswordCredential> testUser2HistoricalPasswords = jspm.getHistoricPasswordCredentials(testUser2,
                    testDomain1.getDomainId());
            assertNotNull(testUser2HistoricalPasswords);
            assertEquals(0, testUser2HistoricalPasswords.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            testUser1HistoricalPasswords = jspm.getHistoricPasswordCredentials(testUser1, testDomain1.getDomainId());
            assertNotNull(testUser1HistoricalPasswords);
            assertEquals(1, testUser1HistoricalPasswords.size());
            testUser2HistoricalPasswords = jspm.getHistoricPasswordCredentials(testUser2, testDomain1.getDomainId());
            assertNotNull(testUser2HistoricalPasswords);
            assertEquals(0, testUser2HistoricalPasswords.size());
            assertEquals(2, jspmCache.size() - cacheSize);

            // test permission cache queries
            cacheSize = jspmCache.size();
            List<PersistentJetspeedPermission> permissions = jspm.getPermissions();
            assertNotNull(permissions);
            assertEquals(2, permissions.size());
            assertEquals(1, jspmCache.size() - cacheSize);
            permissions = jspm.getPermissions();
            assertNotNull(permissions);
            assertEquals(2, permissions.size());
            assertEquals(1, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            permissions = jspm.getPermissions(TEST_PERMISSION_TYPE, TEST_PERMISSION_1);
            assertNotNull(permissions);
            assertEquals(1, permissions.size());
            permissions = jspm.getPermissions(TEST_PERMISSION_TYPE, "NOT-A-PERMISSION");
            assertNotNull(permissions);
            assertEquals(0, permissions.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            permissions = jspm.getPermissions(TEST_PERMISSION_TYPE, TEST_PERMISSION_1);
            assertNotNull(permissions);
            assertEquals(1, permissions.size());
            permissions = jspm.getPermissions(TEST_PERMISSION_TYPE, "NOT-A-PERMISSION");
            assertNotNull(permissions);
            assertEquals(0, permissions.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            assertTrue(jspm.permissionExists(testPermission2));
            assertFalse(jspm.permissionExists(new PersistentJetspeedPermissionImpl(TEST_PERMISSION_TYPE,
                    "NOT-A-PERMISSION")));
            assertEquals(2, jspmCache.size() - cacheSize);
            assertTrue(jspm.permissionExists(testPermission2));
            assertFalse(jspm.permissionExists(new PersistentJetspeedPermissionImpl(TEST_PERMISSION_TYPE,
                    "NOT-A-PERMISSION")));
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            permissions = jspm.getPermissions((PersistentJetspeedPrincipal) testUser1);
            assertNotNull(permissions);
            assertEquals(1, permissions.size());
            permissions = jspm.getPermissions((PersistentJetspeedPrincipal) testRole1);
            assertNotNull(permissions);
            assertEquals(0, permissions.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            permissions = jspm.getPermissions((PersistentJetspeedPrincipal) testUser1);
            assertNotNull(permissions);
            assertEquals(1, permissions.size());
            permissions = jspm.getPermissions((PersistentJetspeedPrincipal) testRole1);
            assertNotNull(permissions);
            assertEquals(0, permissions.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            principals = jspm.getPrincipals((PersistentJetspeedPermission) testPermission1, null,
                    testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(2, principals.size());
            principals = jspm.getPrincipals((PersistentJetspeedPermission) testPermission1, groupType.getName(),
                    testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(0, principals.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            principals = jspm.getPrincipals((PersistentJetspeedPermission) testPermission1, null,
                    testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(2, principals.size());
            principals = jspm.getPrincipals((PersistentJetspeedPermission) testPermission1, groupType.getName(),
                    testDomain1.getDomainId());
            assertNotNull(principals);
            assertEquals(0, principals.size());
            assertEquals(2, jspmCache.size() - cacheSize);

            // test domain cache queries
            cacheSize = jspmCache.size();
            assertNotNull(jspm.getDomain(testDomain1.getDomainId()));
            assertNull(jspm.getDomain(new Long(-1L)));
            assertEquals(2, jspmCache.size() - cacheSize);
            assertNotNull(jspm.getDomain(testDomain1.getDomainId()));
            assertNull(jspm.getDomain(new Long(-1L)));
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            assertNotNull(jspm.getDomainByName(TEST_DOMAIN_1));
            assertNull(jspm.getDomainByName("NOT-A-DOMAIN"));
            assertEquals(2, jspmCache.size() - cacheSize);
            assertNotNull(jspm.getDomainByName(TEST_DOMAIN_1));
            assertNull(jspm.getDomainByName("NOT-A-DOMAIN"));
            assertEquals(2, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            Collection<SecurityDomain> domains = jspm.getAllDomains();
            assertNotNull(domains);
            assertEquals(3, domains.size());
            assertEquals(1, jspmCache.size() - cacheSize);
            domains = jspm.getAllDomains();
            assertNotNull(domains);
            assertEquals(3, domains.size());
            assertEquals(1, jspmCache.size() - cacheSize);
            cacheSize = jspmCache.size();
            domains = jspm.getDomainsOwnedBy(defaultDomain.getDomainId());
            assertNotNull(domains);
            assertEquals(1, domains.size());
            domains = jspm.getDomainsOwnedBy(testDomain1.getDomainId());
            assertNotNull(domains);
            assertEquals(0, domains.size());
            assertEquals(2, jspmCache.size() - cacheSize);
            domains = jspm.getDomainsOwnedBy(defaultDomain.getDomainId());
            assertNotNull(domains);
            assertEquals(1, domains.size());
            domains = jspm.getDomainsOwnedBy(testDomain1.getDomainId());
            assertNotNull(domains);
            assertEquals(0, domains.size());
            assertEquals(2, jspmCache.size() - cacheSize);

            // commit transaction
            commitTransaction();
        }
        finally
        {
            rollbackTransaction();
        }
    }

    /**
     * Begin transaction on current thread.
     */
    private void beginTransaction()
    {
        txn = txnManager.getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED));
    }

    /**
     * Commit transaction on current thread.
     */
    private void commitTransaction()
    {
        if (txn != null)
        {
            txnManager.commit(txn);
            txn = null;
        }
    }

    /**
     * Rollback transaction on current thread.
     */
    private void rollbackTransaction()
    {
        try
        {
            if (txn != null)
            {
                txnManager.rollback(txn);
                txn = null;
            }
        }
        catch (Exception e)
        {
            txn = null;
        }
    }

    /**
     * Force set principal id via reflection for test.
     *
     * @param principal principal to modify
     * @param id principal id to set
     */
    private static void setPrincipalId(PersistentJetspeedPrincipal principal, Long id)
    {
        Field idField = ReflectionUtils.findField(principal.getClass(), "id");
        ReflectionUtils.makeAccessible(idField);
        ReflectionUtils.setField(idField, principal, id);
    }

    /**
     * Force set password credential type via reflection for test.
     *
     * @param passwordCredential password credential to modify
     * @param type password credential type to set
     */
    private static void setPasswordCredentialType(PasswordCredential passwordCredential, Short type)
    {
        Field typeField = ReflectionUtils.findField(passwordCredential.getClass(), "type");
        ReflectionUtils.makeAccessible(typeField);
        ReflectionUtils.setField(typeField, passwordCredential, type);
    }
}
