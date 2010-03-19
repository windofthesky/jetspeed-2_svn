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

/**
 * This class represents a menu definition locator that is
 * comprised of the menu name, (the full definition is saved
 * here from convenience), and concrete path of the
 * defining folder or page.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SiteViewMenuDefinitionLocator
{
    /**
     * menuDefinition - menu definition
     */
    private MenuDefinition menuDefinition;

    /**
     * locator - locator string defined for menu containing
     *           menu name and concrete path of defining node
     */
    private String locator;
    
    /**
     * node - menu definition view path
     */
    private String path;

    /**
     * override - override menu definition flag
     */
    private boolean override;

    /**
     * SiteViewMenuDefinitionLocator - custom menu definition constructor
     *
     * @param menuDefinition custom menu definition
     * @param definitionNode defining page or folder
     * @param path menu definition path
     * @param override menu definition override flag
     */
    public SiteViewMenuDefinitionLocator(MenuDefinition menuDefinition, Node definitionNode, String path, boolean override)
    {
        this.menuDefinition = menuDefinition;
        this.locator = definitionNode.getPath() + "|" + menuDefinition.getName();
        this.path = path;
        this.override = override;
    }

    /**
     * SiteViewMenuDefinitionLocator - standard menu definition constructor
     *
     * @param menuDefinition standard menu definition
     */
    public SiteViewMenuDefinitionLocator(MenuDefinition menuDefinition)
    {
        this.menuDefinition = menuDefinition;
        this.locator = "<standard_menu_definition>|" + menuDefinition.getName();
    }

    /**
     * toString - return locator
     *
     * @return search path
     */
    public String toString()
    {
        return locator;
    }

    /**
     * equals - compare as string to locator
     *
     * @return equals result
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof String)
        {
            return locator.equals(obj);
        }
        return locator.equals(obj.toString());
    }

    /**
     * hashCode - return search path hash code
     *
     * @return hash code
     */
    public int hashCode()
    {
        return locator.hashCode();
    }

    /**
     * getMenuDefinition - return menu definition
     *
     * @return menu definition
     */
    public MenuDefinition getMenuDefinition()
    {
        return menuDefinition;
    }

    /**
     * getName - return name of menu definition
     *
     * @return menu definition name
     */
    public String getName()
    {
        return menuDefinition.getName();
    }

    /**
     * getPath - return menu definition view path
     *
     * @return menu definition view path
     */
    public String getPath()
    {
        return path;
    }
    
    /**
     * isOverride - return override menu definition flag
     *
     * @return override menu definition flag
     */
    public boolean isOverride()
    {
        return override;
    }
}
