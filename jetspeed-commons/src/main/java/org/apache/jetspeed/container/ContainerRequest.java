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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.portlet.PortletRequest;

import org.apache.pluto.om.portlet.Portlet;

/**
 * The container request wrappers the servlet request and is used 
 * within the container to communicate to the invoked servlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ContainerRequest extends HttpServletRequestWrapper
{
    protected Portlet portletDef;
    protected PortletRequest portletRequest;

    public ContainerRequest(HttpServletRequest httpRequest, 
                            Portlet portletDef)
    {
        super(httpRequest);
        this.portletDef = portletDef;
    }

    public Portlet getPortletDefinition()
    {
        return this.portletDef;
    }

    public PortletRequest getPortletRequest()
    {
        return portletRequest;
    }

    public void setPortletRequest(PortletRequest portletRequest)
    {
        this.portletRequest = portletRequest;
    }
}

