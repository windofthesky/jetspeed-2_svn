/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * FragmentPreferenceValueList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FragmentPreferenceValueList extends AbstractList
{
    private FragmentPreferenceImpl preference;

    FragmentPreferenceValueList(FragmentPreferenceImpl preference)
    {
        super();
        this.preference = preference;
    }

    /**
     * wrapValueStringForAdd
     *
     * Wraps and validates preference value string
     * to be added to this list.
     *
     * @param value preference value string to add
     * @return list element to add
     */
    private FragmentPreferenceValue wrapValueStringForAdd(String value)
    {
        // only non-null values supported
        if (value == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        // wrap preference value string
        FragmentPreferenceValue preferenceValue = new FragmentPreferenceValue();
        preferenceValue.setValue(value);
        return preferenceValue;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > preference.accessValues().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // wrap and verify preference value string
        FragmentPreferenceValue preferenceValue = wrapValueStringForAdd((String)element);
        // add to underlying ordered list
        preference.accessValues().add(index, preferenceValue);
        // set value order in added element
        if (index > 0)
        {
            preferenceValue.setValueOrder(((FragmentPreferenceValue)preference.accessValues().get(index-1)).getValueOrder() + 1);
        }
        else
        {
            preferenceValue.setValueOrder(0);
        }
        // maintain value order in subsequent elements
        for (int i = index, limit = preference.accessValues().size() - 1; (i < limit); i++)
        {
            FragmentPreferenceValue nextPreferenceValue = (FragmentPreferenceValue)preference.accessValues().get(i + 1);
            if (nextPreferenceValue.getValueOrder() <= preferenceValue.getValueOrder())
            {
                // adjust value order for next element
                nextPreferenceValue.setValueOrder(preferenceValue.getValueOrder() + 1);
                preferenceValue = nextPreferenceValue;
            }
            else
            {
                // value order maintained for remaining list elements
                break;
            }
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public Object get(int index)
    {
        // implement for modifiable AbstractList:
        // unwrap preference value string
        return ((FragmentPreferenceValue)preference.accessValues().get(index)).getValue();
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Object remove(int index)
    {
        // implement for modifiable AbstractList
        return preference.accessValues().remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public Object set(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // wrap and verify preference value string
        FragmentPreferenceValue newPreferenceValue = wrapValueStringForAdd((String)element);
        // set in underlying ordered list
        FragmentPreferenceValue preferenceValue = (FragmentPreferenceValue)preference.accessValues().set(index, newPreferenceValue);
        // set value order in new element
        newPreferenceValue.setValueOrder(preferenceValue.getValueOrder());
        // return unwrapped preference value string
        return preferenceValue.getValue();
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return preference.accessValues().size();
    }
}
