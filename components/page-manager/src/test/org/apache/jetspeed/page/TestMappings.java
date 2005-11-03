package org.apache.jetspeed.page;

import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.pluto.om.common.Preference;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

public class TestMappings extends TestCase
{

    public void testFragmentMapping() throws Exception
    {
        Mapping mapping = new Mapping();

        // 1. Load the mapping information from the file
        mapping.loadMapping(new InputSource(getClass().getClassLoader().getResourceAsStream(
                "JETSPEED-INF/castor/page-mapping.xml")));

        // 2. Unmarshal the data
        Unmarshaller unmar = new Unmarshaller(mapping);
        Fragment fragment = (Fragment) unmar.unmarshal(new InputSource(getClass().getClassLoader().getResourceAsStream(
                "fragment-test.xml")));
        
        assertNotNull(fragment);
        assertEquals(1, fragment.getPreferences().size());
        Preference pref = (Preference) fragment.getPreferences().get(0);
        
        assertEquals("Google", pref.getName());
        assertEquals(false, pref.isReadOnly());
        Iterator itr = pref.getValues();
        String value = (String )itr.next();
        assertEquals("http://www.google.com", value );

    }
}
