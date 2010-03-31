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
package org.apache.jetspeed.security.mapping.ldap.setup2;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;

import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.impl.EntityImpl;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class TestLDAP2 extends AbstractSetup2LDAPTest
{
    public static Test suite()
    {
        return createFixturedTestSuite(TestLDAP2.class, "ldapTestSetup", "ldapTestTeardown");
    }

    public void testSingleUser() throws Exception
    {
        EntityImpl sampleUser = new EntityImpl("user", "thomas", userAttrDefs);
        sampleUser
                .setInternalId("cn=Thomas, o=Peoples, o=Amsterdam, o=Jetspeed, o=sevenSeas");
        sampleUser.setAttribute(GIVEN_NAME_DEF.getName(), "Thomas");
        sampleUser.setAttribute(UID_DEF.getName(), "thomas");
        sampleUser.setAttribute(CN_DEF.getName(), "Thomas");
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);
    }

    private EntityImpl getFinanceRole(){
        EntityImpl financeRole = new EntityImpl("role", "Finance", roleAttrDefs);
        financeRole.setInternalId("cn=Finance, ou=Roles, o=Jetspeed, o=sevenSeas");
        financeRole.setAttribute(CN_DEF.getName(), "Finance");
        Collection<String> members = new ArrayList<String>();
        members.add("cn=David,o=Peoples,o=SanFrancisco,o=Jetspeed,o=sevenSeas");
        financeRole.setAttribute(UNIQUEMEMBER_ATTR_DEF.getName(), members);
        return financeRole;
    }

    private EntityImpl getUsersRole(){
        EntityImpl usersRole = new EntityImpl("role", "Users", roleAttrDefs);
        usersRole.setInternalId("cn=Users, ou=Roles, o=Jetspeed, o=sevenSeas");
        usersRole.setAttribute(CN_DEF.getName(), "Users");
        Collection<String> members = new ArrayList<String>();
        members.add("cn=David,o=Peoples,o=SanFrancisco,o=Jetspeed,o=sevenSeas");
        members.add("cn=Paul,o=People,o=Amsterdam,o=Jetspeed,o=sevenSeas");
        members.add("cn=Thomas,o=Peoples,o=Amsterdam,o=Jetspeed,o=sevenSeas");
        usersRole.setAttribute(UNIQUEMEMBER_ATTR_DEF.getName(), members);
        return usersRole;
    }

    public void testFetchRolesForUserByRoleAttribute() throws Exception
    {
        EntityImpl userRole = getUsersRole();
        EntityImpl financeRole = getFinanceRole();
        Collection<Entity> resultSet = new ArrayList<Entity>();
        resultSet.add(userRole);
        resultSet.add(financeRole);
        // test fetching roles for a user
        basicTestCases.testFetchRelatedEntitiesFrom("user", "role", "hasRole",
                "David", resultSet);


    }
    
    private EntityImpl getGroup(String id, String description){
        EntityImpl group = new EntityImpl("group", id, groupAttrDefs);
        if (description != null){
            group.setAttribute(DESCRIPTION_ATTR_DEF.getName(), description);
        }
        group.setAttribute(CN_DEF.getName(), id);
        return group;
    }
    
    public void testAddNestedEntities() throws Exception {
        Entity marketingGroup = entityManager.getEntity("group", "Marketing");
        
        assertNotNull(marketingGroup);
        
        EntityImpl nestedGroup = getGroup("nestedGroup1", "Some Nested Group");
        
        entityManager.addEntity(nestedGroup, marketingGroup);
        
        Entity liveNestedGroup = entityManager.getEntity("group", nestedGroup.getId());
        assertNotNull(liveNestedGroup);
        assertEquals("cn=nestedGroup1,cn=Marketing,ou=Groups,o=Jetspeed,o=sevenSeas", liveNestedGroup.getInternalId());
    }
    
    private Entity createUser(String id, String internalId, String givenName, String cn, String uid, String[] roles){
        EntityImpl user = new EntityImpl("user", id, userAttrDefs);
        user.setInternalId(internalId);
        user.setAttribute(GIVEN_NAME_DEF.getName(), givenName);
        user.setAttribute(CN_DEF.getName(), cn);
        user.setAttribute(UID_DEF.getName(), uid);
        return user;
    }

}
