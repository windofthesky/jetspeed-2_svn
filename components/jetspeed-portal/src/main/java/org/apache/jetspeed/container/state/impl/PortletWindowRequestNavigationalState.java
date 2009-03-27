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
package org.apache.jetspeed.container.state.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.container.om.portlet.PublicRenderParameter;

/**
 * PortletWindowRequestNavigationalState
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PortletWindowRequestNavigationalState extends PortletWindowExtendedNavigationalState
{
    private static final long serialVersionUID = 3807035638733358425L;

    private String windowId;
    private PortletDefinition pd;
    private String cacheLevel;
    private String resourceId;
    private Map<String, String[]> privateRenderParametersMap;
    private Map<String, String[]> targetPublicRenderParametersMap;
    private Map<String, String[]> publicRenderParametersMap;
    private Map<QName, String> qnameToIdentifierMap;
    private Map<String, QName> identifierToQNameMap;
    private boolean targetted;
    
    /**
     * true if for a targeted PortletWindow using StateFullParameters the saved (in the session) render parameters
     * must be cleared when synchronizing the states.
     * Prevents the default behavior when using StateFullParameters to copy the parameters from the session
     * when no parameters are specified in the PortletURL.
     * Used if for the targeted PortletWindow no render parameters are specified. 
     */
    private boolean clearParameters;

    public PortletWindowRequestNavigationalState(String windowId)
    {
        this.windowId = windowId;
    }

    public String getWindowId()
    {
        return windowId;
    }
    
    public void setPortletDefinition(PortletDefinition pd)
    {
        this.pd = pd;
    }
    
    public void resolveActionScopedRequestAttributes()
    {
        if (pd != null)
        {
            ContainerRuntimeOption actionScopedRequestAttributesOption = 
                pd.getContainerRuntimeOption(ContainerRuntimeOption.ACTION_SCOPED_REQUEST_ATTRIBUTES_OPTION);
            if (actionScopedRequestAttributesOption == null)
            {
                actionScopedRequestAttributesOption = 
                    pd.getApplication().getContainerRuntimeOption(ContainerRuntimeOption.ACTION_SCOPED_REQUEST_ATTRIBUTES_OPTION);
            }
            setActionScopedRequestAttributes((actionScopedRequestAttributesOption != null) &&
                                             (actionScopedRequestAttributesOption.getValues() != null) &&
                                             (actionScopedRequestAttributesOption.getValues().size() > 0) &&
                                             Boolean.parseBoolean(actionScopedRequestAttributesOption.getValues().get(0)));
        }
    }
    
    public void resolvePublicRenderParametersMapping()
    {
        if (pd != null && qnameToIdentifierMap == null)
        {
            qnameToIdentifierMap = new HashMap<QName, String>();
            identifierToQNameMap = new HashMap<String, QName>();
            for (String identifier : pd.getSupportedPublicRenderParameters())
            {
                PublicRenderParameter prp = pd.getApplication().getPublicRenderParameter(identifier);
                if (prp != null)
                {
                    qnameToIdentifierMap.put(prp.getQName(), identifier);
                    identifierToQNameMap.put(identifier, prp.getQName());
                }
            }
        }
    }
    
    public PortletDefinition getPortletDefinition()
    {
        return pd;
    }
    
    public Map<QName, String> getPublicRenderParametersQNameToIdentifierMap()
    {
        resolvePublicRenderParametersMapping();
        return qnameToIdentifierMap;
    }
    
    public QName getPublicRenderParameterQNameByIdentifier(String identifier)
    {
        resolvePublicRenderParametersMapping();
        return identifierToQNameMap.get(identifier);
    }
        
    public String getPublicRenderParameterIdentifierByQName(QName qname)
    {
        resolvePublicRenderParametersMapping();
        return qnameToIdentifierMap.get(qname);
    }
        
    public String getCacheLevel()
    {
        return cacheLevel;
    }

    public void setCacheLevel(String cacheLevel)
    {
        this.cacheLevel = cacheLevel;
    }

    public String getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(String resourceId)
    {
        this.resourceId = resourceId;
    }

    public Map<String, String[]> getPrivateRenderParametersMap()
    {
        if (privateRenderParametersMap == null)
        {
            privateRenderParametersMap = Collections.emptyMap();
        }
        return privateRenderParametersMap;
    }

    public void setPrivateRenderParameters(String name, String[] values)
    {
        if (privateRenderParametersMap == null)
        {
            privateRenderParametersMap = new HashMap<String, String[]>();
        }
        privateRenderParametersMap.put(name, values);
    }    
    
    public void setPrivateRenderParametersMap(Map<String, String[]> privateRenderParametersMap)
    {
        this.privateRenderParametersMap = privateRenderParametersMap;
    }
    
    public Map<String, String[]> getPublicRenderParametersMap()
    {
        // leave handling null return value to caller
        return publicRenderParametersMap;
    }

    public void setPublicRenderParameters(String name, String[] values)
    {
        if (publicRenderParametersMap == null)
        {
            publicRenderParametersMap = new HashMap<String,String[]>();
        }
        publicRenderParametersMap.put(name, values);
    }    
    
    public void setPublicRenderParametersMap(Map<String, String[]> publicRenderParametersMap)
    {
        this.publicRenderParametersMap = publicRenderParametersMap;
    }
    
    public Map<String, String[]> getTargetPublicRenderParametersMap()
    {
        if (targetPublicRenderParametersMap == null)
        {
            targetPublicRenderParametersMap = new HashMap<String, String[]>(); 
        }
        return this.targetPublicRenderParametersMap;
    }
    
    public void setTargetPublicRenderParametersMap(Map<String, String[]> map)
    {
        this.targetPublicRenderParametersMap = map;
    }
    
    public boolean isClearParameters()
    {
        return clearParameters;
    }
    
    public void setClearParameters(boolean ignoreParameters)
    {
        this.clearParameters = ignoreParameters;
    }
    
    public boolean isTargetted()
    {
        return targetted;
    }
    
    public void setTargetted(boolean targetted)
    {
        this.targetted = targetted;
    }
}
