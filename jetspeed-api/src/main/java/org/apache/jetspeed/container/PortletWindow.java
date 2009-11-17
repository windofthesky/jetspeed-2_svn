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

import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.KeyValue;
import org.apache.pluto.container.PortletRequestContext;
import org.apache.pluto.container.PortletResponseContext;

/**
 * @version $Id$
 *
 */
public interface PortletWindow extends org.apache.pluto.container.PortletWindow
{
    boolean isValid();
    PortletWindowID getId();
    String getWindowId();
    String getPortletEntityId();
    PortletDefinition getPortletDefinition();
    ContentFragment getFragment();
    boolean isInstantlyRendered();
    Map<String, Object> getAttributes();
    Object getAttribute(String name);
    void setAttribute(String name, Object value);
    void removeAttribute(String name);
    
    RequestContext getRequestContext();
    
    // PortletWindow invocation support: may only be used / accessed during invocation
    
    enum Action { NOOP, LOAD, ACTION, EVENT, RESOURCE, RENDER };
    
    Action getAction();
    PortletRequest getPortletRequest();
    PortletResponse getPortletResponse();
    PortletRequestContext getPortletRequestContext();
    PortletResponseContext getPortletResponseContext();
    PortletInstance getPortletInstance();
    
    /**
     * Returns all the contributed head elements which were aggregated from this window content
     * and all the child window contents.
     * <P>
     * The head elements are sorted by the insertion order.
     * </P>
     * 
     * @return
     */
    List<KeyValue<String, HeadElement>> getHeadElements();
    
}
