/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.portalsite.view;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * This class is the base class for all site content
 * proxy implementations.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class SiteViewProxy
{
    /**
     * view - site view this proxy is part of
     */
    private SiteView view;

    /**
     * locatorName - name of profile locator name associated
     *               with the derived delegate of this proxy
     *               in the site view
     */
    private String locatorName;

    /**
     * SiteViewProxy - constructor
     *
     * @param view site view owner of this proxy
     * @param locatorName profile locator name associated with
     *                    the derived delegate of this proxy in
     *                    the site view
     */
    protected SiteViewProxy(SiteView view, String locatorName)
    {
        this.view = view;
        this.locatorName = locatorName;
    }

    /**
     * getView - return site view for this proxy
     *
     * @return site view
     */
    public SiteView getView()
    {
        return view;
    }

    /**
     * getLocatorName - return profile locator name associated
     *                  with the derived delegate of this proxy in
     *                  the site view
     *
     * @return profile locator name
     */
    public String getLocatorName()
    {
        return locatorName;
    }

    /**
     * reflectMethod - trap method reflection exceptions utility function
     *
     * @param methodClass class or interface
     * @param methodName method name
     * @param methodArgs array of type, class, or interface parameter types
     */
    protected static Method reflectMethod(Class methodClass, String methodName, Class [] methodArgs)
    {
        // trap reflection exceptions
        try
        {
            return methodClass.getMethod(methodName, methodArgs);
        }
        catch (NoSuchMethodException nsme)
        {
            RuntimeException rte = new RuntimeException("SiteViewProxy.reflectMethod(): unexpected reflection exception for: " + methodClass.getName() + "." + methodName);
            rte.initCause(nsme);
            throw rte;
        }
    }

    /**
     * getSiteViewProxy - utility method to access SiteViewProxy handler
     *                    from a proxy instance
     *
     * @param proxy proxy instance
     * @return site view invocation handler instance
     */
    public static SiteViewProxy getSiteViewProxy(Object proxy)
    {
        if ((proxy != null) && Proxy.isProxyClass(proxy.getClass()))
        {
            Object proxyHandler = Proxy.getInvocationHandler(proxy);
            if (proxyHandler instanceof SiteViewProxy)
            {
                return (SiteViewProxy)proxyHandler;
            }
        }
        return null;
    }
}
