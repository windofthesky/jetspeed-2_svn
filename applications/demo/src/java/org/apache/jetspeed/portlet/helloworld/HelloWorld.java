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
package org.apache.jetspeed.portlet.helloworld;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 *  This example portlet will display text <pre>This is a test from hello.jsp</pre>
 *
 * <strong>data/portletentityregistry.xml</strong><br/>
 * The following defines the associates the portlet 5.1 to
 * the portlet HelloPortlet in the PortletContainer HW_App.<br/>
 * <pre>
 *   <application id="5">
 *     <desc>HW_App</desc>
 *     <portlet id="1">
 *       <desc>HW_App.HelloPortlet</desc>
 *     </portlet> 
 *   </application>
 * </pre>
 *
 * <strong>data/portletregistry.xml</strong><br>
 * The following will include the portlet 5.3 in the fragment named P2.<br/>
 * <pre>
 *   <fragment name="p2" type="portlet">
 *       <property name="portlet" value="5.1"/>
 *   </fragment>
 * </pre>
 *
 * Also make sure <pre>container.jar</pre> is in <pre>WEB-INF/lib</pre>
 *
 * @author  paul
 */
public class HelloWorld extends GenericPortlet
{
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        System.out.println("HelloWorldPortlet: initializing portlet, config = " + config.getPortletName());
        super.init(config);
    }
    
    /*
     * The content is generated by <pre>/WEB-INF/hello.jsp</pre>
     */
    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/hello.jsp");
        rd.include(request,response);
    }
    
    public void doHelp(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/hello.jsp");
        rd.include(request,response);
    }

    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        PortletContext context = getPortletContext();
        response.setContentType("text/html");

        PortletURL url = response.createRenderURL();
        // url.addParameter("test", "value");
        
        response.getWriter().println("<br/><b>Init Param 'Hello' = " + this.getInitParameter("hello") +  "</b>");
        response.getWriter().println("<br/><b>Render URL = " + url +  "</b>");
        
        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/hello.jsp");
        rd.include(request, response);        
    }
}
