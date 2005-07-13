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
package org.apache.jetspeed.page.document.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.portalsite.view.SiteView;
import org.apache.jetspeed.portalsite.view.SiteViewMenuDefinitionLocator;
import org.apache.jetspeed.portalsite.view.SiteViewProxy;

/**
 * This class proxies Node instances to create a logical
 * view of site content.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class NodeProxy extends SiteViewProxy
{
    /**
     * *_METHOD - Node method constants
     */
    protected static final Method EQUALS_METHOD = reflectMethod(Object.class, "equals", new Class[]{Object.class});
    protected static final Method GET_PARENT_METHOD = reflectMethod(Node.class, "getParent", null);
    protected static final Method GET_PATH_METHOD = reflectMethod(Node.class, "getPath", null);
    protected static final Method GET_URL_METHOD = reflectMethod(Node.class, "getUrl", null);
    protected static final Method HASH_CODE_METHOD = reflectMethod(Object.class, "hashCode", null);
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
     * menuDefinitionLocators - menu definitions aggregated by name saved in
     *                          menu definition locators
     */
    private List menuDefinitionLocators;

    /**
     * menuDefinitionLocatorsAggregated - boolean flag to indicate
     *                                    menuDefinitionLocators aggregated
     */
    private boolean menuDefinitionLocatorsAggregated;

    /**
     * menuDefinitions - menu definitions aggregated by name
     */
    private List menuDefinitions;

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
     */
    protected NodeProxy(SiteView view, String locatorName, Folder parent, String name)
    {
        super(view, locatorName);
        this.parent = parent;
        if ((parent != null) && (name != null))
        {
            NodeProxy parentProxy = getNodeProxy(parent);
            String parentPath = parentProxy.getPath();
            if (parentPath.endsWith(Folder.PATH_SEPARATOR))
            {
                this.path = parentPath + name;
            }
            else
            {
                this.path = parentPath + Folder.PATH_SEPARATOR + name;
            }
        }
        else
        {
            this.path = Folder.PATH_SEPARATOR;
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
     * getUrl - proxy implementation of Node.getUrl()
     * 
     * @return pages relative url used to navigate to folder
     */
    public String getUrl()
    {
        return path;
    }

    /**
     * getMenuDefinitions - proxy implementation of Folder.getMenuDefinitions()
     *                      and Page.getMenuDefinitions()
     *
     * @return definition list
     */
    public List getMenuDefinitions()
    {
        // get menu definitions aggregated by name from
        // aggregated menu definition locators
        if (! menuDefinitionsAggregated)
        {
            List locators = getMenuDefinitionLocators();
            if (locators != null)
            {
                menuDefinitions = new ArrayList(locators.size());
                Iterator locatorsIter = locators.iterator();
                while (locatorsIter.hasNext())
                {
                    menuDefinitions.add(((SiteViewMenuDefinitionLocator)locatorsIter.next()).getMenuDefinition());
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
    public List getMenuDefinitionLocators()
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
     * getMenuDefinitionLocator - get menu definition locator by name
     *
     * @param name menu definition name
     * @return menu definition locator
     */
    public SiteViewMenuDefinitionLocator getMenuDefinitionLocator(String name)
    {
        // get menu definition locators and find by name
        List locators = getMenuDefinitionLocators();
        if (locators != null)
        {
            return findMenuDefinitionLocator(name);
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
     * mergeMenuDefinitionLocators - utilty to merge menu definition locator lists
     *                               to be used by derived implementations to aggregate
     *                               menu definition locators
     *
     * @param definitions list of menu definitions to merge
     * @param node page or folder node that defines menu definitions
     */
    protected void mergeMenuDefinitionLocators(List definitions, Node node)
    {
        // merge definitions into aggregated menu definition
        // locators if defined
        if (definitions != null)
        {
            Iterator definitionsIter = definitions.iterator();
            while (definitionsIter.hasNext())
            {
                // aggregate menu definition by valid name
                MenuDefinition definition = (MenuDefinition)definitionsIter.next();
                String definitionName = definition.getName();
                if (definitionName != null)
                {
                    // add unique menu definition to end of
                    // ordered menu definition locators list
                    if (!menuDefinitionLocatorsContains(definitionName))
                    {
                        if (menuDefinitionLocators == null)
                        {
                            menuDefinitionLocators = new ArrayList(definitions.size() * 2);
                        }
                        menuDefinitionLocators.add(new SiteViewMenuDefinitionLocator(definition, node));
                    }
                }
            }
        }
    }

    /**
     * mergeMenuDefinitionLocators - utilty to merge menu definition locator lists
     *                               to be used by derived implementations to aggregate
     *                               menu definition locators
     *
     * @param locators list of menu definition locators to merge
     */
    protected void mergeMenuDefinitionLocators(List locators)
    {
        // merge locators into aggregated menu definition
        // locators if defined
        if (locators != null)
        {
            Iterator locatorsIter = locators.iterator();
            while (locatorsIter.hasNext())
            {
                // aggregate menu definition by valid name
                SiteViewMenuDefinitionLocator locator = (SiteViewMenuDefinitionLocator)locatorsIter.next();
                String definitionName = locator.getName();

                // add unique menu definition to end of
                // ordered menu definition locators list
                if (!menuDefinitionLocatorsContains(definitionName))
                {
                    if (menuDefinitionLocators == null)
                    {
                        menuDefinitionLocators = new ArrayList(locators.size() * 2);
                    }
                    menuDefinitionLocators.add(locator);
                }
            }
        }
    }

    /**
     * menuDefinitionLocatorsContains - contains test for menu definition locators by name
     *
     * @param name menu definition name
     * @return contains name result
     */
    private boolean menuDefinitionLocatorsContains(String name)
    {
        // test for matching name in menu definition locators
        return (findMenuDefinitionLocator(name) != null);
    }

    /**
     * findMenuDefinitionLocator - find menu definition locator by name
     *
     * @param name menu definition name
     * @return menu definition locator
     */
    private SiteViewMenuDefinitionLocator findMenuDefinitionLocator(String name)
    {
        // find matching menu definition locator by name
        if ((menuDefinitionLocators != null) && (name != null))
        {
            Iterator locatorsIter = menuDefinitionLocators.iterator();
            while (locatorsIter.hasNext())
            {
                SiteViewMenuDefinitionLocator locator = (SiteViewMenuDefinitionLocator)locatorsIter.next();
                if (name.equals(locator.getName()))
                {
                    return locator;
                }
            }
        }
        return null;
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
