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
package org.apache.jetspeed.container;

import javax.servlet.ServletContext;
import javax.portlet.PortletContext;
import javax.portlet.PortletConfig;

import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * Portal Accessor - bridge used by container to communicate with Portal
 * insulating the communications protocol between container and portlet
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class PortalAccessor
{

    public static PortletConfig createPortletConfig(PortletContext portletContext,
                                                 PortletDefinition portletDefinition)
    {
        return PortletConfigFactory.createPortletConfig(portletContext, portletDefinition);         
    }

    public static PortletContext createPortletContext(ServletContext servletContext,
                                                   PortletApplicationDefinition application)
    {
        return PortletContextFactory.createPortletContext(servletContext, application); 
    }


}