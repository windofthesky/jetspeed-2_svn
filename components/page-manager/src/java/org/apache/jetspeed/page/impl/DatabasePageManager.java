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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.psml.ContentPageImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;

/**
 * DatabasePageManagerService
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class DatabasePageManager extends AbstractPageManager implements PageManager
{
    protected final static Log log = LogFactory.getLog(DatabasePageManager.class);
    private PersistenceStore persistenceStore;
    // TODO: this should eventually use a system cach like JCS
    private Map pageCache = new HashMap();

    /**
     * 
     * @param pContainer
     *            PersistenceStoreContainer that will be used to look up the
     *            <code>PersistenceStore</code> to use for persistence
     *            operations
     * @param generator
     *            ID generator that will be used to generate unique page ids
     * @param storeName
     *            Name of the <code>PersistenceStore</code> that will be used
     *            by the
     *            <code>PersistenceStoreContainer.getStoreForThread(String)</code>
     *            method to obtain the current persistence for this thread.
     */
    public DatabasePageManager( PersistenceStore persistenceStore, IdGenerator generator )
    {
        super(generator, false, false);
        this.persistenceStore = persistenceStore;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#getPage(java.lang.String)
     */
    public Page getPage( String id ) throws PageNotFoundException
    {
        if (pageCache.containsKey(id))
        {
            return (Page) pageCache.get(id);
        }
        else
        {
            Filter filter = persistenceStore.newFilter();
            filter.addEqualTo("id", id);
            Object q = persistenceStore.newQuery(pageClass, filter);
            persistenceStore.getTransaction().begin();
            Page page = (Page) persistenceStore.getObjectByQuery(q);
            if (page == null)
            {
                throw new PageNotFoundException("Jetspeed PSML page not found: " + id);
            }

            pageCache.put(id, page);
            return page;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage( Page page ) throws JetspeedException, PageNotUpdatedException
    {
        // sanity checks
        if (page == null)
        {
            log.warn("Recieved null page to update");
            return;
        }

        // unwrap page to be updated
        if (page instanceof ContentPageImpl)
        {
            page = ((ContentPageImpl)page).getPage();
        }

        // register page
        String id = page.getId();
        if (id == null)
        {
            page.setId(generator.getNextPeid());
            id = page.getId();
            log.warn("Page with no Id, created new Id : " + id);
        }

        // update page
        try
        {
            persistenceStore.getTransaction().begin();
            persistenceStore.lockForWrite(page);
            persistenceStore.getTransaction().commit();
        }
        catch (Exception e)
        {
            String msg = "Unable to update Page.";
            log.error(msg, e);
            throw new PageNotUpdatedException(msg, e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage( Page page ) throws PageNotRemovedException
    {
        // unwrap page to be removed
        if (page instanceof ContentPageImpl)
        {
            page = ((ContentPageImpl)page).getPage();
        }

        // remove page
        if (pageCache.containsKey(page.getId()))
        {
            pageCache.remove(pageCache.get(page.getId()));
        }
        try
        {
            persistenceStore.getTransaction().begin();
            persistenceStore.deletePersistent(page);
            persistenceStore.getTransaction().commit();
        }
        catch (Exception e)
        {
            String msg = "Unable to remove Page.";
            log.error(msg, e);
            throw new PageNotRemovedException(msg, e);
        }
    }

    /**
     * <p>
     * getFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getFolder(java.lang.String)
     * @param folderPath
     * @return
     */
    public Folder getFolder( String folderPath )
    {
        throw new UnsupportedOperationException("Not supported by DB impl yet");
    }

    /**
     * <p>
     * updateFolder
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updateFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void updateFolder(Folder folder) throws JetspeedException
    {
        throw new UnsupportedOperationException("Not supported by DB impl yet");
    }

    /**
     * <p>
     * removeFolder
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removeFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void removeFolder(Folder folder) throws JetspeedException
    {
        throw new UnsupportedOperationException("Not supported by DB impl yet");
    }

    /**
     * <p>
     * getLink
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getLink(java.lang.String)
     * @param name
     * @return @throws
     *         DocumentNotFoundException
     */
    public Link getLink( String name ) throws DocumentNotFoundException
    {
        throw new UnsupportedOperationException("Not supported by DB impl yet");
    }

    /**
     * <p>
     * updateLink
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#updateLink(org.apache.jetspeed.om.page.Link)
     */
    public void updateLink(Link link) throws JetspeedException
    {
        throw new UnsupportedOperationException("Not supported by DB impl yet");
    }

    /**
     * <p>
     * removeLink
     * </p>
     * 
     * @see org.apache.jetspeed.services.page.PageManagerService#removeLink(org.apache.jetspeed.om.page.Link)
     */
    public void removeLink(Link link) throws JetspeedException
    {
        throw new UnsupportedOperationException("Not supported by DB impl yet");
    }

    /**
     * <p>
     * getPageSecurity
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity()
     * @return @throws
     *         DocumentNotFoundException
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException
    {
        throw new UnsupportedOperationException("Not supported by DB impl yet");
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getContentPage(java.lang.String)
     */
    public ContentPage getContentPage(String path) throws PageNotFoundException, NodeException
    {
        return new ContentPageImpl(getPage(path));
    }
}
