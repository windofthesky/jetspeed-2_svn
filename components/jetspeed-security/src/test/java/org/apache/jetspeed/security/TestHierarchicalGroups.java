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
package org.apache.jetspeed.security;

import java.util.List;

import javax.security.auth.Subject;

import junit.framework.Test;

/**
 * Test construction and application of hierarchical groups.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class TestHierarchicalGroups extends AbstractLDAPSecurityTestCase
{
    /**
     * Test runs all test*() methods by default.
     * 
     * @return test suite definition.
     */
    public static Test suite()
    {
        return createFixturedTestSuite(TestHierarchicalGroups.class, "ldapTestSetup", "ldapTestTeardown");
    }

    /**
     * Test case for hierarchical groups.
     */
    public void testHierarchicalGroups()
    {
        try
        {
            Group organization = null;
            if (gms.groupExists("organization"))
            {
                organization = gms.getGroup("organization");
            }
            else
            {
                organization = gms.addGroup("organization");
            }
            Role employee = null;
            if (rms.roleExists("employee"))
            {
                employee = rms.getRole("employee");
            }
            else
            {
                employee = rms.addRole("employee");
            }
            List<Role> organizationRoles = rms.getRolesInGroup(organization.getName());
            if (organizationRoles.isEmpty())
            {
                rms.addRoleToGroup(employee.getName(), organization.getName());
                organizationRoles = rms.getRolesInGroup(organization.getName());
            }
            assertTrue("employee is associated with organization", organizationRoles.contains(employee));

            Group department = null;
            if (gms.groupExists("department"))
            {
                department = gms.getGroup("department");
            }
            else
            {
                department = gms.addGroup("department");
            }
            List<Group> departmentOwnerGroups = gms.getGroupsAssociatedFrom(department, JetspeedPrincipalAssociationType.IS_A);
            if (departmentOwnerGroups.isEmpty())
            {
                gms.addGroupToGroup(department, organization, JetspeedPrincipalAssociationType.IS_A);
                departmentOwnerGroups = gms.getGroupsAssociatedFrom(department, JetspeedPrincipalAssociationType.IS_A);
            }
            assertTrue("department is part of organization", departmentOwnerGroups.contains(organization));
            Role departmentMember = null;
            if (rms.roleExists("department-member"))
            {
                departmentMember = rms.getRole("department-member");
            }
            else
            {
                departmentMember = rms.addRole("department-member");
            }
            List<Role> departmentRoles = rms.getRolesInGroup(department.getName());
            if (departmentRoles.isEmpty())
            {
                rms.addRoleToGroup(departmentMember.getName(), department.getName());
                departmentRoles = rms.getRolesInGroup(department.getName());
            }
            assertTrue("department-member is associated with department", departmentRoles.contains(departmentMember));

            Group team = null;
            if (gms.groupExists("team"))
            {
                team = gms.getGroup("team");
            }
            else
            {
                team = gms.addGroup("team");
            }
            List<Group> teamOwnerGroups = gms.getGroupsAssociatedFrom(team, JetspeedPrincipalAssociationType.IS_A);
            if (teamOwnerGroups.isEmpty())
            {
                gms.addGroupToGroup(team, department, JetspeedPrincipalAssociationType.IS_A);
                teamOwnerGroups = gms.getGroupsAssociatedFrom(team, JetspeedPrincipalAssociationType.IS_A);
            }
            assertTrue("team is part of department", teamOwnerGroups.contains(department));
            Role teamMember = null;
            if (rms.roleExists("team-member"))
            {
                teamMember = rms.getRole("team-member");
            }
            else
            {
                teamMember = rms.addRole("team-member");
            }
            List<Role> teamRoles = rms.getRolesInGroup(team.getName());
            if (teamRoles.isEmpty())
            {
                rms.addRoleToGroup(teamMember.getName(), team.getName());
                teamRoles = rms.getRolesInGroup(team.getName());
            }
            assertTrue("team-member is associated with team", teamRoles.contains(teamMember));

            User person = null;
            if (ums.userExists("person"))
            {
                person = ums.getUser("person");
            }
            else
            {
                person = ums.addUser("person");
            }
            List<Group> personGroups = gms.getGroupsForUser(person.getName());
            if (personGroups.isEmpty())
            {
                gms.addUserToGroup(person.getName(), department.getName());
                personGroups = gms.getGroupsForUser(person.getName());
            }
            assertTrue("person is a member of department", personGroups.contains(department));

            Subject subject = ums.getSubject(person);
            assertTrue("person in department", (SubjectHelper.getPrincipal(subject, Group.class, department.getName()) != null));
            assertTrue("person in department-member", (SubjectHelper.getPrincipal(subject, Role.class, departmentMember.getName()) != null));
            assertTrue("person in organization", (SubjectHelper.getPrincipal(subject, Group.class, organization.getName()) != null));
            assertTrue("person in employee", (SubjectHelper.getPrincipal(subject, Role.class, employee.getName()) != null));
            assertFalse("person in team", (SubjectHelper.getPrincipal(subject, Group.class, team.getName()) != null));
            assertFalse("person in team-member", (SubjectHelper.getPrincipal(subject, Role.class, teamMember.getName()) != null));
        }
        catch (SecurityException se)
        {
            throw new RuntimeException("Unexpected security exception: "+se, se);
        }
        finally
        {
            try
            {
                if (ums.userExists("person"))
                {
                    ums.removeUser("person");
                }
                if (rms.roleExists("team-member"))
                {
                    rms.removeRole("team-member");
                }
                if (gms.groupExists("team"))
                {
                    gms.removeGroup("team");
                }
                if (rms.roleExists("department-member"))
                {
                    rms.removeRole("department-member");
                }
                if (gms.groupExists("department"))
                {
                    gms.removeGroup("department");
                }
                if (rms.roleExists("employee"))
                {
                    rms.removeRole("employee");
                }
                if (gms.groupExists("organization"))
                {
                    gms.removeGroup("organization");
                }
            }
            catch (SecurityException se)
            {
            }
        }
    }
}
