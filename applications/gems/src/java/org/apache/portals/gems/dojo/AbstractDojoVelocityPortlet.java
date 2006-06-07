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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.request.RequestContext;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

/**
 * Abstract DOJO portlet for inserting in cross context dojo widget includes
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public abstract class AbstractDojoVelocityPortlet extends GenericVelocityPortlet 
{
    protected abstract void includeDojoRequires(StringBuffer headerInfo);    
    
    /*
     * Class specific logger.
     */
    private final static Log log = LogFactory.getLog(AbstractDojoVelocityPortlet.class);

    /*
     * Jetspeed header resource component
     */
    protected HeaderResourceFactory headerResourceFactoryComponent;

    /*
     * Portlet constructor.
     */
    public AbstractDojoVelocityPortlet() 
    {
        super();
    }

    /*
     * Portlet lifecycle method.
     */
    public void init() throws PortletException 
    {
        super.init();

        // access jetspeed heaader resource component
        synchronized (this) 
        {
            if (headerResourceFactoryComponent == null) 
            {
                headerResourceFactoryComponent = (HeaderResourceFactory) 
                    getPortletContext().getAttribute(CommonPortletServices.CPS_HEADER_RESOURCE_FACTORY);
            }
            if (headerResourceFactoryComponent == null) 
            {
                throw new PortletException("Failed to find the HeaderResourceFactoryComponent instance.");
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.portlet.GenericPortlet#doDispatch(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException 
    {
        // include header content
        includeHeaderContent(request,response);

        // dispatch normally
        super.doDispatch(request, response);
    }

    /*
     * Include Dojo and Turbo header content using header resource component.
     *
     * @param request render request
     * @param response render response
     */
    protected void includeHeaderContent(RenderRequest request, RenderResponse response) 
    {
        // get portal context path
        RequestContext requestContext = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        String portalContextPath = requestContext.getRequest().getContextPath();

        // use header resource component to ensure header logic is included only once
        HeaderResource headerResource = headerResourceFactoryComponent.getHeaderResouce(request);
        StringBuffer headerInfoText = new StringBuffer();
        Map headerInfoMap = null;

        // detect jetspeed-desktop
        String requestEncoder = (String)requestContext.getRequest().getParameter("encoder");

        boolean isJetspeedDesktop = ((requestEncoder == null) || !requestEncoder.equals("desktop")) ? false : true;
        Context velocityContext = getContext(request);
        velocityContext.put("isJetspeedDesktop", new Boolean( isJetspeedDesktop ) );

        // add dojo if not already in use as desktop
        if (!isJetspeedDesktop) 
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
            
            headerInfoText.append("dojo.hostenv.setModulePrefix('jetspeed.desktop', '../desktop/core');\r\n");
            headerInfoText.append("dojo.require('jetspeed.desktop.compatibility');\r\n");

            headerInfoMap = new HashMap(8);
            headerInfoMap.put("language", "JavaScript");
            headerInfoMap.put("type", "text/javascript");
            headerResource.addHeaderInfo("script", headerInfoMap, headerInfoText.toString());
        }
        
        // close DOJO if not already in use as desktop
        if (!isJetspeedDesktop) 
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

    }
    
    protected void appendHeaderText(StringBuffer headerInfoText, String header)
    {
        headerInfoText.append("dojo.require('" + header + "');\r\n");
    }
}
