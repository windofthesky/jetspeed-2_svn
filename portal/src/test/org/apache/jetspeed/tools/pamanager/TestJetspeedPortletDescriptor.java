/*
 * Created on Mar 2, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.tools.pamanager;

import java.util.Collection;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.picocontainer.MutablePicoContainer;

/**
 * @author jford
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestJetspeedPortletDescriptor
    extends AbstractComponentAwareTestCase {
    
    private PortletRegistryComponent registry;
    private MutablePicoContainer container;
    
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
        super(arg0, log4jFile);
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

    public static final String LOG4J_CONFIG_FILE = "log4j.file";
    // TODO: make this relative, move it into script
    public static final String LOG4J_CONFIG_FILE_DEFAULT = "src/webapp/WEB-INF/conf/test/Log4j.properties";
    
    protected void setUp() throws Exception
    {
        super.setUp();
        
        container = (MutablePicoContainer) getContainer();
        registry = (PortletRegistryComponent) container.getComponentInstance(PortletRegistryComponent.class);
    }
    
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestJetspeedPortletDescriptor.class);
        suite.setScript("org/apache/jetspeed/tools/pamanager/containers/pa-container.groovy");
        return suite;
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
        
        PortletDefinitionComposite def = (PortletDefinitionComposite)app.getPortletDefinitionByName("HelloWorld Portlet");
        
        Collection titles = app.getMetadata().getFields("title");
        Collection defTitles = def.getMetadata().getFields("title");
        
        assertEquals(titles.size(), 3);
        assertEquals(defTitles.size(), 3);
    }

}
