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
package org.apache.jetspeed.portlets.security.sso;

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

import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * SSODetails
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SSODetails extends BrowserPortlet
{
    private SSOProvider sso;
    private UserManager userManager;
    private PortletEntityAccessComponent entityAccess;
    private PortletRegistry registry;
    private PortletEntity chooserEntity = null;
    
    private static final String USER_CHOOSER_ENTITY_KEY = "_js2-security-714";
    private static final String PORTLET_NAME = "security::UserChooser";
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        sso = (SSOProvider)getPortletContext().getAttribute(SecurityResources.CPS_SSO_COMPONENT);
        if (null == sso)
        {
            throw new PortletException("Failed to find the SSO Provider on portlet initialization");
        }
        userManager = (UserManager) getPortletContext().getAttribute(SecurityResources.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }

        registry = (PortletRegistry) 
            getPortletContext().getAttribute(SecurityResources.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Registry on portlet initialization");
        }      
        entityAccess = (PortletEntityAccessComponent) 
            getPortletContext().getAttribute(SecurityResources.CPS_ENTITY_ACCESS_COMPONENT);
        if (null == entityAccess)
        {
            throw new PortletException("Failed to find the Entity Accessor on portlet initialization");
        }      
        
        PortletDefinition portletDef = registry.getPortletDefinitionByUniqueName(PORTLET_NAME);
        if (null == portletDef)
        {
            throw new PortletException("Could not find portlet definition in registry for " + PORTLET_NAME);
        }
        
        PortletEntity entity = entityAccess.getPortletEntity(USER_CHOOSER_ENTITY_KEY);
        if (entity == null)
        {            
            entity = entityAccess.newPortletEntityInstance(portletDef, USER_CHOOSER_ENTITY_KEY);
            try
            {
                entityAccess.storePortletEntity(entity);
            }
            catch (Exception e)
            {
                throw new PortletException("Could not create entity for " + PORTLET_NAME);
            }
        }
        chooserEntity = entity;
    }
       
    
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            SSOSite site = null;
            Iterator principals = null;
            List list = null;
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Principal");
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Remote");
            
            String selectedSite = (String)PortletMessaging.receive(request, "site", "selected");
            if (selectedSite != null)
            {
                site = sso.getSite(selectedSite);
                list = sso.getPrincipalsForSite(site);
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
        String change = (String)PortletMessaging.consume(request, "site", "change");
        if (change != null)
        { 
            this.clearBrowserIterator(request);
        }
        Context context = this.getContext(request);        
        String selectedSite = (String)PortletMessaging.receive(request, "site", "selected");
        if (selectedSite != null)
        {        
            context.put("currentSite", selectedSite);
        }        
        context.put("chooser", chooserEntity);        
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.VIEW)
        {
            String refresh = request.getParameter("sso.refresh");
            String save = request.getParameter("sso.save");
            String neue = request.getParameter("sso.new");
            if (refresh != null)
            {
                this.clearBrowserIterator(request);
            }
            else if (neue != null)
            {
                //PortletMessaging.cancel(request, "site", "selected");
                //PortletMessaging.cancel(request, "site", "selectedUrl");                                
            }
            else if (save != null)
            {
                String portalPrincipal = request.getParameter("portal.principal");                
                String remotePrincipal = request.getParameter("remote.principal");
                String remoteCredential = request.getParameter("remote.credential");
                if (!(isEmpty(remotePrincipal) || isEmpty(remotePrincipal) || isEmpty(remoteCredential)))
                {
                    try
                    {
                        String siteName = (String)PortletMessaging.receive(request, "site", "selected");                        
                        SSOSite site = sso.getSite(siteName);
                        User user = userManager.getUser(portalPrincipal);                        
                        if (site != null && user != null)
                        {                            
                            Subject subject = user.getSubject(); 
                            sso.addCredentialsForSite(subject, remotePrincipal, site.getSiteURL(), remoteCredential);
                            this.clearBrowserIterator(request);
                        }
                    }
                    catch (SSOException e)
                    {
                        // TODO: exception handling
                        System.err.println("Exception storing site: " + e);
                    }
                    catch (SecurityException se)
                    {
                        // TODO: exception handling
                        System.err.println("Exception storing site: " + se);
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
