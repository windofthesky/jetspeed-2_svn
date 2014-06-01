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

/**
 * ComponentManagement
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ComponentManagement.java 225607 2005-07-27 20:25:36Z weaver $
 */
public interface ComponentManagement
{
    /**
     * Lookup a Jetspeed Component in the IOC container, returning an automatically casted instance of
     * the component service
     *
     * @param componentName the name of the component (bean) to lookup
     * @param <T> the return type of the interface of the component
     * @return the currently configured component service for the given name or null if not found
     * @since 2.3.0
     */
    <T> T lookupComponent(String componentName);

    /**
     * Lookup a Jetspeed Component in the IOC container, returning an automatically casted instance of
     * the component service
     *
     * @param componentClass the class of the component (bean) singleton to lookup
     * @param <T> the return type of the interface of the component
     * @return the currently configured component service for the given name or null if not found
     * @since 2.3.0
     */
    <T> T lookupComponent(Class componentClass);

    /**
     * Determine if a component exists in the Jetspeed IoC container
     *
     * @param componentName the name of the component to lookup
     * @return true if the component exists, otherwise false
     */
    boolean containsComponent(Object componentName);

    /**
     * Lookup a Jetspeed Component in the IOC container, returning an un-casted instance of
     * the component service. Deprecated. Use {@link ComponentManager#lookupComponent(String)}
     *
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.Object)
     * @param componentName can be either a String or a #{@link @java.lang.Class} If its a Class,
     *                      the component name must match the toString representation of that class
     * @deprecated in 2.3.0
     * @see {@link ComponentManager#lookupComponent(String)} or
     *      {@link ComponentManager#lookupComponent(Class)}
     * @return the component instance of null if not found
     */
    Object getComponent(Object componentName);
    
    boolean containsComponent(Object containerName, Object componentName);

    /**
     * Lookup a Jetspeed Component in the given IOC container, returning an un-casted instance of
     * the component service. Deprecated. Use {@link ComponentManager#lookupComponent(String)}
     *
     * @see org.apache.jetspeed.components.ComponentManagement#getComponent(java.lang.Object)
     * @param containerName the name of the IoC container to lookup the component in
     * @param componentName can be either a String or a #{@link @java.lang.Class} If its a Class,
     *                      the component name must match the toString representation of that class
     * @deprecated in 2.3.0
     * @see {@link ComponentManager#lookupComponent(String)} or
     *      {@link ComponentManager#lookupComponent(Class)}
     * @return the component instance of null if not found
     */
    Object getComponent(Object containerName, Object componentName);

    /**
     * Add a component (bean) to the default Jetspeed IoC container
     *
     * @param name the name of the component service
     * @param bean the actual component to be added
     */
    void addComponent(String name, Object bean);

}
