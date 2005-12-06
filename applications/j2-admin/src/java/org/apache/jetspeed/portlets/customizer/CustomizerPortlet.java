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
package org.apache.jetspeed.portlets.customizer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.portlets.PortletInfo;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.frameworks.model.ModelBean;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.gems.util.ValidationHelper;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * Customizer Portlet
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class CustomizerPortlet extends AbstractVelocityMessagingPortlet
{
    protected PortletRegistry registry;
    protected PermissionManager permissionManager;
    protected PageManager pageManager;

    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        PortletContext context = getPortletContext();                
        registry = (PortletRegistry)context.getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }        
        permissionManager = (PermissionManager)context.getAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (null == permissionManager)
        {
            throw new PortletException("Failed to find the Permission Manager on portlet initialization");
        }
        pageManager = (PageManager)context.getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        if (null == pageManager)
        {
            throw new PortletException("Failed to find the Page Manager on portlet initialization");
        }                
        
    }
    
    protected static final String PORTLET_LIST = "portlets";
    protected static final String ROOT_FOLDER = "rootFolder";
              
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
                 
        Context context = this.getContext(request);
        context.put(PORTLET_LIST, this.retrievePortlets(request, response));
        try
        {
            context.put(ROOT_FOLDER, pageManager.getFolder("/"));
        }
        catch (Exception e)
        {
            throw new PortletException("Failed to get root folder");
        }
        super.doView(request, response);
    }
    
    protected List retrievePortlets(RenderRequest request, RenderResponse response)
    {
        List list = (List)this.receiveRenderMessage(request, PORTLET_LIST);
        if (list != null)
            return list;
        
        Iterator portlets = portlets = registry.getAllPortletDefinitions().iterator();
        list = new LinkedList();
        // get subject
        RequestContext requestContext = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        Locale locale = request.getLocale();
        Subject subject = null;
        if (requestContext != null)
            subject = requestContext.getSubject();
        
        while (portlets.hasNext())
        {
            PortletDefinitionComposite portlet = null;
            portlet = (PortletDefinitionComposite)portlets.next();
            
            if (portlet == null)
                continue;
            
            MutablePortletApplication muta = (MutablePortletApplication)portlet.getPortletApplicationDefinition();
            String appName = muta.getName();
            if (appName != null && appName.equals("jetspeed-layouts"))
                continue;                
            
            // SECURITY filtering
            String uniqueName = appName + "::" + portlet.getName();
            if (subject != null)
            {
                if (permissionManager.checkPermission(subject, 
                    new PortletPermission(portlet.getUniqueName(), 
                    SecuredResource.VIEW_ACTION, subject )))
                {
                    list.add(new PortletInfo(uniqueName, portlet.getDisplayNameText(locale), portlet.getDescriptionText(locale)));
                }
            }
        }
        this.publishRenderMessage(request, PORTLET_LIST, list);
        return list;
    }
        
}

