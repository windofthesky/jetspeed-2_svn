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
package org.apache.jetspeed.security.mapping.ldap.setup1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import junit.framework.Test;

import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.impl.EntityImpl;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class TestLDAP1 extends AbstractSetup1LDAPTest
{
    public static Test suite()
    {
        return createFixturedTestSuite(TestLDAP1.class, "ldapTestSetup", "ldapTestTeardown");
    }
    
    public void testSingleUser() throws Exception
    {
        EntityImpl sampleUser = new EntityImpl("user", "jsmith", userAttrDefs);
        sampleUser
                .setInternalId("cn=jsmith, ou=People, ou=OrgUnit3, o=sevenSeas");
        sampleUser.setAttribute(GIVEN_NAME_DEF.getName(), "Joe Smith");
        sampleUser.setAttribute(UID_DEF.getName(), "jsmith");
        sampleUser.setAttribute(CN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(SN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(UID_DEF.getName(), "jsmith");
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);
    }

    private EntityImpl getInitialRole1(){
        EntityImpl role1 = new EntityImpl("role", "Role1", roleAttrDefs);
        role1.setInternalId("cn=Role1, o=sevenSeas");
        role1.setAttribute(DESCRIPTION_ATTR_DEF.getName(), "Role 1");
        role1.setAttribute(CN_DEF.getName(), "Role1");
        
        role1.setAttribute(UNIQUEMEMBER_ATTR_DEF.getName(), Arrays.asList(new String[]{
                "cn=OrgUnit2User1,ou=People,ou=OrgUnit2,o=sevenSeas",
                "cn=jsmith,ou=People,ou=OrgUnit3,o=sevenSeas",
                "cn=OrgUnit2User2,ou=People,ou=OrgUnit2,o=sevenSeas"}) );
        return role1;
    }
    
    private EntityImpl getDefaultJoeSmith(){
        // first assert that the sample user is equal to the corresponding user in LDAP
        EntityImpl sampleUser = new EntityImpl("user", "jsmith", userAttrDefs);
        sampleUser
                .setInternalId("cn=jsmith, ou=People, ou=OrgUnit3, o=sevenSeas");
        sampleUser.setAttribute(GIVEN_NAME_DEF.getName(), "Joe Smith");
        sampleUser.setAttribute(UID_DEF.getName(), "jsmith");
        sampleUser.setAttribute(CN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(SN_DEF.getName(), "jsmith");
        return sampleUser;
    }
    
    private EntityImpl getDefaultJoeSmithOnlyMappedAttrs(){
        // first assert that the sample user is equal to the corresponding user in LDAP
        EntityImpl sampleUser = new EntityImpl("user", "jsmith", userAttrDefs);
        sampleUser.setInternalId("cn=jsmith, ou=People, ou=OrgUnit3, o=sevenSeas");
        sampleUser.setAttribute(GIVEN_NAME_DEF.getName(), "Joe Smith");
        return sampleUser;
    }
    
    public void testFetchRolesForUserByRoleAttribute() throws Exception
    {
       
        Entity role1 = getInitialRole1();
        EntityImpl role3 = new EntityImpl("role", "Role3", roleAttrDefs);
        role3.setInternalId("cn=Role3, o=sevenSeas");
        role3.setAttribute(DESCRIPTION_ATTR_DEF.getName(), "Role 3");
        role3.setAttribute(CN_DEF.getName(), "Role3");
        role3.setAttribute(UNIQUEMEMBER_ATTR_DEF.getName(), Arrays.asList(new String[]{
                "cn=jsmith,ou=People,ou=OrgUnit3,o=sevenSeas"
        }));
        Collection<Entity> resultSet = new ArrayList<Entity>();
        resultSet.add(role1);
        resultSet.add(role3);
        basicTestCases.testFetchRelatedEntitiesFrom("user", "role", "hasRole",
                "jsmith", resultSet);
    }

    public void testUpdateSingleValuedEntityAttr() throws Exception
    {
        // first assert that the sample user is equal to the corresponding user in LDAP
        EntityImpl sampleUser = getDefaultJoeSmith();
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);
        
        // next, try some identity transformation checks to assert that updating works
        // 1. test attribute modification
        sampleUser.setAttribute(GIVEN_NAME_DEF.getName(), "Joe Smith modified");
        entityManager.updateEntity(sampleUser);
        
        
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);

        // test attribute removal
        sampleUser = new EntityImpl("user", "jsmith", userAttrDefs);
        sampleUser
                .setInternalId("cn=jsmith, ou=People, ou=OrgUnit3, o=sevenSeas");
        sampleUser.setAttribute(UID_DEF.getName(), "jsmith");
        sampleUser.setAttribute(CN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(SN_DEF.getName(), "jsmith");
        
        entityManager.updateEntity(sampleUser);
        
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);

        // add "lastname" attribute value
        sampleUser = new EntityImpl("user", "jsmith", userAttrDefs);
        sampleUser
                .setInternalId("cn=jsmith, ou=People, ou=OrgUnit3, o=sevenSeas");
        sampleUser.setAttribute(UID_DEF.getName(), "jsmith");
        sampleUser.setAttribute(CN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(SN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(LAST_NAME_DEF.getName(), "jsmith");
        
        entityManager.updateEntity(sampleUser);
        
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);

        // test attribute removal of required attribute ("lastname") with a required default value set for it
        sampleUser = new EntityImpl("user", "jsmith", userAttrDefs);
        sampleUser
                .setInternalId("cn=jsmith, ou=People, ou=OrgUnit3, o=sevenSeas");
        sampleUser.setAttribute(CN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(SN_DEF.getName(), "jsmith");
        sampleUser.setAttribute(UID_DEF.getName(), "jsmith");
        
        entityManager.updateEntity(sampleUser);
        
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);

    }
    
    public void testUpdateMultivaluedEntityAttr() throws Exception
    {
        // first assert that the sample user is equal to the corresponding user in LDAP
        EntityImpl sampleRole = getInitialRole1();
        
        basicTestCases.testFetchSingleEntity(entityManager, sampleRole);
        
        // next, try some identity transformation checks to assert that updating works
        // 1. test attribute modification
        sampleRole.setAttribute(UNIQUEMEMBER_ATTR_DEF.getName(), Arrays.asList(new String[]{"cn=jsmith,ou=People,ou=OrgUnit3,o=sevenSeas"}) );
        entityManager.updateEntity(sampleRole);
        
        
        basicTestCases.testFetchSingleEntity(entityManager, sampleRole);

        sampleRole.setAttribute(UNIQUEMEMBER_ATTR_DEF.getName(), Arrays.asList(new String[]{"cn=jsmith,ou=People,ou=OrgUnit3,o=sevenSeas","cn=OrgUnit2User1,ou=People,ou=OrgUnit2,o=sevenSeas"}) );
        entityManager.updateEntity(sampleRole);
        basicTestCases.testFetchSingleEntity(entityManager, sampleRole);

        // 2. test attribute removal
        sampleRole = new EntityImpl("role", "Role1", roleAttrDefs);
        sampleRole.setInternalId("cn=Role1, o=sevenSeas");
        sampleRole.setAttribute(DESCRIPTION_ATTR_DEF.getName(), "Role 1");
        sampleRole.setAttribute(CN_DEF.getName(), "Role1");
        sampleRole.setAttribute(UNIQUEMEMBER_ATTR_DEF.getName(), Collections.EMPTY_LIST );
        
        // note: we call the user DAO directly, because updating internal
        // attributes is only meant to be used internally, e.g. from within an EntityRelationDAO
        entityManager.updateEntity(sampleRole);
        
        basicTestCases.testFetchSingleEntity(entityManager, sampleRole);
    }
    
    public void testAddEntity() throws Exception {
        EntityImpl jsmithCopy = getDefaultJoeSmithOnlyMappedAttrs();
        jsmithCopy.setId("jsmithCopy");
        
        entityManager.addEntity(jsmithCopy);
        
        jsmithCopy.setAttribute(UID_DEF.getName(), "jsmithCopy");
        jsmithCopy.setAttribute(CN_DEF.getName(), "jsmithCopy");
        jsmithCopy.setAttribute(SN_DEF.getName(), "jsmithCopy");
        jsmithCopy.setInternalId("cn=jsmithCopy, o=sevenSeas");
        basicTestCases.testFetchSingleEntity(entityManager, jsmithCopy);
    }
    
    @Override
    protected void internaltearDown() throws Exception
    {
    }

}
