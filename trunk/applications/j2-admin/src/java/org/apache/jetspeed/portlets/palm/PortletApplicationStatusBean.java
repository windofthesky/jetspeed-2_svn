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
package org.apache.jetspeed.portlets.palm;

import java.io.Serializable;

import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;

/**
 * PortletApplicationStatusBean
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: PortletApplicationStatusBean.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class PortletApplicationStatusBean implements Serializable
{
    private String name;
    private String path;
    private String version;
    private boolean local;
    private boolean running;
    
    public PortletApplicationStatusBean(MutablePortletApplication pa, boolean running)
    {
        this.name = pa.getName();
        this.version = pa.getVersion();
        this.local = pa.getApplicationType() == MutablePortletApplication.LOCAL;
        if (local)
        {
            this.path = "<local>";
        }
        else    
        {
            this.path = pa.getWebApplicationDefinition().getContextRoot();
        }
        this.running = running;
    }
    public String getPath()
    {
        return path;
    }
    public boolean isLocal()
    {
        return local;
    }
    public String getName()
    {
        return name;
    }
    public boolean isRunning()
    {
        return running;
    }
    public String getVersion()
    {
        return version;
    }
}
