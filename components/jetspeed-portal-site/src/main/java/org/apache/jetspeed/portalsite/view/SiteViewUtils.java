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
package org.apache.jetspeed.portalsite.view;

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.page.document.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for constructing and accessing site views.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SiteViewUtils
{
    /**
     * mergeMenuDefinitionLocators - utility to merge menu definition locator lists
     *                               to be used by derived implementations to aggregate
     *                               menu definition locators
     *
     * @param definitions list of menu definitions to merge
     * @param definitionNode menu definition node
     * @param path node view path
     * @param override override menu definition
     * @param menuDefinitionLocators merged menu definition locators
     * @return merged menu definition locators
     */
    public static List<SiteViewMenuDefinitionLocator> mergeMenuDefinitionLocators(List<MenuDefinition> definitions, Node definitionNode, String path, boolean override, List<SiteViewMenuDefinitionLocator> menuDefinitionLocators)
    {
        // merge definitions into aggregated menu definition
        // locators if defined
        if (definitions != null)
        {
            for (MenuDefinition definition : definitions)
            {
                // aggregate menu definition by valid name
                String definitionName = definition.getName();
                if (definitionName != null)
                {
                    // add unique menu definition to end of
                    // ordered menu definition locators list
                    if (!menuDefinitionLocatorsContains(menuDefinitionLocators, definitionName))
                    {
                        if (menuDefinitionLocators == null)
                        {
                            menuDefinitionLocators = Collections.synchronizedList(new ArrayList<SiteViewMenuDefinitionLocator>(definitions.size() * 2));
                        }
                        menuDefinitionLocators.add(new SiteViewMenuDefinitionLocator(definition, definitionNode, path, override));
                    }
                    else if (override)
                    {
                        throw new RuntimeException("Override menu definitions must be merged/added before others!");
                    }
                }
            }
        }
        return menuDefinitionLocators;
    }
    
    /**
     * mergeMenuDefinitionLocators - utility to merge menu definition locator lists
     *                               to be used by derived implementations to aggregate
     *                               menu definition locators
     *
     * @param locators list of menu definition locators to merge
     * @param menuDefinitionLocators merged menu definition locators
     * @return merged menu definition locators
     */
    public static List<SiteViewMenuDefinitionLocator> mergeMenuDefinitionLocators(List<SiteViewMenuDefinitionLocator> locators, List<SiteViewMenuDefinitionLocator> menuDefinitionLocators)
    {
        // merge locators into aggregated menu definition
        // locators if defined
        if (locators != null)
        {
            for (SiteViewMenuDefinitionLocator locator : locators)
            {
                // aggregate menu definition by valid name
                String definitionName = locator.getName();

                // add unique menu definition to end of
                // ordered menu definition locators list
                if (!menuDefinitionLocatorsContains(menuDefinitionLocators, definitionName))
                {
                    if (menuDefinitionLocators == null)
                    {
                        menuDefinitionLocators = Collections.synchronizedList(new ArrayList<SiteViewMenuDefinitionLocator>(locators.size() * 2));
                    }
                    menuDefinitionLocators.add(locator);
                }
            }
        }
        return menuDefinitionLocators;
    }

    /**
     * menuDefinitionLocatorsContains - contains test for menu definition locators by name
     *
     * @param menuDefinitionLocators merged menu definition locators
     * @param name menu definition name
     * @return contains name result
     */
    public static boolean menuDefinitionLocatorsContains(List<SiteViewMenuDefinitionLocator> menuDefinitionLocators, String name)
    {
        // test for matching name in menu definition locators
        return (findMenuDefinitionLocator(menuDefinitionLocators, name) != null);
    }

    /**
     * findMenuDefinitionLocator - find menu definition locator by name
     *
     * @param menuDefinitionLocators merged menu definition locators
     * @param name menu definition name
     * @return menu definition locator
     */
    public static SiteViewMenuDefinitionLocator findMenuDefinitionLocator(List<SiteViewMenuDefinitionLocator> menuDefinitionLocators, String name)
    {
        // find matching menu definition locator by name
        if ((menuDefinitionLocators != null) && (name != null))
        {
            synchronized (menuDefinitionLocators)
            {
                for (SiteViewMenuDefinitionLocator locator : menuDefinitionLocators)
                {
                    if (name.equals(locator.getName()))
                    {
                        return locator;
                    }
                }
            }
        }
        return null;
    }
}
