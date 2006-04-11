package org.apache.jetspeed.decoration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

public abstract class AbstractDecoratorActionsFactory implements DecoratorActionsFactory
{
    public List getDecoratorActions(RequestContext rc, PortletApplication pa, PortletWindow pw, PortletMode pm,
                    WindowState ws, Decoration decoration, List actionTemplates)
    {
        DecoratorAction action;
        ArrayList actions = new ArrayList();
        
        Iterator iter = actionTemplates.iterator();
        while (iter.hasNext())
        {
            action = createAction(rc, pw, decoration, (DecoratorActionTemplate)iter.next());
            if ( action != null )
            {
                actions.add(action);
            }
        }
        return actions;
    }

    protected DecoratorAction createAction(RequestContext rc, PortletWindow pw, Decoration decoration,
                    DecoratorActionTemplate template)
    {
        String actionName = template.getAction();

        PortalURL portalURL = rc.getPortalURL();
        String actionURL = portalURL.createPortletURL(pw, template.getCustomMode(), template.getCustomState(),
                        portalURL.isSecure()).toString();

        String linkURL = decoration.getResource("images/" + actionName + ".gif");

        boolean customAction = (template.getMode() != null && !template.getMode().equals(template.getCustomMode()))
                        || (template.getState() != null && !template.getState().equals(template.getCustomState()));

        return new DecoratorAction(actionName, rc.getLocale(), linkURL, actionURL, customAction);
    }
}
