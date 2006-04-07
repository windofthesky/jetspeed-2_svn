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

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;

/**
 * Add Portlet portlet placement action
 * 
 * AJAX Parameters: 
 *    id = portlet full name (pa::portletName) to be added
 *    page = (implied in the URL)
 * Optional Parameters:  
 *    row = the new row to move to
 *    col = the new column to move to
 *    
 * @author <a>David Gurney </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class AddPortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(AddPortletAction.class);
    protected GetPortletsAction getPortletsAction = null;

    public AddPortletAction(String template, String errorTemplate, GetPortletsAction getPortletsAction)
    {
        this(template, errorTemplate, null, null, getPortletsAction);
    }

    public AddPortletAction(String template, 
                            String errorTemplate, 
                            PageManager pageManager,
                            PortletActionSecurityBehavior securityBehavior,
                            GetPortletsAction getPortletsAction)
    {
        super(template, errorTemplate, pageManager, securityBehavior); 
        this.getPortletsAction = getPortletsAction;
    }
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "add");
            // Get the necessary parameters off of the request
            String portletId = requestContext.getRequestParameter(PORTLETID);
            if (portletId == null) 
            { 
                throw new RuntimeException("portlet id not provided"); 
            }
            // Verify that the specified portlet id is valid and accessible
            // If the portletid is not valid an exception will be thrown
            verifyPortletId(requestContext, portletId);
            
            resultMap.put(PORTLETID, portletId);
            if (false == checkAccess(requestContext, JetspeedActions.EDIT))
            {
                if (!createNewPageOnEdit(requestContext))
                {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to edit page");                
                    return success;
                }
                status = "refresh";
            }           
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
            // Use the Portlet Placement Manager to accomplish the removal
            PortletPlacementContext placement = new PortletPlacementContextImpl(requestContext);
            Fragment fragment = pageManager.newFragment();
            fragment.setType(Fragment.PORTLET);
            fragment.setName(portletId);
            fragment.setLayoutColumn(iCol);
            fragment.setLayoutRow(iRow);            
            Coordinate coordinate = placement.add(fragment, new CoordinateImpl(iCol, iRow, iCol, iRow));
            Page page = placement.syncPageFragments();                                                            
            // TODO: this does not handle nested layouts            
            Fragment root = requestContext.getPage().getRootFragment();
            root.getFragments().add(fragment);            
            pageManager.updatePage(page);
            resultMap.put(STATUS, status);
            resultMap.put(NEWCOL, String.valueOf(coordinate
                    .getNewCol()));
            resultMap.put(NEWROW, String.valueOf(coordinate
                    .getNewRow()));            
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
    
    protected void verifyPortletId(RequestContext requestContext, String portletId) throws Exception
    {
        // Get the list of valid portlets from the getPortletAction
        List portletList = getPortletsAction.retrievePortlets(requestContext, null);
        if(portletList != null) {
            for(int i = 0; i < portletList.size(); i++) {
                PortletInfo portletInfo = (PortletInfo)portletList.get(i);
                if(portletInfo != null) {
                    if(portletInfo.getName().equalsIgnoreCase(portletId)) {
                        // A match was found there is no need to continue
                        return;
                    }
                }
            }
        }
        // If we got here, then no match was found
        throw new Exception(portletId + " is not a valid portlet or not allowed for this user");
    }
}
