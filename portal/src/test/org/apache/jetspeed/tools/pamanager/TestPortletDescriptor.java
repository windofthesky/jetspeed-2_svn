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
package org.apache.jetspeed.tools.pamanager;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.PortletMode;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.jetspeed.cache.PortletCache;
import org.apache.jetspeed.components.util.RegistrySupportedTestCase;
import org.apache.jetspeed.factory.JetspeedPortletFactory;
import org.apache.jetspeed.factory.JetspeedPortletFactoryProxy;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.descriptor.PortletApplicationDescriptor;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * TestPortletDescriptor - tests loading the portlet.xml deployment descriptor
 * into Java objects
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 *
 * @version $Id$
 */
public class TestPortletDescriptor extends RegistrySupportedTestCase
{
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        TestRunner.main(new String[] { TestPortletDescriptor.class.getName()});
    }

    public static Test suite()

    {

        // All methods starting with "test" will be executed in the test suite.

        return new TestSuite(TestPortletDescriptor.class);

    }
    
    /*
     * Overrides the database properties
     */
    //    public void overrideProperties(Configuration properties)
    //    {
    //        super.overrideProperties(properties);
    //    }

    public void testLoadPortletApplicationTree() throws Exception
    {
        PortletCache portletCache = new PortletCache();
        new JetspeedPortletFactoryProxy(new JetspeedPortletFactory(portletCache));
        
        System.out.println("Testing loadPortletApplicationTree");
        PortletApplicationDescriptor pad = new PortletApplicationDescriptor(new FileReader("./test/testdata/deploy/portlet.xml"), "unit-test");
        MutablePortletApplication app = pad.createPortletApplication();
        assertNotNull("App is null", app);
        assertNotNull("Version is null", app.getVersion());
        assertTrue("Version invalid: " + app.getVersion(), app.getVersion().equals("1.0"));
        assertNotNull("PA Identifier is null", app.getApplicationIdentifier());
        assertTrue(
            "PA Identifier invalid: " + app.getApplicationIdentifier(),
            app.getApplicationIdentifier().equals("TestRegistry"));

        validateUserInfo(app);

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

    private void validateUserInfo(MutablePortletApplication app)
    {
        // Portlet User Attributes
        Collection userAttributeSet = app.getUserAttributes();
        Iterator it = userAttributeSet.iterator();
        while (it.hasNext())
        {
            UserAttribute userAttribute = (UserAttribute) it.next();
            assertNotNull("User attribute name is null, ", userAttribute.getName());
            if (userAttribute.getName().equals("user.name.given"))
            {
                assertTrue(
                    "User attribute description: " + userAttribute.getDescription(),
                    userAttribute.getDescription().equals("User Given Name"));
            }
            if (userAttribute.getName().equals("user.name.family"))
            {
                assertTrue(
                    "User attribute description: " + userAttribute.getDescription(),
                    userAttribute.getDescription().equals("User Last Name"));
            }
            if (userAttribute.getName().equals("user.home-info.online.email"))
            {
                assertTrue(
                    "User attribute description: " + userAttribute.getDescription(),
                    userAttribute.getDescription().equals("User eMail"));
            }
        }
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
        
        
        MutablePortletApplication app = portletRegistry.getPortletApplication("HW_App");
        if (app != null)
        {
            portletRegistry.removeApplication(app);
          
        }

        PortletApplicationDescriptor pad = new PortletApplicationDescriptor(new FileReader("./test/testdata/deploy/portlet2.xml"), "HW_App");
        app = pad.createPortletApplication();

        app.setName("HW_App");

 
        portletRegistry.registerPortletApplication(app);
  
        // store.invalidateAll();

   
        PortletDefinition pd = portletRegistry.getPortletDefinitionByUniqueName("HW_App::PreferencePortlet");

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

    
        pd = portletRegistry.getPortletDefinitionByUniqueName("HW_App::PickANumberPortlet");
        
        assertNotNull(pd);

        
        portletRegistry.removeApplication(app);
        

    }

    public void testInfusingWebXML() throws Exception
    {
        File warFile = new File("./test/testdata/deploy/webapp");
        PortletApplicationWar paWar = new PortletApplicationWar(new DirectoryHelper(warFile), "unit-test", "/" );

        SAXBuilder builder = new SAXBuilder(false);

        // Use the local dtd instead of remote dtd. This
        // allows to deploy the application offline
        builder.setEntityResolver(new EntityResolver()
        {
            public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId)
                throws SAXException, java.io.IOException
            {

                if (systemId.equals("http://java.sun.com/dtd/web-app_2_3.dtd"))
                {
                    return new InputSource(PortletApplicationWar.class.getResourceAsStream("web-app_2_3.dtd"));
                }
                else
                    return null;
            }
        });

        FileReader srcReader = new FileReader("./test/testdata/deploy/webapp/WEB-INF/web.xml");
        FileReader targetReader = null;
        Document  doc = builder.build(srcReader);

        Element root = doc.getRootElement();

        try
        {
            Object jetspeedServlet = XPath.selectSingleNode(root, PortletApplicationWar.JETSPEED_SERVLET_XPATH);
            Object jetspeedServletMapping = XPath.selectSingleNode(root, PortletApplicationWar.JETSPEED_SERVLET_MAPPING_XPATH);

            assertNull(jetspeedServlet);
            assertNull(jetspeedServletMapping);

            PortletApplicationWar targetWar = paWar.copyWar("./target/webapp");
            targetWar.processWebXML();

            targetReader = new FileReader("./target/webapp/WEB-INF/web.xml");

            Document targetDoc = builder.build(targetReader);
            Element targetRoot = targetDoc.getRootElement();

            jetspeedServlet = XPath.selectSingleNode(targetDoc, PortletApplicationWar.JETSPEED_SERVLET_XPATH);
            jetspeedServletMapping = XPath.selectSingleNode(targetDoc, PortletApplicationWar.JETSPEED_SERVLET_MAPPING_XPATH);


            assertNotNull(jetspeedServlet);
            assertNotNull(jetspeedServletMapping);

        }
        finally
        {
            srcReader.close();
            paWar.close();
            targetReader.close();
            File warFile2 = new File("./target/webapp");
            DirectoryHelper dirHelper = new DirectoryHelper(warFile2);
            dirHelper.remove();
            dirHelper.close();
        }

    }

}
