/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.portletregistry;
import java.util.Iterator;
import java.util.Locale;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.NanoDeployerBasedTestSuite;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.impl.DublinCoreImpl;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.picocontainer.MutablePicoContainer;

/**
 * 
 * TestRegistry runs a suite tests creating, updating, retreiving and deleting
 * portlet information from the registry.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class TestRegistryDirectPart2 extends AbstractComponentAwareTestCase
{

    private MutablePicoContainer container;
    private static final String PORTLET_0_CLASS = "com.portlet.MyClass0";
    private static final String PORTLET_0_NAME = "Portlet 0";
    private static final String PORTLET_1_CLASS = "com.portlet.MyClass";
    private static final String PORTLET_1_NAME = "Portlet 1";
    private static final String PORTLET_1_UID = "com.portlet.MyClass.Portlet 1";
    private static final String PORTLET_0_UID = "com.portlet.MyClass0.Portlet 0";
    private static final String MODE_HELP = "HELP";
    private static final String MODE_VIEW = "VIEW";
    private static final String MODE_EDIT = "EDIT";
    private static int testPasses = 0;
    public static final String APP_1_NAME = "RegistryTestPortlet";
    private static PortletRegistryComponent registry;
    private static PersistenceStore store;

    public void testContainer()
    {
        assertNotNull(container);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        registry = (PortletRegistryComponent) container.getComponentInstance(PortletRegistryComponent.class);
        store = registry.getPersistenceStore();
        
       
        
        testPasses++;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
              
        
        super.tearDown();
    }

    public static Test suite()
    {
       // ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestRegistryDirectPart2.class);
       // suite.setScript("org/apache/jetspeed/containers/test.registry.groovy");
        NanoDeployerBasedTestSuite suite = new NanoDeployerBasedTestSuite(TestRegistryDirectPart2.class);
        return suite;
    }

    

    /**
     * @param testName
     */
    public TestRegistryDirectPart2(String testName)
    {
        super(testName, "./src/test/Log4j.properties");
    }
    
    
    public void test001() throws Exception
    {
      
        // now makes sure everthing got persisted
       
        store.getTransaction().begin();
        PortletApplicationDefinitionImpl app = null;
        Filter filter = store.newFilter();
        filter.addEqualTo("name", "App_1");
        app =(PortletApplicationDefinitionImpl) store.getObjectByQuery(store.newQuery(PortletApplicationDefinitionImpl.class, filter));        
        store.getTransaction().commit();
        assertNotNull("Failed to reteive portlet application", app);
        
        validateDublinCore(app.getMetadata());
        
        WebApplicationDefinitionImpl webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        PortletDefinitionComposite portlet = (PortletDefinitionComposite)app.getPortletDefinitionByName("Portlet 1");

        store.invalidateAll();
        
        assertNotNull("Failed to reteive portlet application via registry", registry.getPortletApplication("App_1"));
        assertNotNull("Web app was not saved along with the portlet app.", webApp);
        assertNotNull("Portlet was not saved along with the portlet app.", app.getPortletDefinitionByName("Portlet 1"));
        portlet = (PortletDefinitionComposite) registry.getPortletDefinitionByUniqueName("App_1::Portlet 1");
        assertNotNull("Portlet could not be retreived by unique name.", portlet);
        
        validateDublinCore(portlet.getMetadata());
        
        assertNotNull("Portlet Application was not set in the portlet defintion.", portlet.getPortletApplicationDefinition());
        assertNotNull("French description was not materialized for the web app.", webApp.getDescription(Locale.FRENCH));
        assertNotNull("French display name was not materialized for the web app.", webApp.getDisplayName(Locale.FRENCH));
        assertNotNull("description was not materialized for the portlet.", portlet.getDescription(Locale.getDefault()));
        assertNotNull("display name was not materialized for the portlet.", portlet.getDisplayName(Locale.getDefault()));
        assertNotNull("\"testparam\" portlet parameter was not saved", portlet.getInitParameterSet().get("testparam"));
        assertNotNull("\"preference 1\" was not found.", portlet.getPreferenceSet().get("preference 1"));
        assertNotNull("Language information not found for Portlet 1", portlet.getLanguageSet().get(Locale.getDefault()));
        assertNotNull("Content Type html not found.", portlet.getContentTypeSet().get("html/text"));
        assertNotNull("Content Type wml not found.", portlet.getContentTypeSet().get("wml"));
        Iterator itr = portlet.getPreferenceSet().get("preference 1").getValues();
        int valueCount = 0;;
        while(itr.hasNext())
        {
            itr.next();
            valueCount++;
        }
        assertEquals("\"preference 1\" did not have to values.", 2, valueCount );

        store.getTransaction().begin();
        webApp = null;
        filter = store.newFilter();
        filter.addEqualTo("name", "App_1");
        app =(PortletApplicationDefinitionImpl) store.getObjectByQuery(store.newQuery(PortletApplicationDefinitionImpl.class, filter));            
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        
        store.getTransaction().commit();
        assertNotNull("Web app was not located by query.", webApp);
        assertNotNull("Web app did NOT persist its description", webApp.getDescription(Locale.getDefault()));
 
        registry.removeApplication(app);
    }
    
    private void validateDublinCore(GenericMetadata metadata)
    {
        DublinCoreImpl dc = new DublinCoreImpl(metadata);
        assertEquals(dc.getTitles().size(), 3);
        assertEquals(dc.getContributors().size(), 1);
        assertEquals(dc.getCoverages().size(), 2);
        assertEquals(dc.getCreators().size(), 1);
        assertEquals(dc.getDescriptions().size(), 1);
        assertEquals(dc.getFormats().size(), 1);
        assertEquals(dc.getIdentifiers().size(), 1);
        assertEquals(dc.getLanguages().size(), 1);
        assertEquals(dc.getPublishers().size(), 1);
        assertEquals(dc.getRelations().size(), 1);
        assertEquals(dc.getRights().size(), 1);
        assertEquals(dc.getSources().size(), 1);
        assertEquals(dc.getSubjects().size(), 1);
        assertEquals(dc.getTypes().size(), 1);        
    }
    

    protected void invalidate(Object[] objs) throws LockFailedException
    {
        store.getTransaction().begin();
        for (int i = 0; i < objs.length; i++)
        {
            store.invalidate(objs[i]);
        }
        store.getTransaction().commit();
    }
}
