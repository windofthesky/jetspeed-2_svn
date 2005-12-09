/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration.caches;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.decoration.PathResolverCache;


/**
 * Uses a <code>java.util.HashMap</code> to cache previously located
 * resources pathes.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class HashMapPathResolverCache implements PathResolverCache
{
    protected Map cache;
    
    public HashMapPathResolverCache()
    {
        this.cache = new HashMap();
    }

    public void addPath(String key, String path)
    {
        cache.put(key, path);
    }

    public String getPath(String key)
    {
        return (String) cache.get(key);
    }

    public String removePath(String key)
    {
        return (String) cache.remove(key);
    }

    public void clear()
    {
        cache.clear();        
    }

}
