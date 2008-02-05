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
package org.apache.jetspeed.portlets.wicket;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.wicket.util.string.StringList;
import org.apache.wicket.util.string.IStringIterator;
import org.apache.wicket.protocol.http.portlet.WicketPortlet;

/**
 * AdminWicketPortlet which overrides init() and processRequest() to pass cps: components to Wicket pages.
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public class AdminWicketPortlet extends WicketPortlet
{

    public static final String SERVICE_COMPONENT_NAMES = "serviceComponentNames";
    
    protected StringList serviceComponentNameList;
    protected Map serviceComponentsMap = null;

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        
        String serviceComponentNames = config.getInitParameter(SERVICE_COMPONENT_NAMES);
        
        if (serviceComponentNames != null)
        {
            this.serviceComponentNameList = StringList.tokenize(serviceComponentNames);
            this.serviceComponentsMap = new HashMap();
            
            for (int i = serviceComponentNameList.size() - 1; i >= 0; i--)
            {
                String serviceComponentName = serviceComponentNameList.get(i).trim();
                
                if ("".equals(serviceComponentName))
                {
                    this.serviceComponentNameList.remove(i);
                }
                else
                {
                    Object component = getPortletContext().getAttribute(serviceComponentName);
                    this.serviceComponentsMap.put(serviceComponentName, component);
                }
            }
        }
    }
    
	protected void processRequest(PortletRequest request, PortletResponse response, String requestType, String pageType) throws PortletException, IOException
    {
        if (this.serviceComponentNameList != null)
        {
            for (IStringIterator it = this.serviceComponentNameList.iterator(); it.hasNext(); )
            {
                String serviceComponentName = it.next();
                Object component = this.serviceComponentsMap.get(serviceComponentName);
                request.setAttribute(serviceComponentName, component);
            }
        }
        
        super.processRequest(request, response, requestType, pageType);
    }
    
}