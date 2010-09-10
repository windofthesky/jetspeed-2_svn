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
package org.apache.jetspeed.om.page.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.proxy.FolderProxy;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;

/**
 * This class proxies PSML DynamicPage instances to create a logical
 * view of site content using the Dynamic Proxy pattern.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class DynamicPageProxy extends NodeProxy implements InvocationHandler
{
    /**
     * *_METHOD - DynamicPage method constants
     */
    protected static final Method GET_MENU_DEFINITIONS_METHOD = reflectMethod(DynamicPage.class, "getMenuDefinitions", null);

    /**
     * dynamicPageReference - proxy delegate dynamic page instance reference
     */
    private DynamicPageWeakReference dynamicPageReference;

    /**
     * newInstance - creates a new proxy instance that implements the DynamicPage interface
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param dynamicPage proxy delegate
     */
    public static DynamicPage newInstance(SearchPathsSiteView view, String locatorName, Folder parentFolder, DynamicPage dynamicPage)
    {
        return (DynamicPage)Proxy.newProxyInstance(dynamicPage.getClass().getClassLoader(), new Class[]{DynamicPage.class}, new DynamicPageProxy(view, locatorName, parentFolder, dynamicPage));
    }

    /**
     * DynamicPageProxy - private constructor used by newInstance()
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param dynamicPage proxy delegate
     */
    private DynamicPageProxy(SearchPathsSiteView view, String locatorName, Folder parentFolder, DynamicPage dynamicPage)
    {
        super(view, locatorName, parentFolder, dynamicPage.getName(), dynamicPage.isHidden());
        this.dynamicPageReference = new DynamicPageWeakReference(view.getPageManager(), dynamicPage);
    }
    
    /**
     * invoke - method invocation dispatch for this proxy, (defaults to
     *          invocation of delegate unless method is implemented in this
     *          proxy handler or should be hidden/stubbed)
     *
     * @param proxy instance invoked against
     * @param method DynamicPage interface method invoked
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
        else if (m.equals(IS_HIDDEN_METHOD))
        {
            return new Boolean(isHidden());
        }
        else if (m.equals(TO_STRING_METHOD))
        {
            return toString();
        }
    
        // proxy suppression of not implemented or mutable methods
        if (m.getName().startsWith("set"))
        {
            throw new RuntimeException("DynamicPage instance is immutable from proxy.");
        }

        try
        {
            // attempt to invoke method on delegate DynamicPage instance
            return m.invoke(dynamicPageReference.getDynamicPage(), args);
        }
        catch (InvocationTargetException ite)
        {
            throw ite.getTargetException();
        }
    }

    /**
     * getDynamicPage - get proxy delegate dynamic page instance
     *
     * @return delegate dynamic page
     */
    public DynamicPage getDynamicPage()
    {
        return dynamicPageReference.getDynamicPage();
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
        FolderProxy parentFolderProxy = FolderProxy.getFolderProxy(getParent());
        DynamicPage dynamicPage = dynamicPageReference.getDynamicPage();
        mergeMenuDefinitionLocators(dynamicPage.getMenuDefinitions(), dynamicPage, parentFolderProxy.getPath(), true);
        mergeMenuDefinitionLocators(parentFolderProxy.getMenuDefinitionLocators());
    }
}
