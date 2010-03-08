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
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.request.RequestContext;
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
    @Path("/page/{path:.*}")
    public PageBean getPage(@Context HttpServletRequest servletRequest,
                            @Context UriInfo uriInfo,
                            @PathParam("path") List<PathSegment> pathSegments)
    {
        String path = PathSegmentUtils.joinWithPrefix(pathSegments, "/", "/");
        
        try
        {
            Page page = pageManager.getPage(path);
            page.checkAccess(JetspeedActions.EDIT);
            return new PageBean(page);
        }
        catch (PageNotFoundException e)
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
    @Path("/page/{path:.*}")
    public PageBean deletePage(@Context HttpServletRequest servletRequest,
                               @Context UriInfo uriInfo,
                               @PathParam("path") List<PathSegment> pathSegments)
    {
        String path = PathSegmentUtils.joinWithPrefix(pathSegments, "/", "/");
        
        try
        {
            Page page = pageManager.getPage(path);
            pageManager.removePage(page);
            return new PageBean(page);
        }
        catch (PageNotFoundException e)
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
    @Path("/page/copy/{path:.*}")
    public PageBean copyPage(@Context HttpServletRequest servletRequest,
                             @Context UriInfo uriInfo,
                             @PathParam("path") List<PathSegment> targetPathSegments,
                             @FormParam("source") String sourcePagePath)
    {
        String targetPath = PathSegmentUtils.joinWithPrefix(targetPathSegments, "/", "/");
        
        try
        {
            Page source = pageManager.getPage(sourcePagePath);
            pageManager.copyPage(source, targetPath);
            Page target = pageManager.getPage(targetPath);
            return new PageBean(target);
        }
        catch (PageNotFoundException e)
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
    @Path("/page/move/{path:.*}")
    public PageBean movePage(@Context HttpServletRequest servletRequest,
                             @Context UriInfo uriInfo,
                             @PathParam("path") List<PathSegment> targetPathSegments,
                             @FormParam("source") String sourcePagePath)
    {
        String targetPath = PathSegmentUtils.joinWithPrefix(targetPathSegments, "/", "/");
        
        try
        {
            Page source = pageManager.getPage(sourcePagePath);
            pageManager.copyPage(source, targetPath);
            pageManager.removePage(source);
            Page copied = pageManager.getPage(targetPath);
            return new PageBean(copied);
        }
        catch (PageNotFoundException e)
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
    @Path("/page/info/{path:.*}")
    public PageBean updatePage(@Context HttpServletRequest servletRequest,
                               @Context UriInfo uriInfo,
                               @PathParam("path") List<PathSegment> pathSegments,
                               @FormParam("title") String title,
                               @FormParam("shorttitle") String shortTitle,
                               @FormParam("hidden") String hidden,
                               @FormParam("skin") String skin,
                               @FormParam("version") String version)
    {
        RequestContext requestContext = (RequestContext) servletRequest.getAttribute(RequestContext.REQUEST_PORTALENV);
        String path = PathSegmentUtils.joinWithPrefix(pathSegments, "/", "/");
        
        try
        {
            Page page = pageManager.getPage(path);
            page.checkAccess(JetspeedActions.EDIT);
            
            boolean changed = false;
            Locale locale = requestContext.getLocale();
            GenericMetadata metadata = page.getMetadata();
            
            if (title != null && !title.equals(page.getTitle()))
            {
                page.setTitle(title);
                setLocalizedField(metadata, locale, "title", title);
                changed = true;
            }
            
            if (shortTitle != null && !shortTitle.equals(page.getShortTitle()))
            {
                page.setShortTitle(shortTitle);
                setLocalizedField(metadata, locale, "short-title", title);
                changed = true;
            }
            
            if (hidden != null)
            {
                boolean hiddenFlag = BooleanUtils.toBoolean(hidden);
                
                if (hiddenFlag != page.isHidden())
                {
                    page.setHidden(hiddenFlag);
                    changed = true;
                }
            }
            
            if (skin != null && !skin.equals(page.getSkin()))
            {
                page.setSkin(skin);
                changed = true;
            }
            
            if (version != null && !version.equals(page.getVersion()))
            {
                page.setVersion(version);
                changed = true;
            }
            
            if (changed)
            {
                pageManager.updatePage(page);
            }
            
            return new PageBean(page);
        }
        catch (PageNotFoundException e)
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
}
