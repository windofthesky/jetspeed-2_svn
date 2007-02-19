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
package org.apache.jetspeed.aggregator;

import java.util.List;

import org.apache.pluto.om.window.PortletWindow;


/**
 * <h4>PortletRendererService<br />
 * Jetspeed-2 Rendering service.</h4>
 * <p>This service process all portlet rendering requests and interfaces with the portlet
 * container to generate the resulting markup</p>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface PortletTrackingManager
{
    /**
     *  Get the default timeout for rendering a portlet in milliseconds
     *
     */
    long getDefaultPortletTimeout();

    /**
     * Out of service limit, if a portlet entity times out past its limit (or default limit) n consecutive times,
     *  it is taken out of service
     *  
     * @return
     */
    int getOutOfServiceLimit();
    
    boolean isOutOfService(PortletWindow window);
    
    boolean exceededTimeout(long renderTime, PortletWindow window);
    
    void incrementRenderTimeoutCount(PortletWindow window);

    void setExpiration(PortletWindow window, long expiration);
    
    void success(PortletWindow window);
      
    void takeOutOfService(PortletWindow window);
    
    void putIntoService(PortletWindow window);
    /**
     * 
     * @param fullPortletNames a list of Strings of full portlet names
     */
    void putIntoService(List fullPortletNames);
    
    List getOutOfServiceList(String fullPortletName);
    
    List getOutOfServiceList();
}