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

package org.apache.cornerstone.framework.bean.helper;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

public class BeanHelper extends BaseObject
{
    public static final String REVISION = "$Revision$";

    public static BeanHelper getSingleton()
    {
        return _Singleton;
    }

    public Set getPropertyNameSet(Object bean)
    {
        Class c = bean.getClass();
        Set propertyNameSet = (Set) _PropertyNameSetMap.get(c);
        if (propertyNameSet == null)
        {
            PropertyDescriptor[] propertyDescriptors =
                PropertyUtils.getPropertyDescriptors(bean);

            // TODO
            // Set hiddenPropertyNameSet = bean.getHiddenPropertyNameSet();
            Set hiddenPropertyNameSet = new HashSet();

            propertyNameSet = new HashSet(propertyDescriptors.length);
            for (int i = 0; i < propertyDescriptors.length; i++)
            {
                String name = propertyDescriptors[i].getName();
                if (hiddenPropertyNameSet.contains(name)) continue;
                propertyNameSet.add(name);
//                _Logger.debug("getPropertyNames: class=" + bean.getClass().getName() + " property=" + name);
            }

            propertyNameSet.removeAll(DEFAULT_HIDDEN_NAME_SET);
            _PropertyNameSetMap.put(c, propertyNameSet);
        }

        return propertyNameSet;
    }

    public PropertyDescriptor getPropertyDescriptor(Object bean, String name)
    {
        try
        {
            return PropertyUtils.getPropertyDescriptor(bean, name);
        }
        catch(Exception ex)
        {
            _Logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public Object getProperty(Object bean, String path)
    {
        try
        {
            // path = removeInternalList(path);
            return PropertyUtils.getNestedProperty(bean, path);
        }
        catch (Exception ex)
        {
            String message = ex.getMessage();
            _Logger.error("failed to get property '" + path + "' of class " + bean.getClass().getName() + " (" + ex + (message == null ? "" : ":" + message) + ")");
            return null;
        }
    }

    public void setProperty(Object bean, String path, Object value)
    {
        try
        {
            PropertyUtils.setNestedProperty(bean, path, value);
        }
        catch (Exception ex)
        {
            String message = ex.getMessage();
            _Logger.error("failed to set property '" + path + "' of class " + bean.getClass().getName() + " (" + ex + (message == null ? "" : ":" + message)+ ")");
        }
    }

    private static Logger _Logger = Logger.getLogger(BeanHelper.class);
    private static BeanHelper _Singleton = new BeanHelper();
    protected static Map _PropertyNameSetMap = new HashMap();

    public static Set DEFAULT_HIDDEN_NAME_SET = new HashSet();
    static
    {
        DEFAULT_HIDDEN_NAME_SET.add("class");
    }
}
