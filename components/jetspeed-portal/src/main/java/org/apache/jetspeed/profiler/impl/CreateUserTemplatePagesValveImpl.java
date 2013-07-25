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

import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.portalsite.PortalSite;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CreateUserTemplatePagesValveImpl
 * 
 * Create User Pages from template folder on first login feature
 * The CreateUserTemplatePagesValveImpl creates a new user's home page from the user template folder.
 * <P>
 * Note: this valve is deprecated. Please consider to use {@link RefreshUserHomepageValveImpl} instead.
 * </P>
 * 
 * @version $Id$
 * @deprecated
 */
public class CreateUserTemplatePagesValveImpl extends ProfilerValveImpl
{
    private static final Logger log = LoggerFactory.getLogger(CreateUserTemplatePagesValveImpl.class);

    public static final String USER_TEMPLATE_FOLDER_REQUEST_ATTR_KEY = "org.apache.jetspeed.profiler.UserTemplateFolder";
    
    protected PageManager pageManager;
    protected String defaultTemplateFolder = "/_template/new-user/";

    /**
     * CreateUserTemplatePagesValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageLayoutComponent page layout component reference
     * @param requestFallback flag to enable root folder fallback
     * @param useHistoryPageManager flag to enable selection of last visited folder page
     * @param pageManager pageManagerComponent reference
     */
    public CreateUserTemplatePagesValveImpl(Profiler profiler, PortalSite portalSite, PageLayoutComponent pageLayoutComponent,
                                            boolean requestFallback, boolean useHistoryPageManager, PageManager pageManager)
    {
        super(profiler, portalSite, pageLayoutComponent, requestFallback, useHistoryPageManager);
        this.pageManager = pageManager;
    }
    
    /**
     * CreateUserTemplatePagesValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageLayoutComponent page layout component reference
     * @param requestFallback flag to enable root folder fallback
     * @param pageManager pageManagerComponent reference
     */
    public CreateUserTemplatePagesValveImpl(Profiler profiler, PortalSite portalSite, PageLayoutComponent pageLayoutComponent,
                                            boolean requestFallback, PageManager pageManager)
    {
        super(profiler, portalSite, pageLayoutComponent, requestFallback);
        this.pageManager = pageManager;
    }

    /**
     * CreateUserTemplatePagesValveImpl - constructor
     *
     * @param profiler profiler component reference
     * @param portalSite portal site component reference
     * @param pageLayoutComponent page layout component reference
     * @param pageManager pageManagerComponent reference
     */
    public CreateUserTemplatePagesValveImpl(Profiler profiler, PortalSite portalSite, PageLayoutComponent pageLayoutComponent, PageManager pageManager)
    {
        super(profiler, portalSite, pageLayoutComponent);
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
                
                // The user folder will have titles named after the user name.
                Folder destFolder = this.pageManager.getFolder(userFolder);
                destFolder.setTitle(userName);
                destFolder.setShortTitle(userName);   
                this.pageManager.updateFolder(destFolder);
                
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
