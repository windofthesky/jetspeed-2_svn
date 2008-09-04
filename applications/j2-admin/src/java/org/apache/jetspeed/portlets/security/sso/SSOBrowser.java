/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.CommonPortletServices;
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
 * SSOBrowser
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: SSOBrowser.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class SSOBrowser extends BrowserPortlet
{
    private SSOProvider sso;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        sso = (SSOProvider)getPortletContext().getAttribute(CommonPortletServices.CPS_SSO_COMPONENT);
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
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add(0, "Url");
            resultSetTitleList.add(1, "Site");

            //subPopulate(rundata, qResult, repo, folder, null);

            List list = new ArrayList();
            while (sites.hasNext())
            {
                List row = new ArrayList(2);
                SSOSite site = (SSOSite)sites.next();
                row.add(0, site.getSiteURL());                     
                row.add(1, site.getName());
                list.add(row);
            }            
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("Site");
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
        String selectedSite = (String)PortletMessaging.receive(request, "site", "selectedUrl");
        if (selectedSite != null)
        {        
            Context context = this.getContext(request);
            context.put("currentUrl", selectedSite);
            String selectedName = (String)PortletMessaging.receive(request, "site", "selectedName");
            context.put("currentName", selectedName);  
            
            String realm = (String)PortletMessaging.receive(request, "site", "realm");
            context.put("currentRealm", realm);  
            String userField = (String)PortletMessaging.receive(request, "site", "idField");
            context.put("currentFFID", userField);  
            String pwdFiled = (String)PortletMessaging.receive(request, "site", "pwdField");
            context.put("currentFFPWD", pwdFiled);

            
            
            
        }
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, "SSOBrowser", "status");
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
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
                    PortletMessaging.publish(request, "site", "selectedUrl", selectedSite);
                    PortletMessaging.publish(request, "site", "selectedName", site.getName());
                    PortletMessaging.publish(request, "site", "change", selectedSite);
                    PortletMessaging.publish(request, "site", "realm", site.getRealm());
                    PortletMessaging.publish(request, "site", "idField", site.getFormUserField());
                    PortletMessaging.publish(request, "site", "pwdField", site.getFormPwdField());
                }
            }
            String refresh = request.getParameter("sso.refresh");
            String save = request.getParameter("sso.save");
            String neue = request.getParameter("sso.new");
            String delete = request.getParameter("ssoDelete");
            
            if (refresh != null)
            {
                this.clearBrowserIterator(request);
            }
            else if (neue != null)
            {
                PortletMessaging.cancel(request, "site", "selected");
                PortletMessaging.cancel(request, "site", "selectedUrl");      
                PortletMessaging.cancel(request, "site", "realm");
                PortletMessaging.cancel(request, "site", "idField");
                PortletMessaging.cancel(request, "site", "pwdField");
            }
            else if (delete != null && (!(isEmpty(delete))))
            {
                try
                {
                    SSOSite site = null;
                    site = sso.getSite(delete);
                    if (site != null)
                    {
                        sso.removeSite(site);
                        this.clearBrowserIterator(request);
                        PortletMessaging.cancel(request, "site", "selected");
                        PortletMessaging.cancel(request, "site", "selectedUrl");   
                        PortletMessaging.cancel(request, "site", "realm");
                        PortletMessaging.cancel(request, "site", "idField");
                        PortletMessaging.cancel(request, "site", "pwdField");
                    }
                }
                catch (SSOException e)
                {
                    publishStatusMessage(request, "SSOBrowser", "status", e, "Could not remove site");
                }
            }
            else if (save != null)
            {
                String siteName = request.getParameter("site.name");                
                String siteUrl = request.getParameter("site.url");
                
                String siteRealm = request.getParameter("site.realm");                
                String siteFormID = request.getParameter("site.form_field_ID");
                String siteFormPWD = request.getParameter("site.form_field_PWD");
                 
                if (!(isEmpty(siteName) || isEmpty(siteUrl)))
                {
                    try
                    {
                        SSOSite site = null;
                        String old = (String)PortletMessaging.receive(request, "site", "selectedUrl");
                        if (old != null)
                        {
                            site = sso.getSite(old);
                        }
                        else
                        {
                            site = sso.getSite(siteUrl);
                        }                        
                        if (site != null)
                        {
                            site.setName(siteName);
                            site.setSiteURL(siteUrl);
                            site.setRealm(siteRealm);
                            if (siteFormID != null && siteFormID.length() > 0
                            	&& siteFormPWD != null && siteFormPWD.length() > 0	)
                            {
                            	// Form authentication
                            	site.setFormAuthentication(true);
                            	site.setFormUserField(siteFormID);
                            	site.setFormPwdField(siteFormPWD);
                            }
                            else
                            {
                            	//Challenge response authentication
                            	site.setChallengeResponseAuthentication(true);
                            }
                            
                            sso.updateSite(site);
                            this.clearBrowserIterator(request);
                            PortletMessaging.publish(request, "site", "selectedName", siteName);
                            PortletMessaging.publish(request, "site", "selectedUrl", siteUrl);    
                            PortletMessaging.publish(request, "site", "realm", siteRealm);
                            PortletMessaging.publish(request, "site", "idField",siteFormID);
                            PortletMessaging.publish(request, "site", "pwdField", siteFormPWD);

                        }
                        else
                        {
                        	if (siteFormID != null && siteFormID.length() > 0
                                	&& siteFormPWD != null && siteFormPWD.length() > 0	)
                            {
                    			sso.addSiteFormAuthenticated(siteName, siteUrl, siteRealm, siteFormID,siteFormPWD);
                    		
                            }
                        	else
                        	{
                        		sso.addSiteChallengeResponse(siteName, siteUrl, siteRealm);
                        	}
                            this.clearBrowserIterator(request);
                        }
                    }
                    catch (SSOException e)
                    {
                        publishStatusMessage(request, "SSOBrowser", "status", e, "Could not store site");
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