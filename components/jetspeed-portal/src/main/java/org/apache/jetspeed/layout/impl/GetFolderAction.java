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

/**
 * Retrieve a single page
 *
 * AJAX Parameters: 
 *    folder = the path of the folder to retrieve information on 
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetFolderAction 
    extends BaseGetResourceAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(GetFolderAction.class);
    
    public GetFolderAction(String template, 
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
            resultMap.put(ACTION, "getfolder");
            if (false == checkAccess(requestContext, JetspeedActions.VIEW))
            {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to get portlets");
                    return success;
            }                                    
            Folder folder = retrieveFolder(requestContext);            
            resultMap.put(STATUS, status);
            resultMap.put(FOLDER, folder);
            putSecurityInformation(resultMap, folder);
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting folder info", e);

            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    protected Folder retrieveFolder(RequestContext requestContext)
    throws Exception
    {        
        String folderName = getActionParameter(requestContext, FOLDER);
        if (folderName == null)
        {
            folderName = "/";
        }
        Folder folder = pageManager.getFolder(folderName);
        return folder;
    }
    
    
}
