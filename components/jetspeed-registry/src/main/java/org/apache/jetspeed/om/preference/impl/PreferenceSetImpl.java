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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.PreferencesValidator;

import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.common.preference.PreferencesValidatorFactory;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.Preference;

/**
 * @version $Id$
 *
 */
public class PreferenceSetImpl implements PreferenceSetComposite, DistributedCacheObject
{
    /** The serial uid. */
    private static final long serialVersionUID = -1254944161724683301L;

    public static class PreferenceImpl implements Preference,PreferenceComposite, Serializable
    {
        /** The serial uid. */
        private static final long serialVersionUID = 6942546526156122157L;
        
        private long id;
        private String name;
        private boolean readOnly;
        private List<String> values;
        
        private PreferenceImpl(long id, String name, List<String> values)
        {
            this.id = id;
            this.name = name;
            if (values == null)
            {
                this.values = new ArrayList<String>();
            }
            else
            {
                if (values.size() == Short.MAX_VALUE)
                {
                    throw new UnsupportedOperationException("Too many values");
                }
                this.values = new ArrayList<String>(values);
            }
        }
        
        public PreferenceImpl(PreferenceImpl other)
        {
            this.id = other.id;
            this.name = other.name;
            this.readOnly = other.readOnly;
            this.values = new ArrayList<String>(other.values);
        }
        
        public long getId()
        {
            return id;
        }
        
        public String getName()
        {
            return name;
        }

        public Iterator<String> getValues()
        {
            return values.iterator();
        }

        public boolean isReadOnly()
        {
            return readOnly;
        }

        public boolean isValueSet()
        {
            return !values.isEmpty();
        }

        public void addDescription(Locale locale, String Description)
        {
            // TODO: remove? (not really used/implemented in Jetspeed)
        }

        public void addValue(String value)
        {
            if (values.size() == Short.MAX_VALUE)
            {
                throw new UnsupportedOperationException("Too many values");
            }
            values.add(value);
        }

        public Description getDescription(Locale locale)
        {
            // TODO: remove? (not really used/implemented in Jetspeed)
            return null;
        }

        public Iterator getDescriptions()
        {
            // TODO: remove? (not really used/implemented in Jetspeed)
            return Collections.EMPTY_LIST.iterator();
        }

        public List<String> getValuesList()
        {
            return new ArrayList<String>(values);
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void setReadOnly(String value)
        {
            readOnly = Boolean.parseBoolean(value);
        }

        public void setValues(List values)
        {
            if (values != null && values.size() == Short.MAX_VALUE)
            {
                throw new UnsupportedOperationException("Too many values");
            }
            
            this.values.clear();
            if (values != null && !values.isEmpty())
            {
                this.values.addAll(values);
            }
        }
        
        public boolean equals(PreferenceImpl other)
        {
            if (other != null && name.equals(other.name) && readOnly == other.readOnly && values.size() == other.values.size() )
            {
                String a;
                String b;
                for (int i = 0, size = values.size(); i < size; i++)
                {
                    a = values.get(i);
                    b = other.values.get(i);
                    if ((a == null && b != null) || (a != null && !a.equals(b)))
                    {
                        return false;
                    }
                }
            }
            return false;
        }
    }
    
    private PreferencesValidatorFactory validatorFactory;
    private HashMap<String,PreferenceImpl> prefs;
    
    public PreferenceSetImpl()
    {
        prefs = new HashMap<String,PreferenceImpl>();
    }
    
    public PreferenceSetImpl(PreferencesValidatorFactory validatorFactory)
    {
        this();
        this.validatorFactory = validatorFactory;
    }
    
    public PreferenceSetImpl(PreferenceSetImpl src)
    {
        prefs = new HashMap<String,PreferenceImpl>(src.prefs.size());
        for (Map.Entry<String,PreferenceImpl> entry: src.prefs.entrySet())
        {
            prefs.put(entry.getKey(), new PreferenceImpl(entry.getValue()));
        }
    }
    
    public Set<String> getNames()
    {
        return prefs.keySet();
    }

    public int size()
    {
        return prefs.size();
    }

    public PreferenceImpl get(String name)
    {
        return prefs.get(name);
    }

    public PreferencesValidator getPreferencesValidator()
    {
        return validatorFactory != null ? validatorFactory.getPreferencesValidator() : null;
    }

    public Iterator iterator()
    {
        return prefs.values().iterator();
    }

    public PreferenceImpl add(String name, List values)
    {
        return add(-1, name, values);
    }

    public PreferenceImpl add(long id, String name, List values)
    {
        PreferenceImpl pref = new PreferenceImpl(id, name, values);
        prefs.put(name, pref);
        return pref;
    }

    public PreferenceImpl remove(String name)
    {
        return prefs.remove(name);
    }

    public void remove(Preference pref)
    {
        prefs.remove(pref.getName());
    }

    public void notifyChange(int action)
    {
    }
}
