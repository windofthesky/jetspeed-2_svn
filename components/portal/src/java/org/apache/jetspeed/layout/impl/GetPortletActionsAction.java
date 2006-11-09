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
import java.util.List;
import java.util.ArrayList;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.container.state.MutableNavigationalState;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.decoration.DecorationValve;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Get Portlet Actions retrieves the current set of valid actions for one or more portlet windows
 *
 * AJAX Parameters: 
 *    id = the fragment id of the portlet for which to retrieve the action list
 *         multiple id parameters are supported
 *    page = (implied in the URL)
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetPortletActionsAction
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(GetPortletActionsAction.class);
    protected String action;
    private DecorationValve decorationValve;
    
    public GetPortletActionsAction(String template,
                                   String errorTemplate,
                                   String action,
                                   DecorationValve decorationValve)            
    throws AJAXException
    {
        this(template, errorTemplate, action, decorationValve, null, null);
    }
    
    public GetPortletActionsAction(String template,
                                   String errorTemplate,
                                   String action,
                                   DecorationValve decorationValve,
                                   PageManager pageManager,
                                   PortletActionSecurityBehavior securityBehavior)
    throws AJAXException
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.action = action;
        this.decorationValve = decorationValve;
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
            String[] portletIds = requestContext.getRequest().getParameterValues( PORTLETID );
            if (portletIds == null) 
            { 
                throw new Exception("no portlet id was provided"); 
            }
            
            ContentPage page = requestContext.getPage();
            
            ArrayList portletFragments = new ArrayList();
            for ( int i = 0 ; i < portletIds.length ; i++ )
            {
                String portletId = portletIds[ i ];
                ContentFragment fragment = (ContentFragment)page.getFragmentById( portletId );
                if ( fragment == null )
                {
                    throw new Exception("fragment not found for specified portlet id: " + portletId); 
                }
                portletFragments.add( fragment );
            }

            // Run the Decoration valve to get actions
            decorationValve.initFragments( requestContext, true, portletFragments );
            
            resultMap.put(FRAGMENTS, portletFragments);
            
            resultMap.put(STATUS, status);
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
}
