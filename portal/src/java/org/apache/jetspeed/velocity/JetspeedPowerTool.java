/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.velocity;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.cps.template.Template;
import org.apache.jetspeed.cps.template.TemplateLocator;
import org.apache.jetspeed.cps.template.TemplateLocatorException;
import org.apache.jetspeed.cps.template.TemplateLocatorService;
import org.apache.jetspeed.entity.PortletEntityAccess;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;

import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.Constants;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * <p>
 * JetspeedPowerTool
 * </p>
 * <p>
 *   The JetspeedPowerTool is meant to be used by template designers to build
 *   templates for internal Jetspeed portlet applications.  It hides the implementation 
 *   details of the more common template actions so that future changes to said implementation
 *   have minimal effect on template.
 * </p>
 * <p>
 *   Where applicable, methods have been marked with a <strong>BEST PRATICES</strong>
 *   meaning that this method should be used instead the synonymous code listed within the
 *   method docuementation.
 * </p>
 * <p>
 * <pre>
 *  Toolbox configuration for Velocity tool box:
 * &lt;tool&gt;
 *   &lt;key&gt;jetspeed&lt;/key&gt;
 *
 *  &lt;scope&gt;request&lt;/scope&gt;
 *  &lt;class&gt;org.apache.jetspeed.velocity.JetspeedPowerTool&lt;/class&gt;
 * &lt;/tool&gt;
 </pre></p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedPowerTool implements ViewTool
{
    public static final String DISPATCHER_ATTR = "dispatcher";
    private static final String COLUMNS_ATTR = "columns";
    public static final String PAGE_ATTR = "page";
    public static final String FRAGMENT_ATTR = "fragment";
    public static final String HIDDEN = "hidden";

    private RenderRequest renderRequest;

    private RenderResponse renderResponse;

    private PortletConfig portletConfig;

    private ViewContext viewCtx;

    private static final Log log = LogFactory.getLog(JetspeedPowerTool.class);

    /**
     * Empty constructor DO NOT USE!!!!  This is only here to allow creation of the
     * via the Velocity Tool Box.  For proper use out side the tool box use @see #JetspeedPowerTool(javax.portlet.RenderRequest, javax.portlet.RenderResponse, javax.portlet.PortletConfig)
     */
    public JetspeedPowerTool()
    {
        super();

    }

    /**
     * This is here to make this tool easily useable in any
     * templating environment including JSPs
     * @param request Current PortletRequest
     */
    public JetspeedPowerTool(RenderRequest request, RenderResponse resp, PortletConfig config)
    {
        this();
        renderRequest = request;
        renderResponse = resp;
        portletConfig = config;
    }

    /**
     * @see org.apache.velocity.tools.view.tools.ViewTool#init(java.lang.Object)
     */
    public void init(Object obj)
    {
        Context ctx = null;
        if (obj instanceof ViewContext)
        {
            ViewContext viewContext = (ViewContext) obj;
            this.viewCtx = viewContext;
            ctx = viewContext.getVelocityContext();
            setRequest((RenderRequest) ctx.get("renderRequest"));
        }
        else if (obj instanceof PortletRequest)
        {
			RenderRequest request = (RenderRequest) obj;
            ctx = (Context) request.getAttribute(JetspeedVelocityViewServlet.VELOCITY_CONTEXT_ATTR);
            setRequest(request);
        }
        else
        {
            throw new IllegalArgumentException("Was expecting " + ViewContext.class +" or " + PortletRequest.class);
        }

        renderRequest = (RenderRequest) ctx.get(Constants.PORTLET_REQUEST);
        renderResponse = (RenderResponse) ctx.get(Constants.PORTLET_RESPONSE);
        portletConfig = (PortletConfig) ctx.get(Constants.PORTLET_CONFIG);

    }

    /**
     * @param request
     */
    private void setRequest(RenderRequest request)
    {
        this.renderRequest = request;
    }
	
	/**
	 * 
	 * @return
	 */
    public Fragment getCurrentFragment()
    {
		checkState();
        return (Fragment) renderRequest.getAttribute(FRAGMENT_ATTR);
    }
	
	/**
	 * 
	 * @param f
	 */
    public void setCurrentFragment(Fragment f)
    {
		checkState();
        renderRequest.setAttribute(FRAGMENT_ATTR, f);
    }
	
	/**
	 * 
	 * @return
	 */
    public Page getPage()
    {
		checkState();
        return (Page) renderRequest.getAttribute(PAGE_ATTR);
    }
	
	/**
	 * 
	 * @return
	 */
    public List[] getColumns()
    {
		checkState();
        return (List[]) renderRequest.getAttribute(COLUMNS_ATTR);
    }

    /**
     * 
     * @return
     */
    public PortletEntity getCurrentPortletEntity()
    {
        PortletEntity portletEntity =
            PortletEntityAccess.getEntity(JetspeedObjectID.createFromString(getCurrentFragment().getId()));
        return portletEntity;
    }

    /**
     * 
     * @param f Fragment whose <code>PortletEntity</code> we want to retreive.
     * @return The PortletEntity represented by the current fragment.
     */
    public PortletEntity getPortletEntity(Fragment f)
    {
        PortletEntity portletEntity = PortletEntityAccess.getEntity(JetspeedObjectID.createFromString(f.getId()));
        return portletEntity;
    }
	
	/**
	 * This method is synonymous with the following code:
	 * <p>
	 *   <code>
	 *    ContentDispatcher dispatcher = (ContentDispatcher) renderRequest.getAttribute("dispatcher");<br />   
	 * </code>
	 * </p>
	 * @see org.apache.jetspeed.aggregator.ContentDispatcher
	 * <strong>BEST PRACTICE:</strong> Use this method in templates instead of 
	 * directly using the equivalent code defined above.
	 * @return ContentDispatcher for the RenderRequest
	 */
    public ContentDispatcher getContentDispatcher()
    {
		checkState();
        return (ContentDispatcher) renderRequest.getAttribute(DISPATCHER_ATTR);
    }
	
	
	/**
	 * Checks the the visibilty of this fragment with respect to the current RenderReqeust.
	 * @param f Fragment
	 * @return whether or not the Fragment in question should be considered visible during rendering.
	 */
    public boolean isHidden(Fragment f)
    {
		checkState();
        if (f == null)
        {
            throw new IllegalArgumentException("Fragment cannot be null for isHidden(Fragment)");
        }
        return f.getState() != null && f.getState().equals(HIDDEN);
    }
	
	
	/**
	 * Retreives a template using Jetspeed's @see org.apache.jetspeed.cps.template.TemplateLocatorService
	 * 
	 * 
	 * @param path Expected to the template.  This may actually be changed by the TL service
	 * based the capability and localization information provided by the client.
	 * @return Template object containng the pertinent information required to inlcude the
	 * request template path in the current response
	 * @throws TemplateLocatorException if the <code>path</code> does not exist.
	 */
    public Template getTemplate(String path) throws TemplateLocatorException
    {
		checkState();
        try
        {
            TemplateLocatorService tls =
                (TemplateLocatorService) CommonPortletServices.getPortalService(TemplateLocatorService.SERVICE_NAME);
            TemplateLocator locator = tls.createLocator("layout");
            locator.setName(path);
            Template template = tls.locateTemplate(locator);
            return template;
        }
        catch (TemplateLocatorException e)
        {
            log.error("Unable to locate template: " + path, e);
            System.out.println("Unable to locate template: " + path);
            throw e;
        }

    }
	
	/**
	 * Includes a portal Fragment into the current <code>RenderResponse.</code>  
	 * This is the same as calling:
	 * <p>
	 *   <code>
	 *    ContentDispatcher dispatcher = (ContentDispatcher) renderRequest.getAttribute("dispatcher");<br />
	 *    dispatcher.include(fragment,  renderRequest, renderResponse);<br />
	 * </code>
	 * </p>
	 * <strong>BEST PRACTICE:</strong> Use this method in templates instead of 
	 * directly using the equivalent code defined above.
	 * @param f Fragment to include.
	 * @throws IOException
	 */
    public void include(Fragment f) throws IOException
    {
		checkState();
        // We need to flush so that content gets render in the correct place
        if(viewCtx != null)
        {
        	VelocityWriter vw = (VelocityWriter) viewCtx.getVelocityContext().get(JetspeedVelocityViewServlet.VELOCITY_WRITER_ATTR);
        	vw.flush();
        }
        getContentDispatcher().include(f,  renderRequest, renderResponse);
    }

    //	public String include(String template) throws  IOException
    //	{	
    //		
    //		// RequestDispatcher dispatcher = viewCtx.getRequest().getRequestDispatcher(template);
    //		StringWriter buf = new StringWriter();
    //		try
    //        {
    //            Velocity.mergeTemplate(template, "UTF-8", viewCtx.getVelocityContext(), buf);
    //        }
    //        catch (Exception e)
    //        {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }

    //	}
	
	/**
	 * 
	 */
    public void flush() throws IOException
    {
    	checkState();
        // ((RenderResponse)renderResponse ).flushBuffer(); 
        viewCtx.getResponse().getWriter().flush();
    }
    
    /**
     * 
     * 
     * @throws java.lang.IllegalStateException if the <code>PortletConfig</code>, <code>RenderRequest</code> or
     * <code>RenderReponse</code> is null.
     */
    private void checkState()
    {
    	if(portletConfig == null || renderRequest == null || renderResponse == null)
    	{
    		throw new IllegalStateException("JetspeedPowerTool has not been properly initialized.  "+  "" +
    			                                              "The JetspeedPowerTool generally only usuable during the rendering phase of  "+
    			                                              "internal portlet applications.");
    	}
    }

}
