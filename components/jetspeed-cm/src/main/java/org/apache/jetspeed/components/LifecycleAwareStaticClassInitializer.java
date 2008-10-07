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

package org.apache.jetspeed.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Utility bean which can be used to initialize a static class member with a Spring managed (singleton) bean on context initialization and removing it
 * automatically again when the context is destroyed.
 * 
 * The target class needs a public static (void) method with one single object argument. This bean needs to be configured with the target class name,
 * the target static method name, the class name of the target value and finally the value (e.g. a bean reference) itself.
 * 
 * The value will be "injected" into the target class when the afterPropertiesSet() method is called.
 * When the destroy() method is called, the same method will be called again with a null value.
 * @version $Id$
 *
 */
public class LifecycleAwareStaticClassInitializer implements InitializingBean, DisposableBean
{
    private String className;
    private String methodName;
    private String typeName;
    private Object value;
    private Method method;
    
    public void setClassName(String className)
    {
        this.className = className;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }
    
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
    
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        Class clazz = Class.forName(className);
        Class type = Class.forName(typeName);
        Method method = clazz.getMethod(methodName, type);
        method.invoke(null, value);
        this.method = method;
    }
    
    public void destroy() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        if (method != null)
        {
            method.invoke(null, (Object)null);
        }
    }
}
