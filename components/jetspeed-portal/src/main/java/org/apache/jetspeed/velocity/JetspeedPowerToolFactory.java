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
package org.apache.jetspeed.velocity;

import java.lang.reflect.Constructor;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.layout.JetspeedPowerTool;
import org.apache.jetspeed.request.RequestContext;

public class JetspeedPowerToolFactory implements org.apache.jetspeed.layout.JetspeedPowerToolFactory
{
    protected static final Logger log = LoggerFactory.getLogger(JetspeedPowerToolFactory.class);
    
    private Class jptClass;
    private Constructor constructor;
    
    /* Allows us to render portlets and other fragments */
    private PortletRenderer renderer;
    
    public JetspeedPowerToolFactory(String jptClassName, PortletRenderer renderer)
    throws ClassNotFoundException, NoSuchMethodException
    {
        jptClass = Thread.currentThread().getContextClassLoader().loadClass(jptClassName);
        constructor =
            jptClass.getConstructor(
                new Class[] {RequestContext.class, PortletConfig.class, RenderRequest.class, RenderResponse.class, PortletRenderer.class});        
        this.renderer = renderer;
    }
       
    public JetspeedPowerTool getJetspeedPowerTool(RequestContext requestContext, PortletConfig portletConfig, RenderRequest renderRequest, RenderResponse renderResponse)
    throws PortletException
    {
        try
        {
        	Object [] initArgs = { requestContext, portletConfig, renderRequest, renderResponse, this.renderer };
            return (JetspeedPowerTool)constructor.newInstance(initArgs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new PortletException(e);
        }
    }
}

