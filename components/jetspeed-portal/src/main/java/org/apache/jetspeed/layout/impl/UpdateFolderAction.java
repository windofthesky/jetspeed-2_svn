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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Update Folder action -- updates various parts of the PSML folder
 * 
 * AJAX Parameters: 
 *    action = updatefolder
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
public class UpdateFolderAction 
    extends BaseSiteUpdateAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(UpdateFolderAction.class);

    public UpdateFolderAction(String template, 
                            String errorTemplate, 
                            PageManager pm,
                            PortletActionSecurityBehavior securityBehavior)
                            
    {
        super(template, errorTemplate, pm, securityBehavior); 
    }
    
    public boolean run(RequestContext requestContext, Map<String,Object> resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "updatefolder");
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
            Folder folder = null; 
            if (!method.equals("add"))
            {
                folder = pageManager.getFolder(path);
            }       
            else
            {
                if (pageManager.folderExists(path))
                {
                    success = false;
                    resultMap.put(REASON, "Can't create: Folder already exists: " + path);                
                    return success;                
                }
            }            
            if (method.equals("info"))
            {
                count = updateInformation(requestContext, resultMap, folder, path);
            }
            else if (method.equals("add-meta"))
            {
                count = insertMetadata(requestContext, resultMap, folder);
            }
            else if ( method.equals("update-meta"))
            {
                count = updateMetadata(requestContext, resultMap, folder);
            }
            else if (method.equals("remove-meta"))
            {
                count = removeMetadata(requestContext, resultMap, folder);
            }
            else if (method.equals("update-secref"))
            {
                count = updateSecurityReference(requestContext, resultMap, folder);
            }            
            else if (method.equals("add-secref"))
            {
                count = insertSecurityReference(requestContext, resultMap, folder);
            }
            else if (method.equals("remove-secref"))
            {
                count = removeSecurityReference(requestContext, resultMap, folder);
            }
            else if (method.equals("remove-secdef"))
            {
                count = removeSecurityDef(requestContext, resultMap, folder);
            }                        
            else if (method.equals("add"))
            {
                folder = pageManager.newFolder(path);
                folder.setTitle(getActionParameter(requestContext, "title"));
                String s = getActionParameter(requestContext, "short-title");
                if (!isBlank(s))
                    folder.setShortTitle(s);
                count++;                
            }
            else if (method.equals("copy"))
            {            	   
            	String destination = getActionParameter(requestContext, "destination");
            	String name = getActionParameter(requestContext, RESOURCE_NAME);
            	destination = destination + Folder.PATH_SEPARATOR + name;
            	pageManager.deepCopyFolder(folder,destination,null);
            }
            else if (method.equals("move"))
            {            	
            	String destination = getActionParameter(requestContext, "destination");
            	String name = getActionParameter(requestContext, RESOURCE_NAME);            	
            	destination = destination + Folder.PATH_SEPARATOR + name;
            	pageManager.deepCopyFolder(folder,destination,null,true);            	
            	pageManager.removeFolder(folder);
            }            
            else if (method.equals("remove"))
            {
                pageManager.removeFolder(folder);
            }                        
            else
            {
                success = false;
                resultMap.put(REASON, "Unsupported Site Update method: " + method);                
                return success;                
            }
            if (count > 0)
            {
                pageManager.updateFolder(folder);                
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
    
    protected int updateInformation(RequestContext requestContext, Map<String,Object> resultMap, Node node, String path)
    throws AJAXException    
    {
        int count = 0;
        try
        {
            Folder folder = (Folder)node;            
            String title = getActionParameter(requestContext, "title");
            if (isFieldModified(title, folder.getTitle()))
                folder.setTitle(title);
            String shortTitle = getActionParameter(requestContext, "short-title");
            if (isFieldModified(shortTitle, folder.getShortTitle()))
                folder.setShortTitle(shortTitle);
            String layoutDecorator = getActionParameter(requestContext, "layout-decorator");
            if (isFieldModified(layoutDecorator, folder.getDefaultDecorator(Fragment.LAYOUT)))
            {
                if (isBlank(layoutDecorator))
                    layoutDecorator = null;                 
                folder.setDefaultDecorator(layoutDecorator, Fragment.LAYOUT);
            }
            String portletDecorator = getActionParameter(requestContext, "portlet-decorator");
            if (isFieldModified(portletDecorator, folder.getDefaultDecorator(Fragment.PORTLET)))
            {
                if (isBlank(portletDecorator))
                    portletDecorator = null;                 
                folder.setDefaultDecorator(portletDecorator, Fragment.PORTLET);
            }
            String defaultPage = getActionParameter(requestContext, "default-page");
            if (isFieldModified(defaultPage, folder.getDefaultPage()))
                folder.setDefaultPage(defaultPage);                        
            String hidden = getActionParameter(requestContext, "hidden");
            if (isBooleanModified(hidden, folder.isHidden()))
                folder.setHidden(!folder.isHidden());                                    
            count++;
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }        
        return count;
    }
    
}
