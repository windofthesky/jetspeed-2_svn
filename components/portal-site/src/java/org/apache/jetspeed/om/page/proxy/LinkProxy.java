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
package org.apache.jetspeed.om.page.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.view.SiteView;

/**
 * This class proxies PSML Link instances to create a logical view
 * of site content using the Dynamic Proxy pattern.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class LinkProxy extends NodeProxy implements InvocationHandler
{
    /**
     * link - proxy delegate link instance
     */
    private Link link;

    /**
     * newInstance - creates a new proxy instance that implements the Link interface
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param link proxy delegate
     */
    public static Link newInstance(SiteView view, String locatorName, Folder parentFolder, Link link)
    {
        return (Link)Proxy.newProxyInstance(link.getClass().getClassLoader(), new Class[]{Link.class}, new LinkProxy(view, locatorName, parentFolder, link));
    }

    /**
     * LinkProxy - private constructor used by newInstance()
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param link proxy delegate
     */
    private LinkProxy(SiteView view, String locatorName, Folder parentFolder, Link link)
    {
        super(view, locatorName, parentFolder, link.getName());
        this.link = link;
    }
    
    /**
     * invoke - method invocation dispatch for this proxy, (defaults to
     *          invocation of delegate unless method is implemented in this
     *          proxy handler or should be hidden/stubbed)
     *
     * @param proxy instance invoked against
     * @param method Link interface method invoked
     * @param args method arguments
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method m, Object [] args) throws Throwable
    {
        // proxy implementation method dispatch
        if (m.equals(GET_PARENT_METHOD))
        {
            return getParent();
        }
        else if (m.equals(GET_PATH_METHOD))
        {
            return getPath();
        }
        else if (m.equals(EQUALS_METHOD))
        {
            return new Boolean(equals(args[0]));
        }
        else if (m.equals(HASH_CODE_METHOD))
        {
            return new Integer(hashCode());
        }
        else if (m.equals(TO_STRING_METHOD))
        {
            return toString();
        }
    
        // proxy suppression of not implemented or mutable methods
        if (m.getName().startsWith("set"))
        {
            throw new RuntimeException("Link instance is immutable from proxy.");
        }

        // attempt to invoke method on delegate Link instance
        return m.invoke(link, args);
    }

    /**
     * getLink - get proxy delegate link instance
     *
     * @return delegate link
     */
    public Link getLink()
    {
        return link;
    }
}
