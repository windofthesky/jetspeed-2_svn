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

/**
 * DatabasePageManagerPrincipalKeyCacheObject
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
*/
public class DatabasePageManagerPrincipalKeyCacheObject implements DistributedCacheObject
{
    private static final long serialVersionUID = 1L;
    
    private String principalKey;
    
    public DatabasePageManagerPrincipalKeyCacheObject(String principalKey)
    {
        this.principalKey = principalKey;
    }

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
        if ((obj != null) && (obj instanceof DatabasePageManagerPrincipalKeyCacheObject))
        {
            final DatabasePageManagerPrincipalKeyCacheObject other = (DatabasePageManagerPrincipalKeyCacheObject) obj;
            return principalKey.equals(other.principalKey);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getPrincipalKey().hashCode();
    }


    /**
     * @return the principalKey
     */
    public String getPrincipalKey()
    {
        return principalKey;
    }
}
