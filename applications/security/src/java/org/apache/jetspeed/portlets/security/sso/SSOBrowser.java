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

import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * SSOBrowser
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SSOBrowser extends BrowserPortlet
{
    private SSOProvider sso;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        sso = (SSOProvider)getPortletContext().getAttribute(SecurityResources.CPS_SSO_COMPONENT);
        if (null == sso)
        {
            throw new PortletException("Failed to find the SSO Provider on portlet initialization");
        }
    }
       
    
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            Iterator sites = sso.getSites("");
            
            // List userObjectList = (List)getParameterFromTemp(portlet, rundata, USER_OBJECTS);

            //
            // Add MetaData headers, types
            //
            
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Site");

            //subPopulate(rundata, qResult, repo, folder, null);

            // TODO: need to try to normalize List/Collection/Iterators
            List list = new ArrayList();
            while (sites.hasNext())
            {
                SSOSite site = (SSOSite)sites.next();
                list.add(site.getName());
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
        String selectedSite = (String)PortletMessaging.receive(request, "site", "selected");
        if (selectedSite != null)
        {        
            Context context = this.getContext(request);
            context.put("currentSite", selectedSite);
            String selectedUrl = (String)PortletMessaging.receive(request, "site", "selectedUrl");
            context.put("currentUrl", selectedUrl);            
        }
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.VIEW)
        {
            String selectedSite = request.getParameter("ssoSite");
            if (selectedSite != null)
            {
                SSOSite site = sso.getSite(selectedSite);
                if (site != null)
                {
                    PortletMessaging.publish(request, "site", "selected", selectedSite);
                    PortletMessaging.publish(request, "site", "selectedUrl", site.getSiteURL());
                    PortletMessaging.publish(request, "site", "change", selectedSite);
                }
            }
            String refresh = request.getParameter("sso.refresh");
            String save = request.getParameter("sso.save");
            String neue = request.getParameter("sso.new");
            if (refresh != null)
            {
                this.clearBrowserIterator(request);
            }
            else if (neue != null)
            {
                PortletMessaging.cancel(request, "site", "selected");
                PortletMessaging.cancel(request, "site", "selectedUrl");                                
            }
            else if (save != null)
            {
                String siteName = request.getParameter("site.name");                
                String siteUrl = request.getParameter("site.url");
                if (!(isEmpty(siteName) || isEmpty(siteUrl)))
                {
                    try
                    {
                        SSOSite site = null;
                        String oldName = (String)PortletMessaging.receive(request, "site", "selected");
                        if (oldName != null)
                        {
                            site = sso.getSite(oldName);
                        }
                        else
                        {
                            site = sso.getSite(siteName);
                        }                        
                        if (site != null)
                        {
                            site.setName(siteName);
                            site.setSiteURL(siteUrl);
                            sso.updateSite(site);
                            this.clearBrowserIterator(request);
                            PortletMessaging.publish(request, "site", "selected", siteName);
                            PortletMessaging.publish(request, "site", "selectedUrl", siteUrl);                            
                        }
                        else
                        {
                            sso.addSite(siteName, siteUrl);
                            this.clearBrowserIterator(request);
                        }
                    }
                    catch (SSOException e)
                    {
                        // TODO: exception handling
                        System.err.println("Exception storing site: " + e);
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
