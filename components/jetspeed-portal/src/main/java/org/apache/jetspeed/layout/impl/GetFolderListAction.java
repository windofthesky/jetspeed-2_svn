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
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Get the immediate contents of a folder in JSON format 
 *
 * AJAX Parameters: 
 *    folder: full path to the folder 
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetFolderListAction 
    extends BaseGetResourceAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(GetThemesAction.class);
    
    public GetFolderListAction(String template, 
            String errorTemplate,
            PageManager pageManager,
            PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, pageManager, securityBehavior);        
    }

    public boolean run(RequestContext requestContext, Map<String,Object> resultMap)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "getfolderlist");
            if (false == checkAccess(requestContext, JetspeedActions.VIEW))
            {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to get folderlist");
                    return success;
            }                     
            String data = getActionParameter(requestContext, "data");
            StringTokenizer tokenizer = new StringTokenizer(data, "[{:\"");
            String folderName = null;            
            while (tokenizer.hasMoreTokens())
            {
                String token = tokenizer.nextToken();
                if (token.equals("widgetId"))
                {
                    folderName = tokenizer.nextToken();
                    break;
                }
            }
            String format = getActionParameter(requestContext, FORMAT);
            if (format == null)
                format = "json";
            if (folderName == null)
            {
                success = false;
                resultMap.put(REASON, "Folder name not found.");
                return success;                
            }
            resultMap.put(FORMAT, format);
            Folder folder = pageManager.getFolder(folderName);
            resultMap.put(FOLDER, folder);
            resultMap.put("folders", folder.getFolders().iterator());
            resultMap.put("pages", folder.getPages().iterator());
            resultMap.put("links", folder.getLinks().iterator());
            resultMap.put(STATUS, status);            
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting theme info", e);
            // Return a failure indicator
            success = false;
        }

        return success;
    }
    
    
}
