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
package org.apache.jetspeed.page;

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuDefinitionElement;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.BasePageElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageFragment;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * AbstractPageManagerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class AbstractPageManager 
    implements PageManager    
{
    private final static Logger log = LoggerFactory.getLogger(AbstractPageManager.class);
    
    private final static long DEFAULT_NODE_REAPING_INTERVAL = 300000;
    
    private final static String FOLDER_NODE_TYPE = "folder";
    private final static String PAGE_NODE_TYPE = "page";
    private final static String FRAGMENT_NODE_TYPE = "fragment";
    private final static String LINK_NODE_TYPE = "link";

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
    protected Class fragmentReferenceClass;
    protected Class pageFragmentClass;
    protected Class pageTemplateClass;
    protected Class dynamicPageClass;
    protected Class fragmentDefinitionClass;
    protected Class fragmentPropertyClass;

    private IdGenerator generator;

    private boolean permissionsEnabled;

    private boolean constraintsEnabled;
    
    private List<PageManagerEventListener> listeners = new LinkedList<PageManagerEventListener>();

    private long nodeReapingInterval = DEFAULT_NODE_REAPING_INTERVAL;
    
    private volatile Thread nodeReapingThread;

    public AbstractPageManager(IdGenerator generator, boolean permissionsEnabled, boolean constraintsEnabled)
    {
        this.generator = generator;
        this.permissionsEnabled = permissionsEnabled;
        this.constraintsEnabled = constraintsEnabled;
    }
    
    /**
     * Initialize PageManager component.
     */
    public void init()
    {
        // start node reaping deamon thread
        if ((nodeReapingInterval > 0) && (nodeReapingThread == null))
        {
            nodeReapingThread = new Thread(new Runnable()
            {
                public void run()
                {
                    // run while running and reaping thread match
                    Thread runningThread = Thread.currentThread();
                    synchronized (runningThread)
                    {
                        while (nodeReapingThread == runningThread)
                        {
                            try
                            {
                                // wait for reap interval or interrupt and invoke
                                // reaping notification on page manager event listeners
                                runningThread.wait(nodeReapingInterval);
                                if (nodeReapingThread == runningThread)
                                {
                                    notifyReapNodes();
                                }
                            }
                            catch (InterruptedException ie)
                            {
                            }
                        }
                    }
                }   
            }, "PageManagerNodeReapingThread");
            nodeReapingThread.setDaemon(true);
            nodeReapingThread.start();
        }
    }
    
    /**
     * Destroy PageManager component.
     */
    public void destroy()
    {
        // stop node reaping deamon thread
        Thread destroyReapingThread = nodeReapingThread;
        if (destroyReapingThread != null)
        {
            // stop thread
            nodeReapingThread = null;
            synchronized (destroyReapingThread)
            {
                destroyReapingThread.notifyAll();
            }
            // wait for thread stop
            try
            {
                destroyReapingThread.join(nodeReapingInterval);
            }
            catch (InterruptedException ie)
            {
            }
        }
    }
    
    public AbstractPageManager(IdGenerator generator, boolean permissionsEnabled, boolean constraintsEnabled, Map<String,Class<?>> modelClasses)
    {
        this(generator, permissionsEnabled, constraintsEnabled);     

        this.fragmentClass = modelClasses.get("FragmentImpl");
        this.pageClass = modelClasses.get("PageImpl");
        this.folderClass = modelClasses.get("FolderImpl");
        this.linkClass = modelClasses.get("LinkImpl");
        this.pageSecurityClass = modelClasses.get("PageSecurityImpl");
        this.folderMenuDefinitionClass = modelClasses.get("FolderMenuDefinitionImpl");
        this.folderMenuExcludeDefinitionClass = modelClasses.get("FolderMenuExcludeDefinitionImpl");
        this.folderMenuIncludeDefinitionClass = modelClasses.get("FolderMenuIncludeDefinitionImpl");
        this.folderMenuOptionsDefinitionClass = modelClasses.get("FolderMenuOptionsDefinitionImpl");
        this.folderMenuSeparatorDefinitionClass = modelClasses.get("FolderMenuSeparatorDefinitionImpl");
        this.pageMenuDefinitionClass = modelClasses.get("PageMenuDefinitionImpl");
        this.pageMenuExcludeDefinitionClass = modelClasses.get("PageMenuExcludeDefinitionImpl");
        this.pageMenuIncludeDefinitionClass = modelClasses.get("PageMenuIncludeDefinitionImpl");
        this.pageMenuOptionsDefinitionClass = modelClasses.get("PageMenuOptionsDefinitionImpl");
        this.pageMenuSeparatorDefinitionClass = modelClasses.get("PageMenuSeparatorDefinitionImpl");
        this.securityConstraintsClass = modelClasses.get("SecurityConstraintsImpl");
        this.folderSecurityConstraintClass = modelClasses.get("FolderSecurityConstraintImpl");
        this.pageSecurityConstraintClass = modelClasses.get("PageSecurityConstraintImpl");
        this.fragmentSecurityConstraintClass = modelClasses.get("FragmentSecurityConstraintImpl");
        this.linkSecurityConstraintClass = modelClasses.get("LinkSecurityConstraintImpl");
        this.pageSecuritySecurityConstraintClass = modelClasses.get("PageSecuritySecurityConstraintImpl");
        this.securityConstraintsDefClass = modelClasses.get("SecurityConstraintsDefImpl");
        this.fragmentPreferenceClass = modelClasses.get("FragmentPreferenceImpl");
        this.fragmentReferenceClass = modelClasses.get("FragmentReferenceImpl");
        this.pageFragmentClass = modelClasses.get("PageFragmentImpl");
        this.pageTemplateClass = modelClasses.get("PageTemplateImpl");
        this.dynamicPageClass = modelClasses.get("DynamicPageImpl");
        this.fragmentDefinitionClass = modelClasses.get("FragmentDefinitionImpl");
        this.fragmentPropertyClass = modelClasses.get("FragmentPropertyImpl");
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPermissionsEnabled()
     */
    public boolean getPermissionsEnabled()
    {
        return permissionsEnabled;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getConstraintsEnabled()
     */
    public boolean getConstraintsEnabled()
    {
        return constraintsEnabled;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getNodeReapingInterval()
     */
    public long getNodeReapingInterval()
    {
        return nodeReapingInterval;
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
     * @see org.apache.jetspeed.page.PageManager#newPageTemplate(java.lang.String)
     */
    public PageTemplate newPageTemplate(String path)
    {
        PageTemplate pageTemplate = null;
        try
        {
            // factory create the page template and set id/path
            pageTemplate = (PageTemplate)createObject(this.pageTemplateClass);            
            if (!path.startsWith(Folder.PATH_SEPARATOR))
            {
                path = Folder.PATH_SEPARATOR + path;
            }
            if (!path.endsWith(PageTemplate.DOCUMENT_TYPE))
            {
                path += Page.DOCUMENT_TYPE;
            }
            pageTemplate.setPath(path);
            
            // create the default fragment
            pageTemplate.setRootFragment(newFragment());            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page template object for " + this.pageTemplateClass;
            log.error(message, e);
        }
        return pageTemplate;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newDynamicPage(java.lang.String)
     */
    public DynamicPage newDynamicPage(String path)
    {
        DynamicPage dynamicPage = null;
        try
        {
            // factory create the dynamic page and set id/path
            dynamicPage = (DynamicPage)createObject(this.dynamicPageClass);            
            if (!path.startsWith(Folder.PATH_SEPARATOR))
            {
                path = Folder.PATH_SEPARATOR + path;
            }
            if (!path.endsWith(DynamicPage.DOCUMENT_TYPE))
            {
                path += DynamicPage.DOCUMENT_TYPE;
            }
            dynamicPage.setPath(path);
            
            // create the default fragment
            dynamicPage.setRootFragment(newFragment());            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create dynamic page object for " + this.dynamicPageClass;
            log.error(message, e);
        }
        return dynamicPage;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragmentDefinition(java.lang.String)
     */
    public FragmentDefinition newFragmentDefinition(String path)
    {
        FragmentDefinition fragmentDefinition = null;
        try
        {
            // factory create the fragment definition and set id/path
            fragmentDefinition = (FragmentDefinition)createObject(this.fragmentDefinitionClass);            
            if (!path.startsWith(Folder.PATH_SEPARATOR))
            {
                path = Folder.PATH_SEPARATOR + path;
            }
            if (!path.endsWith(FragmentDefinition.DOCUMENT_TYPE))
            {
                path += FragmentDefinition.DOCUMENT_TYPE;
            }
            fragmentDefinition.setPath(path);
            
            // create the default portlet fragment
            fragmentDefinition.setRootFragment(newPortletFragment());            
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment definition object for " + this.dynamicPageClass;
            log.error(message, e);
        }
        return fragmentDefinition;        
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
            fragment.setId(generator.getNextPeid());
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment object for " + this.fragmentClass;
            log.error(message, e);
            // throw new NodeException(message, e);
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
            fragment.setId(generator.getNextPeid());
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment object for " + this.fragmentClass;
            log.error(message, e);
            // throw new NodeException(message, e);
        }
        return fragment;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newFragmentReference()
     */
    public FragmentReference newFragmentReference()
    {
        FragmentReference fragment = null;
        try
        {
            fragment = (FragmentReference)createObject(this.fragmentReferenceClass);
            fragment.setId(generator.getNextPeid());
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create page object for " + this.fragmentReferenceClass;
            log.error(message, e);
            // throw new NodeException(message, e);
        }
        return fragment;        
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#newPageFragment()
     */
    public PageFragment newPageFragment()
    {
        PageFragment fragment = null;
        try
        {
            fragment = (PageFragment)createObject(this.pageFragmentClass);
            fragment.setId(generator.getNextPeid());
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment object for " + this.pageFragmentClass;
            log.error(message, e);
            // throw new NodeException(message, e);
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
     * newFragmentPreference - creates a new fragment preference
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
            String message = "Failed to create fragment preference object for " + this.fragmentPropertyClass;
            log.error(message, e);
        }
        return null;
    }

    /**
     * newFragmentProperty - creates a new fragment property
     *
     * @return a newly created FragmentProperty object
     */
    public FragmentProperty newFragmentProperty()
    {
        try
        {
            return (FragmentProperty)createObject(this.fragmentPropertyClass);
        }
        catch (ClassCastException e)
        {
            String message = "Failed to create fragment property object for " + this.fragmentPropertyClass;
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#shutdown()
     */
    public void shutdown()
    {
        // nothing to shutdown by default
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#isDistributed()
     */
    public boolean isDistributed()
    {
        return false;
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
        List<PageManagerEventListener> listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList<PageManagerEventListener>(listeners);
        }
        // notify listeners
        for (PageManagerEventListener listener : listenersList)
        {
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
        List<PageManagerEventListener> listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList<PageManagerEventListener>(listeners);
        }
        // notify listeners
        for (PageManagerEventListener listener : listenersList)
        {
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
        List<PageManagerEventListener> listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList<PageManagerEventListener>(listeners);
        }
        // notify listeners
        for (PageManagerEventListener listener : listenersList)
        {
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
        
    /**
     * notifyReapNodes - notify page manager event listeners of
     *                   reap nodes event
     */
    public void notifyReapNodes()
    {
        // copy listeners list to reduce synchronization deadlock
        List<PageManagerEventListener> listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList<PageManagerEventListener>(listeners);
        }
        // notify listeners
        for (PageManagerEventListener listener : listenersList)
        {
            try
            {
                listener.reapNodes(nodeReapingInterval);
            }
            catch (Exception e)
            {
                log.error("Failed to notify page manager event listener", e);
            }
        }
    }
        
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String)
     */
    public Folder copyFolder(Folder source, String path)
    throws NodeException
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
        folder.setDocumentOrder(this.<String>createList());
        for (String name : source.getDocumentOrder())
        {
            folder.getDocumentOrder().add(name);
        }

        // copy menu definitions
        List<MenuDefinition> menus = source.getMenuDefinitions();
        if (menus != null)
        {
            List<MenuDefinition> copiedMenus = copyMenuDefinitions(FOLDER_NODE_TYPE, menus);
            folder.setMenuDefinitions(copiedMenus);
        }        
                
        return folder;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPage(org.apache.jetspeed.om.page.Page, java.lang.String)
     */
    public Page copyPage(Page source, String path)
        throws NodeException
    {
        return copyPage(source, path, false);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPage(org.apache.jetspeed.om.page.Page, java.lang.String, boolean)
     */
    public Page copyPage(Page source, String path, boolean copyIds)
        throws NodeException
    {
        // create the new page and copy attributes
        Page page = newPage(path);
        copyPageAttributes(source, copyIds, page);
        page.setHidden(source.isHidden());
        return page;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPageTemplate(org.apache.jetspeed.om.page.PageTemplate, java.lang.String)
     */
    public PageTemplate copyPageTemplate(PageTemplate source, String path)
        throws NodeException
    {
        return copyPageTemplate(source, path, false);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPageTemplate(org.apache.jetspeed.om.page.PageTemplate, java.lang.String, boolean)
     */
    public PageTemplate copyPageTemplate(PageTemplate source, String path, boolean copyIds)
        throws NodeException
    {
        // create the new page template and copy attributes
        PageTemplate pageTemplate = newPageTemplate(path);
        copyPageAttributes(source, copyIds, pageTemplate);
        return pageTemplate;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyDynamicPage(org.apache.jetspeed.om.page.DynamicPage, java.lang.String)
     */
    public DynamicPage copyDynamicPage(DynamicPage source, String path)
        throws NodeException
    {
        return copyDynamicPage(source, path, false);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyDynamicPage(org.apache.jetspeed.om.page.DynamicPage, java.lang.String, boolean)
     */
    public DynamicPage copyDynamicPage(DynamicPage source, String path, boolean copyIds)
        throws NodeException
    {
        // create the new dynamic page and copy attributes
        DynamicPage dynamicPage = newDynamicPage(path);
        copyPageAttributes(source, copyIds, dynamicPage);
        dynamicPage.setHidden(source.isHidden());
        dynamicPage.setContentType(source.getContentType());
        dynamicPage.setInheritable(source.isInheritable());
        return dynamicPage;
    }

    public FragmentDefinition copyFragmentDefinition(FragmentDefinition source, String path)
        throws NodeException
    {
        return copyFragmentDefinition(source, path, false);
    }
    
    public FragmentDefinition copyFragmentDefinition(FragmentDefinition source, String path, boolean copyIds)
        throws NodeException
    {
        // create the new fragment definition and copy attributes
        
        FragmentDefinition fragmentDefinition = newFragmentDefinition(path);
        copyFragmentsAttributes(source, copyIds, fragmentDefinition);
        return fragmentDefinition;
    }

    /**
     * Copy shared fragments attributes.
     * 
     * @param source source fragments
     * @param copyIds flag indicating whether to copy or preserve fragment ids
     * @param dest destination fragments
     * @throws NodeException on error creating fragments
     */
    protected void copyFragmentsAttributes(BaseFragmentsElement source, boolean copyIds, BaseFragmentsElement dest)
        throws NodeException
    {
        // create the new page and copy attributes
        dest.setTitle(source.getTitle());
        dest.setShortTitle(source.getShortTitle());
        dest.setVersion(source.getVersion());
    
        // copy locale specific metadata
        dest.getMetadata().copyFields(source.getMetadata().getFields());
    
        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(PAGE_NODE_TYPE, srcSecurity);
            dest.setSecurityConstraints(copiedSecurity);
        }    

        // copy fragments
        BaseFragmentElement root = copyFragment(source.getRootFragment(), null, copyIds);
        dest.setRootFragment(root);
    }

    /**
     * Copy shared page attributes.
     * 
     * @param source source page
     * @param copyIds flag indicating whether to copy or preserve fragment ids
     * @param dest destination page
     * @throws NodeException on error creating fragments
     */
    protected void copyPageAttributes(BasePageElement source, boolean copyIds, BasePageElement dest)
        throws NodeException
    {
        // copy fragments attributes
        copyFragmentsAttributes(source, copyIds, dest);
        
        // copy page attributes
        dest.setDefaultDecorator(source.getDefaultDecorator(Fragment.LAYOUT), Fragment.LAYOUT);
        dest.setDefaultDecorator(source.getDefaultDecorator(Fragment.PORTLET), Fragment.PORTLET);
        dest.setSkin(source.getSkin());
    
        // copy menu definitions
        List<MenuDefinition> menus = source.getMenuDefinitions();
        if (menus != null)
        {
            List<MenuDefinition> copiedMenus = copyMenuDefinitions(PAGE_NODE_TYPE, menus);
            dest.setMenuDefinitions(copiedMenus);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFragment(org.apache.jetspeed.om.page.Fragment, java.lang.String)
     */
    public BaseFragmentElement copyFragment(BaseFragmentElement source, String name)
        throws NodeException
    {
        return copyFragment(source, name, false);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyFragment(org.apache.jetspeed.om.page.Fragment, java.lang.String, boolean)
     */
    public BaseFragmentElement copyFragment(BaseFragmentElement source, String name, boolean copyIds)
        throws NodeException
    {
        // create the new fragment and copy attributes
        BaseFragmentElement copy;
        if (source instanceof Fragment)
        {
            copy = newFragment();
        }
        else if (source instanceof FragmentReference)
        {
            copy = newFragmentReference();
        }
        else if (source instanceof PageFragment)
        {
            copy = newPageFragment();
        }
        else
        {
            throw new IllegalArgumentException("Unsupported fragment type: "+((source != null) ? source.getClass().getName() : "null"));
        }

        if (copyIds)
        {
            copy.setId(source.getId());
        }
        copy.setDecorator(source.getDecorator());
        copy.setLayoutColumn(source.getLayoutColumn());
        copy.setLayoutHeight(source.getLayoutHeight());
        copy.setLayoutRow(source.getLayoutRow());
        copy.setLayoutSizes(source.getLayoutSizes());
        copy.setLayoutX(source.getLayoutX());
        copy.setLayoutY(source.getLayoutY());
        copy.setLayoutZ(source.getLayoutZ());
        copy.setLayoutWidth(source.getLayoutWidth());
        copy.setMode(source.getMode());
        copy.setShortTitle(source.getShortTitle());
        copy.setSkin(source.getSkin());
        copy.setState(source.getState());
        copy.setTitle(source.getTitle());
        
        // copy security constraints
        SecurityConstraints srcSecurity = source.getSecurityConstraints();        
        if ((srcSecurity != null) && !srcSecurity.isEmpty())
        {
            SecurityConstraints copiedSecurity = copySecurityConstraints(FRAGMENT_NODE_TYPE, srcSecurity);
            copy.setSecurityConstraints(copiedSecurity);
        }
        
        // copy properties, (only properties for global and
        // current user/group/role specific values copied)
        for (FragmentProperty prop : source.getProperties())
        {
            String propName = prop.getName();
            String propScope = prop.getScope();
            String propScopeValue = prop.getScopeValue();
            if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED || 
                (propScope == null) ||
                (!propScope.equals(FragmentProperty.GROUP_PROPERTY_SCOPE) && !propScope.equals(FragmentProperty.ROLE_PROPERTY_SCOPE)))
            {
                if (copy.getProperty(propName, propScope, propScopeValue) == null)
                {
                    copy.setProperty(propName, propScope, propScopeValue, prop.getValue());
                }
            }
        }
                  
        // copy preferences
        for (FragmentPreference pref : source.getPreferences())
        {
            FragmentPreference newPref = this.newFragmentPreference();
            newPref.setName(pref.getName());
            newPref.setReadOnly(pref.isReadOnly());
            newPref.setValueList(this.<String>createList());
            for (String value : pref.getValueList())
            {
                newPref.getValueList().add(value);
            }
            copy.getPreferences().add(newPref);
        }

        if (source instanceof Fragment)
        {
            Fragment copyFragment = (Fragment)copy; 
            Fragment sourceFragment = (Fragment)source; 
            if (name == null)
            {
                name = sourceFragment.getName();
            }
            copyFragment.setName(name);
            copyFragment.setType(sourceFragment.getType());

            // recursively copy fragments
            for (BaseFragmentElement fragment : sourceFragment.getFragments())
            {
                BaseFragmentElement copiedFragment = copyFragment(fragment, null, copyIds);
                copyFragment.getFragments().add(copiedFragment);
            }
        }
        else if (source instanceof FragmentReference)
        {
            FragmentReference copyFragment = (FragmentReference)copy; 
            FragmentReference sourceFragment = (FragmentReference)source;
            copyFragment.setRefId(sourceFragment.getRefId());            
        }

        return copy;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyLink(org.apache.jetspeed.om.page.Link, java.lang.String)
     */
    public Link copyLink(Link source, String path)
    throws NodeException
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#copyPageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public PageSecurity copyPageSecurity(PageSecurity source) 
    throws NodeException
    {
        // create the new page security document and copy attributes
        PageSecurity copy = this.newPageSecurity();
        copy.setPath(source.getPath());
        copy.setVersion(source.getVersion());        

        // copy security constraint defintions
        copy.setSecurityConstraintsDefs(this.<SecurityConstraintsDef>createList());
        for (SecurityConstraintsDef def : source.getSecurityConstraintsDefs())
        {
            SecurityConstraintsDef defCopy = this.newSecurityConstraintsDef();
            defCopy.setName(def.getName());
            List<SecurityConstraint> copiedConstraints = createList();
            for (SecurityConstraint srcConstraint : def.getSecurityConstraints())
            {
                SecurityConstraint dstConstraint = newPageSecuritySecurityConstraint();
                copyConstraint(srcConstraint, dstConstraint);
                copiedConstraints.add(dstConstraint);
            }                                            
            defCopy.setSecurityConstraints(copiedConstraints);
            copy.getSecurityConstraintsDefs().add(defCopy);
        }
        
        // copy global security constraint references
        copy.setGlobalSecurityConstraintsRefs(this.<String>createList());
        for (String global : source.getGlobalSecurityConstraintsRefs())
        {
            copy.getGlobalSecurityConstraintsRefs().add(global);
        }
        
        return copy;
    }

    protected List<MenuDefinition> copyMenuDefinitions(String type, List<MenuDefinition> srcMenus)
    {
        List<MenuDefinition> copiedMenus = createList();
        for (MenuDefinition srcMenu : srcMenus)
        {
            MenuDefinition copiedMenu = (MenuDefinition)copyMenuElement(type, srcMenu);
            if (copiedMenu != null)
            {
                copiedMenus.add(copiedMenu);
            }
        }
        return copiedMenus;
    }
    
    protected MenuDefinitionElement copyMenuElement(String type, MenuDefinitionElement srcElement)
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
            List<MenuDefinitionElement> elements = source.getMenuElements();
            if (elements != null)
            {
                List<MenuDefinitionElement> copiedElements = createList();
                for (MenuDefinitionElement element : elements)
                {
                    MenuDefinitionElement copiedElement = copyMenuElement(type, element);
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
            List<SecurityConstraint> copiedConstraints = createList();
            for (SecurityConstraint srcConstraint : source.getSecurityConstraints())
            {
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
            List<String> copiedRefs = createList();
            for (String constraintsRef : source.getSecurityConstraintsRefs())
            {
                copiedRefs.add(constraintsRef);
            }
            security.setSecurityConstraintsRefs(copiedRefs);            
        }
        return security;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepCopyFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String)
     */
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner)
        throws NodeException
    {
        deepCopyFolder(srcFolder, destinationPath, owner, false);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepCopyFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String, boolean)
     */
    public void deepCopyFolder(Folder srcFolder, String destinationPath, String owner, boolean copyIds)
        throws NodeException
    {
        PageManagerUtils.deepCopyFolder(this, srcFolder, destinationPath, owner, copyIds);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepMergeFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String)
     */
    public void deepMergeFolder(Folder srcFolder, String destinationPath, String owner)
        throws NodeException
    {
        deepMergeFolder(srcFolder, destinationPath, owner, false);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#deepMergeFolder(org.apache.jetspeed.om.folder.Folder, java.lang.String, java.lang.String, boolean)
     */
    public void deepMergeFolder(Folder srcFolder, String destinationPath, String owner, boolean copyIds)
        throws NodeException
    {
        PageManagerUtils.deepMergeFolder(this, srcFolder, destinationPath, owner, copyIds);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getUserPage(java.lang.String, java.lang.String)
     */
    public Page getUserPage(String userName, String pageName)
    throws PageNotFoundException, NodeException
    {
        return this.getPage(Folder.USER_FOLDER + userName + Folder.PATH_SEPARATOR + pageName);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getUserFolder(java.lang.String)
     */
    public Folder getUserFolder(String userName) 
        throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        return this.getFolder(Folder.USER_FOLDER + userName);        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#folderExists(java.lang.String)
     */
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#pageExists(java.lang.String)
     */
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#pageTemplateExists(java.lang.String)
     */
    public boolean pageTemplateExists(String pageName)
    {
        try
        {
            getPageTemplate(pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#dynamicPageExists(java.lang.String)
     */
    public boolean dynamicPageExists(String pageName)
    {
        try
        {
            getDynamicPage(pageName);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#fragmentDefinitionExists(java.lang.String)
     */
    public boolean fragmentDefinitionExists(String name)
    {
        try
        {
            getFragmentDefinition(name);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#linkExists(java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#userFolderExists(java.lang.String)
     */
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#userPageExists(java.lang.String, java.lang.String)
     */
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#cleanupRequestCache()
     */
    public void cleanupRequestCache()
    {
        // nothing to cleanup by default
    }
    
    /**
     * Creates a user's home page from the roles of the current user.
     * The use case: when a portal is setup to use shared pages, but then
     * the user attempts to customize. At this point, we create the new page(s) for the user.
     * 
     * @param subject
     */
    public void createUserHomePagesFromRoles(Subject subject)
    throws NodeException
    {
        PageManagerUtils.createUserHomePagesFromRoles(this, subject);
    }

    public FragmentPropertyManagement getFragmentPropertyManager()
    {
    	return null; 
    }
}
