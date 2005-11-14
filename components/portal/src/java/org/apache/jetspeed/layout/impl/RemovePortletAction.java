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
import org.apache.jetspeed.layout.PortletPlacementManager;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.request.RequestContext;


/**
 * Remove Portlet portlet placement action
 * 
 * @author <a>David Gurney </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class RemovePortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{

    /** Logger */
    protected Log log = LogFactory.getLog(RemovePortletAction.class);

    public RemovePortletAction(String template, String errorTemplate)
            throws PipelineException
    {
        super(template, errorTemplate);
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean a_bSuccess = true;

        try
        {
            resultMap.put(ACTION, "remove");

            // Get the necessary parameters off of the request
            String a_sPortletId = requestContext
                    .getRequestParameter(PORTLETID);
            if (a_sPortletId == null) { throw new Exception(
                    "portlet id not provided"); }

            resultMap.put(PORTLETID, a_sPortletId);

            // Use the Portlet Placement Manager to accomplish the removal
            PortletPlacementManager ppm = new PortletPlacementManagerImpl(requestContext);
            Fragment a_oFragment = ppm.getFragmentById(a_sPortletId);
            Coordinate a_oCoordinate = ppm.remove(a_oFragment);

            // Build the results for the response
            resultMap.put(STATUS, "success");
            resultMap.put(OLDCOL, String.valueOf(a_oCoordinate.getOldCol()));
            resultMap.put(OLDROW, String.valueOf(a_oCoordinate.getOldRow()));
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while adding a portlet", e);

            // Return a failure indicator
            a_bSuccess = false;
        }

        return a_bSuccess;
    }
}
