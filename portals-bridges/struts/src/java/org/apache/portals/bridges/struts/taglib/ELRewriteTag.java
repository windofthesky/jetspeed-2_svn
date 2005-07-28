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

import javax.servlet.ServletRequest; // for javadoc
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.portals.bridges.struts.PortletServlet;
import org.apache.portals.bridges.struts.config.PortletURLTypes; // javadoc
import org.apache.struts.taglib.TagUtils;
import org.apache.strutsel.taglib.utils.EvalHelper;

/**
 * Supports the Struts html-el:rewrite tag to be used within a Portlet context.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ELRewriteTag extends org.apache.strutsel.taglib.html.ELRewriteTag 
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

    private String actionURL;
    /**
     * Render an ActionURL when set to "true"
     * <p>
     * Supports jstl expression language.
     * </p>
     * @param actionURL when (evaluated to) "true" renders an ActionURL
     */
    public void setActionURL(String actionURL)
    {
        // delay evaluation of urlType to doStartTag
        this.actionURL = actionURL;
    }
    
    public String getRenderURL()
    {
        return urlType != null && urlType.equals(PortletURLTypes.URLType.RENDER) ? "true" : "false";
    }
        
    private String renderURL;
    /**
     * Render a RenderURL when set to "true"
     * <p>
     * Supports jstl expression language.
     * </p>
     * @param renderURL when (evaluated to) "true" renders a RenderURL
     */
    public void setRenderURL(String renderURL)
    {
        // delay evaluation of urlType to doStartTag
        this.renderURL = renderURL;
    }

    public String getResourceURL()
    {
        return urlType != null && urlType.equals(PortletURLTypes.URLType.RESOURCE) ? "true" : "false";
    }
        
    private String resourceURL;
    
    /**
     * Render a ResourceURL when set to "true"
     * <p>
     * Supports jstl expression language.
     * </p>
     * @param resourceURL when (evaluated to) "true" renders a ResourceURL
     */
    public void setResourceURL(String resourceURL)
    {
        // delay evaluation of urlType to doStartTag
        this.resourceURL = resourceURL;
    }

    /**
     * Generates a PortletURL or a ResourceURL for the link when in the context of a
     * {@link PortletServlet#isPortletRequest(ServletRequest) PortletRequest}, otherwise
     * the default behaviour is maintained.
     * @return the link url
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException
    {
        evaluateExpressions();
        
        if ( PortletServlet.isPortletRequest(pageContext.getRequest()))
        {
            String url = null;
            BodyContent bodyContent = pageContext.pushBody();
            try
            {
                super.doStartTag();
                url = bodyContent.getString();
                
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
            }
            finally
            {
                pageContext.popBody();
            }
            TagUtils.getInstance().write(pageContext, url);
            return (SKIP_BODY);
        }
        else
        {
            return super.doStartTag();
        }
    }
    
    /**
     * Resolve the {@link #actionURL}, {@link #renderURL} and {@link #resourceURL} attributes
     * using the Struts JSTL expression evaluation engine ({@link EvalHelper}).
     * @exception JspException if a JSP exception has occurred
     */
    private void evaluateExpressions() throws JspException {
        Boolean value;

        value = EvalHelper.evalBoolean("actionURL", actionURL,this, pageContext);
        if ( value != null && value.booleanValue() )
        {
            urlType = PortletURLTypes.URLType.ACTION;
        }
        if ( urlType == null )
        {
            value = EvalHelper.evalBoolean("renderURL", renderURL,this, pageContext);
            if ( value != null && value.booleanValue() )
            {
                urlType = PortletURLTypes.URLType.RENDER;
            }            
        }
        if ( urlType == null )
        {
            value = EvalHelper.evalBoolean("resourceURL", resourceURL,this, pageContext);
            if ( value != null && value.booleanValue() )
            {
                urlType = PortletURLTypes.URLType.RESOURCE;
            }            
        }
    }
    
    public void release() {

        super.release();
        urlType = null;
        actionURL = null;
        renderURL = null;
        resourceURL = null;
    }
}