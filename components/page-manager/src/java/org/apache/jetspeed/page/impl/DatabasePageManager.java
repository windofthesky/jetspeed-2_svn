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
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.picocontainer.Startable;

/**
 * DatabasePageManagerService
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class DatabasePageManager extends AbstractPageManager implements PageManager, Startable
{
    protected final static Log log = LogFactory.getLog(DatabasePageManager.class);
    private PersistenceStore persistenceStore;
    // TODO: this should eventually use a system cach like JCS
    private Map pageCache = new HashMap();

	/**
	 * 
	 * @param pContainer PersistenceStoreContainer that will be used to look up 
	 * the <code>PersistenceStore</code> to use for persistence operations
	 * @param generator ID generator that will be used to generate unique page ids
	 * @param storeName Name of the <code>PersistenceStore</code> that will be used
	 * by the <code>PersistenceStoreContainer.getStoreForThread(String)</code>
	 * method to obtain the current persistence for this thread.
	 */
    public DatabasePageManager(PersistenceStore persistenceStore, IdGenerator generator)
    {
        super(generator);
        this.persistenceStore = persistenceStore;
        
    }

    public void start()
    {
        //PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
        // TODO: use new stuff String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");
        // String pluginName = "jetspeed";

        // plugin = ps.getPersistencePlugin(pluginName);

    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#getPage(org.apache.jetspeed.profiler.ProfileLocator)
     */
    public Page getPage(ProfileLocator locator)
    {
        return getPage(locator.getValue("page"));
    }    

    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#getPage(java.lang.String)
     */
    public Page getPage(String id)
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
            Page page = (Page) persistenceStore.getObjectByQuery( q);

            pageCache.put(id, page);
            return page;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#listPages()
     */
    public List listPages()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#registerPage(org.apache.jetspeed.om.page.Page)
     */
    public void registerPage(Page page) throws JetspeedException
    {
        // sanity checks
        if (page == null)
        {
            log.warn("Recieved null page to register");
            return;
        }

        String id = page.getId();

        if (id == null)
        {
            page.setId(generator.getNextPeid());
            id = page.getId();
            log.warn("Page with no Id, created new Id : " + id);
        }

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws JetspeedException, PageNotUpdatedException
    {
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws PageNotRemovedException
    {
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
        // TODO Auto-generated method stub
        return null;
    }
}
