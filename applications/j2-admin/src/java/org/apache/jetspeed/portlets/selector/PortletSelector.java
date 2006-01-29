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
package org.apache.jetspeed.portlets.selector;

import java.io.IOException;
import java.security.AccessControlException;
import java.security.AccessController;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.portlets.PortletInfo;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;

/**
 * Selects one or more portlets
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletSelector.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class PortletSelector extends BrowserPortlet
{
    
    protected static final String CHECKEDSET = "checkedSet";
    protected static final String UNCHECKEDSET = "unCheckedSet";
   

    protected PortletRegistry registry;
    protected SearchEngine searchEngine;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        context = getPortletContext();                
        registry = (PortletRegistry)context.getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }        
        searchEngine = (SearchEngine)context.getAttribute(CommonPortletServices.CPS_SEARCH_COMPONENT);
        if (null == searchEngine)
        {
            throw new PortletException("Failed to find the Search Engine on portlet initialization");
        }        
        
    }
          
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
        
        String filtered = (String)PortletMessaging.receive(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }
        
        String searchString = (String)PortletMessaging.receive(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_SEARCHSTRING);
        if (searchString != null)
        {
            this.getContext(request).put(SEARCH_STRING, searchString);            
        }
        
        Set selectedCheckBoxes = (Set) PortletMessaging.receive(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_SELECTED);
        if(selectedCheckBoxes == null) {
            selectedCheckBoxes = new HashSet();
        }
        this.getContext(request).put("selectedPortlets",selectedCheckBoxes);
        String selectedPortletsString = getAsString(selectedCheckBoxes);
        this.getContext(request).put("selectedPortletsString", selectedPortletsString);
        
        
        super.doView(request, response);
    }

    private String getAsString(Set s) 
    {
        StringBuffer sb = new StringBuffer();
        if(s != null) 
        {
            Iterator it = s.iterator();
            while(it.hasNext()) 
            {
                String portletName = (String) it.next();
                if(sb.length() > 0) {
                    sb.append(",");
                }
                sb.append("box_"+portletName);
            }
        }
        return sb.toString();
    }
    
    private Set getCheckBoxSet(ActionRequest request) {
        Set s = new HashSet();
        String checkBoxesString =  request.getParameter(CHECKEDSET);
        StringTokenizer st = new StringTokenizer(checkBoxesString,",");
        while(st.hasMoreTokens() ) 
        {
            String checkBox = st.nextToken();
            if(checkBox.startsWith("box_")) {
                String portletName = checkBox.substring(4);
                s.add(portletName);
            } else {
                s.add(checkBox);
            }
        }
        // wait we need to subtracted the unchecked stuff
        String unCheckBoxesString =  request.getParameter(UNCHECKEDSET);
        StringTokenizer st2 = new StringTokenizer(unCheckBoxesString,",");
        Set uns = new HashSet();
        while(st2.hasMoreTokens() ) 
        {
            String checkBox = st2.nextToken();
            if(checkBox.startsWith("box_")) {
                String portletName = checkBox.substring(4);
                uns.add(portletName);
            } else {
                uns.add(checkBox);
            }
        }
        // here's the actual removal
        s.removeAll(uns);
        
        return s;
    }
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        String filtered = (String)request.getParameter(FILTERED);
        
        Set checkBoxes = getCheckBoxSet(request);
        PortletMessaging.publish(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_SELECTED, checkBoxes);            
        
        String searchString = (String)request.getParameter(SEARCH_STRING);
        // huh, apparently this messaging API won't basically take a null and assume that's equivalent to a cancel.  In fact the null won't even
        // overwrite the previous value.. it does nothing...
        // So, we'll write some extra code to get around that
        if((searchString != null) && (searchString.trim().length() > 0) )
        {
            PortletMessaging.publish(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_SEARCHSTRING, searchString);
        } else
        {
            PortletMessaging.cancel(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_SEARCHSTRING);
        }
        
        // if the filter parameter is non null AND also has some real chars... then we'll be filtered.
        // otherwise assume no filter
        if ((filtered != null) && (filtered.trim().length() > 0) )
        {
            PortletMessaging.publish(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_FILTERED, "on");            
        }
        else
        {
            PortletMessaging.cancel(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_FILTERED);
        }
        
        super.processAction(request, response);
            
    }
    
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        getRows(request, sql, windowSize, null);
    }

    public void getRows(RenderRequest request, String sql, int windowSize, String filter)
    throws Exception    
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            Iterator portlets = null;
            
            if (filter == null)
                portlets = registry.getAllPortletDefinitions().iterator();
            else
                portlets = searchEngine.search(filter).getResults().iterator();
                                    
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Portlet");
            resultSetTitleList.add("Description");            
            Locale locale = request.getLocale();
            List list = new ArrayList();
            
            while (portlets.hasNext())
            {
                PortletDefinitionComposite portlet = null;
                if (filter == null)
                    portlet = (PortletDefinitionComposite)portlets.next();
                else
                    portlet = this.getPortletFromParsedObject((ParsedObject)portlets.next());
                
                if (portlet == null)
                    continue;
                
                MutablePortletApplication muta = (MutablePortletApplication)portlet.getPortletApplicationDefinition();
                String appName = muta.getName();
                if (appName != null && appName.equals("jetspeed-layouts"))
                    continue;                
                
                // SECURITY filtering
                String uniqueName = appName + "::" + portlet.getName();
                try
                {
                    AccessController.checkPermission(new PortletPermission(portlet.getUniqueName(), JetspeedActions.MASK_VIEW));
                    String name = portlet.getDisplayNameText(locale);
                    if (name == null)
                    {
                        name = portlet.getName();
                    }
                    list.add(new PortletInfo(uniqueName, name, portlet.getDescriptionText(locale)));
                }
                catch (AccessControlException ace)
                {
                    //continue
                }
            }            
            BrowserIterator iterator = new PortletIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("Portlet");
        }
        catch (Exception e)
        {
            //log.error("Exception in CMSBrowserAction.getRows: ", e);
            e.printStackTrace();
            throw e;
        }        
    }
      
    public int find(BrowserIterator iterator, String searchString, String searchColumn)
    {
        int index = 0;
        
        Iterator it = iterator.getResultSet().iterator();
        while (it.hasNext())
        {
            PortletInfo info = (PortletInfo)it.next();
            String name = info.getDisplayName();
            if (name != null && name.startsWith(searchString))
            {
                return index;
            }
            index++;
        }
        
        return -1;
    }
    
    protected PortletDefinitionComposite getPortletFromParsedObject(ParsedObject po)
    {
        boolean found = false;
        String name = "";
        Map fields = po.getFields();
        if(fields != null)
        {
            Object id = fields.get("ID");
    
            if(id != null)
            {
                if(id instanceof Collection)
                {
                    Collection coll = (Collection)id;
                    name = (String) coll.iterator().next();
                }
                else
                {
                    name = (String)id;
                }
            }
            
            if(po.getType().equals("portlet"))
            {
                Object pa = fields.get("portlet_application");
                String paName = "";
                if(pa != null)
                {
                    if(id instanceof Collection)
                    {
                        Collection coll = (Collection)pa;
                        paName = (String) coll.iterator().next();
                    }
                    else
                    {
                        paName = (String)pa;
                    }
                }
                name = paName + "::" + name;
                found = true;
            }
        }
        if (found == false)
            return null;
        
        return registry.getPortletDefinitionByUniqueName(name);
    }
}
