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

package org.apache.cornerstone.framework.singleton;

import java.util.HashMap;
import java.util.Map;
import org.apache.cornerstone.framework.api.singleton.ISingletonManager;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

/**
Manager for all singleton instances.
*/

public class BaseSingletonManager implements ISingletonManager
{
    public static final String REVISION = "$Revision$";

    public static BaseSingletonManager getSingleton()
    {
    	return _Singleton;
    }

    /**
     * Retrieves the singleton instance of a class.
     * @param className
     * @return singleton instance of class className.
     */
    public Object getSingleton(String className)
    {
        try
        {
            // make sure class is loaded first
            Object s = _singletonMap.get(className);
            if (s == null)
            {
                s = Util.createInstance(className);
                addSingleton(s);
            }
            return s;
        }
        catch (Exception e)
        {
            _Logger.error("failed to create singleton for class " + className, e);
            return null;
        }
    }

    protected BaseSingletonManager()
    {
    	addSingleton(this);
    }

    /**
     * Registers a singleton instance with this manager.
     * @param singleton
     */
    protected void addSingleton(Object singleton)
    {
        _singletonMap.put(singleton.getClass().getName(), singleton);
        if (_Logger.isInfoEnabled())
        {
            _Logger.info(singleton + " added");
        }
    }

    private static Logger _Logger = Logger.getLogger(BaseSingletonManager.class);
    private static BaseSingletonManager _Singleton = new BaseSingletonManager();
    protected Map _singletonMap = new HashMap();
}