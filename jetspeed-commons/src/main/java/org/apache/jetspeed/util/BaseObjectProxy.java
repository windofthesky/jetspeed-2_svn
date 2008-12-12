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
import java.lang.reflect.Method;

/**
 * BaseObjectProxy
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: BaseObjectProxy.java 516448 2007-03-09 16:25:47Z ate $
 */
public class BaseObjectProxy implements InvocationHandler 
{

    protected static Method hashCodeMethod;
    protected static Method equalsMethod;
    protected static Method toStringMethod;
    
    static 
    {
    	try 
        {
    	    hashCodeMethod = Object.class.getMethod("hashCode", (Class [])null);
    	    equalsMethod = Object.class.getMethod("equals", new Class [] { Object.class });
    	    toStringMethod = Object.class.getMethod("toString", (Class [])null);
        } 
        catch (NoSuchMethodException e) 
        {
    	    throw new NoSuchMethodError(e.getMessage());
    	}
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Object result = null;
    	Class declaringClass = method.getDeclaringClass();

    	if (declaringClass == Object.class) 
        {
    	    if (hashCodeMethod.equals(method)) 
            {
                result = proxyHashCode(proxy);
    	    } 
            else if (equalsMethod.equals(method)) 
            {
                result = proxyEquals(proxy, args[0]);
    	    } 
            else if (toStringMethod.equals(method)) 
            {
                result = proxyToString(proxy);
    	    } 
            else 
            {
                throw new InternalError("unexpected Object method dispatched: " + method);
    	    }
    	}
        else
        {
            throw new InternalError("unexpected Object method dispatched: " + method);
        }
        
        return result;
    }
    
    protected Integer proxyHashCode(Object proxy) 
    {
    	return new Integer(System.identityHashCode(proxy));
    }

    protected Boolean proxyEquals(Object proxy, Object other) 
    {
    	return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
    }

    protected String proxyToString(Object proxy) 
    {
    	return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
    }

}
