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
     * Indicates if a RenderURL or ActionURL must be generated.
     * <p>If not specified, the type will be determined by
     * {@link PortletURLTypes#isActionURL(String)}</p>.
     */
    protected Boolean actionURL = null;
        
    public String getActionURL()
    {
        return actionURL != null ? action.toString() : null;
    }

    /**
     * Render an ActionURL when set to "true" otherwise render a RenderURL
     * @param actionURL "true" renders an ActionURL otherwise a RenderURL
     */
    public void setActionURL(String actionURL)
    {
        this.actionURL = actionURL != null ? actionURL.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE : null; 
    }
    
    public String getRenderURL()
    {
        return actionURL != null ? actionURL.booleanValue() ? "false" : "true" : null;
    }
        
    /**
     * Render a RenderURL when set to "true" otherwise render an ActionURL
     * @param renderURL "true" renders a RenderURL otherwise an ActionURL
     */
    public void setRenderURL(String renderURL)
    {
        this.actionURL = renderURL != null ? renderURL.equalsIgnoreCase("true") ? Boolean.FALSE : Boolean.TRUE : null; 
    }

    /**
     * Generates a PortletURL for the link when in the context of a
     * {@link PortletServlet#isPortletRequest(ServletRequest) PortletRequest}, otherwise
     * the default behaviour is maintained.
     * @return the link url
     * @exception JspException if a JSP exception has occurred
     */
    protected String calculateURL() throws JspException 
    {
        if ( PortletServlet.isPortletRequest(pageContext.getRequest() ))
        {
            return TagsSupport.getPortletURL(pageContext, super.calculateURL(), actionURL);
        }
        else
        {
            return super.calculateURL();
        }
    }
    
    public void release() {

        super.release();
        actionURL = null;
    }
}
