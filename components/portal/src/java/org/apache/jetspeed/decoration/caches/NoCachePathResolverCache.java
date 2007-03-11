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
package org.apache.jetspeed.decoration.caches;

import org.apache.jetspeed.decoration.PathResolverCache;

/**
 * A non-caching cache :)
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class NoCachePathResolverCache implements PathResolverCache
{

    public void addPath(String key, String path)
    {
        // Does nothing
    }

    public String getPath(String key)
    {
        // alawys returns null
        return null;
    }

    public String removePath(String key)
    {
        // alawys returns null
        return null;
    }

    public void clear()
    {
        // Does nothing        
    }

}
