/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.ldap;

import java.util.List;

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;

/**
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestLdapGroupSecurityHandler extends AbstractLdapTest
{

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        LdapDataHelper.seedGroupData(gpUid1);
    }

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        LdapDataHelper.removeGroupData(gpUid1);
    }

    /**
     * @throws Exception
     */
    public void testGetGroupPrincipal() throws Exception
    {
        String fullPath = (new GroupPrincipalImpl(gpUid1)).getFullPath();
        assertNotNull("Group was not found.", grHandler.getGroupPrincipal(fullPath));
    }

    /**
     * @throws Exception
     */
    public void testAddDuplicateGroupPrincipal() throws Exception
    {
        grHandler.setGroupPrincipal(new GroupPrincipalImpl(gpUid1));
    }

    /**
     * @throws Exception
     */
    public void testRemoveExistantUserPrincipal() throws Exception
    {
        GroupPrincipal gp = new GroupPrincipalImpl(gpUid1);
        grHandler.removeGroupPrincipal(gp);
        assertNull("Group was found and should have been removed.", grHandler.getGroupPrincipal(gp.getFullPath()));
    }

    /**
     * @throws Exception
     */
    public void testRemoveNonExistantUserPrincipal() throws Exception
    {
        String localUid = Integer.toString(rand.nextInt()).toString();
        GroupPrincipal localPrin = new GroupPrincipalImpl(localUid);

        grHandler.removeGroupPrincipal(localPrin);
    }

    /**
     * @throws Exception
     */
    public void testGetGroupPrincipals() throws Exception
    {
        try
        {
            LdapDataHelper.seedGroupData(gpUid2);
            assertTrue("getUserPrincipals should have returned more than one user.", grHandler.getGroupPrincipals("*")
                    .size() > 1);

            String fullPath = (new GroupPrincipalImpl(gpUid1)).getFullPath();
            List groups = grHandler.getGroupPrincipals(fullPath);
            assertTrue("getGroupPrincipals should have returned one group.", groups.size() == 1);
            assertTrue("List should have consisted of GroupPrincipal objects.", groups.get(0) instanceof GroupPrincipal);

            String localUid = Integer.toString(rand.nextInt()).toString();
            assertTrue("getGroupPrincipals should not have found any groups with the specified filter.", grHandler
                    .getGroupPrincipals(new GroupPrincipalImpl(localUid).getFullPath()).isEmpty());
        }
        finally
        {
            LdapDataHelper.removeGroupData(gpUid2);
        }
    }

}