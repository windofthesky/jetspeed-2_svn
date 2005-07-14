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
package org.apache.jetspeed.page.impl;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.folder.impl.MenuDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.MenuExcludeDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.MenuIncludeDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.MenuOptionsDefinitionImpl;
import org.apache.jetspeed.om.folder.impl.MenuSeparatorDefinitionImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.LinkImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PropertyImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerEventListener;
import org.apache.jetspeed.page.document.Node;

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
    
    protected Class fragmentClass = FragmentImpl.class;
    protected Class pageClass = PageImpl.class;
    protected Class folderClass = FolderImpl.class;
    protected Class linkClass = LinkImpl.class;
    protected Class propertyClass = PropertyImpl.class;
    protected Class menuDefinitionClass = MenuDefinitionImpl.class;
    protected Class menuExcludeDefinitionClass = MenuExcludeDefinitionImpl.class;
    protected Class menuIncludeDefinitionClass = MenuIncludeDefinitionImpl.class;
    protected Class menuOptionsDefinitionClass = MenuOptionsDefinitionImpl.class;
    protected Class menuSeparatorDefinitionClass = MenuSeparatorDefinitionImpl.class;

    protected IdGenerator generator = null;

    private boolean permissionsEnabled;

    private boolean constraintsEnabled;

    private List listeners = new LinkedList();

    public AbstractPageManager(IdGenerator generator, boolean permissionsEnabled, boolean constraintsEnabled)
    {    
        this.generator = generator;
        this.permissionsEnabled = permissionsEnabled;
        this.constraintsEnabled = constraintsEnabled;
    }
    
    public AbstractPageManager(IdGenerator generator, boolean permissionsEnabled, boolean constraintsEnabled, List modelClasses)
    {
        this.generator = generator;     
        if (modelClasses.size() > 0)
        {
            this.fragmentClass = (Class)modelClasses.get(0);
            if (modelClasses.size() > 1)
            {
                this.pageClass  = (Class)modelClasses.get(1);
                if (modelClasses.size() > 2)
                {
                    this.propertyClass  = (Class)modelClasses.get(2);
                }                
            }
        }                                 
        this.permissionsEnabled = permissionsEnabled;
        this.constraintsEnabled = constraintsEnabled;
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
            page.setId(path);
            
            // create the default fragment
            Fragment fragment = (Fragment)createObject(this.fragmentClass);
            fragment.setId(generator.getNextPeid());
            fragment.setType(Fragment.LAYOUT);
            page.setRootFragment(fragment);            
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
            folder.setId(path);
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
            link.setId(path);
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
            fragment.setId(generator.getNextPeid());
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
}
