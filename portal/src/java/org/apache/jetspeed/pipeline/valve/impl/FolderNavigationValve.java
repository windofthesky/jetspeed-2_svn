/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.pipeline.valve.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * <p>
 * FolderNavigationValve
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class FolderNavigationValve extends AbstractValve implements Valve
{
    
    public static final String FOLDER_ATTR_KEY = "org.apache.jetspeed.folder";
    private PageManager pageManager;

    public FolderNavigationValve( PageManager pageManager )
    {
        this.pageManager = pageManager;
    }

    /**
     * <p>
     * invoke
     * </p>
     * 
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     * @param request
     * @param context
     * @throws PipelineException
     */
    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        HttpServletRequest httpRequest = request.getRequest();
        String folderInRequest = getFolderPath(httpRequest);
        Folder selectedFolder = null;

        if (folderInRequest != null)
        {
            selectedFolder = pageManager.getFolder(folderInRequest);
        }

        if (selectedFolder != null)
        {            
            httpRequest.getSession().setAttribute(FOLDER_ATTR_KEY, selectedFolder);            
        }
        else
        {
            selectedFolder = (Folder) httpRequest.getAttribute(FOLDER_ATTR_KEY);
            if(selectedFolder == null)
            {
                selectedFolder = pageManager.getFolder("/");
            }
        }
        
        String pathInfo = httpRequest.getPathInfo();
        if(pathInfo == null || !pathInfo.endsWith(".psml"))
        {
            String defaultPage = selectedFolder.getDefaultPage();
            request.setPath(defaultPage);
    
        }
        
        httpRequest.setAttribute(FOLDER_ATTR_KEY, selectedFolder);
        context.invokeNext(request);

    }
    
    protected String getFolderPath(HttpServletRequest httpRequest)
    {
        String pathInfo = httpRequest.getPathInfo();
        String folder = null;
        if(pathInfo != null )
        {
           if(pathInfo.endsWith(PageManager.PAGE_SUFFIX))
           {
               int lastSlash = pathInfo.lastIndexOf("/");
               return pathInfo.substring(0,lastSlash);               
           }
           else
           {
               return pathInfo;
           }
        }
        else
        {
            return "/";
        }
    }
    
    /**
     * <p>
     * toString
     * </p>
     *
     * @see java.lang.Object#toString()
     * @return
     */
    public String toString()
    {
        // TODO Auto-generated method stub
        return "FolderValve";
    }
}