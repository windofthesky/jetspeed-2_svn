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

package org.apache.jetspeed.engine;

import javax.servlet.ServletConfig;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.request.RequestContext;

/**
 * @version $Id$
 *
 */
public class MockJetspeedEngine implements Engine
{
    private ComponentManager componentManager;
    
    public void setComponentManager(ComponentManager componentManager)
    {
        this.componentManager = componentManager;
    }
    
    public ComponentManager getComponentManager()
    {
        return componentManager;
    }

    public PortalContext getContext()
    {
        return null;
    }

    public RequestContext getCurrentRequestContext()
    {
        return null;
    }

    public Pipeline getPipeline()
    {
        return null;
    }

    public Pipeline getPipeline(String pipelineName)
    {
        return null;
    }

    public String getRealPath(String path)
    {
        return null;
    }

    public ServletConfig getServletConfig()
    {
        return null;
    }

    public void service(RequestContext context) throws JetspeedException
    {
    }

    public void shutdown() throws JetspeedException
    {
    }

    public void start() throws JetspeedException
    {
    }
}
