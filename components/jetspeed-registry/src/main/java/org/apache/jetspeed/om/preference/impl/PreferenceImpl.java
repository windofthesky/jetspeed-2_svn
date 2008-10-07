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

/**
 * @version $Id$
 *
 */
public class PreferenceImpl implements Serializable
{
    private long id;
    private String applicationName;
    private String portletName;
    private String name;
    
    public long getId()
    {
        return id;
    }
    public void setId(long id)
    {
        this.id = id;
    }
    public String getApplicationName()
    {
        return applicationName;
    }
    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }
    public String getPortletName()
    {
        return portletName;
    }
    public void setPortletName(String portletName)
    {
        this.portletName = portletName;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    
    public int hashCode()
    {
        return applicationName.hashCode()+portletName.hashCode()+name.hashCode();
    }

    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if ((object instanceof PreferenceImpl))
        {
            PreferenceImpl other = (PreferenceImpl)object;            
            return applicationName.equals(other.applicationName) && portletName.equals(other.applicationName) && name.equals(other.name);
        }
        return false;
    }
}
