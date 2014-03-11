/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.preference.FragmentPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable content fragment preferences element implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class ContentFragmentPreferenceImpl implements FragmentPreference
{
    private String name;
    private boolean readOnly;
    private List valueList;
    
    /**
     * Construct content fragment preferences element.
     * 
     * @param name preference name
     * @param readOnly preference read only flag
     * @param valueList preference values list
     */
    public ContentFragmentPreferenceImpl(String name, boolean readOnly, List valueList)
    {
        this.name = name;
        this.readOnly = readOnly;
        this.valueList = valueList;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#getValueList()
     */
    public List<String> getValueList()
    {
        if (this.valueList == null)
        {
            this.valueList = new ArrayList();
        }
        return this.valueList;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#isReadOnly()
     */
    public boolean isReadOnly()
    {
        return readOnly;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#setName(java.lang.String)
     */
    public void setName(String name)
    {
        throw new UnsupportedOperationException("FragmentPreference.setName()");
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly)
    {
        throw new UnsupportedOperationException("FragmentPreference.setReadOnly()");
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.preference.FragmentPreference#setValueList(java.util.List)
     */
    public void setValueList(List<String> values)
    {
        throw new UnsupportedOperationException("FragmentPreference.setValueList()");
    }
}
