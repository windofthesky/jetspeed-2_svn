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

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Supports;

/**
 * PortletDefinitionBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="definition")
public class PortletDefinitionBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String applicationName;
    private String applicationContextPath;
    private String portletName;
    private String uniqueName;
    private String portletIcon;
    private String portletIconHolder;
    private String portletIconBasePath;
    private PortletInfoBean portletInfoBean;
    private Collection<DisplayNameBean> displayNameBeans;
    private Collection<DescriptionBean> descriptionBeans;
    private Collection<SupportsBean> supportsBeans;
    private Collection<LanguageBean> languageBeans;
    private GenericMetadataBean metadataBean;
    private Collection<ContainerRuntimeOptionBean> containerRuntimeOptionBeans;
    private Collection<InitParamBean> initParamBeans;
    
    public PortletDefinitionBean()
    {
        
    }
    
    public PortletDefinitionBean(final PortletDefinition portletDefinition)
    {
        PortletApplication portletApplication = portletDefinition.getApplication();
        applicationName = portletApplication.getName();
        applicationContextPath = portletApplication.getContextPath();
        portletName = portletDefinition.getPortletName();
        uniqueName = portletDefinition.getUniqueName();
        portletInfoBean = new PortletInfoBean(portletDefinition.getPortletInfo());
        
        ArrayList<DisplayNameBean> displayNameBeanList = new ArrayList<DisplayNameBean>();
        for (DisplayName displayName : portletDefinition.getDisplayNames())
        {
            displayNameBeanList.add(new DisplayNameBean(displayName));
        }
        displayNameBeans = displayNameBeanList;
        
        ArrayList<DescriptionBean> descriptionBeanList = new ArrayList<DescriptionBean>();
        for (Description description : portletDefinition.getDescriptions())
        {
            descriptionBeanList.add(new DescriptionBean(description));
        }
        descriptionBeans = descriptionBeanList;
        
        ArrayList<SupportsBean> supportsBeanList = new ArrayList<SupportsBean>();
        for (Supports supports : portletDefinition.getSupports())
        {
            supportsBeanList.add(new SupportsBean(supports));
        }
        supportsBeans = supportsBeanList;
        
        ArrayList<LanguageBean> languageBeanList = new ArrayList<LanguageBean>();
        for (Language language : portletDefinition.getLanguages())
        {
            languageBeanList.add(new LanguageBean(language));
        }
        languageBeans = languageBeanList;
        
        metadataBean = new GenericMetadataBean(portletDefinition.getMetadata());
        
        ArrayList<ContainerRuntimeOptionBean> containerRuntimeOptionBeanList = new ArrayList<ContainerRuntimeOptionBean>();
        for (ContainerRuntimeOption containerRuntimeOption : portletDefinition.getContainerRuntimeOptions())
        {
            containerRuntimeOptionBeanList.add(new ContainerRuntimeOptionBean(containerRuntimeOption));
        }
        containerRuntimeOptionBeans = containerRuntimeOptionBeanList;
        
        ArrayList<InitParamBean> initParamBeanList = new ArrayList<InitParamBean>();
        for (InitParam initParam : portletDefinition.getInitParams())
        {
            initParamBeanList.add(new InitParamBean(initParam));
            
            if ("portlet-icon".equals(initParam.getParamName()))
            {
                portletIcon = initParam.getParamValue();
            }
        }
        
        if (portletIcon != null)
        {
            GenericMetadata appMetadata = portletApplication.getMetadata();
            
            Collection<LocalizedField> fields = appMetadata.getFields(PortalReservedParameters.PORTLET_ICON_HOLDER);
            
            if (fields != null && !fields.isEmpty())
            {
                portletIconHolder = fields.iterator().next().getValue();
            }
            
            fields = appMetadata.getFields(PortalReservedParameters.PORTLET_ICON_BASE_PATH);
            
            if (fields != null && !fields.isEmpty())
            {
                portletIconBasePath = fields.iterator().next().getValue();
            }
        }
        
        initParamBeans = initParamBeanList;
    }

    public String getApplicationName()
    {
        return applicationName;
    }

    public void setApplicationName(String applicationName)
    {
        this.applicationName = applicationName;
    }

    public String getApplicationContextPath()
    {
        return applicationContextPath;
    }

    public void setApplicationContextPath(String applicationContextPath)
    {
        this.applicationContextPath = applicationContextPath;
    }

    public String getPortletName()
    {
        return portletName;
    }

    public void setPortletName(String portletName)
    {
        this.portletName = portletName;
    }

    public String getPortletIcon()
    {
        return portletIcon;
    }

    public void setPortletIcon(String portletIcon)
    {
        this.portletIcon = portletIcon;
    }

    public String getPortletIconHolder()
    {
        return portletIconHolder;
    }

    public void setPortletIconHolder(String portletIconHolder)
    {
        this.portletIconHolder = portletIconHolder;
    }

    public String getPortletIconBasePath()
    {
        return portletIconBasePath;
    }

    public void setPortletIconBasePath(String portletIconBasePath)
    {
        this.portletIconBasePath = portletIconBasePath;
    }

    public String getUniqueName()
    {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName)
    {
        this.uniqueName = uniqueName;
    }
    
    @XmlElement(name="portletInfo")
    public PortletInfoBean getPortletInfoBean()
    {
        return portletInfoBean;
    }

    public void setPortletInfoBean(PortletInfoBean portletInfoBean)
    {
        this.portletInfoBean = portletInfoBean;
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

    @XmlElementWrapper(name="supports")
    @XmlElements(@XmlElement(name="support"))
    public Collection<SupportsBean> getSupportsBeans()
    {
        return supportsBeans;
    }

    public void setSupportsBeans(Collection<SupportsBean> supportsBeans)
    {
        this.supportsBeans = supportsBeans;
    }

    @XmlElementWrapper(name="languages")
    @XmlElements(@XmlElement(name="language"))
    public Collection<LanguageBean> getLanguageBeans()
    {
        return languageBeans;
    }

    public void setLanguageBeans(Collection<LanguageBean> languageBeans)
    {
        this.languageBeans = languageBeans;
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
    
    @XmlElementWrapper(name="initParams")
    @XmlElements(@XmlElement(name="initParam"))
    public Collection<InitParamBean> getInitParamBeans()
    {
        return initParamBeans;
    }

    public void setInitParamBeans(Collection<InitParamBean> initParamBeans)
    {
        this.initParamBeans = initParamBeans;
    }
    
}
