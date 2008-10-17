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

import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.portlet.Supports;

/**
 * @version $Id$
 */
public class SupportsImpl implements Supports, Serializable
{
    private static final long serialVersionUID = 3053050293118297163L;
    
    private String mimeType;
    protected List<String> portletModes = new ArrayList<String>();
    protected List<String> windowStates = new ArrayList<String>();
    
    public SupportsImpl()
    {
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Supports)
        {
            return mimeType.equals(((Supports)obj).getMimeType());
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(27, 87);
        hasher.append(mimeType);
        return hasher.toHashCode();
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public List<String> getPortletModes()
    {
        return portletModes;
    }

    public List<String> getWindowStates()
    {
        return windowStates;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }
}
