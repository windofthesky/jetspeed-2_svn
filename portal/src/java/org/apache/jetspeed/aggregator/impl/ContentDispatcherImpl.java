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

import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.ContentDispatcher;
import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.PortletContent;
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

//    public void notify( ObjectID oid )
//    {
//        PortletContentImpl content = (PortletContentImpl) contents.get(oid);
//
//        if (content != null)
//        {
//            synchronized (content)
//            {
//                if ((debugLevel > 0) && log.isDebugEnabled())
//                {
//                    log.debug("Notifying complete OID " + oid);
//                }
//                content.complete();
//                content.notifyAll();
//            }
//        }
//    }

    public HttpServletResponse getResponseForWindow( PortletWindow window, RequestContext request )
    {
        PortletContentImpl myContent = new PortletContentImpl();

        return getResponseForId(request, myContent, window.getId());
    }
    
    public HttpServletResponse getResponseForFragment( Fragment fragment, RequestContext request )
    {
        PortletContentImpl myContent = new PortletContentImpl();
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
    protected HttpServletResponse getResponseForId( RequestContext request, PortletContentImpl myContent, ObjectID oid )
    {
        synchronized (contents)
        {
            contents.put(oid, myContent);
        }

        return new HttpBufferedResponse(request.getResponse(), myContent.getWriter());
    }

    /**
     * <p>
     * getPortletContent
     * </p>
     *
     * @see org.apache.jetspeed.aggregator.ContentDispatcher#getPortletContent(org.apache.jetspeed.om.page.Fragment)
     * @param fragment
     * @return
     */
    public PortletContent getPortletContent( Fragment fragment )
    {       
        ObjectID oid = JetspeedObjectID.createFromString(fragment.getId());
        PortletContentImpl content = (PortletContentImpl) contents.get(oid);
        return content;
    }
}
