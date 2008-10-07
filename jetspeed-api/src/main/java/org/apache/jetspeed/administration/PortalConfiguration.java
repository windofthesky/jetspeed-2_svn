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
 * @since 2.1.2
 * @version $Id: $
 */
public interface PortalConfiguration
{
    boolean getBoolean(String key);
    boolean getBoolean(String key, boolean defaultValue);    
    String getString(String key);
    String getString(String key, String defaultValue);    
    double getDouble(String key);
    double getDouble(String key, double defaultValue);        
    float getFloat(String key);
    float getFloat(String key, float defaultValue);        
    int getInt(String key);
    int getInt(String key, int defaultValue);        
    List getList(String key);    
    long getLong(String key);
    long getLong(String key, long defaultValue);        
    String[] getStringArray(String key);
    Iterator getKeys();
    void setString(String key, String value);
}
