/*
 * Copyright 2005 The Apache Software Foundation.
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
     * locator - locator string defined for menu containing
     *           menu name and concrete path of defining node
     */
    private String locator;

    /**
     * menuDefinition - menu definition
     */
    private MenuDefinition menuDefinition;

    /**
     * SiteViewMenuDefinitionLocator - custom menu definition constructor
     *
     * @param menuDefinition custom menu definition
     * @param definingNode defining page or folder
     */
    public SiteViewMenuDefinitionLocator(MenuDefinition menuDefinition, Node definingNode)
    {
        this.menuDefinition = menuDefinition;
        this.locator = definingNode.getPath() + "|" + menuDefinition.getName();
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
            return locator.equals((String)obj);
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
}
