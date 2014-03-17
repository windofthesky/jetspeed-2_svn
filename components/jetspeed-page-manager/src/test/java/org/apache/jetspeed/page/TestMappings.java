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
package org.apache.jetspeed.page;

import junit.framework.TestCase;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.preference.FragmentPreference;
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
        System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
        Unmarshaller unmar = new Unmarshaller(mapping);
        Fragment fragment = (Fragment) unmar.unmarshal(new InputSource(getClass().getClassLoader().getResourceAsStream(
                "fragment-test.xml")));
        
        assertNotNull(fragment);
        assertEquals(1, fragment.getPreferences().size());
        FragmentPreference pref = (FragmentPreference) fragment.getPreferences().get(0);
        
        assertEquals("Google", pref.getName());
        assertEquals(false, pref.isReadOnly());
        assertEquals("http://www.google.com", pref.getValueList().get(0));

    }
}
