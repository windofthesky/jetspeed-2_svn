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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.util.HashCodeBuilder;


/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class InitParamImpl implements InitParam, Serializable
{

    private String name;
    private String value;
    protected List<Description> descriptions;

    public String getParamName()
    {
        return name;
    }

    public String getParamValue()
    {
        return value;
    }

    public void setParamName(String name)
    {
        this.name = name;
    }

    public void setParamValue(String value)
    {
        this.value = value;
    }

    public boolean equals(Object obj)
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            InitParamImpl p = (InitParamImpl) obj;            
            return (name != null && p.getParamName() != null && name.equals(p.getParamName()));
        }

        return false;

    }

    public int hashCode()
    {
        HashCodeBuilder hash = new HashCodeBuilder(17, 77);
        return hash.append(name).toHashCode();
    }

    public Description getDescription(Locale locale)
    {
        for (Description d : getDescriptions())
        {
            if (d.getLocale().equals(locale))
            {
                return d;
            }
        }
        return null;
    }
    
    public List<Description> getDescriptions()
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList<Description>();
        }
        return descriptions;
    }
    
    public Description addDescription(String lang)
    {
        DescriptionImpl d = new DescriptionImpl();
        d.setLang(lang);
        if (getDescription(d.getLocale()) != null)
        {
            throw new IllegalArgumentException("Description for language: "+d.getLocale()+" already defined");
        }
        getDescriptions();
        descriptions.add(d);
        return d;
    }
}
