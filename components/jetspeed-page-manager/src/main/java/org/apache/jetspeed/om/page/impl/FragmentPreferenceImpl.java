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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.List;

/**
 * FragmentPreferenceImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FragmentPreferenceImpl implements FragmentPreference
{
    private int id;
    private String name;
    private boolean readOnly;
    private List<FragmentPreferenceValue> values;

    private FragmentPreferenceValueList preferenceValues;

    /**
     * accessValues
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List<FragmentPreferenceValue> accessValues()
    {
        // create initial collection if necessary
        if (values == null)
        {
            values = DatabasePageManagerUtils.createList();
        }
        return values;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#getName()
     * @see org.apache.pluto.container.om.common.Preference#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#setName(java.lang.String)
     * @see org.apache.pluto.container.om.common.PreferenceCtrl#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#isReadOnly()
     * @see org.apache.pluto.container.om.common.Preference#isReadOnly()
     */
    public boolean isReadOnly()
    {
        return readOnly;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#getValueList()
     */
    public List<String> getValueList()
    {
        // return mutable preference value list
        // using list wrapper to manage value order
        if (preferenceValues == null)
        {
            preferenceValues = new FragmentPreferenceValueList(this);
        }
        return preferenceValues;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#setValueList(java.util.List)
     */
    public void setValueList(List<String> values)
    {
        // set preference values by replacing existing
        // entries with new elements if new collection
        // is specified
        List<String> preferenceValues = getValueList();
        if (values != preferenceValues)
        {
            // replace all values
            preferenceValues.clear();
            if (values != null)
            {
                preferenceValues.addAll(values);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof FragmentPreferenceImpl)
        {
            if (name != null)
            {
                return name.equals(((FragmentPreferenceImpl)o).getName());
            }
            return (((FragmentPreferenceImpl)o).getName() == null);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (name != null)
        {
            return name.hashCode();
        }
        return 0;
    }
}
