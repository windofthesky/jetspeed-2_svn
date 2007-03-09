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
package org.apache.jetspeed.aggregator.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.aggregator.PortletTrackingManager;
import org.apache.jetspeed.aggregator.RenderTrackable;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Tracks out of service status for portlets
 *  
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletTrackingManagerImpl implements PortletTrackingManager
{
    protected Map outOfService = Collections.synchronizedMap(new HashMap());

    /**
     * when rendering a portlet, the default timeout period in milliseconds
     * setting to zero will disable (no timeout) the timeout
     *  
     */
    protected long defaultPortletTimeout; 
    
    /**
     * Out of service limit, if a portlet entity times out past its limit (or default limit) n consecutive times, 
     * it is taken out of service
     */
    protected int outOfServiceLimit;
    
    protected PortletWindowAccessor windowAccessor;
    
    public PortletTrackingManagerImpl(PortletWindowAccessor windowAccessor, long defaultPortletTimeout, int outOfServiceLimit)
    {
        this.windowAccessor = windowAccessor;
        this.defaultPortletTimeout = defaultPortletTimeout;
        this.outOfServiceLimit = outOfServiceLimit;
    }
    
    public long getDefaultPortletTimeout()
    {
        return this.defaultPortletTimeout;
    }

    public boolean exceededTimeout(long renderTime, PortletWindow window)
    {
        RenderTrackable trackInfo = (RenderTrackable)window.getPortletEntity();
        long defaultTimeout = this.getDefaultPortletTimeout();
        if (trackInfo.getExpiration() > 0)
        {
            return (renderTime > trackInfo.getExpiration());
        }
        else if (defaultTimeout > 0)
        {
            return (renderTime > defaultTimeout);
        }
        return false;
    }
    
    public boolean isOutOfService(PortletWindow window)
    {
        RenderTrackable trackable = (RenderTrackable)window.getPortletEntity();
        if (trackable.getRenderTimeoutCount() > this.outOfServiceLimit)
        {
            return true;
        }
        return false;
    }
    
    public int getOutOfServiceLimit()
    {
        return this.outOfServiceLimit;
    }
    
    public void incrementRenderTimeoutCount(PortletWindow window)
    {
        RenderTrackable trackable = (RenderTrackable)window.getPortletEntity();
        trackable.incrementRenderTimeoutCount();       
    }
   
    public void success(PortletWindow window)
    {
        RenderTrackable trackable = (RenderTrackable)window.getPortletEntity();
        trackable.success();
    }
    
    public void setExpiration(PortletWindow window, long expiration)
    {
        RenderTrackable trackable = (RenderTrackable)window.getPortletEntity();
        trackable.setExpiration(expiration); // * 1000);                
    }
        
    public void takeOutOfService(PortletWindow window)
    {
        RenderTrackable trackable = (RenderTrackable)window.getPortletEntity();
        trackable.setRenderTimeoutCount((int)this.defaultPortletTimeout + 1);
    }
    
    public void putIntoService(PortletWindow window)
    {
        RenderTrackable trackable = (RenderTrackable)window.getPortletEntity();
        trackable.setRenderTimeoutCount(0);        
    }
    
    public void putIntoService(List fullPortletNames)
    {
        Iterator windows = this.windowAccessor.getPortletWindows().iterator();
        while (windows.hasNext())
        {
            Map.Entry entry = (Map.Entry)windows.next();
            PortletWindow window = (PortletWindow)entry.getValue();
            PortletDefinitionComposite pd = (PortletDefinitionComposite)window.getPortletEntity().getPortletDefinition();          
            for (int ix = 0; ix < fullPortletNames.size(); ix++)
            {
                if (pd.getUniqueName().equals(fullPortletNames.get(ix)))
                {
                    putIntoService(window);
                }
            }
        }        
    }
    
    public List getOutOfServiceList(String fullPortletName)
    {
        List outs = new ArrayList();
        Iterator windows = this.windowAccessor.getPortletWindows().iterator();
        while (windows.hasNext())
        {
            Map.Entry entry = (Map.Entry)windows.next();
            PortletWindow window = (PortletWindow)entry.getValue();
            PortletDefinitionComposite pd = (PortletDefinitionComposite)window.getPortletEntity().getPortletDefinition();
            if (pd.getUniqueName().equals(fullPortletName) && isOutOfService(window))
            {
                outs.add(window);
            }
        }
        return outs;
    }
    
    public List getOutOfServiceList()
    {
        List outs = new ArrayList();
        Iterator windows = this.windowAccessor.getPortletWindows().iterator();
        while (windows.hasNext())
        {
            Map.Entry entry = (Map.Entry)windows.next();
            PortletWindow window = (PortletWindow)entry.getValue();
            if (isOutOfService(window))
            {
                outs.add(window);                
            }
        }
        return outs;
    }
}