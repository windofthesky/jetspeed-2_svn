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
package org.apache.jetspeed.components.portletregistry;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.DublinCoreImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;

/**
 * @author scott
 */
public abstract class AbstractRegistryTest extends DatasourceEnabledSpringTestCase
{

    protected static final String PORTLET_0_CLASS = "com.portlet.MyClass0";

    protected static final String PORTLET_0_NAME = "Portlet 0";

    protected static final String PORTLET_1_CLASS = "com.portlet.MyClass";

    protected static final String PORTLET_1_NAME = "Portlet 1";

    protected static final String PORTLET_1_UID = "com.portlet.MyClass.Portlet 1";

    protected static final String PORTLET_0_UID = "com.portlet.MyClass0.Portlet 0";

    protected static final String MODE_HELP = "HELP";

    protected static final String MODE_VIEW = "VIEW";

    protected static final String MODE_EDIT = "EDIT";

    public static final String APP_1_NAME = "RegistryTestPortlet";

    protected PortletRegistry registry;

    private static int testPasses = 0;

    /**
     * <p>
     * setUp
     * </p>
     * 
     * @see junit.framework.TestCase#setUp()
     * @throws Exception
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        this.registry = (PortletRegistry) ctx.getBean("portletRegistry");

        testPasses++;
    }

    /**
     * <p>
     * tearDown
     * </p>
     * 
     * @see junit.framework.TestCase#tearDown()
     * @throws Exception
     */
    protected void tearDown() throws Exception
    {
        //super.tearDown();
    }

    protected void validateDublinCore(GenericMetadata metadata)
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

    // protected void invalidate( Object[] objs ) throws LockFailedException
    // {
    // persistenceStore.getTransaction().begin();
    // for (int i = 0; i < objs.length; i++)
    // {
    // persistenceStore.invalidate(objs[i]);
    // }
    // persistenceStore.getTransaction().commit();
    // }

    protected void verifyData(boolean afterUpdates) throws Exception
    {
        MutablePortletApplication app;
        WebApplicationDefinitionImpl webApp;
        PortletDefinitionComposite portlet;

        app = null;

        app = (PortletApplicationDefinitionImpl) registry.getPortletApplication("App_1");

        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        portlet = (PortletDefinitionImpl) app.getPortletDefinitionByName("Portlet 1");

        assertNotNull("Failed to reteive portlet application", app);

        validateDublinCore(app.getMetadata());

        Collection services = app.getJetspeedServices();
        assertNotNull("jetspeed services is null", services);
        System.out.println("services is " + services);

        assertNotNull("Failed to reteive portlet application via registry", registry.getPortletApplication("App_1"));
        assertNotNull("Web app was not saved along with the portlet app.", webApp);
        assertNotNull("Portlet was not saved along with the portlet app.", app.getPortletDefinitionByName("Portlet 1"));
        if (!afterUpdates)
        {
            assertTrue("\"user.name.family\" user attribute was not found.", app.getUserAttributes().size() == 1);
        }
        else
        {
            assertTrue("\"user.name.family\" and user.pets user attributes were not found.", app.getUserAttributes()
                    .size() == 2);

        }

        portlet = (PortletDefinitionComposite) registry.getPortletDefinitionByUniqueName("App_1::Portlet 1");

        assertNotNull("Portlet could not be retreived by unique name.", portlet);

        validateDublinCore(portlet.getMetadata());

        assertNotNull("Portlet Application was not set in the portlet defintion.", portlet
                .getPortletApplicationDefinition());
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
        int valueCount = 0;

        while (itr.hasNext())
        {
            itr.next();
            valueCount++;
        }
        assertEquals("\"preference 1\" did not have 2 values.", 2, valueCount);

        // Pull out our Web app and add a Description to it

        webApp = null;

        app = registry.getPortletApplication("App_1");

        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        assertNotNull("Web app was not located by query.", webApp);
        webApp.addDescription(Locale.getDefault(), "Web app description");

        webApp = null;

        app = registry.getPortletApplication("App_1");
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();

        assertNotNull("Web app was not located by query.", webApp);
        assertNotNull("Web app did NOT persist its description", webApp.getDescription(Locale.FRENCH));

    }

    protected String[] getConfigurations()
    {
        return new String[]
        { "transaction.xml", "prefs.xml", "registry.xml" };
    }

}