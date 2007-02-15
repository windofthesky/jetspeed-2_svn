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

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;

public class MutablePortletApplicationProxy implements InvocationHandler, PortletApplicationProxy
{
    public boolean equals(Object obj)
    {
        return super.equals(obj);
        //        if (obj instanceof MutablePortletApplicationProxy)
//        {
//            MutablePortletApplicationProxy other = (MutablePortletApplicationProxy)obj;
//            if (this.getRealApplication().g)
//        }
    }

    private MutablePortletApplication app = null;
    private static PortletRegistry registry;
    private String name;
    
    public MutablePortletApplicationProxy(MutablePortletApplication app)
    {
        this.app = app;
        this.name = app.getName();
    }

    public static void setRegistry(PortletRegistry r)
    {
        registry = r;
    }
    
    public static MutablePortletApplication createProxy(
            MutablePortletApplication app)
    {
        Class[] proxyInterfaces = new Class[]
        { MutablePortletApplication.class, PortletApplicationProxy.class};
        MutablePortletApplication proxy = (MutablePortletApplication) Proxy
                .newProxyInstance(MutablePortletApplication.class
                        .getClassLoader(), proxyInterfaces,
                        new MutablePortletApplicationProxy(app));
        return proxy;
    }

    protected void invalidate()
    {
        this.app = null;
    }
    
    public void setRealApplication(MutablePortletApplication app)
    {
        this.app = app;
    }
    
    public MutablePortletApplication getRealApplication()
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
                setRealApplication((MutablePortletApplication)args[0]);
                return null;
            }
            else
            {
                if (app == null)
                {
                    app = registry.getPortletApplication(name);
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
