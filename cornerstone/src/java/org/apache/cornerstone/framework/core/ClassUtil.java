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