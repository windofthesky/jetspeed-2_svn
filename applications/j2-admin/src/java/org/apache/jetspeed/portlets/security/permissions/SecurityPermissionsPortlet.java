/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.security.permissions;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.portals.gems.dojo.AbstractDojoVelocityPortlet;
import org.apache.velocity.context.Context;

/**
 * Security Permissions Portlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class SecurityPermissionsPortlet extends AbstractDojoVelocityPortlet
{
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected PermissionManager pm = null;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        PortletContext context = getPortletContext();
        pm = (PermissionManager) context
                .getAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (pm == null)
                throw new PortletException(
                        "Could not get instance of portal permission manager component");
    }

    protected void includeDojoRequires(StringBuffer headerInfoText)
    {
        appendHeaderText(headerInfoText, "dojo.lang.*");
        appendHeaderText(headerInfoText, "dojo.event.*");
        appendHeaderText(headerInfoText, "dojo.io");
        appendHeaderText(headerInfoText, "dojo.widget.LayoutContainer");
        appendHeaderText(headerInfoText, "dojo.widget.ContentPane");
        appendHeaderText(headerInfoText, "dojo.widget.LinkPane");
        appendHeaderText(headerInfoText, "dojo.widget.SplitContainer");
        appendHeaderText(headerInfoText, "dojo.widget.TabContainer");
        appendHeaderText(headerInfoText, "dojo.widget.Tree");
    }
    
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        Context velocityContext = getContext(request);
        PortletSession session = request.getPortletSession();
        List permissions = new LinkedList();
        permissions.add("one");
        permissions.add("two");
        permissions.add("three");
        permissions.add("four");
        permissions.add("five");
        permissions.add("six");        
        velocityContext.put("permissions", permissions);
        super.doView(request, response);
    }

    public void processAction(ActionRequest request,
            ActionResponse actionResponse) throws PortletException, IOException
    {
        PortletSession session = request.getPortletSession();
        //session.setAttribute(SESSION_RESULTS, stats);
    }

}
