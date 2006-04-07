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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.services.information.PortalContextProvider;

/**
 * Move Portlet portlet placement action
 *
 * AJAX Parameters: 
 *    id = the fragment id of the portlet to move
 *    page = (implied in the URL)
 * Additional Absolute Parameters:  
 *    row = the new row to move to
 *    col = the new column to move to
 * Additional Relative Parameters: (move left, right, up, down)
 *    none
 *    
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class ChangePortletAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(ChangePortletAction.class);
    protected String action;
    protected PortalContextProvider portalContext;
    protected Map validWindowStates = new HashMap();
    protected Map validPortletModes = new HashMap();
    
    public ChangePortletAction(String template, 
            String errorTemplate, 
            String action)
    throws AJAXException    
    {
        this(template, errorTemplate, action, null, null, null);
    }
    
    public ChangePortletAction(String template, 
                             String errorTemplate, 
                             String action,
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior,
                             PortalContextProvider portalContext)
    throws AJAXException
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.action = action;
        this.portalContext = portalContext;
        Iterator modes = this.portalContext.getSupportedPortletModes().iterator();        
        while (modes.hasNext())
        {
            String mode = modes.next().toString();
            this.validPortletModes.put(mode, mode);
        }
        Iterator states = this.portalContext.getSupportedWindowStates().iterator();        
        while (states.hasNext())
        {
            String state = states.next().toString();
            this.validWindowStates.put(state, state);
        }        
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, action);
            // Get the necessary parameters off of the request
            String portletId = requestContext.getRequestParameter(PORTLETID);
            if (portletId == null) 
            { 
                throw new Exception("portlet id not provided"); 
            }            
            resultMap.put(PORTLETID, portletId);
            
            String windowState = requestContext.getRequestParameter(WINDOW_STATE);
            String portletMode = requestContext.getRequestParameter(PORTLET_MODE);
            if (windowState == null && portletMode == null) 
            { 
                throw new Exception("portlet window state or mode not provided"); 
            }           
            if (windowState != null && !isValidWindowState(windowState))
            {
                throw new Exception("portlet window state " + windowState + " is not supported");
            }
            if (portletMode != null && !isValidPortletMode(portletMode))
            {
                throw new Exception("portlet mode " + portletMode + " is not supported");
            }
                        
            if (false == checkAccess(requestContext, JetspeedActions.EDIT))
            {
                Page page = requestContext.getPage();
                Fragment fragment = page.getFragmentById(portletId);
                if (fragment == null)
                {
                    success = false;
                    resultMap.put(REASON, "Fragment not found");
                    return success;                    
                }
                int column = fragment.getLayoutColumn();
                int row = fragment.getLayoutRow();                
                if (!createNewPageOnEdit(requestContext))
                {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to edit page");
                    return success;
                }
                status = "refresh";
                // translate old portlet id to new portlet id
                Fragment newFragment = getFragmentIdFromLocation(row, column, requestContext.getPage());
                if (newFragment == null)
                {
                    success = false;
                    resultMap.put(REASON, "Failed to find new fragment");
                    return success;                    
                }                
                portletId = newFragment.getId();
            }            
            Page page = requestContext.getPage();            
            Fragment fragment = page.getFragmentById(portletId);
            String oldState = fragment.getState();
            String oldMode = fragment.getMode();
            if (windowState != null)
                fragment.setState(windowState);
            if (portletMode != null)
                fragment.setMode(portletMode);
            
            if (pageManager != null)
                pageManager.updatePage(page);
            
            resultMap.put(STATUS, status);
            
            if (windowState != null)
            {
                resultMap.put(OLD_WINDOW_STATE, oldState);
                resultMap.put(WINDOW_STATE, windowState);
            }

            if (portletMode != null)
            {
                resultMap.put(OLD_PORTLET_MODE, oldMode);
                resultMap.put(PORTLET_MODE, portletMode);
            }
            
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while moving a portlet", e);
            resultMap.put(REASON, e.toString());
            // Return a failure indicator
            success = false;
        }

        return success;
    }
    
    protected boolean isValidWindowState(String windowState)
    {
        return this.validWindowStates.containsKey(windowState);
    }
    protected boolean isValidPortletMode(String portletMode)
    {
        return this.validPortletModes.containsKey(portletMode);
    }
    
}
