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

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PreferencesValidator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.jetspeed.container.InternalPortletConfig;
import org.apache.jetspeed.container.InternalPortletContext;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.PortletContainerException;

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
    void registerPortletApplication(PortletApplication pa, ClassLoader paClassLoader);
    void unregisterPortletApplication(PortletApplication pa);
    boolean isPortletApplicationRegistered(PortletApplication pa);
    ClassLoader getPortletApplicationClassLoader(PortletApplication pa);
    PortletInstance getPortletInstance( ServletContext servletContext, PortletDefinition pd ) throws PortletException;
    PreferencesValidator getPreferencesValidator(PortletDefinition pd);
    void updatePortletConfig(PortletDefinition pd);
    org.apache.pluto.internal.InternalPortletContext getPortletContext(PortletApplication pa)  throws PortletContainerException;
    // TODO: 2.2 refactor following 3 interfaces to a more common factory interface
    InternalPortletContext createPortletContext(ServletContext servletContext, PortletApplication application);
    InternalPortletConfig createPortletConfig(ServletConfig config, PortletContext portletContext, PortletDefinition pd);    
    PortletRequestDispatcher createRequestDispatcher(RequestDispatcher requestDispatcher);
}
