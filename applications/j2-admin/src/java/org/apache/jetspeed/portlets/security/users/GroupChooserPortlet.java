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
package org.apache.jetspeed.portlets.security.users;

import java.security.Principal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;

/**
 * GroupChooserPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: GroupChooserPortlet.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class GroupChooserPortlet extends BrowserPortlet
{
    private GroupManager groupManager;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        groupManager = (GroupManager) 
            getPortletContext().getAttribute(CommonPortletServices.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }
           
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            Iterator groups = groupManager.getGroups("");
                        
            
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Group");

            // TODO: need to try to normalize List/Collection/Iterators
            List list = new ArrayList();
            while (groups.hasNext())
            {
                Group group = (Group)groups.next();
                
                Principal principal = group.getPrincipal();                
                list.add(principal.getName());
            }            
            
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("Group");
        }
        catch (Exception e)
        {
            //log.error("Exception in CMSBrowserAction.getRows: ", e);
            e.printStackTrace();
            throw e;
        }        
    }
       
}
