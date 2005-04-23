/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.portlets.security.groups;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * SSODetails
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GroupDetails extends BrowserPortlet
{
    private UserManager userManager;
    private GroupManager groupManager;
        
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager) getPortletContext().getAttribute(SecurityResources.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        groupManager = (GroupManager) getPortletContext().getAttribute(SecurityResources.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
        }        
    }
       
    
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            List list = null;
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("User");
            
            String selectedGroup = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);
            if (selectedGroup != null)
            {
                list = new ArrayList();
            }
            else
            {
                list = new ArrayList();
            }
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
        }
        catch (Exception e)
        {
            //log.error("Exception in CMSBrowserAction.getRows: ", e);
            e.printStackTrace();
            throw e;
        }        
    }
   
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String change = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_CHANGED);
        if (change != null)
        { 
            this.clearBrowserIterator(request);
        }
        Context context = this.getContext(request);        
        String selectedGroup = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);
        if (selectedGroup != null)
        {        
            context.put("currentGroup", selectedGroup);
        }        
        
        // get relative link, TODO: encapsulate Jetspeed links access into component
        String userChooser = getAbsoluteUrl(request, "/Administrative/choosers/users.psml");
        String groupChooser = getAbsoluteUrl(request, "/Administrative/choosers/groups.psml");
        
        context.put("userChooser", userChooser);
        context.put("groupChooser", groupChooser);
        
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, "SSODetails", "status");
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
        
        super.doView(request, response);
    }
    
    public String getAbsoluteUrl(RenderRequest renderRequest, String relativePath)
    {
        RequestContext requestContext = (RequestContext) renderRequest.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        HttpServletRequest request = requestContext.getRequest();
        StringBuffer path = new StringBuffer();
        return path.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(
                request.getServerPort()).append(request.getContextPath()).append(request.getServletPath()).append(
                relativePath).toString();
    }
    
    
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.VIEW)
        {
            String refresh = request.getParameter("sso.refresh");
            String add = request.getParameter("sso.add");
            String delete = request.getParameter("ssoDelete");
           
            if (refresh != null)
            {
                this.clearBrowserIterator(request);
            }
            else if (delete != null && !(isEmpty(delete)))
            {
                try
                {
                    String siteName = (String)PortletMessaging.receive(request, "site", "selectedUrl");                                            
                    SSOSite site = sso.getSite(siteName);
                    User user = null;
                    try
                    {
                        user = userManager.getUser(delete);   
                    }
                    catch(SecurityException se)
                    {
                        // User doesn't exist -- maybe a group
                        user =null;
                    }
                    
                    if ( site != null )
                    {
                        /*
	                     * If the user is null try to remove a group
	                     */
	                    if ( user != null)
	                    {
	                        // Remove USER
	                        Subject subject = user.getSubject(); 
	                        sso.removeCredentialsForSite(subject, site.getSiteURL());
	                        this.clearBrowserIterator(request);
	                    }
	                    else
	                    {
	                        // Try group removal
	                        String fullPath = "/group/" + delete;
	                        sso.removeCredentialsForSite(fullPath, site.getSiteURL());
	                        this.clearBrowserIterator(request);
	                    }  
	                 }
                }
                catch (SSOException e)
                {
                    publishStatusMessage(request, "SSODetails", "status", e, "Could not remove credentials");
                }
            }
            else if (add != null)
            {
                // Roger: here is the principal type
                String principalType = request.getParameter("principal.type");  //group user
                String portalPrincipal = request.getParameter("portal.principal");                
                String remotePrincipal = request.getParameter("remote.principal");
                String remoteCredential = request.getParameter("remote.credential");
                
                // The principal type can benull if the user just typed the name instead of
                // using the choosers.
                
                if (principalType == null || principalType.length() == 0 )
                    principalType = "user";
                
                if (!(isEmpty(remotePrincipal) || isEmpty(remotePrincipal) || isEmpty(remoteCredential)))
                {
                    try
                    {
                        String siteName = (String)PortletMessaging.receive(request, "site", "selectedUrl");                        
                        SSOSite site = sso.getSite(siteName);
                        Subject subject = null;
                        String groupFullPath = null;
                        
                        if (principalType.compareTo("user") == 0)
                        {
                            User user = userManager.getUser(portalPrincipal);    
                            subject = user.getSubject();
                        }
                        else
                        {
                            // Create fullPath
                            groupFullPath = "/group/" + portalPrincipal;
                          }
                        
                        if (site != null && (subject != null || groupFullPath != null) )
                        {
                            if (subject != null )
                                sso.addCredentialsForSite(subject, remotePrincipal, site.getSiteURL(), remoteCredential);
                            else
                                sso.addCredentialsForSite(groupFullPath, remotePrincipal, site.getSiteURL(), remoteCredential);
                            
                            this.clearBrowserIterator(request);
                        }
                    }
                    catch (SSOException e)
                    {
                        publishStatusMessage(request, "SSODetails", "status", e, "Could not add credentials");
                    }
                    catch (SecurityException se)
                    {
                        publishStatusMessage(request, "SSODetails", "status", se, "Could not add credentials");
                    }                    
                }
            }            
        }
        super.processAction(request, response);
            
    }

    private boolean isEmpty(String s)
    {
        if (s == null) return true;
        
        if (s.trim().equals("")) return true;
        
        return false;
    }
    
}
