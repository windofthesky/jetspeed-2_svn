/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.DublinCore;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.JetspeedServiceReference;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.om.portlet.impl.DublinCoreImpl;
import org.apache.jetspeed.om.portlet.impl.JetspeedServiceReferenceImpl;
import org.apache.jetspeed.om.portlet.impl.SupportsImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.UserAttributeImpl;
import org.apache.jetspeed.om.portlet.impl.UserAttributeRefImpl;
import org.apache.jetspeed.util.JetspeedLocale;

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
        String lang = Locale.getDefault().toString();
        
        // Create an Application and a Web app      
        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName("App_1");
        app.setContextPath("/app1");

        UserAttributeRef uaRef = app.addUserAttributeRef("user-name-family");
        uaRef.setNameLink("user.name.family");

        UserAttribute ua = app.addUserAttribute("user.name.family");
        ua.addDescription(lang).setDescription("User Last Name");

        app.addJetspeedServiceReference("PortletEntityAccessComponent");
        app.addJetspeedServiceReference("PortletRegistryComponent");

        addDublinCore(app.getMetadata());

        PortletDefinition portlet = app.addPortlet("Portlet 1");
        portlet.setPortletClass("org.apache.Portlet");
        portlet.addDescription(lang).setDescription("Portlet description.");
        portlet.addDisplayName(lang).setDisplayName("Portlet display Name.");

        InitParam initParam = portlet.addInitParam("testparam");
        initParam.setParamValue("test value");
        initParam.addDescription(lang).setDescription("This is a test portlet parameter");

        addDublinCore(portlet.getMetadata());

        Preferences prefs = portlet.getPortletPreferences();
        Preference pref = prefs.addPreference("preference 1");
        pref.addValue("value 1");
        pref.addValue("value 2");
        
        Language language = portlet.addLanguage(Locale.getDefault());
        language.setTitle("Portlet 1");
        language.setShortTitle("Portlet 1");
        
        Supports supports = portlet.addSupports("html/text");
        supports.addPortletMode("EDIT");
        supports.addPortletMode("VIEW");
        supports.addPortletMode("HELP");
        
        supports = portlet.addSupports("wml");
        supports.addPortletMode("HELP");
        supports.addPortletMode("VIEW");
        
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
