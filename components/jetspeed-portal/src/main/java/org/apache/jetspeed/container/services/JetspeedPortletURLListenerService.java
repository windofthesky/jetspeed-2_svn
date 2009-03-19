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
package org.apache.jetspeed.container.services;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletURLGenerationListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.pluto.container.PortletURLListenerService;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;

public class JetspeedPortletURLListenerService implements PortletURLListenerService
{
    /** Logger. */
    private static final Log LOG = LogFactory.getLog(JetspeedPortletURLListenerService.class);

    public List<PortletURLGenerationListener> getPortletURLGenerationListeners(PortletApplicationDefinition app)
    {
        List<PortletURLGenerationListener> listeners = new ArrayList<PortletURLGenerationListener>();
        //this list is needed for the classnames
        List<? extends Listener> portletURLFilterList = ((PortletApplication) app).getListeners();
        //Iterate over the classnames and for each entry in the list the filter..URL is called.
        if (portletURLFilterList != null){
            for (Listener listener : portletURLFilterList) {
                try {
                    listeners.add(listener.getListenerInstance(Thread.currentThread().getContextClassLoader()));
                } catch (ClassNotFoundException e) {
                    String message = "The listener class isn't found: " + listener.getListenerClass();
                    LOG.error(message);
                } catch (InstantiationException e) {
                    String message = "The listener class instantiation fail: " + listener.getListenerClass();
                    LOG.error(message);
                } catch (IllegalAccessException e) {
                    String message = "IllegalAccessException on the listener class: " + listener.getListenerClass();
                    LOG.error(message);
                }
            }
        }
        return listeners;
    }
}
