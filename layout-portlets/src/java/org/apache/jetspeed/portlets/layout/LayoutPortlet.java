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
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.velocity.JetspeedPowerTool;
import org.apache.pluto.om.window.PortletWindow;

/**
 */
public class LayoutPortlet extends org.apache.jetspeed.portlet.ServletPortlet
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(LayoutPortlet.class);

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");

        RequestContext context = Jetspeed.getCurrentRequestContext();
        PortletWindow window = context.getNavigationalState().getMaximizedWindow(context.getPage());
        boolean maximized = (window != null);
        
        request.setAttribute("page", getPage(request));
        request.setAttribute("fragment", getFragment(request, maximized));
        request.setAttribute("dispatcher", getDispatcher(request));
        if (maximized)
        {
            request.setAttribute("layout", getMaximizedLayout(request));
        }
        else
        {
            request.setAttribute("layout", getFragment(request, false));
        }
        // now invoke the JSP associated with this portlet
        JetspeedPowerTool jpt = new JetspeedPowerTool(request, response, getPortletConfig());
        PortletPreferences prefs = request.getPreferences();
        if (prefs != null)
        {
            String absViewPage = null;
            try
            {
                if (maximized)
                {
                    String viewPage = prefs.getValue(PARAM_MAX_PAGE, "maximized");
                    
                    // TODO: Need to retreive layout.properties instead of hard-coding ".vm" 
                    absViewPage = jpt.getTemplate(viewPage+"/"+JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE+".vm", 
                                                  JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();                    
                }
                else
                {
                    String viewPage = prefs.getValue(PARAM_VIEW_PAGE, "columns");
                    
                    // TODO: Need to retreive layout.properties instead of hard-coding ".vm" 
                    absViewPage = jpt.getTemplate(viewPage+"/"+JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE+".vm", 
                                                  JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();
                }
                log.debug("Path to view page for LayoutPortlet "+absViewPage);
                request.setAttribute(PARAM_VIEW_PAGE, absViewPage);
            }
            catch (TemplateLocatorException e)
            {
                throw new PortletException("Unable to locate view page " + absViewPage, e);
            }
        }

        super.doView(request, response);

        request.removeAttribute("page");
        request.removeAttribute("fragment");        
        request.removeAttribute("layout");
        request.removeAttribute("dispatcher");
    }

    protected Fragment getFragment(RenderRequest request, boolean maximized)
    {
        // Very ugly and Pluto dependant but I don't see anything better right now
        ServletRequest innerRequest = ((HttpServletRequestWrapper) request).getRequest();
        String attribute = (maximized) ? "org.apache.jetspeed.maximized.Fragment" : "org.apache.jetspeed.Fragment";
        Fragment fragment = (Fragment) innerRequest.getAttribute(attribute);

        return fragment;
    }

    protected Fragment getMaximizedLayout(RenderRequest request)
    {
        // Very ugly and Pluto dependant but I don't see anything better right now
        ServletRequest innerRequest = ((HttpServletRequestWrapper) request).getRequest();
        String attribute = "org.apache.jetspeed.maximized.Layout" ;
        Fragment fragment = (Fragment) innerRequest.getAttribute(attribute);
        return fragment;        
    }    
    
    protected Page getPage(RenderRequest request)
    {
        // Very ugly and Pluto dependant but I don't see anything better right now
        ServletRequest innerRequest = ((HttpServletRequestWrapper) request).getRequest();
        Page page = (Page) innerRequest.getAttribute("org.apache.jetspeed.Page");

        return page;
    }

    protected ContentDispatcher getDispatcher(RenderRequest request)
    {
        // Very ugly and Pluto dependant but I don't see anything better right now
        ServletRequest innerRequest = ((HttpServletRequestWrapper) request).getRequest();
        ContentDispatcher dispatcher = (ContentDispatcher) innerRequest.getAttribute("org.apache.jetspeed.ContentDispatcher");

        return dispatcher;
    }
    
    

    /**
     * <p>
     * doEdit
     * </p>
     *
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
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