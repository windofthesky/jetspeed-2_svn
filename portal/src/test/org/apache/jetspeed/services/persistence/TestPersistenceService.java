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
package org.apache.jetspeed.services.persistence;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.om.common.LanguageImpl;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.portlet.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionImpl;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.servlet.WebApplicationDefinitionImpl;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * Unit tests for the persistence framework.
 * 
 * @version $Id$
 */
public class TestPersistenceService extends JetspeedTest
{

    private PersistenceService service;

    /**
     * @param testName
     */
    public TestPersistenceService(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPersistenceService.class);
    }

    /**
     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
     */
    public void overrideProperties(Configuration properties)
    {
        super.overrideProperties(properties);
    }

    public void testInit() throws Exception
    {
        assertNotNull(getService());
    }

    public void testDefaultPlugin()
    {
        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");
        assertNotNull(plugin);
    }

    public void testExtents()
    {
        //        PersistencePlugin plugin = getService().getPlugin("jetspeed-test");
        //        Object query =
        //            plugin.generateQuery(Application.class, new SimpleCriteria());
        //        Collection extent =
        //            plugin.getCollectionByQuery(Application.class, query);
        //        assertNotNull(extent);
        //        assertTrue(extent.size() > 0);
        //        Iterator itr = extent.iterator();
        //        while (itr.hasNext())
        //        {
        //            Application app = (Application) itr.next();
        //            System.out.println("Application Name: " + app.getName());
        //        }

    }

    public void testSingleObjectByCollection()
    {
        //        PersistencePlugin plugin = getService().getPlugin("jetspeed-test");
        //        SimpleCriteria crit = new SimpleCriteria();
        //        crit.addComparsion("name", "demo", SimpleCriteria.EQUAL);
        //        Object query = plugin.generateQuery(Application.class, crit);
        //        Collection extent =
        //            plugin.getCollectionByQuery(Application.class, query);
        //        assertNotNull(extent);
        //        assertTrue(extent.size() == 1);
        //        Application app =
        //            (Application) extent.toArray()[0];
        //        System.out.println("Only Application Name: " + app.getName());
    }

    public void testAddObject()
    {
        initTestObject();
        MutablePortletApplication app = getTestObject1();
        assertNotNull(app);
        WebApplicationDefinition wad = app.getWebApplicationDefinition();
        assertNotNull(wad);
        assertTrue(app.getPortletDefinitions().size() == 2);

        PortletDefinition pd = app.getPortletDefinitionByName("Portlet 1");
        assertNotNull(pd);
    }

    public void testAddingLangaugeToPortlet()
    {
        PersistencePlugin plugin = service.getPersistencePlugin("jetspeed-test");
        initTestObject();
        MutablePortletApplication app = getTestObject1();

        Language english = getEnglishLanguage();
        assertNotNull(english);

        PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName("Portlet 1");

        pdc.addLanguage(english);

        plugin.update(pdc);

    }

    public void testUpdate()
    {
        initTestObject();

        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");

        PortletApplicationDefinitionImpl app = getTestObject1();

        assertNotNull(app);

        String version = app.getVersion();

        app.setVersion("5.5.5");

        MutableWebApplication wac = (MutableWebApplication) app.getWebApplicationDefinition();

        wac.setContextRoot("/root/changed");

        plugin.update(app);

        app = getTestObject1();

        assertNotNull(app);

        assertTrue(app.getVersion().equals("5.5.5"));
        String cRoot = app.getWebApplicationDefinition().getContextRoot();

        assertTrue(cRoot.equals("/root/changed"));
    }

    public void testAdd2atATime()
    {
        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");
        MutablePortletApplication app1 = new PortletApplicationDefinitionImpl();
        app1.setName("This is app 1 of 2");
        app1.setVersion("1.0");
        app1.setDescription("This is app 1 of 2");
        app1.setApplicationIdentifier("app1of2");

        plugin.add(app1);

        MutablePortletApplication app2 = new PortletApplicationDefinitionImpl();
        app2.setName("This is app 2 of 2");
        app2.setVersion("1.0");
        app2.setDescription("This is app 2 of 2");
		app1.setApplicationIdentifier("app2of2");

        plugin.add(app2);

    }

    public void testGet2atATime()
    {
        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");
        LookupCriteria lc1 = plugin.newLookupCriteria();
        lc1.addEqualTo("name", "This is app 1 of 2");
        MutablePortletApplication app1 =
            (MutablePortletApplication) plugin.getObjectByQuery(
                PortletApplicationDefinitionImpl.class,
                plugin.generateQuery(PortletApplicationDefinitionImpl.class, lc1));

        assertNotNull("Could not retrieve test app 1 from the db", app1);
        plugin.delete(app1);
        

        LookupCriteria lc2 = plugin.newLookupCriteria();
        lc2.addEqualTo("name", "This is app 2 of 2");
        MutablePortletApplication app2 =
            (MutablePortletApplication) plugin.getObjectByQuery(
                PortletApplicationDefinitionImpl.class,
                plugin.generateQuery(PortletApplicationDefinitionImpl.class, lc2));
        assertNotNull("Could not retrieve test app 2 from the db", app2);
        plugin.delete(app2);
    }

    public void testDelete()
    {
        initTestObject();

        PortletApplicationDefinitionImpl app = getTestObject1();

        assertNotNull(app);

        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");

        plugin.delete(app);

        app = getTestObject1();

        assertNull(app);
    }

    protected PersistenceService getService()
    {
        if (service == null)
        {
            service = (PersistenceService) ServiceUtil.getServiceByName(PersistenceService.SERVICE_NAME);
        }
        return service;
    }

    protected void initTestObject()
    {

        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");
        MutablePortletApplication app = new PortletApplicationDefinitionImpl();
        app.setName("test adding object");
        app.setVersion("1.0");
        app.setDescription("This is a test from persistence layer");
        // create a web application
        MutableWebApplication wad = new WebApplicationDefinitionImpl();
        wad.addDescription(getEnglishLanguage().getLocale(), "This is an english desrcitpion");
        wad.addDisplayName(getEnglishLanguage().getLocale(), "This is an english display name");
        wad.setContextRoot("/test");
        app.setWebApplicationDefinition(wad);

        // Create some Portlets 
        PortletDefinitionComposite pdc = new PortletDefinitionImpl();
        pdc.setClassName("com.bogus.Class1");
        pdc.setName("Portlet 1");

        PortletDefinitionComposite pdc2 = new PortletDefinitionImpl();
        pdc2.setClassName("com.bogus.Class2");
        pdc2.setName("Portlet 2");

        app.addPortletDefinition(pdc);
        app.addPortletDefinition(pdc2);

        plugin.add(app);
    }

    protected Language getEnglishLanguage()
    {
        PersistencePlugin plugin = service.getPersistencePlugin("jetspeed-test");
        MutableLanguage lang = new LanguageImpl();

        lang.setTitle("Portlet Title");
        lang.setShortTitle("Portlet Short Title");
        lang.setLocale(Locale.ENGLISH);

        return lang;

    }

    protected PortletApplicationDefinitionImpl getTestObject1()
    {
        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("name", "test adding object");

        Object obj =
            plugin.getObjectByQuery(
                PortletApplicationDefinitionImpl.class,
                plugin.generateQuery(PortletApplicationDefinitionImpl.class, c));

        return (PortletApplicationDefinitionImpl) obj;
    }

    protected void destroyTestObject()
    {
        PersistencePlugin plugin = getService().getPersistencePlugin("jetspeed-test");
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("name", "test adding object");
        Collection removeUs =
            plugin.getCollectionByQuery(
                PortletApplicationDefinitionImpl.class,
                plugin.generateQuery(PortletApplicationDefinitionImpl.class, c));

        Iterator itr = removeUs.iterator();
        while (itr.hasNext())
        {
            plugin.delete(itr.next());
        }

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown()
    {
        super.tearDown();
        destroyTestObject();
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {
        super.setUp();
        destroyTestObject();
    }

}
