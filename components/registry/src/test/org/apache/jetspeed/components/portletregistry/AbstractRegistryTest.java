/*
 * Created on Jun 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.portletregistry;

import java.util.Iterator;
import java.util.Locale;

import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.DublinCoreImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl;
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;

/**
 * @author scott
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public abstract class AbstractRegistryTest extends PersistenceSupportedTestCase 
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

    protected PortletRegistryComponentImpl registry;
    private static int testPasses = 0;
    

    /**
     *  
     */
    public AbstractRegistryTest()
    {
        super();
    }

    /**
     * @param arg0
     */
    public AbstractRegistryTest( String arg0 )
    {
        super(arg0);
    }

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
        registry = new PortletRegistryComponentImpl(persistenceStore);

        PropertyManagerImpl pms = new PropertyManagerImpl(persistenceStore);
        PreferencesProviderImpl provider = new PreferencesProviderImpl(persistenceStore,
                "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", false);
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

       // super.tearDown();
    }

    protected void validateDublinCore( GenericMetadata metadata )
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

    protected void invalidate( Object[] objs ) throws LockFailedException
    {
        persistenceStore.getTransaction().begin();
        for (int i = 0; i < objs.length; i++)
        {
            persistenceStore.invalidate(objs[i]);
        }
        persistenceStore.getTransaction().commit();
    }

    protected void verifyData() throws Exception
    {
        PortletApplicationDefinitionImpl app;
        WebApplicationDefinitionImpl webApp;
        PortletDefinitionComposite portlet;

        // Now makes sure everthing got persisted
        persistenceStore.getTransaction().begin();
        app = null;
        Filter filter = persistenceStore.newFilter();
        app = (PortletApplicationDefinitionImpl) registry.getPortletApplication("App_1");

        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        portlet = (PortletDefinitionImpl) app.getPortletDefinitionByName("Portlet 1");

        assertNotNull("Failed to reteive portlet application", app);

        validateDublinCore(app.getMetadata());

        assertNotNull("Failed to reteive portlet application via registry", registry.getPortletApplication("App_1"));
        assertNotNull("Web app was not saved along with the portlet app.", webApp);
        assertNotNull("Portlet was not saved along with the portlet app.", app.getPortletDefinitionByName("Portlet 1"));
        assertTrue("\"user.name.family\" user attribute was not found.", app.getUserAttributes().size() == 1);

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
        persistenceStore.getTransaction().begin();
        webApp = null;
        filter = persistenceStore.newFilter();
        filter.addEqualTo("name", "App_1");
        app = (PortletApplicationDefinitionImpl) persistenceStore.getObjectByQuery(persistenceStore.newQuery(
                PortletApplicationDefinitionImpl.class, filter));
        persistenceStore.lockForWrite(app);
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        assertNotNull("Web app was not located by query.", webApp);
        webApp.addDescription(Locale.getDefault(), "Web app description");

        persistenceStore.getTransaction().commit();

        persistenceStore.getTransaction().begin();
        webApp = null;
        filter = persistenceStore.newFilter();
        filter.addEqualTo("name", "App_1");
        app = (PortletApplicationDefinitionImpl) persistenceStore.getObjectByQuery(persistenceStore.newQuery(
                PortletApplicationDefinitionImpl.class, filter));
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();

        persistenceStore.getTransaction().commit();
        assertNotNull("Web app was not located by query.", webApp);
        assertNotNull("Web app did NOT persist its description", webApp.getDescription(Locale.getDefault()));

    }

   
}