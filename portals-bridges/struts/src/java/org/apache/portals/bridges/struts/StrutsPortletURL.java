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
package org.apache.portals.bridges.struts;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * StrutsPortletURL
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class StrutsPortletURL
{
    public static final String PAGE = "_spage";
    public static final String ORIGIN = "_sorig";
    public static String getPageURL(ServletRequest request)
    {
        return request.getParameter(PAGE);
    }
    public static String getOriginURL(ServletRequest request)
    {
        return request.getParameter(ORIGIN);
    }
    private static PortletURL createPortletURL(ServletRequest request,
            String pageURL, boolean actionURL)
    {
        RenderResponse renderResponse = (RenderResponse) request
                .getAttribute("javax.portlet.response");
        PortletURL portletURL;
        if (actionURL)
            portletURL = renderResponse.createActionURL();
        else
            portletURL = renderResponse.createRenderURL();
        if (request instanceof HttpServletRequest)
        {
            String contextPath = ((HttpServletRequest) request)
                    .getContextPath();
            if (pageURL.startsWith(contextPath))
                pageURL = pageURL.substring(contextPath.length());
        }
        portletURL.setParameter(PAGE, pageURL);
        if (actionURL)
        {
            String originURL = request.getParameter(PAGE);
            if (originURL != null)
                portletURL.setParameter(ORIGIN, originURL);
        }
        return portletURL;
    }
    public static PortletURL createRenderURL(ServletRequest request,
            String pageURL)
    {
        return createPortletURL(request, pageURL, false);
    }
    public static PortletURL createActionURL(ServletRequest request,
            String pageURL)
    {
        return createPortletURL(request, pageURL, true);
    }
}