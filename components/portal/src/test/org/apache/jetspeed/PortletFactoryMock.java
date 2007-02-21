/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed;

import javax.portlet.PortletException;
import javax.portlet.PreferencesValidator;
import javax.servlet.ServletContext;

import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.pluto.om.portlet.PortletDefinition;

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
}