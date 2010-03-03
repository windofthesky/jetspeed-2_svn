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

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.PreferencesValidator;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletFactory
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * 
 * @version $Id$
 *  
 */
public interface PortletFactory
{
    void setPortalContext(PortalContext portalContext);
    void registerPortletApplication(PortletApplication pa, ClassLoader paClassLoader);
    void unregisterPortletApplication(PortletApplication pa);
    boolean isPortletApplicationRegistered(PortletApplication pa);
    ClassLoader getPortletApplicationClassLoader(PortletApplication pa);
    PortletInstance getPortletInstance( ServletContext servletContext, PortletDefinition pd ) throws PortletException;
    PortletInstance getPortletInstance( ServletContext servletContext, PortletDefinition pd, boolean noProxy ) throws PortletException;
    PreferencesValidator getPreferencesValidator(PortletDefinition pd);
    void updatePortletConfig(PortletDefinition pd);
    ResourceBundle getResourceBundle(PortletApplication pa, Locale locale);
    ResourceBundle getResourceBundle(PortletDefinition pd, Locale locale);
    List<PortletURLGenerationListener> getPortletApplicationListeners(PortletApplication pa) throws PortletException;
    PortletFilterInstance getPortletFilterInstance(PortletApplication pa, String filterName) throws PortletException;
    
    /**
     * Returns true when the portlet class is type of javax.portlet.GenericPortlet
     * and it contains a helper method for the portlet mode with public access.
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
    boolean hasRenderHelperMethod( PortletDefinition pd, PortletMode mode );
    
    /**
     * Tries to reload resource bundles of portlet application.
     * @param pa portlet application
     * @return True if the reloading has been succeeded.
     * @throws PortletException
     */
    void reloadResourceBundles(PortletApplication pa) throws PortletException;
    
    /**
     * Tries to reload resource bundles of portlet definition.
     * @param pd portlet definition
     * @return True if the reloading has been succeeded.
     * @throws PortletException
     */
    void reloadResourceBundles(PortletDefinition pd) throws PortletException;
    
}
