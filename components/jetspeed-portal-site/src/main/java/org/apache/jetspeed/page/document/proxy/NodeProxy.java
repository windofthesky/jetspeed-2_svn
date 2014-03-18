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
package org.apache.jetspeed.page.document.proxy;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteViewProxy;
import org.apache.jetspeed.portalsite.view.SiteViewMenuDefinitionLocator;
import org.apache.jetspeed.portalsite.view.SiteViewUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class proxies Node instances to create a logical
 * view of site content.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class NodeProxy extends SearchPathsSiteViewProxy
{
    /**
     * URL_ENCODING - the name of a character encoding to be used in encoding path component name.
     */
    private static final String URL_ENCODING = "ISO-8859-1";
    
    /**
     * *_METHOD - Node method constants
     */
    protected static final Method EQUALS_METHOD = reflectMethod(Object.class, "equals", new Class[]{Object.class});
    protected static final Method GET_PARENT_METHOD = reflectMethod(Node.class, "getParent", null);
    protected static final Method GET_PATH_METHOD = reflectMethod(Node.class, "getPath", null);
    protected static final Method GET_URL_METHOD = reflectMethod(Node.class, "getUrl", null);
    protected static final Method HASH_CODE_METHOD = reflectMethod(Object.class, "hashCode", null);
    protected static final Method IS_HIDDEN_METHOD = reflectMethod(Node.class, "isHidden", null);
    protected static final Method TO_STRING_METHOD = reflectMethod(Object.class, "toString", null);

    /**
     * parent - view parent proxy folder instance
     */
    private Folder parent;

    /**
     * path - view path
     */
    private String path;
    
    /**
     * url - view url
     */
    private String url;

    /**
     * hidden - hidden status of this or parent node
     */
    private boolean hidden;

    /**
     * menuDefinitionLocators - menu definitions aggregated by name saved in
     *                          menu definition locators
     */
    private List<SiteViewMenuDefinitionLocator> menuDefinitionLocators;

    /**
     * menuDefinitionLocatorsAggregated - boolean flag to indicate
     *                                    menuDefinitionLocators aggregated
     */
    private boolean menuDefinitionLocatorsAggregated;

    /**
     * menuDefinitions - menu definitions aggregated by name
     */
    private List<MenuDefinition> menuDefinitions;

    /**
     * menuDefinitionsAggregated - boolean flag to indicate menuDefinitions
     *                             aggregated from menuDefinitionLocators
     */
    private boolean menuDefinitionsAggregated;

    /**
     * NodeProxy - constructor
     *
     * @param view site view owner of this proxy
     * @param locatorName profile locator name associated with
     *                    the derived delegate of this proxy in
     *                    the site view
     * @param parent view parent proxy folder
     * @param name name of node to proxy
     * @param hidden hidden status of node to proxy
     */
    protected NodeProxy(SearchPathsSiteView view, String locatorName, Folder parent, String name, boolean hidden)
    {
        super(view, locatorName);
        this.parent = parent;
        
        if ((parent != null) && (name != null))
        {
            NodeProxy parentProxy = getNodeProxy(parent);
            String parentPath = parentProxy.getPath();
            String parentUrl = parentProxy.getUrl();
            String urlEncodedName = name;
            
            try
            {
                urlEncodedName = URLEncoder.encode(name, URL_ENCODING);
            }
            catch (UnsupportedEncodingException e)
            {
                // do nothing. just use the plain name instead.
            }

            if (parentPath.endsWith(Folder.PATH_SEPARATOR))
            {
                this.path = parentPath + name;
                this.url = parentUrl + urlEncodedName;
            }
            else
            {
                this.path = parentPath + Folder.PATH_SEPARATOR + name;
                this.url = parentUrl + Folder.PATH_SEPARATOR + urlEncodedName;
            }
            
            this.hidden = (hidden || parentProxy.isHidden());
        }
        else
        {
            this.path = Folder.PATH_SEPARATOR;
            this.url = Folder.PATH_SEPARATOR;
            this.hidden = hidden;
        }
    }
    
    /**
     * getParent - proxy implementation of Node.getParent()
     *
     * @return parent folder
     */
    public Node getParent()
    {
        return parent;
    }

    /**
     * getPath - proxy implementation of Node.getPath()
     * 
     * @return pages relative path used to identify proxy
     */
    public String getPath()
    {
        return path;
    }

    /**
     * getPath - proxy implementation of Node.isHidden()
     * 
     * @return hidden status of node or parent
     */
    public boolean isHidden()
    {
        return hidden;
    }

    /**
     * getUrl - proxy implementation of Node.getUrl()
     * 
     * @return pages relative url used to navigate to folder
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * getMenuDefinitions - proxy implementation of Folder.getMenuDefinitions()
     *                      and Page.getMenuDefinitions()
     *
     * @return definition list
     */
    public List<MenuDefinition> getMenuDefinitions()
    {
        // get menu definitions aggregated by name from
        // aggregated menu definition locators
        if (! menuDefinitionsAggregated)
        {
            List<SiteViewMenuDefinitionLocator> locators = getMenuDefinitionLocators();
            if (locators != null)
            {
                menuDefinitions = Collections.synchronizedList(new ArrayList<MenuDefinition>(locators.size()));
                for (SiteViewMenuDefinitionLocator locator : locators)
                {
                    menuDefinitions.add(locator.getMenuDefinition());
                }
            }
            menuDefinitionsAggregated = true;
        }
        return menuDefinitions;
    }

    /**
     * getMenuDefinitionLocators - get list of menu definition locators
     *                             aggregated by name for this folder or page
     *
     * @return definition locator list
     */
    public List<SiteViewMenuDefinitionLocator> getMenuDefinitionLocators()
    {
        // get menu definition locators aggregated by name
        if (! menuDefinitionLocatorsAggregated)
        {
            aggregateMenuDefinitionLocators();
            menuDefinitionLocatorsAggregated = true;
        }
        return menuDefinitionLocators;
    }

    /**
     * mergeMenuDefinitionLocators - utility to merge menu definition locator lists
     *                               to be used by derived implementations to aggregate
     *                               menu definition locators
     *
     * @param definitions list of menu definitions to merge
     * @param definitionNode menu definition node
     * @param path menu definition path
     * @param override override menu definition
     */
    public void mergeMenuDefinitionLocators(List<MenuDefinition> definitions, Node definitionNode, String path, boolean override)
    {
        // merge definitions into aggregated menu definition
        // locators if defined
        menuDefinitionLocators = SiteViewUtils.mergeMenuDefinitionLocators(definitions, definitionNode, path, override, menuDefinitionLocators);
    }

    /**
     * mergeMenuDefinitionLocators - utility to merge menu definition locator lists
     *                               to be used by derived implementations to aggregate
     *                               menu definition locators
     *
     * @param locators list of menu definition locators to merge
     */
    public void mergeMenuDefinitionLocators(List<SiteViewMenuDefinitionLocator> locators)
    {
        // merge locators into aggregated menu definition
        // locators if defined
        menuDefinitionLocators = SiteViewUtils.mergeMenuDefinitionLocators(locators, menuDefinitionLocators);
    }
    
    /**
     * getMenuDefinitionLocator - get menu definition locator by name
     *
     * @param name menu definition name
     * @return menu definition locator
     */
    public SiteViewMenuDefinitionLocator getMenuDefinitionLocator(String name)
    {
        // get menu definition locators and find by name
        List<SiteViewMenuDefinitionLocator> locators = getMenuDefinitionLocators();
        if (locators != null)
        {
            return SiteViewUtils.findMenuDefinitionLocator(locators, name);
        }
        return null;
    }

    /**
     * aggregateMenuDefinitionLocators - aggregate all menu definition locators
     *                                   in site view for this folder or page
     */
    protected void aggregateMenuDefinitionLocators()
    {
        // no menu definition locators by default
    }

    /**
     * equals - proxy implementation of Object.equals()
     * 
     * @param object test instance
     * @return equals test result
     */
    public boolean equals(Object object)
    {
        if (object != null)
        {
            // trivial compare
            if (object == this)
            {
                return true;
            }

            // compare as NodeProxy
            if (!(object instanceof NodeProxy))
            {
                object = getNodeProxy(object);
            }
            if (object instanceof NodeProxy)
            {
                return path.equals(((NodeProxy)object).path);
            }
        }
        return false;
    }

    /**
     * toString - proxy implementation of Object.toString()
     * 
     * @return string representation of proxy path
     */
    public String toString()
    {
        return path;
    }

    /**
     * hashCode - proxy implementation of Object.hashCode()
     * 
     * @return hash code based on proxy path
     */
    public int hashCode()
    {
        return path.hashCode();
    }

    /**
     * getNodeProxy - utility method to access NodeProxy handler
     *                from Node proxy instance
     *
     * @param node node proxy instance
     * @return node proxy invocation handler instance
     */
    public static NodeProxy getNodeProxy(Object node)
    {
        if ((node != null) && Proxy.isProxyClass(node.getClass()))
        {
            Object nodeProxyHandler = Proxy.getInvocationHandler(node);
            if (nodeProxyHandler instanceof NodeProxy)
            {
                return (NodeProxy)nodeProxyHandler;
            }
        }
        return null;
    }
}
