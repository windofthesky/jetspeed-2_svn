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

import javax.portlet.PortletMode;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.NanoDeployerBasedTestSuite;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.impl.DublinCoreImpl;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.portlet.impl.ContentTypeImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.preference.impl.DefaultPreferenceImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.util.JetspeedLocale;
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
public class TestRegistryDirect extends AbstractComponentAwareTestCase
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
        // ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestRegistryDirect.class);
        // suite.setScript("org/apache/jetspeed/containers/test.registry.groovy");
        NanoDeployerBasedTestSuite suite = new NanoDeployerBasedTestSuite(TestRegistryDirect.class);
        
        return suite;
    }

    

    /**
     * @param testName
     */
    public TestRegistryDirect(String testName)
    {
        super(testName, "./src/test/Log4j.properties");
    }
    
    
    public void test001() throws Exception
    {
        // Create an Application and a Web app
        assertNotNull(getContainer().getComponentInstanceOfType(PortletEntityAccessComponent.class));
        store.getTransaction().begin();
        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName("App_1");
        app.setApplicationIdentifier("App_1");
        
        addDublinCore(app.getMetadata());        
        
        WebApplicationDefinitionImpl webApp = new WebApplicationDefinitionImpl();
        webApp.setContextRoot("/app1");
        webApp.addDescription(Locale.FRENCH, "Description: La fromage est dans ma pantalon!");
        webApp.addDisplayName(Locale.FRENCH, "Display Name: La fromage est dans ma pantalon!");
        
        PortletDefinitionComposite portlet = new PortletDefinitionImpl();
        portlet.setClassName("org.apache.Portlet");
        portlet.setName("Portlet 1");
        portlet.addDescription(Locale.getDefault(),"POrtlet description.");
        portlet.addDisplayName(Locale.getDefault(),"Portlet display Name.");
        
        portlet.addInitParameter("testparam", "test value", "This is a test portlet parameter", Locale.getDefault());
        
        addDublinCore(portlet.getMetadata());
        
        PreferenceComposite pc = new DefaultPreferenceImpl();
        pc.setName("preference 1");
        pc.addValue("value 1");
        pc.addValue("value 2");
        pc.addDescription(JetspeedLocale.getDefaultLocale(), "Preference Description");
        portlet.addPreference(pc);
        
        portlet.addLanguage(
                registry.createLanguage(
                Locale.getDefault(), 
                "Portlet 1", 
                "Portlet 1", 
                "This is Portlet 1", 
                null));
        
        ContentTypeComposite html = new ContentTypeImpl();
        html.setContentType("html/text");
        ContentTypeComposite wml = new ContentTypeImpl();
        html.addPortletMode(new PortletMode(MODE_EDIT));
        html.addPortletMode(new PortletMode(MODE_VIEW));
        html.addPortletMode(new PortletMode(MODE_HELP));
        wml.setContentType("wml");
        wml.addPortletMode(new PortletMode(MODE_HELP));
        wml.addPortletMode(new PortletMode(MODE_VIEW));
        portlet.addContentType(html);
        portlet.addContentType(wml);
        
        app.addPortletDefinition(portlet);
        app.setWebApplicationDefinition(webApp);
        store.makePersistent(app);
        store.getTransaction().commit();      
      
        //invalidate(new Object[] {app});
        // now makes sure everthing got persisted
        store.getTransaction().begin();
        app = null;
        Filter filter = store.newFilter();
//        filter.addEqualTo("name", "App_1");
//        app =(PortletApplicationDefinitionImpl) store.getObjectByQuery(store.newQuery(PortletApplicationDefinitionImpl.class, filter));        
//        store.getTransaction().commit();
        app = (PortletApplicationDefinitionImpl) registry.getPortletApplication("App_1");
        
        
        
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        portlet = (PortletDefinitionImpl)app.getPortletDefinitionByName("Portlet 1");
        
        assertNotNull("Failed to reteive portlet application", app);
        
        validateDublinCore(app.getMetadata());
        
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
        
        
  
        
        // pull out our Web app and add a Description to it
        store.getTransaction().begin();
        webApp = null;
        filter = store.newFilter();
        filter.addEqualTo("name", "App_1");
        app = (PortletApplicationDefinitionImpl) store.getObjectByQuery(store.newQuery(
                PortletApplicationDefinitionImpl.class, filter));
        store.lockForWrite(app);
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        // store.lockForWrite(webApp);
        assertNotNull("Web app was not located by query.", webApp);
        webApp.addDescription(Locale.getDefault(), "Web app description");
 
        store.getTransaction().commit();

        
        // invalidate(new Object[] {webApp});
 
        
        store.getTransaction().begin();
        webApp = null;
        filter = store.newFilter();
        filter.addEqualTo("name", "App_1");
        app =(PortletApplicationDefinitionImpl) store.getObjectByQuery(store.newQuery(PortletApplicationDefinitionImpl.class, filter));            
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        
        store.getTransaction().commit();
        assertNotNull("Web app was not located by query.", webApp);
        assertNotNull("Web app did NOT persist its description", webApp.getDescription(Locale.getDefault()));
        
        
    }
    
    private void addDublinCore(GenericMetadata metadata)
    {
        DublinCore dc = new DublinCoreImpl(metadata);
        dc.addTitle(JetspeedLocale.getDefaultLocale(), "Test title 1");
        dc.addTitle(JetspeedLocale.getDefaultLocale(), "Test title 2");
        dc.addTitle(JetspeedLocale.getDefaultLocale(), "Test title 3");
        dc.addContributor(JetspeedLocale.getDefaultLocale(), "Contrib 1");
        dc.addCoverage(JetspeedLocale.getDefaultLocale(), "Coverage 1");
        dc.addCoverage(JetspeedLocale.getDefaultLocale(), "Coverage 2");
        dc.addCreator(JetspeedLocale.getDefaultLocale(), "Creator 1");
        dc.addDescription(JetspeedLocale.getDefaultLocale(), "Description 1");
        dc.addFormat(JetspeedLocale.getDefaultLocale(), "Format 1");
        dc.addIdentifier(JetspeedLocale.getDefaultLocale(), "Identifier 1");
        dc.addLanguage(JetspeedLocale.getDefaultLocale(), "Language 1");
        dc.addPublisher(JetspeedLocale.getDefaultLocale(), "Publisher 1");
        dc.addRelation(JetspeedLocale.getDefaultLocale(), "Relation 1");
        dc.addRight(JetspeedLocale.getDefaultLocale(), "Right 1");
        dc.addSource(JetspeedLocale.getDefaultLocale(), "Source 1");
        dc.addSubject(JetspeedLocale.getDefaultLocale(), "Subject 1");
        dc.addType(JetspeedLocale.getDefaultLocale(), "Type 1");
    }
    
    private void validateDublinCore(GenericMetadata metadata)
    {
        DublinCore dc = new DublinCoreImpl(metadata);
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
