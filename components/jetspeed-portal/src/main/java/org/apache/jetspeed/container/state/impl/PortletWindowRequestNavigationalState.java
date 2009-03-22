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

import java.util.HashMap;
import java.util.Map;

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
    private String cacheLevel;
    private String resourceId;
    private Map<String, String[]> privateRenderParametersMap;
    private Map<String, String[]> publicRenderParametersMap;
    
    /**
     * true if for a targeted PortletWindow using StateFullParameters the saved (in the session) render parameters
     * must be cleared when synchronizing the states.
     * Prevents the default behavior when using StateFullParameters to copy the parameters from the session
     * when no parameters are specified in the PortletURL.
     * Used if for the targeted PortletWindow no render parameters are specified. 
     */
    private boolean clearParameters;

    public PortletWindowRequestNavigationalState(String windowId, boolean actionScopedRequestAttributes)
    {
        super(actionScopedRequestAttributes);
        this.windowId = windowId;
    }

    public String getWindowId()
    {
        return windowId;
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
        return publicRenderParametersMap;
    }

    public void setPublicRenderParameters(String name, String[] values)
    {
        if ( publicRenderParametersMap == null )
        {
            publicRenderParametersMap = new HashMap<String, String[]>();
        }
        publicRenderParametersMap.put(name, values);
    }    
    
    public void setPublicRenderParametersMap(Map<String, String[]> publicRenderParametersMap)
    {
        this.publicRenderParametersMap = publicRenderParametersMap;
    }
    
    public boolean isClearParameters()
    {
        return clearParameters;
    }
    
    public void setClearParameters(boolean ignoreParameters)
    {
        this.clearParameters = ignoreParameters;
    }
}
