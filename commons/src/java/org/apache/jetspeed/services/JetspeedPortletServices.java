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
package org.apache.jetspeed.services;

import java.util.HashMap;
import java.util.Map;


/**
 * JetspeedPortletServices
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedPortletServices implements PortletServices
{
    private Map services;
    private static PortletServices singleton = null;
    
    /**
     * Necessary evil until we get a PA component framework 
     * @return
     */
    public static PortletServices getSingleton()
    {
        return singleton;
    }
    
    public JetspeedPortletServices()
    {
        this(new HashMap());
    }
    
    public JetspeedPortletServices(Map services)
    {
        singleton = this;
        this.services = services;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.PortletServices#addPortletService(java.lang.String, java.lang.Object)
     */
    public void addPortletService(String serviceName, Object service)
    {
        services.put(serviceName, service);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.components.PortletServices#getService(java.lang.String)
     */
    public Object getService(String serviceName)
    {
        return services.get(serviceName);
    }

}
