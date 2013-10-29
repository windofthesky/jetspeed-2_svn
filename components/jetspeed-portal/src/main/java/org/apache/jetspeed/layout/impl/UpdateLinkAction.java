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
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Update Link action -- updates various parts of the PSML link
 * 
 * AJAX Parameters: 
 *    action = updatelink
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
public class UpdateLinkAction 
    extends BaseSiteUpdateAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(UpdateLinkAction.class);

    public UpdateLinkAction(String template, 
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
            resultMap.put(ACTION, "updatelink");
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
            Link link = null; 
            if (!method.equals("add"))
            {
                link = pageManager.getLink(path);                
            }                        
            else
            {
                if (pageManager.linkExists(path))
                {
                    success = false;
                    resultMap.put(REASON, "Can't create: Link already exists: " + path);                
                    return success;                
                }
            }
            if (method.equals("info"))
            {
                count = updateInformation(requestContext, resultMap, link, path);
            }
            else if (method.equals("update-meta"))
            {
                count = updateMetadata(requestContext, resultMap, link);
            }
            else if (method.equals("add-meta"))
            {
                count = insertMetadata(requestContext, resultMap, link);
            }
            else if (method.equals("remove-meta"))
            {
                count = removeMetadata(requestContext, resultMap, link);
            }
            else if (method.equals("add-secref"))
            {
                count = insertSecurityReference(requestContext, resultMap, link);
            }
            else if (method.equals("update-secref"))
            {
                count = updateSecurityReference(requestContext, resultMap, link);
            }                        
            else if (method.equals("remove-secref"))
            {
                count = removeSecurityReference(requestContext, resultMap, link);
            }
            else if (method.equals("remove-secdef"))
            {
                count = removeSecurityDef(requestContext, resultMap, link);
            }                        
            else if (method.equals("add"))
            {
                link = pageManager.newLink(path);
                link.setTitle(getActionParameter(requestContext, "title"));
                String s = getActionParameter(requestContext, "short-title");
                if (!isBlank(s))
                    link.setShortTitle(s);
                link.setUrl(getActionParameter(requestContext, "url"));
                count++;
            }
            else if (method.equals("copy"))
            {            	
            	String destination = getActionParameter(requestContext, "destination");
            	String name = getActionParameter(requestContext, RESOURCE_NAME);
            	destination = destination + Folder.PATH_SEPARATOR + name;
            	Link newLink = pageManager.copyLink(link, destination);
            	pageManager.updateLink(newLink);
            }
            else if (method.equals("move"))
            {            	
            	String destination = getActionParameter(requestContext, "destination");
            	String name = getActionParameter(requestContext, RESOURCE_NAME);            	
            	destination = destination + Folder.PATH_SEPARATOR + name;
            	Link newLink = pageManager.copyLink(link, destination);            	
            	pageManager.updateLink(newLink);
            	pageManager.removeLink(link);
            	
            } 
            else if (method.equals("remove"))
            {
                pageManager.removeLink(link);
            }                        
            else
            {
                success = false;
                resultMap.put(REASON, "Unsupported Site Update method: " + method);                
                return success;                
            }
            if (count > 0)
            {
                pageManager.updateLink(link);                
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
            Link link = (Link)node;            
            String title = getActionParameter(requestContext, "title");
            if (isFieldModified(title, link.getTitle()))
                link.setTitle(title);
            String shortTitle = getActionParameter(requestContext, "short-title");
            if (isFieldModified(shortTitle, link.getShortTitle()))
                link.setShortTitle(shortTitle);
            String url = getActionParameter(requestContext, "url");
            if (isFieldModified(url, link.getUrl()))
                link.setUrl(url);
            String target = getActionParameter(requestContext, "target");
            if (isFieldModified(target, link.getTarget()))
                link.setTarget(target);                        
            String hidden = getActionParameter(requestContext, "hidden");
            if (isBooleanModified(hidden, link.isHidden()))
                link.setHidden(!link.isHidden());                                    
            count++;
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }        
        return count;
    }
    
}
