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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
Keys are maintained in their insertion order.
*/

public class OrderedProperties extends PropertiesTree
{
    public static final String REVISION = "$Revision$";

    public Object put(Object key, Object value)
    {
//        if (_Logger.isDebugEnabled()) _Logger.debug("put: key=" + key + " value=" + value);
        Object v = super.put(key, value);
        if (!_keyList.contains(key))    // order is not changed when modified
            _keyList.add(key);
        _valueList = null;
        return v;
    }

    public Object remove(Object key)
    {
        Object v = super.remove(key);
        _keyList.remove(key);
        _valueList = null;
        return v;
    }

    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public synchronized void clear()
    {
        super.clear();
        _keyList.clear();
        _valueList = null;
    }

    public List getKeyList()
    {
        return _keyList;
    }

    public List getValueList()
    {
        if (_valueList == null)
        {
            List kl = getKeyList();
            _valueList = new ArrayList();
            for (int i = 0; i < kl.size(); i++)
            {
                _valueList.add(get(kl.get(i)));
            }
        }
        return _valueList;
    }

    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public synchronized void putAll(Map map)
    {
        if (map instanceof OrderedProperties)
        {
            // maintain order
            OrderedProperties op = (OrderedProperties) map;
            List keyList = op.getKeyList();
            for (int i = 0; i < keyList.size(); i++)
            {
                String key = (String) keyList.get(i);
                String value = op.getProperty(key);
                setProperty(key, value);
            }
        }
        else
        {
            super.putAll(map);
        }
    }

    private static Logger _Logger = Logger.getLogger(OrderedProperties.class);
    protected List _keyList = new ArrayList();
    protected List _valueList = null;
}