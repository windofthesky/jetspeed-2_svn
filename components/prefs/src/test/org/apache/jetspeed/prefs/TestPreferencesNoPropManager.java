/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.prefs;

import java.util.Map;
import java.util.prefs.BackingStoreException;
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
        clean();
        // super.tearDown();
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
     * Clean properties.
     * </p>
     */
    protected void clean() throws Exception
    {
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1");
        try
        {
            Map propertyKeys = propertyManager.getPropertyKeys(pref);
            propertyManager.removePropertyKeys(pref, propertyKeys.keySet());
            Preferences.userRoot().node("/user").removeNode();
            Preferences.userRoot().node("/an1").removeNode();
            Preferences.userRoot().node("/rn1").removeNode();
            Preferences.userRoot().node("/testOpenNode").removeNode();
            Preferences.userRoot().node("/removeTest").removeNode();
        }
        catch (PropertyException pex)
        {
            System.out.println("PropertyException" + pex);
        }
        catch (BackingStoreException bse)
        {
            System.out.println("BackingStoreException" + bse);
        }
    }

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

    protected String[] getConfigurations()
    {
        return new String[]
        { "prefs.xml", "transaction.xml" };
    }
}
