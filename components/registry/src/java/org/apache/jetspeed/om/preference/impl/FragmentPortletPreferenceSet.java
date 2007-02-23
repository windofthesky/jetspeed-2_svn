/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
import org.apache.pluto.om.common.Preference;

/**
 * This is a per-request wrapper for a PreferenceSet that allows 
 * the use of fragment-specified Preferences within a portlet instance
 * in a page.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class FragmentPortletPreferenceSet implements PreferenceSetComposite
{
    private final PreferenceSetComposite preferenceSet;
    private final Fragment fragment;
    private final Map prefs;
    
    public FragmentPortletPreferenceSet(PreferenceSetComposite preferenceSet, Fragment fragment)
    {
        // save mutable preference set and read only fragment
        this.preferenceSet = preferenceSet;
        this.fragment = fragment;

        // construct merged portlet definition prefs map;
        // note that user specific preferences accessed via
        // the portlet entity should override these defaults
        int prefsSize = preferenceSet.size() + 1;        
        if (fragment != null && fragment.getPreferences() != null)
        {
            prefsSize += fragment.getPreferences().size();
        }
        this.prefs = new HashMap(prefsSize);

        // add global portlet definition defaults to prefs
        Iterator iterator = preferenceSet.iterator();
        while(iterator.hasNext())
        {
            Preference pref = (Preference) iterator.next();
            prefs.put(pref.getName(), pref);
        }        

        // add/override global portlet definition defaults
        // using more specific fragment preferences
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

    public Preference add(String arg0, List arg1)
    {        
        Preference pref = preferenceSet.add(arg0, arg1);
        prefs.put(arg0, pref);
        return pref;
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
        return preferenceSet.getPreferencesValidator();
    }

    public Iterator iterator()
    {
        return prefs.values().iterator();
    }

    public void remove(Preference pref)
    {
        prefs.remove(pref.getName());
        preferenceSet.remove(pref);
    }

    public Preference remove(String name)
    {
        Preference pref = (Preference) prefs.remove(name);
        preferenceSet.remove(name);
        return pref;
    }

    public int size()
    {
        return prefs.size();
    }
}
