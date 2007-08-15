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
package org.apache.jetspeed.components.portletregistry;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.ojb.broker.Identity;

/**
 * OJB cache 
 * 
 * @author dtaylor
 *
 */
public class RegistryCacheObjectWrapper implements DistributedCacheObject
{
    /** The serial uid. */
    private static final long serialVersionUID = 1853381807991868844L;
    Identity id = null;
    String key = null;;


    public RegistryCacheObjectWrapper(Identity id, String key)
    {
        //System.out.println(this.getClass().getName() + "-" + "NodeCache - fullpath=" + fullpath);
        this.key = key;
        this.id = id;
    }


    public Identity getId()
    {
        //System.out.println(this.getClass().getName() + "-" +"getNode=" + node.getFullPath());
        return id;
    }

    public void setIdentity(Identity id)
    {
        // System.out.println(this.getClass().getName() + "-" +"setFullpath=" + node.getFullPath());
        this.id = id;
    }


    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof RegistryCacheObjectWrapper)
        {
            RegistryCacheObjectWrapper other = (RegistryCacheObjectWrapper) obj;
            return getKey().equals(other.getKey());
        }
        return false;
    }

    public int hashCode()
    {
        return getKey().hashCode();
    }

    public String getCacheKey()
    {
        return getKey();
    }

    public String getKey()
    {
        return key;
    }

    
    public void notifyChange(int action)
    {

        switch (action)
        {
            case CacheElement.ActionAdded:
//              System.out.println("CacheObjectAdded =" + this.getKey());
                break;
            case CacheElement.ActionChanged:
//              System.out.println("CacheObjectChanged =" + this.getKey());
                break;
            case CacheElement.ActionRemoved:
//              System.out.println("CacheObjectRemoved =" + this.getKey());
                break;
            case CacheElement.ActionEvicted:
//              System.out.println("CacheObjectEvicted =" + this.getKey());
                break;
            case CacheElement.ActionExpired:
//              System.out.println("CacheObjectExpired =" + this.getKey());
                break;
            default:
                System.out.println("CacheObject -UNKOWN OPRERATION =" + this.getKey());
                return;
        }
        return;
    }
}
