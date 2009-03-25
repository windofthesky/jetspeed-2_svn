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
package org.apache.jetspeed.container.state;

import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.PortletWindow;

/**
 * MutableNavigationalState
 *
 * Allows changing the PortletMode and/or WindowState of a PortletWindow state.
 * <br>
 * This interface extends the {@link NavigationState} interface to cleanly define the immutable contract of the latter.
 * <br>
 * Note: this is actually an ugly hack into the Portal as formally (per the portlet specs) the PortletMode and/or
 * WindowState are only to be modified *and* then retained for the *next* subsequent renderRequest.
 * <br>
 * This interface is used for support of the Pluto required PortalActionProvider implementation (which definition
 * is not undisputed, see: [todo: link to pluto-dev "Why PortalActionProvider?" mail discussion]).
 * <br>
 * Furthermore, this interface is also used by the Jetspeed-1 JetspeedFusionPortlet to synchronize the NavigationalState.
 * Under which conditions that is done isn't clear yet (to me) but possibly that can/should be done differently also.
 * <br>
 * Modifying the Navigational State *during* a renderRequest (before the actual) rendering can result in a lost of these new states on a
 * subsequent refresh of the Portlet if that doesn't trigger changing them again, because the state of these changes is
 * only saved in PortletURLs created during the renderRequest, *not* in the session (if SessionNavigationalState is used).
 * The session state has already been synchronized (if done) *before* these methods can be called.
 * <br>
 * Modifying the Navigational State *during* an actionRequest, as done by Pluto through the PortalActionProvider
 * interface just before it sends a redirect, is kinda strange as it can more cleanly be done through the
 * its PortalURLProvider interface (see above link to the mail discussion about this).
 *  
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public interface MutableNavigationalState extends NavigationalState
{
    /**
     * Sets the window state for the given portlet window.
     * 
     * @param window
     * @param windowState
     */
    void setState(PortletWindow window, WindowState windowState);
    
    /**
     * Remove state for the given (possibly invalid) portlet window
     */
    void removeState(PortletWindow window);
    
    /**
     * Sets the portlet mode for the given portlet window.
     * 
     * @param window
     * @param portletMode
     */
    void setMode(PortletWindow window, PortletMode portletMode);
    
    /**
     * Clear the request parameters to emulate an action reset
     * 
     * @param window
     */
    
    void setTargetted(PortletWindow window);
    
    void clearParameters(PortletWindow window);
    
    void setParametersMap(PortletWindow window, Map<String, String[]> parametersMap);

    void setActionScopeId(PortletWindow window, String actionScopeId);
    
    void setActionScopeRendered(PortletWindow window, boolean actionScopeRendered);

    void setCacheLevel(PortletWindow window, String cacheLevel);

    void setResourceId(PortletWindow window, String resourceId);
    
    void setPrivateRenderParametersMap(PortletWindow window, Map<String, String[]> privateRenderParametersMap);

    void setPublicRenderParametersMap(PortletWindow window, Map<String, String[]> publicRenderParametersMap);
}
