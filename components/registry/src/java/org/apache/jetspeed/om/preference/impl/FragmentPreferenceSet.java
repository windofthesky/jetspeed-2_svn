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
package org.apache.jetspeed.om.preference.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PreferencesValidator;

import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.PageManager;
import org.apache.pluto.om.common.Preference;

/**
 * This is a per-request wrapper for a PreferenceSet that allows 
 * editing the fragment-specified Preferences within a portlet instance
 * in a page without need for database persistent preferences.
 * 
 * @version $Id$
 */
public class FragmentPreferenceSet implements PreferenceSetComposite
{
    private final Map prefs = new HashMap();
    private final PageManager pm;
    
    public FragmentPreferenceSet(Fragment fragment, PageManager pm)
    {
        this.pm = pm;
        
        if (fragment != null && fragment.getPreferences() != null)
        {
            Iterator itr = fragment.getPreferences().iterator();        
            while(itr.hasNext())
            {
                Preference pref = (Preference) itr.next();
                prefs.put(pref.getName(), pref);
            }
        }
    }

    public Preference add(String name, List values)
    {                
        FragmentPreference preference = pm.newFragmentPreference();
        preference.setName(name);
        preference.setValueList(values);
        prefs.put(name, preference);
        return (Preference)preference;
    }

    public Preference get(String name)
    {
        return (Preference) prefs.get(name);
    }

    public Set getNames()
    {
        return prefs.keySet();
    }

    public PreferencesValidator getPreferencesValidator()
    {
        // never called by Pluto 1.0.1 container so no need to provide it here.
        // Pluto only uses PortletDefinition.getPreferencesSet().getPreferencesValidator()      
        return null;
    }

    public Iterator iterator()
    {
        return prefs.values().iterator();
    }

    public void remove(Preference pref)
    {
        prefs.remove(pref.getName());
    }

    public Preference remove(String name)
    {
        Preference pref = (Preference) prefs.remove(name);
        return pref;
    }

    public int size()
    {
        return prefs.size();
    }
}
