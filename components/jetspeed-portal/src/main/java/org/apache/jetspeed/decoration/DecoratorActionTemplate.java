/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.decoration;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

public class DecoratorActionTemplate
{
    protected static final String ACTION_TYPE_MODE = "mode";
    protected static final String ACTION_TYPE_STATE = "state";
    protected static final String ACTION_TYPE_BOTH = "both";

    private String action;
    private PortletMode mode;
    private PortletMode customMode;
    private WindowState state;
    private WindowState customState;

    private String actionType;

    public DecoratorActionTemplate(String action, PortletMode mode, PortletMode customMode, WindowState state, WindowState customState)
    {
        this.action = action;
        this.mode = mode;
        this.customMode = customMode;
        this.state = state;
        this.customState = customState;
        if ( mode != null )
        {
            this.actionType = ( state != null ) ? ACTION_TYPE_BOTH : ACTION_TYPE_MODE;
        }
        else if ( state != null )
        {
            this.actionType = ACTION_TYPE_STATE;
        }
    }

    public DecoratorActionTemplate(String action, PortletMode mode, WindowState state)
    {
        this(action,mode,mode,state,state);
    }

    public DecoratorActionTemplate(PortletMode mode, PortletMode customMode)
    {
        this(mode.toString(), mode, customMode, null, null);
    }

    public DecoratorActionTemplate(PortletMode mode)
    {
        this(mode,mode);
    }

    public DecoratorActionTemplate(WindowState state, WindowState customState)
    {
        this(state.toString(), null, null, state, customState);
    }

    public DecoratorActionTemplate(WindowState state)
    {
        this(state,state);
    }

    public String getAction()
    {
        return action;
    }

    public String getActionType()
    {
        return actionType;
    }

    public PortletMode getCustomMode()
    {
        return customMode;
    }

    public WindowState getCustomState()
    {
        return customState;
    }

    public PortletMode getMode()
    {
        return mode;
    }

    public WindowState getState()
    {
        return state;
    }
    
    public int hashCode()
    {
        return action.hashCode();
    }
    
    public boolean equals(Object o)
    {
        if ( o != null && o instanceof DecoratorActionTemplate)
        {
            return action.equals(((DecoratorActionTemplate)o).action);
        }
        return false;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public void setActionType( String actionType )
    {
        this.actionType = actionType;
    }

    public void setCustomMode(PortletMode customMode)
    {
        this.customMode = customMode;
    }

    public void setCustomState(WindowState customState)
    {
        this.customState = customState;
    }

    public void setMode(PortletMode mode)
    {
        this.mode = mode;
    }

    public void setState(WindowState state)
    {
        this.state = state;
    }
}
