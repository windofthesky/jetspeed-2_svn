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
package org.apache.jetspeed.layout.impl;

import java.util.Locale;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.request.RequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstracted behavior of security checks for portlet actions
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletActionSecurityPathBehavior implements PortletActionSecurityBehavior
{
    protected Logger log = LoggerFactory.getLogger(PortletActionSecurityPathBehavior.class);    
    protected PageManager pageManager;
    protected PageLayoutComponent pageLayoutComponent;
    private boolean enableCreateUserPagesFromRolesOnEdit;
    
    public PortletActionSecurityPathBehavior(PageManager pageManager, PageLayoutComponent pageLayoutComponent)
    {
    	this( pageManager, pageLayoutComponent, Boolean.FALSE ) ;
    }
    public PortletActionSecurityPathBehavior(PageManager pageManager, PageLayoutComponent pageLayoutComponent, Boolean enableCreateUserPagesFromRolesOnEdit )
    {
        this.pageManager = pageManager;
        this.pageLayoutComponent = pageLayoutComponent;
        this.enableCreateUserPagesFromRolesOnEdit = ( enableCreateUserPagesFromRolesOnEdit == null ? false : enableCreateUserPagesFromRolesOnEdit.booleanValue() );
    }

    public boolean checkAccess(RequestContext context, String action)
    {
        ContentPage page = context.getPage();
        String path = page.getPath();
        if (path == null)
            return false;
        if (path.indexOf(Folder.ROLE_FOLDER) > -1 || path.indexOf(Folder.GROUP_FOLDER) > -1)
        {
            if (action.equals(JetspeedActions.VIEW))
                return true;
            return false;
        }
        return true;
    }
    
    public boolean isCreateNewPageOnEditEnabled()
    {
    	return enableCreateUserPagesFromRolesOnEdit;
    }
    public boolean isPageQualifiedForCreateNewPageOnEdit(RequestContext context)
    {
    	if ( ! this.enableCreateUserPagesFromRolesOnEdit || context == null )
    		return false ;
    	return isPageQualifiedForCreateNewPageOnEdit( context.getPage().getPath() );
    }
    
    protected boolean isPageQualifiedForCreateNewPageOnEdit( String pagePath )
    {
        if (pagePath == null)
        	return false;
        // page must be in role directory
        return (pagePath.indexOf(Folder.ROLE_FOLDER) == 0);
    }

    public boolean createNewPageOnEdit(RequestContext context)
    {
    	if ( ! this.enableCreateUserPagesFromRolesOnEdit )
    		return false ;

        ContentPage contentPage = context.getPage();
        String pagePath = contentPage.getPath();
        try
        {
        	if ( isPageQualifiedForCreateNewPageOnEdit( pagePath ) )
            {
        	    // create user home pages
        		String pageName = contentPage.getName();
                pageManager.createUserHomePagesFromRoles(context.getSubject());
                Page page = pageManager.getPage(Folder.USER_FOLDER 
                                                + context.getRequest().getUserPrincipal().getName()
                                                + Folder.PATH_SEPARATOR 
                                                + pageName);   // was Folder.FALLBACK_DEFAULT_PAGE prior to 2007-11-06
                
                // TODO validate effectiveness of request context reset below
                context.setPage(pageLayoutComponent.newContentPage(page));
                context.getRequest().removeAttribute(ProfilerValveImpl.PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY);
                context.getRequest().removeAttribute(ProfilerValveImpl.PROFILED_PAGE_CONTEXT_ATTR_KEY);
                context.getRequest().getSession().removeAttribute(ProfilerValveImpl.PORTAL_SITE_SESSION_CONTEXT_ATTR_KEY);                
            }
        }
        catch (Exception e)
        {
            // already logged error
            return false;
        }
        return true;
    }
}
