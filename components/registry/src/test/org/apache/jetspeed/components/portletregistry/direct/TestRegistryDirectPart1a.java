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
package org.apache.jetspeed.components.portletregistry.direct;

import java.util.Arrays;
import java.util.Locale;

import javax.portlet.PortletMode;

import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.portletregistry.AbstractRegistryTest;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.DublinCoreImpl;
import org.apache.jetspeed.om.impl.JetspeedServiceReferenceImpl;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.jetspeed.om.impl.UserAttributeRefImpl;
import org.apache.jetspeed.om.portlet.impl.ContentTypeImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.om.common.PreferenceSetCtrl;

/**
 * 
 * TestRegistry runs a suite tests creating, updating, retreiving and deleting
 * portlet information from the registry.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class TestRegistryDirectPart1a extends AbstractRegistryTest
{

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        buildTestData();

    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        //  super.tearDown();
    }

 

    /**
     * <p>
     * buildTestData
     * </p>
     * 
     * @throws RegistryException
     * @throws LockFailedException
     */
    private void buildTestData() throws RegistryException, LockFailedException
    {
        // Create an Application and a Web app

        
        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName("App_1");
        app.setApplicationIdentifier("App_1");

        UserAttributeRef uaRef = new UserAttributeRefImpl("user-name-family", "user.name.family");
        app.addUserAttributeRef(uaRef);

        UserAttribute ua = new UserAttributeImpl("user.name.family", "User Last Name");
        app.addUserAttribute(ua);

        JetspeedServiceReference service1 = new JetspeedServiceReferenceImpl("PortletEntityAccessComponent");
        app.addJetspeedService(service1);
        JetspeedServiceReference service2 = new JetspeedServiceReferenceImpl("PortletRegistryComponent");
        app.addJetspeedService(service2);

        addDublinCore(app.getMetadata());

        WebApplicationDefinitionImpl webApp = new WebApplicationDefinitionImpl();
        webApp.setContextRoot("/app1");
        webApp.addDescription(Locale.FRENCH, "Description: La fromage est dans ma pantalon!");
        webApp.addDisplayName(Locale.FRENCH, "Display Name: La fromage est dans ma pantalon!");

        PortletDefinitionComposite portlet = new PortletDefinitionImpl();
        portlet.setClassName("org.apache.Portlet");
        portlet.setName("Portlet 1");
        portlet.addDescription(Locale.getDefault(), "POrtlet description.");
        portlet.addDisplayName(Locale.getDefault(), "Portlet display Name.");

        portlet.addInitParameter("testparam", "test value", "This is a test portlet parameter", Locale.getDefault());

        addDublinCore(portlet.getMetadata());

        // PreferenceComposite pc = new PrefsPreference();
        app.addPortletDefinition(portlet);
        PreferenceSetCtrl prefSetCtrl = (PreferenceSetCtrl) portlet.getPreferenceSet();
        PreferenceComposite pc = (PreferenceComposite) prefSetCtrl.add("preference 1", Arrays.asList(new String[]{
                "value 1", "value 2"}));
        pc.addDescription(JetspeedLocale.getDefaultLocale(), "Preference Description");

        assertNotNull(pc.getValueAt(0));

        portlet.addLanguage(registry.createLanguage(Locale.getDefault(), "Portlet 1", "Portlet 1", "This is Portlet 1",
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

        app.setWebApplicationDefinition(webApp);
        registry.registerPortletApplication(app);
    }

    private void addDublinCore( GenericMetadata metadata )
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

    public void testData() throws Exception
    {
        verifyData(false);
    }
}
