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
package org.apache.jetspeed.portalsite.impl;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.portalsite.MenuOption;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.view.AbstractSiteView;

/**
 * This class implements the portal-site menu option
 * elements constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuOptionImpl extends MenuElementImpl implements MenuOption, Cloneable
{
    /**
     * definition - menu option definition
     */
    private MenuOptionsDefinition definition;

    /**
     * MenuOptionImpl - constructor
     *
     * @param view site view used to construct menu option
     * @param parent containing menu implementation
     * @param node menu option node view
     * @param definition menu option definition
     */
    public MenuOptionImpl(AbstractSiteView view, MenuImpl parent, Node node, MenuOptionsDefinition definition)
    {
        super(view, parent, node);
        this.definition = definition;
    }

    /**
     * getElementType - get type of menu element
     *
     * @return OPTION_ELEMENT_TYPE
     */
    public String getElementType()
    {
        return OPTION_ELEMENT_TYPE;
    }

    /**
     * getType - get type of menu option
     *
     * @return FOLDER_OPTION_TYPE, PAGE_OPTION_TYPE, or
     *         LINK_OPTION_TYPE
     */
    public String getType()
    {
        // return type of menu option node view
        Node node = getNode();
        if (node instanceof Page)
        {
            return PAGE_OPTION_TYPE;
        }
        else if (node instanceof Link)
        {
            return LINK_OPTION_TYPE;
        }
        else if (node instanceof Folder)
        {
            return FOLDER_OPTION_TYPE;
        }
        return null;
    }

    /**
     * getSkin - get skin name for menu element
     *
     * @return skin name
     */
    public String getSkin()
    {
        // get skin from definition, from menu option
        // node view, or inherit from parent menu
        String skin = definition.getSkin();
        if (skin == null)
        {
            Node node = getNode();
            if (node instanceof Page)
            {
                skin = ((Page)node).getSkin();
            }
            else if (node instanceof Link)
            {
                skin = ((Link)node).getSkin();
            }
            else if (node instanceof Folder)
            {
                skin = ((Folder)node).getSkin();
            }
        }
        if (skin == null)
        {
            skin = super.getSkin();
        }
        return skin;
    }

    /**
     * getUrl - get url of menu option
     *
     * @return folder, page, or link url
     */
    public String getUrl()
    {
        return getNode().getUrl();
    }

    /**
     * getTarget - get target for url of menu option
     *
     * @return url target
     */
    public String getTarget()
    {
        // only link nodes support target
        Node node = getNode();
        if (node instanceof Link)
        {
            return ((Link)node).getTarget();
        }
        return null;
    }

    /**
     * getDefaultPage - get default page for a folder (if folder) of menu option
     *
     * @return url target
     */
    public String getDefaultPage()
    {
        // only link nodes support target
        Node node = getNode();
        if (node instanceof Folder)
        {
            return ((Folder)node).getDefaultPage();
        }
        return null;
    }
    
    /**
     * isHidden - get hidden state of menu option
     *
     * @return hidden state
     */
    public boolean isHidden()
    {
        return getNode().isHidden();
    }

    /**
     * isSelected - return true if menu option is selected by
     *              the specified request context
     *
     * @param context request context
     * @return selected state
     */
    public boolean isSelected(PortalSiteRequestContext context)
    {
        // compare the site view url of the page or
        // folder menu option view with the url of
        // the context request profiled page view
        if (context != null)
        {
            // get request page
            BaseConcretePageElement requestPage = null;
            try
            {
                // menus only available for concrete page requests
                if (context.isConcretePage())
                {
                    requestPage = (BaseConcretePageElement)context.getPageOrTemplate();
                }
            }
            catch (NodeNotFoundException nnfe)
            {
            }
            catch (SecurityException se)
            {
            }
            if (requestPage != null)
            {
                // get selected status based or request page url
                Node node = getNode();
                if (node instanceof Page)
                {
                    // page urls must match the request page
                    // urls to be considered selected
                    return requestPage.getUrl().equals(node.getUrl());
                }
                else if (node instanceof Folder)
                {
                    // folder urls must be a prefix of the
                    // request page urls to be considered
                    // selected
                    return requestPage.getUrl().startsWith(node.getUrl());
                }
            }
        }
        return false;
    }
}
