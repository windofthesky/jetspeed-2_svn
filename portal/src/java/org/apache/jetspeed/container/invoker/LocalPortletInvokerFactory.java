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
 * LocalPortletInvokerFactory is the factory for creating portlet invokers that 
 * invoke portlets running in 'jetsped local' portlet applications. s
 * <h3>Sample Configuration</h3>
 * <pre>
 * <code>
 * factory.invoker.local = org.apache.jetspeed.container.invoker.LocalPortletInvoker
 * factory.invoker.local.pool.size = 50
 * </code> 
 * </pre>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class LocalPortletInvokerFactory extends AbstractPortletInvokerFactory
{    
    public final static String INVOKER_LOCAL = "factory.invoker.local";
    public final static String INVOKER_LOCAL_POOL_SIZE = "factory.invoker.local.pool.size";

    
    /**
     * Create a local portlet invoker factory
     * 
     * @param pc The portal's context to get at the configuration.
     */
    public LocalPortletInvokerFactory(PortalContext pc)
    {
        super();        
        String localInvokerClass = pc.getConfigurationProperty(INVOKER_LOCAL);
        int localInvokerPoolSize = pc.getConfiguration().getInt(INVOKER_LOCAL_POOL_SIZE);
        init(localInvokerClass, localInvokerPoolSize);        
    }
        
}
