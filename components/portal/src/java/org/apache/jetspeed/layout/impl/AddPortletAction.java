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
    extends MovePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(AddPortletAction.class);
    protected GetPortletsAction getPortletsAction = null;
    protected boolean allowDuplicatePortlets = true;

    public AddPortletAction(String template, String errorTemplate, GetPortletsAction getPortletsAction)
        throws AJAXException
    {
        this(template, errorTemplate, null, null, getPortletsAction, true);
    }

    public AddPortletAction(String template, 
                            String errorTemplate, 
                            PageManager pageManager,
                            PortletActionSecurityBehavior securityBehavior,
                            GetPortletsAction getPortletsAction)
        throws AJAXException
    {
        this(template, errorTemplate, pageManager, securityBehavior, getPortletsAction, true);
    }

    public AddPortletAction(String template, 
                            String errorTemplate, 
                            PageManager pageManager,
                            PortletActionSecurityBehavior securityBehavior,
                            GetPortletsAction getPortletsAction,
                            boolean allowDuplicatePortlets)
        throws AJAXException
    {
        super(template, errorTemplate, pageManager, securityBehavior); 
        this.getPortletsAction = getPortletsAction;
        this.allowDuplicatePortlets = allowDuplicatePortlets;
    }
    
    protected boolean runAction( RequestContext requestContext, Map resultMap, boolean batch ) throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "add");
            // Get the necessary parameters off of the request
            String portletId = getActionParameter(requestContext, PORTLETID);
            if (portletId == null) 
            { 
                throw new RuntimeException("portlet id not provided"); 
            }
            resultMap.put(PORTLETID, portletId);
            
            // Verify that the specified portlet id is valid and accessible
            // If the portletid is not valid an exception will be thrown
            verifyPortletId(requestContext, portletId);
            
            if(allowDuplicatePortlets == false) {
            	// Check to see if this portlet has already been added to the page
            	checkForDuplicatePortlet(requestContext, resultMap, portletId);
            }
            
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
            
            Page page = requestContext.getPage();
            String layoutId = getActionParameter(requestContext, LAYOUTID);
            Fragment fragment = pageManager.newFragment();
            fragment.setType(Fragment.PORTLET);
            fragment.setName(portletId);
            //fragment.setLayoutColumn(iCol);
            //fragment.setLayoutRow(iRow);
            
            Fragment placeInLayoutFragment = null;
            if ( layoutId != null && layoutId.length() > 0 )
            {
                placeInLayoutFragment = page.getFragmentById( layoutId );
                if ( placeInLayoutFragment == null )
                {
                    throw new Exception( "layout id not found: " + layoutId );
                }
            }
            else
            {
                placeInLayoutFragment = page.getRootFragment();
            }

            success = placeFragment( requestContext,
                                     pageManager,
                                     batch,
                                     resultMap,
                                     fragment,
                                     placeInLayoutFragment ) ;

            resultMap.put(STATUS, status);
            resultMap.put(PORTLETENTITY, fragment.getId());            
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
    
    protected void checkForDuplicatePortlet(RequestContext requestContext, Map resultMap, String portletId)
    throws AJAXException
    {
    	// Look at each portlet currently on the page
    	Page page = requestContext.getPage();
    	
    	boolean duplicateFound = isDuplicateFragment(page.getRootFragment(), portletId);
    	
    	// Throw an exception if a duplicate is found
    	if(duplicateFound == true) {
    		throw new AJAXException(portletId + " is already on the page, duplicates are not allowed");
    	}
    }

    protected boolean isDuplicateFragment(Fragment fragment, String portletId) {
    	if(fragment != null) {
	    	// Get the name of this fragment
	    	String fragmentName = fragment.getName();
	    	if(fragmentName.equals(portletId)) {
	    		// Duplicate was found
	    		return true;
	    	} else {
	    		// Process the child fragments if found
	    		List childFragments = fragment.getFragments();
	    		if(childFragments != null) {
	    			for(int i = 0; i < childFragments.size(); i++) {
	    				// Recursively call this method again to process the child fragments
	    				if(isDuplicateFragment((Fragment)childFragments.get(i),portletId) == true) {
	    					// No need to continue to loop if a duplicate was found
	    					return true;
	    				}
	    			}
	    		}
	    	}
    	}
    	// We will only get here if no duplicates were found
    	return false;
    }
}
