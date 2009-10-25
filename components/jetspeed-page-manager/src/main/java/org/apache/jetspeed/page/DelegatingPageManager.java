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

import java.util.Map;

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;


/**
 * DelegatingPageManager
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */

public class DelegatingPageManager extends AbstractPageManager
{
    public DelegatingPageManager(
            IdGenerator generator,
            boolean isPermissionsSecurity, 
            boolean isConstraintsSecurity,
            Map modelClasses)
    {
        super(generator, isPermissionsSecurity, isConstraintsSecurity, modelClasses);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     */
    public Page getPage(String id) throws PageNotFoundException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getContentPage(java.lang.String)
     */
    public ContentPage getContentPage(String path)
            throws PageNotFoundException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLink(java.lang.String)
     */
    public Link getLink(String name) throws DocumentNotFoundException,
            UnsupportedDocumentTypeException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity()
     */
    public PageSecurity getPageSecurity() throws DocumentNotFoundException,
            UnsupportedDocumentTypeException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean checkConstraint(String securityConstraintName, String actions)
    {
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolder(java.lang.String)
     */
    public Folder getFolder(String folderPath) throws FolderNotFoundException,
            InvalidFolderException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolders(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getFolders(Folder folder) throws DocumentException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getFolder(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */
    public Folder getFolder(Folder folder, String name) throws FolderNotFoundException, DocumentException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPages(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getPages(Folder folder) throws NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPage(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */
    public Page getPage(Folder folder, String name) throws PageNotFoundException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLinks(org.apache.jetspeed.om.folder.Folder)
     */    
    public NodeSet getLinks(Folder folder) throws NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getLink(org.apache.jetspeed.om.folder.Folder,java.lang.String)
     */    
    public Link getLink(Folder folder, String name) throws DocumentNotFoundException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getPageSecurity(org.apache.jetspeed.om.folder.Folder)
     */    
    public PageSecurity getPageSecurity(Folder folder) throws DocumentNotFoundException, NodeException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#getAll(org.apache.jetspeed.om.folder.Folder)
     */
    public NodeSet getAll(Folder folder) throws DocumentException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage(Page page) throws NodeException,
            PageNotUpdatedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page) throws NodeException,
            PageNotRemovedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void updateFolder(Folder folder) throws NodeException,
            FolderNotUpdatedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateFolder(org.apache.jetspeed.om.folder.Folder,boolean)
     */
    public void updateFolder(Folder folder, boolean deep) throws NodeException,
            FolderNotUpdatedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeFolder(org.apache.jetspeed.om.folder.Folder)
     */
    public void removeFolder(Folder folder) throws NodeException,
            FolderNotRemovedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updateLink(org.apache.jetspeed.om.page.Link)
     */
    public void updateLink(Link link) throws NodeException,
            LinkNotUpdatedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removeLink(org.apache.jetspeed.om.page.Link)
     */
    public void removeLink(Link link) throws NodeException,
            LinkNotRemovedException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#updatePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void updatePageSecurity(PageSecurity pageSecurity) throws
            NodeException, FailedToUpdateDocumentException
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManager#removePageSecurity(org.apache.jetspeed.om.page.PageSecurity)
     */
    public void removePageSecurity(PageSecurity pageSecurity) throws
            NodeException, FailedToDeleteDocumentException
    {
        // TODO Auto-generated method stub

    }
    
    public int addPages(Page[] pages)
    throws NodeException
    {
        throw new NodeException("not impl");
    }
}
