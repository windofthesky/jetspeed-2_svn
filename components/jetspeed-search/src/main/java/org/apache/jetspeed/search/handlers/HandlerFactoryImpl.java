/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.search.handlers;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.search.HandlerFactory;
import org.apache.jetspeed.search.ObjectHandler;

/**
 * Search object handler factory
 *
 * @author <a href="mailto: morciuch@apache.org">Mark Orciuch</a>
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 * 
 * @version $Id$
 */
public class HandlerFactoryImpl implements HandlerFactory
{
    private final Map handlerCache = new HashMap();
    private Map classNameMapping = new HashMap();
    
    public HandlerFactoryImpl(Map classNameMapping)
    {
        this.classNameMapping = classNameMapping;
    }
    
    public void addClassNameMapping(String className, String handlerClassName)
    {
        classNameMapping.put(className, handlerClassName);
    }
    
    /**
     * Returns parsed object handler for specific object
     * 
     * @param obj
     * @return 
     */
    public ObjectHandler getHandler(Object obj) throws Exception
    {
        return getHandler(obj.getClass().getName());

    }
    
    /**
    * Returns parsed object handler for specific object
    * 
    * @param obj
    * @return 
    */
    public ObjectHandler getHandler(String className) throws Exception
    {
        ObjectHandler handler = null;
        
        if(handlerCache.containsKey(className))
        {
            handler = (ObjectHandler)handlerCache.get(className);
        }
        else
        {
            String handlerClass = (String) classNameMapping.get(className);
    
            if (handlerClass == null)
            {
                throw new Exception("No handler was found for document type: " + className);
            }
    
            //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            
            //handler = (ObjectHandler) classLoader.loadClass(handlerClass).newInstance();
            handler = (ObjectHandler)Class.forName(handlerClass).newInstance();
            handlerCache.put(className, handler);
        }
        //System.out.println("HandlerFactory: returning handler " + handler + " for " + obj);

        return handler;
    }
}
