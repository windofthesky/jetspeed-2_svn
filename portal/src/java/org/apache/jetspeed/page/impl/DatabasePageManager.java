/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.page.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotRemovedException;
import org.apache.jetspeed.page.PageNotUpdatedException;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
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

    private PersistencePlugin plugin;

    private PersistencePlugin originalPlugin;

    private String originalAlias;

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
    public DatabasePageManager(PersistenceStoreContainer pContainer, IdGenerator generator, String storeName)
    {
        super(generator);
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
            LookupCriteria c = plugin.newLookupCriteria();
            c.addEqualTo("id", id);
            Object q = plugin.generateQuery(pageClass, c);
            Page page = (Page) plugin.getObjectByQuery(pageClass, q);

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
            plugin.prepareForUpdate(page);
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
            plugin.prepareForDelete(page);
        }
        catch (Exception e)
        {
            String msg = "Unable to remove Page.";
            log.error(msg, e);
            throw new PageNotRemovedException(msg, e);
        }
    }

}
