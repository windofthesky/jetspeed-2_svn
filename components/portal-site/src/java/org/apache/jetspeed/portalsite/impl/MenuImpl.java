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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.portalsite.Menu;
import org.apache.jetspeed.portalsite.MenuElement;
import org.apache.jetspeed.portalsite.MenuOption;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.portalsite.menu.DefaultMenuDefinition;
import org.apache.jetspeed.portalsite.menu.DefaultMenuOptionsDefinition;
import org.apache.jetspeed.portalsite.view.SiteView;

/**
 * This class implements the portal-site menu elements
 * constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuImpl extends MenuElementImpl implements Menu, Cloneable
{
    /**
     * definition - menu definition
     */
    private MenuDefinition definition;

    /**
     * elements - ordered list of menu elements that
     *            make up this instaniated menu
     */
    private List elements;

    /**
     * elementRelative - flag that indicates whether any relative paths
     *                   dependent on the current page in context were
     *                   referenced while constructing menu elements:
     *                   requires request, not session, caching
     */
    private boolean elementRelative;

    /**
     * MenuImpl - request/session context dependent constructor
     *
     * @param parent containing menu implementation
     * @param definition menu definition
     * @param context request context
     * @param menus related menu definition names set
     */
    public MenuImpl(MenuImpl parent, MenuDefinition definition, PortalSiteRequestContextImpl context, Set menus)
    {
        super(parent);
        this.definition = definition;

        // get site view from context
        SiteView view = ((PortalSiteSessionContextImpl)context.getSessionContext()).getSiteView();
        if (view != null)
        {
            // define menu node for titles and metadata if options
            // specifies a single visible page or folder proxy
            String options = definition.getOptions();
            Node optionProxy = null;
            if ((options != null) && (options.indexOf(',') == -1))
            {
                try
                {
                    optionProxy = view.getNodeProxy(options, context.getPage(), true, true);
                }
                catch (NodeNotFoundException nnfe)
                {
                }
                catch (SecurityException se)
                {
                }
                if (optionProxy != null)
                {
                    setNode(optionProxy);
                }
            }

            // construct menu elements from menu definition
            // or nested menu definition elements; note that
            // menu elements override menu options attribute
            if ((definition.getMenuElements() == null) || definition.getMenuElements().isEmpty())
            {
                // if options optionProxy is a single folder, force
                // options to include all folder children if not to
                // be expanded with paths and depth inclusion is
                // specified
                List overrideOptionProxies = null;
                if (optionProxy != null)
                {
                    if ((optionProxy instanceof Folder) && !definition.isPaths() && (definition.getDepth() != 0))
                    {
                        // assemble folder children path using wildcard
                        String folderChildrenPath = null;
                        if (!options.endsWith(Folder.PATH_SEPARATOR))
                        {
                            folderChildrenPath = options + Folder.PATH_SEPARATOR + "*";
                        }
                        else
                        {
                            folderChildrenPath = options + "*";
                        }

                        // override menu options with visible folder contents
                        // or create empty menu if no contents exist
                        List folderChildren = null;
                        try
                        {
                            folderChildren = view.getNodeProxies(folderChildrenPath, context.getPage(), true, true);
                        }
                        catch (NodeNotFoundException nnfe)
                        {
                        }
                        catch (SecurityException se)
                        {
                        }
                        if ((folderChildren != null) && !folderChildren.isEmpty())
                        {
                            overrideOptionProxies = folderChildren;
                        }
                        else
                        {
                            return;
                        }
                    }
                    else
                    {
                        // override menu options with single folder/page/link
                        overrideOptionProxies = new ArrayList(1);
                        overrideOptionProxies.add(optionProxy);
                    }
                    
                    // set relative element flag if options path is relative
                    this.elementRelative = (this.elementRelative || !options.startsWith(Folder.PATH_SEPARATOR));
                }

                // menu defined only with menu definition options
                this.elements = constructMenuElements(context, view, options, overrideOptionProxies, definition.getDepth(), definition.isPaths(), definition.isRegexp(), definition.getProfile(), definition.getOrder());
            }
            else
            {
                // limit cyclic references to this menu if named and
                // referencable as root menu instance
                boolean menuNameReferenced = false;
                if ((definition.getName() != null) && (parent == null))
                {
                    if (menus == null)
                    {
                        menus = new HashSet(4);
                    }
                    menuNameReferenced = menus.add(definition.getName());
                }
                
                // process menu elements in chunks between separators:
                // separators are included only if menu options are
                // generated after the separator and include/exclude
                // merge/filter operations apply to options bounded
                // by separators
                MenuSeparatorImpl separator = null;
                List separatedElements = null;

                // process each defined menu element
                Iterator menuElementsIter = definition.getMenuElements().iterator();
                while (menuElementsIter.hasNext())
                {
                    Object menuElement = menuElementsIter.next();
                    if (menuElement instanceof MenuOptionsDefinition)
                    {
                        // construct menu option elements from definition using
                        // defaults from menu definition as appropriate
                        MenuOptionsDefinition optionDefinition = (MenuOptionsDefinition)menuElement;
                        String locatorName = optionDefinition.getProfile();
                        if (locatorName == null)
                        {
                            locatorName = definition.getProfile();
                        }
                        String order = optionDefinition.getOrder();
                        if (order == null)
                        {
                            order = definition.getOrder();
                        }
                        List optionsAndMenus = constructMenuElements(context, view, optionDefinition.getOptions(), null, optionDefinition.getDepth(), optionDefinition.isPaths(), optionDefinition.isRegexp(), locatorName, order);

                        // append option and menu elements to current separator
                        // elements list
                        if (optionsAndMenus != null)
                        {
                            if (separatedElements == null)
                            {
                                separatedElements = optionsAndMenus;
                            }
                            else
                            {
                                appendMenuElements(optionsAndMenus, separatedElements);
                            }
                        }
                    }
                    else if (menuElement instanceof MenuSeparatorDefinition)
                    {
                        // append current separator and separated option/menu elements
                        // to menu elements list if at least one option/menu
                        // element exists: do not include disassociated separators in menu
                        if ((separatedElements != null) && !separatedElements.isEmpty())
                        {
                            if (this.elements == null)
                            {
                                int initialSize = separatedElements.size();
                                if (separator != null)
                                {
                                    initialSize++;
                                }
                                this.elements = new ArrayList(initialSize);
                            }
                            if (separator != null)
                            {
                                this.elements.add(separator);
                            }
                            this.elements.addAll(separatedElements);
                        }

                        // construct new separator and reset separator
                        // and separator option/menu elements list
                        MenuSeparatorDefinition separatorDefinition = (MenuSeparatorDefinition)menuElement;
                        separator = new MenuSeparatorImpl(this, separatorDefinition);
                        if (separatedElements != null)
                        {
                            separatedElements.clear();
                        }
                    }
                    else if (menuElement instanceof MenuDefinition)
                    {
                        // construct nested menu element from definition
                        MenuDefinition menuDefinition = (MenuDefinition)menuElement;
                        MenuImpl nestedMenu = new MenuImpl(this, menuDefinition, context, menus);

                        // append menu element to current separated elements list
                        if (separatedElements == null)
                        {
                            separatedElements = new ArrayList(1);
                        }
                        appendMenuElement(nestedMenu, separatedElements);

                        // set relative element flag if nested menu is relative
                        this.elementRelative = (this.elementRelative || nestedMenu.isElementRelative());
                    }
                    else if (menuElement instanceof MenuIncludeDefinition)
                    {
                        // include or nest referenced menu definition
                        // assuming reference to menu is not cyclic
                        MenuIncludeDefinition includeDefinition = (MenuIncludeDefinition)menuElement;
                        if ((menus == null) || !menus.contains(includeDefinition.getName()))
                        {
                            // get named root menu from context, (menu may
                            // not exist in this context so failure to
                            // access menu is ignored)
                            MenuImpl includeMenu = null;
                            try
                            {
                                includeMenu = (MenuImpl)context.getMenu(includeDefinition.getName());
                            }
                            catch (NodeNotFoundException nnfe)
                            {
                            }
                            catch (SecurityException se)
                            {
                            }
                            if (includeMenu != null)
                            {
                                // nest menu or include elements, clone required
                                // to support reparenting to this menu
                                if (includeDefinition.isNest())
                                {
                                    // nest menu instance
                                    try
                                    {
                                        // clone menu and reparent
                                        includeMenu = (MenuImpl)includeMenu.clone();
                                        includeMenu.setParentMenu(this);

                                        // append menu element to current separated elements list
                                        if (separatedElements == null)
                                        {
                                            separatedElements = new ArrayList(1);
                                        }
                                        appendMenuElement(includeMenu, separatedElements);
                                    }
                                    catch (CloneNotSupportedException cnse)
                                    {
                                    }
                                }
                                else
                                {
                                    // include menu elements
                                    if (!includeMenu.isEmpty())
                                    {
                                        Iterator elementsIter = includeMenu.getElements().iterator();
                                        while (elementsIter.hasNext())
                                        {
                                            MenuElementImpl includeElement = (MenuElementImpl)elementsIter.next();
                                            try
                                            {
                                                // clone menu element and reparent
                                                includeElement = (MenuElementImpl)includeElement.clone();
                                                includeElement.setParentMenu(this);
                                                
                                                // insert separators or options and menus
                                                if (includeElement instanceof MenuSeparatorImpl)
                                                {
                                                    // append current separator and separated option/menu elements
                                                    if ((separatedElements != null) && !separatedElements.isEmpty())
                                                    {
                                                        if (this.elements == null)
                                                        {
                                                            int initialSize = separatedElements.size();
                                                            if (separator != null)
                                                            {
                                                                initialSize++;
                                                            }
                                                            this.elements = new ArrayList(initialSize);
                                                        }
                                                        if (separator != null)
                                                        {
                                                            this.elements.add(separator);
                                                        }
                                                        this.elements.addAll(separatedElements);
                                                    }

                                                    // reset separator and separator option/menu elements list
                                                    // using separator menu element
                                                    separator = (MenuSeparatorImpl)includeElement;
                                                    if (separatedElements != null)
                                                    {
                                                        separatedElements.clear();
                                                    }
                                                }
                                                else
                                                {
                                                    // append menu element to current separated elements list
                                                    if (separatedElements == null)
                                                    {
                                                        separatedElements = new ArrayList(includeMenu.getElements().size());
                                                    }
                                                    appendMenuElement(includeElement, separatedElements);
                                                }
                                            }
                                            catch (CloneNotSupportedException cnse)
                                            {
                                            }
                                        }
                                    }
                                }

                                // set relative element flag if included menu is relative
                                this.elementRelative = (this.elementRelative || includeMenu.isElementRelative());
                            }
                        }
                    }
                    else if (menuElement instanceof MenuExcludeDefinition)
                    {
                        // exclusion requires current separated elements
                        if ((separatedElements != null) && !separatedElements.isEmpty())
                        {
                            // exclude top level referenced menu definition
                            // options assuming reference to menu is not cyclic
                            MenuExcludeDefinition excludeDefinition = (MenuExcludeDefinition)menuElement;
                            if ((menus == null) || !menus.contains(excludeDefinition.getName()))
                            {
                                // get named root menu from context, (menu may
                                // not exist in this context so failure to
                                // access menu is ignored)
                                MenuImpl excludeMenu = null;
                                try
                                {
                                    excludeMenu = (MenuImpl)context.getMenu(excludeDefinition.getName());
                                }
                                catch (NodeNotFoundException nnfe)
                                {
                                }
                                catch (SecurityException se)
                                {
                                }
                                if (excludeMenu != null)
                                {
                                    // remove referenced menu options from current
                                    // separated elements list
                                    removeMenuElements(excludeMenu.getElements(), separatedElements);

                                    // set relative element flag if excluded menu is relative
                                    this.elementRelative = (this.elementRelative || excludeMenu.isElementRelative());
                                }
                            }
                        }
                    }
                }

                // append last separator and separated option/menu elements
                // to menu elements list if at least one option/menu
                // element exists: do not include trailing separators
                if ((separatedElements != null) && !separatedElements.isEmpty())
                {
                    if (this.elements == null)
                    {
                        // use the separated elements as the menu elements
                        // collection and insert the separator
                        this.elements = separatedElements;
                        if (separator != null)
                        {
                            this.elements.add(0, separator);
                        }
                    }
                    else
                    {
                        // copy into existing menu elements collection
                        if (separator != null)
                        {
                            this.elements.add(separator);
                        }
                        this.elements.addAll(separatedElements);
                    }
                }

                // restore referencing for this menu if limited
                if (menuNameReferenced)
                {
                    menus.remove(definition.getName());
                }
            }
        }
    }

    /**
     * MenuImpl - request/session context dependent constructor
     *
     * @param definition menu definition
     * @param context request context
     * @param menus related menu definition names set
     */
    public MenuImpl(MenuDefinition definition, PortalSiteRequestContextImpl context, Set menus)
    {
        this(null, definition, context, menus);
    }

    /**
     * appendMenuElement - append to ordered list of unique menu
     *                     option/menu elements
     * 
     * @param appendMenuElement option/menu element to append
     * @param menuElements option/menu element list
     */
    private void appendMenuElement(MenuElementImpl appendMenuElement, List menuElements)
    {
        // make sure new menu element is unique and
        // add to menu element list
        if (appendMenuElement != null)
        {
            if (!menuElements.contains(appendMenuElement))
            {
                menuElements.add(appendMenuElement);
            }
        }
    }
    
    /**
     * appendMenuElements - append to ordered list of unique menu
     *                      option/menu elements
     * 
     * @param appendMenuElements option/menu element list to append
     * @param menuElements option/menu element list
     */
    private void appendMenuElements(List appendMenuElements, List menuElements)
    {
        // make sure new menu elements are unique and
        // add to menu element list
        if (appendMenuElements != null)
        {
            Iterator elementsIter = appendMenuElements.iterator();
            while (elementsIter.hasNext())
            {
                appendMenuElement((MenuElementImpl)elementsIter.next(), menuElements);
            }
        }
    }
    
    /**
     * removeMenuElements - remove from ordered list of unique menu
     *                      option/menu elements
     * 
     * @param removeMenuElements option/menu element list to remove
     * @param menuElements option/menu element list
     */
    private void removeMenuElements(List removeMenuElements, List menuElements)
    {
        // remove equivalent menu elements from menu
        // element list
        if (removeMenuElements != null)
        {
            menuElements.removeAll(removeMenuElements);
        }
    }

    /**
     * constructMenuElements - construct ordered list of menu elements in
     *                         context/site view using specified element
     *                         selection parameters; also sets up the
     *                         elementRelative flag while constructing the
     *                         menu elements
     * 
     * @param context request context
     * @param view context site view
     * @param options option paths specification
     * @param overrideElementProxies override menu element node proxies
     * @param depth inclusion depth
     * @param paths paths elements flag
     * @param regexp regexp flag
     * @param locatorName profile locator name
     * @param order ordering patterns list
     */
    private List constructMenuElements(PortalSiteRequestContextImpl context, SiteView view, String options, List overrideElementProxies, int depth, boolean paths, boolean regexp, String locatorName, String order)
    {
        if (options != null)
        {
            // use override element proxies if specified; otherwise
            // compute proxy list using specified menu options
            List elementProxies = overrideElementProxies;
            if (elementProxies == null)
            {
                // split multiple comma separated option paths from specified options 
                String [] optionPaths = options.split(",");
                
                // use regexp processing if specified or simple
                // path evaluation to retrieve list of proxies from
                // the site view for the specified options
                for (int i = 0; (i < optionPaths.length); i++)
                {
                    String optionPath = optionPaths[i].trim();
                    if (optionPath.length() > 0)
                    {
                        // get proxies/proxy for path
                        if (regexp)
                        {
                            // get list of visible proxies for path from view and append
                            // to list if unique and pass profile locator name filter
                            List pathProxies = null;
                            try
                            {
                                pathProxies = view.getNodeProxies(optionPath, context.getPage(), true, true);
                            }
                            catch (NodeNotFoundException nnfe)
                            {
                            }
                            catch (SecurityException se)
                            {
                            }
                            if (pathProxies != null)
                            {
                                Iterator pathProxiesIter = pathProxies.iterator();
                                while (pathProxiesIter.hasNext())
                                {
                                    Node pathProxy = (Node)pathProxiesIter.next();
                                    if ((locatorName == null) || locatorName.equals(MenuOptionsDefinition.ANY_PROFILE_LOCATOR) ||
                                        locatorName.equals(view.getProfileLocatorName(pathProxy)))
                                    {
                                        if (elementProxies == null)
                                        {
                                            elementProxies = new ArrayList();
                                        }
                                        appendMenuElementProxies(pathProxy, elementProxies);
                                    }
                                }
                            }
                        }
                        else
                        {
                            // get visible proxy for path from view and append to
                            // list if unique and pass profile locator name filter
                            Node pathProxy = null;
                            try
                            {
                                pathProxy = view.getNodeProxy(optionPath, context.getPage(), true, true);
                            }
                            catch (NodeNotFoundException nnfe)
                            {
                            }
                            catch (SecurityException se)
                            {
                            }
                            if ((pathProxy != null) &&
                                ((locatorName == null) || locatorName.equals(MenuOptionsDefinition.ANY_PROFILE_LOCATOR) ||
                                 locatorName.equals(view.getProfileLocatorName(pathProxy))))
                            {
                                if (elementProxies == null)
                                {
                                    elementProxies = new ArrayList();
                                }
                                appendMenuElementProxies(pathProxy, elementProxies);
                            }
                        }

                        // set relative element flag if path is relative
                        elementRelative = (elementRelative || !optionPath.startsWith(Folder.PATH_SEPARATOR));
                    }
                }

                // return if no proxies available
                if (elementProxies == null)
                {
                    return null;
                }
            }
            
            // sort elements proxies using url and/or names if order
            // specified and more than one element proxy in list
            if ((order != null) && (elementProxies.size() > 1))
            {
                // create ordered element proxies
                List orderedElementProxies = new ArrayList(elementProxies.size());
                
                // split multiple comma separated elements orderings
                // after converted to regexp pattern
                String [] orderings = orderRegexpPattern(order).split(",");
                
                // copy ordered proxies per ordering
                for (int i=0; ((i < orderings.length) && (elementProxies.size() > 1)); i++)
                {
                    String ordering = orderings[i].trim();
                    if (ordering.length() > 0)
                    {
                        // get ordering pattern and matcher
                        Pattern pattern = Pattern.compile(ordering);
                        Matcher matcher = null;
                        
                        // use regular expression to match urls or names of
                        // element proxies; matched proxies are removed and
                        // placed in the ordered elements proxies list
                        Iterator elementProxiesIter = elementProxies.iterator();
                        while (elementProxiesIter.hasNext())
                        {
                            Node elementProxy = (Node)elementProxiesIter.next(); 
                            
                            // get url or name to test ordering match against
                            String test = null;
                            if (ordering.charAt(0) == Folder.PATH_SEPARATOR_CHAR)
                            {
                                test = elementProxy.getUrl();
                            }
                            else
                            {
                                test = elementProxy.getName();
                            }
                            
                            // construct or reset ordering matcher
                            if (matcher == null)
                            {
                                matcher = pattern.matcher(test);
                            }
                            else
                            {
                                matcher.reset(test);
                            }
                            
                            // move proxy to ordered list if matched
                            if (matcher.matches())
                            {
                                orderedElementProxies.add(elementProxy);
                                elementProxiesIter.remove();
                            }
                        }
                    }
                }
                
                // copy remaining unordered proxies
                orderedElementProxies.addAll(elementProxies);
                
                // replace element proxies with ordered list
                elementProxies = orderedElementProxies;
            }
            
            // expand paths if single page or folder element proxy
            // has been specified in elements with no depth expansion
            if (paths && (depth == 0) && (elementProxies.size() == 1) &&
                ((elementProxies.get(0) instanceof Folder) || (elementProxies.get(0) instanceof Page)))
            {
                Node parentNode = ((Node)elementProxies.get(0)).getParent();
                while (parentNode != null)
                {
                    elementProxies.add(0, parentNode);
                    parentNode = parentNode.getParent();
                }
            }
            
            // convert elements proxies into menu elements
            DefaultMenuOptionsDefinition defaultMenuOptionsDefinition = null;
            ListIterator elementProxiesIter = elementProxies.listIterator();
            while (elementProxiesIter.hasNext())
            {
                Node elementProxy = (Node)elementProxiesIter.next();
                MenuElement menuElement = null;

                // convert folders into nested menus if depth specified
                // with no paths expansion, (negative depth values are
                // interpreted as complete menu expansion)
                if ((elementProxy instanceof Folder) && ((depth < 0) || (depth > 1)) && !paths)
                {
                    // construct menu definition and associated menu
                    MenuDefinition nestedMenuDefinition = new DefaultMenuDefinition(elementProxy.getUrl(), depth - 1, locatorName);
                    menuElement = new MenuImpl(this, nestedMenuDefinition, context, null);
                }
                else
                {
                    // construct shared default menu option definition and menu option
                    if (defaultMenuOptionsDefinition == null)
                    {
                        defaultMenuOptionsDefinition = new DefaultMenuOptionsDefinition(options, depth, paths, regexp, locatorName, order);
                    }
                    menuElement = new MenuOptionImpl(this, elementProxy, defaultMenuOptionsDefinition);
                }

                // replace element proxy with menu element
                elementProxiesIter.set(menuElement);
            }
            List menuElements = elementProxies;

            // return list of menu elements constructed from element proxies
            return menuElements;
        }

        // no options specified
        return null;
    }

    /**
     * appendMenuElementProxies - append to ordered list of unique menu
     *                            element proxies
     * 
     * @param pathProxy menu element page, folder, or link proxy at path
     * @param elementProxies element proxies list
     */
    private void appendMenuElementProxies(Node pathProxy, List elementProxies)
    {
        // make sure new proxy is unique and add
        // to element proxies list
        if (!elementProxies.contains(pathProxy))
        {
            elementProxies.add(pathProxy);
        }
    }
    
    /**
     * clone - clone this instance
     *
     * @return unparented deep copy
     */
    public Object clone() throws CloneNotSupportedException
    {
        // clone this object
        MenuImpl copy = (MenuImpl)super.clone();

        // clone and reparent copy elements
        if (copy.elements != null)
        {
            Iterator elementsIter = copy.elements.iterator();
            copy.elements = new ArrayList(copy.elements.size());
            while (elementsIter.hasNext())
            {
                MenuElementImpl elementCopy = (MenuElementImpl)((MenuElementImpl)elementsIter.next()).clone();
                elementCopy.setParentMenu(copy);
                copy.elements.add(elementCopy);
            }
        }
        return copy;
    }

    /**
     * getElementType - get type of menu element
     *
     * @return MENU_ELEMENT_TYPE
     */
    public String getElementType()
    {
        return MENU_ELEMENT_TYPE;
    }

    /**
     * getName - get name of menu
     *
     * @return menu name
     */
    public String getName()
    {
        return definition.getName();
    }

    /**
     * getTitle - get default title for menu element
     *
     * @return title text
     */
    public String getTitle()
    {
        // return definition title
        String title = definition.getTitle();
        if (title != null)
        {
            return title;
        }
        // return node or default title
        return super.getTitle();
    }

    /**
     * getShortTitle - get default short title for menu element
     *
     * @return short title text
     */
    public String getShortTitle()
    {
        // return definition short title
        String title = definition.getShortTitle();
        if (title != null)
        {
            return title;
        }

        // return node or default short title
        return super.getShortTitle();
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
        // return definition short title for preferred locale
        String title = definition.getTitle(locale);
        if (title != null)
        {
            return title;
        }

        // return node or default title for preferred locale
        return super.getTitle(locale);
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
        // return definition short title for preferred locale
        String title = definition.getShortTitle(locale);
        if (title != null)
        {
            return title;
        }

        // return node or default short title for preferred locale
        return super.getShortTitle(locale);
    }

    /**
     * getMetadata - get generic metadata for menu element
     *
     * @return metadata
     */    
    public GenericMetadata getMetadata()
    {
        // return definition metadata
        GenericMetadata metadata = definition.getMetadata();
        if ((metadata != null) && (metadata.getFields() != null) && !metadata.getFields().isEmpty())
        {
            return metadata;
        }

        // return node metadata
        return super.getMetadata();
    }

    /**
     * getSkin - get skin name for menu element
     *
     * @return skin name
     */
    public String getSkin()
    {
        // get skin from definition or inherit from parent menu
        String skin = definition.getSkin();
        if (skin == null)
        {
            skin = super.getSkin();
        }
        return skin;
    }

    /**
     * getUrl - get url of top level folder that defined
     *          menu options; only available for menus
     *          defined without multiple options, nested
     *          menus, or separators
     *
     * @return folder url
     */
    public String getUrl()
    {
        // return url of node associated with menu
        // option if defined
        if (getNode() != null)
        {
            return getNode().getUrl();
        }
        return null;
    }

    /**
     * isHidden - get hidden state of folder that defined
     *            menu options; only available for menus
     *            defined without multiple options, nested
     *            menus, or separators
     *
     * @return hidden state
     */
    public boolean isHidden()
    {
        // return hidden state of node associated with
        // menu option if defined
        if (getNode() != null)
        {
            return getNode().isHidden();
        }
        return false;
    }

    /**
     * isSelected - return true if an option or nested
     *              menu within this menu are selected by
     *              the specified request context
     *
     * @param context request context
     * @return selected state
     */
    public boolean isSelected(PortalSiteRequestContext context)
    {
        // menu is selected if a selected element exists
        return (getSelectedElement(context) != null);
    }

    /**
     * getElements - get ordered list of menu elements that
     *               are members of this menu; possibly contains
     *               options, nested menus, or separators
     *
     * @return menu elements list
     */
    public List getElements()
    {
        return elements;
    }

    /**
     * isEmpty - get empty state of list of menu elements
     *
     * @return menu elements list empty state
     */
    public boolean isEmpty()
    {
        return ((elements == null) || elements.isEmpty());
    }

    /**
     * isElementRelative - get flag that indicates whether any relative paths
     *                     dependent on the current page in context were
     *                     referenced while constructing menu elements
     *
     * @return relative element status
     */
    public boolean isElementRelative()
    {
        return elementRelative;
    }

    /**
     * getSelectedElement - return selected option or nested
     *                      menu within this menu selected by
     *                      the specified request context
     *
     * @return selected menu element
     */
    public MenuElement getSelectedElement(PortalSiteRequestContext context)
    {
        // test nested menu and option menu
        // elements for selected status
        if (elements != null)
        {
            Iterator elementsIter = elements.iterator();
            while (elementsIter.hasNext())
            {
                MenuElement element = (MenuElement)elementsIter.next();
                
                // test element selected
                boolean selected = false;
                if (element instanceof MenuOption)
                {
                    selected = ((MenuOption)element).isSelected(context);
                }
                else if (element instanceof Menu)
                {
                    selected = ((Menu)element).isSelected(context);
                }
                
                // return selected element
                if (selected)
                {
                    return element;
                }
            }
        }
        return null;
    }

    /**
     * orderRegexpPattern - tests for and converts simple order wildcard
     *                      and character class regular exressions to
     *                      perl5/standard java pattern syntax
     *
     * @param regexp - candidate order regular expression
     * @return - converted pattern
     */
    private static String orderRegexpPattern(String regexp)
    {
        // convert expression to pattern
        StringBuffer pattern = null;
        for (int i = 0, limit = regexp.length(); (i < limit); i++)
        {
            char regexpChar = regexp.charAt(i);
            switch (regexpChar)
            {
                case '*':
                case '.':
                case '?':
                case '[':
                    if (pattern == null)
                    {
                        pattern = new StringBuffer(regexp.length()*2);
                        pattern.append(regexp.substring(0, i));
                    }
                    switch (regexpChar)
                    {
                        case '*':
                            pattern.append("[^"+Folder.PATH_SEPARATOR+"]*");
                            break;
                        case '.':
                            pattern.append("\\.");
                            break;
                        case '?':
                            pattern.append("[^"+Folder.PATH_SEPARATOR+"]");
                            break;
                        case '[':
                            pattern.append('[');
                            break;
                    }
                    break;
                default:
                    if (pattern != null)
                    {
                        pattern.append(regexpChar);
                    }
                    break;
            }
        }

        // return converted pattern
        if (pattern != null)
            return pattern.toString();
        return regexp;
    }
}
