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

package org.apache.cornerstone.framework.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.util.BaseNameValueList;

/**
Base implementation of IContext.
*/

public class BaseContext extends BaseNameValueList implements IContext
{
    public static final String REVISION = "$Revision$";

    public BaseContext()
    {
        _map = new HashMap();
    }

    public BaseContext(Map map)
    {
        _map = map;
    }

    /**
     * Gets a property from context.
     * @param name name of property.
     * @return value of property.
     */
    public Object getValue(String name)
    {
        return _map.get(name);
    }

    /**
     * Sets a property on context.
     * @param name name of property.
     * @param value value of property.
     */
    public void setValue(String name, Object value)
    {
        _map.put(name, value);
    }

    public Set getNameSet()
    {
        return _map.keySet();
    }

    protected Map _map;
}