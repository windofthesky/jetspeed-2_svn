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
package org.apache.jetspeed.aggregator.impl;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Hashtable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>The ContentDispatcher allows customer classes to retrieved
 *    rendered content for a specific fragment</p>
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class ContentDispatcherImpl implements ContentDispatcher, ContentDispatcherCtrl
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(ContentDispatcherImpl.class);

    private Map contents = new Hashtable();

    private boolean isParallel = true;

    private static int debugLevel = 1;

    public ContentDispatcherImpl()
    {
    }

    public ContentDispatcherImpl(boolean isParallel)
    {
        this.isParallel = isParallel;
    }

    /**
     * Include in the provided PortletResponse output stream the rendered content
     * of the request fragment.
     * If the fragment rendered content is not yet available, the method will
     * hold until it's completely rendered.
     */
    public void include(Fragment fragment, HttpServletRequest req, HttpServletResponse rsp)
    {
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());

        if ((debugLevel > 1) && log.isDebugEnabled())
        {
            log.debug("Including content for OID "+ oid);
        }

        // If we work synchronously, call Renderer.renderNow
        if (!isParallel)
        {
            if ((debugLevel > 0) && log.isDebugEnabled())
            {
                log.debug("Synchronous rendering for OID "+ oid);
            }
            PortletRenderer renderer = (PortletRenderer)CommonPortletServices.getPortalService(PortletRenderer.SERVICE_NAME);
            renderer.renderNow(fragment,req,rsp);
            return;
        }

        PortletContent content = (PortletContent)contents.get(oid);

        if (content!=null)
        {
            synchronized (content)
            {
                if (!content.isComplete())
                {
                    if ((debugLevel > 0) && log.isDebugEnabled())
                    {
                        log.debug("Waiting for content OID "+oid);
                    }

                    try
                    {
                        content.wait();
                    }
                    catch (InterruptedException e)
                    {
                    }

                    if ((debugLevel > 0) && log.isDebugEnabled())
                    {
                        log.debug("Been notified that OID "+oid+" is complete");
                    }
                }

                if ((debugLevel > 1) && log.isDebugEnabled())
                {
                    log.debug("Content OID "+oid+": "+content.toString());
                }
            }

            try
            {
                try
                {
                    rsp.getWriter().write(content.toString());
                }
                catch (IllegalStateException e)
                {
                    rsp.getOutputStream().print(content.toString());
                }
            }
            catch (Exception e)
            {
                log.error("Unable to include content OID "+oid+" in response object", e);
            }
            finally
            {
                synchronized(contents)
                {
                    if ((debugLevel > 1) && log.isDebugEnabled())
                    {
                        log.debug("Removing content OID "+oid);
                    }
                    ((PortletContent)contents.remove(oid)).release();
                }
            }
        }
        else
        {
            // should only happen when a layout tries to render an inner layout
            // trigger a synchronous rendering of this fragment

            if ((debugLevel > 1) && log.isDebugEnabled())
            {
                log.debug("Content is null for OID "+oid);
            }

            PortletRenderer renderer = (PortletRenderer)CommonPortletServices.getPortalService(PortletRenderer.SERVICE_NAME);
            renderer.renderNow(fragment,req,rsp);
            return;
        }
    }

    /**
     * Include in the provided PortletResponse output stream the rendered content
     * of the request fragment.
     * If the fragment rendered content is not yet available, the method will
     * hold until it's completely rendered.
     */
    public void include(Fragment fragment, javax.portlet.RenderRequest req, javax.portlet.RenderResponse rsp)
    {
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());

        if ((debugLevel > 1) && log.isDebugEnabled())
        {
            log.debug("Including content for OID "+ oid);
        }

        PortletContent content = (PortletContent)contents.get(oid);

        if (content!=null)
        {
            synchronized (content)
            {
                if (!content.isComplete())
                {
                    if ((debugLevel > 0) && log.isDebugEnabled())
                    {
                        log.debug("Waiting for content OID "+oid);
                    }

                    try
                    {
                        content.wait();
                    }
                    catch (InterruptedException e)
                    {
                    }

                    if ((debugLevel > 0) && log.isDebugEnabled())
                    {
                        log.debug("Been notified that OID "+oid+" is complete");
                    }
                }

                if ((debugLevel > 1) && log.isDebugEnabled())
                {
                    log.debug("Content OID "+oid+": "+content.toString());
                }
            }

            try
            {
                try
                {
                    rsp.getWriter().write(content.toString());
                }
                catch (IllegalStateException e)
                {
                    //rsp.getPortletOutputStream().print(content.toString());
                }
            }
            catch (Exception e)
            {
                log.error("Unable to include content OID "+oid+" in response object", e);
            }
            finally
            {
                synchronized(contents)
                {
                    if ((debugLevel > 1) && log.isDebugEnabled())
                    {
                        log.debug("Removing content OID "+oid);
                    }
                    ((PortletContent)contents.remove(oid)).release();
                }
            }
        }
        else
        {
            // should only happen when a layout tries to render an inner layout
            // trigger a synchronous rendering of this fragment

            if ((debugLevel > 1) && log.isDebugEnabled())
            {
                log.debug("Content is null for OID "+oid);
            }

            PortletRenderer renderer = (PortletRenderer)CommonPortletServices.getPortalService(PortletRenderer.SERVICE_NAME);

            //unwrap the RenderRequest and RenderResponse to avoid having to cascade several
            // portlet requests/responses
            HttpServletRequest request = (HttpServletRequest)((HttpServletRequestWrapper)req).getRequest();
            HttpServletResponse response = (HttpServletResponse)((HttpServletResponseWrapper)rsp).getResponse();
            renderer.renderNow(fragment,request,response);
            return;
        }
    }

    public void notify(ObjectID oid)
    {
        PortletContent content = (PortletContent)contents.get(oid);

        if (content!=null)
        {
            synchronized (content)
            {
                if ((debugLevel > 0) && log.isDebugEnabled())
                {
                    log.debug("Notifying complete OID "+oid);
                }
                content.setComplete(true);
                content.notifyAll();
            }
        }
    }

    public HttpServletResponse getResponseForWindow(PortletWindow window, RequestContext request)
    {
        PortletContent myContent = new PortletContent();

        synchronized (contents)
        {
            contents.put(window.getId(),myContent);
        }

        return new HttpBufferedResponse(request.getResponse(),myContent.getWriter());
    }

    protected class PortletContent
    {
        private CharArrayWriter cw;
        private PrintWriter writer;
        private boolean complete = false;

        PortletContent()
        {
            init();
        }

        public PrintWriter getWriter()
        {
            return writer;
        }

        public void init()
        {
            cw = new CharArrayWriter();
            writer = new PrintWriter(cw);
        }

        public void release()
        {
            writer.close();
        }

        public String toString()
        {
            writer.flush();
            return cw.toString();
        }

        public void writeTo(java.io.Writer out) throws java.io.IOException
        {
            writer.flush();
            cw.writeTo(out);
        }

        public char[] toCharArray()
        {
            writer.flush();
            return cw.toCharArray();
        }

        public boolean isComplete()
        {
            return complete;
        }

        void setComplete(boolean state)
        {
            this.complete = state;
        }
    }
}
