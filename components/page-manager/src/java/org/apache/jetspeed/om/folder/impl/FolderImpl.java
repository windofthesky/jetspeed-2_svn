/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.folder.impl;

import java.io.File;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.AbstractNode;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeOrderCompartaor;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.NodeSetImpl;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;

/**
 * FolderImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class FolderImpl extends AbstractNode implements Folder
{
    
    public static final String FALLBACK_DEFAULT_PAGE = "default-page.psml";

    private static final String PAGE_NOT_FOUND_PAGE = "page_not_found.psml";
    private String defaultTheme;
    private NodeSet allNodes;
    private File directory;
    private DocumentHandlerFactory handlerFactory;
    private FolderMetaData metadata;
    private FolderHandler folderHandler;
    
    private static final Log log = LogFactory.getLog(FolderImpl.class);

    public FolderImpl( String path, FolderMetaData metadata, DocumentHandlerFactory handlerFactory,
            FolderHandler folderHandler )
    {
        this.metadata = metadata;
        this.metadata.setParent(this);
        this.handlerFactory = handlerFactory;
        this.folderHandler = folderHandler;
        setId(path);
        setPath(path);
    }

    public FolderImpl( String path, DocumentHandlerFactory handlerFactory, FolderHandler folderHandler )
    {
        this.metadata = new FolderMetaDataImpl();
        this.metadata.setTitle(path);
        this.metadata.setParent(this);
        this.handlerFactory = handlerFactory;
        this.folderHandler = folderHandler;
        setId(path);
        setPath(path);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultPage(boolean)
     */
    public String getDefaultPage(boolean allowDefaulting)
    {
        try
        {   
            String defaultPage = metadata.getDefaultPage();
            if(defaultPage == null)
            {
                defaultPage = FALLBACK_DEFAULT_PAGE;
            }
            return getPage(defaultPage).getName();
        }
        catch (NodeException e)
        {
            if (allowDefaulting)
            {
                try
                {
                    Iterator pagesIter = getPages().iterator();
                    if (pagesIter.hasNext())
                    {
                        return ((Page) pagesIter.next()).getName();
                    }
                    else
                    {
                        return PAGE_NOT_FOUND_PAGE;
                    }
                }
                catch (NodeException e1)
                {
                    return PAGE_NOT_FOUND_PAGE;
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultPage()
     */
    public void setDefaultPage( String defaultPage )
    {
        metadata.setDefaultPage(defaultPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDefaultTheme()
     */
    public String getDefaultTheme()
    {
        return defaultTheme;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#setDefaultTheme()
     */
    public void setDefaultTheme( String defaultTheme )
    {
        this.defaultTheme = defaultTheme;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getFolders()
     */
    public NodeSet getFolders() throws FolderNotFoundException, DocumentException
    {
        return getAllNodes().subset(Folder.FOLDER_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getPages()
     */
    public NodeSet getPages() throws NodeException
    {
        return getAllNodes().subset(Page.DOCUMENT_TYPE);
    }

    public Page getPage( String name ) throws PageNotFoundException, NodeException
    {
        Page page = (Page) getPages().get(name);
        if (page == null)
        {
            throw new PageNotFoundException("Jetspeed PSML page not found: " + name);
        }
        return page;
    }

    /**
     * <p>
     * getLinks
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getLinks()
     * @return @throws
     *         DocumentNotFoundException
     */
    public NodeSet getLinks() throws NodeException
    {
        return getAllNodes().subset(Link.DOCUMENT_TYPE);
    }

    /**
     * <p>
     * getDocumentSets
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getDocumentSets()
     * @return @throws
     *         DocumentNotFoundException
     */
    public NodeSet getDocumentSets() throws NodeException
    {
        return getAllNodes().subset(DocumentSet.DOCUMENT_TYPE);
    }

    /**
     * <p>
     * getMetaData
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.Folder#getMetaData()
     * @return
     */
    public FolderMetaData getMetaData()
    {
        return metadata;
    }

    /**
     * <p>
     * getAllNodes
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.Folder#getAllNodes()
     * @return
     * @throws DocumentException
     * @throws FolderNotFoundException
     */
    public NodeSet getAllNodes() throws FolderNotFoundException, DocumentException
    {
        if(allNodes == null)
        {            
            if(metadata.getDocumentOrder() != null)
            {
                if (getPath().endsWith("/"))
                {
                    allNodes = new NodeSetImpl(getPath(), new NodeOrderCompartaor(metadata.getDocumentOrder(), getPath()));
                }
                else
                {
                    allNodes = new NodeSetImpl(getPath(), new NodeOrderCompartaor(metadata.getDocumentOrder(), getPath()+"/"));
                }
            }
            else
            {
                allNodes = new NodeSetImpl(getPath());
            }
            
            //DocumentHandler docHandler = handlerFactory.getDocumentHandler(documentType);

            String[] nodeNames = folderHandler.listAll(getPath());
            for (int i = 0; i < nodeNames.length; i++)
            {
                Node node = null;
                try
                {
                    if (getPath().endsWith("/"))
                    {
                        if(nodeNames[i].indexOf(".") > -1)
                        {    
                            node = handlerFactory.getDocumentHandlerForPath(nodeNames[i]).getDocument(getPath() + nodeNames[i]);
                        }
                        else
                        {
                            node = folderHandler.getFolder(getPath() + nodeNames[i]);
                        }
                    }
                    else
                    {
                        
                        if(nodeNames[i].indexOf(".") > -1)
                        {    
                            node = handlerFactory.getDocumentHandlerForPath(nodeNames[i]).getDocument(getPath() +"/"+ nodeNames[i]);
                        }
                        else
                        {
                            node = folderHandler.getFolder(getPath() +"/"+ nodeNames[i]);
                        }
                    }
                    
                    node.setParent(this);
                    allNodes.add(node);
                }               
                catch (UnsupportedDocumentTypeException e)
                {
                    // Skip unsupported documents
                    log.info("getAllNodes() Skipping unsupported document: "+nodeNames[i]);
                }
                catch (Exception e)
                {
                    log.warn("getAllNodes() failed to create Node: "+nodeNames[i]+":"+e.toString(), e);
                }               
            }            
        }
        
        return allNodes;
    }
    /**
     * <p>
     * getMetadata
     * </p>
     *
     * @see org.apache.jetspeed.page.document.AbstractNode#getMetadata()
     * @return
     */
    public GenericMetadata getMetadata()
    {        
        return metadata.getMetadata();
    }
    
    
    /**
     * <p>
     * getTitle
     * </p>
     *
     * @see org.apache.jetspeed.page.document.AbstractNode#getTitle(java.util.Locale)
     * @param locale
     * @return
     */
    public String getTitle( Locale locale )
    {
        return metadata.getTitle(locale);
    }
    
    
    /**
     * <p>
     * getAcl
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecuredResource#getAcl()
     * @return
     */
    public String getAcl()
    {
        return metadata.getAcl();
    }
    /**
     * <p>
     * getTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElement#getTitle()
     * @return
     */
    public String getTitle()
    {
        return metadata.getTitle();
    }
    /**
     * <p>
     * setAcl
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecuredResource#setAcl(java.lang.String)
     * @param aclName
     */
    public void setAcl( String aclName )
    {
       metadata.setAcl(aclName);
    }
    /**
     * <p>
     * setTitle
     * </p>
     *
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElement#setTitle(java.lang.String)
     * @param title
     */
    public void setTitle( String title )
    {
        metadata.setTitle(title);
    }
    /**
     * <p>
     * getType
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#getType()
     * @return
     */
    public String getType()
    {
        return FOLDER_TYPE;
    }
    /**
     * <p>
     * isHidden
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     * @return
     */
    public boolean isHidden()
    {
        return metadata.isHidden();
    }
    /**
     * <p>
     * setHidden
     * </p>
     *
     * @see org.apache.jetspeed.page.document.AbstractNode#setHidden(boolean)
     * @param hidden
     */
    public void setHidden( boolean hidden )
    {        
        ((AbstractNode)metadata).setHidden(hidden);
    }
}
