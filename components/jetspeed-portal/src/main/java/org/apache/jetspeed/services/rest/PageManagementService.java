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
package org.apache.jetspeed.services.rest;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.BasePageElement;
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.beans.FolderBean;
import org.apache.jetspeed.services.beans.LinkBean;
import org.apache.jetspeed.services.beans.NodeBean;
import org.apache.jetspeed.services.beans.PageBean;
import org.apache.jetspeed.services.rest.util.PathSegmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PageManagementService
 * 
 * @vesion $Id$
 */

@Path("/pagemanagement/")
public class PageManagementService
{
    
    private static Logger log = LoggerFactory.getLogger(PageManagementService.class);
    
    @Context
    private ServletConfig servletConfig;
    
    @Context
    private ServletContext servletContext;
    
    private PageManager pageManager;
    
    public PageManagementService(PageManager pageManager)
    {
        this.pageManager = pageManager;
    }
    
    @GET
    @Path("/{type}/{path:.*}")
    public NodeBean getNode(@Context HttpServletRequest servletRequest,
                            @Context UriInfo uriInfo,
                            @PathParam("type") String type,
                            @PathParam("path") List<PathSegment> pathSegments)
    {
        String path = PathSegmentUtils.joinWithPrefix(pathSegments, "/", "/");
        
        try
        {
            if (Page.DOCUMENT_TYPE.equals(type))
            {
                Page page = pageManager.getPage(path);
                page.checkAccess(JetspeedActions.EDIT);
                return new PageBean(page);
            }
            else if (Link.DOCUMENT_TYPE.equals(type))
            {
                Link link = getLink(path);
                link.checkAccess(JetspeedActions.EDIT);
                return new LinkBean(link);
            }
            else if (Folder.FOLDER_TYPE.equals(type))
            {
                Folder folder = pageManager.getFolder(path);
                folder.checkAccess(JetspeedActions.EDIT);
                return new FolderBean(folder);
            }
            else
            {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }
        catch (DocumentNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (FolderNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (NodeException e)
        {
            throw new WebApplicationException(e);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
    }
    
    @DELETE
    @Path("/{type}/{path:.*}")
    public NodeBean deleteNode(@Context HttpServletRequest servletRequest,
                               @Context UriInfo uriInfo,
                               @PathParam("type") String type,
                               @PathParam("path") List<PathSegment> pathSegments)
    {
        String path = PathSegmentUtils.joinWithPrefix(pathSegments, "/", "/");
        
        try
        {
            if (Page.DOCUMENT_TYPE.equals(type))
            {
                Page page = pageManager.getPage(path);
                pageManager.removePage(page);
                return new PageBean(page);
            }
            else if (Link.DOCUMENT_TYPE.equals(type))
            {
                Link link = getLink(path);
                pageManager.removeLink(link);
                return new LinkBean(link);
            }
            else if (Folder.FOLDER_TYPE.equals(type))
            {
                Folder folder = pageManager.getFolder(path);
                pageManager.removeFolder(folder);
                return new FolderBean(folder);
            }
            else
            {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }
        catch (DocumentNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (FolderNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (NodeException e)
        {
            throw new WebApplicationException(e);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
    }
    
    @POST
    @Path("/copy/{type}/{sourcePath:.*}")
    public NodeBean copyNode(@Context HttpServletRequest servletRequest,
                             @Context UriInfo uriInfo,
                             @PathParam("type") String type,
                             @PathParam("sourcePath") List<PathSegment> sourcePathSegments,
                             @FormParam("target") String targetPath,
                             @FormParam("deep") boolean deepCopy,
                             @FormParam("merge") boolean merging,
                             @FormParam("owner") String owner,
                             @FormParam("copyids") boolean copyIds)
    {
        String sourcePath = PathSegmentUtils.joinWithPrefix(sourcePathSegments, "/", "/");
        
        try
        {
            if (Page.DOCUMENT_TYPE.equals(type))
            {
                Page source = pageManager.getPage(sourcePath);
                Page target = pageManager.copyPage(source, targetPath);
                pageManager.updatePage(target);
                return new PageBean(target);
            }
            else if (Link.DOCUMENT_TYPE.equals(type))
            {
                Link source = getLink(sourcePath);
                Link target = pageManager.copyLink(source, targetPath);
                pageManager.updateLink(target);
                return new LinkBean(target);
            }
            else if (Folder.FOLDER_TYPE.equals(type))
            {
                Folder source = pageManager.getFolder(sourcePath);
                
                if (deepCopy)
                {
                    if (merging)
                    {
                        pageManager.deepMergeFolder(source, targetPath, owner, copyIds);
                    }
                    else
                    {
                        pageManager.deepCopyFolder(source, targetPath, owner, copyIds);
                    }
                    
                    Folder target = pageManager.getFolder(targetPath);
                    return new FolderBean(target);
                }
                else
                {
                    Folder target = pageManager.copyFolder(source, targetPath);
                    pageManager.updateFolder(target, true);
                    return new FolderBean(target);
                }
            }
            else 
            {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
        }
        catch (DocumentNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (FolderNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (NodeException e)
        {
            throw new WebApplicationException(e);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
    }
    
    @POST
    @Path("/move/{type}/{sourcePath:.*}")
    public NodeBean moveNode(@Context HttpServletRequest servletRequest,
                             @Context UriInfo uriInfo,
                             @PathParam("type") String type,
                             @PathParam("sourcePath") List<PathSegment> sourcePathSegments,
                             @FormParam("target") String targetPath,
                             @FormParam("deep") boolean deepCopy,
                             @FormParam("merge") boolean merging,
                             @FormParam("owner") String owner,
                             @FormParam("copyids") boolean copyIds)
    {
        NodeBean nodeBean = copyNode(servletRequest, uriInfo, type, sourcePathSegments, targetPath, deepCopy, merging, owner, copyIds);
        deleteNode(servletRequest, uriInfo, type, sourcePathSegments);
        return nodeBean;
    }
    
    @POST
    @Path("/info/{type}/{path:.*}")
    public NodeBean updateNodeInfo(@Context HttpServletRequest servletRequest,
                                   @Context UriInfo uriInfo,
                                   @PathParam("type") String type,
                                   @PathParam("path") List<PathSegment> pathSegments,
                                   @FormParam("title") String title,
                                   @FormParam("shorttitle") String shortTitle,
                                   @FormParam("hidden") String hidden,
                                   @FormParam("skin") String skin,
                                   @FormParam("version") String version,
                                   @FormParam("docorder") String documentOrder,
                                   @FormParam("url") String url)
    {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        String path = PathSegmentUtils.joinWithPrefix(pathSegments, "/", "/");
        
        try
        {
            Node node = null;
            
            if (Page.DOCUMENT_TYPE.equals(type))
            {
                node = pageManager.getPage(path);
            }
            else if (Link.DOCUMENT_TYPE.equals(type))
            {
                node = getLink(path);
            }
            else if (Folder.FOLDER_TYPE.equals(type))
            {
                node = pageManager.getFolder(path);
            }
            else
            {
                throw new WebApplicationException(Status.NOT_FOUND);
            }
            
            node.checkAccess(JetspeedActions.EDIT);
            
            boolean changed = false;
            Locale locale = requestContext.getLocale();
            GenericMetadata metadata = node.getMetadata();
            
            if (title != null && !title.equals(node.getTitle()))
            {
                node.setTitle(title);
                setLocalizedField(metadata, locale, "title", title);
                changed = true;
            }
            
            if (shortTitle != null && !shortTitle.equals(node.getShortTitle()))
            {
                node.setShortTitle(shortTitle);
                setLocalizedField(metadata, locale, "short-title", title);
                changed = true;
            }
            
            if (hidden != null)
            {
                boolean hiddenFlag = BooleanUtils.toBoolean(hidden);
                
                if (hiddenFlag != node.isHidden())
                {
                    node.setHidden(hiddenFlag);
                    changed = true;
                }
            }
            
            if (skin != null)
            {
                if (node instanceof BasePageElement)
                {
                    if (!skin.equals(((BasePageElement) node).getSkin()))
                    {
                        ((BasePageElement) node).setSkin(skin);
                        changed = true;
                    }
                }
                else if (node instanceof Link)
                {
                    if (!skin.equals(((Link) node).getSkin()))
                    {
                        ((Link) node).setSkin(skin);
                        changed = true;
                    }
                }
                else if (node instanceof Folder)
                {
                    if (!skin.equals(((Folder) node).getSkin()))
                    {
                        ((Folder) node).setSkin(skin);
                        changed = true;
                    }
                }
            }
            
            if (version != null)
            {
                if (node instanceof Document)
                {
                    if (!version.equals(((Document) node).getVersion()))
                    {
                        ((Document) node).setVersion(version);
                        changed = true;
                    }
                }
            }
            
            if (documentOrder != null)
            {
                if (node instanceof Folder)
                {
                    String [] docIndexArray = StringUtils.split(documentOrder, ",\r\n");
                    for (int i = 0; i < docIndexArray.length; i++)
                    {
                        docIndexArray[i] = docIndexArray[i].trim();
                    }
                    ((Folder) node).setDocumentOrder(Arrays.asList(docIndexArray));
                    changed = true;
                }
            }
            
            if (url != null)
            {
                if (node instanceof Link)
                {
                    if (!url.equals(((Link) node).getUrl()))
                    {
                        ((Link) node).setUrl(url);
                        changed = true;
                    }
                }
            }
            
            if (changed)
            {
                if (node instanceof Page)
                {
                    pageManager.updatePage((Page) node);
                }
                else if (node instanceof Link)
                {
                    pageManager.updateLink((Link) node);
                }
                else if (node instanceof Folder)
                {
                    pageManager.updateFolder((Folder) node);
                }
            }
            
            if (Page.DOCUMENT_TYPE.equals(type))
            {
                return new PageBean((Page) node);
            }
            else if (Link.DOCUMENT_TYPE.equals(type))
            {
                return new LinkBean((Link) node);
            }
            else if (Folder.FOLDER_TYPE.equals(type))
            {
                return new FolderBean((Folder) node);
            }
            
            return null;
        }
        catch (DocumentNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (FolderNotFoundException e)
        {
            throw new WebApplicationException(e, Status.NOT_FOUND);
        }
        catch (NodeException e)
        {
            throw new WebApplicationException(e);
        }
        catch (SecurityException e)
        {
            throw new WebApplicationException(e, Status.FORBIDDEN);
        }
    }
    
    private LocalizedField getLocalizedField(GenericMetadata metadata, Locale locale, String name)
    {
        Collection<LocalizedField> fields = metadata.getFields(name);
        
        if (fields != null)
        {
            for (LocalizedField field : fields)
            {
                if (locale.equals(field.getLocale()))
                {
                    return field;
                }
            }
        }
        
        return null;
    }
    
    private void setLocalizedField(GenericMetadata metadata, Locale locale, String name, String value)
    {
        LocalizedField field = getLocalizedField(metadata, locale, name);
        
        if (field != null)
        {
            field.setValue(value);
        }
        else
        {
            metadata.addField(locale, name, value);
        }
    }
    
    private Link getLink(String path) throws FolderNotFoundException, InvalidFolderException, NodeException, DocumentNotFoundException
    {
        String folderPath = null;
        String name = null;
        int offset = path.lastIndexOf('/');
        
        if (offset != -1)
        {
            folderPath = path.substring(0, offset);
            name = path.substring(offset + 1);
        }
        
        if (StringUtils.isBlank(name))
        {
            throw new IllegalArgumentException("Invalid link path: " + path);
        }
        
        if (StringUtils.isEmpty(folderPath))
        {
            folderPath = "/";
        }
        
        Folder folder = pageManager.getFolder(folderPath);
        Link link = pageManager.getLink(folder, name);
        
        return link;
    }
    
}
