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

package org.apache.cornerstone.framework.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.util.OrderedProperties;
import org.apache.log4j.Logger;

public class ClassUtil
{
    public static final String CLASS_VAR_CONFIG = ClassUtil.class.getName() + ".config";

    /**
     * Gets the configuration of class c.
     * @param c target class.
     * @return configuration defined for class c.
     */
    public static OrderedProperties getClassConfig(Class c)
    {
        if (c == Object.class)
            return new OrderedProperties();

        OrderedProperties config = (OrderedProperties) getClassVariable(c, CLASS_VAR_CONFIG);
        if (config == null)
        {
            if (c.getName().endsWith("BaseService"))
            {
                int n = 0;
            }
            OrderedProperties selfConfig = loadClassConfig(c);
            OrderedProperties superConfig = getClassConfig(c.getSuperclass());
            config = new OrderedProperties();
            config.putAll(superConfig);
            config.putAll(selfConfig);
            setClassVariable(c, CLASS_VAR_CONFIG, config);
            _Logger.debug("class=" + c.getName() + " config=" + config);

            if (_Logger.isDebugEnabled())
            {
                List keyList = config.getKeyList();
                for (int i = 0; i < keyList.size(); i++)
                {
                    String key = (String) keyList.get(i);
                    String value = config.getProperty(key);
                    _Logger.debug(key + "='" + value + "'");
                }
            }
        }

        return config;
    }

    public static Object getClassVariable(Class c, String name)
    {
        return _ClassVariableMap.get(getClassVariableKey(c, name));
    }

    public static void setClassVariable(Class c, String name, Object value)
    {
        if (_Logger.isDebugEnabled()) _Logger.debug("setClassVariable: name=" + getClassVariableKey(c, name));
        _ClassVariableMap.put(getClassVariableKey(c, name), value);
    }

    protected static String getClassVariableKey(Class c, String name)
    {
        return c.getName() + ":" + name;
    }

    /**
     * Loads the configuration of a class.
     * @param c
     * @return Configuration of class c as Properties.
     */
    protected static OrderedProperties loadClassConfig(Class c)
    {
        return loadClassConfig(c, null);
    }

    /**
     * Same as loadClassConfig(Class).  infix is no longer used.
     * @param c
     * @param infix
     * @return Configuration of class c as Properties.
     */
    protected static OrderedProperties loadClassConfig(Class c, String infix)
    {
        OrderedProperties config = new OrderedProperties();
        try
        {
            String cn = c.getName();
            cn = cn.substring(cn.lastIndexOf('.') + 1);

            String fileName;
            if (infix == null)
                fileName = cn + Constant.FILE_EXTENSION_PROPERTIES;
            else
                fileName = cn + "." + infix + Constant.FILE_EXTENSION_PROPERTIES;
            _Logger.debug("class=" + c.getName() + " classConfigFileName=" + fileName);
            InputStream is = c.getResourceAsStream(fileName);
            config.load(is);

            return config;
        }
        catch (Exception e)
        {
            // most likely this class doesn't have config
            return config;
        }
    }

    protected static Map _ClassVariableMap = new HashMap();
    private static Logger _Logger = Logger.getLogger(ClassUtil.class);
}