/*
 * Copyright 2008 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.portlet.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;


public class PreferencesImpl implements Preferences
{
    List<Preference> preferences = new LinkedList<Preference>();
    String preferencesValidator;
    
    public Preference addPreference(String name)
    {
        Preference preference = new PreferenceImpl(name);
        preferences.add(preference);
        return preference;
    }

    public Preference getPortletPreference(String name)
    {
        for (Preference p : preferences)
        {
            if (p.getName().equals(name))
                return p;
        }
        return null;
    }

    public List<Preference> getPortletPreferences()
    {
        return preferences;
    }

    public String getPreferencesValidator()
    {
        return this.preferencesValidator;
    }

    public void setPreferencesValidator(String validator)
    {
        this.preferencesValidator = validator;
    }

}
