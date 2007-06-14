/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import javax.portlet.GenericPortlet;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletMode;

/**
 * @author <a href="mailto:woon_san@yahoo.com">Woonsan Ko</a>
 * @version $Id$
 */
public class HelloGroovy extends GenericPortlet
{
    public void doView(RenderRequest request, RenderResponse response)
    {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher rd = 
            context.getRequestDispatcher("/WEB-INF/demo/groovy/hello-groovy-view.jsp");
        rd.include(request, response);
    }
    
    public void doEdit(RenderRequest request, RenderResponse response)
    {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher rd = 
            context.getRequestDispatcher("/WEB-INF/demo/groovy/hello-groovy-edit.jsp");
        rd.include(request, response);
    }

    public void doHelp(RenderRequest request, RenderResponse response)
    {
        PortletContext context = getPortletContext();
        PortletRequestDispatcher rd = 
            context.getRequestDispatcher("/WEB-INF/demo/groovy/hello-groovy-help.jsp");
        rd.include(request,response);
    }
    
    public void processAction(ActionRequest request, ActionResponse response)
    {
        String message = request.getParameter("message");
        
        if (null != message && !"".equals(message)) {
            PortletPreferences prefs = request.getPreferences();
            prefs.setValue("message", message);
            prefs.store();
            response.setPortletMode(PortletMode.VIEW);
        }
    }
}