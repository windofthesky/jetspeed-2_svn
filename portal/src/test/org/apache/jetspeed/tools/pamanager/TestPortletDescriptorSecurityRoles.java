/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.tools.pamanager;

import java.io.FileInputStream;
import java.util.Properties;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.om.servlet.impl.SecurityRoleImpl;
import org.apache.log4j.PropertyConfigurator;
import org.apache.pluto.om.common.SecurityRole;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.common.SecurityRoleSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.picocontainer.MutablePicoContainer;

/**
 * TestPortletDescriptorSecurityRoles - test and validate security roles and
 * security role references from portlet.xml and web.xml deployment descriptor.
 * 
 * @author <a href="ate@douma.nu">Ate Douma </a>
 * 
 * @version $Id$
 */
public class TestPortletDescriptorSecurityRoles extends AbstractComponentAwareTestCase {

    private PortletRegistryComponent registry;

    private MutablePicoContainer container;

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestPortletDescriptorSecurityRoles(String name) {
        super(name);
    }

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main(String args[]) {
        TestRunner.main(new String[] { TestPortletDescriptorSecurityRoles.class.getName()});
    }

    public static final String LOG4J_CONFIG_FILE = "log4j.file";

    // TODO: make this relative, move it into script
    public static final String LOG4J_CONFIG_FILE_DEFAULT = "src/webapp/WEB-INF/conf/Log4j.properties";

    protected void setUp() throws Exception {
        // super.setUp();

        // TODO: this is REALLY strange. If I don't setup LOG4J here Digester
        // crashes with a NPE
        // if I setup Log4J in my super class, Digester crashes ... mysterious
        // TODO: need a Logging Component

        String log4jFile = LOG4J_CONFIG_FILE_DEFAULT;
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(log4jFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(p);

        System.getProperties().setProperty(LogFactory.class.getName(), Log4jFactory.class.getName());

        container = (MutablePicoContainer) getContainer();
        registry = (PortletRegistryComponent) container.getComponentInstance(PortletRegistryComponent.class);
    }

    /**
     * Creates the test suite.
     * 
     * @return a test suite (<code>TestSuite</code>) that includes all
     *         methods starting with "test"
     */
    public static Test suite() {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestPortletDescriptorSecurityRoles.class);
        suite.setScript("org/apache/jetspeed/tools/pamanager/containers/pa-container.groovy");
        return suite;
    }

    public void testSecurityRoles() throws Exception {
        System.out.println("Testing securityRoles");
        MutablePortletApplication app = PortletDescriptorUtilities.loadPortletDescriptor("./test/testdata/deploy/security-roles/portlet.xml", "unit-test");
        assertNotNull("App is null", app);
        MutableWebApplication webApp = (MutableWebApplication) WebDescriptorUtilities.loadDescriptor("./test/testdata/deploy/security-roles/web.xml", "/",
                Jetspeed.getDefaultLocale(), "unit-test");
        assertNotNull("WebApp is null", webApp);

        app.setWebApplicationDefinition(webApp);

        PortletDefinition portlet = app.getPortletDefinitionByName("TestPortlet");
        assertNotNull("TestPortlet is null", portlet);
        checkWebSecurityRoles(webApp);
        checkPortletSecurityRoleRefs(portlet);
        boolean validateFailed = false;
        try {
            PortletDescriptorUtilities.validate(app);
        } catch (PortletApplicationException e) {
            validateFailed = true;
        }
        assertEquals("Invalid PortletDescriptor validation result", true, validateFailed);
        SecurityRoleImpl role = new SecurityRoleImpl();
        role.setRoleName("users.manager");
        webApp.addSecurityRole(role);
        try {
            PortletDescriptorUtilities.validate(app);
            validateFailed = false;
        } catch (PortletApplicationException e) {
        }
        assertEquals("Invalid PortletDescriptor validation result", false, validateFailed);
    }

    private void checkWebSecurityRoles(MutableWebApplication webApp) {
        SecurityRoleSet roles = webApp.getSecurityRoles();
        assertEquals("Invalid number of security role definitions found", 1, roles.size());
        SecurityRole role = roles.get("users.admin");
        assertNotNull("Role users.admin undefined", role);
    }

    private void checkPortletSecurityRoleRefs(PortletDefinition portlet) {
        SecurityRoleRefSet roleRefs = portlet.getInitSecurityRoleRefSet();
        assertEquals("Invalid number of security role references found", 2, roleRefs.size());
        SecurityRoleRef roleRef = roleRefs.get("admin");
        assertNotNull("Security Role Ref admin undefined", roleRef);
        assertEquals("security Role link expected", "users.admin", roleRef.getRoleLink());
        roleRef = roleRefs.get("users.manager");
        assertNotNull("Security Role Ref users.manager undefined", roleRef);
        assertNull("Undefined security Role link for users.managers expected", roleRef.getRoleLink());
    }
}
