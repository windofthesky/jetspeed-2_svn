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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;

/**
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a> 
 */
public class TestLdapGroupSecurityHandler extends AbstractLdapTest
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(TestLdapGroupSecurityHandler.class);

    public void testGetGroupPrincipal()
    {
        assertNotNull("Group was not found.", grHandler.getGroupPrincipal(gp1.getFullPath()));
    }

    public void testAddDuplicateGroupPrincipal() throws SecurityException
    {
        grHandler.setGroupPrincipal(new GroupPrincipalImpl(gpUid1));
    }

    public void testRemoveExistantUserPrincipal() throws SecurityException
    {
        grHandler.removeGroupPrincipal((gp1));
        assertNull("Group was found and should have been removed.", grHandler.getGroupPrincipal(gp1.getFullPath()));
    }

    public void testRemoveNonExistantUserPrincipal() throws SecurityException
    {
        String localUid = Integer.toString(rand.nextInt()).toString();
        GroupPrincipal localPrin = new GroupPrincipalImpl(localUid);

        grHandler.removeGroupPrincipal(localPrin);
    }

    public void testGetGroupPrincipals() throws SecurityException
    {
        assertTrue("getUserPrincipals should have returned more than one user.", grHandler.getGroupPrincipals("*")
                .size() > 1);

        List groups = grHandler.getGroupPrincipals(gp1.getFullPath());

        assertTrue("getGroupPrincipals should have returned one group.", groups.size() == 1);
        assertTrue("List should have consisted of GroupPrincipal objects.", groups.get(0) instanceof GroupPrincipal);

        String localUid = Integer.toString(rand.nextInt()).toString();

        assertTrue("getGroupPrincipals should not have found any groups with the specified filter.", grHandler
                .getGroupPrincipals(new GroupPrincipalImpl(localUid).getFullPath()).isEmpty());
    }

}