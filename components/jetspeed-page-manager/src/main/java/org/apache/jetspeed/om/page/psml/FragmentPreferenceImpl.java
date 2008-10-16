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
package org.apache.jetspeed.om.page.psml;

import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.pluto.om.portlet.Preference;
import org.apache.pluto.om.portlet.PreferenceCtrl;

/**
 * 
 * Preference implementation to be used with Fragment-based
 * portlet Preferences.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class FragmentPreferenceImpl implements Preference, PreferenceCtrl, FragmentPreference
{
    private String name;
    private List values;
    private boolean readOnly;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public boolean isReadOnly()
    {
        return readOnly;
    }
    
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }
    
    public void setReadOnly(String arg0)
    {
        readOnly = new Boolean(arg0).booleanValue();        
    }
    
    public Iterator getValues()
    {
        return getValueList().iterator();
    }
    
    public List getValueList()
    {
        return this.values;
    }
    
    public void setValues(List values)
    {
        this.values = values;
    }
    
    public void setValueList(List values)
    {
        setValues(values);
    }

    public boolean isValueSet()
    {
        return values != null && values.size() > 0;
    }


}
