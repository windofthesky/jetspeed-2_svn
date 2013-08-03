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
 * Retrieves configuration settings for all basic data types from the read only portal configuration. This configuration
 * is usually a set of read only property files such as jetspeed.properties and override.properties.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.1.2
 * @version $Id: $
 */
public interface PortalConfiguration
{
    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @return the boolean representation of this setting
     */
    boolean getBoolean(String key);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @param defaultValue if the property is not found, use this default value
     * @return the boolean representation of this setting
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @return the string representation of this setting
     */
    String getString(String key);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @param defaultValue if the property is not found, use this default value
     * @return the string representation of this setting
     */
    String getString(String key, String defaultValue);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @return the double representation of this setting
     */
    double getDouble(String key);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @param defaultValue if the property is not found, use this default value
     * @return the double representation of this setting
     */
    double getDouble(String key, double defaultValue);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @return the float representation of this setting
     */
    float getFloat(String key);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @param defaultValue if the property is not found, use this default value
     * @return the float representation of this setting
     */
    float getFloat(String key, float defaultValue);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @return the integer representation of this setting
     */
    int getInt(String key);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @param defaultValue if the property is not found, use this default value
     * @return the integer representation of this setting
     */
    int getInt(String key, int defaultValue);

    /**
     * Retrieve a list of multivalued portal configuration settings
     *
     * @param key the name of the portal configuration setting
     * @return the list of multivalued values for this setting
     */
    List getList(String key);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @return the long representation of this setting
     */
    long getLong(String key);

    /**
     * Retrieve a portal configuration setting
     *
     * @param key the name of the portal configuration setting
     * @param defaultValue if the property is not found, use this default value
     * @return the long representation of this setting
     */
    long getLong(String key, long defaultValue);

    /**
     * Retrieve the multivalued string array of portal configuration setting for a given key
     *
     * @param key the name of the portal configuration setting
     * @return the string array multiple values for this setting key
     */
    String[] getStringArray(String key);

    /**
     * Retrieve an iterator over the keys of all portal configuration settings
     *
     * @return an {@link java.util.Iterator} over the keys for all configuration settings
     */
    Iterator<String> getKeys();

    /**
     * Set a non-persisted (runtime only) configuration setting value for a given key
     *
     * @param key the name of the portal configuration setting
     * @param value the new value to override the existing value for this setting
     */
    void setString(String key, String value);
}
