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
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.request.RequestContext;

/**
 * Add Portlet portlet placement action
 * 
 * @author <a>David Gurney </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class AddPortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{

    /** Logger */
    protected Log log = LogFactory.getLog(AddPortletAction.class);

    public AddPortletAction(String template, String errorTemplate)
    {
        super(template, errorTemplate);
    }

    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        boolean success = true;

        try
        {
            resultMap.put(ACTION, "add");
            // Get the necessary parameters off of the request
            String portletId = requestContext.getRequestParameter(PORTLETID);

            if (portletId == null) 
            { 
                throw new RuntimeException("portlet id not provided"); 
            }
            resultMap.put(PORTLETID, portletId);

            // These are optional parameters
            String col = requestContext.getRequestParameter(COL);
            String row = requestContext.getRequestParameter(ROW);

            // Convert the col and row into integers
            int iCol = 0;
            int iRow = 0;

            if (col != null)
            {
                iCol = Integer.parseInt(col);
                resultMap.put(NEWCOL, new Integer(iCol));
            }

            if (row != null)
            {
                iRow = Integer.parseInt(row);
                resultMap.put(NEWROW, new Integer(iRow));
            }

            // todo .. work with the page manager to add the portlet

            resultMap.put(STATUS, "success");
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while adding a portlet", e);

            // Return a failure indicator
            success = false;
        }

        return success;
    }
}
