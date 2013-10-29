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
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

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
    protected Logger log = LoggerFactory.getLogger(AddPortletAction.class);
    protected GetPortletsAction getPortletsAction = null;
    protected boolean allowDuplicatePortlets = true;

    public AddPortletAction( String template, String errorTemplate, PortletRegistry registry, GetPortletsAction getPortletsAction )
        throws AJAXException
    {
        this( template, errorTemplate, registry, null, null, getPortletsAction, true );
    }

    public AddPortletAction( String template, 
                             String errorTemplate, 
                             PortletRegistry registry,
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior,
                             GetPortletsAction getPortletsAction )
        throws AJAXException
    {
        this( template, errorTemplate, registry, pageManager, securityBehavior, getPortletsAction, true );
    }

    public AddPortletAction( String template, 
                             String errorTemplate,
                             PortletRegistry registry,
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior,
                             GetPortletsAction getPortletsAction,
                             boolean allowDuplicatePortlets )
        throws AJAXException
    {
        super( template, errorTemplate, registry, pageManager, securityBehavior );
        this.getPortletsAction = getPortletsAction;
        this.allowDuplicatePortlets = allowDuplicatePortlets;
    }
    
    protected boolean runAction( RequestContext requestContext, Map<String,Object> resultMap, boolean batch ) throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put( ACTION, "add" );
            // Get the necessary parameters off of the request
            String portletId = getActionParameter( requestContext, PORTLETID );
            if (portletId == null) 
            { 
                throw new RuntimeException( "portlet id not provided" ); 
            }
            resultMap.put( PORTLETID, portletId );
            
            // Verify that the specified portlet id is valid and accessible
            // If the portletid is not valid an exception will be thrown
            verifyPortletId( requestContext, portletId );
            
            if( allowDuplicatePortlets == false )
            {
            	// Check to see if this portlet has already been added to the page
            	checkForDuplicatePortlet( requestContext, resultMap, portletId );
            }
            
            String layoutId = getActionParameter( requestContext, LAYOUTID );
            
            if ( false == checkAccess( requestContext, JetspeedActions.EDIT ) )
            {
            	NestedFragmentContext addToFragmentContext = null;
            	if ( layoutId != null && layoutId.length() > 0 )
            	{
            		ContentPage page = requestContext.getPage();
            		ContentFragment fragment = page.getFragmentById( layoutId );
            		if ( fragment == null )
            		{
            			success = false;
            			resultMap.put( REASON, "Specified layout fragment not found: " + layoutId );
            			return success;
            		}
            	
            		try
            		{
            			addToFragmentContext = new NestedFragmentContext( fragment, page, getPortletRegistry() );
            		}
            		catch ( Exception ex )
            		{
            			log.error( "Failure to construct nested context for fragment " + layoutId, ex );
            			success = false;
            			resultMap.put( REASON, "Cannot construct nested context for specified layout fragment" );
            			return success;
            		}
            	}
            	
                if ( ! createNewPageOnEdit( requestContext ) )
                {
                    success = false;
                    resultMap.put( REASON, "Insufficient access to edit page" );
                    return success;
                }
                status = "refresh";

                if ( addToFragmentContext != null )
                {
                	ContentPage newPage = requestContext.getPage();

                	// using NestedFragmentContext, find portlet id for copy of target portlet in the new page 
                	ContentFragment newFragment = null;
                	try
                	{
                		newFragment = addToFragmentContext.getFragmentOnNewPage( newPage, getPortletRegistry() );
                	}
                	catch ( Exception ex )
                	{
                		log.error( "Failure to locate copy of fragment " + layoutId, ex );
                		success = false;
                		resultMap.put( REASON, "Failed to find new fragment for specified layout id: " + layoutId );
                		return success;
                	}
                	layoutId = newFragment.getId();
                }
            }
            
            ContentPage page = requestContext.getPage();
            ContentFragment placeInLayoutFragment = null;
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
            
            ContentFragment fragment = placeInLayoutFragment.addPortlet(ContentFragment.PORTLET, portletId);
            success = placeFragment( requestContext,
                                     batch,
                                     resultMap,
                                     fragment,
                                     placeInLayoutFragment ) ;
            
            resultMap.put( PORTLETENTITY, fragment.getId() );
            if ( success )
            {
            	resultMap.put( STATUS, status );
            }
        } 
        catch ( Exception e )
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
    	List<PortletInfo> portletList = getPortletsAction.retrievePortlets(requestContext, null);
    	if(portletList != null) {
    		for(int i = 0; i < portletList.size(); i++) {
    			PortletInfo portletInfo = portletList.get(i);
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
    
    protected void checkForDuplicatePortlet(RequestContext requestContext, Map<String,Object> resultMap, String portletId)
    throws AJAXException
    {
    	// Look at each portlet currently on the page
    	ContentPage page = requestContext.getPage();
    	
    	boolean duplicateFound = isDuplicateFragment(page.getRootFragment(), portletId);
    	
    	// Throw an exception if a duplicate is found
    	if(duplicateFound == true) {
    		throw new AJAXException(portletId + " is already on the page, duplicates are not allowed");
    	}
    }

    protected boolean isDuplicateFragment(ContentFragment fragment, String portletId) {
    	if(fragment != null) {
	    	// Get the name of this fragment
	    	String fragmentName = fragment.getName();
	    	if(fragmentName.equals(portletId)) {
	    		// Duplicate was found
	    		return true;
	    	} else {
	    		// Process the child fragments if found
	    		List<ContentFragment> childFragments = fragment.getFragments();
	    		if(childFragments != null) {
	    			for(int i = 0; i < childFragments.size(); i++) {
	    				// Recursively call this method again to process the child fragments
	    				if(isDuplicateFragment(childFragments.get(i),portletId) == true) {
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
