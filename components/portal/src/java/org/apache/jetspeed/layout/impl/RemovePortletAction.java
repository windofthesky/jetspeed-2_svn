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
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.request.RequestContext;


/**
 * Remove Portlet portlet placement action
 * 
 * AJAX Parameters: 
 *    id = the fragment id of the portlet to remove
 *    page = (implied in the URL)
 *    
 * @author <a>David Gurney </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class RemovePortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    private PageManager pageManager = null;

    /** Logger */
    protected Log log = LogFactory.getLog(RemovePortletAction.class);

    public RemovePortletAction(String template, String errorTemplate)
            throws PipelineException
    {
        this(template, errorTemplate, null);
    }

    public RemovePortletAction(String template, String errorTemplate, PageManager pageManager)
    throws PipelineException
    {
        super(template, errorTemplate);
        this.pageManager = pageManager;
    }
    
    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;

        try
        {
            resultMap.put(ACTION, "remove");

            // Get the necessary parameters off of the request
            String portletId = requestContext.getRequestParameter(PORTLETID);
            if (portletId == null) 
            { 
                throw new Exception("portlet id not provided"); 
            }

            resultMap.put(PORTLETID, portletId);

            if (false == checkAccess(requestContext, SecuredResource.EDIT_ACTION))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to edit page");
                return success;
            }
            
            // Use the Portlet Placement Manager to accomplish the removal
            PortletPlacementContext placement = new PortletPlacementContextImpl(requestContext);
            Fragment fragment = placement.getFragmentById(portletId);
            Coordinate coordinate = placement.remove(fragment);

            Page page = placement.syncPageFragments();
            if (pageManager != null)
                pageManager.updatePage(page);
            
            // Build the results for the response
            resultMap.put(STATUS, "success");
            resultMap.put(OLDCOL, String.valueOf(coordinate.getOldCol()));
            resultMap.put(OLDROW, String.valueOf(coordinate.getOldRow()));
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while adding a portlet", e);
            resultMap.put(REASON, e.toString());
            // Return a failure indicator
            success = false;
        }

        return success;
    }
}
