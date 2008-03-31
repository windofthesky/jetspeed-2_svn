/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.prefs;

import java.util.prefs.Preferences;

import org.apache.jetspeed.prefs.util.test.AbstractPrefsSupportedTestCase;

/**
 * <p>
 * TestPreferencesNoPropManager
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class TestPreferencesNoPropManager extends AbstractPrefsSupportedTestCase
{

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();

        // Make sure we are starting with a clean slate
        clearChildren(Preferences.userRoot());
        clearChildren(Preferences.systemRoot());

    }

    protected void clearChildren(Preferences node) throws Exception
    {
        String[] names = node.childrenNames();
        for (int i = 0; i < names.length; i++)
        {
            node.node(names[i]).removeNode();
        }
    }

    /**
     * <p>
     * Legacy test from the times where we add a property manager. The property manager is
     * since gone, but the test still tests the prefs implementation.
     * </p>
     * 
     * @throws Exception
     */
    public void testSansPropertyManager() throws Exception
    {

        // Make sure we are starting with a clean slate
        clearChildren(Preferences.userRoot());
        clearChildren(Preferences.systemRoot());

        Preferences pref0 = Preferences.userRoot();
        // Test that the property manager is off
        Preferences pref1 = pref0.node("testOpenNode");
        pref1.put("0", "I am 0 key");

        assertNotNull(pref1.get("0", null));

    }

    /**
     * @see org.apache.jetspeed.components.test.AbstractSpringTestCase#getConfigurations()
     */
    protected String[] getConfigurations()
    {
        return new String[]
        { "prefs.xml", "transaction.xml", "cache.xml" };
    }
}
