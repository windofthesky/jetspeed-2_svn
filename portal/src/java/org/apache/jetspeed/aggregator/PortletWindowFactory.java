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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowCtrl;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.om.window.PortletWindowListCtrl;

/**
 * PortletWindowFactory
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletWindowFactory
{
    public static PortletWindow getWindow(PortletDefinition portletDefinition, String portletName)
    {
    	if(portletDefinition == null)
    	{
    		throw new IllegalArgumentException("PortletDefinition for PortletWindow.getWindow() cannot be null.");
    	}
    	
    	PortletEntityAccessComponent entityAccess = (PortletEntityAccessComponent) Jetspeed.getComponentManager().getComponent(PortletEntityAccessComponent.class);
        PortletEntity portletEntity = entityAccess.getPortletEntity(portletDefinition, portletName);


        // TODO: This needs to be changed to support multiple windows per entity
        PortletWindow portletWindow = portletEntity.getPortletWindowList().get(portletEntity.getId());

        if (portletWindow == null)
        {
            portletWindow = new PortletWindowImpl(portletEntity.getId());
            ((PortletWindowCtrl) portletWindow).setPortletEntity(portletEntity);
            PortletWindowList windowList = portletEntity.getPortletWindowList();
            ((PortletWindowListCtrl) windowList).add(portletWindow);
        }

        return portletWindow;
    }

    public static PortletWindow getWindow(PortletEntity entity, ObjectID windowID)
    {
        PortletWindow portletWindow = entity.getPortletWindowList().get(windowID);

        if (portletWindow == null)
        {
            portletWindow = new PortletWindowImpl(windowID);
            ((PortletWindowCtrl) portletWindow).setPortletEntity(entity);
            PortletWindowList windowList = entity.getPortletWindowList();
            ((PortletWindowListCtrl) windowList).add(portletWindow);
        }

        return portletWindow;
    }
}
