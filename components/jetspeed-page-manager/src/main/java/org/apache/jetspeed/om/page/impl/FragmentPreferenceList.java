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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FragmentPreferenceList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FragmentPreferenceList extends AbstractList<FragmentPreference>
{
    private BaseFragmentElementImpl fragment;

    private List<FragmentPreference> removedPreferences;

    FragmentPreferenceList(BaseFragmentElementImpl fragment)
    {
        super();
        this.fragment = fragment;
    }

    /**
     * validatePreferenceForAdd
     *
     * Validates preference to be added to this list.
     *
     * @param preference preference to add
     * @return list element to add
     */
    private FragmentPreferenceImpl validatePreferenceForAdd(FragmentPreference preference)
    {
        // only non-null definitions supported
        if (preference == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        // make sure element is unique
        if (fragment.accessPreferences().contains(preference))
        {
            throw new IllegalArgumentException("Unable to add duplicate entry to list: " + preference.getName());
        }
        // retrieve from removed list to reuse
        // previously removed element copying
        // security constraint defs
        if (removedPreferences != null)
        {
            int removedIndex = removedPreferences.indexOf(preference);
            if (removedIndex >= 0)
            {
                FragmentPreference addPreference = preference;
                preference = removedPreferences.remove(removedIndex);
                // TODO: move this logic to copy methods on implementations
                preference.setReadOnly(addPreference.isReadOnly());
                preference.setValueList(addPreference.getValueList());
            }
        }
        return (FragmentPreferenceImpl)preference;
    }

    /**
     * getRemovedPreferences
     *
     * @return removed preferences tracking collection
     */
    private List<FragmentPreference> getRemovedPreferences()
    {
        if (removedPreferences == null)
        {
            removedPreferences = Collections.synchronizedList(new ArrayList<FragmentPreference>());
        }
        return removedPreferences;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, FragmentPreference element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > fragment.accessPreferences().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // verify preference
        FragmentPreferenceImpl preference = validatePreferenceForAdd(element);
        // add to underlying ordered list
        fragment.accessPreferences().add(index, preference);
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public FragmentPreference get(int index)
    {
        // implement for modifiable AbstractList
        return fragment.accessPreferences().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public FragmentPreference remove(int index)
    {
        // implement for modifiable AbstractList:
        // save removed element 
        FragmentPreference removed = fragment.accessPreferences().remove(index);
        if (removed != null)
        {
            getRemovedPreferences().add(removed);
        }
        return removed;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public FragmentPreference set(int index, FragmentPreference element)
    {
        // implement for modifiable AbstractList:
        // verify preference
        FragmentPreferenceImpl newPreference = validatePreferenceForAdd(element);
        // set in underlying ordered list
        FragmentPreferenceImpl preference = fragment.accessPreferences().set(index, newPreference);
        // save replaced element
        getRemovedPreferences().add(preference);
        // return constraints ref
        return preference;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return fragment.accessPreferences().size();
    }
}
