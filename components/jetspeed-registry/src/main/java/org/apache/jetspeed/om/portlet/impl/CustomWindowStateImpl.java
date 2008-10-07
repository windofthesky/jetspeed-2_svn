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
package org.apache.jetspeed.om.portlet.impl;

import javax.portlet.WindowState;

import org.apache.jetspeed.om.common.portlet.CustomWindowState;

public class CustomWindowStateImpl implements CustomWindowState
{
    /** The application id. */
    protected long                  appId;

    protected long                  id;

    protected String                customName;

    protected String                mappedName;

    protected String                description;

    protected transient WindowState customState;

    protected transient WindowState mappedState;

    public CustomWindowStateImpl()
    {
    }

    public void setCustomName(String customName)
    {
        if (customName == null)
        {
            throw new IllegalArgumentException("CustomName is required");
        }
        else if ( this.customName != null )
        {
            throw new IllegalStateException("CustomName already set");
        }
        this.customName = customName.toLowerCase();
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setMappedName(String mappedName)
    {
        if ( this.mappedName != null || this.mappedState != null )
        {
            throw new IllegalArgumentException("MappedName already set");
        }
        else if ( mappedName != null )
        {
            this.mappedName = mappedName.toLowerCase();
        }
    }

    public WindowState getCustomState()
    {
        if (customState == null)
        {
            customState = new WindowState(customName);
        }
        return customState;
    }

    public WindowState getMappedState()
    {
        if (mappedState == null)
        {
            if (mappedName != null)
            {
                mappedState = new WindowState(mappedName);
            } else
            {
                mappedState = getCustomState();
            }
        }
        return mappedState;
    }

    public String getDescription()
    {
        return description;
    }

    public int hashCode()
    {
        return customName != null ? customName.hashCode() : super.hashCode();
    }

    public boolean equals(Object object)
    {
        if (object instanceof CustomWindowStateImpl)
            return customName.equals(((CustomWindowStateImpl) object).customName);
        else
            return false;
    }
}
