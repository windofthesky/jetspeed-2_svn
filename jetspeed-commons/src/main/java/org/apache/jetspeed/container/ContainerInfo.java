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

import java.util.ResourceBundle;

/**
 * Container Information
 *
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 */
public final class ContainerInfo implements org.apache.pluto.container.ContainerInfo
{
    public static final ResourceBundle CONTAINER_INFO = ResourceBundle.getBundle("org.apache.jetspeed.container.resources.ContainerInfo");
    
    private static final ContainerInfo instance = new ContainerInfo();
    
    public static ContainerInfo getInfo()
    {
        return instance;
    }
    
    private ContainerInfo()
    {
    }
    
    public int getMajorSpecificationVersion()
    {
        return Integer.parseInt(CONTAINER_INFO.getString("javax.portlet.version.major"));
    }

    public int getMinorSpecificationVersion()
    {
        return Integer.parseInt(CONTAINER_INFO.getString("javax.portlet.version.minor"));
    }

    public String getPortletContainerName()
    {
        return CONTAINER_INFO.getString("jetspeed.container.name");
    }

    public String getPortletContainerMajorVersion()
    {
        return CONTAINER_INFO.getString("jetspeed.container.version.major");
    }

    public String getPortletContainerMinorVersion()
    {
        return CONTAINER_INFO.getString("jetspeed.container.version.minor");
    }

    public String getPortletContainerVersion()
    {
        return getPortletContainerMajorVersion()+"."+getPortletContainerMinorVersion();
    }

    public String getServerInfo()
    {
        return getPortletContainerName()+"/"+getPortletContainerMajorVersion()+"."+getPortletContainerMinorVersion();
    }
}
