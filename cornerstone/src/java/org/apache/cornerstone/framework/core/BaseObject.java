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

import java.util.*;
import org.apache.cornerstone.framework.api.config.IConfigurable;
import org.apache.cornerstone.framework.api.core.IObject;
import org.apache.cornerstone.framework.api.singleton.ISingletonManager;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.util.OrderedProperties;
import org.apache.log4j.Logger;

/**
The common superclass of all framework classes that adds configurability
through the class' own properties file.
*/

public abstract class BaseObject implements IObject, IConfigurable
{
    public static final String REVISION = "$Revision$";

    public static final String CONFIG_META_INSTANCE_IS_SINGLETON = Constant.META + Constant.DOT + Constant.INSTANCE + Constant.DOT + "isSingleton";

    public void init()
    {
        // copy class config
    	_config = new OrderedProperties();
        OrderedProperties classConfig = ClassUtil.getClassConfig(getClass());
        _config.putAll(classConfig);
    }

    public OrderedProperties getConfig()
    {
    	return _config;
    }

    /**
     * Gets the value of property p in my configuration.
     * @param p name of configuration property.
     * @return value of configuration property.
     */
    public String getConfigProperty(String p)
    {
    	String value = _config.getProperty(p);
        return value;
    }

    /**
     * Gets the value of property p1.p2 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2)
    {
        String p = p1 + Constant.CONF_DELIM + p2;
        return getConfigProperty(p);
    }

    /**
     * Gets the value of property p1.p2.p3 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2, String p3)
    {
        String p = p1 + Constant.CONF_DELIM + p2 + Constant.CONF_DELIM + p3;
        return getConfigProperty(p);
    }

    /**
     * Gets the value of property p1.p2.p3.p4 in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param p4 name of segement 4 of configuration property.
     * @return value of configuration property
     */
    public String getConfigProperty(String p1, String p2, String p3, String p4)
    {
        String p = p1 + Constant.CONF_DELIM + p2 + Constant.CONF_DELIM + p3 + Constant.CONF_DELIM + p4;
        return getConfigProperty(p);
    }

    /**
     * Gets the value of property p in my configuration.
     * @param p name of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p, String defaultValue)
    {
        String value = getConfigProperty(p);
        return value == null ? defaultValue : value;
    }

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String defaultValue)
    {
        String value = getConfigProperty(p1, p2);
        return value == null ? defaultValue : value;
    }

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String defaultValue)
    {
        String value = getConfigProperty(p1, p2, p3);
        return value == null ? defaultValue : value;
    }

    /**
     * Gets the value of property p in my configuration.
     * @param p1 name of segement 1 of configuration property.
     * @param p2 name of segement 2 of configuration property.
     * @param p3 name of segement 3 of configuration property.
     * @param p4 name of segement 4 of configuration property.
     * @param defaultValue value used when configuration properties is
     *   missing
     * @return value of configuration property.
     */
    public String getConfigPropertyWithDefault(String p1, String p2, String p3, String p4, String defaultValue)
    {
        String value = getConfigProperty(p1, p2, p3, p4);
        return value == null ? defaultValue : value;
    }

    /**
     * Overwrites configuration with another.
     * @param overwrites configuration used to overwrite.
     */
    public void overwriteConfig(Properties overwrites)
    {
        for (
            Enumeration e = overwrites.propertyNames();
            e.hasMoreElements();
        )
        {
            String name = (String) e.nextElement();
            String value = overwrites.getProperty(name);
            _config.setProperty(name, value);
        }
    }

    public Object getClassVariable(String name)
    {
        return _ClassVariableMap.get(getClassVariableKey(name));
    }

    public void setClassVariable(String name, Object value)
    {
        _ClassVariableMap.put(getClassVariableKey(name), value);
    }

	protected BaseObject()
	{
        init();

        String configIsSingleton = getConfigProperty(CONFIG_META_INSTANCE_IS_SINGLETON);
        Boolean isSingleton = new Boolean(configIsSingleton);

        // does singleton already exist?
        if (isSingleton == Boolean.TRUE)
        {
        	ISingletonManager singletonManager = (ISingletonManager) Cornerstone.getImplementation(ISingletonManager.class);
        	Object existingInstance = singletonManager.getSingleton(getClass().getName());
            if (existingInstance != null)
                throw new RuntimeException("singleton already exists; cannot create another instance");
        }
	}

    protected String getClassVariableKey(String name)
    {
        return getClass().getName() + ":" + name;
    }

    private static Logger _Logger = Logger.getLogger(BaseObject.class);
    protected static Map _ClassVariableMap = new HashMap();
    protected OrderedProperties _config;
}