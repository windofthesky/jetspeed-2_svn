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
package org.apache.jetspeed.components.portletpreferences;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.engine.MockJetspeedEngine;

public class TestPortletPreferencesProvider extends
		DatasourceEnabledSpringTestCase 
{

    private static MockJetspeedEngine mockEngine = new MockJetspeedEngine();
	private PortletPreferencesProvider prefsProvider;
	private PortletRegistry registry;
	
	@Override
	protected String[] getConfigurations() {
	    return new String[]
           { "transaction.xml", "registry-test.xml", "cache-test.xml" };
	}

    protected void setUp() throws Exception
    {
        super.setUp();
        mockEngine.setComponentManager(scm);
        Jetspeed.setEngine(mockEngine);
        this.prefsProvider = scm.lookupComponent("org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider");
        PortletPreferencesProvider temp = scm.lookupComponent("portletPreferencesProvider");
        System.out.println("temp = " + temp);
        System.out.println("prefs = " + prefsProvider);
        this.registry = scm.lookupComponent("portletRegistry");

//        teardownTestData();
//        setupTestData();
    }

    protected void tearDown() throws Exception
    {
//        teardownTestData();
        Jetspeed.setEngine(null);
        super.tearDown();
    }

    public void testEntities() throws Exception
    {}
    
//    public void testPrefs() throws Exception
//    {
//        System.out.println("Testing prefs");
//        PortletDefinition pd = registry.getPortletDefinitionByUniqueName("j2-admin::CategoryPortletSelector");
//        PortletDefinitionImpl.setPortletPreferencesProvider(prefsProvider);
//        assertNotNull(pd);
//        Preferences prefs = pd.getPortletPreferences();
//        assertNotNull(prefs);
//        Preference pref = prefs.getPortletPreference("Keywords:Fun");
//        assertNotNull(pref);
//        List<String> values = pref.getValues();
//        assert(values.size() > 0);
//        String oldValue = values.get(0); 
//        oldValue += ",UPDATED";
//        values.set(0, oldValue);
//        prefsProvider.storeDefaults(pd, prefs);
//    }
    
}
