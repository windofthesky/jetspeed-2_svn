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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.request.RequestContext;

/**
 * Update Page action -- updates various parts of the PSML page
 * 
 * AJAX Parameters: 
 *    action = updatepage
 *    General methods:
 *    method = add | remove 
 *    Info methods:
 *    | info 
 *    Meta methods:
 *    | add-meta | update-meta | remove-meta
 *    Security methods:
 *    | add-secref | remove-secref
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class UpdatePageAction 
    extends BaseSiteUpdateAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(UpdatePageAction.class);

    public UpdatePageAction(String template, 
                            String errorTemplate, 
                            PageManager pm)
    {
        super(template, errorTemplate, pm); 
    }
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "updatepage");
            // Get the necessary parameters off of the request
            String method = getActionParameter(requestContext, "method");
            if (method == null) 
            { 
                throw new RuntimeException("Method not provided"); 
            }            
            resultMap.put("method", method);
            if (false == checkAccess(requestContext, JetspeedActions.EDIT))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to administer portal permissions");                
                return success;
            }           
            int count = 0;
            String path = getActionParameter(requestContext, "path");
            if (path == null)
                throw new AJAXException("Missing 'path' parameter");
            Page page = null; 
            if (!method.equals("add"))
            {
                page = pageManager.getPage(path);
            }
            else
            {
                if (pageManager.pageExists(path))
                {
                    success = false;
                    resultMap.put(REASON, "Can't create: Page already exists: " + path);                
                    return success;                
                }
            }                
            if (method.equals("info"))
            {
                count = updateInformation(requestContext, resultMap, page, path);
            }
            else if (method.equals("add-meta"))
            {
                count = insertMetadata(requestContext, resultMap, page);
            }
            else if (method.equals("update-meta"))
            {
                count = updateMetadata(requestContext, resultMap, page);
            }
            else if (method.equals("remove-meta"))
            {
                count = removeMetadata(requestContext, resultMap, page);
            }
            else if (method.equals("add-secref"))
            {
                count = insertSecurityReference(requestContext, resultMap, page);
            }
            else if (method.equals("update-secref"))
            {
                count = updateSecurityReference(requestContext, resultMap, page);
            }                        
            else if (method.equals("remove-secref"))
            {
                count = removeSecurityReference(requestContext, resultMap, page);
            }
            else if (method.equals("remove-secdef"))
            {
                count = removeSecurityDef(requestContext, resultMap, page);
            }            
            else if (method.equals("add"))
            {
                page = pageManager.newPage(path);
                page.setTitle(getActionParameter(requestContext, "title"));
                String s = getActionParameter(requestContext, "short-title");
                if (!isBlank(s))
                    page.setShortTitle(s);                
                page.getRootFragment().setName(getActionParameter(requestContext, "defaultLayout"));
                count++;                
            }
            else if (method.equals("remove"))
            {
                pageManager.removePage(page);
            }            
            else
            {
                success = false;
                resultMap.put(REASON, "Unsupported Site Update method: " + method);                
                return success;                
            }
            if (count > 0)
            {
                pageManager.updatePage(page);                
            }                        
            resultMap.put("count", Integer.toString(count));
            resultMap.put(STATUS, status);
        } 
        catch (Exception e)
        {
            log.error("exception administering Site update", e);
            resultMap.put(REASON, e.toString());
            success = false;
        }
        return success;
    }
    
    protected int updateInformation(RequestContext requestContext, Map resultMap, Node node, String path)
    throws AJAXException    
    {
        int count = 0;
        try
        {
            Page page = (Page)node;            
            String title = getActionParameter(requestContext, "title");
            if (isFieldModified(title, page.getTitle()))
                page.setTitle(title);
            String shortTitle = getActionParameter(requestContext, "short-title");
            if (isFieldModified(shortTitle, page.getShortTitle()))
                page.setShortTitle(shortTitle);
            String layoutDecorator = getActionParameter(requestContext, "layout-decorator");
            if (isFieldModified(layoutDecorator, page.getDefaultDecorator(Fragment.LAYOUT)))
            {
                if (isBlank(layoutDecorator))
                    layoutDecorator = null; 
                page.setDefaultDecorator(layoutDecorator, Fragment.LAYOUT);
            }
            String portletDecorator = getActionParameter(requestContext, "portlet-decorator");
            if (isFieldModified(portletDecorator, page.getDefaultDecorator(Fragment.PORTLET)))
            {
                if (isBlank(portletDecorator))
                    portletDecorator = null;                 
                page.setDefaultDecorator(portletDecorator, Fragment.PORTLET);
            }
            String theme = getActionParameter(requestContext, "theme");
            if (isFieldModified(theme, page.getSkin()))
            {
                if (isBlank(theme))
                    theme = null;                 
                page.setSkin(theme);
            }
            String hidden = getActionParameter(requestContext, "hidden");
            if (isBooleanModified(hidden, page.isHidden()))
                page.setHidden(!page.isHidden());                                    
            count++;
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }        
        return count;
    }
    
}
