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
package org.apache.portals.bridges.struts.taglib;

import javax.servlet.ServletRequest; // javadoc
import javax.servlet.jsp.JspException;

import org.apache.portals.bridges.struts.PortletServlet; // javadoc
import org.apache.portals.bridges.struts.config.PortletURLTypes;

/**
 * Supports the Struts html:link tag to be used within a Portlet context.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class LinkTag extends org.apache.struts.taglib.html.LinkTag 
{
    /**
     * Indicates which type of a url must be generated: action, render or resource.
     * <p>If not specified, the type will be determined by
     * {@link PortletURLTypes#getType(String)}</p>.
     */
    protected PortletURLTypes.URLType urlType = null;
        
    /**
     * @return "true" if an ActionURL must be rendered
     */
    public String getActionURL()
    {
        return urlType != null && urlType.equals(PortletURLTypes.URLType.ACTION) ? "true" : "false";
    }

    /**
     * Render an ActionURL when set to "true"
     * @param value "true" renders an ActionURL
     */
    public void setActionURL(String value)
    {
        this.urlType = value != null && value.equalsIgnoreCase("true") ? PortletURLTypes.URLType.ACTION : null; 
    }
    
    public String getRenderURL()
    {
        return urlType != null && urlType.equals(PortletURLTypes.URLType.RENDER) ? "true" : "false";
    }
        
    /**
     * Render a RenderURL when set to "true"
     * @param value "true" renders a RenderURL
     */
    public void setRenderURL(String value)
    {
        this.urlType = value != null && value.equalsIgnoreCase("true") ? PortletURLTypes.URLType.RENDER : null; 
    }

    public String getResourceURL()
    {
        return urlType != null && urlType.equals(PortletURLTypes.URLType.RESOURCE) ? "true" : "false";
    }
        
    /**
     * Render a ResourceURL when set to "true"
     * @param value "true" renders a ResourceURL
     */
    public void setResourceURL(String value)
    {
        this.urlType = value != null && value.equalsIgnoreCase("true") ? PortletURLTypes.URLType.RESOURCE : null; 
    }

    /**
     * Generates a PortletURL or a ResourceURL for the link when in the context of a
     * {@link PortletServlet#isPortletRequest(ServletRequest) PortletRequest}, otherwise
     * the default behaviour is maintained.
     * @return the link url
     * @exception JspException if a JSP exception has occurred
     */
    protected String calculateURL() throws JspException 
    {
        if ( PortletServlet.isPortletRequest(pageContext.getRequest() ))
        {
            String url = super.calculateURL();
            
            // process embedded anchor
            String anchor = null;
            int hash = url.indexOf('#');
            if ( hash > -1 )
            {
                // save embedded anchor to be appended later and strip it from the url
                anchor = url.substring(hash);
                url = url.substring(0,hash);
            }
            
            url = TagsSupport.getURL(pageContext, url, urlType);

            if ( anchor != null )
            {
                url = url + anchor;
            }
            return url;
        }
        else
        {
            return super.calculateURL();
        }
    }
    
    public void release() {

        super.release();
        urlType = null;
    }
}
