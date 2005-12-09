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
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;

/**
 * Get Page retrieves a page from the Page Manager store and PSML format
 *
 * AJAX Parameters: 
 *    page = the path and name of the page ("/_user/ronaldino/goals.psml")
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetPageAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    /** Logger */
    protected Log log = LogFactory.getLog(GetPageAction.class);

    private PageManager pageManager = null;
    
    public GetPageAction(String template, 
            String errorTemplate, 
            PageManager pageManager)
    {
        super(template, errorTemplate);
        this.pageManager = pageManager;
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;

        try
        {
            resultMap.put(ACTION, "getpage");

            if (false == checkAccess(requestContext, SecuredResource.VIEW_ACTION))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to view page");
                return success;
            }
            
            String filter = requestContext.getRequestParameter(FILTER);            
                        
            Page page = requestContext.getPage();
            
            resultMap.put(STATUS, "success");

            resultMap.put(PAGE, page);

        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting page", e);

            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
        
}
