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
package org.apache.jetspeed.portlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import org.apache.portals.bridges.util.PreferencesHelper;


/**
 * IFrameGenericPortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class IFrameGenericPortlet extends GenericVelocityPortlet
{
    private Map attributes = new HashMap();

    private Map maxAttributes = new HashMap();

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        attributes.put("SRC", "http://www.apache.org");
        attributes.put("ALIGN", "BOTTOM");
        attributes.put("CLASS", "");
        attributes.put("FRAMEBORDER", "0");
        attributes.put("ID", "");
        attributes.put("MARGINHEIGHT", "0");
        attributes.put("MARGINWIDTH", "0");
        attributes.put("NAME", "");

        attributes.put("HEIGHT", "100%");
        attributes.put("WIDTH", "100%");
        attributes.put("SCROLLING", "NO");
        attributes.put("STYLE", "");

        maxAttributes.put("HEIGHT", "800");
        maxAttributes.put("WIDTH", "100%");
        maxAttributes.put("SCROLLING", "AUTO");
        maxAttributes.put("STYLE", "");
    }

    private String getAttributePreference(PortletPreferences prefs, String attribute)
    {
        return this.getMappedAttributePreference(prefs, attribute, attributes);
    }

    private String getMaxAttributePreference(PortletPreferences prefs, String attribute)
    {
        return this.getMappedAttributePreference(prefs, "MAX-" + attribute, maxAttributes);
    }

    private String getMappedAttributePreference(PortletPreferences prefs, String attribute, Map map)
    {
        return prefs.getValue(attribute, (String) map.get(attribute));
    }

    private void appendAttribute(PortletPreferences prefs, StringBuffer content, String attribute, Map map)
    {
        String value;
        
        if (map == maxAttributes)
            value = getMaxAttributePreference(prefs, attribute);
        else
            value = getAttributePreference(prefs, attribute);
        
        System.out.println("ATT = " + attribute + "VALUE = " + value);
        if (value == null || value == "") { return; }
        content.append(" ").append(attribute).append("=\"").append(value).append("\"");
    }

    private void appendAttribute(PortletPreferences prefs, StringBuffer content, String attribute)
    {
        appendAttribute(prefs, content, attribute, attributes);
    }

    private void appendMaxAttribute(PortletPreferences prefs, StringBuffer content, String attribute)
    {
        appendAttribute(prefs, content, attribute, maxAttributes);
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        doIFrame(request, response);
    }

    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        Context context = getContext(request);
        PortletPreferences prefs = request.getPreferences();
        Iterator it = prefs.getMap().entrySet().iterator();
        context.put("prefs", it);
        super.doEdit(request, response);
    }
    
    /**
     * Render IFRAME content
     */
    protected void doIFrame(RenderRequest request, RenderResponse response) throws IOException
    {
        PortletPreferences prefs = request.getPreferences();
        // generate HTML IFRAME content
        StringBuffer content = new StringBuffer(4096);
        content.append("<IFRAME");
        appendAttribute(prefs, content, "SRC");
        appendAttribute(prefs, content, "ALIGN");
        appendAttribute(prefs, content, "CLASS");
        appendAttribute(prefs, content, "FRAMEBORDER");
        appendAttribute(prefs, content, "ID");
        appendAttribute(prefs, content, "MARGINHEIGHT");
        appendAttribute(prefs, content, "MARGINWIDTH");
        appendAttribute(prefs, content, "NAME");
        if (request.getWindowState().equals(WindowState.MAXIMIZED))
        {
            appendMaxAttribute(prefs, content, "HEIGHT");
            appendMaxAttribute(prefs, content, "WIDTH");
            appendMaxAttribute(prefs, content, "SCROLLING");
            appendMaxAttribute(prefs, content, "STYLE");
        }
        else
        {
            appendAttribute(prefs, content, "HEIGHT");
            appendAttribute(prefs, content, "WIDTH");
            appendAttribute(prefs, content, "SCROLLING");
            appendAttribute(prefs, content, "STYLE");
        }
        content.append(">");
        content.append("<P STYLE=\"textAlign:center\"><A HREF=\"").append(getAttributePreference(prefs, "SRC")).append(
                "\">").append(getAttributePreference(prefs, "SRC")).append("</A></P>");
        content.append("</IFRAME>");

        // set required content type and write HTML IFRAME content
        response.setContentType("text/html");
        response.getWriter().print(content.toString());
    }

    /**
     * Save the prefs
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
        PortletPreferences prefs = request.getPreferences();
        PreferencesHelper.requestParamsToPreferences(request);
        prefs.store();
        actionResponse.setPortletMode(PortletMode.VIEW);
    }
    
}