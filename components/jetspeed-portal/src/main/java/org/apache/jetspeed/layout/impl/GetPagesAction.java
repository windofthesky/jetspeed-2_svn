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
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Get Pages retrieves all pages for the given folder
 *
 * AJAX Parameters: 
 *    folder = the path of folder containing the pages 
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetPagesAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants, Comparator
{
    protected static final Logger log = LoggerFactory.getLogger(GetPortletsAction.class);
    
    public GetPagesAction(String template, 
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
            resultMap.put(ACTION, "getpages");
            if (false == checkAccess(requestContext, JetspeedActions.VIEW))
            {
//                if (!createNewPageOnEdit(requestContext))                
//                {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to get portlets");
                    return success;
//                }
//                status = "refresh";
            }                                    
            List pages = retrievePages(requestContext);            
            resultMap.put(STATUS, status);
            resultMap.put(PAGES, pages);
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting portlet info", e);

            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    protected List retrievePages(RequestContext requestContext)
    {        
        List list = new ArrayList();
 
        String folderName = getActionParameter(requestContext, FOLDER);
        if (folderName == null)
        {
            return list;
        }
        try
        {
            Folder folder = pageManager.getFolder(folderName);
            Iterator it = folder.getPages().iterator();
            while (it.hasNext())
            {
                Page page = (Page)it.next();
                list.add(page);
            }
            Collections.sort(list, this);
        }
        catch (Exception e)
        {            
        }
        return list;
    }
    
    
    public int compare(Object obj1, Object obj2)
    {
        Page page1 = (Page)obj1;
        Page page2 = (Page)obj2;
        String name1 = page1.getName();
        String name2 = page2.getName();
        name1 = (name1 == null) ? "unknown" : name1;
        name2 = (name2 == null) ? "unknown" : name2;
        return name1.compareTo(name2);
    }
}
