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

/**
 * FolderOrder
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class FolderOrder
{
    private int id;
    private int sortOrder;
    private String name;

    /**
     * getSortOrder
     *
     * @return sort order
     */
    public int getSortOrder()
    {
        return sortOrder;
    }

    /**
     * setSortOrder
     *
     * @param order sort order
     */
    public void setSortOrder(int order)
    {
        sortOrder = order;
    }

    /**
     * getName
     *
     * @return folder/page/link name
     */
    public String getName()
    {
        return name;
    }

    /**
     * setName
     *
     * @param name folder/page/link name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof FolderOrder)
        {
            if (name != null)
            {
                return name.equals(((FolderOrder)o).getName());
            }
            else
            {
                return (((FolderOrder)o).getName() == null);
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (name != null)
        {
            return name.hashCode();
        }
        return 0;
    }
}
