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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.page.document.Node;

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
     * @param locators list of menu definition locators to merge
     * @param node node view
     * @param override override menu definition
     * @param menuDefinitionLocators merged menu definition locators
     * @return merged menu definition locators
     */
    public static List mergeMenuDefinitionLocators(List definitions, Node node, boolean override, List menuDefinitionLocators)
    {
        // merge definitions into aggregated menu definition
        // locators if defined
        if (definitions != null)
        {
            Iterator definitionsIter = definitions.iterator();
            while (definitionsIter.hasNext())
            {
                // aggregate menu definition by valid name
                MenuDefinition definition = (MenuDefinition)definitionsIter.next();
                String definitionName = definition.getName();
                if (definitionName != null)
                {
                    // add unique menu definition to end of
                    // ordered menu definition locators list
                    if (!menuDefinitionLocatorsContains(menuDefinitionLocators, definitionName))
                    {
                        if (menuDefinitionLocators == null)
                        {
                            menuDefinitionLocators = Collections.synchronizedList(new ArrayList(definitions.size() * 2));
                        }
                        menuDefinitionLocators.add(new SiteViewMenuDefinitionLocator(definition, node, override));
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
    public static List mergeMenuDefinitionLocators(List locators, List menuDefinitionLocators)
    {
        // merge locators into aggregated menu definition
        // locators if defined
        if (locators != null)
        {
            Iterator locatorsIter = locators.iterator();
            while (locatorsIter.hasNext())
            {
                // aggregate menu definition by valid name
                SiteViewMenuDefinitionLocator locator = (SiteViewMenuDefinitionLocator)locatorsIter.next();
                String definitionName = locator.getName();

                // add unique menu definition to end of
                // ordered menu definition locators list
                if (!menuDefinitionLocatorsContains(menuDefinitionLocators, definitionName))
                {
                    if (menuDefinitionLocators == null)
                    {
                        menuDefinitionLocators = Collections.synchronizedList(new ArrayList(locators.size() * 2));
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
    public static boolean menuDefinitionLocatorsContains(List menuDefinitionLocators, String name)
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
    public static SiteViewMenuDefinitionLocator findMenuDefinitionLocator(List menuDefinitionLocators, String name)
    {
        // find matching menu definition locator by name
        if ((menuDefinitionLocators != null) && (name != null))
        {
            synchronized (menuDefinitionLocators)
            {
                Iterator locatorsIter = menuDefinitionLocators.iterator();
                while (locatorsIter.hasNext())
                {
                    SiteViewMenuDefinitionLocator locator = (SiteViewMenuDefinitionLocator)locatorsIter.next();
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