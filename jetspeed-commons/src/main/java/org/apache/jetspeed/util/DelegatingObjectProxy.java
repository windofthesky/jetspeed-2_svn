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
package org.apache.jetspeed.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * DelegatingObjectProxy is a convenient utility class to provide a dynamic proxy
 * by using delegating object(s).
 * If a delegator object containing the invoked method is found, 
 * the proxy will invoke the method of the delegator object.
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 */
public class DelegatingObjectProxy extends BaseObjectProxy
{
    protected Object [] delegatorObjects;
    protected Class [] proxyInterfaces;
    protected Map<Method, ObjectMethodPair> delegatorObjectMethodPairCache = new HashMap<Method, ObjectMethodPair>();

    public static Object createProxy(Class [] proxyInterfaces, Object... delegatorObjects)
    {
        if (proxyInterfaces == null || proxyInterfaces.length == 0)
        {
            throw new IllegalArgumentException("No proxy interface.");
        }
        
        if (delegatorObjects == null || delegatorObjects.length == 0)
        {
            throw new IllegalArgumentException("No delegator object.");
        }
        
        return createProxy(proxyInterfaces, delegatorObjects[0].getClass().getClassLoader(), delegatorObjects);
    }
    
    public static Object createProxy(Class [] proxyInterfaces, ClassLoader classLoader, Object... delegatorObjects)
    {
        if (proxyInterfaces == null || proxyInterfaces.length == 0)
        {
            throw new IllegalArgumentException("No proxy interface.");
        }
        
        if (delegatorObjects == null || delegatorObjects.length == 0)
        {
            throw new IllegalArgumentException("No delegator object.");
        }
        
        InvocationHandler handler = new DelegatingObjectProxy(proxyInterfaces, delegatorObjects);
        return Proxy.newProxyInstance(classLoader, proxyInterfaces, handler);
    }
    
    public DelegatingObjectProxy(Class [] proxyInterfaces, Object... delegatorObjects)
    {
        this.proxyInterfaces = proxyInterfaces;
        this.delegatorObjects = delegatorObjects;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Class targetProxyInterface = null;
        Class declaringClass = method.getDeclaringClass();
        
        for (Class proxyInterface : this.proxyInterfaces)
        {
            if (declaringClass == proxyInterface)
            {
                targetProxyInterface = proxyInterface;
                break;
            }
        }
        
        try
        {
            if (targetProxyInterface != null)
            {
                ObjectMethodPair targetObjectMethodPair = findDelegatorObjectMethodPair(targetProxyInterface, method);            
                return targetObjectMethodPair.method.invoke(targetObjectMethodPair.object, args);
            }
            else
            {
                return super.invoke(proxy, method, args);
            }
        }
        catch (InvocationTargetException ite)
        {
            throw ite.getTargetException();
        }
    }

    private ObjectMethodPair findDelegatorObjectMethodPair(Class proxyInterface, Method method) throws SecurityException, NoSuchMethodException
    {
        ObjectMethodPair objectMethodPair = this.delegatorObjectMethodPairCache.get(method);
        
        if (objectMethodPair == null)
        {
            Method delegatorMethod = null;
            Class targetType = null;
            
            for (Object delegatorObject : this.delegatorObjects)
            {
                targetType = delegatorObject.getClass();

                if (proxyInterface.isAssignableFrom(targetType))
                {
                    targetType = proxyInterface;
                }
                
                try
                {
                    delegatorMethod = targetType.getMethod(method.getName(), method.getParameterTypes());
                }
                catch (Throwable th)
                {
                }
                
                if (delegatorMethod != null)
                {
                    objectMethodPair = new ObjectMethodPair(delegatorObject, delegatorMethod);
                    this.delegatorObjectMethodPairCache.put(method, objectMethodPair);
                    break;
                }
            }
            
            if (objectMethodPair == null)
            {
                throw new NoSuchMethodException(method.toString());
            }
        }
        
        return objectMethodPair;
    }
    
    private class ObjectMethodPair
    {
        Object object;
        Method method;
        
        ObjectMethodPair(Object object, Method method)
        {
            this.object = object;
            this.method = method;
        }
    }
}
