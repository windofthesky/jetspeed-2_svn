/*
 * Created on Mar 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.tools.pamanager;

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.jetspeed.components.util.RegistrySupportedTestCase;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

/**
 * @author jford
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestJetspeedPortletDescriptor
    extends RegistrySupportedTestCase {
    
    private static final String PORTLET_01 = "HelloWorld Portlet";
    private static final String PORTLET_02 = "Display the Portlet Request Information";
    private static final String PORTLET_03 = "Pick a number game";
    private static final String PORTLET_04 = "Attribute Scope Demo";
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestJetspeedPortletDescriptor(String name)
    {
        super(name);
    }

    /**
     * @param arg0
     * @param log4jFile
     */
    public TestJetspeedPortletDescriptor(String arg0, String log4jFile) {
        super(arg0);
    }
    
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
        MutablePortletApplication app =
            PortletDescriptorUtilities.loadPortletDescriptor("./test/testdata/deploy/portlet.xml", "unit-test");
        assertNotNull("App is null", app);
        assertNotNull("Version is null", app.getVersion());
        assertTrue("Version invalid: " + app.getVersion(), app.getVersion().equals("1.0"));
        assertNotNull("PA Identifier is null", app.getApplicationIdentifier());
        assertTrue(
                "PA Identifier invalid: " + app.getApplicationIdentifier(),
                app.getApplicationIdentifier().equals("TestRegistry"));

        boolean result = JetspeedDescriptorUtilities.loadPortletDescriptor("./test/testdata/deploy/jetspeed-portlet.xml", app);
        assertTrue(result);
        
        PortletDefinitionComposite def1 = (PortletDefinitionComposite)app.getPortletDefinitionByName(PORTLET_01);
        PortletDefinitionComposite def2 = (PortletDefinitionComposite)app.getPortletDefinitionByName(PORTLET_02);
        PortletDefinitionComposite def3 = (PortletDefinitionComposite)app.getPortletDefinitionByName(PORTLET_03);
        PortletDefinitionComposite def4 = (PortletDefinitionComposite)app.getPortletDefinitionByName(PORTLET_04);
        
        Collection titles = app.getMetadata().getFields("title");
        Collection def1Titles = def1.getMetadata().getFields("title");
        Collection def2Subjects = def2.getMetadata().getFields("subject");
        Collection def3Creators = def3.getMetadata().getFields("creator");
        Collection def4Field1 = def4.getMetadata().getFields("field1");
        Collection def4Fiels2 = def4.getMetadata().getFields("field2");
        
        assertEquals(titles.size(), 3);
        assertEquals(def1Titles.size(), 4);
        assertEquals(def2Subjects.size(), 5);
        assertEquals(def3Creators.size(), 4);
        assertEquals(def4Field1.size(), 3);
        assertEquals(def4Fiels2.size(), 2);
    }

}
