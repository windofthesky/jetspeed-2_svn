/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.services.plugin;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.services.persistence.PathResolver;

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
