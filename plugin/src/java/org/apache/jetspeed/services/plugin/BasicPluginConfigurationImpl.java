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
package org.apache.jetspeed.services.plugin;

import java.util.HashMap;
import java.util.Map;


/**
 *  Simple concrete implementation of PluginConfiguration
 */
public class BasicPluginConfigurationImpl implements PluginConfiguration
{
    private String name;
    private String description;
    private String classname;
    private Map properties;
    private boolean isDefault;
    private PathResolver pathResolver;
    private Object factory;

    public BasicPluginConfigurationImpl()
    {
        properties = new HashMap();
    }

    /**
     * @return
     */
    public String getClassName()
    {
        return classname;
    }

    /**
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param string
     */
    public void setClassName(String string)
    {
        classname = string;
    }

    /**
     * @param string
     */
    public void setDescription(String string)
    {
        description = string;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name = string;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PluginConfiguration#getProperty(java.lang.String)
     */
    public String getProperty(String name)
    {
        return (String) properties.get(name);
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PluginConfiguration#setProperty(java.lang.String, java.lang.String)
     */
    public void setProperty(String name, String value)
    {
        properties.put(name, value);

    }

    /**
     * @return
     */
    public boolean isDefault()
    {
        return isDefault;
    }

    /**
     * @param b
     */
    public void setDefault(boolean b)
    {
        isDefault = b;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PluginConfiguration#getProperty(java.lang.String, java.lang.String)
     */
    public String getProperty(String name, String defaultValue)
    {
        String value = getProperty(name);
        if (value == null)
        {
            value = defaultValue;
        }

        return value;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PluginConfiguration#getPathResolver()
     */
    public PathResolver getPathResolver()
    {
        return pathResolver;
    }

    /**
     * @see org.apache.jetspeed.services.perisistence.PluginConfiguration#setPathResolver(org.apache.jetspeed.services.perisistence.PathResolver)
     */
    public void setPathResolver(PathResolver pathResolver)
    {
        this.pathResolver = pathResolver;
    }

    /**
     * @return
     */
    public Object getFactory()
    {
        return factory;
    }

    /**
     * @param object
     */
    public void setFactory(Object object)
    {
        factory = object;
    }

}
