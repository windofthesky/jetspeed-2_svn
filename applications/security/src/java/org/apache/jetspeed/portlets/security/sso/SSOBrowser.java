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

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;

import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.AbstractBrowserPortlet;

/**
 * SSOBrowser
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SSOBrowser extends AbstractBrowserPortlet
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
            //
            // get query parameters from registry
            //
//            Iterator it = sqlParameters.iterator();
//            int ix = 0;
//            while (it.hasNext())
//            {
//                ix++;
//                Object object = it.next();
//            }

            //
            // submit the query
            //
            Iterator sites = sso.getSites("");
            
            // List userObjectList = (List)getParameterFromTemp(portlet, rundata, USER_OBJECTS);


            //
            // Add MetaData headers, types
            //
            
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Site");

            //
            // add user objects
            //
            //subPopulate(rundata, qResult, repo, folder, null);

            // TODO: need to try to normalize List/Collection/Iterators
            List list = new ArrayList();
            while (sites.hasNext())
            {
                list.add(sites.next());
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
    
}
