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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.request.RequestContext;

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
    private boolean enableCreateUserPagesFromRolesOnEdit;
    
    public PortletActionSecurityPathBehavior(PageManager pageManager )
    {
    	this( pageManager, Boolean.FALSE ) ;
    }
    public PortletActionSecurityPathBehavior(PageManager pageManager, Boolean enableCreateUserPagesFromRolesOnEdit )
    {
        this.pageManager = pageManager;
        this.enableCreateUserPagesFromRolesOnEdit = ( enableCreateUserPagesFromRolesOnEdit == null ? false : enableCreateUserPagesFromRolesOnEdit.booleanValue() );
    }

    public boolean checkAccess(RequestContext context, String action)
    {
        Page page = context.getPage();
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

        Page page = context.getPage();        
        String pagePath = page.getPath();
        try
        {
        	if ( isPageQualifiedForCreateNewPageOnEdit( pagePath ) )
            {
        		String pageName = page.getName();
                this.pageManager.createUserHomePagesFromRoles(context.getSubject());
                page = this.pageManager.getPage(Folder.USER_FOLDER 
                                                + context.getRequest().getUserPrincipal().getName()
                                                + Folder.PATH_SEPARATOR 
                                                + pageName);   // was Folder.FALLBACK_DEFAULT_PAGE prior to 2007-11-06
                context.setPage(new ContentPageImpl(page));
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
