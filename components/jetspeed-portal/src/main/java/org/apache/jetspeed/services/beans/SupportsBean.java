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
package org.apache.jetspeed.services.beans;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.portlet.Supports;

/**
 * SupportsBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="support")
public class SupportsBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String mimeType;
    private Collection<String> portletModes;
    private Collection<String> windowStates;

    public SupportsBean()
    {
        
    }
    
    public SupportsBean(final Supports supports)
    {
        mimeType = supports.getMimeType();
        portletModes = supports.getPortletModes();
        windowStates = supports.getWindowStates();
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    @XmlElementWrapper(name="portletModes")
    @XmlElements(@XmlElement(name="portletMode"))
    public Collection<String> getPortletModes()
    {
        return portletModes;
    }

    public void setPortletModes(Collection<String> portletModes)
    {
        this.portletModes = portletModes;
    }

    @XmlElementWrapper(name="windowStates")
    @XmlElements(@XmlElement(name="windowState"))
    public Collection<String> getWindowStates()
    {
        return windowStates;
    }

    public void setWindowStates(Collection<String> windowStates)
    {
        this.windowStates = windowStates;
    }

}
