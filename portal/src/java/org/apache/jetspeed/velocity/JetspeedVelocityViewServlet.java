/**
 * Created on Jan 9, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.Constants;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.apache.velocity.util.SimplePool;

/**
 * <p>
 * JetspeedVelocityViewServlet
 * </p>
 * Extends <code>VelocityViewServlet</code> to allow us to put portle-specific
 * information into the Velocity context.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedVelocityViewServlet extends VelocityViewServlet
{
	
	public static final String VELOCITY_WRITER_ATTR = "org.apache.velocity.io.VelocityWriter";
    /** Cache of writers */
   private static SimplePool writerPool = new SimplePool(40);
	

    public static final String VELOCITY_CONTEXT_ATTR = "org.apache.velocity.Context";
    /**
     * Adds the RenderRequest, RenderResponse and PortletConfig to the context
     * 
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.apache.velocity.context.Context)
     */
    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) throws Exception
    {
        PortletRequest renderRequest = (PortletRequest) request.getAttribute(Constants.PORTLET_REQUEST);
        RenderResponse renderResponse = (RenderResponse) request.getAttribute(Constants.PORTLET_RESPONSE);
        PortletConfig portletConfig = (PortletConfig) request.getAttribute(Constants.PORTLET_CONFIG);
        if (renderRequest != null)
        {
            renderRequest.setAttribute(VELOCITY_CONTEXT_ATTR, ctx);
        }

        ctx.put("renderRequest", renderRequest);
        ctx.put("renderResponse", renderResponse);
        ctx.put("portletConfig", portletConfig);
        ctx.put("portletModeView", PortletMode.VIEW);
        ctx.put("portletModeEdit", PortletMode.EDIT);
        ctx.put("portletModeHelp", PortletMode.HELP);
        ctx.put("windowStateNormal", WindowState.NORMAL);
        ctx.put("windowStateMinimized", WindowState.MINIMIZED);
        ctx.put("windowStateMaximized", WindowState.MAXIMIZED);
        return super.handleRequest(request, response, ctx);
    }

    /**
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet#mergeTemplate(org.apache.velocity.Template, org.apache.velocity.context.Context, javax.servlet.http.HttpServletResponse)
     */
    protected void mergeTemplate(Template template, Context context, HttpServletResponse response)
        throws
            ResourceNotFoundException,
            ParseErrorException,
            MethodInvocationException,
            IOException,
            UnsupportedEncodingException,
            Exception
    {
        PrintWriter pw = response.getWriter();
        VelocityWriter vw = null;

        try
        {
            vw = (VelocityWriter) writerPool.get();

            if (vw == null)
            {
                vw = new VelocityWriter(pw, 4 * 1024, true);
            }
            else
            {
                vw.recycle(pw);
            }
			
			// Place the VelocityWriter into the Context
			context.put(VELOCITY_WRITER_ATTR, vw);
            template.merge(context, vw);
        }
        finally
        {
            try
            {
                if (vw != null)
                {
                    // flush and put back into the pool
                    // don't close to allow us to play
                    // nicely with others.
                    vw.flush();
                    /* This hack sets the VelocityWriter's internal ref to the 
                     * PrintWriter to null to keep memory free while
                     * the writer is pooled. See bug report #18951 */
                    vw.recycle(null);
                    writerPool.put(vw);
                }
            }
            catch (Exception e)
            {
                // do nothing
            }
        }
    }

}
