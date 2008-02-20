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
package org.apache.jetspeed.portlets.security.users;

import java.io.Serializable;
import java.io.NotSerializableException;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.prefs.Preferences;
import java.security.Principal;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.PortletRequest;

import org.apache.wicket.RequestContext;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.protocol.http.portlet.PortletRequestContext;

import org.apache.portals.messaging.PortletMessaging;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.SecurityUtil;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.administration.PortalConfiguration;

import org.apache.jetspeed.portlets.wicket.AdminWicketPortlet;
import org.apache.jetspeed.portlets.wicket.component.LinkPropertyColumn;
import org.apache.jetspeed.portlets.wicket.component.PortletOddEvenItem;

/**
 * User Admin Wicket WebPage
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public class WicketUserAdmin extends WebPage
{
    
    public static final String USER_ADMINISTRATION = "J2 User Administration";
    
	public WicketUserAdmin()
	{
	}
    
    protected PortletRequest getPortletRequest()
    {
        return ((PortletRequestContext) RequestContext.get()).getPortletRequest();
    }
    
    protected String getPAIdentifier()
    {
        return (String) getPortletRequest().getAttribute(AdminWicketPortlet.JETSPEED_PA_IDENTIFIER);
    }

    protected UserManager getUserManager()
    {
        return (UserManager) getPortletRequest().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
    }
    
    protected RoleManager getRoleManager()
    {
        return (RoleManager) getPortletRequest().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
    }
    
    protected GroupManager getGroupManager()
    {
        return (GroupManager) getPortletRequest().getAttribute(CommonPortletServices.CPS_GROUP_MANAGER_COMPONENT);
    }
    
    protected Profiler getProfiler()
    {
        return (Profiler) getPortletRequest().getAttribute(CommonPortletServices.CPS_PROFILER_COMPONENT);
    }
    
    protected AuditActivity getAuditActivity()
    {
        return (AuditActivity) getPortletRequest().getAttribute(CommonPortletServices.CPS_AUDIT_ACTIVITY);
    }
    
    protected PageManager getPageManager()
    {
        return (PageManager) getPortletRequest().getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
    }
    
    protected PortletRegistry getPortletRegistry()
    {
        return (PortletRegistry) getPortletRequest().getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
    }

    protected PortalConfiguration getPortalConfiguration()
    {
        return (PortalConfiguration) getPortletRequest().getAttribute(CommonPortletServices.CPS_PORTAL_CONFIGURATION);
    }

    protected PasswordCredential getCredential(User user)
    {
        PasswordCredential credential = null;
        
        Set credentials = user.getSubject().getPrivateCredentials();
        Iterator iter = credentials.iterator();
        
        while (iter.hasNext())
        {
            Object o = iter.next();
            
            if (o instanceof PasswordCredential)
            {
                credential = (PasswordCredential)o;
                break;
            }
        }
        
        return credential;
    }

    protected String getIPAddress()
    {
        org.apache.jetspeed.request.RequestContext context = (org.apache.jetspeed.request.RequestContext) getPortletRequest().getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        
        return (context == null ? "" : context.getRequest().getRemoteAddr());
    }
    
}