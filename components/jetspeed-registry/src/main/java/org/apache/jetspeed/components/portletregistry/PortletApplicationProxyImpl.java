/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.om.portlet.PortletApplication;

public class PortletApplicationProxyImpl implements InvocationHandler, PortletApplicationProxy
{
    private PortletApplication app = null;
    private static PortletRegistry registry;
    private String name;
    
    public PortletApplicationProxyImpl(PortletApplication app)
    {
        this.app = app;
        this.name = app.getName();
    }

    public static void setRegistry(PortletRegistry r)
    {
        registry = r;
    }
    
    public static PortletApplication createProxy(
            PortletApplication app)
    {
        Class[] proxyInterfaces = new Class[]
        { PortletApplication.class, PortletApplicationProxy.class};
        PortletApplication proxy = (PortletApplication) Proxy
                .newProxyInstance(PortletApplication.class
                        .getClassLoader(), proxyInterfaces,
                        new PortletApplicationProxyImpl(app));
        return proxy;
    }

    protected void invalidate()
    {
        this.app = null;
    }
    
    public void setRealApplication(PortletApplication app)
    {
        this.app = app;
    }
    
    public PortletApplication getRealApplication()
    {
        return app;
    }
    
    public Object invoke(Object proxy, Method m, Object[] args)
            throws Throwable
    {
        try 
        {
            if (m.getName().equals("getRealApplication"))
            {
                return getRealApplication();
            }
            else if (m.getName().equals("setRealApplication"))
            {
                setRealApplication((PortletApplication)args[0]);
                return null;
            }
            else
            {
                if (app == null)
                {
                    app = registry.getPortletApplication(name, true);
                }
                return m.invoke(app, args);
            }
        } 
        catch (InvocationTargetException e) 
        {
            throw e.getTargetException();
        }
    }

}
