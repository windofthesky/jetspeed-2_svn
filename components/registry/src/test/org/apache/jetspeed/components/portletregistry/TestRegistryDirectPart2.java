/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.components.portletregistry;
import java.util.Iterator;
import java.util.Locale;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent;
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
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestRegistryDirectPart2.class);
        suite.setScript("org/apache/jetspeed/containers/registry.container.groovy");
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
        
        WebApplicationDefinitionImpl webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        PortletDefinitionComposite portlet = (PortletDefinitionComposite)app.getPortletDefinitionByName("Portlet 1");

        store.invalidateAll();
        
        assertNotNull("Failed to reteive portlet application via registry", registry.getPortletApplication("App_1"));
        assertNotNull("Web app was not saved along with the portlet app.", webApp);
        assertNotNull("Portlet was not saved along with the portlet app.", app.getPortletDefinitionByName("Portlet 1"));
        portlet = (PortletDefinitionComposite) registry.getPortletDefinitionByUniqueName("App_1::Portlet 1");
        assertNotNull("Portlet could not be retreived by unique name.", portlet);
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
        
        filter =  store.newFilter();
        store.getTransaction().begin();
        store.deleteAll(store.newQuery(PortletApplicationDefinitionImpl.class, filter));
        store.getTransaction().commit();  
        
        
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
