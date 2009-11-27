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
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.PortletApplication;

/**
 * PortletApplicationBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="application")
public class PortletApplicationBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String contextPath;
    private String localContextPath;
    private String defaultNamespace;
    private int applicationType;
    private long checksum;
    private long revision;
    private Collection<DisplayNameBean> displayNameBeans;
    private Collection<DescriptionBean> descriptionBeans;
    private GenericMetadataBean metadataBean;
    private Collection<ContainerRuntimeOptionBean> containerRuntimeOptionBeans;
    
    public PortletApplicationBean()
    {
        
    }
    
    public PortletApplicationBean(final PortletApplication portletApplication)
    {
        name = portletApplication.getName();
        contextPath = portletApplication.getContextPath();
        localContextPath = portletApplication.getLocalContextPath();
        defaultNamespace = portletApplication.getDefaultNamespace();
        applicationType = portletApplication.getApplicationType();
        checksum = portletApplication.getChecksum();
        revision = portletApplication.getRevision();
        
        ArrayList<DisplayNameBean> displayNameBeanList = new ArrayList<DisplayNameBean>();
        for (DisplayName displayName : portletApplication.getDisplayNames())
        {
            displayNameBeanList.add(new DisplayNameBean(displayName));
        }
        displayNameBeans = displayNameBeanList;
        
        ArrayList<DescriptionBean> descriptionBeanList = new ArrayList<DescriptionBean>();
        for (Description description : portletApplication.getDescriptions())
        {
            descriptionBeanList.add(new DescriptionBean(description));
        }
        descriptionBeans = descriptionBeanList;
        
        metadataBean = new GenericMetadataBean(portletApplication.getMetadata());
        
        ArrayList<ContainerRuntimeOptionBean> containerRuntimeOptionBeanList = new ArrayList<ContainerRuntimeOptionBean>();
        for (ContainerRuntimeOption containerRuntimeOption : portletApplication.getContainerRuntimeOptions())
        {
            containerRuntimeOptionBeanList.add(new ContainerRuntimeOptionBean(containerRuntimeOption));
        }
        containerRuntimeOptionBeans = containerRuntimeOptionBeanList;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    public String getLocalContextPath()
    {
        return localContextPath;
    }

    public void setLocalContextPath(String localContextPath)
    {
        this.localContextPath = localContextPath;
    }
    
    public String getDefaultNamespace()
    {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace)
    {
        this.defaultNamespace = defaultNamespace;
    }

    public int getApplicationType()
    {
        return applicationType;
    }

    public void setApplicationType(int applicationType)
    {
        this.applicationType = applicationType;
    }

    public long getChecksum()
    {
        return checksum;
    }

    public void setChecksum(long checksum)
    {
        this.checksum = checksum;
    }

    public long getRevision()
    {
        return revision;
    }

    public void setRevision(long revision)
    {
        this.revision = revision;
    }

    @XmlElementWrapper(name="displayNames")
    @XmlElements(@XmlElement(name="displayName"))
    public Collection<DisplayNameBean> getDisplayNameBeans()
    {
        return displayNameBeans;
    }

    public void setDisplayNameBeans(Collection<DisplayNameBean> displayNameBeans)
    {
        this.displayNameBeans = displayNameBeans;
    }

    @XmlElementWrapper(name="descriptions")
    @XmlElements(@XmlElement(name="description"))
    public Collection<DescriptionBean> getDescriptionBeans()
    {
        return descriptionBeans;
    }

    public void setDescriptionBeans(Collection<DescriptionBean> descriptionBeans)
    {
        this.descriptionBeans = descriptionBeans;
    }
    
    @XmlElement(name="metadata")
    public GenericMetadataBean getMetadataBean()
    {
        return metadataBean;
    }
    
    public void setMetadataBean(GenericMetadataBean metadataBean)
    {
        this.metadataBean = metadataBean;
    }
    
    @XmlElementWrapper(name="containerRuntimeOptions")
    @XmlElements(@XmlElement(name="containerRuntimeOption"))
    public Collection<ContainerRuntimeOptionBean> getContainerRuntimeOptionBeans()
    {
        return containerRuntimeOptionBeans;
    }

    public void setContainerRuntimeOptionBeans(Collection<ContainerRuntimeOptionBean> containerRuntimeOptionBeans)
    {
        this.containerRuntimeOptionBeans = containerRuntimeOptionBeans;
    }
}
