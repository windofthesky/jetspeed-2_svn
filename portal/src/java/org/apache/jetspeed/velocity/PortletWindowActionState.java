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
package org.apache.jetspeed.velocity;

import java.util.List;
import java.io.Serializable;
import java.util.Vector;

/**
 * DecoratorAction
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletWindowActionState implements Serializable
{
    private List actions = new Vector();
    private String windowState;
    private String portletMode;
    
    public PortletWindowActionState(String windowState, String portletMode)
    {        
        this.windowState = windowState;
        this.portletMode = portletMode;
    }
    

    /**
     * @return Returns the actions.
     */
    public List getActions()
    {
        return actions;
    }
    /**
     * @return Returns the portletMode.
     */
    public String getPortletMode()
    {
        return portletMode;
    }
    /**
     * @return Returns the windowState.
     */
    public String getWindowState()
    {
        return windowState;
    }
}
