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
package org.apache.jetspeed.aggregator.impl;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.UnrenderedContentException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>
 * The ContentDispatcher allows customer classes to retrieved rendered content
 * for a specific fragment
 * </p>
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @version $Id$
 */
public class ContentDispatcherImpl implements ContentDispatcher, ContentDispatcherCtrl
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(ContentDispatcherImpl.class);

    private Map contents = new Hashtable();

    private boolean isParallel = true;

    private static int debugLevel = 1;

    

    public ContentDispatcherImpl( boolean isParallel )
    {        
        this.isParallel = isParallel;
    }

    /**
     * Include in the provided PortletResponse output stream the rendered
     * content of the request fragment. If the fragment rendered content is not
     * yet available, the method will hold until it's completely rendered.
     * 
     * @throws FailedToRenderFragmentException
     *                   if the Fragment to include could not be rendered.
     * @throws UnrenderedContentException
     */
    public void include( Fragment fragment )
            throws  UnrenderedContentException
    {
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());
        PortletContent content = (PortletContent) contents.get(oid);
        log.debug("Including content for OID " + oid);

        if (!isParallel)
        {
             log.debug("Synchronous rendering for OID " + oid);
             if(content.toString().length() > 0)
             {
                fragment.setRenderedContent(content.toString());
             }
        }
        else
        {

            if (content != null)
            {
                synchronized (content)
                {
                    if (!content.isComplete())
                    {
                        log.debug("Waiting for content OID " + oid);
                        try
                        {
                            content.wait();
                        }
                        catch (InterruptedException e)
                        {
                        }
                        log.debug("Been notified that OID " + oid + " is complete");
                    }

                    log.debug("Content OID " + oid + ": " + content.toString());
                }

                try
                {
                    if(content.toString().length() > 0)
                    {
                        fragment.setRenderedContent(content.toString());
                    }
                }
                catch (Exception e)
                {
                    log.error("Unable to include content OID " + oid + " in response object", e);
                }
                finally
                {
                    synchronized (contents)
                    {
                        log.debug("Removing content OID " + oid);
                        ((PortletContent) contents.remove(oid)).release();
                    }
                }
            }
            else
            {
                throw new UnrenderedContentException("It appears that the content for fragment "+oid+" was not rendered.  "+
                        "Please verify that your aggregagtion implementation fully renders all content.");
            }
        }
    }

    public void notify( ObjectID oid )
    {
        PortletContent content = (PortletContent) contents.get(oid);

        if (content != null)
        {
            synchronized (content)
            {
                if ((debugLevel > 0) && log.isDebugEnabled())
                {
                    log.debug("Notifying complete OID " + oid);
                }
                content.setComplete(true);
                content.notifyAll();
            }
        }
    }

    public HttpServletResponse getResponseForWindow( PortletWindow window, RequestContext request )
    {
        PortletContent myContent = new PortletContent();

        return getResponseForId(request, myContent, window.getId());
    }
    
    public HttpServletResponse getResponseForFragment( Fragment fragment, RequestContext request )
    {
        PortletContent myContent = new PortletContent();
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());
        
        return getResponseForId(request, myContent, oid);
    }

    /**
     * <p>
     * getResponseForId
     * </p>
     *
     * @param request
     * @param myContent
     * @param oid
     * @return
     */
    protected HttpServletResponse getResponseForId( RequestContext request, PortletContent myContent, ObjectID oid )
    {
        synchronized (contents)
        {
            contents.put(oid, myContent);
        }

        return new HttpBufferedResponse(request.getResponse(), myContent.getWriter());
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

        public void writeTo( java.io.Writer out ) throws java.io.IOException
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

        void setComplete( boolean state )
        {
            this.complete = state;
        }
    }

    /*
     * Sequentially wait on content generation
     * 
     * @see org.apache.jetspeed.aggregator.ContentDispatcher#sync(org.apache.jetspeed.om.page.Fragment)
     */
    public void sync( Fragment fragment )
    {
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());

        PortletContent content = (PortletContent) contents.get(oid);

        synchronized (content)
        {
            if (!content.isComplete())
            {
                if ((debugLevel > 0) && log.isDebugEnabled())
                {
                    log.debug("Waiting for content OID " + oid);
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
                    log.debug("Been notified that OID " + oid + " is complete");
                }
            }

            if ((debugLevel > 1) && log.isDebugEnabled())
            {
                log.debug("Content OID " + oid + ": " + content.toString());
            }
        }

    }

}
