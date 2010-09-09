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
package org.apache.jetspeed.container.invoker;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.jetspeed.util.ThreadLocalHashMap;

/**
 * ConcurrentRequestMethodInterceptor
 * 
 * @version $Id$
 */
public class ConcurrentRequestMethodInterceptor implements MethodInterceptor
{
    private static final String DEFAULT_PROPERTY_ATTRIBUTE_PREFIX = ConcurrentRequestMethodInterceptor.class.getName() + ".";
    
    private Object requestObject;
    private ThreadLocalHashMap<String, Object> attributes;
    private boolean synchronizeRequestObjectOnDefaultInvocation = true;
    private Set<String> attributableProperties;
    private boolean hasAttributableProperties = false;
    private String propertyAttributePrefix = DEFAULT_PROPERTY_ATTRIBUTE_PREFIX;
    
    public ConcurrentRequestMethodInterceptor(Object requestObject)
    {
        this.requestObject = requestObject;
        attributes = new ThreadLocalHashMap<String, Object>();
    }
    
    public void setSynchronizeRequestObjectOnDefaultInvocation(boolean synchronizeRequestObjectOnDefaultInvocation)
    {
        this.synchronizeRequestObjectOnDefaultInvocation = synchronizeRequestObjectOnDefaultInvocation;
    }
    
    public boolean getSynchronizeRequestObjectOnDefaultInvocation()
    {
        return synchronizeRequestObjectOnDefaultInvocation;
    }
    
    public void setAttributableProperties(final String [] attributableProperties)
    {
        this.attributableProperties = new HashSet<String>();
        
        for (String attributableProperty : attributableProperties)
        {
            this.attributableProperties.add(attributableProperty);
        }
        
        this.hasAttributableProperties = !this.attributableProperties.isEmpty();
    }
    
    public String [] getAttributableProperties()
    {
        if (attributableProperties == null || attributableProperties.isEmpty())
        {
            return new String[0];
        }
        
        String [] arr = new String[attributableProperties.size()];
        
        int index = 0;
        for (String attributableProperty : attributableProperties)
        {
            arr[index++] = attributableProperty;
        }
        
        return arr;
    }
    
    public void setPropertyAttributePrefix(String propertyAttributePrefix)
    {
        this.propertyAttributePrefix = propertyAttributePrefix;
    }
    
    public String getPropertyAttributePrefix()
    {
        return propertyAttributePrefix;
    }
    
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable
    {
        if (Modifier.isAbstract(method.getModifiers()))
        {
            return null;
        }
        
        String methodName = method.getName();
        
        if ("getAttribute".equals(methodName))
        {
            if (attributes.containsKey((String) args[0])) 
            {
                return attributes.get((String) args[0]);
            }
        }
        else if ("setAttribute".equals(methodName))
        {
            attributes.put((String) args[0], args[1]);
        }
        else if ("removeAttribute".equals(methodName))
        {
            attributes.remove((String) args[0]);
        }
        else if (hasAttributableProperties)
        {
            if (methodName.startsWith("get") && args.length == 0)
            {
                String propName = getPropertyName(method);
                
                if (propName != null && attributableProperties.contains(propName))
                {
                    String attrName = propertyAttributePrefix + propName;
                    
                    if (attributes.containsKey(attrName))
                    {
                        return attributes.get(attrName);
                    }
                }
            }
            else if (methodName.startsWith("set") && args.length == 1)
            {
                String propName = getPropertyName(method);
                
                if (propName != null && attributableProperties.contains(propName))
                {
                    String attrName = propertyAttributePrefix + propName;
                    attributes.put(attrName, args[0]);
                }
            }
        }
        
        if (synchronizeRequestObjectOnDefaultInvocation)
        {
            synchronized (requestObject)
            {
                return method.invoke(requestObject, args);
            }
        }
        else
        {
            return method.invoke(requestObject, args);
        }
    }
    
    private String getPropertyName(final Method method)
    {
        String name = method.getName();
        
        if (name.startsWith("get") || name.startsWith("set"))
        {
            char firstChar = name.charAt(3);
            
            if (Character.isUpperCase(firstChar))
            {
                StringBuilder sb = new StringBuilder(name.substring(3));
                sb.setCharAt(0, Character.toLowerCase(firstChar));
                name = sb.toString();
            }
            
            return name;
        }
        
        return null;
    }
}
