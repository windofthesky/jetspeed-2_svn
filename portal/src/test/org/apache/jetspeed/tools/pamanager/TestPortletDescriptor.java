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
package org.apache.jetspeed.tools.pamanager;

import java.util.Iterator;
import java.util.Locale;
import javax.portlet.PortletMode;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.services.registry.JetspeedPortletRegistry;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;

/**
 * TestPortletDescriptor - tests loading the portlet.xml deployment descriptor
 * into Java objects
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * 
 * @version $Id$
 */
public class TestPortletDescriptor extends JetspeedTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestPortletDescriptor(String name)
    {
        super(name);
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
        return new TestSuite(TestPortletDescriptor.class);
    }

    /*
     * Overrides the database properties
     */
    public void overrideProperties(Configuration properties)
    {
        super.overrideProperties(properties);
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

        // portlets
        PortletDefinitionList portletsList = app.getPortletDefinitionList();
        Iterator it = portletsList.iterator();
        int count = 0;
        while (it.hasNext())
        {
            PortletDefinitionComposite portlet = (PortletDefinitionComposite) it.next();
            String identifier = portlet.getPortletIdentifier();
            assertNotNull("Portlet.Identifier is null", identifier);
            if (identifier.equals("HelloPortlet"))
            {
                validateHelloPortlet(portlet);
            }
            count++;
        }
        assertTrue("Portlet Count != 4, = " + count, count == 4);

    }

    private void validateHelloPortlet(PortletDefinitionComposite portlet)
    {
        // Portlet Name
        assertNotNull("Portlet.Name is null", portlet.getName());
        assertTrue("Portlet.Name invalid: " + portlet.getName(), portlet.getName().equals("HelloWorld Portlet"));

        // Portlet Class
        assertNotNull("Portlet.Class is null", portlet.getClassName());
        assertTrue(
            "Portlet.Class invalid: " + portlet.getClassName(),
            portlet.getClassName().equals("org.apache.jetspeed.portlet.helloworld.HelloWorld"));

        // Expiration Cache
        assertNotNull("Portlet.Expiration is null", portlet.getExpirationCache());
        assertTrue("Portlet.Expiration invalid: " + portlet.getExpirationCache(), portlet.getExpirationCache().equals("-1"));

        // Display Name                
        DisplayName displayName = portlet.getDisplayName(Locale.ENGLISH);
        assertNotNull("Display Name is null", displayName);
        assertTrue(
            "Portlet.DisplayName invalid: " + displayName.getDisplayName(),
            displayName.getDisplayName().equals("HelloWorld Portlet Wrapper"));

        // Init Parameters
        ParameterSet paramsList = portlet.getInitParameterSet();
        Iterator it = paramsList.iterator();
        int count = 0;
        while (it.hasNext())
        {
            ParameterComposite parameter = (ParameterComposite) it.next();
            assertTrue("InitParam.Name invalid: " + parameter.getName(), parameter.getName().equals("hello"));
            assertTrue("InitParam.Value invalid: " + parameter.getValue(), parameter.getValue().equals("Hello Portlet"));
            assertTrue(
                "InitParam.Description invalid: " + parameter.getDescription(Locale.ENGLISH),
                parameter.getDescription(Locale.ENGLISH).getDescription().equals("test init param"));
            count++;
        }
        assertTrue("InitParam Count != 1, count = " + count, count == 1);

        // Supports Content Type
        ContentTypeSet supports = portlet.getContentTypeSet();
        it = supports.iterator();
        count = 0;
        while (it.hasNext())
        {
            ContentTypeComposite contentType = (ContentTypeComposite) it.next();
            assertTrue("MimeType invalid: " + contentType.getContentType(), contentType.getContentType().equals("text/html"));

            // Portlet Modes
            Iterator modesIterator = contentType.getPortletModes();
            int modesCount = 0;
            while (modesIterator.hasNext())
            {
                PortletMode mode = (PortletMode) modesIterator.next();
                // System.out.println("mode = " + mode);
                modesCount++;
            }
            assertTrue("Portlets Modes Count != 3, count = " + count, modesCount == 3);

            count++;
        }
        assertTrue("ContentType Count != 1, count = " + count, count == 1);

        // Portlet Info
        LanguageSet infos = portlet.getLanguageSet();
        it = infos.iterator();
        count = 0;
        while (it.hasNext())
        {
            MutableLanguage info = (MutableLanguage) it.next();
            assertTrue("PortletInfo.Title invalid: " + info.getTitle(), info.getTitle().equals("HelloWorldTitle"));
            assertTrue(
                "PortletInfo.ShortTitle invalid: " + info.getShortTitle(),
                info.getShortTitle().equals("This is the short title"));
            Iterator keywords = info.getKeywords();
            assertNotNull("Keywords cannot be null", keywords);
            int keywordCount = 0;
            while (keywords.hasNext())
            {
                String keyword = (String) keywords.next();
                if (keywordCount == 0)
                {
                    assertTrue("PortletInfo.Keywords invalid: + " + keyword, keyword.equals("Test"));
                }
                else
                {
                    assertTrue("PortletInfo.Keywords invalid: + " + keyword, keyword.equals("David"));
                }
                keywordCount++;
            }
            assertTrue("Keywords Count != 2, count = " + count, keywordCount == 2);

            count++;
        }
        assertTrue("PortletInfo Count != 1, count = " + count, count == 1);

        // Portlet Preferences
        PreferenceSet prefs = portlet.getPreferenceSet();
        it = prefs.iterator();
        count = 0;
        while (it.hasNext())
        {
            PreferenceComposite pref = (PreferenceComposite) it.next();
            assertNotNull("Preference.Name is null", pref.getName());
            if (pref.getName().equals("time-server"))
            {
                assertTrue("Preference.Name invalid: " + pref.getName(), pref.getName().equals("time-server"));
                assertTrue("Preference.Modifiable invalid: ", pref.isReadOnly() == false);
                validatePreferences(pref, new String[] { "http://timeserver.myco.com", "http://timeserver.foo.com" });
            }
            else
            {
                assertTrue("Preference.Name invalid: " + pref.getName(), pref.getName().equals("port"));
                assertTrue("Preference.Modifiable invalid: ", pref.isReadOnly() == true);
                validatePreferences(pref, new String[] { "404" });
            }
            count++;
        }
        assertTrue("PortletPreference Count != 2, count = " + count, count == 2);
    }

    private void validatePreferences(PreferenceComposite pref, String[] expectedValues)
    {
        Iterator values = pref.getValues();

        int count = 0;
        while (values.hasNext())
        {
            String value = (String) values.next();
            assertTrue("Preference.Value invalid: + " + value + "[" + count + "]", value.equals(expectedValues[count]));
            count++;
            // System.out.println("value = " + value);
        }
        assertTrue("Value Count != expectedCount, count = " + expectedValues.length, count == (expectedValues.length));

    }

    public void testWritingToDB() throws Exception
    {
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication("HW_App");
        if (app != null)
        {
            JetspeedPortletRegistry.removeApplication(app);
        }
        app = PortletDescriptorUtilities.loadPortletDescriptor("./test/testdata/deploy/portlet2.xml", "HW_App");

        app.setName("HW_App");
        JetspeedPortletRegistry.registerPortletApplication(app);
        PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
        PersistencePlugin plugin = ps.getDefaultPersistencePlugin();
        plugin.clearCache();
        

        PortletDefinition pd = JetspeedPortletRegistry.getPortletDefinitionByUniqueName("PreferencePortlet");
        assertNotNull(pd);

        assertNotNull(pd.getPreferenceSet());

        Preference pref1 = pd.getPreferenceSet().get("pref1");

        assertNotNull(pref1);

        Iterator itr = pref1.getValues();
        int count = 0;
        while (itr.hasNext())
        {
            count++;
            System.out.println("Value " + count + "=" + itr.next());
        }

        assertTrue(count > 0);

        JetspeedPortletRegistry.removeApplication(app);

    }
}
