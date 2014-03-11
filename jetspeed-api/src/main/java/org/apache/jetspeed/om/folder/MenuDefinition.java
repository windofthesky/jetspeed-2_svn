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

import org.apache.jetspeed.om.portlet.GenericMetadata;

import java.util.List;
import java.util.Locale;

/**
 * This interface describes the object used to define
 * portal site menus comprised of nested menus, options,
 * and separators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public interface MenuDefinition extends MenuDefinitionElement
{
    /**
     * ANY_PROFILE_LOCATOR - wildcard value for profile locator names
     */
    String ANY_PROFILE_LOCATOR = MenuOptionsDefinition.ANY_PROFILE_LOCATOR;

    /**
     * getName - get menu name
     *
     * @return menu name
     */
    String getName();

    /**
     * setName - set menu name
     *
     * @param name menu name
     */
    void setName(String name);

    /**
     * getOptions - get comma separated menu options if not specified as elements
     *
     * @return option paths specification
     */
    String getOptions();

    /**
     * setOptions - set comma separated menu options if not specified as elements
     *
     * @param options paths specification
     */
    void setOptions(String options);

    /**
     * getDepth - get depth of inclusion for folder menu options
     *
     * @return inclusion depth
     */
    int getDepth();

    /**
     * setDepth - set depth of inclusion for folder menu options
     *
     * @param depth inclusion depth
     */
    void setDepth(int depth);

    /**
     * isPaths - get generate ordered path options for specified options
     *
     * @return paths options flag
     */
    boolean isPaths();
    
    /**
     * setPaths - set generate ordered path options for specified options
     *
     * @param paths paths options flag
     */
    void setPaths(boolean paths);
    
    /**
     * isRegexp - get regexp flag for interpreting specified options
     *
     * @return regexp flag
     */
    boolean isRegexp();

    /**
     * setRegexp - set regexp flag for interpreting specified options
     *
     * @param regexp regexp flag
     */
    void setRegexp(boolean regexp);

    /**
     * getProfile - get profile locator used to filter specified options
     *
     * @return profile locator name
     */
    String getProfile();

    /**
     * setProfile - set profile locator used to filter specified options
     *
     * @param locatorName profile locator name
     */
    void setProfile(String locatorName);

    /**
     * getOrder - get comma separated regexp ordering patterns for options
     *
     * @return ordering patterns list
     */
    String getOrder();

    /**
     * setOrder - set comma separated regexp ordering patterns for options
     *
     * @param order ordering patterns list
     */
    void setOrder(String order);

    /**
     * getSkin - get skin name for menu
     *
     * @return skin name
     */
    String getSkin();

    /**
     * setSkin - set skin name for menu
     *
     * @param name skin name
     */
    void setSkin(String name);

    /**
     * getTitle - get default title for menu
     *
     * @return title text
     */
    String getTitle();

    /**
     * setTitle - set default title for menu
     *
     * @param title title text
     */
    void setTitle(String title);

    /**
     * getShortTitle - get default short title for menu
     *
     * @return short title text
     */
    String getShortTitle();

    /**
     * setShortTitle - set default short title for menu
     *
     * @param title short title text
     */
    void setShortTitle(String title);

    /**
     * getTitle - get locale specific title for menu from metadata
     *
     * @param locale preferred locale
     * @return title text
     */
    String getTitle(Locale locale);

    /**
     * getShortTitle - get locale specific short title for menu from metadata
     *
     * @param locale preferred locale
     * @return short title text
     */
    String getShortTitle(Locale locale);

    /**
     * getMetadata - get generic metadata instance for menu
     *
     * @return metadata instance
     */
    GenericMetadata getMetadata();

    /**
     * getMenuElements - get ordered list of menu options,
     *                   nested menus, separators, included
     *                   menu, and excluded menu elements
     *
     * @return element list
     */
    List<MenuDefinitionElement> getMenuElements();

    /**
     * setMenuElements - set ordered list of menu options
     *
     * @param elements element list
     */
    void setMenuElements(List<MenuDefinitionElement> elements);
}
