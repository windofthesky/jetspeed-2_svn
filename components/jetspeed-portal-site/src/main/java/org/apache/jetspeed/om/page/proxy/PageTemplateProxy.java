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
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;

/**
 * This class proxies PSML PageTemplate instances to create a logical
 * view of site content using the Dynamic Proxy pattern.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class PageTemplateProxy extends NodeProxy implements InvocationHandler
{
    /**
     * *_METHOD - PageTemplate method constants
     */
    protected static final Method GET_MENU_DEFINITIONS_METHOD = reflectMethod(PageTemplate.class, "getMenuDefinitions", null);

    /**
     * pageTemplateReference - proxy delegate page template instance reference
     */
    private PageTemplateWeakReference pageTemplateReference;

    /**
     * newInstance - creates a new proxy instance that implements the Page interface
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param pageTemplate proxy delegate
     */
    public static PageTemplate newInstance(SearchPathsSiteView view, String locatorName, Folder parentFolder, PageTemplate pageTemplate)
    {
        return (PageTemplate)Proxy.newProxyInstance(pageTemplate.getClass().getClassLoader(), new Class[]{PageTemplate.class}, new PageTemplateProxy(view, locatorName, parentFolder, pageTemplate));
    }

    /**
     * PageTemplateProxy - private constructor used by newInstance()
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param pageTemplate proxy delegate
     */
    private PageTemplateProxy(SearchPathsSiteView view, String locatorName, Folder parentFolder, PageTemplate pageTemplate)
    {
        super(view, locatorName, parentFolder, pageTemplate.getName(), false);
        this.pageTemplateReference = new PageTemplateWeakReference(view.getPageManager(), pageTemplate);
    }
    
    /**
     * invoke - method invocation dispatch for this proxy, (defaults to
     *          invocation of delegate unless method is implemented in this
     *          proxy handler or should be hidden/stubbed)
     *
     * @param proxy instance invoked against
     * @param method PageTemplate interface method invoked
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
            throw new RuntimeException("PageTemplate instance is immutable from proxy.");
        }

        try
        {
            // attempt to invoke method on delegate PageTemplate instance
            return m.invoke(pageTemplateReference.getPageTemplate(), args);
        }
        catch (InvocationTargetException ite)
        {
            throw ite.getTargetException();
        }
    }

    /**
     * getPageTemplate - get proxy delegate page template instance
     *
     * @return delegate page template
     */
    public PageTemplate getPageTemplate()
    {
        return pageTemplateReference.getPageTemplate();
    }

    /**
     * aggregateMenuDefinitionLocators - aggregate all menu definition locators
     *                                   in site view for this folder or page
     */
    protected void aggregateMenuDefinitionLocators()
    {
        // merge only page template menu definition locators by name
        FolderProxy parentFolderProxy = FolderProxy.getFolderProxy(getParent());
        PageTemplate pageTemplate = pageTemplateReference.getPageTemplate();
        mergeMenuDefinitionLocators(pageTemplate.getMenuDefinitions(), pageTemplate, parentFolderProxy.getPath(), false);
    }
}
