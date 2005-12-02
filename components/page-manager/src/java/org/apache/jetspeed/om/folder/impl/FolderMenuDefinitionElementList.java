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
package org.apache.jetspeed.om.folder.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;

/**
 * FolderMenuDefinitionElementList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FolderMenuDefinitionElementList extends AbstractList
{
    private FolderMenuDefinitionImpl menuDefinition;

    FolderMenuDefinitionElementList(FolderMenuDefinitionImpl menuDefinition)
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
    private FolderMenuDefinitionElement validateMenuElementForAdd(Object menuElement)
    {
        // validate element instance class
        if (menuElement == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        if (!(menuElement instanceof FolderMenuDefinitionElement))
        {
            // duplicate menu element from equivalent types
            if (menuElement instanceof MenuDefinition)
            {
                MenuDefinition origMenuElement = (MenuDefinition)menuElement;
                FolderMenuDefinitionImpl dupMenuElement = new FolderMenuDefinitionImpl();
                // TODO: move this logic to copy methods on implementations
                dupMenuElement.setName(origMenuElement.getName());
                dupMenuElement.setOptions(origMenuElement.getOptions());
                dupMenuElement.setDepth(origMenuElement.getDepth());
                dupMenuElement.setPaths(origMenuElement.isPaths());
                dupMenuElement.setRegexp(origMenuElement.isRegexp());
                dupMenuElement.setProfile(origMenuElement.getProfile());
                dupMenuElement.setOrder(origMenuElement.getOrder());
                dupMenuElement.setSkin(origMenuElement.getSkin());
                dupMenuElement.setTitle(origMenuElement.getTitle());
                dupMenuElement.setShortTitle(origMenuElement.getShortTitle());
                dupMenuElement.setMenuElements(origMenuElement.getMenuElements());
                dupMenuElement.getMetadata().copyFields(origMenuElement.getMetadata().getFields());
                menuElement = dupMenuElement;
            }
            else if (menuElement instanceof MenuExcludeDefinition)
            {
                MenuExcludeDefinition origMenuElement = (MenuExcludeDefinition)menuElement;
                FolderMenuExcludeDefinitionImpl dupMenuElement = new FolderMenuExcludeDefinitionImpl();
                // TODO: move this logic to copy methods on implementations
                dupMenuElement.setName(origMenuElement.getName());
                menuElement = dupMenuElement;
            }
            else if (menuElement instanceof MenuIncludeDefinition)
            {
                MenuIncludeDefinition origMenuElement = (MenuIncludeDefinition)menuElement;
                FolderMenuIncludeDefinitionImpl dupMenuElement = new FolderMenuIncludeDefinitionImpl();
                // TODO: move this logic to copy methods on implementations
                dupMenuElement.setName(origMenuElement.getName());
                dupMenuElement.setNest(origMenuElement.isNest());
                menuElement = dupMenuElement;
            }
            else if (menuElement instanceof MenuOptionsDefinition)
            {
                MenuOptionsDefinition origMenuElement = (MenuOptionsDefinition)menuElement;
                FolderMenuOptionsDefinitionImpl dupMenuElement = new FolderMenuOptionsDefinitionImpl();
                // TODO: move this logic to copy methods on implementations
                dupMenuElement.setOptions(origMenuElement.getOptions());
                dupMenuElement.setDepth(origMenuElement.getDepth());
                dupMenuElement.setPaths(origMenuElement.isPaths());
                dupMenuElement.setRegexp(origMenuElement.isRegexp());
                dupMenuElement.setProfile(origMenuElement.getProfile());
                dupMenuElement.setOrder(origMenuElement.getOrder());
                dupMenuElement.setSkin(origMenuElement.getSkin());
                menuElement = dupMenuElement;
            }
            else if (menuElement instanceof MenuSeparatorDefinition)
            {
                MenuSeparatorDefinition origMenuElement = (MenuSeparatorDefinition)menuElement;
                FolderMenuSeparatorDefinitionImpl dupMenuElement = new FolderMenuSeparatorDefinitionImpl();
                // TODO: move this logic to copy methods on implementations
                dupMenuElement.setSkin(origMenuElement.getSkin());
                dupMenuElement.setTitle(origMenuElement.getTitle());
                dupMenuElement.setText(origMenuElement.getText());
                dupMenuElement.getMetadata().copyFields(origMenuElement.getMetadata().getFields());
                menuElement = dupMenuElement;
            }
            else
            {
                throw new ClassCastException("Unable to create menu element list instance from: " + menuElement.getClass().getName() + ".");
            }
        }
        return (FolderMenuDefinitionElement)menuElement;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > menuDefinition.accessElements().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // verify element
        FolderMenuDefinitionElement menuElement = validateMenuElementForAdd(element);
        // add to underlying ordered list
        menuDefinition.accessElements().add(index, menuElement);
        // set element order in added element
        if (index > 0)
        {
            menuElement.setElementOrder(((FolderMenuDefinitionElement)menuDefinition.accessElements().get(index-1)).getElementOrder() + 1);
        }
        else
        {
            menuElement.setElementOrder(0);
        }
        // maintain element order in subsequent elements
        for (int i = index, limit = menuDefinition.accessElements().size() - 1; (i < limit); i++)
        {
            FolderMenuDefinitionElement nextMenuElement = (FolderMenuDefinitionElement)menuDefinition.accessElements().get(i + 1);
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
    public Object get(int index)
    {
        // implement for modifiable AbstractList
        return menuDefinition.accessElements().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Object remove(int index)
    {
        // implement for modifiable AbstractList
        return menuDefinition.accessElements().remove(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public Object set(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // verify element
        FolderMenuDefinitionElement newMenuElement = validateMenuElementForAdd(element);
        // set in underlying ordered list
        FolderMenuDefinitionElement menuElement = (FolderMenuDefinitionElement)menuDefinition.accessElements().set(index, newMenuElement);
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
