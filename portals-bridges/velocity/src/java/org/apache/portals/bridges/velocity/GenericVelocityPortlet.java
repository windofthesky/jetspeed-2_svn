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
package org.apache.portals.bridges.velocity;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;


/**
 * <p>
 * Generic Velocity Portlet emulating basic functionality provided in the Portlet API (for JSPs) 
 * to Velocity portlets and templates. Provides the following Velocity context variables emulating
 * PLT.22 JSP request variables:
 * * <ul>
 * <li>$renderRequest
 * <li>$renderResponse
 * <li>$portletConfig
 * </ul>
 * </p>
 * <p>
 * PLT.22 Tags:
 * <ul>
 * <li>$actionURL -- use renderResponse.createActionURL()
 * <li>$renderURL -- use renderResponse.createRenderURL()
 * <li>$namespace -- use rennderResponse.getNamespace() (Namespace)
 * </ul>
 *  Beware that Param tags cannot be added incrementally i.e. $renderURL.setParameter("name","value").setParameter("name","value")
 *  since the portlet api returns void on setParameter (or setWindowState, setPortletMode)
 *  Thus it is required to set each param or state on a single line:
 * </p>
 * <p>   
 *    #set($max = $renderResponse.createRenderURL())
 *    $max.setWindowState($STATE_MAX)
 *    $max.setParameter("bush", "war")
 * </p>
 * <p>
 * Constants: 
 * $MODE_EDIT, $MODE_HELP, $MODE_VIEW, $STATE_NORMAL, $STATE_MIN, $STATE_MAX
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GenericVelocityPortlet extends GenericServletPortlet
{
    public final static String PORTLET_BRIDGE_CONTEXT = "portals.bridges.velocity.context"; 
    
    public GenericVelocityPortlet()
    {
    }

    public void init(PortletConfig config)
    throws PortletException
    {
        super.init(config);
    }
    
    /**
     * Execute the servlet as define by the init parameter or preference PARAM_ACTION_PAGE.  The value
     * if the parameter is a relative URL, i.e. /actionPage.jsp will execute the
     * JSP editPage.jsp in the portlet application's web app.  The action should
     * not generate any content.  The content will be generate by doCustom(),
     * doHelp() , doEdit(), or doView().
     *
     * See section PLT.16.2 of the JSR 168 Portlet Spec for more information
     * around executing a servlet or JSP in processAction()
     *
     * @see javax.portlet.GenericPortlet#processAction
     *
     * @task Need to be able to execute a servlet for the action
     */
    public void processAction(ActionRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
        super.processAction(request, actionResponse);
    }
    
    /**
     * Execute the servlet as define by the init parameter or preference PARAM_EDIT_PAGE.  The value
     * if the parameter is a relative URL, i.e. /editPage.jsp will execute the
     * JSP editPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doCustom
     */
    public void doCustom(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        super.doCustom(request, response);
    }
    
    /**
     * Execute the servlet as define by the init parameter or preference PARAM_EDIT_PAGE.
     * The value if the parameter is a relative URL, i.e. /editPage.jsp will execute the
     * JSP editPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doEdit
     */
    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        super.doEdit(request, response);
    }
    
    /**
     * Execute the servlet as define by the init parameter or preference PARAM_HELP_PAGE.
     * The value if the parameter is a relative URL, i.e. /helpPage.jsp will exeute the
     * JSP helpPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doView
     */
    public void doHelp(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        super.doHelp(request, response);
    }

    /**
     * Execute the servlet as define by the init parameter or preference PARAM_VIEW_PAGE.
     * The value if the parameter is a relative URL, i.e. /viewPage.jsp will execute the
     * JSP viewPage.jsp in the portlet application's web app.
     *
     * @see javax.portlet.GenericPortlet#doView
     */
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        super.doView(request, response);
    }
    
    public void render(RenderRequest request, RenderResponse response)
    throws PortletException, java.io.IOException    
    {
        createPortletVelocityContext(request, response);
        super.render(request, response);
    }
    
    private Context createPortletVelocityContext(RenderRequest request, RenderResponse response)
    {
        Context ctx = new VelocityContext();
        request.setAttribute(PORTLET_BRIDGE_CONTEXT, ctx);
        // PLT.22
        ctx.put("renderRequest", request);
        ctx.put("renderResponse", response);
        ctx.put("portletConfig", getPortletConfig());
        // constants
        ctx.put("STATE_NORMAL", WindowState.NORMAL);
        ctx.put("STATE_MAX", WindowState.MAXIMIZED);
        ctx.put("STATE_MIN", WindowState.MINIMIZED);
        ctx.put("MODE_VIEW", PortletMode.VIEW);
        ctx.put("MODE_EDIT", PortletMode.EDIT);
        ctx.put("MODE_HELP", PortletMode.HELP);
        return ctx;
    }
    
    public Context getContext(RenderRequest request)
    {
        return (Context)request.getAttribute(PORTLET_BRIDGE_CONTEXT);
    }
    
    
    
}
