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
package org.apache.jetspeed.services.rest;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JaxrsServiceValve
 *
 * @version $Id$
 */
public class JaxrsServiceValve extends AbstractValve
{
    
    private static final Logger log = LoggerFactory.getLogger(JaxrsServiceValve.class);

    private Class<?> busFactoryClass;       // org.apache.cxf.BusFactory
    private Object bus;                     // org.apache.cxf.Bus
    private Object servletController;       // org.apache.cxf.transport.servlet.ServletController
    private Object jaxrsServerFactoryBean;  // org.apache.cxf.jaxrs.JAXRSServerFactoryBean
    
    public JaxrsServiceValve(Class<?> busFactoryClass, Object bus, Object servletController, Object jaxrsServerFactoryBean)
    {
        this.busFactoryClass = busFactoryClass;
        this.bus = bus;
        this.servletController = servletController;
        this.jaxrsServerFactoryBean = jaxrsServerFactoryBean;
    }
    
    @Override
    public void initialize() throws PipelineException
    {
        try 
        {
            MethodUtils.invokeStaticMethod(busFactoryClass, "setThreadDefaultBus", new Object [] { bus });
            MethodUtils.invokeMethod(jaxrsServerFactoryBean, "create", null);
        }
        catch (Exception e)
        {
            log.error("Failed to initialize jaxrs server.", e);
        }
        finally 
        {
            try
            {
                MethodUtils.invokeStaticMethod(busFactoryClass, "setThreadDefaultBus",  new Object [] { null }, new Class[] {bus.getClass()});
            }
            catch (Exception ignore)
            {
            }
        }
    }
    
    public void destroy() 
    {
        try
        {
            MethodUtils.invokeMethod(bus, "shutdown", Boolean.TRUE);
        }
        catch (Exception e)
        {
            log.error("Failed to destroy jaxrs bus.", e);
        }
    }
    
    @Override
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try 
        {
            MethodUtils.invokeStaticMethod(busFactoryClass, "setThreadDefaultBus", new Object [] { bus });
            MethodUtils.invokeMethod(servletController, "invoke", new Object [] { request.getRequest(), request.getResponse() });
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
            {
                log.error("Failed to invoke jaxrs service.", e);
            }
            else
            {
                log.error("Failed to invoke jaxrs service. {}", e.toString());
            }
        }
        finally 
        {
            try
            {
                MethodUtils.invokeStaticMethod(busFactoryClass, "setThreadDefaultBus",  new Object [] { null }, new Class[] {bus.getClass()});
            }
            catch (Exception ignore)
            {
            }
        }
    }
    
}
