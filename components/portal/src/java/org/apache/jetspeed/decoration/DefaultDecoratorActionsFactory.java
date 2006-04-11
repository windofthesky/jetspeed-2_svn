package org.apache.jetspeed.decoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

public class DefaultDecoratorActionsFactory extends AbstractDecoratorActionsFactory
{
    private final List supportedActions;

    public DefaultDecoratorActionsFactory()
    {
        ArrayList list = new ArrayList(JetspeedActions.getStandardPortletModes());
        list.addAll(JetspeedActions.getStandardWindowStates());
        supportedActions = Collections.unmodifiableList(list);
    }

    public List getSupportedActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode pm,
                    WindowState ws, Decoration decoration)
    {
        return supportedActions;
    }
}
