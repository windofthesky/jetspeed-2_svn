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
package org.apache.jetspeed.container.services;

import java.util.List;

import javax.portlet.PortletException;
import javax.portlet.PortletURLGenerationListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.pluto.container.PortletURLListenerService;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;

public class JetspeedPortletURLListenerService implements PortletURLListenerService
{
    private static final Logger log = LoggerFactory.getLogger(JetspeedPortletURLListenerService.class);
    
    protected PortletFactory portletFactory;
    
    public JetspeedPortletURLListenerService(PortletFactory portletFactory)
    {
        this.portletFactory = portletFactory;
    }

    public List<PortletURLGenerationListener> getPortletURLGenerationListeners(PortletApplicationDefinition app)
    {
        List<PortletURLGenerationListener> listeners = null;
        
        try
        {
            listeners = this.portletFactory.getPortletApplicationListeners((PortletApplication) app);
        }
        catch (PortletException e)
        {
            log.error("Failed to retrieve portlet application listeners: " + e.getMessage(), e);
        }
        
        return listeners;
    }
    
}
