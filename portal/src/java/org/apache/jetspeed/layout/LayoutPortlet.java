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
package org.apache.jetspeed.layout;

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
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.velocity.JetspeedPowerTool;

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

        request.setAttribute("page", getPage(request));
        request.setAttribute("fragment", getFragment(request));
        request.setAttribute("dispatcher", getDispatcher(request));

        // now invoke the JSP associated with this portlet
        JetspeedPowerTool jpt = new JetspeedPowerTool(request, response, getPortletConfig());
        PortletPreferences prefs = request.getPreferences();
        if (prefs != null)
        {
            String absViewPage = null;
            try
            {
                String viewPage = prefs.getValue(PARAM_VIEW_PAGE, "columns");
                // Need to retreive layout.properties instead of hard-coding ".vm" 
                absViewPage = jpt.getTemplate(viewPage+"/"+JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE+".vm", JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAppRelativePath();
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
        request.removeAttribute("dispatcher");
    }

    protected Fragment getFragment(RenderRequest request)
    {
        // Very ugly and Pluto dependant but I don't see anything better right now
        ServletRequest innerRequest = ((HttpServletRequestWrapper) request).getRequest();
        Fragment fragment = (Fragment) innerRequest.getAttribute("org.apache.jetspeed.Fragment");

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

}