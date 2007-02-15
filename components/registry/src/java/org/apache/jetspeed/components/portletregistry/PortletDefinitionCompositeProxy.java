/*
 * Copyright 2000-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the  "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.apache.jetspeed.components.portletregistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

public class PortletDefinitionCompositeProxy implements InvocationHandler
{
    private PortletDefinitionComposite def = null;
    private static PortletRegistry registry;
    private String name;
    
    public PortletDefinitionCompositeProxy(PortletDefinitionComposite def)
    {
        this.def = def;
        this.name = def.getUniqueName();
    }

    public static void setRegistry(PortletRegistry r)
    {
        registry = r;
    }
    
    public static PortletDefinitionComposite createProxy(
            PortletDefinitionComposite def)
    {
        Class[] proxyInterfaces = new Class[]
        { PortletDefinitionComposite.class};
        PortletDefinitionComposite proxy = (PortletDefinitionComposite) Proxy
                .newProxyInstance(PortletDefinitionComposite.class
                        .getClassLoader(), proxyInterfaces,
                        new PortletDefinitionCompositeProxy(def));
        return proxy;
    }

    protected void invalidate()
    {
        this.def = null;
    }
    
    protected void setRealDefinition(PortletDefinitionComposite d)
    {
        this.def = d;
    }
    
    protected PortletDefinitionComposite getRealApplication()
    {
        return def;
    }
    
    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable
    {
        try 
        {
            if (def == null)
            {
                def = registry.getPortletDefinitionByUniqueName(name);
            }
            return m.invoke(def, args);
        } 
        catch (InvocationTargetException e) 
        {
            throw e.getTargetException();
        }
    }

}