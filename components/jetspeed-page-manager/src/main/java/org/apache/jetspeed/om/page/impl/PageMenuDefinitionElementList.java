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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.folder.MenuDefinitionElement;

import java.util.AbstractList;

/**
 * PageMenuDefinitionElementList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class PageMenuDefinitionElementList extends AbstractList<MenuDefinitionElement>
{
    private PageMenuDefinitionImpl menuDefinition;

    PageMenuDefinitionElementList(PageMenuDefinitionImpl menuDefinition)
    {
        super();
        this.menuDefinition = menuDefinition;
    }

    /**
     * validateMenuElementForAdd
     *
     * Validates element to be added to this list.
     *
     * @param menuElement element to add
     * @return list element to add
     */
    private PageMenuDefinitionElement validateMenuElementForAdd(MenuDefinitionElement menuElement)
    {
        // validate element instance
        if (menuElement == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        return (PageMenuDefinitionElement)menuElement;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, MenuDefinitionElement element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > menuDefinition.accessElements().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // verify element
        PageMenuDefinitionElement menuElement = validateMenuElementForAdd(element);
        // add to underlying ordered list
        menuDefinition.accessElements().add(index, menuElement);
        // set element order in added element
        if (index > 0)
        {
            menuElement.setElementOrder(((PageMenuDefinitionElement)menuDefinition.accessElements().get(index-1)).getElementOrder() + 1);
        }
        else
        {
            menuElement.setElementOrder(0);
        }
        // maintain element order in subsequent elements
        for (int i = index, limit = menuDefinition.accessElements().size() - 1; (i < limit); i++)
        {
            PageMenuDefinitionElement nextMenuElement = (PageMenuDefinitionElement)menuDefinition.accessElements().get(i + 1);
            if (nextMenuElement.getElementOrder() <= menuElement.getElementOrder())
            {
                // adjust element order for next element
                nextMenuElement.setElementOrder(menuElement.getElementOrder() + 1);
                menuElement = nextMenuElement;
            }
            else
            {
                // element order maintained for remaining list elements
                break;
            }
        }
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public MenuDefinitionElement get(int index)
    {
        // implement for modifiable AbstractList
        return menuDefinition.accessElements().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public MenuDefinitionElement remove(int index)
    {
        // implement for modifiable AbstractList
        return menuDefinition.accessElements().remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public MenuDefinitionElement set(int index, MenuDefinitionElement element)
    {
        // implement for modifiable AbstractList:
        // verify element
        PageMenuDefinitionElement newMenuElement = validateMenuElementForAdd(element);
        // set in underlying ordered list
        PageMenuDefinitionElement menuElement = (PageMenuDefinitionElement)menuDefinition.accessElements().set(index, newMenuElement);
        // set element order in new element
        newMenuElement.setElementOrder(menuElement.getElementOrder());
        // return element
        return menuElement;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return menuDefinition.accessElements().size();
    }
}
