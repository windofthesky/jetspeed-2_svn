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
package org.apache.jetspeed.components.persistence;
import java.io.InputStreamReader;
import java.io.Reader;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentManager;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * <p>
 * TestPersistenceContainer
 * </p>@
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class TestPersistenceContainer extends AbstractComponentAwareTestCase
{

    private ComponentManager persistenceCm;
    private ComponentManager rdbmsCm;
    private MutablePicoContainer rdbmsContainer;
    private MutablePicoContainer persistenceContainer;
    private DefaultPicoContainer parent;


    /**
     * @param arg0
     */
    public TestPersistenceContainer(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPersistenceContainer.class);
    }

    public void testStartContainer()
    {
        assertNotNull(rdbmsCm);
        assertNotNull(persistenceCm);
    }



    //    private static final String TEST_APP_1 = "test adding object";
    //    private PersistenceStoreContainer pContainer;
    //    private ComponentManager cm;
    //    public static Test suite()
    //    {
    //        // All methods starting with "test" will be executed in the test suite.
    //        return new DatasourceEnabledTestSuite(TestPersistenceContainer.class);
    //    }
    //
    //    /**
    //     * @param testName
    //     */
    //    public TestPersistenceContainer(String testName)
    //    {
    //        super(testName);
    //    }
    //    public void testStartContainer() throws Exception
    //    {
    //        PersistenceStoreContainer pContainer = null;
    //        try
    //        {
    //            pContainer = getInstance();
    //            PersistenceStore store = pContainer.getStoreForThread("jetspeed");
    //            assertNotNull(store);
    //        }
    //        catch (RuntimeException e)
    //        {
    //            e.printStackTrace();
    //            throw e;
    //        }
    //        finally
    //        {
    //            cm.killContainer();
    //        }
    //    }
    //    public void testBasicPersistence() throws Exception
    //    {
    //        PersistenceStoreContainer pContainer = getInstance();
    //        PersistenceStore store = pContainer.getStoreForThread("jetspeed");
    //        assertNotNull(store);
    //        initTestObject(store);
    //        store.getTransaction().begin();
    //        assertNotNull(getTestObject1(store));
    //        store.getTransaction().commit();
    //    }
    //    protected PersistenceStoreContainer getInstance() throws Exception
    //    {
    //        if (this.pContainer == null)
    //        {
    //            pContainer = (PersistenceStoreContainer) cm.getRootContainer();
    //        }
    //        return pContainer;
    //    }
    //    protected void initTestObject(PersistenceStore store) throws Exception
    //    {
    //        try
    //        {
    //            store.getTransaction().begin();
    //            MutablePortletApplication app = new PortletApplicationDefinitionImpl();
    //            store.makePersistent(app);
    //            app.setName(TEST_APP_1);
    //            app.setVersion("1.0");
    //            app.setDescription("This is a test from persistence layer");
    //            // create a web application
    //            MutableWebApplication wad = new WebApplicationDefinitionImpl();
    //            wad.addDescription(getEnglishLanguage().getLocale(), "This is an english
    // desrcitpion");
    //            wad.addDisplayName(getEnglishLanguage().getLocale(), "This is an english
    // display name");
    //            wad.setContextRoot("/test");
    //            app.setWebApplicationDefinition(wad);
    //
    //            // Create some Portlets
    //            PortletDefinitionComposite pdc = new PortletDefinitionImpl();
    //            pdc.setClassName("com.bogus.Class1");
    //            pdc.setName("Portlet 1");
    //            PortletDefinitionComposite pdc2 = new PortletDefinitionImpl();
    //            pdc2.setClassName("com.bogus.Class2");
    //            pdc2.setName("Portlet 2");
    //            app.addPortletDefinition(pdc);
    //            app.addPortletDefinition(pdc2);
    //            store.getTransaction().commit();
    //        }
    //        catch (Exception e)
    //        {
    //            store.getTransaction().rollback();
    //            store.close();
    //            throw e;
    //        }
    //    }
    //    protected Language getEnglishLanguage()
    //    {
    //        MutableLanguage lang = new LanguageImpl();
    //        lang.setTitle("Portlet Title");
    //        lang.setShortTitle("Portlet Short Title");
    //        lang.setLocale(Locale.ENGLISH);
    //        return lang;
    //    }
    //    protected void destroyTestObject(PersistenceStore store) throws
    // Exception
    //    {
    //        try
    //        {
    //            store.getTransaction().begin();
    //            Filter c = store.newFilter();
    //            c.addEqualTo("name", TEST_APP_1);
    //            Object query = store.newQuery(PortletApplicationDefinitionImpl.class,
    // c);
    //            Collection removeUs = store.getCollectionByQuery(query);
    //            Iterator itr = removeUs.iterator();
    //            while (itr.hasNext())
    //            {
    //                store.deletePersistent(itr.next());
    //            }
    //            store.getTransaction().commit();
    //        }
    //        catch (Exception e)
    //        {
    //            store.getTransaction().rollback();
    //            throw e;
    //        }
    //    }
    //    protected PortletApplicationDefinitionImpl
    // getTestObject1(PersistenceStore store)
    //    {
    //        Filter c = store.newFilter();
    //        c.addEqualTo("name", TEST_APP_1);
    //        Object obj =
    // store.getObjectByQuery(store.newQuery(PortletApplicationDefinitionImpl.class,
    // c));
    //        return (PortletApplicationDefinitionImpl) obj;
    //    }
    //
    //    /**
    //     * @see junit.framework.TestCase#setUp()
    //     */
    //    protected void setUp() throws Exception
    //    {
    //        super.setUp();
    //        Map oms = new HashMap();
    //        oms.put("pluto.om", "../../portal/src/webapp/WEB-INF/conf/pluto.om");
    //        if (!OMHelper.isInitialized())
    //        {
    //            OMHelper helper = new OMHelper(oms);
    //            helper.start();
    //        }
    //        
    //        Reader composition = new
    // InputStreamReader(Thread.currentThread().getContextClassLoader()
    //        .getResourceAsStream(
    //        "org/apache/jetspeed/containers/persistence.container.groovy"));
    //        cm = new ComponentManager(composition, ComponentManager.GROOVY);
    //        cm.getRootContainer();
    //        destroyTestObject(getInstance().getStore("jetspeed"));
    //    }
    //
    //    /**
    //     * @see junit.framework.TestCase#tearDown()
    //     */
    //    protected void tearDown() throws Exception
    //    {
    //        destroyTestObject(getInstance().getStore("jetspeed"));
    //        super.tearDown();
    //    }

    //	public void testAdd2atATime() throws TransactionStateException
    //	{
    //		MutablePortletApplication app1 = new PortletApplicationDefinitionImpl();
    //
    //		try
    //		{
    //			plugin.beginTransaction();
    //            
    //			plugin.prepareForUpdate(app1);
    //			app1.setName("This is app 1 of 2");
    //			app1.setVersion("1.0");
    //			app1.setDescription("This is app 1 of 2");
    //			app1.setApplicationIdentifier("app1of2");
    //
    //			MutablePortletApplication app2 = new PortletApplicationDefinitionImpl();
    //			plugin.prepareForUpdate(app2);
    //			app2.setName("This is app 2 of 2");
    //			app2.setVersion("1.0");
    //			app2.setDescription("This is app 2 of 2");
    //			app1.setApplicationIdentifier("app2of2");
    //
    //			plugin.commitTransaction();
    //		}
    //		catch (TransactionStateException e)
    //		{
    //			try
    //			{
    //				plugin.rollbackTransaction();
    //			}
    //			catch (TransactionStateException e1)
    //			{
    //				// TODO Auto-generated catch block
    //				e1.printStackTrace();
    //			}
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //			throw e;
    //		}
    //
    //	}
    //
    //
    //	public void testAddingLangaugeToPortlet() throws Exception
    //	{
    //        
    //
    //		try
    //		{
    //			initTestObject();
    //			MutablePortletApplication app = getTestObject1();
    //			plugin.beginTransaction();
    //			plugin.prepareForUpdate(app);
    //
    //			Language english = getEnglishLanguage();
    //			assertNotNull(english);
    //
    //			PortletDefinitionComposite pdc = (PortletDefinitionComposite)
    // app.getPortletDefinitionByName("Portlet 1");
    //			plugin.prepareForUpdate(pdc);
    //			pdc.addLanguage(english);
    //
    //			plugin.commitTransaction();
    //		}
    //		catch (TransactionStateException e)
    //		{
    //			try
    //			{
    //				plugin.rollbackTransaction();
    //			}
    //			catch (TransactionStateException e1)
    //			{
    //				e1.printStackTrace();
    //				throw e1;
    //			}
    //			e.printStackTrace();
    //			throw e;
    //		}
    //
    //	}
    //
    //
    //	public void testAddObject()
    //	{
    //		initTestObject();
    //		MutablePortletApplication app = getTestObject1();
    //		assertNotNull(app);
    //		WebApplicationDefinition wad = app.getWebApplicationDefinition();
    //		assertNotNull(wad);
    //		assertTrue(app.getPortletDefinitions().size() == 2);
    //
    //		PortletDefinition pd = app.getPortletDefinitionByName("Portlet 1");
    //		assertNotNull(pd);
    //	}
    //
    //
    //	public void testDefaultPlugin()
    //	{
    //        
    //		assertNotNull(plugin);
    //	}
    //
    //
    //	public void testDelete() throws TransactionStateException
    //	{
    //		initTestObject();
    //
    //		PortletApplicationDefinitionImpl app = getTestObject1();
    //
    //		assertNotNull(app);
    //
    //        
    //
    //		try
    //		{
    //			plugin.beginTransaction();
    //			plugin.prepareForDelete(app);
    //			plugin.commitTransaction();
    //
    //			app = getTestObject1();
    //
    //			assertNull(app);
    //		}
    //		catch (TransactionStateException e)
    //		{
    //			try
    //			{
    //				plugin.rollbackTransaction();
    //			}
    //			catch (TransactionStateException e1)
    //			{
    //            
    //				e1.printStackTrace();
    //			}
    //            
    //			e.printStackTrace();
    //			throw e;
    //		}
    //	}
    //    
    //
    //	public void testDeleteByQuery()
    //	{
    //		initTestObject();
    //
    //		PortletApplicationDefinitionImpl app = getTestObject1();
    //
    //		assertNotNull(app);
    //        
    //        
    //		LookupCriteria c = plugin.newLookupCriteria();
    //		c.addEqualTo("name", TEST_APP_1);
    //		Object query =
    // plugin.generateQuery(PortletApplicationDefinitionImpl.class, c);
    //		plugin.deleteByQuery(query);
    //
    //		app = getTestObject1();
    //
    //		assertNull(app);
    //	}
    //
    //
    //	public void testExtents()
    //	{
    //		// PersistencePlugin plugin = getService().getPlugin("jetspeed-test");
    //		// Object query =
    //		// plugin.generateQuery(Application.class, new SimpleCriteria());
    //		// Collection extent =
    //		// plugin.getCollectionByQuery(Application.class, query);
    //		// assertNotNull(extent);
    //		// assertTrue(extent.size() > 0);
    //		// Iterator itr = extent.iterator();
    //		// while (itr.hasNext())
    //		// {
    //		// Application app = (Application) itr.next();
    //		// System.out.println("Application Name: " + app.getName());
    //		// }
    //
    //	}
    //
    //
    //	public void testGet2atATime() throws TransactionStateException
    //	{
    //       
    //		LookupCriteria lc1 = plugin.newLookupCriteria();
    //		lc1.addEqualTo("name", "This is app 1 of 2");
    //		MutablePortletApplication app1 =
    //			(MutablePortletApplication) plugin.getObjectByQuery(
    //				PortletApplicationDefinitionImpl.class,
    //				plugin.generateQuery(PortletApplicationDefinitionImpl.class, lc1));
    //
    //		assertNotNull("Could not retrieve test app 1 from the db", app1);
    //		try
    //		{
    //			plugin.beginTransaction();
    //			plugin.prepareForDelete(app1);
    //
    //			LookupCriteria lc2 = plugin.newLookupCriteria();
    //			lc2.addEqualTo("name", "This is app 2 of 2");
    //			MutablePortletApplication app2 =
    //				(MutablePortletApplication) plugin.getObjectByQuery(
    //					PortletApplicationDefinitionImpl.class,
    //					plugin.generateQuery(PortletApplicationDefinitionImpl.class, lc2));
    //			assertNotNull("Could not retrieve test app 2 from the db", app2);
    //			plugin.prepareForDelete(app2);
    //			plugin.commitTransaction();
    //		}
    //		catch (TransactionStateException e)
    //		{
    //			try
    //			{
    //				plugin.rollbackTransaction();
    //			}
    //			catch (TransactionStateException e1)
    //			{
    //				e1.printStackTrace();
    //			}
    //
    //			e.printStackTrace();
    //			throw e;
    //		}
    //	}
    //
    //
    //	/**
    //	 * @see
    // org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
    //	 */
    //// public void overrideProperties(Configuration properties)
    //// {
    //// super.overrideProperties(properties);
    //// }
    //
    //	public void testInit() throws Exception
    //	{
    //		assertNotNull(getService());
    //	}
    //
    //
    //	public void testSingleObjectByCollection()
    //	{
    //		// PersistencePlugin plugin = getService().getPlugin("jetspeed-test");
    //		// SimpleCriteria crit = new SimpleCriteria();
    //		// crit.addComparsion("name", "demo", SimpleCriteria.EQUAL);
    //		// Object query = plugin.generateQuery(Application.class, crit);
    //		// Collection extent =
    //		// plugin.getCollectionByQuery(Application.class, query);
    //		// assertNotNull(extent);
    //		// assertTrue(extent.size() == 1);
    //		// Application app =
    //		// (Application) extent.toArray()[0];
    //		// System.out.println("Only Application Name: " + app.getName());
    //	}
    //
    //
    //	public void testUpdate() throws Exception
    //	{
    //		initTestObject();
    //
    //        
    //
    //		PortletApplicationDefinitionImpl app;
    //		try
    //		{
    //			app = getTestObject1();
    //
    //			assertNotNull(app);
    //			plugin.beginTransaction();
    //			plugin.prepareForUpdate(app);
    //
    //			String version = app.getVersion();
    //
    //			app.setVersion("5.5.5");
    //
    //			MutableWebApplication wac = (MutableWebApplication)
    // app.getWebApplicationDefinition();
    //
    //			wac.setContextRoot("/root/changed");
    //
    //			plugin.commitTransaction();
    //		}
    //		catch (Exception e)
    //		{
    //			plugin.rollbackTransaction();
    //			e.printStackTrace();
    //			throw e;
    //		}
    //
    //		app = getTestObject1();
    //
    //		assertNotNull(app);
    //
    //		assertTrue(app.getVersion().equals("5.5.5"));
    //		String cRoot = app.getWebApplicationDefinition().getContextRoot();
    //
    //		assertTrue(cRoot.equals("/root/changed"));
    //	}






    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
                
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Reader rdbmsScript = new InputStreamReader(cl
                .getResourceAsStream("org/apache/jetspeed/containers/rdbms.container.groovy"));
        Reader persistenceScript = new InputStreamReader(cl
                .getResourceAsStream("org/apache/jetspeed/containers/persistence.container.groovy"));
        rdbmsCm = new ComponentManager(rdbmsScript, ComponentManager.GROOVY);
        persistenceCm = new ComponentManager(persistenceScript, ComponentManager.GROOVY);        
        ObjectReference parentRef = new SimpleReference();
        ObjectReference rdbmsRef = new SimpleReference();
        ObjectReference persistenceRef = new SimpleReference();
        parentRef.set(parent);
        rdbmsCm.getContainerBuilder().buildContainer(rdbmsRef, parentRef, "TEST_PERSISTENCE");
        persistenceCm.getContainerBuilder().buildContainer(persistenceRef, parentRef, "TEST_PERSISTENCE");
        rdbmsContainer = (MutablePicoContainer) rdbmsRef.get();
        persistenceContainer = (MutablePicoContainer) persistenceRef.get();
        
    }

    protected void tearDown() throws Exception
    {        
        // parent.stop();
        rdbmsContainer.stop();
        persistenceContainer.stop();
        super.tearDown();
    }
}
