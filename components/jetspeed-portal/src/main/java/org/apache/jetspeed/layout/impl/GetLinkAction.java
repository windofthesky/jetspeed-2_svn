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
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Retrieve a single link
 *
 * AJAX Parameters: 
 *    link = the path of the link to retrieve information on 
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetLinkAction 
    extends BaseGetResourceAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(GetLinkAction.class);
    
    public GetLinkAction(String template, 
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
            resultMap.put(ACTION, "getlink");
            if (false == checkAccess(requestContext, JetspeedActions.VIEW))
            {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to get link");
                    return success;
            }                                    
            Link link = retrieveLink(requestContext);            
            resultMap.put(STATUS, status);            
            resultMap.put(LINK, link);
            // resultMap.put(METADATA, link.getMetadata().getFields());
            putSecurityInformation(resultMap, link);            
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting link info", e);
            resultMap.put(REASON, e.getMessage());
            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    protected Link retrieveLink(RequestContext requestContext)
    throws Exception
    {        
        String linkName = getActionParameter(requestContext, LINK);
        if (linkName == null)
        {
            linkName = "/";
        }
        Link link = pageManager.getLink(linkName);
        return link;
    }        
}
