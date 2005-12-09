/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
public final class ContainerInfo
{
    public static final ResourceBundle CONTAINER_INFO;

    static
    {
        CONTAINER_INFO = ResourceBundle.getBundle("org.apache.jetspeed.container.resources.ContainerInfo");
    }

    public static final String getPortletContainerName()
    {
        return CONTAINER_INFO.getString("jetspeed.container.name");
    }

    public static final String getPortletContainerMajorVersion()
    {
        return CONTAINER_INFO.getString("jetspeed.container.version.major");
    }

    public static final String getPortletContainerMinorVersion()
    {
        return CONTAINER_INFO.getString("jetspeed.container.version.minor");
    }

    public static final int getMajorSpecificationVersion()
    {
        return Integer.parseInt(CONTAINER_INFO.getString("javax.portlet.version.major"));
    }

    public static final int getMinorSpecificationVersion()
    {
        return Integer.parseInt(CONTAINER_INFO.getString("javax.portlet.version.minor"));
    }

    public static final String getServerInfo()
    {
        StringBuffer sb = new StringBuffer(getPortletContainerName()).append(
                CONTAINER_INFO.getString("jetspeed.container.separator")).append(getPortletContainerMajorVersion())
                .append(".").append(getPortletContainerMinorVersion());
        return sb.toString();
    }

}
