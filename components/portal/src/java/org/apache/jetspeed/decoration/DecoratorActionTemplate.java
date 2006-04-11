package org.apache.jetspeed.decoration;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

public class DecoratorActionTemplate
{
    private String action;
    private PortletMode mode;
    private PortletMode customMode;
    private WindowState state;
    private WindowState customState;

    public DecoratorActionTemplate(String action, PortletMode mode, PortletMode customMode, WindowState state, WindowState customState)
    {
        this.action = action;
        this.mode = mode;
        this.customMode = customMode;
        this.state = state;
        this.customState = customState;
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
