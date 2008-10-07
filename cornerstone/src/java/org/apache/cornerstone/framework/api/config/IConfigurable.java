/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cornerstone.framework.api.config;

import java.util.Properties;

/**
Interface for configurability which all configurable classes implement.
*/

public interface IConfigurable
{
    public static final String REVISION = "$Revision$";

    /**
     * Gets the value of property p in my configuration.
     * @param p name of configuration property.
     * @return value of configuration property.
     */
    public String getConfigProperty(String p);

    /**
     * Gets the value of property p1.p2 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2);

    /**
     * Gets the value of property p1.p2.p3 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2, String p3);

    /**
     * Gets the value of property p1.p2.p3.p4 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param p4 name of segement 4 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2, String p3, String p4);

    /**
     * Gets the value of property p in my configuration.
     * @param p name of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p, String defaultValue);

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String defaultValue);

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String defaultValue);

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param p4 name of segement 4 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String p4, String defaultValue);

    /**
     * Overwrites configuration with another.
     * @param overwrites configuration used to overwrite.
     */
    public void overwriteConfig(Properties overwrites);
}