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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;

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
    
    private final static String FOLDER_NODE_TYPE = "folder";
    private final static String PAGE_NODE_TYPE = "page";
    private final static String FRAGMENT_NODE_TYPE = "fragment";
    private final static String LINK_NODE_TYPE = "link";
    private final static String PAGE_SECURITY_NODE_TYPE = "pageSecurity";

    protected Class fragmentClass;
    protected Class pageClass;
    protected Class folderClass;
    protected Class linkClass;
    protected Class pageSecurityClass;
    protected Class propertyClass;
    protected Class folderMenuDefinitionClass;
    protected Class folderMenuExcludeDefinitionClass;
    protected Class folderMenuIncludeDefinitionClass;
    protected Class folderMenuOptionsDefinitionClass;
    protected Class folderMenuSeparatorDefinitionClass;
    protected Class pageMenuDefinitionClass;
    protected Class pageMenuExcludeDefinitionClass;
    protected Class pageMenuIncludeDefinitionClass;
    protected Class pageMenuOptionsDefinitionClass;
    protected Class pageMenuSeparatorDefinitionClass;
    protected Class securityConstraintsClass;
    protected Class folderSecurityConstraintClass;
    protected Class pageSecurityConstraintClass;
    protected Class fragmentSecurityConstraintClass;
    protected Class linkSecurityConstraintClass;
    protected Class pageSecuritySecurityConstraintClass;
    protected Class securityConstraintsDefClass;
    protected Class fragmentPreferenceClass;

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

        this.fragmentClass = (Class)modelClasses.get("FragmentImpl");
        this.pageClass = (Class)modelClasses.get("PageImpl");
        this.folderClass = (Class)modelClasses.get("FolderImpl");
        this.linkClass = (Class)modelClasses.get("LinkImpl");
        this.pageSecurityClass = (Class)modelClasses.get("PageSecurityImpl");
        this.folderMenuDefinitionClass = (Class)modelClasses.get("FolderMenuDefinitionImpl");
        this.folderMenuExcludeDefinitionClass = (Class)modelClasses.get("FolderMenuExcludeDefinitionImpl");
        this.folderMenuIncludeDefinitionClass = (Class)modelClasses.get("FolderMenuIncludeDefinitionImpl");
        this.folderMenuOptionsDefinitionClass = (Class)modelClasses.get("FolderMenuOptionsDefinitionImpl");
        this.folderMenuSeparatorDefinitionClass = (Class)modelClasses.get("FolderMenuSeparatorDefinitionImpl");
        this.pageMenuDefinitionClass = (Class)modelClasses.get("PageMenuDefinitionImpl");
        this.pageMenuExcludeDefinitionClass = (Class)modelClasses.get("PageMenuExcludeDefinitionImpl");
        this.pageMenuIncludeDefinitionClass = (Class)modelClasses.get("PageMenuIncludeDefinitionImpl");
        this.pageMenuOptionsDefinitionClass = (Class)modelClasses.get("PageMenuOptionsDefinitionImpl");
        this.pageMenuSeparatorDefinitionClass = (Class)modelClasses.get("PageMenuSeparatorDefinitionImpl");
        this.securityConstraintsClass = (Class)modelClasses.get("SecurityConstraintsImpl");
        this.folderSecurityConstraintClass = (Class)modelClasses.get("FolderSecurityConstraintImpl");
        this.pageSecurityConstraintClass = (Class)modelClasses.get("PageSecurityConstraintImpl");
        this.fragmentSecurityConstraintClass = (Class)modelClasses.get("FragmentSecurityConstraintImpl");
        this.linkSecurityConstraintClass = (Class)modelClasses.get("LinkSecurityConstraintImpl");
        this.pageSecuritySecurityConstraintClass = (Class)modelClasses.get("PageSecuritySecurityConstraintImpl");
        this.securityConstraintsDefClass = (Class)modelClasses.get("SecurityConstraintsDefImpl");
        this.fragmentPreferenceClass = (Class)modelClasses.get("FragmentPreferenceImpl");
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
     * @see org.apache.jetspeed.page.PageManager#newPageSecurity()
     */
    public PageSecurity newPageSecurity()
    {
        PageSecurity pageSecurity = null;
        try
        {
            // factory create the document and set id/path
            pageSecurity = (PageSecurity)createObject(this.pageSecurityClass);            
            pageSecurity.setPath(Folder.PATH_SEPARATOR + PageSecurity.DOCUMENT_TYPE);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page security object for " + this.pageClass;
            log.error(message, e);
        }
        return pageSecurity;        
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
     * @see org.apache.jetspeed.page.PageManager#newPortletFragment()
     */
    public Fragment newPortletFragment()
    {
        Fragment fragment = null;
        try
        {
            fragment = (Fragment)createObject(this.fragmentClass);
            fragment.setType(Fragment.PORTLET);          
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.pageClass;
            log.error(message, e);
            // throw new JetspeedException(message, e);
        }
        return fragment;        
    }
    
    /**
     * newFolderMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object
     */
    public MenuDefinition newFolderMenuDefinition()
    {
        try
        {
            return (MenuDefinition)createObject(this.folderMenuDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu definition object for " + this.folderMenuDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newFolderMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object
     */
    public MenuExcludeDefinition newFolderMenuExcludeDefinition()
    {
        try
        {
            return (MenuExcludeDefinition)createObject(this.folderMenuExcludeDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu exclude definition object for " + this.folderMenuExcludeDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newFolderMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object
     */
    public MenuIncludeDefinition newFolderMenuIncludeDefinition()
    {
        try
        {
            return (MenuIncludeDefinition)createObject(this.folderMenuIncludeDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu include definition object for " + this.folderMenuIncludeDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newFolderMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object
     */
    public MenuOptionsDefinition newFolderMenuOptionsDefinition()
    {
        try
        {
            return (MenuOptionsDefinition)createObject(this.folderMenuOptionsDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu options definition object for " + this.folderMenuOptionsDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newFolderMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object
     */
    public MenuSeparatorDefinition newFolderMenuSeparatorDefinition()
    {
        try
        {
            return (MenuSeparatorDefinition)createObject(this.folderMenuSeparatorDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu separator definition object for " + this.folderMenuSeparatorDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newPageMenuDefinition - creates a new empty menu definition
     *
     * @return a newly created MenuDefinition object
     */
    public MenuDefinition newPageMenuDefinition()
    {
        try
        {
            return (MenuDefinition)createObject(this.pageMenuDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu definition object for " + this.pageMenuDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newPageMenuExcludeDefinition - creates a new empty menu exclude definition
     *
     * @return a newly created MenuExcludeDefinition object
     */
    public MenuExcludeDefinition newPageMenuExcludeDefinition()
    {
        try
        {
            return (MenuExcludeDefinition)createObject(this.pageMenuExcludeDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu exclude definition object for " + this.pageMenuExcludeDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newPageMenuIncludeDefinition - creates a new empty menu include definition
     *
     * @return a newly created MenuIncludeDefinition object
     */
    public MenuIncludeDefinition newPageMenuIncludeDefinition()
    {
        try
        {
            return (MenuIncludeDefinition)createObject(this.pageMenuIncludeDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu include definition object for " + this.pageMenuIncludeDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newPageMenuOptionsDefinition - creates a new empty menu options definition
     *
     * @return a newly created MenuOptionsDefinition object
     */
    public MenuOptionsDefinition newPageMenuOptionsDefinition()
    {
        try
        {
            return (MenuOptionsDefinition)createObject(this.pageMenuOptionsDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu options definition object for " + this.pageMenuOptionsDefinitionClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newPageMenuSeparatorDefinition - creates a new empty menu separator definition
     *
     * @return a newly created MenuSeparatorDefinition object
     */
    public MenuSeparatorDefinition newPageMenuSeparatorDefinition()
    {
        try
        {
            return (MenuSeparatorDefinition)createObject(this.pageMenuSeparatorDefinitionClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create menu separator definition object for " + this.pageMenuSeparatorDefinitionClass;
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
     * newFolderSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object
     */
    public SecurityConstraint newFolderSecurityConstraint()
    {
        try
        {
            return (SecurityConstraint)createObject(this.folderSecurityConstraintClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraint definition object for " + this.folderSecurityConstraintClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newPageSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object
     */
    public SecurityConstraint newPageSecurityConstraint()
    {
        try
        {
            return (SecurityConstraint)createObject(this.pageSecurityConstraintClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraint definition object for " + this.pageSecurityConstraintClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newFragmentSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object
     */
    public SecurityConstraint newFragmentSecurityConstraint()
    {
        try
        {
            return (SecurityConstraint)createObject(this.fragmentSecurityConstraintClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraint definition object for " + this.fragmentSecurityConstraintClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newLinkSecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object
     */
    public SecurityConstraint newLinkSecurityConstraint()
    {
        try
        {
            return (SecurityConstraint)createObject(this.linkSecurityConstraintClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraint definition object for " + this.linkSecurityConstraintClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newPageSecuritySecurityConstraint - creates a new security constraint definition
     *
     * @return a newly created SecurityConstraint object
     */
    public SecurityConstraint newPageSecuritySecurityConstraint()
    {
        try
        {
            return (SecurityConstraint)createObject(this.pageSecuritySecurityConstraintClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraint definition object for " + this.pageSecuritySecurityConstraintClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newSecurityConstraintsDef - creates a new security constraints definition
     *
     * @return a newly created SecurityConstraintsDef object
     */
    public SecurityConstraintsDef newSecurityConstraintsDef()
    {
        try
        {
            return (SecurityConstraintsDef)createObject(this.securityConstraintsDefClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraints definition object for " + this.securityConstraintsDefClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newFragmentPreference - creates a new security constraints definition
     *
     * @return a newly created FragmentPreference object
     */
    public FragmentPreference newFragmentPreference()
    {
        try
        {
            return (FragmentPreference)createObject(this.fragmentPreferenceClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create security constraints definition object for " + this.fragmentPreferenceClass;
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
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }

    /**
     * removeListener - remove page manager event listener
     *
     * @param listener page manager event listener
     */
    public void removeListener(PageManagerEventListener listener)
    {
        // remove listener from listeners list
        synchronized (listeners)
        {
            listeners.remove(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#reset()
     */
    public void reset()
    {
        // nothing to reset by default
    }

    /**
     * notifyNewNode - notify page manager event listeners of
     *                 new node event
     *
     * @param node new managed node if known
     */
    public void notifyNewNode(Node node)
    {
        // copy listeners list to reduce synchronization deadlock
        List listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList(listeners);
        }
        // notify listeners
        Iterator listenersIter = listenersList.iterator();
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
    public void notifyUpdatedNode(Node node)
    {
        // copy listeners list to reduce synchronization deadlock
        List listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList(listeners);
        }
        // notify listeners
        Iterator listenersIter = listenersList.iterator();
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
    public void notifyRemovedNode(Node node)
    {
        // copy listeners list to reduce synchronization deadlock
        List listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList(listeners);
        }
        // notify listeners
        Iterator listenersIter = listenersList.iterator();
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
        
    public Folder copyFolder(Folder source, String path)
    throws JetspeedException, FolderNotUpdatedException
    {
        // create the new folder and copy attributes
        Folder folder = newFolder(path);
        folder.setDefaultPage(source.getDefaultPage()); 
        folder.setShortTitle(source.getShortTitle());
        folder.setTitle(source.getTitle());
        folder.setHidden(source.isHidden());
        folder.setDefaultDecorator(source.getDefaultDecorator(Fragment.LAYOUT), Fragment.LAYOUT);
        folder.setDefaultDecorator(source.getDefaultDecorator(Fragment.PORTLET), Fragment.PORTLET);
        folder.setSkin(source.getSkin());

        // copy locale specific metadata
        folder.getMetadata().copyFields(source.getMetadata().getFields());
        
        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(FOLDER_NODE_TYPE, srcSecurity);
            folder.setSecurityConstraints(copiedSecurity);
        }    
        
        // copy document orders
        folder.setDocumentOrder(new ArrayList());
        Iterator documentOrders = source.getDocumentOrder().iterator();
        while (documentOrders.hasNext())
        {
            String name = (String)documentOrders.next();
            folder.getDocumentOrder().add(name);
        }

        // copy menu definitions
        List menus = source.getMenuDefinitions();
        if (menus != null)
        {
            List copiedMenus = copyMenuDefinitions(FOLDER_NODE_TYPE, menus);
            folder.setMenuDefinitions(copiedMenus);
        }        
                
        return folder;
    }
    
    public Page copyPage(Page source, String path)
    throws JetspeedException, PageNotUpdatedException
    {
        // create the new page and copy attributes
        Page page = newPage(path);
        page.setTitle(source.getTitle());
        page.setShortTitle(source.getShortTitle());
        page.setVersion(source.getVersion());
        page.setDefaultDecorator(source.getDefaultDecorator(Fragment.LAYOUT), Fragment.LAYOUT);
        page.setDefaultDecorator(source.getDefaultDecorator(Fragment.PORTLET), Fragment.PORTLET);
        page.setSkin(source.getSkin());
        page.setHidden(source.isHidden());
        
        // copy locale specific metadata
        page.getMetadata().copyFields(source.getMetadata().getFields());
        
        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(PAGE_NODE_TYPE, srcSecurity);
            page.setSecurityConstraints(copiedSecurity);
        }    

        // copy menu definitions
        List menus = source.getMenuDefinitions();
        if (menus != null)
        {
            List copiedMenus = copyMenuDefinitions(PAGE_NODE_TYPE, menus);
            page.setMenuDefinitions(copiedMenus);
        }        
        
        // copy fragments
        Fragment root = copyFragment(source.getRootFragment(), source.getRootFragment().getName());
        page.setRootFragment(root);
        
        return page;
    }

    public Fragment copyFragment(Fragment source, String name)
    {
        // create the new fragment and copy attributes
        Fragment copy = newFragment();
        copy.setDecorator(source.getDecorator());
        copy.setName(name);
        copy.setShortTitle(source.getShortTitle());
        copy.setSkin(source.getSkin());
        copy.setTitle(source.getTitle());
        copy.setType(source.getType());
        copy.setState(source.getState());

        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(FRAGMENT_NODE_TYPE, srcSecurity);
            copy.setSecurityConstraints(copiedSecurity);
        }    
        
        // copy properties
        Iterator props = source.getProperties().entrySet().iterator();
        while (props.hasNext())
        {
            Map.Entry prop = (Map.Entry)props.next();
            copy.getProperties().put(prop.getKey(), prop.getValue());
        }
                  
        // copy preferences
        copy.setPreferences(new ArrayList());
        Iterator prefs = source.getPreferences().iterator();
        while (prefs.hasNext())
        {
            FragmentPreference pref = (FragmentPreference)prefs.next();
            FragmentPreference newPref = this.newFragmentPreference();
            newPref.setName(pref.getName());
            newPref.setReadOnly(pref.isReadOnly());
            newPref.setValueList(new ArrayList());
            Iterator values = pref.getValueList().iterator();            
            while (values.hasNext())
            {
                String value = (String)values.next();
                newPref.getValueList().add(value);
            }
            copy.getPreferences().add(newPref);
        }

        // recursively copy fragments
        Iterator fragments = source.getFragments().iterator();
        while (fragments.hasNext())
        {
            Fragment fragment = (Fragment)fragments.next();
            Fragment copiedFragment = copyFragment(fragment, fragment.getName());
            copy.getFragments().add(copiedFragment);
        }
        return copy;
    }
    
    public Link copyLink(Link source, String path)
    throws JetspeedException, LinkNotUpdatedException
    {
        // create the new link and copy attributes
        Link link = newLink(path);
        link.setTitle(source.getTitle());
        link.setShortTitle(source.getShortTitle());
        link.setSkin(source.getSkin());
        link.setVersion(source.getVersion());
        link.setTarget(source.getTarget());
        link.setUrl(source.getUrl());
        link.setHidden(source.isHidden());
        
        // copy locale specific metadata
        link.getMetadata().copyFields(source.getMetadata().getFields());
        
        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(LINK_NODE_TYPE, srcSecurity);
            link.setSecurityConstraints(copiedSecurity);
        }    

        return link;
    }

    public PageSecurity copyPageSecurity(PageSecurity source) 
    throws JetspeedException, FailedToUpdateDocumentException
    {
        // create the new page security document and copy attributes
        PageSecurity copy = this.newPageSecurity();
        copy.setPath(source.getPath());
        copy.setVersion(source.getVersion());        

        // copy security constraint defintions
        copy.setSecurityConstraintsDefs(new ArrayList());                
        Iterator defs = source.getSecurityConstraintsDefs().iterator();
        while (defs.hasNext())
        {
            SecurityConstraintsDef def = (SecurityConstraintsDef)defs.next();
            SecurityConstraintsDef defCopy = this.newSecurityConstraintsDef();            
            defCopy.setName(def.getName());
            List copiedConstraints = new ArrayList();
            Iterator constraints = def.getSecurityConstraints().iterator();
            while (constraints.hasNext())
            {
                SecurityConstraint srcConstraint = (SecurityConstraint)constraints.next();
                SecurityConstraint dstConstraint = newPageSecuritySecurityConstraint();
                copyConstraint(srcConstraint, dstConstraint);
                copiedConstraints.add(dstConstraint);
            }                                            
            defCopy.setSecurityConstraints(copiedConstraints);
            copy.getSecurityConstraintsDefs().add(defCopy);
        }
        
        // copy global security constraint references
        copy.setGlobalSecurityConstraintsRefs(new ArrayList());
        Iterator globals = source.getGlobalSecurityConstraintsRefs().iterator();
        while (globals.hasNext())
        {
            String global = (String)globals.next();
            copy.getGlobalSecurityConstraintsRefs().add(global);
        }
        
        return copy;
    }

    protected List copyMenuDefinitions(String type, List srcMenus)
    {
        List copiedMenus = new ArrayList(4); 
        Iterator menus = srcMenus.iterator();
        while (menus.hasNext())
        {
            MenuDefinition srcMenu = (MenuDefinition)menus.next();
            MenuDefinition copiedMenu = (MenuDefinition)copyMenuElement(type, srcMenu);
            if (copiedMenu != null)
            {
                copiedMenus.add(copiedMenu);
            }
        }
        return copiedMenus;
    }
    
    protected Object copyMenuElement(String type, Object srcElement)
    {
        if (srcElement instanceof MenuDefinition)
        {
            // create the new menu element and copy attributes
            MenuDefinition source = (MenuDefinition)srcElement;
            MenuDefinition menu = null;
            if (type.equals(PAGE_NODE_TYPE))
            {
                menu = newPageMenuDefinition();
            }
            else if (type.equals(FOLDER_NODE_TYPE))
            {
                menu = newFolderMenuDefinition();
            }
            menu.setDepth(source.getDepth());
            menu.setName(source.getName());
            menu.setOptions(source.getOptions());
            menu.setOrder(source.getOrder());
            menu.setPaths(source.isPaths());
            menu.setProfile(source.getProfile());
            menu.setRegexp(source.isRegexp());
            menu.setShortTitle(source.getShortTitle());
            menu.setSkin(source.getSkin());
            menu.setTitle(source.getTitle());

            // copy locale specific metadata
            menu.getMetadata().copyFields(source.getMetadata().getFields());
        
            // recursively copy menu elements
            List elements = source.getMenuElements();
            if (elements != null)
            {
                List copiedElements = new ArrayList(4); 
                Iterator elementsIter = elements.iterator();
                while (elementsIter.hasNext())
                {
                    Object element = elementsIter.next();
                    Object copiedElement = copyMenuElement(type, element);
                    if (copiedElement != null)
                    {
                        copiedElements.add(copiedElement);
                    }
                }
                menu.setMenuElements(copiedElements);
            }

            return menu;
        }
        else if (srcElement instanceof MenuExcludeDefinition)
        {
            // create the new menu exclude element and copy attributes
            MenuExcludeDefinition source = (MenuExcludeDefinition)srcElement;
            MenuExcludeDefinition menuExclude = null;
            if (type.equals(PAGE_NODE_TYPE))
            {
                menuExclude = newPageMenuExcludeDefinition();
            }
            else if (type.equals(FOLDER_NODE_TYPE))
            {
                menuExclude = newFolderMenuExcludeDefinition();
            }
            menuExclude.setName(source.getName());
            return menuExclude;
        }
        else if (srcElement instanceof MenuIncludeDefinition)
        {
            // create the new menu include element and copy attributes
            MenuIncludeDefinition source = (MenuIncludeDefinition)srcElement;
            MenuIncludeDefinition menuInclude = null;
            if (type.equals(PAGE_NODE_TYPE))
            {
                menuInclude = newPageMenuIncludeDefinition();
            }
            else if (type.equals(FOLDER_NODE_TYPE))
            {
                menuInclude = newFolderMenuIncludeDefinition();
            }
            menuInclude.setName(source.getName());
            menuInclude.setNest(source.isNest());
            return menuInclude;
        }
        else if (srcElement instanceof MenuOptionsDefinition)
        {
            // create the new menu options element and copy attributes
            MenuOptionsDefinition source = (MenuOptionsDefinition)srcElement;
            MenuOptionsDefinition menuOptions = null;
            if (type.equals(PAGE_NODE_TYPE))
            {
                menuOptions = newPageMenuOptionsDefinition();
            }
            else if (type.equals(FOLDER_NODE_TYPE))
            {
                menuOptions = newFolderMenuOptionsDefinition();
            }
            menuOptions.setDepth(source.getDepth());
            menuOptions.setOptions(source.getOptions());
            menuOptions.setOrder(source.getOrder());
            menuOptions.setPaths(source.isPaths());
            menuOptions.setProfile(source.getProfile());
            menuOptions.setRegexp(source.isRegexp());
            menuOptions.setSkin(source.getSkin());
            return menuOptions;
        }
        else if (srcElement instanceof MenuSeparatorDefinition)
        {
            // create the new menu separator element and copy attributes
            MenuSeparatorDefinition source = (MenuSeparatorDefinition)srcElement;
            MenuSeparatorDefinition menuSeparator = null;
            if (type.equals(PAGE_NODE_TYPE))
            {
                menuSeparator = newPageMenuSeparatorDefinition();
            }
            else if (type.equals(FOLDER_NODE_TYPE))
            {
                menuSeparator = newFolderMenuSeparatorDefinition();
            }
            menuSeparator.setSkin(source.getSkin());
            menuSeparator.setTitle(source.getTitle());
            menuSeparator.setText(source.getText());

            // copy locale specific metadata
            menuSeparator.getMetadata().copyFields(source.getMetadata().getFields());
        
            return menuSeparator;
        }
        return null;
    }

    protected void copyConstraint(SecurityConstraint srcConstraint, SecurityConstraint dstConstraint)
    {
        dstConstraint.setUsers(srcConstraint.getUsers());                
        dstConstraint.setRoles(srcConstraint.getRoles());
        dstConstraint.setGroups(srcConstraint.getGroups());
        dstConstraint.setPermissions(srcConstraint.getPermissions());        
    }
    
    protected SecurityConstraints copySecurityConstraints(String type, SecurityConstraints source)
    {
        SecurityConstraints security = newSecurityConstraints();
        if (source.getOwner() != null)        
        {
            security.setOwner(source.getOwner());
        }
        if (source.getSecurityConstraints() != null)
        {
            List copiedConstraints = new ArrayList(8);
            Iterator constraints = source.getSecurityConstraints().iterator();
            while (constraints.hasNext())
            {
                SecurityConstraint srcConstraint = (SecurityConstraint)constraints.next();
                SecurityConstraint dstConstraint = null;
                if (type.equals(PAGE_NODE_TYPE))
                {
                    dstConstraint = newPageSecurityConstraint();
                }
                else if (type.equals(FOLDER_NODE_TYPE))
                {
                    dstConstraint = newFolderSecurityConstraint();
                }
                else if (type.equals(LINK_NODE_TYPE))
                {
                    dstConstraint = newLinkSecurityConstraint();
                }
                else if (type.equals(FRAGMENT_NODE_TYPE))
                {
                    dstConstraint = newFragmentSecurityConstraint();
                }
                copyConstraint(srcConstraint, dstConstraint);
                copiedConstraints.add(dstConstraint);
            }
            security.setSecurityConstraints(copiedConstraints);
        }
        if (source.getSecurityConstraintsRefs() != null)
        {
            List copiedRefs = new ArrayList(8);
            Iterator refs = source.getSecurityConstraintsRefs().iterator();
            while (refs.hasNext())
            {                
                String constraintsRef = (String)refs.next();                
                copiedRefs.add(constraintsRef);
            }
            security.setSecurityConstraintsRefs(copiedRefs);            
        }
        return security;
    }
    
    /**
     * Deep copy a folder
     *  
     * @param source source folder
     * @param dest destination folder
     */
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner)
    throws JetspeedException, PageNotUpdatedException
    {
        PageManagerUtils.deepCopyFolder(this, srcFolder, destinationPath, owner);
    }
            
    public Page getUserPage(String userName, String pageName)
    throws PageNotFoundException, NodeException
    {
        return this.getPage(Folder.USER_FOLDER + userName + Folder.PATH_SEPARATOR + pageName);
    }
    
    public Folder getUserFolder(String userName) 
        throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        return this.getFolder(Folder.USER_FOLDER + userName);        
    }

    public boolean folderExists(String folderName)
    {
        try
        {
            getFolder(folderName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    public boolean pageExists(String pageName)
    {
        try
        {
            getPage(pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public boolean linkExists(String linkName)
    {
        try
        {
            getLink(linkName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean userFolderExists(String userName)
    {
        try
        {
            getFolder(Folder.USER_FOLDER + userName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    public boolean userPageExists(String userName, String pageName)
    {
        try
        {
            getPage(Folder.USER_FOLDER + userName + Folder.PATH_SEPARATOR + pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Creates a user's home page from the roles of the current user.
     * The use case: when a portal is setup to use shared pages, but then
     * the user attempts to customize. At this point, we create the new page(s) for the user.
     * 
     * @param subject
     */
    public void createUserHomePagesFromRoles(Subject subject)
    throws JetspeedException
    {
        PageManagerUtils.createUserHomePagesFromRoles(this, subject);
    }

}
