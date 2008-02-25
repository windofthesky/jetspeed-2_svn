/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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