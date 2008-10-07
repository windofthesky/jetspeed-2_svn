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
package org.apache.jetspeed.demo.jmx;

import java.io.IOException;

import java.util.List;

import javax.management.ObjectName;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import mx4j.connector.RemoteMBeanServer;
import mx4j.connector.rmi.jrmp.JRMPConnector;

/**
 * JMXPortlet
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JMXPortlet extends GenericPortlet
{

    /**
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {

        PortletContext context = getPortletContext();
        JRMPConnector connector = new JRMPConnector();

        // Pass in the adaptor's JNDI name, no properties
        String jndiName = "jrmp";

        try
        {
            connector.connect(jndiName, null);
            // Get the remote MBeanServer from the connector
            // And use it as if it is an MBeanServer
            RemoteMBeanServer server = connector.getRemoteMBeanServer();
            List portlets = (List) server.getAttribute(new ObjectName("org.apache.jetspeed:mbean=Registry"), "portlets");

            request.setAttribute("portlets", portlets);
        }
        catch (Exception e)
        {
            request.setAttribute("portlets", "failed to retreive number of portlets");
            e.printStackTrace();
        }

        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/demo/jmx/jmx-view.jsp");
        rd.include(request, response);

    }

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        System.out.println("Initializing JMX portlet example.  Can ya' smell what the Rock is cookin'?");
        super.init(config);
    }

}
