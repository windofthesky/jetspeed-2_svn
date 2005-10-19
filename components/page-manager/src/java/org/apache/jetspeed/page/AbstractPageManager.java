/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.portalsite.MenuElement;

/**
 * AbstractPageManagerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractPageManager 
    implements PageManager    
{
    private final static Log log = LogFactory.getLog(AbstractPageManager.class);
    
    protected Class fragmentClass;
    protected Class pageClass;
    protected Class folderClass;
    protected Class linkClass;
    protected Class propertyClass;
    protected Class menuDefinitionClass;
    protected Class menuExcludeDefinitionClass;
    protected Class menuIncludeDefinitionClass;
    protected Class menuOptionsDefinitionClass;
    protected Class menuSeparatorDefinitionClass;
    protected Class securityConstraintsClass;
    protected Class securityConstraintClass;

    private boolean permissionsEnabled;

    private boolean constraintsEnabled;

    private List listeners = new LinkedList();

    public AbstractPageManager(boolean permissionsEnabled, boolean constraintsEnabled)
    {    
        this.permissionsEnabled = permissionsEnabled;
        this.constraintsEnabled = constraintsEnabled;
    }
    
    public AbstractPageManager(boolean permissionsEnabled, boolean constraintsEnabled, Map modelClasses)
    {
        this(permissionsEnabled, constraintsEnabled);     

        this.fragmentClass = (Class)modelClasses.get("FragmentImpl.class");
        this.pageClass = (Class)modelClasses.get("PageImpl.class");
        this.folderClass = (Class)modelClasses.get("FolderImpl.class");
        this.linkClass = (Class)modelClasses.get("LinkImpl.class");
        this.propertyClass = (Class)modelClasses.get("PropertyImpl.class");
        this.menuDefinitionClass = (Class)modelClasses.get("MenuDefinitionImpl.class");
        this.menuExcludeDefinitionClass = (Class)modelClasses.get("MenuExcludeDefinitionImpl.class");
        this.menuIncludeDefinitionClass = (Class)modelClasses.get("MenuIncludeDefinitionImpl.class");
        this.menuOptionsDefinitionClass = (Class)modelClasses.get("MenuOptionsDefinitionImpl.class");
        this.menuSeparatorDefinitionClass = (Class)modelClasses.get("MenuSeparatorDefinitionImpl.class");
        this.securityConstraintsClass = (Class)modelClasses.get("SecurityConstraintsImpl.class");
        this.securityConstraintClass = (Class)modelClasses.get("SecurityConstraintImpl.class");
    }
    
    /**
     * <p>
     * getPermissionsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.PageManager#getPermissionsEnabled()
     * @return
     */
    public boolean getPermissionsEnabled()
    {
        return permissionsEnabled;
    }

    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @see org.apache.jetspeed.page.PageManager#getConstraintsEnabled()
     * @return
     */
    public boolean getConstraintsEnabled()
    {
        return constraintsEnabled;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPage(java.lang.String)
     */
    public Page newPage(String path)
    {
        Page page = null;
        try
        {
            // factory create the page and set id/path
            page = (Page)createObject(this.pageClass);            
            if (!path.startsWith(Folder.PATH_SEPARATOR))
            {
                path = Folder.PATH_SEPARATOR + path;
            }
            if (!path.endsWith(Page.DOCUMENT_TYPE))
            {
                path += Page.DOCUMENT_TYPE;
            }
            page.setPath(path);
            
            // create the default fragment
            page.setRootFragment(newFragment());            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
        }
        return page;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFolder(java.lang.String)
     */
    public Folder newFolder(String path)
    {
        Folder folder = null;
        try
        {
            // factory create the folder and set id/path
            folder = (Folder)createObject(this.folderClass);            
            if (!path.startsWith(Folder.PATH_SEPARATOR))
            {
                path = Folder.PATH_SEPARATOR + path;
            }
            folder.setPath(path);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create link object for " + this.linkClass;
            log.error(message, e);
        }
        return folder;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newLink(java.lang.String)
     */
    public Link newLink(String path)
    {
        Link link = null;
        try
        {
            // factory create the page and set id/path
            link = (Link)createObject(this.linkClass);            
            if (!path.startsWith(Folder.PATH_SEPARATOR))
            {
                path = Folder.PATH_SEPARATOR + path;
            }
            if (!path.endsWith(Link.DOCUMENT_TYPE))
            {
                path += Link.DOCUMENT_TYPE;
            }
            link.setPath(path);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create link object for " + this.linkClass;
            log.error(message, e);
        }
        return link;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragment()
     */
    public Fragment newFragment()
    {
        Fragment fragment = null;
        try
        {
            fragment = (Fragment)createObject(this.fragmentClass);
            fragment.setType(Fragment.LAYOUT);          
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
            // throw new JetspeedException(message, e);
        }
        return fragment;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newProperty()
     */
    public Property newProperty()
    {
        Property property = null;
        try
        {
            property = (Property)createObject(this.propertyClass);
            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment-property object for " + this.propertyClass;
            log.error(message, e);
            // throw new JetspeedException(message, e);
        }
        return property;        
    }

    /**
     * newMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object
     */
    public MenuDefinition newMenuDefinition()
    {
        try
        {
            return (MenuDefinition)createObject(this.menuDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu definition object for " + this.menuDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object
     */
    public MenuExcludeDefinition newMenuExcludeDefinition()
    {
        try
        {
            return (MenuExcludeDefinition)createObject(this.menuExcludeDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu exclude definition object for " + this.menuExcludeDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object
     */
    public MenuIncludeDefinition newMenuIncludeDefinition()
    {
        try
        {
            return (MenuIncludeDefinition)createObject(this.menuIncludeDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu include definition object for " + this.menuIncludeDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object
     */
    public MenuOptionsDefinition newMenuOptionsDefinition()
    {
        try
        {
            return (MenuOptionsDefinition)createObject(this.menuOptionsDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu options definition object for " + this.menuOptionsDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object
     */
    public MenuSeparatorDefinition newMenuSeparatorDefinition()
    {
        try
        {
            return (MenuSeparatorDefinition)createObject(this.menuSeparatorDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu separator definition object for " + this.menuSeparatorDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newSecurityConstraints - creates a new empty security constraints definition
     *
     * @return a newly created SecurityConstraints object
     */
    public SecurityConstraints newSecurityConstraints()
    {
        try
        {
            return (SecurityConstraints)createObject(this.securityConstraintsClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraints definition object for " + this.securityConstraintsClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object
     */
    public SecurityConstraint newSecurityConstraint()
    {
        try
        {
            return (SecurityConstraint)createObject(this.securityConstraintClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraint definition object for " + this.securityConstraintClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * createObject - creates a new page manager implementation object
     *
     * @param classe implementation class
     * @return a newly created implementation object
     */
    private Object createObject(Class classe)
    {
        Object object = null;
        try
        {
            object = classe.newInstance();
        }
        catch (Exception e)
        {
            log.error("Factory failed to create object: " + classe.getName(), e);            
        }
        return object;        
    }    

    /**
     * addListener - add page manager event listener
     *
     * @param listener page manager event listener
     */
    public void addListener(PageManagerEventListener listener)
    {
        // add listener to listeners list
        listeners.add(listener);
    }

    /**
     * removeListener - remove page manager event listener
     *
     * @param listener page manager event listener
     */
    public void removeListener(PageManagerEventListener listener)
    {
        // remove listener from listeners list
        listeners.remove(listener);
    }

    /**
     * notifyNewNode - notify page manager event listeners of
     *                 new node event
     *
     * @param node new managed node if known
     */
    protected void notifyNewNode(Node node)
    {
        Iterator listenersIter = listeners.iterator();
        while (listenersIter.hasNext())
        {
            PageManagerEventListener listener = (PageManagerEventListener)listenersIter.next();
            try
            {
                listener.newNode(node);
            }
            catch (Exception e)
            {
                log.error("Failed to notify page manager event listener", e);
            }
        }
    }

    /**
     * notifyUpdatedNode - notify page manager event listeners of
     *                     updated node event
     *
     * @param node updated managed node if known
     */
    protected void notifyUpdatedNode(Node node)
    {
        Iterator listenersIter = listeners.iterator();
        while (listenersIter.hasNext())
        {
            PageManagerEventListener listener = (PageManagerEventListener)listenersIter.next();
            try
            {
                listener.updatedNode(node);
            }
            catch (Exception e)
            {
                log.error("Failed to notify page manager event listener", e);
            }
        }
    }

    /**
     * notifyRemovedNode - notify page manager event listeners of
     *                     removed node event
     *
     * @param node removed managed node if known
     */
    protected void notifyRemovedNode(Node node)
    {
        Iterator listenersIter = listeners.iterator();
        while (listenersIter.hasNext())
        {
            PageManagerEventListener listener = (PageManagerEventListener)listenersIter.next();
            try
            {
                listener.removedNode(node);
            }
            catch (Exception e)
            {
                log.error("Failed to notify page manager event listener", e);
            }
        }
    }
    
    protected Fragment cloneFragment(Fragment source)
    {
        Fragment clone = newFragment();
        clone.setDecorator(source.getDecorator());
        clone.setName(source.getName());
        clone.setShortTitle(source.getShortTitle());
        clone.setSkin(source.getSkin());
        clone.setTitle(source.getTitle());
        clone.setType(source.getType());
        clone.setState(source.getState());
        
        // clone properties
        Iterator names = source.getLayoutProperties().iterator();
        while (names.hasNext())
        {
            String name = (String)names.next();
            Iterator props = source.getProperties(name).iterator();
            while (props.hasNext())
            {
                Property srcProp = (Property)props.next();
                Property dstProp = newProperty();
                dstProp.setLayout(name);
                dstProp.setName(srcProp.getName());
                dstProp.setValue(srcProp.getValue());
                clone.addProperty(dstProp);
            }            
        }
        
        // clone security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();
        if (srcSecurity != null)
        {
            SecurityConstraints dstSecurity = cloneSecurityConstraints(srcSecurity);
            clone.setSecurityConstraints(dstSecurity);
        }            
        
        // recursively clone fragments
        Iterator fragments = source.getFragments().iterator();
        while (fragments.hasNext())
        {
            Fragment fragment = (Fragment)fragments.next();
            Fragment clonedFragment = cloneFragment(fragment);
            clone.getFragments().add(clonedFragment);
        }
        
        return clone;
    }
    
    public Page clonePage(Page source, String path)
    throws JetspeedException, PageNotUpdatedException
    {
        // create the new page and clone attributes
        Page page = newPage(path);
        page.setTitle(source.getTitle());
        page.setShortTitle(source.getShortTitle());
        page.setDefaultDecorator(source.getDefaultDecorator(Fragment.LAYOUT), Fragment.LAYOUT);
        page.setDefaultDecorator(source.getDefaultDecorator(Fragment.PORTLET), Fragment.PORTLET);
        page.setDefaultSkin(source.getDefaultSkin());
            
        // TODO: clone the metadata
//        if (source.getMetadata() != null)
//        {
//            Collection fields = source.getMetadata().getFields();
//            if (fields != null)
//            {
//                Iterator fieldsIterator = fields.iterator();
//                while (fieldsIterator.hasNext())
//                {
//                    // LEFT OFF HERE Field srcField = (Field)fieldIterator.next();
//                }
//            }
//        }
        
        // create the root fragment and clone attributes and all subfragments
        Fragment root = cloneFragment(source.getRootFragment());
        page.setRootFragment(root);
        
        // clone menus
        List menus = page.getMenuDefinitions();
        if (menus != null)
        {
            List clonedMenus = cloneMenuDefinitions(page.getMenuDefinitions());
            page.setMenuDefinitions(clonedMenus);
        }
        
        // clone security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if (srcSecurity != null)
        {
            SecurityConstraints clonedSecurity = cloneSecurityConstraints(srcSecurity);
            page.setSecurityConstraints(clonedSecurity);
        }    
        
        updatePage(page);
        return page;
    }

    protected List cloneMenuDefinitions(List srcMenus)
    {
        List clonedMenus = new ArrayList(4); 
        Iterator menus = srcMenus.iterator();
        while (menus.hasNext())
        {
            MenuDefinition srcMenu = (MenuDefinition)menus.next();
            MenuDefinition clonedMenu = newMenuDefinition();
            clonedMenu.setDepth(srcMenu.getDepth());
            clonedMenu.setName(srcMenu.getName());
            clonedMenu.setOptions(srcMenu.getOptions());
            clonedMenu.setOrder(srcMenu.getOrder());
            clonedMenu.setPaths(srcMenu.isPaths());
            clonedMenu.setProfile(srcMenu.getProfile());
            clonedMenu.setRegexp(srcMenu.isRegexp());
            
            // TODO: how do I clone all localized short titles?
            clonedMenu.setShortTitle(srcMenu.getShortTitle());
            
            clonedMenu.setSkin(srcMenu.getSkin());
            
            // TODO: how do I clone all localized titles?            
            clonedMenu.setTitle(srcMenu.getTitle());
            
            // TODO: clone metadata
            clonedMenu.getMetadata();
            
            List srcElements = clonedMenu.getMenuElements();
            if (srcElements != null)
            {
                List clonedElements = cloneMenuElements(srcElements);
                clonedMenu.setMenuElements(clonedElements);
            }
        }
        return clonedMenus;
    }
    
    protected List cloneMenuElements(List srcElements)
    {
        List clonedElements = new ArrayList(8);
        Iterator elements = srcElements.iterator();
        while (elements.hasNext())
        {
            MenuElement srcElement = (MenuElement)elements.next();
            // MenuElement clonedElement = newMenuElement();
        }
        return clonedElements;
    }
    
    protected SecurityConstraints cloneSecurityConstraints(SecurityConstraints source)
    {
        SecurityConstraints security = newSecurityConstraints();
        if (source.getOwner() != null)        
        {
            security.setOwner(source.getOwner());
        }
        if (source.getSecurityConstraints() != null)
        {
            List clonedConstraints = new ArrayList(8);
            Iterator constraints = source.getSecurityConstraints().iterator();
            while (constraints.hasNext())
            {
                SecurityConstraint srcConstraint = (SecurityConstraint)constraints.next();
                SecurityConstraint dstConstraint = newSecurityConstraint();
                dstConstraint.setUsers(srcConstraint.getUsers());                
                dstConstraint.setRoles(srcConstraint.getRoles());
                dstConstraint.setGroups(srcConstraint.getGroups());
                dstConstraint.setPermissions(srcConstraint.getPermissions());
                clonedConstraints.add(dstConstraint);
            }
            security.setSecurityConstraints(clonedConstraints);
        }
        if (source.getSecurityConstraintsRefs() != null)
        {
            List clonedRefs = new ArrayList(8);
            Iterator refs = source.getSecurityConstraintsRefs().iterator();
            while (refs.hasNext())
            {                
                String constraintsRef = (String)refs.next();                
                clonedRefs.add(constraintsRef);
            }
            security.setSecurityConstraintsRefs(clonedRefs);            
        }
        return security;
    }
}
