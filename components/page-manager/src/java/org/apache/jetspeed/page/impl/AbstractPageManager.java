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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PropertyImpl;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.ProfiledPageContext;
import org.apache.jetspeed.profiler.ProfileLocator;

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
    protected Class propertyClass = PropertyImpl.class;
    protected IdGenerator generator = null;

    public AbstractPageManager(IdGenerator generator)
    {    
        this.generator = generator;
    }
    
    public AbstractPageManager(IdGenerator generator, List modelClasses)
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
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#newPage()
     */
    public Page newPage()
    {
        Page page = null;
        try
        {
            // factory create the page
            page = (Page)createObject(this.pageClass);            
            page.setId(generator.getNextPeid());
            
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
     * @see org.apache.jetspeed.services.page.PageManagerService#newFragment()
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
     * @see org.apache.jetspeed.services.page.PageManagerService#newProperty()
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

    public Object createObject(Class classe)
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
    
    protected void populateProfiledPageContext(ProfiledPageContext pageContext, Folder folder, Page page, NodeSet siblingPages, Folder parentFolder, NodeSet siblingFolders, NodeSet rootLinks, NodeSet documentSets, Map documentSetNodeSets)
    {
        // debug profiled page context elements
        if (log.isDebugEnabled())
        {
            log.debug("populateProfiledPageContext(), folder = " + folder + ", url = " + folder.getUrl());
            log.debug("populateProfiledPageContext(), page = " + page + ", url = " + page.getUrl());
            if ((siblingPages != null) && (siblingPages.size() > 0))
            {
                Iterator debugIter = siblingPages.iterator();
                while (debugIter.hasNext())
                {
                    Page debug = (Page) debugIter.next();
                    log.debug("populateProfiledPageContext(), siblingPage = " + debug + ", url = " + debug.getUrl());
                }
            }
            else
                log.debug("populateProfiledPageContext(), siblingPages = null/empty");
            log.debug("populateProfiledPageContext(), parentFolder = " + parentFolder + ", url = " + ((parentFolder != null) ? parentFolder.getUrl() : "null"));
            if ((siblingFolders != null) && (siblingFolders.size() > 0))
            {
                Iterator debugIter = siblingFolders.iterator();
                while (debugIter.hasNext())
                {
                    Folder debug = (Folder) debugIter.next();
                    log.debug("populateProfiledPageContext(), siblingFolder = " + debug + ", url = " + debug.getUrl());
                }
            }
            else
                log.debug("populateProfiledPageContext(), siblingFolders = null/empty");
            if ((rootLinks != null) && (rootLinks.size() > 0))
            {
                Iterator debugIter = rootLinks.iterator();
                while (debugIter.hasNext())
                {
                    Link debug = (Link) debugIter.next();
                    log.debug("populateProfiledPageContext(), rootLink = " + debug + ", url = " + debug.getUrl());
                }
            }
            else
                log.debug("populateProfiledPageContext(), rootLinks = null/empty");
            if ((documentSets != null) && (documentSets.size() > 0) && (documentSetNodeSets != null) && (documentSetNodeSets.size() > 0))
            {
                Iterator debugIter = documentSets.iterator();
                while (debugIter.hasNext())
                {
                    DocumentSet debug = (DocumentSet) debugIter.next();
                    NodeSet debugNodes = (NodeSet) documentSetNodeSets.get(debug);
                    String debugMessage = "document set " + debug.getDocumentSetName() + " = {";
                    Iterator nodesIter = debugNodes.iterator();
                    if (nodesIter.hasNext())
                    {
                        debugMessage += ((Node) nodesIter.next()).getUrl();
                    }
                    while (nodesIter.hasNext())
                    {
                        debugMessage += ", " + ((Node) nodesIter.next()).getUrl();
                    }
                    debugMessage += "}, url = " + debug.getUrl();
                    log.debug("populateProfiledPageContext(), " + debugMessage);
                }
            }
            else
                log.debug("populateProfiledPageContext(), documentSets/documentSetNodeSets = null/empty");
        }

        // populate supplied page context object
        pageContext.setPage(page);
        pageContext.setFolder(folder);
        pageContext.setSiblingPages(siblingPages);
        pageContext.setParentFolder(parentFolder);
        pageContext.setSiblingFolders(siblingFolders);
        pageContext.setRootLinks(rootLinks);
        if (documentSets != null)
        {
            Iterator documentSetIter = documentSets.iterator();
            while (documentSetIter.hasNext())
            {
                DocumentSet documentSet = (DocumentSet) documentSetIter.next();
                NodeSet documentSetNodes = (NodeSet) documentSetNodeSets.get(documentSet);
                pageContext.setDocumentSet(documentSet.getDocumentSetName(), documentSet, documentSetNodes);
            }
        }
    }

    protected void copyProfiledPageContext(ProfiledPageContext from, ProfiledPageContext to)
    {
        // copy page context elements
        to.setPage(from.getPage());
        to.setFolder(from.getFolder());
        to.setSiblingPages(from.getSiblingPages());
        to.setParentFolder(from.getParentFolder());
        to.setSiblingFolders(from.getSiblingFolders());
        to.setRootLinks(from.getRootLinks());
        Iterator documentSetNamesIter = from.getDocumentSetNames();
        while (documentSetNamesIter.hasNext())
        {
            String documentSetName = (String) documentSetNamesIter.next();
            to.setDocumentSet(documentSetName, from.getDocumentSet(documentSetName), from.getDocumentSetNodes(documentSetName));
        }
    }

    protected ProfileLocator selectPageProfileLocator(Map profileLocators)
    {
        // select page profile locator from session/principal profile locators
        return (ProfileLocator) profileLocators.get(ProfileLocator.PAGE_LOCATOR);
    }

    protected ProfileLocator selectNavigationProfileLocator(String profileLocatorName, Map profileLocators)
    {
        // select navigation profile locator from session/principal profile locators
        ProfileLocator locator = null;
        if (profileLocatorName != null)
        {
            locator = (ProfileLocator) profileLocators.get(profileLocatorName);
        }
        else
        {
            locator = (ProfileLocator) profileLocators.get(ProfileLocator.DOCSET_LOCATOR);
            if (locator == null)
            {
                locator = (ProfileLocator) profileLocators.get(ProfileLocator.PAGE_LOCATOR);
            }
        }
        return locator;
    }
}
