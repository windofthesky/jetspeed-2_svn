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
package org.apache.jetspeed.administration;

import org.apache.commons.configuration.Configuration;

import java.util.Iterator;
import java.util.List;


/**
 * Portal Configuration
 * 
 * Retrieve basic data types from the jetspeed.properties configuration
 * This is a subset of Commons Configuration functionality
 * Not the best solution wrappering commons configuration, but it does continue 
 * with the requirements of interface-driven development and zero dependencies in API
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortalConfigurationImpl implements PortalConfiguration
{
    Configuration configuration;
    
    public PortalConfigurationImpl(Configuration configuration)
    {
        this.configuration = configuration;
    }
    
    public boolean getBoolean(String key, boolean defaultValue)
    {
        return configuration.getBoolean(key, defaultValue);
    }

    public boolean getBoolean(String key)
    {
        return configuration.getBoolean(key);
    }

    public double getDouble(String key, double defaultValue)
    {
        return configuration.getDouble(key, defaultValue);
    }

    public double getDouble(String key)
    {
        return configuration.getDouble(key);
    }

    public float getFloat(String key, float defaultValue)
    {
        return configuration.getFloat(key, defaultValue);
    }

    public float getFloat(String key)
    {
        return configuration.getFloat(key);
    }

    public int getInt(String key, int defaultValue)
    {
        return configuration.getInt(key, defaultValue);        
    }

    public int getInt(String key)
    {
        return configuration.getInt(key);        
    }

    public List<Object> getList(String key)
    {
        return configuration.getList(key);
    }

    public long getLong(String key, long defaultValue)
    {
        return configuration.getLong(key, defaultValue);        
    }

    public long getLong(String key)
    {
        return configuration.getLong(key);        
    }

    public String getString(String key, String defaultValue)
    {
        return configuration.getString(key, defaultValue);        
    }

    public String getString(String key)
    {
        return configuration.getString(key);        
    }

    public String[] getStringArray(String key)
    {
        return configuration.getStringArray(key);                
    }
    
    public Iterator getKeys()
    {
        return configuration.getKeys();
    }
    
    public void setString(String key, String value)
    {
        configuration.setProperty(key, value);
    }
}
