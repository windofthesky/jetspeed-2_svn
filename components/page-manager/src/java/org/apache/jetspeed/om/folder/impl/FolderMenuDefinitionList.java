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

/**
 * FolderMenuDefinitionList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FolderMenuDefinitionList extends AbstractList
{
    private FolderImpl folder;

    private List removedMenuDefinitions;

    FolderMenuDefinitionList(FolderImpl folder)
    {
        super();
        this.folder = folder;
    }

    /**
     * validateDefinitionForAdd
     *
     * Validates menu definition to be added to this list.
     *
     * @param definition menu definition to add
     * @return list element to add
     */
    private FolderMenuDefinitionImpl validateDefinitionForAdd(Object definition)
    {
        // only non-null definitions supported
        if (definition == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        if (!(definition instanceof FolderMenuDefinitionImpl))
        {
            // duplicate menu element from equivalent types
            if (definition instanceof MenuDefinition)
            {
                MenuDefinition origDefinition = (MenuDefinition)definition;
                FolderMenuDefinitionImpl dupDefinition = new FolderMenuDefinitionImpl();
                // TODO: move this logic to copy methods on implementations
                dupDefinition.setName(origDefinition.getName());
                dupDefinition.setOptions(origDefinition.getOptions());
                dupDefinition.setDepth(origDefinition.getDepth());
                dupDefinition.setPaths(origDefinition.isPaths());
                dupDefinition.setRegexp(origDefinition.isRegexp());
                dupDefinition.setProfile(origDefinition.getProfile());
                dupDefinition.setOrder(origDefinition.getOrder());
                dupDefinition.setSkin(origDefinition.getSkin());
                dupDefinition.setTitle(origDefinition.getTitle());
                dupDefinition.setShortTitle(origDefinition.getShortTitle());
                dupDefinition.setMenuElements(origDefinition.getMenuElements());
                dupDefinition.getMetadata().copyFields(origDefinition.getMetadata().getFields());
                definition = dupDefinition;
            }
            else
            {
                throw new ClassCastException("Unable to create menu element list instance from: " + definition.getClass().getName() + ".");
            }
        }
        // make sure element is unique
        if (folder.accessMenus().contains(definition))
        {
            throw new IllegalArgumentException("Unable to add duplicate entry to list: " + ((FolderMenuDefinitionImpl)definition).getName());
        }
        // retrieve from removed list to reuse
        // previously removed element copying
        // menu definition data
        if (removedMenuDefinitions != null)
        {
            int removedIndex = removedMenuDefinitions.indexOf(definition);
            if (removedIndex >= 0)
            {
                // reuse menu definition with matching name
                FolderMenuDefinitionImpl addDefinition = (FolderMenuDefinitionImpl)definition;
                FolderMenuDefinitionImpl removedDefinition = (FolderMenuDefinitionImpl)removedMenuDefinitions.remove(removedIndex);
                // TODO: move this logic to copy methods on implementations
                // copy menu definition members
                removedDefinition.setOptions(addDefinition.getOptions());
                removedDefinition.setDepth(addDefinition.getDepth());
                removedDefinition.setPaths(addDefinition.isPaths());
                removedDefinition.setRegexp(addDefinition.isRegexp());
                removedDefinition.setProfile(addDefinition.getProfile());
                removedDefinition.setOrder(addDefinition.getOrder());
                removedDefinition.setSkin(addDefinition.getSkin());
                removedDefinition.setTitle(addDefinition.getTitle());
                removedDefinition.setShortTitle(addDefinition.getShortTitle());
                removedDefinition.setMenuElements(addDefinition.getMenuElements());
                // copy menu definition metadata members
                // TODO: strengthen... this code is not robust
                // and may fail if multiple edits without a db
                // update occur and duplicate metadata members
                // are removed in one operation and reinserted
                // in a subsequent operation because the
                // metadata members are required to be unique
                // and a removal list is not maintained for the
                // metadata fields collections yet
                removedDefinition.getMetadata().copyFields(addDefinition.getMetadata().getFields());
                definition = removedDefinition;
            }
        }
        return (FolderMenuDefinitionImpl)definition;
    }

    /**
     * getRemovedMenuDefinitions
     *
     * @return removed menu definitions tracking collection
     */
    private List getRemovedMenuDefinitions()
    {
        if (removedMenuDefinitions == null)
        {
            removedMenuDefinitions = new ArrayList(folder.accessMenus().size());
        }
        return removedMenuDefinitions;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > folder.accessMenus().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // verify menu definition
        FolderMenuDefinitionImpl definition = validateDefinitionForAdd(element);
        // add to underlying ordered list
        folder.accessMenus().add(index, definition);
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public Object get(int index)
    {
        // implement for modifiable AbstractList
        return folder.accessMenus().get(index);
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public Object remove(int index)
    {
        // implement for modifiable AbstractList:
        // save removed element 
        FolderMenuDefinitionImpl removed = (FolderMenuDefinitionImpl)folder.accessMenus().remove(index);
        if (removed != null)
        {
            getRemovedMenuDefinitions().add(removed);
        }
        return removed;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public Object set(int index, Object element)
    {
        // implement for modifiable AbstractList:
        // verify menu definition
        FolderMenuDefinitionImpl newDefinition = validateDefinitionForAdd(element);
        // set in underlying ordered list
        FolderMenuDefinitionImpl definition = (FolderMenuDefinitionImpl)folder.accessMenus().set(index, newDefinition);
        // save replaced element
        getRemovedMenuDefinitions().add(definition);
        // return menu definition
        return definition;
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return folder.accessMenus().size();
    }
}
