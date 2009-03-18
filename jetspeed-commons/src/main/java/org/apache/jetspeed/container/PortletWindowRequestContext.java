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

package org.apache.jetspeed.container;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.jetspeed.factory.PortletInstance;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.PortletResponseContext;

/**
 * @version $Id$
 *
 */
public interface PortletWindowRequestContext
{
    final class Current
    {
        private static final ThreadLocal<PortletWindowRequestContext> current = new ThreadLocal<PortletWindowRequestContext>();
        
        public static PortletWindowRequestContext create(PortletWindowRequestContext context)
        {
            current.set(context);
            return context;
        }
        public static PortletWindowRequestContext get()
        {
            return current.get();
        }
        public static PortletWindowRequestContext remove()
        {
            PortletWindowRequestContext pwrc = current.get();
            current.remove();
            return pwrc;
        }
    }
    
    enum Action { NOOP, LOAD, ACTION, EVENT, RESOURCE, RENDER };
    
    Action getAction();
    PortletWindow getPortletWindow();
    PortletRequest getPortletRequest();
    PortletResponse getPortletResponse();
    PortletRequestContext getPortletRequestContext();
    PortletResponseContext getPortletResponseContext();
    PortletInstance getPortletInstance();
}
