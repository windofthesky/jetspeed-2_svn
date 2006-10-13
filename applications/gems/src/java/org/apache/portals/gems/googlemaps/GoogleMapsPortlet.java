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
package org.apache.portals.gems.googlemaps;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;

import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.portals.gems.dojo.AbstractDojoVelocityPortlet;
/**
 * This is a simple class used to override processAction
 * to save location form submission value to location preference
 *
 * @version $Id: GoogleMapsPortlet.java 393251 2006-04-22 15:50:52Z jdp $
 */
public class GoogleMapsPortlet extends AbstractDojoVelocityPortlet
{
    
    /**
     * no change
     */
    public GoogleMapsPortlet()
    {
        super();
    }
    
    /**
     * save submitted value
     *
     * @see javax.portlet.GenericPortlet#processActions
     *
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
	String location = request.getParameter("location");
	PortletPreferences preferences = request.getPreferences();
	preferences.setValue("Location",location);
	preferences.store();
    }
    
    public void doHeader(PortletHeaderRequest request, PortletHeaderResponse response)
    throws PortletException
    {
        StringBuffer headerInfoText = new StringBuffer();
        Map headerInfoMap = null;
        
        // close DOJO if not already in use as desktop
        if (!request.isDesktopEncoder()) 
        {
            // complete DoJo includes
            headerInfoText.setLength(0);
            headerInfoMap = new HashMap(8);
            headerInfoMap.put("language", "JavaScript");            
            headerInfoMap.put("src", "http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAisHr-hr7f_yfo_m3teTC5RQXGaCFRGWXJQavRKQcb1Ew_fwkKRQ26QnpXVIkxSMwwTECWDV23ZDaLQ");
            headerInfoMap.put("type", "text/javascript");
            response.getHeaderResource().addHeaderInfo("script", headerInfoMap, headerInfoText.toString());
        }
        super.doHeader(request, response);
    }
    
    protected void includeDojoRequires(StringBuffer headerInfoText)
    {
        appendHeaderText(headerInfoText, "dojo.lang.*");
        appendHeaderText(headerInfoText, "dojo.event.*");
        appendHeaderText(headerInfoText, "dojo.io.*");             
    }    
        
}











