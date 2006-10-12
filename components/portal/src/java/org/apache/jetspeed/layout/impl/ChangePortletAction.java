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
    protected Log log = LogFactory.getLog(ChangePortletAction.class);
    protected String action;
    protected Map validWindowStates = new HashMap();
    protected Map validPortletModes = new HashMap();
    
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

    public boolean runBatch(RequestContext requestContext, Map resultMap) throws AJAXException
    {
        return runAction(requestContext, resultMap, true);
    }    
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        return runAction(requestContext, resultMap, false);
    }
    
    public boolean runAction(RequestContext requestContext, Map resultMap, boolean batch)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, action);
            // Get the necessary parameters off of the request
            String portletId = getActionParameter(requestContext, PORTLETID);
            if (portletId == null) 
            { 
                throw new Exception("portlet id not provided"); 
            }            
            resultMap.put(PORTLETID, portletId);
            
            String windowState = getActionParameter(requestContext, WINDOW_STATE);
            String portletMode = getActionParameter(requestContext, PORTLET_MODE);
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
            
            if (pageManager != null && !batch)
            {
                pageManager.updatePage(page);
            }
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
    
    // TODO: The validWindowStates and validPortletModes maps only contain 
    //       internal (portal level) valid modes and states.
    //       *if* a pa defines a custom mode/state with a different name but
    //       mapped onto a interal (portal) mode/state 
    //       *then* first the real internal mode/state needs to be retrieved from the 
    //       targetted portlet its application:
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
