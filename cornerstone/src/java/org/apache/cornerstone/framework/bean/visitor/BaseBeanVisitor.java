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

package org.apache.cornerstone.framework.bean.visitor;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.cornerstone.framework.core.ClassUtil;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

public abstract class BaseBeanVisitor extends BaseObject
{
    public static final String REVISION = "$Revision$";

    public static final String VISITOR_SKIP_PROPERTIES = "visitor.skipProperties";

    public abstract void beginObject();
    public abstract void endObject();

    public void visit(Object bean)
    {
        if (bean == null) return;

// TODO: need to rethink
//        if (!isLeafType(bean))
//        {
//            if (_visited.contains(bean)) return;
//            _visited.add(bean);
//        }

//        if (getConfigProperty("printPath") != null)
//            _Logger.debug("path=" + getPath());

        if (isSpecialObject(bean))
        {
            visitSpecialObject(bean);
        }
        else
        {
            beginObject();
            visitProperties(bean);
            endObject();
        }
    }

    public void visitProperties(Object bean)
    {
        Properties beanClassProperties = ClassUtil.getClassConfig(bean.getClass());
        Set skipPropertyNameSet = Util.convertStringsToSet(beanClassProperties.getProperty(VISITOR_SKIP_PROPERTIES)); 

        Set propertyNameSet = (bean instanceof Map) ? ((Map) bean).keySet() : _beanHelper.getPropertyNameSet(bean);
        for (Iterator itr = propertyNameSet.iterator(); itr.hasNext();)
        {
            String propertyName = (String) itr.next();
            if (!skipPropertyNameSet.contains(propertyName))
            {
                enterProperty(bean, propertyName);
                visitProperty(bean, propertyName);
                exitProperty(bean, propertyName);
            }
        }
    }

    public void enterProperty(Object bean, String name)
    {
        // _Logger.debug("entering property '" + name + "'");
        _path.push(name);
        _index.push(new Integer(-1));
        _level++;
    }

    public void visitProperty(Object bean, String name)
    {
        if (bean instanceof Map)
        {
            Object value = ((Map) bean).get(name);
            enterSimpleProperty(bean, name, value);
            visitSimpleProperty(bean, name, value);
            exitSimpleProperty(bean, name, value);
        }
        else
        {
            PropertyDescriptor propDesc = _beanHelper.getPropertyDescriptor(bean, name);
            if (propDesc == null)
            {
                Object value = _beanHelper.getProperty(bean, name);
                enterSimpleProperty(bean, name, value);
                visitSimpleProperty(bean, name, value);
                exitSimpleProperty(bean, name, value);
            }
            else if (propDesc instanceof IndexedPropertyDescriptor)
            {
                enterIndexedProperty(bean, name);
                visitIndexedProperty(bean, name);
                exitIndexedProperty(bean, name);
            }
            else
            {
                Object value = _beanHelper.getProperty(bean, name);
                enterSimpleProperty(bean, name, value);
                visitSimpleProperty(bean, name, value);
                exitSimpleProperty(bean, name, value);
            }
        }
    }

    public void exitProperty(Object bean, String name)
    {
        _index.pop();
        _path.pop();
        _level--;
    }

    public void enterSimpleProperty(Object bean, String name, Object value)
    {
    }

    public void visitSimpleProperty(Object bean, String name, Object value)
    {
        visit((Object) value);
    }

    public void exitSimpleProperty(Object bean, String name, Object value)
    {
    }

    public void enterIndexedProperty(Object bean, String name)
    {
        _level++;
    }

    public void visitIndexedProperty(Object bean, String name)
    {
        // _Logger.debug("visitIndexedProperty('" + name + "')");
        // TO DO: only List and Object[] are supported right now
        Object indexedPropertyValue = _beanHelper.getProperty(bean, name);
        if (indexedPropertyValue instanceof List)
        {
            List list = (List) indexedPropertyValue;
            if (list != null)
            {
                for (int i = 0; i < list.size(); i++)
                {
                    Object value = list.get(i);
                    enterIndexedPropertyElement(bean, name, i, value);
                    visitIndexedPropertyElement(bean, name, 0, i, value);
                    exitIndexedPropertyElement(bean, name, i, value);
                }
            }
        }
        else
        {
            Object[] array = (Object[]) indexedPropertyValue;
            if (array != null)
            {
                for (int i = 0; i < array.length; i++)
                {
                    Object value = array[i];
                    enterIndexedPropertyElement(bean, name, i, value);
                    visitIndexedPropertyElement(bean, name, 0, i, value);
                    exitIndexedPropertyElement(bean, name, i, value);
                }
            }
        }
    }

    /**
     * Visits elements of an indexed property in a range.
     * @param bean
     * @param name
     * @param start Start index of the range.
     * @param end End index of the range.
     */
    public void visitIndexedProperty(
        Object bean,
        String name,
        int start,
        int end
    )
    {
        _path.push(name);
        _index.push(new Integer(-1));

        Object indexedPropertyValue = _beanHelper.getProperty(bean, name);
        if (indexedPropertyValue instanceof List)
        {
            List list = (List) indexedPropertyValue;
            for (int i = start; i <= end; i++)
            {
                Object value = list.get(i);
                enterIndexedPropertyElement(bean, name, i, value);
                visitIndexedPropertyElement(bean, name, start, i, value);
                exitIndexedPropertyElement(bean, name, i, value);
            }
        }
        else
        {
            Object[] array = (Object[]) indexedPropertyValue;
            for (int i = start; i <= end; i++)
            {
                Object value = array[i];
                enterIndexedPropertyElement(bean, name, i, value);
                visitIndexedPropertyElement(bean, name, start, i, value);
                exitIndexedPropertyElement(bean, name, i, value);
            }
        }

        _index.pop();
        _path.pop();
    }

    public void exitIndexedProperty(Object bean, String name)
    {
        _level--;
    }

    public void enterIndexedPropertyElement(Object bean, String name, int index, Object value)
    {
        _index.pop();
        _index.push(new Integer(index));
        _level++;
    }

    public void visitIndexedPropertyElement(Object bean, String name, int startIndex, int index, Object value)
    {
        // _Logger.debug("visitIndexedPropertyElement('" + name + "'): (value instanceof Object)=" + (value instanceof Object));
        visit((Object) value);
    }

    public void exitIndexedPropertyElement(Object bean, String name, int index, Object value)
    {
        _level--;
    }

    public void visitSpecialObject(Object value)
    {
    }

    protected String getPath()
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < _path.size(); i++)
        {
            if (i > 0) buf.append('.');

            buf.append(_path.get(i));

            int index = ((Integer) _index.get(i)).intValue();
            if (index >= 0)
            {
                buf.append('[');
                buf.append("" + index);
                buf.append(']');
            }
        }

        return buf.toString();
    }

    protected boolean isSpecialObject(Object o)
    {
        return false;
    }

    protected static Class[] _LeafTypes =
    {
        String.class, Boolean.class, Integer.class, Long.class,
        Float.class, Double.class, Byte.class, Character.class,
        Number.class, Short.class, BigDecimal.class, Timestamp.class
    };

    protected static Set _LeafTypeSet = new HashSet();
    static
    {
        for (int i = 0; i < _LeafTypes.length; i++)
        {
            _LeafTypeSet.add(_LeafTypes[i]);
        }
    }

    protected boolean isLeafType(Object o)
    {
        return _LeafTypeSet.contains(o.getClass());
    }

    private static Logger _Logger = Logger.getLogger(BaseBeanVisitor.class);
    protected BeanHelper _beanHelper = BeanHelper.getSingleton();
    protected Set _visited = new HashSet();
    protected int _level = 0;
    protected Stack _path = new Stack();
    protected Stack _index = new Stack();
}