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
package org.apache.jetspeed.audit.impl;

import java.sql.Timestamp;

public class ActivityBean
{
    private String activity;
    private String category;
    private String admin;
    private String userName;
    private Timestamp timestamp;
    private String ipAddress;
    private String name;
    private String beforeValue;
    private String afterValue;
    private String description;
    
    public String getActivity()
    {
        return activity;
    }
    
    public void setActivity(String activity)
    {
        this.activity = activity;
    }
    
    public String getAdmin()
    {
        return admin;
    }
    
    public void setAdmin(String admin)
    {
        this.admin = admin;
    }
    
    public String getCategory()
    {
        return category;
    }
    
    public void setCategory(String category)
    {
        this.category = category;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getIpAddress()
    {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    
    public String getAfterValue()
    {
        return afterValue;
    }

    
    public void setAfterValue(String afterValue)
    {
        this.afterValue = afterValue;
    }

    
    public String getBeforeValue()
    {
        return beforeValue;
    }

    
    public void setBeforeValue(String beforeValue)
    {
        this.beforeValue = beforeValue;
    }
    
}