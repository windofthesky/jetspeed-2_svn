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
package org.apache.jetspeed.om.folder.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * FolderOrderList
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
class FolderOrderList extends AbstractList<String>
{
    private FolderImpl folder;

    private List<FolderOrder> removedFolderOrders;

    FolderOrderList(FolderImpl folder)
    {
        super();
        this.folder = folder;
    }

    /**
     * wrapNameStringForAdd
     *
     * Wraps and validates folder order name string
     * to be added to this list.
     *
     * @param name folder order name string to add
     * @return list element to add
     */
    private FolderOrder wrapNameStringForAdd(String name)
    {
        // only non-null names supported
        if (name == null)
        {
            throw new NullPointerException("Unable to add null to list.");
        }
        // wrap folder order name string
        FolderOrder folderOrder = new FolderOrder();
        folderOrder.setName(name);
        // make sure element is unique
        if (folder.accessFolderOrders().contains(folderOrder))
        {
            throw new IllegalArgumentException("Unable to add duplicate entry to list: " + folderOrder.getName());
        }
        // retrieve from removed list to reuse
        // previously removed element
        if (removedFolderOrders != null)
        {
            int removedIndex = removedFolderOrders.indexOf(folderOrder);
            if (removedIndex >= 0)
            {
                folderOrder = (FolderOrder)removedFolderOrders.remove(removedIndex);
            }
        }
        return folderOrder;
    }

    /**
     * getRemovedFolderOrders
     *
     * @return removed folder orders tracking collection
     */
    private List<FolderOrder> getRemovedFolderOrders()
    {
        if (removedFolderOrders == null)
        {
            removedFolderOrders = Collections.synchronizedList(new ArrayList<FolderOrder>());
        }
        return removedFolderOrders;
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int,java.lang.Object)
     */
    public void add(int index, String element)
    {
        // implement for modifiable AbstractList:
        // validate index
        if ((index < 0) || (index > folder.accessFolderOrders().size()))
        {
            throw new IndexOutOfBoundsException("Unable to add to list at index: " + index);
        }
        // wrap and verify folder order name string
        FolderOrder folderOrder = wrapNameStringForAdd(element);
        // add to underlying ordered list
        folder.accessFolderOrders().add(index, folderOrder);
        // set sort order in added element
        if (index > 0)
        {
            folderOrder.setSortOrder(folder.accessFolderOrders().get(index-1).getSortOrder() + 1);
        }
        else
        {
            folderOrder.setSortOrder(0);
        }
        // maintain sort order in subsequent elements
        for (int i = index, limit = folder.accessFolderOrders().size() - 1; (i < limit); i++)
        {
            FolderOrder nextFolderOrder = folder.accessFolderOrders().get(i + 1);
            if (nextFolderOrder.getSortOrder() <= folderOrder.getSortOrder())
            {
                // adjust sort order for next element
                nextFolderOrder.setSortOrder(folderOrder.getSortOrder() + 1);
                folderOrder = nextFolderOrder;
            }
            else
            {
                // sort order maintained for remaining list elements
                break;
            }
        }
        // clear all cached folder ordering
        folder.clearDocumentOrderComparator();
    }

    /* (non-Javadoc)
     * @see java.util.List#get(int)
     */
    public String get(int index)
    {
        // implement for modifiable AbstractList:
        // unwrap folder order name string
        return folder.accessFolderOrders().get(index).getName();
    }

    /* (non-Javadoc)
     * @see java.util.List#remove(int)
     */
    public String remove(int index)
    {
        // implement for modifiable AbstractList
        FolderOrder removed = folder.accessFolderOrders().remove(index);
        if (removed != null)
        {
            // save removed element 
            getRemovedFolderOrders().add(removed);
            // clear all cached folder ordering
            folder.clearDocumentOrderComparator();
            return removed.getName();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int,java.lang.Object)
     */
    public String set(int index, String element)
    {
        // implement for modifiable AbstractList:
        // wrap and verify folder order name string
        FolderOrder newFolderOrder = wrapNameStringForAdd(element);
        // set in underlying ordered list
        FolderOrder folderOrder = folder.accessFolderOrders().set(index, newFolderOrder);
        // set sort order in new element
        newFolderOrder.setSortOrder(folderOrder.getSortOrder());
        // save replaced element
        getRemovedFolderOrders().add(folderOrder);
        // clear all cached folder ordering
        folder.clearDocumentOrderComparator();
        // return unwrapped folder order name string
        return folderOrder.getName();
    }

    /* (non-Javadoc)
     * @see java.util.List#size()
     */
    public int size()
    {
        // implement for modifiable AbstractList
        return folder.accessFolderOrders().size();
    }
}
