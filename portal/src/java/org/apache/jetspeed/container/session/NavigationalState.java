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
package org.apache.jetspeed.container.session;

import java.util.Iterator;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.om.window.PortletWindow;

/**
 * NavigationalState contains the state of the Portal URL and all navigational state context
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface NavigationalState 
{
    WindowState getState(PortletWindow window);
    
    void setState(PortletWindow window, WindowState state);
    
    PortletMode getMode(PortletWindow window);
    
    void setMode(PortletWindow window, PortletMode mode);
    
    PortletMode getPreviousMode(PortletWindow window);
    
    WindowState getPreviousState(PortletWindow window);
    
    ///////////////////////////////////////////////
    
    boolean isNavigationalParameter(String token);
    
    Iterator getRenderParamNames(PortletWindow window);
    
    String[] getRenderParamValues(PortletWindow window, String paramName);

    PortletWindow getPortletWindowOfAction();
    
    void clearRenderParameters(PortletWindow portletWindow);
    
    void setAction(PortletWindow window);
    
    void setRequestParam(String name, String[] values);
    
    void setRenderParam(PortletWindow window, String name, String[] values);
    
    String toString();
    
    String toString(boolean secure);
    
    String getBaseURL();
    
}
