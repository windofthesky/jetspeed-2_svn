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
package org.apache.jetspeed.container.services;

import java.util.HashMap;

import org.apache.pluto.services.ContainerService;
import org.apache.pluto.services.PortletContainerEnvironment;

/**
 * All services that are to be made available to the container should be added via the addService method
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedContainerServices implements PortletContainerEnvironment
{
    private HashMap services = new HashMap();

    public JetspeedContainerServices()
    {
    }

    /**
     * Add a container service. This service wrappers a portal service exposed to the container.
     * 
     * @param service The service to make visible to the container.
     */
    public void addService(ContainerService service)
    {
        Class serviceClass = service.getClass();
        while (serviceClass != null)
        {
            Class[] interfaces = serviceClass.getInterfaces();
            for (int i = 0; i < interfaces.length; i++)
            {
                Class[] interfaces2 = interfaces[i].getInterfaces();
                for (int ii = 0; ii < interfaces2.length; ii++)
                {
                    if (interfaces2[ii].equals(ContainerService.class))
                    {
                        services.put(interfaces[i], service);
                    }
                }
            }
            serviceClass = serviceClass.getSuperclass();
        }
    }

    public void addServiceForClass(Class serviceClass, ContainerService service)
    {
        services.put(serviceClass, service);
    }

    /**
     * Gets a container service by class name. The portal implementation can only
     * add to the available services provided to the container.  
     * 
     * @param service The found service or null if not found.
     */
    public ContainerService getContainerService(Class service)
    {       
        return (ContainerService) services.get(service);
    }

}
