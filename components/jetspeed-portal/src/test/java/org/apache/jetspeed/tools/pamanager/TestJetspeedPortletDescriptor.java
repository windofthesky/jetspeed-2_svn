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

import java.io.FileReader;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.jetspeed.AbstractRequestContextTestCase;
import org.apache.jetspeed.components.util.TransactionCacheEnabledSpringTestCase;
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.util.descriptor.ExtendedPortletMetadata;
import org.apache.jetspeed.util.descriptor.PortletApplicationDescriptor;


/**
 * Tests jetspeed-portlet.xml XML-Java mappings
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestJetspeedPortletDescriptor extends AbstractRequestContextTestCase
{
    
    private static final String PORTLET_01 = "HelloPortlet";
    private static final String PORTLET_02 = "DisplayRequestPortlet";
    private static final String PORTLET_03 = "PickANumberPortlet";
    private static final String PORTLET_04 = "AttributeScopePortlet";
    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        TestRunner.main(new String[] { TestPortletDescriptor.class.getName()});
    }

   
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestJetspeedPortletDescriptor.class);
    }
    
    public void testLoadPortletApplicationTree() throws Exception
    {
        System.out.println("Testing loadPortletApplicationTree");
        PortletApplicationDescriptor pad = new PortletApplicationDescriptor(new FileReader(getBaseDir()+"src/test/testdata/deploy/portlet.xml"), "unit-test");
        PortletApplication app = pad.createPortletApplication();            
        assertNotNull("App is null", app);
        assertNotNull("Version is null", app.getVersion());
        assertTrue("Version invalid: " + app.getVersion(), app.getVersion().equals("1.0"));
        assertNotNull("PA Identifier is null", app.getApplicationIdentifier());
        assertTrue(
                "PA Identifier invalid: " + app.getApplicationIdentifier(),
                app.getApplicationIdentifier().equals("TestRegistry"));
       
        ExtendedPortletMetadata md = new ExtendedPortletMetadata(new FileReader(getBaseDir()+"src/test/testdata/deploy/jetspeed-portlet.xml"), app); 
        md.load();
       
        PortletDefinition def1 = (PortletDefinition)app.getPortletDefinitionByName(PORTLET_01);
        PortletDefinition def2 = (PortletDefinition)app.getPortletDefinitionByName(PORTLET_02);
        PortletDefinition def3 = (PortletDefinition)app.getPortletDefinitionByName(PORTLET_03);
        PortletDefinition def4 = (PortletDefinition)app.getPortletDefinitionByName(PORTLET_04);
        
        Collection titles = app.getMetadata().getFields("title");
        Collection def1Titles = def1.getMetadata().getFields("title");
        Collection def2Subjects = def2.getMetadata().getFields("subject");
        Collection def3Creators = def3.getMetadata().getFields("creator");
        Collection def4Field1 = def4.getMetadata().getFields("field1");
        Collection def4Fiels2 = def4.getMetadata().getFields("field2");
        
        String securityRef = app.getJetspeedSecurityConstraint();
        assertEquals(titles.size(), 3);
        assertEquals(def1Titles.size(), 4);
        assertEquals(def2Subjects.size(), 5);
        assertEquals(def3Creators.size(), 4);
        assertEquals(def4Field1.size(), 3);
        assertEquals(def4Fiels2.size(), 2);
        
        // Security Constraints tests
        assertEquals(securityRef, "admin-only");
        assertEquals(def1.getJetspeedSecurityConstraint(), "users-1");
        assertEquals(def2.getJetspeedSecurityConstraint(), "users-2");
        assertEquals(def3.getJetspeedSecurityConstraint(), "users-4");
        assertNull(def4.getJetspeedSecurityConstraint());
        
        Collection servicesCollection = app.getJetspeedServices();
        assertNotNull("Metadata services is null", servicesCollection);
        assertEquals("Expected 2 service definitions", servicesCollection.size(), 2);
        Object[] services = servicesCollection.toArray();
        JetspeedServiceReference service = (JetspeedServiceReference)services[0];
        System.out.println("**** service = " + service.getName());
                
        assertEquals( ((JetspeedServiceReference)services[0]).getName(), "PortletRegistryComponent");
        assertEquals( ((JetspeedServiceReference)services[1]).getName(), "PortletEntityAccessComponent");
    }

}
