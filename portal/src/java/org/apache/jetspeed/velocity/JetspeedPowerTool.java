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
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.capability.CapabilityMap;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
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
    protected static final String PORTLET_CONFIG_ATTR = "portletConfig";
    protected static final String RENDER_RESPONSE_ATTR = "renderResponse";
    protected static final String RENDER_REQUEST_ATTR = "renderRequest";
    public static final String DISPATCHER_ATTR = "dispatcher";
    private static final String COLUMNS_ATTR = "columns";
    public static final String PAGE_ATTR = "page";
    public static final String FRAGMENT_ATTR = "fragment";
    public static final String HIDDEN = "hidden";

    public static final String LAYOUT_TEMPLATE_TYPE = "layout";
    public static final String DECORATOR_TEMPLATE_TYPE = "decorator";
    public static final String GENERIC_TEMPLATE_TYPE = "generic";

    private RenderRequest renderRequest;

    private RenderResponse renderResponse;

    private PortletConfig portletConfig;

    private ViewContext viewCtx;

    private Writer templateWriter;

    private Stack fragmentStack;

    private static final Log log = LogFactory.getLog(JetspeedPowerTool.class);

    private CapabilityMap capabilityMap;
    private Locale locale;
    private LocatorDescriptor locatorDescriptor;
    private TemplateLocator locator;
    private PortletEntityAccessComponent entityAccess;
    /**
     * Empty constructor DO NOT USE!!!!  This is only here to allow creation of the
     * via the Velocity Tool Box.  For proper use out side the tool box use @see #JetspeedPowerTool(javax.portlet.RenderRequest, javax.portlet.RenderResponse, javax.portlet.PortletConfig)
     */
    public JetspeedPowerTool()
    {
        super();

    }

    /**
      * This is here to make this tool easily useable in 
      * within standard java classes.
     * @param request
     * @param resp
     * @param config
     */
    public JetspeedPowerTool(RenderRequest request, RenderResponse resp, PortletConfig config)
    {
        this();
        renderRequest = request;
        renderResponse = resp;
        portletConfig = config;
        try
        {
            // I am not sure that this will produce the required result.
            templateWriter = renderResponse.getWriter();
        }
        catch (IOException e)
        {
            log.error("Unable to retreive Writer from the RenderResponse: " + e.toString(), e);
        }
        fragmentStack = new Stack();
		clientSetup(Jetspeed.getCurrentRequestContext());
    }

    /**
     * Use this constructor when using the JetspeedPowerTool within JSP
     * pages or custom tags.
     * @param jspContext
     */
    public JetspeedPowerTool(PageContext jspContext)
    {
        this();
        renderRequest = (RenderRequest) jspContext.getAttribute(RENDER_REQUEST_ATTR);
        renderResponse = (RenderResponse) jspContext.getAttribute(RENDER_RESPONSE_ATTR);
        portletConfig = (PortletConfig) jspContext.getAttribute(PORTLET_CONFIG_ATTR);
        templateWriter = jspContext.getOut();
        fragmentStack = new Stack();
		clientSetup(Jetspeed.getCurrentRequestContext());
		entityAccess = (PortletEntityAccessComponent) Jetspeed.getComponentManager().getComponent(PortletEntityAccessComponent.class);
    }

    /**
     * @see org.apache.velocity.tools.view.tools.ViewTool#init(java.lang.Object)
     */
    public void init(Object obj)
    {
        Context ctx = null;
        entityAccess = (PortletEntityAccessComponent) Jetspeed.getComponentManager().getComponent(PortletEntityAccessComponent.class);
        if (obj instanceof ViewContext)
        {
            ViewContext viewContext = (ViewContext) obj;
            this.viewCtx = viewContext;
            ctx = viewContext.getVelocityContext();
            setRequest((RenderRequest) ctx.get(RENDER_REQUEST_ATTR));
           
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
        fragmentStack = new Stack();
        clientSetup(Jetspeed.getCurrentRequestContext());

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
        String fragmentId = getCurrentFragment().getId();
        JetspeedObjectID peid = JetspeedObjectID.createFromString(fragmentId);
        PortletEntity portletEntity = entityAccess.getPortletEntity(peid);
        return portletEntity;
    }

    /**
     * 
     * @param f Fragment whose <code>PortletEntity</code> we want to retreive.
     * @return The PortletEntity represented by the current fragment.
     */
    public PortletEntity getPortletEntity(Fragment f)
    {
        PortletEntity portletEntity = entityAccess.getPortletEntity(JetspeedObjectID.createFromString(f.getId()));
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
     * Retreives a template using Jetspeed's @see org.apache.jetspeed.locator.TemplateLocator
     * 
     * 
     * @param path Expected to the template.  This may actually be changed by the TL service
     * based the capability and localization information provided by the client.
     * @param templateType Type off template we are interested in.
     * @return Template object containng the pertinent information required to inlcude the
     * request template path in the current response
     * @throws TemplateLocatorException if the <code>path</code> does not exist.
     */
    public TemplateDescriptor getTemplate(String path, String templateType) throws TemplateLocatorException
    {
        checkState();
        if (templateType == null)
        {
            templateType = GENERIC_TEMPLATE_TYPE;
        }
        try
        {
        	
			locatorDescriptor.setName(path);
			locatorDescriptor.setType(templateType);
			
            TemplateDescriptor template = locator.locateTemplate(locatorDescriptor);
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
        flush();
        getContentDispatcher().include(f, renderRequest, renderResponse);

    }
    
    public void  includeTemplate(String template, String templateType) throws IOException
    {
    	checkState();    	
    	try
        {
			flush();
            PortletRequestDispatcher pDispatcher = portletConfig.getPortletContext().getRequestDispatcher(getTemplate(template, templateType).getAppRelativePath());
            pDispatcher.include(renderRequest, renderResponse);
        }
        catch (Exception e)
        {            
            PrintWriter directError = new PrintWriter(renderResponse.getWriter());
			directError.write("Error occured process includeTemplate(): "+e.toString()+"\n\n");
            e.printStackTrace(directError);
            directError.close();            
        }    	
    }
    


    /**
     * 
     */
    public void flush() throws IOException
    {
        checkState();
        if (templateWriter != null)
        {
            templateWriter.flush();
        }
        else if(viewCtx != null)
        {
			templateWriter = (VelocityWriter) viewCtx.getVelocityContext().get(JetspeedVelocityViewServlet.VELOCITY_WRITER_ATTR);
			templateWriter.flush();
        }
    }

    /**
     * <p>
     * This does not actaully "include()" as per the ContentDispatcher, instead,
     * it locates the decorator for this Fragment or, if none has been defined the 
     * default decorator for this Fragment type from the parent Page.
     * </p>
     * <p>
     * The decorator template itself is responsible for inlcluding the content
     * of the target Fragment which is easily acheived like so:
     * <br />
     * in Velocity:
     * <pre>
     *   <code>
     *     $jetspeed.include($jetspeed.currentFragment)
     *  </code>
     * </pre>
     * In JSP:
     *   <pre>
     *   <code>
     *    <% 
     *     JetspeedPowerTool jetspeed = new JetspeedPowerTool(renderRequest, renderResponse, portletConfig);
     *     jetspeed.include(jetspeed.getCurrentFragment());
     *    %>
     *  </code>
     * </pre>
     * 
     * 
     * @param f Fragment to "decorate"
     * @throws IOException
     * @throws TemplateLocatorException
     */
    public void decorateAndInclude(Fragment f) throws TemplateLocatorException, PortletException, IOException
    {
        // makes sure that any previous content has been written to
        // preserve natural HTML rendering order
        flush();
        String decorator = f.getDecorator();
        // Fallback to the default decorator if the current fragment is not
        // specifically decorated
        if (decorator == null)
        {
            decorator = getPage().getDefaultDecorator(f.getType());
        }

        TemplateDescriptor propsTemp = getTemplate(decorator + "/" + DECORATOR_TEMPLATE_TYPE + ".properties", DECORATOR_TEMPLATE_TYPE);

        Configuration decoConf = new PropertiesConfiguration(propsTemp.getAbsolutePath());
        String ext = decoConf.getString("template.extension");

        // Set this fragment as the current fragment, making sure
        // the last currentFragment goes on to the fragmentStack
        if (getCurrentFragment() != null)
        {
            fragmentStack.push(getCurrentFragment());
        }
        setCurrentFragment(f);

        String decoratorPath = decorator + "/" + DECORATOR_TEMPLATE_TYPE + ext;
        TemplateDescriptor template = getTemplate(decoratorPath, DECORATOR_TEMPLATE_TYPE);
        PortletRequestDispatcher prd = portletConfig.getPortletContext().getRequestDispatcher(template.getAppRelativePath());
        prd.include(renderRequest, renderResponse);

        // Now that were are done with this fragment reset to the last "current" fragment
        Fragment lastFragment = (Fragment) fragmentStack.pop();
        if (lastFragment != null)
        {
            setCurrentFragment(lastFragment);
        }
    }

    /**
     * 
     * 
     * @throws java.lang.IllegalStateException if the <code>PortletConfig</code>, <code>RenderRequest</code> or
     * <code>RenderReponse</code> is null.
     */
    protected void checkState()
    {
        if (portletConfig == null || renderRequest == null || renderResponse == null)
        {
            throw new IllegalStateException(
                "JetspeedPowerTool has not been properly initialized.  "
                    + ""
                    + "The JetspeedPowerTool generally only usuable during the rendering phase of  "
                    + "internal portlet applications.");
        }
    }

    protected void clientSetup(RequestContext requestContext) 
    {        
        ComponentManager cm = Jetspeed.getComponentManager();
        locator = (TemplateLocator) cm.getComponent("TemplateLocator");
        // By using null, we create a re-useable locator    
        try
        {
            locatorDescriptor = locator.createLocatorDescriptor(null);        
            capabilityMap = requestContext.getCapabilityMap();
            locatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
            locale = requestContext.getLocale();
            locatorDescriptor.setCountry(locale.getCountry());
            locatorDescriptor.setLanguage(locale.getLanguage());       
        }
        catch (Exception e)
        {
           log.error("Unable to perform client setup: "+e.toString(), e);
        }
    }

}
