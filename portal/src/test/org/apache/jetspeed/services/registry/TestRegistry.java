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
package org.apache.jetspeed.services.registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.ParameterSetCtrl;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 * 
 * TestRegistry runs a suite tests creating, updating, retreiving and deleting
 * portlet information from the registry.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TestRegistry extends JetspeedTest
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRegistry.class);
    }

    public static final String APP_1_NAME = "RegistryTestPortlet";

    /**
     * @param testName
     */
    public TestRegistry(String testName)
    {
        super(testName);
    }
    /**
     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
     */
    public void overrideProperties(Configuration properties)
    {
        super.overrideProperties(properties);
    }

    public void testBuildBaseApp()
    {
        clean();
        MutablePortletApplication pac = JetspeedPortletRegistry.newPortletApplication();
        MutableWebApplication wac = JetspeedPortletRegistry.newWebApplication();

        pac.setName(APP_1_NAME);
        pac.setDescription("This is a Registry Test Portlet.");
        pac.setVersion("1.0");

        wac.setContextRoot("/root");
        wac.addDescription(Jetspeed.getDefaultLocale(), "This is an english desrcitpion");
        wac.addDisplayName(Jetspeed.getDefaultLocale(), "This is an english display name");

        pac.setWebApplicationDefinition(wac);

        //add a portlet
        PortletDefinitionComposite portlet0 = JetspeedPortletRegistry.newPortletDefinition();

        portlet0.setClassName("com.portlet.MyClass0");
        portlet0.setPortletIdentifier("com.portlet.MyClass0.Portlet 0");
        portlet0.setName("Portlet 0");
        pac.addPortletDefinition(portlet0);

        try
        {
            JetspeedPortletRegistry.registerPortletApplication(pac);
        }
        catch (Throwable e)
        {

            e.printStackTrace();
        }
    }

    public void testAddApplication()
    {

        // test that portlet application exists
        MutablePortletApplication appExists = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(appExists);

        // test that the web app exists
        WebApplicationDefinition wad = appExists.getWebApplicationDefinition();
        assertNotNull(wad);

    }

    public void testAddingPortlet()
    {
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);

        //add a portlet
        PortletDefinitionComposite portlet1 = JetspeedPortletRegistry.newPortletDefinition();

        portlet1.setClassName("com.portlet.MyClass");
        portlet1.setName("Portlet 1");
        portlet1.setPortletIdentifier("com.portlet.MyClass.Portlet 1");
        app.addPortletDefinition(portlet1);

        portlet1.addDisplayName(Jetspeed.getDefaultLocale(), "Portlet 1 Display Name");
        portlet1.addDescription(Jetspeed.getDefaultLocale(), "Portlet 1 Description");

        try
        {
            JetspeedPortletRegistry.updatePortletApplication(app);
            System.out.print("");
        }
        catch (Throwable e)
        {

            e.printStackTrace();
            throw new AssertionFailedError();
        }

    }

    public void testPortletAdded()
    {
        // test that portlet application exists
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);
        assertTrue(((Collection) app.getPortletDefinitionList()).size() == 2);

        PortletDefinitionComposite portlet1 = (PortletDefinitionComposite) app.getPortletDefinitionByName("Portlet 1");

        assertNotNull(portlet1);

        Description desc = portlet1.getDescription(Jetspeed.getDefaultLocale());

        assertNotNull(desc);

        System.out.println("Default local description for Portlet 1 is " + desc.getDescription());

        DisplayName displayName = portlet1.getDisplayName(Jetspeed.getDefaultLocale());

        assertNotNull(displayName);

        System.out.println("Default local displayName  for Portlet 1 is " + displayName.getDisplayName());
    }

    public void testAddPortletInfo()
    {
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName("Portlet 0");
        assertNotNull(pdc);

        // add 2 parameters
         ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 1", "value 1");
        ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 2", "value 2");

        // add a language
        pdc.addLanguage(
            JetspeedPortletRegistry.createLanguage(Locale.getDefault(), "Test Portlet 0", "Portlet 0", "This is Portlet 0", null));

        //add content types
        ContentTypeComposite html = JetspeedPortletRegistry.newContentType();
        html.setContentType("html/text");
        ContentTypeComposite wml = JetspeedPortletRegistry.newContentType();
        html.addPortletMode("EDIT");
        html.addPortletMode("VIEW");
        html.addPortletMode("HELP");
        wml.setContentType("wml");
        wml.addPortletMode("HELP");
        wml.addPortletMode("VIEW");
        pdc.addContentType(html);
        pdc.addContentType(wml);

        PreferenceComposite pref =
            pdc.addPreference("preference 1", Arrays.asList(new String[] { "pref 1 values 1", "pref 1 value 2" }));

        pref.addDescription(Jetspeed.getDefaultLocale(), "Preference 1 description");

        pdc.addDisplayName(Jetspeed.getDefaultLocale(), "Portlet 0 Display Name");
        pdc.addDescription(Jetspeed.getDefaultLocale(), "Portlet 0 Description");

        JetspeedPortletRegistry.updatePortletApplication(app);
    }

    public void testContentType()
    {
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName("Portlet 0");
        assertNotNull(pdc);

        ContentType html = pdc.getContentTypeSet().get("html/text");
        if (html == null)
            return;
        System.out.println("Content Type : " + html.getContentType());
        Iterator modes = html.getPortletModes();
        while (modes.hasNext())
        {
            System.out.println("   - Available Mode: " + modes.next());
        }
    }

    public void testParameters()
    {
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName("Portlet 0");
        assertNotNull(pdc);

        Iterator itr = pdc.getInitParameterSet().iterator();
        int count = 0;
        for (; itr.hasNext(); count++)
            itr.next();

        assertTrue(count == 2);
    }

    public void testPreferences()
    {
        Collection portlets = JetspeedPortletRegistry.getAllPortletDefinitions();
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        PortletDefinitionComposite pdc =
            (PortletDefinitionComposite) JetspeedPortletRegistry.getPortletDefinitionByIndetifier("com.portlet.MyClass0.Portlet 0");
        Iterator itr = pdc.getPreferenceSet().iterator();
        int count = 0;
        while (itr.hasNext())
        {
            count++;
            PreferenceComposite pref = (PreferenceComposite) itr.next();
            System.out.println("Preference: " + pref.getName());

            assertNotNull(pref.getDescription(Jetspeed.getDefaultLocale()));

            System.out.println("Preference Description: " + pref.getDescription(Jetspeed.getDefaultLocale()));

            Iterator prefValues = pref.getValues();
            while (prefValues.hasNext())
            {
                System.out.println("   -value:" + prefValues.next().toString());
            }

        }

        PreferenceComposite pref1 = (PreferenceComposite) pdc.getPreferenceSet().get("preference 1");

        assertNotNull("could not locate \"preference 1\" ", pref1);

        Iterator valItr = pref1.getValues();
        int valueCount = 0;
        while (valItr.hasNext())
        {
            valueCount++;
			valItr.next();
        }

        assertTrue("\"preference 1\" should have 2 values not " + valueCount, valueCount == 2);
        
        assertTrue(count == 1);
    }

    public void testCascadeDelete()
    {
        clean();

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown()
    {
        super.tearDown();

    }

    protected void clean()
    {
        MutablePortletApplication pac = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        if (pac != null)
        {
            try
            {
                JetspeedPortletRegistry.removeApplication(pac);
            }
            catch (Exception e)
            {
                System.out.println("Unable to tear down test.");
                e.printStackTrace();
            }
        }
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {
        super.setUp();

    }

}
