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

import org.apache.portals.bridges.struts.PortletServlet;
import org.apache.portals.bridges.struts.config.PortletURLTypes; //javadoc
import org.apache.strutsel.taglib.utils.EvalHelper;
// javadoc

/**
 * Supports the Struts html-el:link tag to be used within a Portlet context.
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ELLinkTag extends org.apache.strutsel.taglib.html.ELLinkTag 
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
     * struts-el support for {@link #setActionURL(String)}
     */
    protected String actionURLExpr = null;

    public String getActionURLExpr()
    {
        return actionURLExpr;
    }
    
    public void setActionURLExpr(String actionURLExpr)
    {
        this.actionURLExpr = actionURLExpr;
    }
    
    /**
     * struts-el support for {@link #setRenderURL(String)}
     */
    protected String renderURLExpr = null;

    public String getRenderURLExpr()
    {
        return renderURLExpr;
    }
    
    public void setRenderURLExpr(String renderURLExpr)
    {
        this.renderURLExpr = renderURLExpr;
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
    
    public int doStartTag() throws JspException {
        evaluateExpressions();
        return(super.doStartTag());
    }
    
    /**
     * Resolve the {@link #actionURLExpr} and {@link #renderURLExpr} attributes using the JSTL expression
     * evaluation engine ({@link EvalHelper}).
     * @exception JspException if a JSP exception has occurred
     */
    private void evaluateExpressions() throws JspException {
        String  string  = null;

        if ((string = EvalHelper.evalString("actionURL", getActionURLExpr(),this, pageContext)) != null)
        {
            setActionURL(string);
        }
        if ((string = EvalHelper.evalString("renderURL", getRenderURLExpr(),this, pageContext)) != null)
        {
            setRenderURL(string);
        }
    }
    
    public void release() 
    {
        super.release();
        actionURL = null;
        actionURLExpr = null;
    }
}
