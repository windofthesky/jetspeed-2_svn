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
import java.util.Collection;

import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.impl.EntityImpl;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class UserTests extends AbstractSetup1LDAPTest
{

    public void testSingleUser() throws Exception
    {
        EntityImpl sampleUser = new EntityImpl("user", "jsmith", userAttrDefs);
        sampleUser
                .setInternalId("cn=jsmith, ou=People, ou=OrgUnit3, o=sevenSeas");
        sampleUser.setAttribute(GIVEN_NAME_DEF.getName(), "Joe Smith");
        sampleUser.setAttribute(UID_DEF.getName(), "jsmith");
        sampleUser.setAttribute(CN_DEF.getName(), "jsmith");
        basicTestCases.testFetchSingleEntity(entityManager, sampleUser);
    }

    public void testFetchRolesForUserByRoleAttribute() throws Exception
    {
        EntityImpl role1 = new EntityImpl("role", "Role1", roleAttrDefs);
        role1.setInternalId("cn=Role1, o=sevenSeas");
        role1.setAttribute(DESCRIPTION_ATTR_DEF.getName(), "Role 1");
        role1.setAttribute(CN_DEF.getName(), "Role1");

        EntityImpl role3 = new EntityImpl("role", "Role3", roleAttrDefs);
        role3.setInternalId("cn=Role3, o=sevenSeas");
        role3.setAttribute(DESCRIPTION_ATTR_DEF.getName(), "Role 3");
        role3.setAttribute(CN_DEF.getName(), "Role3");

        Collection<Entity> resultSet = new ArrayList<Entity>();
        resultSet.add(role1);
        resultSet.add(role3);
        basicTestCases.testFetchRelatedEntities("user", "role", "hasRole",
                "jsmith", resultSet);
    }

    @Override
    protected void internaltearDown() throws Exception
    {
    }

}
