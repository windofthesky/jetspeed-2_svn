/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.portlet.helloworld;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
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
        response.getWriter().println("<br/><b>Init Param 'Hello' = " + this.getInitParameter("hello") +  "</b>");
        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/hello.jsp");
        rd.include(request, response);        
    }
}
