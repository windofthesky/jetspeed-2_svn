/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>The ContentDispatcher control interface used for updating the content of
 *    a ContentDispatcher</p>
 *
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @version $Id$
 */
public interface ContentDispatcherCtrl extends ContentDispatcher
{
    /**
     * Return the HttpServletResponse to use for a given PortletWindow
     * in order to be able to capture parallel rendering portlets
     */
    public HttpServletResponse getResponseForWindow(PortletWindow window, RequestContext request);
    
    /**
     * 
     * <p>
     * getResponseForFragment
     * </p>
     * <p>
     *  Return the HttpServletResponse to use for a given Fragment
     *  in order to be able to capture parallel rendering portlets
     * </p>
     * @param fragment
     * @param request
     * @return
     */
    public HttpServletResponse getResponseForFragment( Fragment fragment, RequestContext request );
}