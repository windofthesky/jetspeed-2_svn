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
package org.apache.jetspeed.profiler.impl;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.request.RequestContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CreateUserTemplatePagesValveImpl
 * 
 * Create User Pages from template folder on first login feature
 * The CreateUserTemplatePagesValveImpl creates a new user's home page from the user template folder.
 * 
 * @author Woonsan Ko
 * @version $Id$
 */
public class CreateUserTemplatePagesValveImpl extends ProfilerValveImpl
{
    private static final Log log = LogFactory.getLog(CreateUserTemplatePagesValveImpl.class);

    public static final String USER_TEMPLATE_FOLDER_REQUEST_ATTR_KEY = "org.apache.jetspeed.profiler.UserTemplateFolder";
    
    protected PageManager pageManager;
    protected String defaultTemplateFolder = "/_user/template/";

    /**
     * CreateUserTemplatePagesValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param requestFallback flag to enable root folder fallback
     * @param useHistory flag to enable selection of last visited folder page
     * @param pageManager pageManagerComponent reference
     */
    public CreateUserTemplatePagesValveImpl(Profiler profiler, PortalSite portalSite, boolean requestFallback, boolean useHistoryPageManager, PageManager pageManager)
    {
        super(profiler, portalSite, requestFallback, useHistoryPageManager);
        this.pageManager = pageManager;
    }
    
    /**
     * CreateUserTemplatePagesValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param requestFallback flag to enable root folder fallback
     * @param pageManager pageManagerComponent reference
     */
    public CreateUserTemplatePagesValveImpl(Profiler profiler, PortalSite portalSite, 
                             boolean requestFallback, PageManager pageManager)
    {
        super(profiler, portalSite, requestFallback);
        this.pageManager = pageManager;
    }

    /**
     * CreateUserTemplatePagesValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageManager pageManagerComponent reference
     */
    public CreateUserTemplatePagesValveImpl(Profiler profiler, PortalSite portalSite, PageManager pageManager)
    {
        super(profiler, portalSite);
        this.pageManager = pageManager;
    }
    
    public void setDefaultTemplateFolder(String defaultTemplateFolder)
    {
        this.defaultTemplateFolder = defaultTemplateFolder;
    }
    
    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        boolean created = false;
        
        try
        {
            created = createUserFolderPages(request);
        }
        catch (Exception e)
        {
            log.error("Exception occurred during creating user folder.", e);
        }
        finally
        {
            if (created)
            {
                super.invoke(request, context);
            }
            else
            {
                context.invokeNext(request);
            }
        }
    }
    
    private boolean createUserFolderPages(RequestContext request) throws Exception
    {
        boolean created = false;
        
        String userName = request.getUserPrincipal().getName();
        String userFolder = Folder.USER_FOLDER + userName;        
        
        boolean found = true;
        
        try
        {
            this.pageManager.getFolder(userFolder);
        }
        catch (FolderNotFoundException e)
        {
            found = false;
        }

        if (!found)
        {
            try
            {
                String templateFolder = (String) request.getAttribute(USER_TEMPLATE_FOLDER_REQUEST_ATTR_KEY);
                
                if (templateFolder == null)
                {
                    templateFolder = this.defaultTemplateFolder;
                }
                
                Folder source = this.pageManager.getFolder(templateFolder);
                
                // copy the entire dir tree from the template folder
                this.pageManager.deepCopyFolder(source, userFolder, userName);
                created = true;
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        
        return created;
    }
}
