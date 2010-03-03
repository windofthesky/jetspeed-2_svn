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
package org.apache.jetspeed;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.PreferencesValidator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletFilterInstance;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;

public final class PortletFactoryMock implements PortletFactory
{
    public void registerPortletApplication(PortletApplication pa, ClassLoader paClassLoader){}

    public void unregisterPortletApplication(PortletApplication pa){}

    public ClassLoader getPortletApplicationClassLoader(PortletApplication pa){return null;}

    public PortletInstance getPortletInstance(ServletContext servletContext, PortletDefinition pd) throws PortletException{return null;}

    public PreferencesValidator getPreferencesValidator(PortletDefinition pd){return null;}

    public boolean isPortletApplicationRegistered(PortletApplication pa){return true;}
    
    public static final PortletFactoryMock instance = new PortletFactoryMock();
    
    public void updatePortletConfig(PortletDefinition pd) {}

    public void setPortalContext(PortalContext portalContext) {}
    
    public PortletRequestDispatcher createRequestDispatcher(RequestDispatcher requestDispatcher) { return null; }
    
    public PortletRequestDispatcher createRequestDispatcher(RequestDispatcher requestDispatcher, String path) { return null; }

    public List<PortletURLGenerationListener> getPortletApplicationListeners(PortletApplication pa)
    throws PortletException
    {
        return null;
    }

    public PortletFilterInstance getPortletFilterInstance(PortletApplication pa, String filterName)
    throws PortletException
    {
        return null;
    }

    public PortletInstance getPortletInstance(ServletContext servletContext, PortletDefinition pd, boolean noProxy)
    throws PortletException
    {
        return null;
    }

    public ResourceBundle getResourceBundle(PortletDefinition pd, Locale locale)
    {
        return null;
    }

    public ResourceBundle getResourceBundle(PortletApplication pa, Locale locale)
    {
        return null;
    }

    public boolean hasRenderHelperMethod(PortletDefinition pd, PortletMode mode)
    {
        return false;
    }

    public void reloadResourceBundles(PortletApplication pa) throws PortletException
    {
    }

    public void reloadResourceBundles(PortletDefinition pd) throws PortletException
    {
    }
}