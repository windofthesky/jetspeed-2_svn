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
package org.apache.jetspeed.container.invoker;

import org.apache.jetspeed.PortalContext;

/**
 * ServletPortletInvokerFactory is the factory for creating portlet invokers that 
 * use Jetspeed Container servlet. 
 * <h3>Sample Configuration</h3>
 * <pre>
 * <code>
 * factory.invoker.servlet = org.apache.jetspeed.container.invoker.ServletPortletInvoker
 * factory.invoker.servlet.pool.size = 50
 * factory.invoker.servlet.mapping.name = /container
 * </code> 
 * </pre>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletPortletInvokerFactory extends AbstractPortletInvokerFactory
{
    public final static String INVOKER_SERVLET = "factory.invoker.servlet";
    public final static String INVOKER_SERVLET_POOL_SIZE = "factory.invoker.servlet.pool.size";
    public final static String INVOKER_SERVLET_MAPPING_NAME = "factory.invoker.servlet.mapping.name";
    public final static String DEFAULT_MAPPING_NAME = "/container";
    
    protected String servletMappingName = null;
    
    public ServletPortletInvokerFactory(PortalContext pc)
    {    
        super();                
        String servletInvokerClass = pc.getConfigurationProperty(INVOKER_SERVLET);        
        int servletInvokerPoolSize = pc.getConfiguration().getInt(INVOKER_SERVLET_POOL_SIZE, 50);
        servletMappingName = pc.getConfigurationProperty(INVOKER_SERVLET_MAPPING_NAME, DEFAULT_MAPPING_NAME);
        init(servletInvokerClass, servletInvokerPoolSize);                
    }
    
    public String getServletMappingName()
    {
        return servletMappingName;
    }
    
}
