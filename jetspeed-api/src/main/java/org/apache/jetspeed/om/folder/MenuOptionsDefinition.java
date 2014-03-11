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
package org.apache.jetspeed.om.folder;

/**
 * This interface describes the object used to define
 * portal site menu options.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface MenuOptionsDefinition extends MenuDefinitionElement
{
    /**
     * ANY_PROFILE_LOCATOR - wildcard value for profile locator names
     */
    String ANY_PROFILE_LOCATOR = "*";

    /**
     * getOptions - get comma separated menu options
     *
     * @return option paths specification
     */
    String getOptions();

    /**
     * setOptions - set comma separated menu options
     *
     * @param options option paths specification
     */
    void setOptions(String options);

    /**
     * getDepth - get depth of inclusion for folder options
     *
     * @return inclusion depth
     */
    int getDepth();

    /**
     * setDepth - set depth of inclusion for folder options
     *
     * @param depth inclusion depth
     */
    void setDepth(int depth);

    /**
     * isPaths - get generate ordered path options
     *
     * @return paths options flag
     */
    boolean isPaths();
    
    /**
     * setPaths - set generate ordered path options
     *
     * @param paths paths options flag
     */
    void setPaths(boolean paths);
    
    /**
     * isRegexp - get regexp flag for interpreting options
     *
     * @return regexp flag
     */
    boolean isRegexp();

    /**
     * setRegexp - set regexp flag for interpreting options
     *
     * @param regexp regexp flag
     */
    void setRegexp(boolean regexp);

    /**
     * getProfile - get profile locator used to filter options
     *
     * @return profile locator name
     */
    String getProfile();

    /**
     * setProfile - set profile locator used to filter options
     *
     * @param locatorName profile locator name
     */
    void setProfile(String locatorName);

    /**
     * getOrder - get comma separated regexp ordering patterns
     *
     * @return ordering patterns list
     */
    String getOrder();

    /**
     * setOrder - set comma separated regexp ordering patterns
     *
     * @param order ordering patterns list
     */
    void setOrder(String order);

    /**
     * getSkin - get skin name for options
     *
     * @return skin name
     */
    String getSkin();

    /**
     * setSkin - set skin name for options
     *
     * @param name skin name
     */
    void setSkin(String name);
}
