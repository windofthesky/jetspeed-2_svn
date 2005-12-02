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
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
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
    protected Class pageSecurityClass;
    protected Class propertyClass;
    protected Class menuDefinitionClass;
    protected Class menuExcludeDefinitionClass;
    protected Class menuIncludeDefinitionClass;
    protected Class menuOptionsDefinitionClass;
    protected Class menuSeparatorDefinitionClass;
    protected Class securityConstraintsClass;
    protected Class securityConstraintClass;
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

        this.fragmentClass = (Class)modelClasses.get("FragmentImpl.class");
        this.pageClass = (Class)modelClasses.get("PageImpl.class");
        this.folderClass = (Class)modelClasses.get("FolderImpl.class");
        this.linkClass = (Class)modelClasses.get("LinkImpl.class");
        this.pageSecurityClass = (Class)modelClasses.get("PageSecurityImpl.class");
        this.menuDefinitionClass = (Class)modelClasses.get("MenuDefinitionImpl.class");
        this.menuExcludeDefinitionClass = (Class)modelClasses.get("MenuExcludeDefinitionImpl.class");
        this.menuIncludeDefinitionClass = (Class)modelClasses.get("MenuIncludeDefinitionImpl.class");
        this.menuOptionsDefinitionClass = (Class)modelClasses.get("MenuOptionsDefinitionImpl.class");
        this.menuSeparatorDefinitionClass = (Class)modelClasses.get("MenuSeparatorDefinitionImpl.class");
        this.securityConstraintsClass = (Class)modelClasses.get("SecurityConstraintsImpl.class");
        this.securityConstraintClass = (Class)modelClasses.get("SecurityConstraintImpl.class");
        this.securityConstraintsDefClass = (Class)modelClasses.get("SecurityConstraintsDefImpl.class");
        this.fragmentPreferenceClass = (Class)modelClasses.get("FragmentPreferenceImpl.class");
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
    public void notifyUpdatedNode(Node node)
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
    public void notifyRemovedNode(Node node)
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
        
    public Folder copyFolder(Folder source, String path)
    throws JetspeedException, PageNotUpdatedException
    {
        Folder folder = newFolder(path);
        folder.setDefaultPage(source.getDefaultPage()); 
        folder.setShortTitle(source.getShortTitle());
        folder.setTitle(source.getTitle());
        folder.setHidden(source.isHidden());
        
        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(srcSecurity);
            folder.setSecurityConstraints(copiedSecurity);
        }    
        
        // TODO: document orders

        // TODO: menu definitions
                
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
        page.setDefaultSkin(source.getDefaultSkin());
        page.setHidden(source.isHidden());
        
        // metadata
        copyMetadata(source, page);
        
        // fragments
        Fragment root = copyFragment(source.getRootFragment(), source.getRootFragment().getName());
        page.setRootFragment(root);
        
        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(srcSecurity);
            page.setSecurityConstraints(copiedSecurity);
        }    

        // menus
//        List menus = page.getMenuDefinitions();
//        if (menus != null)
//        {
//            List copiedMenus = copyMenuDefinitions(page.getMenuDefinitions());
//            page.setMenuDefinitions(copiedMenus);
//        }        
        
        return page;
    }

    public Fragment copyFragment(Fragment source, String name)
    {
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
            SecurityConstraints copiedSecurity = copySecurityConstraints(srcSecurity);
            copy.setSecurityConstraints(copiedSecurity);
        }    
        
        // recursively copy fragments
        Iterator fragments = source.getFragments().iterator();
        while (fragments.hasNext())
        {
            Fragment fragment = (Fragment)fragments.next();
            Fragment copiedFragment = copyFragment(fragment, fragment.getName());
            copy.getFragments().add(copiedFragment);
        }
        
        
        // copy properties
        Iterator props = source.getProperties().entrySet().iterator();
        while (props.hasNext())
        {
            Map.Entry prop = (Map.Entry)props.next();
            copy.getProperties().put(prop.getKey(), prop.getValue());
        }
                  
        // copy preferences
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
        }
        return copy;
    }
    
    
    public void copyMetadata(Page source, Page dest)
    {
        if (source.getMetadata() != null)
        {
            Collection fields = source.getMetadata().getFields();
            if (fields != null)
            {
                Iterator fieldsIterator = fields.iterator();
                while (fieldsIterator.hasNext())
                {
                    LocalizedField field = (LocalizedField)fieldsIterator.next();                    
                    dest.getMetadata().addField(field.getLocale(), field.getName(), field.getValue());
                }
            }
        }       
    }
    
    protected List copyMenuDefinitions(List srcMenus)
    {
        List copiedMenus = new ArrayList(4); 
        Iterator menus = srcMenus.iterator();
        while (menus.hasNext())
        {
            MenuDefinition srcMenu = (MenuDefinition)menus.next();
            MenuDefinition copiedMenu = newMenuDefinition();
            copiedMenu.setDepth(srcMenu.getDepth());
            copiedMenu.setName(srcMenu.getName());
            copiedMenu.setOptions(srcMenu.getOptions());
            copiedMenu.setOrder(srcMenu.getOrder());
            copiedMenu.setPaths(srcMenu.isPaths());
            copiedMenu.setProfile(srcMenu.getProfile());
            copiedMenu.setRegexp(srcMenu.isRegexp());
            
            // TODO: how do I copy all localized short titles?
            copiedMenu.setShortTitle(srcMenu.getShortTitle());
            
            copiedMenu.setSkin(srcMenu.getSkin());
            
            // TODO: how do I copy all localized titles?            
            copiedMenu.setTitle(srcMenu.getTitle());
            
            // TODO: copy metadata
            copiedMenu.getMetadata();
            
            List srcElements = copiedMenu.getMenuElements();
            if (srcElements != null)
            {
                List copiedElements = copyMenuElements(srcElements);
                copiedMenu.setMenuElements(copiedElements);
            }
        }
        return copiedMenus;
    }
    
    protected List copyMenuElements(List srcElements)
    {
        List copiedElements = new ArrayList(8);
        Iterator elements = srcElements.iterator();
        while (elements.hasNext())
        {
            MenuElement srcElement = (MenuElement)elements.next();
            // MenuElement copiedElement = newMenuElement();
        }
        return copiedElements;
    }

    protected void copyConstraint(SecurityConstraint srcConstraint, SecurityConstraint dstConstraint)
    {
        dstConstraint.setUsers(srcConstraint.getUsers());                
        dstConstraint.setRoles(srcConstraint.getRoles());
        dstConstraint.setGroups(srcConstraint.getGroups());
        dstConstraint.setPermissions(srcConstraint.getPermissions());        
    }
    
    protected SecurityConstraints copySecurityConstraints(SecurityConstraints source)
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
                SecurityConstraint dstConstraint = newSecurityConstraint();
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
    
    public PageSecurity copyPageSecurity(PageSecurity source) 
    throws JetspeedException
    {
        PageSecurity copy = this.newPageSecurity();
        // this is backwards
        copy.setGlobalSecurityConstraintsRefs(new ArrayList());
        copy.setSecurityConstraintsDefs(new ArrayList());                
        
        copy.setPath(source.getPath());
        copy.setVersion(source.getVersion());        
        
        Iterator defs = source.getSecurityConstraintsDefs().iterator();
        while (defs.hasNext())
        {
            SecurityConstraintsDef def = (SecurityConstraintsDef)defs.next();
            SecurityConstraintsDef defCopy = this.newSecurityConstraintsDef();            
            defCopy.setName(def.getName());                
            List copiedConstraints = new ArrayList();
            defCopy.setSecurityConstraints(copiedConstraints);
            Iterator constraints = def.getSecurityConstraints().iterator();
            while (constraints.hasNext())
            {
                SecurityConstraint srcConstraint = (SecurityConstraint)constraints.next();
                SecurityConstraint dstConstraint = newSecurityConstraint();
                copyConstraint(srcConstraint, dstConstraint);
                copiedConstraints.add(dstConstraint);
            }                                            
            copy.getSecurityConstraintsDefs().add(defCopy);            
        }
        
        Iterator globals = source.getGlobalSecurityConstraintsRefs().iterator();
        while (globals.hasNext())
        {
            String global = (String)globals.next();
            copy.getGlobalSecurityConstraintsRefs().add(global);
        }
        
        return copy;
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
        boolean found = true;
        try
        {
            Folder check = this.getFolder(destinationPath);
        }
        catch (FolderNotFoundException e)
        {
            found = false;
        }
        if (found)
        {
            throw new JetspeedException("Destination already exists");
        }
        Folder dstFolder = this.copyFolder(srcFolder, destinationPath);
        if (owner != null)
        {
            SecurityConstraints constraints = dstFolder.getSecurityConstraints();
            if (constraints == null)
            {
                constraints = this.newSecurityConstraints();
                dstFolder.setSecurityConstraints(constraints);
            }
            dstFolder.getSecurityConstraints().setOwner(owner);
        }
        this.updateFolder(dstFolder);
        
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext())
        {
            Page srcPage = (Page)pages.next();
            String path = this.concatenatePaths(destinationPath, srcPage.getName());
            Page dstPage = this.copyPage(srcPage, path);
            this.updatePage(dstPage);
        }
     
        // TODO: LINKS
        
        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext())
        {
            Folder folder = (Folder)folders.next();
            String newPath = concatenatePaths(destinationPath, folder.getName()); 
            deepCopyFolder(folder, newPath, null);
        }        
    }
        
    protected String concatenatePaths(String base, String path)
    {
        String result = "";
        if (base == null)
        {
            if (path == null)
            {
                return result;
            }
            return path;
        }
        else
        {
            if (path == null)
            {
                return base;
            }
        }
        if (base.endsWith(Folder.PATH_SEPARATOR)) 
        {
            if (path.startsWith(Folder.PATH_SEPARATOR))
            {
                result = base.concat(path.substring(1));
                return result;
            }
        
        }
        else
        {
            if (!path.startsWith(Folder.PATH_SEPARATOR)) 
            {
                result = base.concat(Folder.PATH_SEPARATOR).concat(path);
                return result;
            }
        }
        return base.concat(path);
    }
    
}
