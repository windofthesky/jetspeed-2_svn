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

import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.layout.JetspeedPowerTool;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.title.DynamicTitleService;

public class JetspeedPowerToolFactory implements org.apache.jetspeed.layout.JetspeedPowerToolFactory
{
    protected static final Log log = LogFactory.getLog(JetspeedPowerToolFactory.class);
    
    private Class jptClass;
    private Constructor constructor;
    private DynamicTitleService titleService;
    
    /* Allows us to render portlets and other fragments */
    private PortletRenderer renderer;
    
    public JetspeedPowerToolFactory(String jptClassName, DynamicTitleService titleService, PortletRenderer renderer)
    throws ClassNotFoundException, NoSuchMethodException
    {
        jptClass = Thread.currentThread().getContextClassLoader().loadClass(jptClassName);
        constructor =
            jptClass.getConstructor(
                new Class[] {RequestContext.class, DynamicTitleService.class, PortletRenderer.class});        
        this.titleService = titleService;
        this.renderer = renderer;
    }
       
    public JetspeedPowerTool getJetspeedPowerTool(RequestContext requestContext)
    throws PortletException
    {
        try
        {
        	Object [] initArgs = { requestContext, this.titleService, this.renderer };
            return (JetspeedPowerTool)constructor.newInstance(initArgs);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new PortletException(e);
        }
    }
}

