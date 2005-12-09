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

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
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
        { "prefs.xml", "transaction.xml" };
    }
}
