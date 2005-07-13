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
import org.apache.jetspeed.om.folder.proxy.FolderProxy;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.view.SiteView;

/**
 * This class proxies PSML Page instances to create a logical view
 * of site content using the Dynamic Proxy pattern.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageProxy extends NodeProxy implements InvocationHandler
{
    /**
     * *_METHOD - Page method constants
     */
    protected static final Method GET_MENU_DEFINITIONS_METHOD = reflectMethod(Page.class, "getMenuDefinitions", null);

    /**
     * page - proxy delegate page instance
     */
    private Page page;

    /**
     * newInstance - creates a new proxy instance that implements the Page interface
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param page proxy delegate
     */
    public static Page newInstance(SiteView view, String locatorName, Folder parentFolder, Page page)
    {
        return (Page)Proxy.newProxyInstance(page.getClass().getClassLoader(), new Class[]{Page.class}, new PageProxy(view, locatorName, parentFolder, page));
    }

    /**
     * PageProxy - private constructor used by newInstance()
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param page proxy delegate
     */
    private PageProxy(SiteView view, String locatorName, Folder parentFolder, Page page)
    {
        super(view, locatorName, parentFolder, page.getName());
        this.page = page;
    }
    
    /**
     * invoke - method invocation dispatch for this proxy, (defaults to
     *          invocation of delegate unless method is implemented in this
     *          proxy handler or should be hidden/stubbed)
     *
     * @param proxy instance invoked against
     * @param method Page interface method invoked
     * @param args method arguments
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method m, Object [] args) throws Throwable
    {
        // proxy implementation method dispatch
        if (m.equals(GET_MENU_DEFINITIONS_METHOD))
        {
            return getMenuDefinitions();
        }
        else if (m.equals(GET_PARENT_METHOD))
        {
            return getParent();
        }
        else if (m.equals(GET_PATH_METHOD))
        {
            return getPath();
        }
        else if (m.equals(GET_URL_METHOD))
        {
            return getUrl();
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
            throw new RuntimeException("Page instance is immutable from proxy.");
        }

        // attempt to invoke method on delegate Page instance
        return m.invoke(page, args);
    }

    /**
     * getPage - get proxy delegate page instance
     *
     * @return delegate page
     */
    public Page getPage()
    {
        return page;
    }

    /**
     * aggregateMenuDefinitionLocators - aggregate all menu definition locators
     *                                   in site view for this folder or page
     */
    protected void aggregateMenuDefinitionLocators()
    {
        // merge page and parent folder menu definition locators
        // by name, (most specific page definitions are merged first
        // since they override any folder definitions); note parent
        // folder menu definitions include standard menu definition
        // locator defaults
        mergeMenuDefinitionLocators(page.getMenuDefinitions(), page);
        FolderProxy parentFolderProxy = (FolderProxy)Proxy.getInvocationHandler(getParent());
        mergeMenuDefinitionLocators(parentFolderProxy.getMenuDefinitionLocators());
    }
}
