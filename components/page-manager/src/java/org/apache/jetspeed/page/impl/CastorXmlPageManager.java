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

//standard java stuff
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;
import org.apache.jetspeed.profiler.ProfileLocator;

/**
 * This service is responsible for loading and saving PSML pages serialized to
 * disk
 * 
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta </a>
 * @author <a href="mailto:weaver@apache.org">Scott T Weaver </a>
 * @version $Id$
 */
public class CastorXmlPageManager extends AbstractPageManager implements PageManager
{
    private final static Log log = LogFactory.getLog(CastorXmlPageManager.class);

    protected final static String CONFIG_EXT = "ext";

    private DocumentHandlerFactory handlerFactory;

    private FolderHandler folderHandler;

    // default configuration values

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory hanlderFactory,
            FolderHandler folderHandler ) throws FileNotFoundException
    {
        super(generator);
        this.handlerFactory = hanlderFactory;
        this.folderHandler = folderHandler;

    }

    public CastorXmlPageManager( IdGenerator generator, DocumentHandlerFactory hanlderFactory,
            FolderHandler folderHandler, List modelClasses ) throws FileNotFoundException
    {
        super(generator, modelClasses);
        this.handlerFactory = hanlderFactory;
        this.folderHandler = folderHandler;
    }

    /**
     * 
     * <p>
     * getPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPage(org.apache.jetspeed.profiler.ProfileLocator)
     * @param locator
     * @return @throws
     *         PageNotFoundException
     * @throws PageNotFoundException
     * @throws NodeException
     */
    public Page getPage( ProfileLocator locator ) throws PageNotFoundException, NodeException
    {
        return getPage(locator.getValue("page"));
    }

    /**
     * 
     * <p>
     * getPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     * @param id
     * @return @throws
     *         PageNotFoundException
     * @throws IllegalStateException
     *             if the page could be inserted into the FileCache.
     */
    public Page getPage( String id ) throws PageNotFoundException, NodeException
    {
        return (Page) addParent(handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).getDocument(id), id);
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#registerPage(org.apache.jetspeed.om.page.Page)
     */
    public void registerPage( Page page ) throws JetspeedException
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

        handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).updateDocument(page);
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage( Page page ) throws JetspeedException
    {
        registerPage(page);
    }

    /**
     * @throws UnsupportedDocumentTypeException
     * @throws FailedToDeleteDocumentException
     * @throws DocumentNotFoundException
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage( Page page ) throws DocumentNotFoundException, FailedToDeleteDocumentException,
            UnsupportedDocumentTypeException
    {
        String id = page.getId();

        if (id == null)
        {
            log.warn("Unable to remove page with null Id from disk");
            return;
        }

        handlerFactory.getDocumentHandler(Page.DOCUMENT_TYPE).removeDocument(page);

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
     * @throws DocumentNotFoundException
     * @throws UnsupportedDocumentTypeException
     * @throws 
     * @throws NodeException
     * @throws FolderNotFoundException
     */
    public Link getLink( String name ) throws DocumentNotFoundException, UnsupportedDocumentTypeException, FolderNotFoundException, NodeException
    {
        return (Link) addParent(handlerFactory.getDocumentHandler(Link.DOCUMENT_TYPE).getDocument(name), name);
    }

    /**
     * <p>
     * getFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getFolder(java.lang.String)
     * @param folderPath
     * @return @throws
     *         DocumentException
     * @throws FolderNotFoundException
     * @throws NodeException
     * @throws InvalidFolderException
     * @throws IOException
     */
    public Folder getFolder( String folderPath ) throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        return folderHandler.getFolder(folderPath);
    }

    protected Node addParent( Node childNode, String nodePath ) throws NodeException, InvalidFolderException
    {
        int lastSlash = nodePath.indexOf("/");
        if (lastSlash > -1)
        {
            childNode.setParent(folderHandler.getFolder(nodePath.substring(0, lastSlash)));
        }
        else
        {
            childNode.setParent(folderHandler.getFolder("/"));
        }

        return childNode;

    }
}