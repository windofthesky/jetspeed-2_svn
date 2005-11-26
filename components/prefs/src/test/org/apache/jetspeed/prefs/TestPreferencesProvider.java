/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs;

import java.util.Iterator;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;

/**
 * <p>
 * Unit testing for {@link Preferences}.
 * </p>
 * 
 * @author <a href="dlestrat@yahoo.com">David Le Strat </a>
 */
public class TestPreferencesProvider extends DatasourceEnabledSpringTestCase
{
    private PreferencesProvider provider;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        provider = (PreferencesProvider) ctx.getBean("prefsProvider");

        // Make sure we are starting with a clean slate
        clearChildren(Preferences.userRoot());
        clearChildren(Preferences.systemRoot());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
    }

    /**
     * @return The test suite.
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPreferencesProvider.class);
    }

    public void testLookupProperty() throws Exception
    {
        Preferences info = Preferences.userRoot().node("/user/dynamite/userinfo");
        info.put("user.name.family", "Dynamite");
        info.put("user.name.given", "Napolean");
        info.put("user.email", "napolean@dynamite.xxx");
        info.flush();

        Iterator result = provider.lookupPreference("userinfo", "user.email", "napolean@dynamite.xxx").iterator();
        int count = 0;
        while (result.hasNext())
        {
            Node node = (Node) result.next();
            System.out.println("node = " + node.getFullPath());
            Iterator props = node.getNodeProperties().iterator();
            while (props.hasNext())
            {
                Property prop = (Property) props.next();
                String name = prop.getPropertyName();
                String value = prop.getPropertyValue();
                if ("user.name.family".equals(name))
                {
                    assertTrue("family name wrong " + value, "Dynamite".equals(value));
                }
                else if ("user.name.given".equals(name))
                {
                    assertTrue("given name wrong " + value, "Napolean".equals(value));
                }
                else if ("user.email".equals(name))
                {
                    assertTrue("email is wrong " + value, "napolean@dynamite.xxx".equals(value));
                }       
                else
                {
                    assertTrue("bad property name " + name, false);
                }
            }
            count++;
        }
        assertTrue("test-1: count is one " + count, count == 1);
    }

    /**
     * <p>
     * Clears all test data.
     * </p>
     * 
     * @param node
     * @throws Exception
     */
    protected void clearChildren(Preferences node) throws Exception
    {
        String[] names = node.childrenNames();
        for (int i = 0; i < names.length; i++)
        {
            node.node(names[i]).removeNode();
        }
    }

    /**
     * @see org.apache.jetspeed.components.test.AbstractSpringTestCase#getConfigurations()
     */
    protected String[] getConfigurations()
    {
        return new String[] { "prefs.xml", "transaction.xml" };
    }
}
