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
package org.apache.jetspeed.profiler.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;

/**
 * ProfilerValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class ProfilerValveImpl extends AbstractValve implements PageProfilerValve
{
    protected Log log = LogFactory.getLog(ProfilerValveImpl.class);
    private Profiler profiler;
    static final String LOCATOR_KEY = "org.apache.jetpeed.profileLocator";
    public static final String FOLDER_ATTR_KEY = "org.apache.jetspeed.folder";
    private PageManager pageManager;

    public ProfilerValveImpl( Profiler profiler, PageManager pageManager )
    {
        this.profiler = profiler;
        this.pageManager = pageManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        try
        {

            HttpServletRequest httpRequest = request.getRequest();
            ProfileLocator locator = null;
            Folder folder = getFolder(request);
            httpRequest.setAttribute(FOLDER_ATTR_KEY, folder);
            request.setPage(pageManager.getPage(getPageName(request, folder)));

            locator = profiler.getProfile(request);
            request.setProfileLocator(locator);
            // request.setPage(profiler.getPage(locator));
            context.invokeNext(request);

        }       
        catch (PageNotFoundException e)
        {
            log.error(e.getMessage(), e);
            try
            {
                request.getResponse().sendError(404, e.getMessage());
            }
            catch (IOException e1)
            {
                log.error("Failed to invoke HttpServletReponse.sendError: " + e1.getMessage(), e1);
            }
        }
        catch (Exception e)
        {
            throw new PipelineException(e.toString(), e);
        }
    }


    protected Folder getFolder( RequestContext request ) throws IOException
    {
        HttpServletRequest httpRequest = request.getRequest();
        String folderInRequest = getFolderPath(request);
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
            if (selectedFolder == null)
            {
                selectedFolder = pageManager.getFolder("/");
            }
        }

        return selectedFolder;

    }

    protected String getFolderPath( RequestContext request )
    {
        String pathInfo = request.getPath();  
        
        String folder = null;
        if (pathInfo != null)
        {
            if (pathInfo.endsWith(PageManager.PAGE_SUFFIX))
            {
                int lastSlash = pathInfo.lastIndexOf("/");
                if(lastSlash > -1)
                {
                    return pathInfo.substring(0, lastSlash);
                }
                else
                {
                    return "/";
                }
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

    protected String getPageName( RequestContext request, Folder currentFolder )
    {
        String pathInfo = request.getPath();
        if (pathInfo == null || !pathInfo.endsWith(".psml"))
        {
            return currentFolder.getDefaultPage();
        }
        else
        {
            return pathInfo;
        }
    }    

    public String toString()
    {
        return "ProfilerValve";
    }

}