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
package org.apache.jetspeed.layout.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;

/**
 * Abstracted behavior of security checks for portlet actions
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletActionSecurityPathBehavior implements PortletActionSecurityBehavior
{
    protected Log log = LogFactory.getLog(PortletActionSecurityPathBehavior.class);    
    protected PageManager pageManager;
    
    public PortletActionSecurityPathBehavior(PageManager pageManager)
    {
        this.pageManager = pageManager;
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

    public boolean createNewPageOnEdit(RequestContext context)
    {
        Page page = context.getPage();        
        String path = page.getPath();
        try
        {        
            if (path == null)
                return false;
            // make sure we are not copying from user area
            if (path.indexOf(Folder.USER_FOLDER) == -1)
            {
                this.pageManager.createUserHomePagesFromRoles(context.getSubject());
                page = this.pageManager.getPage(Folder.USER_FOLDER 
                                                + context.getRequest().getUserPrincipal().getName()
                                                + Folder.PATH_SEPARATOR 
                                                + Folder.FALLBACK_DEFAULT_PAGE);                 
                context.setPage(new ContentPageImpl(page));
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
