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
package org.apache.jetspeed.portlets.decorator;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * GenericDecoratorPortlet
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GenericDecoratorPortlet
    extends GenericPortlet
    implements DecoratorPortlet
{
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        PortletContext context = getPortletContext();
        response.setContentType("text/html");

//        PortletURL url = response.createRenderURL();
        // url.addParameter("test", "value");
        
        // OK, so this is square one as they say. We're going to need some portlet tools next
        response.getWriter().println("<br/><b>Decorator Portlet</b>");
        
//        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/hello.jsp");
//        rd.include(request, response);        
    }
    
}
