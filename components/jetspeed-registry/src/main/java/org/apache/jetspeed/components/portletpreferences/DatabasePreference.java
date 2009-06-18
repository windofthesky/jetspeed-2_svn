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
package org.apache.jetspeed.components.portletpreferences;

import java.util.Collection;

import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * <p>
 * The database representation of a preference object
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class DatabasePreference
{
    private long id;
    private String dtype;
    private String applicationName;
    private String portletName;
    private String name;
    private String userName;
    private String entityId;
    private boolean readOnly;
    private Collection<DatabasePreferenceValue> values;
    
    public DatabasePreference()
    {}
        
    public String getDtype()
    {
        return dtype;
    }
    
    public void setDtype(String dtype)
    {
        this.dtype = dtype;
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
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public String getEntityId()
    {
        return entityId;
    }
    
    public void setEntityId(String entityId)
    {
        this.entityId = entityId;
    }
    
    public boolean isReadOnly()
    {
        return readOnly;
    }
    
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }
    
    public long getId()
    {
        return id;
    }

    @SuppressWarnings("unchecked")
    public Collection<DatabasePreferenceValue> getPreferenceValues()
    {
        if (values == null)
        {
            values = CollectionUtils.createCollection();
        }
        return values;
    }

    public String[] getValues()
    {
        // ensure initialized
        getPreferenceValues();
        if(values.size() == 0)
        {
            // Making changes for TCK compliance
			return null;
        }
        String[] result = new String[values.size()];
        int index = 0;
        for (DatabasePreferenceValue value : this.getPreferenceValues())
        {
            result[index] = value.getValue();
            index++;
        }
        return result; 
    }
    
    public int hashCode()
    {
        return applicationName.hashCode()+portletName.hashCode()+name.hashCode();
    }

    // TODO: 2.2 going to probably want to break these up into a base class and two subclasses if we need the equals
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if ((object instanceof DatabasePreference))
        {
            DatabasePreference other = (DatabasePreference)object;            
            return applicationName.equals(other.applicationName) && portletName.equals(other.applicationName) && name.equals(other.name);
        }
        return false;
    }
    
}
