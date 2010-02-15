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
package org.apache.jetspeed.factory;

import javax.portlet.Portlet;

import org.apache.jetspeed.portlet.PortletObjectProxy;

/**
 * JetspeedPortletProxyInstance
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: JetspeedPortletProxyInstance.java 516448 2007-03-09 16:25:47Z ate $
 *
 */
public class JetspeedPortletProxyInstance extends JetspeedPortletInstance
{
    
    private Portlet proxiedPortlet;
    
    public JetspeedPortletProxyInstance(String portletName, Portlet portlet, 
                                        boolean autoSwitchEditDefaultsModeToEditMode, 
                                        boolean autoSwitchConfigMode, String customConfigModePortletUniqueName)
    {
        super(portletName, 
              (Portlet) PortletObjectProxy.createProxy(portlet, autoSwitchEditDefaultsModeToEditMode, 
                                                       autoSwitchConfigMode, customConfigModePortletUniqueName));
        this.proxiedPortlet = portlet;
    }
    
    public JetspeedPortletProxyInstance(String portletName, Portlet portlet, 
                                        boolean autoSwitchEditDefaultsModeToEditMode, 
                                        boolean autoSwitchConfigMode, String customConfigModePortletUniqueName,
                                        boolean autoSwitchPreviewMode, String customPreviewModePortletUniqueName)
    {
        super(portletName, 
              (Portlet) PortletObjectProxy.createProxy(portlet, autoSwitchEditDefaultsModeToEditMode, 
                                                       autoSwitchConfigMode, customConfigModePortletUniqueName,
                                                       autoSwitchPreviewMode, customPreviewModePortletUniqueName));
        this.proxiedPortlet = portlet;
    }
    
    @Override
    public boolean isProxyInstance()
    {
        return true;
    }
    
    @Override
    protected Portlet getNonProxyPortletObject()
    {
        return proxiedPortlet;
    }
    
}
