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
package org.apache.jetspeed.tools.pamanager;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.jetspeed.AbstractRequestContextTestCase;
import org.apache.jetspeed.descriptor.JetspeedDescriptorService;
import org.apache.jetspeed.descriptor.JetspeedDescriptorServiceImpl;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.SecurityRole;
import org.apache.jetspeed.om.portlet.SecurityRoleRef;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;
import org.apache.pluto.container.impl.PortletAppDescriptorServiceImpl;

/**
 * TestPortletDescriptorSecurityRoles - test and validate security roles and
 * security role references from portlet.xml and web.xml deployment descriptor.
 *
 * @author <a href="ate@douma.nu">Ate Douma </a>
 *
 * @version $Id: TestPortletDescriptorSecurityRoles.java,v 1.4 2004/05/27
 *                19:57:24 weaver Exp $
 */
public class TestPortletDescriptorSecurityRoles extends AbstractRequestContextTestCase
{

    /**
     * Start the tests.
     *
     * @param args
     *                  the arguments. Not used
     */
    public static void main( String args[] )
    {
        TestRunner.main(new String[]{TestPortletDescriptorSecurityRoles.class.getName()});
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all
     *              methods starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortletDescriptorSecurityRoles.class);
    }

    public void setUp() throws Exception
    {
        super.setUp();
        
    }
    
    public void testSecurityRoles() throws Exception
    {
        System.out.println("Testing securityRoles");
        File warFile = new File(getBaseDir()+"src/test/testdata/deploy/webapp");
        JetspeedDescriptorService descriptorService = new JetspeedDescriptorServiceImpl(new PortletAppDescriptorServiceImpl());
        PortletApplicationWar paWar = new PortletApplicationWar(new DirectoryHelper(warFile), "unit-test", "/", descriptorService );

        PortletApplication portletApp = null;
        
        boolean validateFailed = false;
        try
        {
            // From 2.2, createPortletApp() will do validation also.
            portletApp = paWar.createPortletApp();
        }
        catch (PortletApplicationException e)
        {
            validateFailed = true;
            portletApp = paWar.getPortletApp();
        }
        assertTrue("Invalid PortletDescriptor validation result", validateFailed);
        
        assertNotNull("portletApp is null", portletApp);

        PortletDefinition portlet = portletApp.getPortlet("TestPortlet");
        assertNotNull("TestPortlet is null", portlet);
        checkPortletApplicationSecurityRoles(portletApp);
        checkPortletSecurityRoleRefs(portlet);
        
        portletApp.addSecurityRole("users.manager");

        try
        {
            paWar.validate();
            validateFailed = false;
        }
        catch (PortletApplicationException e)
        {
        }
        assertEquals("Invalid PortletDescriptor validation result", false, validateFailed);

    }

    private void checkPortletApplicationSecurityRoles( PortletApplication portletApp )
    {
        List<SecurityRole> roles = portletApp.getSecurityRoles();
        assertEquals("Invalid number of security role definitions found", 1, roles.size());
        boolean roleFound = false;
        
        for (SecurityRole role : roles)
        {
            if ("users.admin".equals(role.getName()))
            {
                roleFound = true;
                break;
            }
        }
        
        assertTrue("Role users.admin undefined", roleFound);
    }

    private void checkPortletSecurityRoleRefs( PortletDefinition portlet )
    {
        List<SecurityRoleRef> roleRefs = portlet.getSecurityRoleRefs();
        assertEquals("Invalid number of security role references found", 2, roleRefs.size());
        
        SecurityRoleRef roleRef = portlet.getSecurityRoleRef("admin");
        assertNotNull("Security Role Ref admin undefined", roleRef);
        assertEquals("security Role link expected", "users.admin", roleRef.getRoleLink());
        
        roleRef = portlet.getSecurityRoleRef("users.manager");
        assertNotNull("Security Role Ref users.manager undefined", roleRef);
        assertNull("Undefined security Role link for users.managers expected", roleRef.getRoleLink());
    }
}
