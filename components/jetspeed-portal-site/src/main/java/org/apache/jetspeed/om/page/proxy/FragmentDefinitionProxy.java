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
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;

/**
 * This class proxies PSML FragmentDefinition instances to create
 * a logical view of site content using the Dynamic Proxy pattern.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class FragmentDefinitionProxy extends NodeProxy implements InvocationHandler
{
    /**
     * fragmentDefinitionReference - proxy delegate fragment definition instance reference
     */
    private FragmentDefinitionWeakReference fragmentDefinitionReference;

    /**
     * newInstance - creates a new proxy instance that implements the FragmentDefinition interface
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param fragmentDefinition proxy delegate
     */
    public static FragmentDefinition newInstance(SearchPathsSiteView view, String locatorName, Folder parentFolder, FragmentDefinition fragmentDefinition)
    {
        return (FragmentDefinition)Proxy.newProxyInstance(fragmentDefinition.getClass().getClassLoader(), new Class[]{FragmentDefinition.class}, new FragmentDefinitionProxy(view, locatorName, parentFolder, fragmentDefinition));
    }

    /**
     * FragmentDefinitionProxy - private constructor used by newInstance()
     *
     * @param view site view owner of this proxy
     * @param locatorName name of profile locator associated
     *                    with the proxy delegate
     * @param parentFolder view parent proxy folder
     * @param fragmentDefinition proxy delegate
     */
    private FragmentDefinitionProxy(SearchPathsSiteView view, String locatorName, Folder parentFolder, FragmentDefinition fragmentDefinition)
    {
        super(view, locatorName, parentFolder, fragmentDefinition.getName(), false);
        this.fragmentDefinitionReference = new FragmentDefinitionWeakReference(view.getPageManager(), fragmentDefinition);
    }
    
    /**
     * invoke - method invocation dispatch for this proxy, (defaults to
     *          invocation of delegate unless method is implemented in this
     *          proxy handler or should be hidden/stubbed)
     *
     * @param proxy instance invoked against
     * @param method FragmentDefinition interface method invoked
     * @param args method arguments
     * @throws Throwable
     */
    public Object invoke(Object proxy, Method m, Object [] args) throws Throwable
    {
        if (m.equals(GET_PARENT_METHOD))
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
            throw new RuntimeException("FragmentDefinition instance is immutable from proxy.");
        }

        try
        {
            // attempt to invoke method on delegate FragmentDefinition instance
            return m.invoke(fragmentDefinitionReference.getFragmentDefinition(), args);
        }
        catch (InvocationTargetException ite)
        {
            throw ite.getTargetException();
        }
    }

    /**
     * getFragmentDefinition - get proxy delegate fragment definition instance
     *
     * @return delegate fragment definition
     */
    public FragmentDefinition getFragmentDefinition()
    {
        return fragmentDefinitionReference.getFragmentDefinition();
    }
}
