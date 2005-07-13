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
package org.apache.jetspeed.portalsite.impl;

import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.portalsite.Menu;
import org.apache.jetspeed.portalsite.MenuElement;

/**
 * This abstract class implements common features of portal-site
 * menu elements constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class MenuElementImpl implements MenuElement, Cloneable
{
    /**
     * parentMenu - parent menu implementation
     */
    private MenuImpl parent;

    /**
     * node - underlying node proxy associated with this
     *        menu element in the site view
     */
    private Node node;

    /**
     * skin - inherited skin name for menu element
     */
    private String skin;

    /**
     * skinInherited - flag indicating whether skin value
     *                 has been inherited by propagating
     *                 from parent menu
     */
    private boolean skinInherited;

    /**
     * MenuElementImpl - constructor
     *
     * @param parent containing menu implementation
     */
    protected MenuElementImpl(MenuImpl parent)
    {
        this.parent = parent;
    }

    /**
     * MenuElementImpl - node proxy constructor
     *
     * @param parent containing menu implementation
     * @param node menu element node proxy
     */
    protected MenuElementImpl(MenuImpl parent, Node node)
    {
        this(parent);
        this.node = node;
    }

    /**
     * clone - clone this instance
     *
     * @return unparented copy
     */
    public Object clone() throws CloneNotSupportedException
    {
        // clone this object
        MenuElementImpl copy = (MenuElementImpl) super.clone();

        // clear parent reference
        copy.parent = null;
        return copy;
    }

    /**
     * equals - compare menu element implementations
     *
     * @return equals result
     */
    public boolean equals(Object obj)
    {
        // compare menu implementation by type, url, and
        // name, instances with no url and no name are
        // always considered unique
        if (this.getClass().equals(obj.getClass()))
        {
            String url = getUrl();
            String name = getName();
            if ((url != null) || (name != null))
            {
                String objUrl = ((MenuElementImpl)obj).getUrl();
                String objName = ((MenuElementImpl)obj).getName();
                return ((((name == null) && (objName == null)) || ((name != null) && name.equals(objName))) &&
                        (((url != null) && url.equals(objUrl)) || ((url == null) && (objUrl == null))));
            }
        }
        return false;
    }

    /**
     * getElementType - get type of menu element
     *
     * @return MENU_ELEMENT_TYPE, OPTION_ELEMENT_TYPE, or
     *         SEPARATOR_ELEMENT_TYPE
     */
    public abstract String getElementType();

    /**
     * getParentMenu - get menu that contains menu element 
     *
     * @return parent menu
     */    
    public Menu getParentMenu()
    {
        return parent;
    }

    /**
     * setParentMenu - set menu that contains menu element 
     *
     * @param parentMenu parent menu
     */    
    protected void setParentMenu(Menu parentMenu)
    {
        parent = (MenuImpl)parentMenu;
    }

    /**
     * getName - get name of menu element used for default title
     *
     * @return menu element name
     */
    public String getName()
    {
        // no name by default
        return null;
    }

    /**
     * getUrl - get url of menu element used for comparison
     *
     * @return folder, page, or link url
     */
    public String getUrl()
    {
        // no url by default
        return null;
    }

    /**
     * getTitle - get default title for menu element
     *
     * @return title text
     */
    public String getTitle()
    {
        // return node or default title
        if (node != null)
        {
            return node.getTitle();
        }
        return getName();
    }

    /**
     * getShortTitle - get default short title for menu element
     *
     * @return short title text
     */
    public String getShortTitle()
    {
        // return node or default short title
        if (node != null)
        {
            return node.getShortTitle();
        }
        return getName();
    }

    /**
     * getTitle - get locale specific title for menu element
     *            from metadata
     *
     * @param locale preferred locale
     * @return title text
     */
    public String getTitle(Locale locale)
    {
        // return node or default title for preferred locale
        if (node != null)
        {
            return node.getTitle(locale);
        }
        return getName();
    }

    /**
     * getShortTitle - get locale specific short title for menu
     *                 element from metadata
     *
     * @param locale preferred locale
     * @return short title text
     */
    public String getShortTitle(Locale locale)
    {
        // return node or default short title for preferred locale
        if (node != null)
        {
            return node.getShortTitle(locale);
        }
        return getName();
    }

    /**
     * getMetadata - get generic metadata for menu element
     *
     * @return metadata
     */    
    public GenericMetadata getMetadata()
    {
        // return node metadata
        if (node != null)
        {
            GenericMetadata metadata = node.getMetadata();
            if ((metadata != null) && ((metadata.getFields() == null) || metadata.getFields().isEmpty()))
            {
                return metadata;
            }
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
        // no skin by default, check parent for
        // skin value and cache locally
        if (!skinInherited)
        {
            if (parent != null)
            {
                skin = parent.getSkin();
            }
            skinInherited = true;
        }
        return skin;
    }

    /**
     * getNode - get menu element node proxy in the site view
     *
     * @return node proxy
     */
    protected Node getNode()
    {
        return node;
    } 

    /**
     * setNode - set menu element node proxy in the site view
     *
     * @param node node proxy
     */
    protected void setNode(Node node)
    {
        this.node = node;
    } 
}
