/*
 * Created on Oct 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.jetspeed.velocity;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.Constants;
import org.apache.portals.bridges.velocity.BridgesVelocityViewServlet;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

/**
 * @version $Id$
 */
public class JetspeedVelocityViewServlet extends BridgesVelocityViewServlet
{

    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) throws Exception
    {
        PortletRequest renderRequest = (PortletRequest) request.getAttribute(Constants.PORTLET_REQUEST);
        RenderResponse renderResponse = (RenderResponse) request.getAttribute(Constants.PORTLET_RESPONSE);
        PortletConfig portletConfig = (PortletConfig) request.getAttribute(Constants.PORTLET_CONFIG);
        if (renderRequest != null)
        {
            renderRequest.setAttribute(VELOCITY_CONTEXT_ATTR, ctx);
        }        
                
        ctx.put("JS2RequestContext", request.getAttribute(RequestContext.REQUEST_PORTALENV));
        ctx.put("renderRequest", renderRequest);
        ctx.put("renderResponse", renderResponse);
        ctx.put("portletConfig", portletConfig);
        ctx.put("portletModeView", PortletMode.VIEW);
        ctx.put("portletModeEdit", PortletMode.EDIT);
        ctx.put("portletModeHelp", PortletMode.HELP);
        ctx.put("windowStateNormal", WindowState.NORMAL);
        ctx.put("windowStateMinimized", WindowState.MINIMIZED);
        ctx.put("windowStateMaximized", WindowState.MAXIMIZED);
        StringBuffer appRoot = new StringBuffer(request.getScheme()).append("://")
                                   .append(request.getServerName()).append(":")
                                   .append(request.getServerPort()).append(renderRequest.getContextPath());
        ctx.put("appRoot", appRoot.toString());
        return super.handleRequest(request, response, ctx);        
    }
}
