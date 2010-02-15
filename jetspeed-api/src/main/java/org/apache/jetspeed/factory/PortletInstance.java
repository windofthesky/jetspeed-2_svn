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
package org.apache.jetspeed.factory;

import javax.portlet.EventPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceServingPortlet;

import org.apache.jetspeed.container.JetspeedPortletConfig;

/**
 * PortletInstance
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 *
 */
public interface PortletInstance extends Portlet, EventPortlet, ResourceServingPortlet
{
    
    /**
     * Returns Jetspeed portlet config object.
     * @return
     */
    JetspeedPortletConfig getConfig();
    
    /**
     * Returns the real portlet object instance. This portlet object instance
     * can be a proxy instance. You can check if it is a proxy by using {@link #isProxyInstance()}.
     * @return
     */
    Portlet getRealPortlet();
    
    /**
     * True if the real portlet object instance is a proxy instance.
     * @return
     */
    boolean isProxyInstance();
    
    /**
     * Returns true when the real portlet object instance is type of javax.portlet.GenericPortlet
     * and the instance contains a helper method for the portlet mode with public access.
     * <P>
     * The helper methods can be overriden from the <CODE>javax.portlet.GenericPortlet</CODE> such as the following methods</CODE> 
     * or annotated with <CODE>@RenderMode (javax.portlet.RenderMode)</CODE>.
     * <ul>
     *   <li><code>doView</code> for handling <code>view</code> requests</li>
     *   <li><code>doEdit</code> for handling <code>edit</code> requests</li>
     *   <li><code>doHelp</code> for handling <code>help</code> requests</li>
     * </ul>
     * </P>
     * 
     * @param mode
     * @return
     * 
     * @see javax.portlet.RenderMode
     * @see javax.portlet.GenericPortlet#doView(RenderRequest, RenderResponse)
     * @see javax.portlet.GenericPortlet#doEdit(RenderRequest, RenderResponse)
     * @see javax.portlet.GenericPortlet#doHelp(RenderRequest, RenderResponse)
     */
    boolean hasRenderHelperMethod(PortletMode mode);
    
}