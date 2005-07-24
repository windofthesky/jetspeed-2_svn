/*
 * Created on Oct 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.prefs;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.apache.jetspeed.prefs.impl.PersistenceBrokerPreferencesProvider;
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;
import org.apache.jetspeed.testhelpers.OJBHelper;

/**
 * <p>
 * TestPreferencesNoPropManager
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TestPreferencesNoPropManager extends TestCase
{

 

    /**
     * The property manager. 
     */
    private static PropertyManager pms;
    private PreferencesProvider provider;
    private OJBHelper ojbHelper;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        
        Map context = new HashMap();
        ojbHelper = new OJBHelper(context);
        ojbHelper.setUp();

        PersistenceBrokerPreferencesProvider targetProvider = new PersistenceBrokerPreferencesProvider("META-INF/prefs_repository.xml", true);
        targetProvider.init();
        this.provider = (PreferencesProvider) 
            ojbHelper.getTxProxiedObject(targetProvider, new String[]{PreferencesProvider.class.getName()});                
        
        PropertyManagerImpl targetPropMan = new PropertyManagerImpl(provider);
        
        pms = (PropertyManager) 
            ojbHelper.getTxProxiedObject(targetPropMan, new String[]{PropertyManager.class.getName()});                
        
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

    protected String[] getConfigurations()
    {
        return new String[]{"META-INF/prefs-noprop-dao.xml", "META-INF/transaction.xml"};
    }

    protected void clearChildren(Preferences node) throws Exception
    {
        String[] names = node.childrenNames();
        for(int i=0; i < names.length; i++)
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
            Map propertyKeys = pms.getPropertyKeys(pref);
            pms.removePropertyKeys(pref, propertyKeys.keySet());
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
}
