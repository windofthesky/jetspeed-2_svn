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
package org.apache.jetspeed.aggregator;

import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>The ContentDispatcher control interface used for updating the content of
 *    a ContentDispatcher</p>
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface ContentDispatcherCtrl
{
    /** Notify ContentDispatcher that the content for the specified OID is
     * completely generated and is available for inclusion in other content
     */
    public void notify(ObjectID oid);

    /**
     * Return the HttpServletResponse to use for a given PortletWindow
     * in order to be able to capture parallel rendering portlets
     */
    public HttpServletResponse getResponseForWindow(PortletWindow window, RequestContext request);
}
