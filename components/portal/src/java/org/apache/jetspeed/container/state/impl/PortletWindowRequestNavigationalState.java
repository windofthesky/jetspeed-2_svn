/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.state.impl;

/**
 * PortletWindowRequestNavigationalState
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: PortletWindowRequestNavigationalState.java 187753 2004-10-15 21:47:25Z ate $
 */
public class PortletWindowRequestNavigationalState extends PortletWindowExtendedNavigationalState
{
    private String  windowId;
    
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
        
    public boolean isClearParameters()
    {
        return clearParameters;
    }
    
    public void setClearParameters(boolean ignoreParameters)
    {
        this.clearParameters = ignoreParameters;
    }
}
