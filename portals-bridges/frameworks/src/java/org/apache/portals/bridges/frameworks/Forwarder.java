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
package org.apache.portals.bridges.frameworks;

import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.apache.portals.bridges.frameworks.model.PortletApplicationModel;


/**
 * Forwarder
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class Forwarder
{
    PortletApplicationModel model;
    RenderRequest  request;
    RenderResponse response;    
    
    public Forwarder(PortletApplicationModel model,
                     RenderRequest  request,
                     RenderResponse response)
    {
        this.model = model;
        this.request = request;        
        this.response = response;
    }
    
    private Forwarder()
    {        
    }
    
    /**
     * Get a link from a view name plus optional comma separated mode, window state 
     * Supports syntax from forwards
     * Examples of viewName parameter:
     *   "myview" 
     *   "myview"
     *   "myview,state:maximized"
     *   "myview,state:normal"
     *   "myview,mode:view,state:maximized"
     *   "myview,mode:edit,state:normal"
     * 
     * @param actionForward
     * @return
     */    
    public PortletURL getView(String viewName)
    {
        PortletURL url = response.createRenderURL();        
        buildLink(viewName, url);
        return url;
    }
    
    /**
     * Get a link from a action forward logical name
     * in the form of view:action where action can be
     * "success" or "failure"
     *  
     * @param actionForward
     * @return
     */
    public PortletURL getLink(String actionForward)
    {
        String forwardName = model.getForward(actionForward);
        PortletURL url = response.createRenderURL();
        buildLink(forwardName, url);
        return url;
    }

    /**
     * Get a link from a action forward logical name
     * for the given action
     * 
     * @param actionForward
     * @return
     */    
    public PortletURL getLink(String forward, String action)
    {
        String actionForward = model.getForward(forward, action);
        PortletURL url = response.createRenderURL();
        buildLink(actionForward, url);
        return url;
    }
    
    // TODO: signatures of getLink with 'dynamic' parameters i.e. pass in a map of runtime binding parameters
    
    private void buildLink(String actionForward, PortletURL url)
    {
        if (actionForward == null)
        {
            return; // no parameters
        }
        
        PortletMode mode = null;
        StringTokenizer tokenizer = new StringTokenizer(actionForward, ForwardConstants.DELIMITER);
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (token.startsWith(ForwardConstants.MODE_PREFIX))
            {
                mode = setPortletMode(token.substring(ForwardConstants.MODE_PREFIX.length()), url);
            }
            else if (token.startsWith(ForwardConstants.STATE_PREFIX))
            {
                setWindowState(token.substring(ForwardConstants.STATE_PREFIX.length()), url);                
            }
            else
            {
                if (mode == null)
                {
                    mode = request.getPortletMode();
                }
                if (mode.equals(PortletMode.VIEW))
                {
                    url.setParameter(FrameworkConstants.VIEW_VIEW_MODE, token);
                }
                else if (mode.equals(PortletMode.EDIT))
                {
                    url.setParameter(FrameworkConstants.VIEW_EDIT_MODE, token);                    
                }
                else if (mode.equals(PortletMode.HELP))
                {
                    url.setParameter(FrameworkConstants.VIEW_HELP_MODE, token);                    
                }
            }
        }                                        
    }
    
    private void setWindowState(String forward, PortletURL url)
    {
        try
        {
            if (forward.equals(ForwardConstants.MAXIMIZED))
            {
                url.setWindowState(WindowState.MAXIMIZED);
            }
            else if (forward.equals(ForwardConstants.MINIMIZED))
            {
                url.setWindowState(WindowState.MINIMIZED);
            }
            else if (forward.equals(ForwardConstants.NORMAL))
            {
                url.setWindowState(WindowState.NORMAL);
            }
        }
        catch (WindowStateException e)
        {
        }
    }
    
    private PortletMode setPortletMode(String forward, PortletURL url)
    {
        PortletMode mode = null;
        try
        {
            if (forward.equals(ForwardConstants.VIEW))
            {
                url.setPortletMode(PortletMode.VIEW);
                mode = PortletMode.VIEW;
            }
            else if (forward.equals(ForwardConstants.EDIT))
            {
                url.setPortletMode(PortletMode.EDIT);
                mode = PortletMode.EDIT;                
            }
            else if (forward.equals(ForwardConstants.HELP))
            {
                url.setPortletMode(PortletMode.HELP);
                mode = PortletMode.HELP;                
            }            
        }
        catch (PortletModeException e)
        {
        }
        return mode;
    }
    
}
