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
package org.apache.jetspeed.layout;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.apache.jetspeed.layout.impl.LayoutValve;
import org.apache.jetspeed.mocks.ResourceLocatingServletContext;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.testhelpers.AbstractTestPrincipal;
import org.jmock.Mock;

import javax.security.auth.Subject;
import java.io.File;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test Security Constraints Manipulation
 *  
 * @author <a>David Sean Taylor </a>
 * @version $Id: $
 */
public class TestConstraintsAction extends JetspeedTestCase
{

    private ComponentManager cm;

    private LayoutValve valve;
    
    private PageManager pageManager;

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(TestConstraintsAction.class);
    }

    /**
     * Setup the request context
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        MockServletConfig servletConfig = new MockServletConfig();        
        ResourceLocatingServletContext servletContent = new ResourceLocatingServletContext(new File(getBaseDir()));        
        servletConfig.setServletContext(servletContent);
        ServletConfigFactoryBean.setServletConfig(servletConfig);
        
        // Load the Spring configs
        String[] bootConfigs = null;
        String[] appConfigs =
        { //"src/webapp/WEB-INF/assembly/layout-api.xml",
                "src/test/assembly/test-layout-constraints-api.xml",
                "src/test/assembly/page-manager.xml",
                "src/test/assembly/cache-test.xml"};
        
                
        cm = new SpringComponentManager(null, bootConfigs, appConfigs, servletContent, getBaseDir());
        cm.start();
        valve = cm.lookupComponent("layoutValve");
        pageManager = cm.lookupComponent("pageManager");
    }

    protected void tearDown() throws Exception
    {
        cm.stop();
        super.tearDown();
    }

    public void testUpdate()
    throws Exception
    {
        String method = "update-def";
        String defName = "users";
        String xml =
            "<security-constraints-def name=\"" + 
                  defName + 
                  "\"><security-constraint><roles>user, manager</roles><permissions>view,edit</permissions></security-constraint></security-constraints-def>";
        runTest(xml, defName, method);
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        SecurityConstraintsDef def = pageSecurity.getSecurityConstraintsDef(defName);
        assertNotNull("definition " + defName + " not found ", def);
        assertNotNull("first constraint for " + defName + " not found ", def);
        List constraints = def.getSecurityConstraints();
        assertFalse("constraint list is empty.", constraints.isEmpty());
        SecurityConstraint constraint =  (SecurityConstraint) constraints.get(0);
        assertEquals("update failed for constraints " + constraint.getPermissions().toString(), constraint.getPermissions().toString(), "[view, edit]");
    }

    public void testAdd()
    throws Exception
    {
        String method = "add-def";
        String defName = "newone";
        String xml =
            "<security-constraints-def name=\"" + 
                  defName + 
                  "\"><security-constraint><roles>user, manager</roles><permissions>view,edit</permissions></security-constraint></security-constraints-def>";
        runTest(xml, defName, method);
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        SecurityConstraintsDef def = pageSecurity.getSecurityConstraintsDef(defName);
        assertNotNull("definition " + defName + " not found ", def);
        SecurityConstraint constraint =  (SecurityConstraint)def.getSecurityConstraints().get(0);
        assertNotNull("first constraint for " + defName + " not found ", def);
        assertEquals("update failed for constraints " + constraint.getPermissions().toString(), constraint.getPermissions().toString(), "[view, edit]");
    }
    
    public void testAdds()
    throws Exception
    {
        String method = "update-def";        
        String defName = "users";
        String xml =
            "<security-constraints-def name=\"" + 
                  defName + 
                  "\"><security-constraint><roles>user, manager,anon</roles><permissions>view,edit,help</permissions></security-constraint>" +
                  "<security-constraint><groups>accounting,finance</groups><permissions>view,edit,help</permissions></security-constraint>" +
                  "<security-constraint><users>tomcat</users><permissions>view</permissions></security-constraint>" +
                  "<security-constraint><users>manager,admin</users><permissions>view,help</permissions></security-constraint>" +
                  "</security-constraints-def>";
                  
        runTest(xml, defName, method);
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        SecurityConstraintsDef def = pageSecurity.getSecurityConstraintsDef(defName);
        assertNotNull("definition " + defName + " not found ", def);
        SecurityConstraint constraint =  (SecurityConstraint)def.getSecurityConstraints().get(0);
        assertNotNull("first constraint for " + defName + " not found ", constraint);
        assertEquals("update failed for constraints " + constraint.getPermissions().toString(), constraint.getPermissions().toString(), "[view, edit, help]");
        assertEquals("update failed for constraints " + constraint.getRoles().toString(), constraint.getRoles().toString(), "[user, manager, anon]");
        
        SecurityConstraint constraint2 =  (SecurityConstraint)def.getSecurityConstraints().get(1);
        assertNotNull("second constraint for " + defName + " not found ", constraint2);
        assertEquals("add failed for constraints " + constraint2.getPermissions().toString(), constraint2.getPermissions().toString(), "[view, edit, help]");
        assertEquals("add failed for constraints " + constraint2.getGroups().toString(), constraint2.getGroups().toString(), "[accounting, finance]");

        SecurityConstraint constraint3 =  (SecurityConstraint)def.getSecurityConstraints().get(2);
        assertNotNull("third constraint for " + defName + " not found ", constraint3);
        assertEquals("add failed for constraints " + constraint3.getPermissions().toString(), constraint3.getPermissions().toString(), "[view]");
        assertEquals("add failed for constraints " + constraint3.getUsers().toString(), constraint3.getUsers().toString(), "[tomcat]");

        SecurityConstraint constraint4 =  (SecurityConstraint)def.getSecurityConstraints().get(3);
        assertNotNull("fourth constraint for " + defName + " not found ", constraint4);
        assertEquals("add failed for constraints " + constraint4.getPermissions().toString(), constraint4.getPermissions().toString(), "[view, help]");
        assertEquals("add failed for constraints " + constraint4.getUsers().toString(), constraint4.getUsers().toString(), "[manager, admin]");
        
    }

    public void testDeletes()
    throws Exception
    {
        String method = "update-def";        
        String defName = "delete3";
        String xml =
            "<security-constraints-def name=\"" + 
                  defName + 
                  "\"><security-constraint><users>*</users><permissions>view</permissions></security-constraint></security-constraints-def>";
        runTest(xml, defName, method);
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        SecurityConstraintsDef def = pageSecurity.getSecurityConstraintsDef(defName);
        assertNotNull("definition " + defName + " not found ", def);
        SecurityConstraint constraint =  (SecurityConstraint)def.getSecurityConstraints().get(0);
        assertNotNull("first constraint for " + defName + " not found ", def);
        assertEquals("delete merge failed for constraints " + constraint.getPermissions().toString(), constraint.getPermissions().toString(), "[view]");
        assertEquals("delete merge failed for constraints " + constraint.getUsers().toString(), constraint.getUsers().toString(), "[*]");        
        assertTrue("constrainst size should be 1 ", def.getSecurityConstraints().size() == 1);        
    }

    public void testDeleteDef()
    throws Exception
    {
        String method = "remove-def";        
        String defName = "deleteme";
        String xml = "";
        runTest(xml, defName, method);
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        SecurityConstraintsDef def = pageSecurity.getSecurityConstraintsDef(defName);
        assertNull("definition " + defName + " should be deleted ", def);
    }

    public void testAddGlobal()
    throws Exception
    {
        String method = "add-global";        
        String defName = "manager";
        String xml = "";
        runTest(xml, defName, method);
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        List globals = pageSecurity.getGlobalSecurityConstraintsRefs();
        assertTrue("should have found new global " + defName,  globals.contains(defName));
        assertTrue("should have found old global " + defName,  globals.contains("admin"));
    }

    public void testDeleteGlobal()
    throws Exception
    {
        PageSecurity pageSecurity = pageManager.getPageSecurity();        
        String method = "add-global";        
        String defName = "public-edit";
        String xml = "";        
        runTest(xml, defName, method);
        List globals = pageSecurity.getGlobalSecurityConstraintsRefs();
        assertTrue("should have found new global " + defName,  globals.contains(defName));
        method = "remove-global";        
        runTest(xml, defName, method);
        globals = pageSecurity.getGlobalSecurityConstraintsRefs();
        assertFalse("should have not found new global " + defName,  globals.contains(defName));
    }
    
    public void runTest(String xml, String defName, String method)
    throws Exception
    {
        RequestContextComponent rcc = (RequestContextComponent) new Mock(RequestContextComponent.class).proxy();
        
        MockServletConfig config = new MockServletConfig();
        MockServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        session.setupServletContext(context);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setupAddParameter("action", "constraints");
        request.setupAddParameter("method", method);
        request.setupAddParameter("xml", xml);
        request.setupAddParameter("name", defName);
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        final RequestContext rc = 
            new JetspeedRequestContext(rcc, request, response, config, null);
        
        Set principals = new HashSet();
        principals.add(new TestUser("admin"));
        principals.add(new TestRole("admin"));
        principals.add(new TestRole("user"));        
        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());
        
        JSSubject.doAsPrivileged(subject, new PrivilegedAction()
                {
                    public Object run() 
                    {
                         try
                        {
                             valve.invoke(rc, null);                 
                            return null;
                        }
                        catch (PipelineException e)
                        {
                            return e;
                        }                    
                    }
                }, null);
     
        
    }
        
    static class TestUser extends AbstractTestPrincipal implements User
    {
        private static final long serialVersionUID = 1L;

        public TestUser(String name)
        {
            super(JetspeedPrincipalType.USER, name);
        }
    }

    static class TestRole extends AbstractTestPrincipal implements Role
    {
        private static final long serialVersionUID = 1L;

        public TestRole(String name)
        {
            super(JetspeedPrincipalType.ROLE, name);
        }
    }
}
