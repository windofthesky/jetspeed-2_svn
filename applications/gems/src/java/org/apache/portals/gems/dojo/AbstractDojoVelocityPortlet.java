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
package org.apache.portals.gems.dojo;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

/**
 * Abstract DOJO portlet for inserting in cross context dojo widget includes
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public abstract class AbstractDojoVelocityPortlet extends GenericVelocityPortlet implements SupportsHeaderPhase 
{
    protected void includeDojoRequires(StringBuffer headerInfoText)
    {
    }
    protected void includeDojoWidgetRequires(StringBuffer headerInfoText)
    {
        appendHeaderText(headerInfoText, "dojo.widget.Manager");
    }
    protected void includeDojoCustomWidgetRequires(StringBuffer headerInfoText)
    {
        headerInfoText.append("dojo.hostenv.setModulePrefix('jetspeed.ui.widget', '../desktop/widget');\r\n");
        headerInfoText.append("dojo.hostenv.setModulePrefix('jetspeed.desktop', '../desktop/core');\r\n");
    }
    
    /*
     * Class specific logger.
     */
    private final static Log log = LogFactory.getLog(AbstractDojoVelocityPortlet.class);

    /*
     * Portlet constructor.
     */
    public AbstractDojoVelocityPortlet() 
    {
        super();
    }

    /*
     * Include Dojo and Turbo header content using header resource component.
     *
     * @param request render request
     * @param response render response
     */    
    public void doHeader(PortletHeaderRequest request, PortletHeaderResponse response)
    throws PortletException
    {
        String portalContextPath = request.getPortalContextPath();

        // use header resource component to ensure header logic is included only once
        HeaderResource headerResource = response.getHeaderResource();
        StringBuffer headerInfoText = new StringBuffer();
        Map headerInfoMap = null;

        // add dojo if not already in use as desktop
        if (!request.isDesktopEncoder()) 
        {
            // dojo configuration
            headerInfoText.setLength(0);
            headerInfoText.append("\r\n");
            headerInfoText.append("var djConfig = {isDebug: true, debugAtAllCosts: true, baseScriptUri: '" + portalContextPath + "/javascript/dojo/'};\r\n");
            headerInfoMap = new HashMap(8);
            headerInfoMap.put("type", "text/javascript");
            headerInfoMap.put("language", "JavaScript");
            headerResource.addHeaderInfo("script", headerInfoMap, headerInfoText.toString());
    
            // dojo script
            headerInfoMap = new HashMap(8);
            headerInfoMap.put("language", "JavaScript");
            headerInfoMap.put("type", "text/javascript");
            headerInfoMap.put("src", portalContextPath + "/javascript/dojo/dojo.js");
            headerResource.addHeaderInfo("script", headerInfoMap, "");
            
            // dojo includes
            headerInfoText.setLength(0);
            headerInfoText.append("\r\n");
            includeDojoRequires(headerInfoText);
            includeDojoWidgetRequires(headerInfoText);
            includeDojoCustomWidgetRequires(headerInfoText);
            
            headerInfoText.append("dojo.require('jetspeed.desktop.compatibility');\r\n");

            headerInfoMap = new HashMap(8);
            headerInfoMap.put("language", "JavaScript");
            headerInfoMap.put("type", "text/javascript");
            headerResource.addHeaderInfo("script", headerInfoMap, headerInfoText.toString());
        }
        
        // close DOJO if not already in use as desktop
        if (!request.isDesktopEncoder()) 
        {
            // complete DoJo includes
            headerInfoText.setLength(0);
            headerInfoText.append("\r\n");
            headerInfoText.append("dojo.hostenv.writeIncludes();\r\n");
            headerInfoMap = new HashMap(8);
            headerInfoMap.put("language", "JavaScript");
            headerInfoMap.put("type", "text/javascript");
            headerResource.addHeaderInfo("script", headerInfoMap, headerInfoText.toString());
        }

        // add jetspeed widget package if not already in use as desktop
        if (!request.isDesktopEncoder()) 
        {
            headerInfoText.setLength(0);
            headerInfoText.append("\r\n");
            headerInfoText.append("dojo.widget.manager.registerWidgetPackage('jetspeed.ui.widget');\r\n");
            headerInfoMap = new HashMap(8);
            headerInfoMap.put("language", "JavaScript");
            headerInfoMap.put("type", "text/javascript");
            headerResource.addHeaderInfo("script", headerInfoMap, headerInfoText.toString());
        }
        
        if (!request.isDesktopEncoder()) 
        {
            headerInfoText.setLength(0);
            headerInfoText.append("\r\n");
            headerInfoText.append("html, body\r\n");
            headerInfoText.append("{\r\n");
            headerInfoText.append("   width: 100%;\r\n");
            headerInfoText.append("   height: 100%;\r\n");
            headerInfoText.append("   margin: 0 0 0 0;\r\n");
            headerInfoText.append("}\r\n");
            headerInfoMap = new HashMap(8);
            headerResource.addHeaderInfo("style", headerInfoMap, headerInfoText.toString());
        }
    }
    
    protected void appendHeaderText(StringBuffer headerInfoText, String header)
    {
        headerInfoText.append("dojo.require('" + header + "');\r\n");
    }
}
