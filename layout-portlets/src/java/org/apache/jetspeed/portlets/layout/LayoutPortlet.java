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
package org.apache.jetspeed.portlets.layout;

import java.io.IOException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.velocity.JetspeedPowerTool;
import org.apache.pluto.om.window.PortletWindow;

/**
 */
public class LayoutPortlet extends org.apache.portals.bridges.common.GenericServletPortlet
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(LayoutPortlet.class);
    public void init( PortletConfig config ) throws PortletException
    {
        super.init(config);
    }

    public void doHelp( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        response.setContentType("text/html");
        JetspeedPowerTool jpt = getJetspeedPowerTool(request);

        PortletPreferences prefs = request.getPreferences();
        String absHelpPage = "";

        // request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, getPage(request));
        // request.setAttribute("fragment", getFragment(request, false));        

        if (prefs != null)
        {

            try
            {
                String helpPage = prefs.getValue(PARAM_HELP_PAGE, null);
                if (helpPage == null)
                {
                    helpPage = this.getInitParameter(PARAM_HELP_PAGE);
                    if (helpPage == null)
                        helpPage = "columns";
                }
                

                // TODO: Need to retreive layout.properties instead of
                // hard-coding ".vm"
                absHelpPage = jpt.getTemplate(helpPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + "-help.vm",
                        JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();
                log.debug("Path to help page for LayoutPortlet " + absHelpPage);
                request.setAttribute(PARAM_VIEW_PAGE, absHelpPage);
            }
            catch (TemplateLocatorException e)
            {
                throw new PortletException("Unable to locate view page " + absHelpPage, e);
            }
        }
        super.doView(request, response);

     //   request.removeAttribute(PortalReservedParameters.PAGE_ATTRIBUTE);
     //   request.removeAttribute("fragment");
     //   request.removeAttribute("layout");
     //   request.removeAttribute("dispatcher");
    }

    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        response.setContentType("text/html");
        RequestContext context = getRequestContext(request);
        PortletWindow window = context.getPortalURL().getNavigationalState().getMaximizedWindow();
        boolean maximized = (window != null);

        if (maximized)
        {
            request.setAttribute("layout", getMaximizedLayout(request));
        }
        else
        {
            request.setAttribute("layout", getFragment(request, false));
        }

        PortletPreferences prefs = request.getPreferences();
        if (prefs != null)
        {
            String absViewPage = null;
            try
            {
                JetspeedPowerTool jpt = getJetspeedPowerTool(request);
                if (maximized)
                {
                    String viewPage = prefs.getValue(PARAM_MAX_PAGE, null);
                    if (viewPage == null)
                    {
                        viewPage = this.getInitParameter(PARAM_MAX_PAGE);
                        if (viewPage == null)
                            viewPage = "maximized";
                    }

                    // TODO: Need to retreive layout.properties instead of
                    // hard-coding ".vm"
                    absViewPage = jpt.getTemplate(viewPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + ".vm",
                            JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();
                }
                else
                {
                    String viewPage = prefs.getValue(PARAM_VIEW_PAGE, null);
                    if (viewPage == null)
                    {
                        viewPage = this.getInitParameter(PARAM_VIEW_PAGE);
                        if (viewPage == null)
                            viewPage = "columns";
                    }
                    

                    // TODO: Need to retreive layout.properties instead of
                    // hard-coding ".vm"
                    absViewPage = jpt.getTemplate(viewPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + ".vm",
                            JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();
                }
                log.debug("Path to view page for LayoutPortlet " + absViewPage);
                request.setAttribute(PARAM_VIEW_PAGE, absViewPage);
            }
            catch (TemplateLocatorException e)
            {
                throw new PortletException("Unable to locate view page " + absViewPage, e);
            }
        }

        super.doView(request, response);

        request.removeAttribute(PortalReservedParameters.PAGE_ATTRIBUTE);
        request.removeAttribute("fragment");
        request.removeAttribute("layout");
        request.removeAttribute("dispatcher");
    }

    /**
     * <p>
     * initJetspeedPowerTool
     * </p>
     * 
     * @param request
     * @param response
     * @return
     * @throws PortletException
     */
    protected JetspeedPowerTool getJetspeedPowerTool( RenderRequest request ) throws PortletException
    {
        JetspeedPowerTool tool = (JetspeedPowerTool) (RequestContext) request.getAttribute(PortalReservedParameters.JETSPEED_POWER_TOOL_REQ_ATTRIBUTE);
        RequestContext requestContext = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);

        if (tool == null)
        {

            try
            {
                if (requestContext == null)
                {
                    throw new IllegalStateException(
                            "LayoutPortlet unable to handle request because there is no RequestContext in "
                                    + "the HttpServletRequest.");
                }

                tool = new JetspeedPowerTool(requestContext);
                request.setAttribute(PortalReservedParameters.JETSPEED_POWER_TOOL_REQ_ATTRIBUTE, tool);
            }

            catch (Exception e1)
            {
                throw new PortletException("Unable to init JetspeedPowerTool: " + e1.toString(), e1);
            }
        }
        
        return tool;
    }

    protected Fragment getFragment( RenderRequest request, boolean maximized )
    {
        String attribute = (maximized)
                ? PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE
                : PortalReservedParameters.FRAGMENT_ATTRIBUTE;
        return (Fragment) request.getAttribute(attribute);       
    }

    protected Fragment getMaximizedLayout( RenderRequest request )
    {
        return (Fragment) request.getAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
    }

    protected RequestContext getRequestContext( RenderRequest request )
    {
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        if (requestContext != null)
        {
            return requestContext;
        }
        else
        {
            throw new IllegalStateException(
                    "getRequestContext() failed as it appears that now RenderRequest is available within the RenderRequest");
        }
    }

    /**
     * <p>
     * doEdit
     * </p>
     * 
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest,
     *          javax.portlet.RenderResponse)
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    public void doEdit( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        doView(request, response);
    }
}