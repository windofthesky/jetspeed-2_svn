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
package org.apache.jetspeed.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotGeneratedException;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.jetspeed.services.information.PortletURLProviderImpl;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.pluto.Constants;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.velocity.context.Context;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;


/**
 * <p>
 * JetspeedPowerTool
 * </p>
 * <p>
 * The JetspeedPowerTool is meant to be used by template designers to build
 * templates for internal Jetspeed portlet applications. It hides the
 * implementation details of the more common template actions so that future
 * changes to said implementation have minimal effect on template.
 * </p>
 * <p>
 * Where applicable, methods have been marked with a <strong>BEST PRATICES
 * </strong> meaning that this method should be used instead the synonymous code
 * listed within the method docuementation.
 * </p>
 * <p>
 * 
 * <pre>
 * 
 *  
 *   
 *    
 *     
 *      
 *       
 *        
 *          Toolbox configuration for Velocity tool box:
 *         &lt;tool&gt;
 *           &lt;key&gt;jetspeed&lt;/key&gt;
 *        
 *          &lt;scope&gt;request&lt;/scope&gt;
 *          &lt;class&gt;org.apache.jetspeed.velocity.JetspeedPowerTool&lt;/class&gt;
 *         &lt;/tool&gt;
 *         
 *        
 *       
 *      
 *     
 *    
 *   
 *  
 * </pre>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class JetspeedPowerTool implements ViewTool
{

    public static final String FRAGMENT_PROCESSING_ERROR_PREFIX = "fragment.processing.error.";
    
    protected static final String PORTLET_CONFIG_ATTR = "portletConfig";
    protected static final String RENDER_RESPONSE_ATTR = "renderResponse";
    protected static final String RENDER_REQUEST_ATTR = "renderRequest";
    public static final String DISPATCHER_ATTR = "dispatcher";
    private static final String COLUMNS_ATTR = "columns";
  
    public static final String FRAGMENT_ATTR = "fragment";
    public static final String LAYOUT_ATTR = "layout";
    
    public static final String HIDDEN = "hidden";

    public static final String LAYOUT_TEMPLATE_TYPE = "layout";
    public static final String DECORATOR_TYPE = "decorator";    
    public static final String GENERIC_TEMPLATE_TYPE = "generic";

    private RenderRequest renderRequest;

    private RenderResponse renderResponse;

    private PortletConfig portletConfig;

    private ViewContext viewCtx;

    private Writer templateWriter;

    private Stack fragmentStack;
    
    private static final String POWER_TOOL_SESSION_ACTIONS = "org.apache.jetspeed.powertool.actions";

    private static final Log log = LogFactory.getLog(JetspeedPowerTool.class);

    private CapabilityMap capabilityMap;
    private Locale locale;
    private LocatorDescriptor templateLocatorDescriptor;
    private TemplateLocator templateLocator;
    private PortletEntityAccessComponent entityAccess;
    private TemplateLocator decorationLocator;
    private LocatorDescriptor decorationLocatorDescriptor;
    private ComponentManager cm;
    private PortletWindowAccessor windowAccess;
    /**
     * Empty constructor DO NOT USE!!!! This is only here to allow creation of
     * the via the Velocity Tool Box. For proper use out side the tool box use
     * 
     * @see #JetspeedPowerTool(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse, javax.portlet.PortletConfig)
     */
    public JetspeedPowerTool()
    {
        super();
        cm = Jetspeed.getComponentManager();
        windowAccess = (PortletWindowAccessor) cm.getComponent(PortletWindowAccessor.class);

    }

    /**
     * This is here to make this tool easily useable in within standard java
     * classes.
     * 
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
     * Use this constructor when using the JetspeedPowerTool within JSP pages or
     * custom tags.
     * 
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
     * Gets the window state for the current portlet window (fragment)
     * 
     * @return The window state for the current window
     * @throws Exception
     */
    public WindowState getWindowState() throws Exception
    {
        try
        {
            RequestContext context = Jetspeed.getCurrentRequestContext();
            NavigationalState nav = context.getPortalURL().getNavigationalState();
            return nav.getState(windowAccess.getPortletWindow(getCurrentFragment()));
        }
        catch (Exception e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            return null;
        }
    }
    
    /**
     * Gets the portlet mode for a current portlet window (fragment)
     * 
     * @return The portlet mode of the current window
     * @throws Exception
     */
    public PortletMode getPortletMode() throws Exception
    {
        RequestContext context = Jetspeed.getCurrentRequestContext();
        NavigationalState nav = context.getPortalURL().getNavigationalState();
        try
        {
            return nav.getMode(windowAccess.getPortletWindow(getCurrentFragment()));
        }
        catch (FailedToRetrievePortletWindow e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            return null;
        }        
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
    
    public void setCurrentLayout()
    {
        checkState();
        RequestContext context = Jetspeed.getCurrentRequestContext();
        Fragment f = (Fragment)context.getRequest().getAttribute(LAYOUT_ATTR);
        renderRequest.setAttribute(LAYOUT_ATTR, f);        
    }
    
    /**
     * 
     * @return
     */
    public Fragment getCurrentLayout()
    {
        checkState();
        return (Fragment) renderRequest.getAttribute(LAYOUT_ATTR);
    }
    
    /**
     * 
     * @return
     */
    public Page getPage()
    {
        checkState();
        return (Page) renderRequest.getAttribute(PortalReservedParameters.PAGE_ATTRIBUTE_KEY);
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
     * @return @throws
     *         Exception
     */
    public PortletEntity getCurrentPortletEntity() throws Exception
    {
        try
        {
            return windowAccess.getPortletWindow(getCurrentFragment()).getPortletEntity();
        }
        catch (Exception e)
        {            
            handleError(e, "JetspeedPowerTool failed to retreive the current PortletEntity.  "+e.toString(), getCurrentFragment() );
            return null;
        }        
    }

    /**
     * 
     * @param f
     *            Fragment whose <code>PortletEntity</code> we want to
     *            retreive.
     * @return The PortletEntity represented by the current fragment.
     * @throws Exception
     */
    public PortletEntity getPortletEntity(Fragment f) throws Exception 
    {
        PortletEntity portletEntity = entityAccess.getPortletEntityForFragment(f);
        if(portletEntity == null)
        {
            try
            {
                portletEntity = entityAccess.generateEntityFromFragment(f);
                entityAccess.storePortletEntity(portletEntity);
            }
            catch (PortletEntityNotGeneratedException e)
            {
                String msg = "JetspeedPowerTool failed to retreive a PortletEntity for Fragment "+f.getId()+".  "+e.toString();
                handleError(e, msg, f);               
            }
            catch (PortletEntityNotStoredException e)
            {
                String msg = "JetspeedPowerTool failed to store a PortletEntity for Fragment "+f.getId()+".  "+e.toString();
                handleError(e, msg, f);  
            }
        }
        return portletEntity;
    }



    /**
     * This method is synonymous with the following code:
     * <p>
     * <code>
     *    ContentDispatcher dispatcher = (ContentDispatcher) renderRequest.getAttribute("dispatcher");<br />   
     * </code>
     * </p>
     * 
     * @see org.apache.jetspeed.aggregator.ContentDispatcher <strong>BEST
     *      PRACTICE: </strong> Use this method in templates instead of directly
     *      using the equivalent code defined above.
     * @return ContentDispatcher for the RenderRequest
     */
    public ContentDispatcher getContentDispatcher()
    {
        checkState();
        return (ContentDispatcher) renderRequest.getAttribute(DISPATCHER_ATTR);
    }

    /**
     * Checks the the visibilty of this fragment with respect to the current
     * RenderReqeust.
     * 
     * @param f
     *            Fragment
     * @return whether or not the Fragment in question should be considered
     *         visible during rendering.
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
     * Retreives a template using Jetspeed's
     * 
     * @see org.apache.jetspeed.locator.TemplateLocator
     * 
     * 
     * @param path
     *            Expected to the template. This may actually be changed by the
     *            TL service based the capability and localization information
     *            provided by the client.
     * @param templateType
     *            Type off template we are interested in.
     * @return Template object containng the pertinent information required to
     *         inlcude the request template path in the current response
     * @throws TemplateLocatorException
     *             if the <code>path</code> does not exist.
     */
    public TemplateDescriptor getTemplate(String path, String templateType) throws TemplateLocatorException
    {
        checkState();
        return getTemplate(path, templateType, templateLocator, templateLocatorDescriptor);
    }
    
    public Configuration getTypeConfiguration(String type, String name, String location) throws Exception
    {
        ArgUtil.assertNotNull(String.class, type, this, "getTypeConfiguration(String type, String name)");
        ArgUtil.assertNotNull(String.class, name, this, "getTypeConfiguration(String type, String name)");
        try
        {
            TemplateDescriptor locator = null;
            if(location.equals("templates"))
            {
                locator = getTemplate(name+"/"+type+".properties", type);
            }
            else if(location.equals("decorations"))
            {
                locator = getDecoration(name+"/decorator.properties", type);
            }
            else
            {
                throw new IllegalArgumentException("Location type "+location+" is not supported by getTypeConfiguration().");
            }
            return new PropertiesConfiguration(locator.getAbsolutePath());
        }
        catch (TemplateLocatorException e)
        {
            log.warn(e.toString(), e);
            return null;
        }
    }
    
    public TemplateDescriptor getDecoration(String path, String templateType) throws TemplateLocatorException
    {
        checkState();
        return getTemplate(path, templateType, decorationLocator, decorationLocatorDescriptor);
    }

    /**
     * Includes a portal Fragment into the current <code>RenderResponse.</code>
     * This is the same as calling:
     * <p>
     * <code>
     *    ContentDispatcher dispatcher = (ContentDispatcher) renderRequest.getAttribute("dispatcher");<br />
     *    dispatcher.include(fragment,  renderRequest, renderResponse);<br />
     * </code>
     * </p>
     * <strong>BEST PRACTICE: </strong> Use this method in templates instead of
     * directly using the equivalent code defined above.
     * 
     * @param f
     *            Fragment to include.
     * @throws IOException
     */
    public void include(Fragment f) throws IOException
    {
        checkState();

        // We need to flush so that content gets render in the correct place
        flush();
        try
        {
            getContentDispatcher().include(f, renderRequest, renderResponse);
        }
        catch (FailedToRenderFragmentException e)
        {
            handleError(e, e.getMessage(), f);
        }
        
        Set exceptions = (Set) renderRequest.getAttribute(FRAGMENT_PROCESSING_ERROR_PREFIX+f.getId());
    	if(exceptions != null)
    	{
    	    Iterator itr = exceptions.iterator();
    	    while(itr.hasNext())
    	    {
    	        Exception e = (Exception) itr.next();
    	        PrintWriter writer = renderResponse.getWriter();
                writer.write("<strong>"+e.toString()+"<br/></strong>");
    	        writer.print("<textarea cols=\"100\" rows=\"15\">");
    	        e.printStackTrace(writer);
    	        writer.print("</textarea><br/>");
    	    }
    	}

    }
    
    public void  includeTemplate(String template, String templateType) throws IOException
    {
    	checkState();    	
    	try
        {
			flush();
            TemplateDescriptor useLocator = getTemplate(template, templateType);
            PortletRequestDispatcher pDispatcher = portletConfig.getPortletContext().getRequestDispatcher(useLocator.getAppRelativePath());
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
    
    public void  includeDecoration(String template, String templateType) throws IOException
    {
    	checkState();    	
    	try
        {
			flush();
            PortletRequestDispatcher pDispatcher = portletConfig.getPortletContext().getRequestDispatcher(getDecoration(template, templateType).getAppRelativePath());
            pDispatcher.include(renderRequest, renderResponse);
        }
        catch (Exception e)
        {            
            PrintWriter directError = new PrintWriter(renderResponse.getWriter());
			directError.write("Error occured process includeDecoration(): "+e.toString()+"\n\n");
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
     * Decorate and include fragment content.
     * </p>
     * 
     * @param f Fragment to include and decorate
     * @throws Exception
     */
    public void decorateAndInclude(Fragment f) throws Exception
    {
        // makes sure that any previous content has been written to
        // preserve natural HTML rendering order
        flush();

        // Set current fragment and layout, making sure
        // the last currentFragment goes on to the fragmentStack
        if (getCurrentFragment() != null)
        {
            fragmentStack.push(getCurrentFragment());
        }
        setCurrentFragment(f);
        setCurrentLayout();

        // include decorated layout or portlet fragment
        try
        {
            String fragmentType = f.getType();
            if (fragmentType.equals(Fragment.PORTLET))
            {
                decorateAndIncludePortlet(f);
            }
            else if (fragmentType.equals(Fragment.LAYOUT))
            {
                decorateAndIncludeLayout(f);
            }
        }
        catch (Exception e)
        {
            // include stack trace on exception
            renderResponse.getWriter().write(e.toString());          
        }
        finally
        {
            // Now that were are done with this fragment reset to the last
            // "current" fragment
            Fragment lastFragment = (Fragment) fragmentStack.pop();
            if (lastFragment != null)
            {
                setCurrentFragment(lastFragment);
            }
        }
    }

    /**
     * <p>
     * Invoke nested layout portlet by including per the ContentDispatcher.
     * </p>
     * <p>
     * 
     * @param f Layout fragment to include
     * @throws Exception
     */
    private void decorateAndIncludeLayout(Fragment f) throws Exception
    {
        // include current layout fragment which includes layout template 
        include(f);
    }

    /**
     * <p>
     * This does not actaully "include()" as per the ContentDispatcher, instead,
     * it locates the decorator for this Fragment or, if none has been defined
     * the default decorator for this Fragment type from the parent Page.
     * </p>
     * <p>
     * The decorator template itself is responsible for including the content
     * of the target Fragment which is easily acheived like so: <br />
     * in Velocity:
     * 
     * <pre>
     *   <code>
     * $jetspeed.include($jetspeed.currentFragment)
     * </code>
     * </pre>
     * 
     * In JSP:
     * 
     * <pre>
     *   <code>
     *            &lt;% 
     *             JetspeedPowerTool jetspeed = new JetspeedPowerTool(renderRequest, renderResponse, portletConfig);
     *             jetspeed.include(jetspeed.getCurrentFragment());
     *            %&gt;
     * </code>
     * </pre>
     * 
     * 
     * @param f Portlet fragment to "decorate"
     * @throws Exception
     */
    private void decorateAndIncludePortlet(Fragment f) throws Exception
    {
        // make sure that any previous content has been written to
        // preserve natural HTML rendering order
        flush();

        // get fragment decorator; fallback to the default decorator
        // if the current fragment is not specifically decorated
        String fragmentType = f.getType();
        String decorator = f.getDecorator();
        if (decorator == null)
        {
            decorator = getPage().getDefaultDecorator(fragmentType);
        }

        // get fragment properties for fragmentType or generic
        TemplateDescriptor propsTemp = getTemplate(decorator + "/" + DECORATOR_TYPE + ".properties", fragmentType, decorationLocator, decorationLocatorDescriptor);
        if(propsTemp == null)
        {
            fragmentType = GENERIC_TEMPLATE_TYPE;
            propsTemp = getTemplate(decorator + "/" + DECORATOR_TYPE + ".properties", fragmentType, decorationLocator, decorationLocatorDescriptor);
        }

        // get decorator template
        Configuration decoConf = new PropertiesConfiguration(propsTemp.getAbsolutePath());
        String ext = decoConf.getString("template.extension");
        String decoratorPath = decorator + "/" + DECORATOR_TYPE + ext;
        TemplateDescriptor template = null;
        try
        {
            template = getDecoration(decoratorPath, fragmentType);
        }
        catch (TemplateLocatorException e)
        {
            String parent = decoConf.getString("extends");
            if(parent != null)
            {
                template = getDecoration(parent + "/" + DECORATOR_TYPE + ext, fragmentType);
            }
        }

        // include decorator which includes current portlet fragment
        PortletRequestDispatcher prd = portletConfig.getPortletContext().getRequestDispatcher(template.getAppRelativePath());
        prd.include(renderRequest, renderResponse);
    }

    /**
     * 
     * 
     * @throws java.lang.IllegalStateException
     *             if the <code>PortletConfig</code>,
     *             <code>RenderRequest</code> or <code>RenderReponse</code>
     *             is null.
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
        templateLocator = (TemplateLocator) cm.getComponent("TemplateLocator");
        decorationLocator = (TemplateLocator) cm.getComponent("DecorationLocator");
        // By using null, we create a re-useable locator
        try
        {
            capabilityMap = requestContext.getCapabilityMap();
            locale = requestContext.getLocale();
            
            templateLocatorDescriptor = templateLocator.createLocatorDescriptor(null);        
            templateLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
            templateLocatorDescriptor.setCountry(locale.getCountry());
            templateLocatorDescriptor.setLanguage(locale.getLanguage());
            
            decorationLocatorDescriptor = decorationLocator.createLocatorDescriptor(null);        
            decorationLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
            decorationLocatorDescriptor.setCountry(locale.getCountry());
            decorationLocatorDescriptor.setLanguage(locale.getLanguage());            
            
        }
        catch (Exception e)
        {
           log.error("Unable to perform client setup: "+e.toString(), e);
        }
    }
    
    protected TemplateDescriptor getTemplate(String path, String templateType, TemplateLocator locator, LocatorDescriptor descriptor) throws TemplateLocatorException
    {
        checkState();
        if (templateType == null)
        {
            templateType = GENERIC_TEMPLATE_TYPE;
        }
        try
        {
        	
            descriptor.setName(path);
            descriptor.setType(templateType);
			
            TemplateDescriptor template = locator.locateTemplate(descriptor);
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
     * <p>
     * handleError
     * </p>
     * 
     * @param e
     * @param msg
     */
    protected void handleError( Exception e, String msg, Fragment fragment ) 
    {
        log.error(msg, e);
        
        Set exceptions = (Set) renderRequest.getAttribute(FRAGMENT_PROCESSING_ERROR_PREFIX+fragment.getId());
        if(exceptions == null)
        {
            exceptions = new HashSet();
            renderRequest.setAttribute(FRAGMENT_PROCESSING_ERROR_PREFIX+fragment.getId(), exceptions);
        }
        exceptions.add(e);
        
       
    }
         
    /**
     * Gets the list of decorator actions for a window.
     * Each window (on each page) has its own collection of actions associated with it.
     * The creation of the decorator action list per window will only be called once per session.
     * This optimization is to avoid the expensive operation of security checks and action object creation and logic
     * on a per request basis. 
     * 
     * @return A list of actions available to the current window, filtered by securty access and current state.
     * @throws Exception
     */
    public List getDecoratorActions() 
    {
        try
        {
            RequestContext context = Jetspeed.getCurrentRequestContext();
            String key = getPage().getId() + ":" + this.getCurrentFragment().getId();
            Map sessionActions = (Map)context.getSessionAttribute(POWER_TOOL_SESSION_ACTIONS);
            if (null == sessionActions)
            {
                sessionActions = new HashMap();
                context.setSessionAttribute(POWER_TOOL_SESSION_ACTIONS, sessionActions);
            }        
            PortletWindowActionState actionState = (PortletWindowActionState)sessionActions.get(key);
            
            String state = getWindowState().toString();        
            String mode = getPortletMode().toString();

            if (null == actionState)
            {
                actionState = new PortletWindowActionState(state, mode);   
                sessionActions.put(key, actionState);
            }
            else
            {
                // check to see if state or mode has changed
                if (actionState.getWindowState().equals(state))
                {
                    if (actionState.getPortletMode().equals(mode))
                    {
                        // nothing has changed
                        return actionState.getActions();
                    }                
                    else
                    {
                        actionState.setPortletMode(mode);                    
                    }
                }
                else
                {
                    actionState.setWindowState(state);
                }
                // something has changed, rebuild the list
            }
            
                            
            List actions = actionState.getActions();
            actions.clear();
    
            PortletDefinitionComposite portlet = 
                (PortletDefinitionComposite) getCurrentPortletEntity().getPortletDefinition();
            if (null == portlet)
            {
                return actions; // allow nothing
            }        
                    
            ContentTypeSet content = portlet.getContentTypeSet();
            
            if (state.equals(WindowState.NORMAL.toString()))
            {
                createAction(actions, JetspeedActions.INDEX_MINIMIZE, portlet);
                createAction(actions, JetspeedActions.INDEX_MAXIMIZE, portlet);
            }
            else if (state.equals(WindowState.MAXIMIZED.toString()))
            {
                createAction(actions, JetspeedActions.INDEX_MINIMIZE, portlet);
                createAction(actions, JetspeedActions.INDEX_NORMAL, portlet);            
            }
            else // minimized
            {
                createAction(actions, JetspeedActions.INDEX_MAXIMIZE, portlet);
                createAction(actions, JetspeedActions.INDEX_NORMAL, portlet);                        
            }
            
            if (mode.equals(PortletMode.VIEW.toString()))
            {
                if (content.supportsPortletMode(PortletMode.EDIT))
                {
                    createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
                }
                if (content.supportsPortletMode(PortletMode.HELP))
                {            
                    createAction(actions, JetspeedActions.INDEX_HELP, portlet);
                }
            }
            else if (mode.equals(PortletMode.EDIT.toString()))
            {
                createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
                if (content.supportsPortletMode(PortletMode.HELP))
                {                        
                    createAction(actions, JetspeedActions.INDEX_HELP, portlet);
                }
            }
            else // help
            {
                createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
                if (content.supportsPortletMode(PortletMode.EDIT))
                {            
                    createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
                }
            }
            return actions;
        }
        catch (Exception e)
        {
           log.warn("Unable to generate decortator actions: "+e.toString());
           return Collections.EMPTY_LIST;
        }
    }

    /**
     * Gets the list of decorator actions for a page.
     * Each layout fragment on a page has its own collection of actions associated with it.
     * The creation of the layout decorator action list per page will only be called once per session.
     * This optimization is to avoid the expensive operation of security checks and action object creation and logic
     * on a per request basis. 
     * 
     * @return A list of actions available to the current window, filtered by securty access and current state.
     * @throws Exception
     */
    public List getPageDecoratorActions() throws Exception
    {
        // check page access
        boolean readOnlyPageAccess = true;
        try
        {
            getPage().checkAccess(Page.EDIT_ACTION);
            readOnlyPageAccess = false;
        }
        catch (SecurityException se)
        {
        }
        
        // determine cached actions state key
        String key = "PAGE " + getPage().getId() + ":" + this.getCurrentFragment().getId() +
            ":" + (readOnlyPageAccess ? Page.VIEW_ACTION : Page.EDIT_ACTION );
        
        // get cached actions state 
        RequestContext context = Jetspeed.getCurrentRequestContext();
        Map sessionActions = (Map)context.getSessionAttribute(POWER_TOOL_SESSION_ACTIONS);
        if (null == sessionActions)
        {
            sessionActions = new HashMap();
            context.setSessionAttribute(POWER_TOOL_SESSION_ACTIONS, sessionActions);
        }        
        PortletWindowActionState actionState = (PortletWindowActionState)sessionActions.get(key);
        
        String state = getWindowState().toString();        
        String mode = getPortletMode().toString();

        if (null == actionState)
        {
            actionState = new PortletWindowActionState(state, mode);   
            sessionActions.put(key, actionState);
        }
        else
        {
            if (actionState.getPortletMode().equals(mode))
            {
                // nothing has changed
                return actionState.getActions();
            }                
            // something has changed, rebuild the list
            actionState.setPortletMode(mode);
        }
        
        List actions = actionState.getActions();
        actions.clear();
     
        // if there is no root fragment, return no actions
        PortletDefinitionComposite portlet = 
            (PortletDefinitionComposite) getCurrentPortletEntity().getPortletDefinition();
        if (null == portlet)
        {
            return actions;
        }        

        // if the page is being read only accessed, return no actions
        if (readOnlyPageAccess)
        {
            return actions;
        }

        // generate standard page actions depending on
        // portlet capabilities
        ContentTypeSet content = portlet.getContentTypeSet();
        if (mode.equals(PortletMode.VIEW.toString()))
        {
            if (content.supportsPortletMode(PortletMode.EDIT))
            {
                createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
            }
            if (content.supportsPortletMode(PortletMode.HELP))
            {            
                createAction(actions, JetspeedActions.INDEX_HELP, portlet);
            }
        }
        else if (mode.equals(PortletMode.EDIT.toString()))
        {
            createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
            if (content.supportsPortletMode(PortletMode.HELP))
            {                        
                createAction(actions, JetspeedActions.INDEX_HELP, portlet);
            }
        }
        else // help
        {
            createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
            if (content.supportsPortletMode(PortletMode.EDIT))
            {            
                createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
            }
        }
        return actions;
    }
    
    /**
     * Determines whether the access request indicated by the specified permission should be 
     * allowed or denied, based on the security policy currently in effect.
     *  
     * @param resource The fully qualified resource name of the portlet (PA::portletName)
     * @param action The action to perform on this resource (i.e. view, edit, help, max, min...)
     * @return true if the action is allowed, false if it is not
     */
    private boolean checkPermission(String resource, String action)
    {
        try
        {
            // TODO: it may be better to check the PagePermission for the outer most
            // fragment (i.e. the PSML page)
            AccessController.checkPermission(new PortletPermission(resource, action));            
        }
        catch (AccessControlException e)
        {
            return false;
        }        
        return true;         
    }
    
    /**
     * Creates a Decorator Action link to be added to the list of actions decorating a portlet.
     * 
     * @param actions
     * @param kind
     * @param resource
     * @return
     * @throws Exception
     */
    public DecoratorAction createAction(List actions, int actionId, PortletDefinitionComposite portlet)
        throws Exception
    {               
        String resource = portlet.getUniqueName();
        String actionName = JetspeedActions.ACTIONS[actionId];
        if (checkPermission(resource, actionName)) // TODO: should be !checkPermission
        {
            return null;
        }
        DecoratorAction action =  new DecoratorAction(actionName, 
                                                      actionName, 
                                    "content/images/" +  actionName + ".gif"); // TODO: HARD-CODED .gif
        
        PortletEntity entity = getCurrentPortletEntity();
        
        PortletURLProviderImpl url = 
            new PortletURLProviderImpl(Jetspeed.getCurrentRequestContext(), 
                                       windowAccess.getPortletWindow(getCurrentFragment()));
        switch (actionId)
        {
            case JetspeedActions.INDEX_MAXIMIZE:
                url.setWindowState(WindowState.MAXIMIZED);
                break;
            case JetspeedActions.INDEX_MINIMIZE:
                url.setWindowState(WindowState.MINIMIZED);
                break;
            case JetspeedActions.INDEX_NORMAL:
                url.setWindowState(WindowState.NORMAL);
                break;
            case JetspeedActions.INDEX_VIEW:
                url.setPortletMode(PortletMode.VIEW);
                break;
            case JetspeedActions.INDEX_EDIT:
                url.setPortletMode(PortletMode.EDIT);
                break;
            case JetspeedActions.INDEX_HELP:
                url.setPortletMode(PortletMode.HELP);
                break;                                
        }
        
        action.setAction(url.toString());
        actions.add(action);
        return action;
        
    }
    /**
     * 
     * <p>
     * getTitle
     * </p>
     * Returns the appropriate for the title based on locale prferences
     * 
     * @param entity
     * @return
     */
    public String getTitle(PortletEntity entity, Fragment f)
    {
        String title = null;
        
        if (f != null)
        {
            title = f.getTitle();
        }
        
        if (entity != null && title == null)
        {
            title = Jetspeed.getCurrentRequestContext().getPreferedLanguage(entity.getPortletDefinition()).getTitle();
        }
        if (title == null && entity != null)
        {
            title = entity.getPortletDefinition().getName();
        }
        
        return title;
    }
    
    /**
     * 
     * <p>
     * getTitle
     * </p>
     * Returns the appropriate for the title based on locale prferences
     * 
     * @param entity
     * @return
     */
    public String getTitle(PortletEntity entity)
    {
        String title = null;
        if (entity != null)
        {
            title  = Jetspeed.getCurrentRequestContext().getPreferedLanguage(entity.getPortletDefinition()).getTitle();
        }
        if (title == null)
        {
            title = entity.getPortletDefinition().getName();
        }
        return title;
    }
    
    public Object getComponent(String name)
    {
        return Jetspeed.getComponentManager().getComponent(name);
    }
    
    public String getAbsoluteUrl(String relativePath)
    {
        HttpServletRequest request = Jetspeed.getCurrentRequestContext().getRequest();
        StringBuffer path = new StringBuffer();
        return path.append(request.getScheme())
        .append("://")
        .append(request.getServerName())
        .append(":")
        .append(request.getServerPort())
        .append(request.getContextPath())
        .append(request.getServletPath())
        .append(relativePath)
        .toString(); 
    }
    
}
