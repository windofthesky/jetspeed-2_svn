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
package org.apache.portals.applications.desktop;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

public class FlashPortlet extends GenericVelocityPortlet
{
    public static final String HEIGHT = "HEIGHT";
    public static final String WIDTH = "WIDTH";
    public static final String SRC = "SRC";
    public static final String MAX_SRC = "MAX-SRC";
    public static final String MAX_HEIGHT = "MAX-HEIGHT";
    public static final String MAX_WIDTH = "MAX-WIDTH";
        
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        Context context = super.getContext(request);
        PortletPreferences prefs = request.getPreferences();        
        if (request.getWindowState().toString().equals(WindowState.MAXIMIZED.toString()))
        {
            context.put(HEIGHT, prefs.getValue(MAX_HEIGHT, "800"));
            context.put(WIDTH, prefs.getValue(MAX_WIDTH, "600"));
            context.put("windowState", "max");
            String src = prefs.getValue(MAX_SRC, "");
            if (src.equals(""))
            {
                src = prefs.getValue(SRC, "");
            }
            context.put(SRC, src);
            
        }
        else
        {
            context.put("windowState", "normal");
            context.put(HEIGHT, prefs.getValue(HEIGHT, "250"));
            context.put(WIDTH, prefs.getValue(WIDTH, "250"));
            context.put(SRC, prefs.getValue(SRC, ""));
                        
        }
        super.doView(request, response);
    }
    
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");        
        doPreferencesEdit(request, response);
    }    


    /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        String source = request.getParameter(SRC);
        String height = request.getParameter(HEIGHT);
        String width = request.getParameter(WIDTH);
        String maxSource = request.getParameter(MAX_SRC);
        String maxHeight = request.getParameter(MAX_HEIGHT);
        String maxWidth = request.getParameter(MAX_WIDTH);
        
        PortletPreferences prefs = request.getPreferences();
        prefs.setValue(SRC, source);
        prefs.setValue(HEIGHT, height);
        prefs.setValue(WIDTH, width);
        prefs.setValue(MAX_SRC, maxSource);
        prefs.setValue(MAX_HEIGHT, maxHeight);
        prefs.setValue(MAX_WIDTH, maxWidth);        
        prefs.store();
        super.processAction(request, response);
    }    
}
    