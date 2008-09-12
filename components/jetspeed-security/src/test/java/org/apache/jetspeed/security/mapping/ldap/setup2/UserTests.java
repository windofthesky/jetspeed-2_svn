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

import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.impl.EntityImpl;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class UserTests extends AbstractSetup2LDAPTest
{

    public void testSingleUser() throws Exception
    {
        EntityImpl sampleUser = new EntityImpl("user", "someManager", userAttrDefs);
        sampleUser
                .setInternalId("cn=someManager, ou=People, ou=rootOrg, o=sevenSeas");
        sampleUser.setAttribute(GIVEN_NAME_DEF.getName(), "Some Manager");
        sampleUser.setAttribute(UID_DEF.getName(), "someManager");
        sampleUser.setAttribute(CN_DEF.getName(), "someManager");
        Collection<String> roles = new ArrayList<String>();
        roles.add("manager");
        roles.add("user");
        sampleUser.setAttribute(J2_ROLE_DEF.getName(), roles);
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);
    }

    public void testFetchRolesForUserByRoleAttribute() throws Exception
    {
        EntityImpl managerRole = new EntityImpl("role", "manager", roleAttrDefs);
        managerRole.setInternalId("cn=manager, ou=Roles, ou=rootOrg, o=sevenSeas");
        managerRole.setAttribute(DESCRIPTION_ATTR_DEF.getName(), "Manager Role");
        managerRole.setAttribute(CN_DEF.getName(), "manager");
        managerRole.setAttribute(UID_DEF.getName(), "manager");

        EntityImpl userRole = new EntityImpl("role", "user", roleAttrDefs);
        userRole.setInternalId("cn=user, ou=Roles, ou=rootOrg, o=sevenSeas");
        userRole.setAttribute(DESCRIPTION_ATTR_DEF.getName(), "User Role");
        userRole.setAttribute(CN_DEF.getName(), "user");
        userRole.setAttribute(UID_DEF.getName(), "user");

        Collection<Entity> resultSet = new ArrayList<Entity>();
        resultSet.add(managerRole);
        resultSet.add(userRole);
        
        // test fetching roles for a user
        basicTestCases.testFetchRelatedEntitiesTo("user", "role", "hasRole",
                "someManager", resultSet);

        // .. next, test fetching users for a role using the same EntityRelationDAO
        Entity user = createUser("someManager", 
                "cn=someManager, ou=People, ou=rootOrg, o=sevenSeas",
                 "Some Manager","someManager","someManager",new String[]{"manager","user"});
        Entity jetspeed = createUser("jetspeed", 
                "cn=jetspeed, ou=People, ou=rootOrg, o=sevenSeas",
                 "jetspeed","jetspeed","jetspeed",new String[]{"manager"});
        Entity admin = createUser("admin", 
                "cn=admin, ou=People, ou=rootOrg, o=sevenSeas",
                 "Admin","admin","admin",new String[]{"admin","manager","user"});
        
        
        resultSet = new ArrayList<Entity>();
        resultSet.add(user);
        resultSet.add(jetspeed);
        resultSet.add(admin);
        basicTestCases.testFetchRelatedEntitiesFrom("user", "role", "hasRole",
                "manager", resultSet);

    }
    
    private Entity createUser(String id, String internalId, String givenName, String cn, String uid, String[] roles){
        EntityImpl user = new EntityImpl("user", id, userAttrDefs);
        user.setInternalId(internalId);
        user.setAttribute(GIVEN_NAME_DEF.getName(), givenName);
        user.setAttribute(CN_DEF.getName(), cn);
        user.setAttribute(UID_DEF.getName(), uid);
        Collection<String> roleValues=new ArrayList<String>();
        for (int i = 0; i < roles.length; i++)
        {
            roleValues.add(roles[i]);
        }
        user.setAttribute(J2_ROLE_DEF.getName(), roleValues);
        return user;
    }

}
