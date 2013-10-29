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
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.decoration.PageActionAccess;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Changes the window state or portlet mode for a given portlet window
 *
 * AJAX Parameters: 
 *    id = the fragment id of the portlet to move
 *    page = (implied in the URL)
 *    state = the new window state
 *    mode = the new portlet mode
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class ChangePortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected static final Logger log = LoggerFactory.getLogger(ChangePortletAction.class);
    protected String action;
    protected Map<String,String> validWindowStates = new HashMap();
    protected Map<String,String> validPortletModes = new HashMap();
    
    public ChangePortletAction(String template, 
            String errorTemplate, 
            String action)            
    throws AJAXException    
    {
        this(template, errorTemplate, action, null, null);
    }
    
    public ChangePortletAction(String template, 
                             String errorTemplate, 
                             String action,
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior)
    throws AJAXException
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.action = action;
        
        // Build the maps of allowed (internal) modes and states
        Iterator modes = JetspeedActions.getStandardPortletModes().iterator();        
        while (modes.hasNext())
        {
            String mode = modes.next().toString();
            this.validPortletModes.put(mode, mode);
        }
        modes = JetspeedActions.getExtendedPortletModes().iterator();
        while (modes.hasNext())
        {
            String mode = modes.next().toString();
            this.validPortletModes.put(mode, mode);
        }
        Iterator states = JetspeedActions.getStandardWindowStates().iterator();        
        while (states.hasNext())
        {
            String state = states.next().toString();
            this.validWindowStates.put(state, state);
        }        
        states = JetspeedActions.getExtendedWindowStates().iterator();        
        while (states.hasNext())
        {
            String state = states.next().toString();
            this.validWindowStates.put(state, state);
        }        
    }

    public boolean runBatch(RequestContext requestContext, Map<String,Object> resultMap) throws AJAXException
    {
        return runAction(requestContext, resultMap, true);
    }    
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        return runAction(requestContext, resultMap, false);
    }
    
    public boolean runAction(RequestContext requestContext, Map<String,Object> resultMap, boolean batch)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, action);
            // Get the necessary parameters off of the request
            String fragmentId = getActionParameter(requestContext, FRAGMENTID);
            if (fragmentId == null) 
            { 
                throw new Exception("fragment id not provided"); 
            }
            resultMap.put(FRAGMENTID, fragmentId);
            
            ContentPage page = requestContext.getPage();            
            ContentFragment fragment = page.getFragmentById(fragmentId);
            
            if ( fragment == null )
            {
            	throw new Exception( "fragment specified by id cannot be found" );
            }
            String requestedState = getActionParameter(requestContext, WINDOW_STATE);
            String requestedMode = getActionParameter(requestContext, PORTLET_MODE);    
            if ( "layout".equals( fragment.getType() ) )
            {
            	if ( ! fragment.getId().equals( page.getRootFragment().getId() ) )
            	{
            		throw new Exception( "for layout fragments, change action applies to only to the root layout fragment (i.e. it does not apply to nested layout fragments)" );
            	}
            	PageActionAccess pageActionAccess = (PageActionAccess)requestContext.getAttribute( PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE );
            	if ( pageActionAccess == null )
            	{
            		throw new Exception( "cannot change action for root layout fragment due to null PageActionAccess object" );
            	}
            	//pageActionAccess.
            	PortletWindow window = requestContext.getPortletWindow(fragment);
            	if (!window.isValid())
            	{
            	    throw new Exception("Failed to retrieve Portlet Definition for: "+ fragment.getId() + ", " + fragment.getName());
            	}
            	PortletMode currentMode = requestContext.getPortalURL().getNavigationalState().getMode( window );
            	WindowState currentState = requestContext.getPortalURL().getNavigationalState().getState( window );
            	
            	boolean requestedModeAlreadySet = false;
            	if ( requestedMode == null )
            	{
            		requestedModeAlreadySet = true;
            	}
            	else
            	{
            		if ( requestedMode.equals( PortletMode.EDIT.toString() ) )
            		{
            			if( pageActionAccess.isEditing() )
            				requestedModeAlreadySet = true;
            			else
            			{
            				if ( pageActionAccess.isEditAllowed())
            				{
            					pageActionAccess.setEditing( true );
            					resultMap.put(STATUS, status);
            					resultMap.put(OLD_PORTLET_MODE, currentMode.toString());
            					resultMap.put(PORTLET_MODE, requestedMode);
            				}
            				else
            				{
            					throw new Exception( "permissions do no allow page edit" );
            				}
            			}
            		}
            		else if ( requestedMode.equals( PortletMode.VIEW.toString() ) )
            		{
            			pageActionAccess.setEditing( false );
            			//if ( currentMode.equals( PortletMode.HELP ) )
            			resultMap.put(STATUS, status);
            			resultMap.put(OLD_PORTLET_MODE, currentMode.toString());
            			resultMap.put(PORTLET_MODE, requestedMode);
            		}
            		else
            		{
            			requestedModeAlreadySet = true;
            		}
            	}
            	if ( requestedModeAlreadySet )
            	{
           			resultMap.put(STATUS, status);
           			resultMap.put(OLD_PORTLET_MODE, currentMode.toString());
           			resultMap.put(PORTLET_MODE, currentMode.toString());
           		}
            }
            else
            {
	            if (requestedState == null && requestedMode == null) 
	            { 
	                throw new Exception("portlet window state or mode not provided"); 
	            }           
	            if (requestedState != null && !isValidWindowState(requestedState))
	            {
	                throw new Exception("portlet window state " + requestedState + " is not supported");
	            }
	            if (requestedMode != null && !isValidPortletMode(requestedMode))
	            {
	                throw new Exception("portlet mode " + requestedMode + " is not supported");
	            }
	
	            
	            String oldState = fragment.getState();
	            String oldMode = fragment.getMode();
	            
	            // Now Change the transient navigational state
	            MutableNavigationalState navState = (MutableNavigationalState)requestContext.getPortalURL().getNavigationalState();
	            PortletWindow portletWindow = requestContext.getPortletWindow(fragment);
	            if (portletWindow != null)
	            {
	                oldState = navState.getState(portletWindow).toString();
	                oldMode =  navState.getMode(portletWindow).toString();
	                if (requestedState != null)
	                {
	                    navState.setState(portletWindow, new WindowState(requestedState));
	                }
	                if (requestedMode != null)
	                {
	                    navState.setMode(portletWindow, new PortletMode(requestedMode));
	                }
	                navState.sync(requestContext);                                
	            }
	
	            if (checkAccess(requestContext, JetspeedActions.EDIT))
	            {
	                fragment.updateStateMode(requestedState, requestedMode);
	            }
	            
	            //requestContext.getPortalURL().getNavigationalState().
	            resultMap.put(STATUS, status);
	            
	            if (requestedState != null)
	            {
	                resultMap.put(OLD_WINDOW_STATE, oldState);
	                resultMap.put(WINDOW_STATE, requestedState);
	            }
	
	            if (requestedMode != null)
	            {
	                resultMap.put(OLD_PORTLET_MODE, oldMode);
	                resultMap.put(PORTLET_MODE, requestedMode);
	            }
            }
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while changing portlet/page action", e);
            resultMap.put(REASON, e.toString());
            // Return a failure indicator
            success = false;
        }

        return success;
    }
    
    // TODO: The validWindowStates and validPortletModes maps only contain 
    //       internal (portal level) valid modes and states.
    //       *if* a pa defines a custom mode/state with a different name but
    //       mapped onto a internal (portal) mode/state 
    //       *then* first the real internal mode/state needs to be retrieved from the 
    //       targeted portlet its application:
    //       o.a.j.om.common.portlet.PortletApplication.getMappedMode(customMode) and
    //       o.a.j.om.common.portlet.PortletApplication.getMappedState(customState)        
    
    protected boolean isValidWindowState(String windowState)
    {
        return this.validWindowStates.containsKey(windowState);
    }
    protected boolean isValidPortletMode(String portletMode)
    {
        return this.validPortletModes.containsKey(portletMode);
    }
    
}
