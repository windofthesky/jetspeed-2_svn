/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.cornerstone.framework.util;

import java.util.Map;
import java.util.Set;

public class NameValueListOnMap extends BaseNameValueList
{
    public NameValueListOnMap(Map map)
    {
        _map = map;
    }

    /* (non-Javadoc)
     * @see cornerstone.framework.util.INameValueList#getNameSet()
     */
    public Set getNameSet()
    {
        return _map.keySet();
    }

    /* (non-Javadoc)
     * @see cornerstone.framework.util.INameValueList#getValue(java.lang.String)
     */
    public Object getValue(String name)
    {
        return _map.get(name);
    }

    /* (non-Javadoc)
     * @see cornerstone.framework.util.INameValueList#setValue(java.lang.String, java.lang.Object)
     */
    public void setValue(String name, Object value)
    {
        _map.put(name, value);
    }

    protected Map _map;
}