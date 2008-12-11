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
package org.apache.jetspeed.page.impl;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.ojb.broker.Identity;

/**
 * DatabasePageManagerCacheObject
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
*/
public class DatabasePageManagerCacheObject implements DistributedCacheObject
{
    private static final long serialVersionUID = 3575475610695136850L;

    // Members
    
    private Identity id = null;
    private String path = null;

    // Constructor
    
    /**
     * Construct new cache object with id and path
     * 
     * @param id
     * @param path
     */
    public DatabasePageManagerCacheObject(final Identity id, final String path)
    {
        this.path = path;
        this.id = id;
    }

    /**
     * Serialization constructor
     */
    public DatabasePageManagerCacheObject()
    {
    }

    // Implementation
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cache.DistributedCacheObject#notifyChange(int)
     */
    public void notifyChange(int action)
    {
        switch (action)
        {
            case CacheElement.ActionAdded:
            case CacheElement.ActionChanged:
            case CacheElement.ActionRemoved:
            case CacheElement.ActionEvicted:
            case CacheElement.ActionExpired:
                break;
            default:
                return;
        }
        return;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof DatabasePageManagerCacheObject))
        {
            final DatabasePageManagerCacheObject other = (DatabasePageManagerCacheObject) obj;
            return getPath().equals(other.getPath());
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getPath().hashCode();
    }

    // Data access
    
    /**
     * @return wrapper id
     */
    public Identity getId()
    {
        return id;
    }

    /**
     * @return wrapper path
     */
    public String getPath()
    {
        return path;
    }
}
