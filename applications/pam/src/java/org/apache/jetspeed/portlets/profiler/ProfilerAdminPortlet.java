/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.profiler;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.portals.bridges.myfaces.FacesPortlet;


//import org.apache.portals.bridges.myfaces.FacesPortlet;

/**
 * This portlet is a browser over all folders and documents in the system.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfilerAdminPortlet extends FacesPortlet
{

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            System.out.println("[V]key/value = " + key + ": [" + request.getParameter(key) + "]");
        }
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements())
        {
            String key = (String)e.nextElement();
            System.out.println("[A]key/value = " + key + ": [" + request.getParameter(key) + "]");
        }        
        super.processAction(request, response);
    }
    
    
}
