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

package org.apache.cornerstone.framework.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceDescriptor;
import org.apache.cornerstone.framework.api.service.IServiceManager;
import org.apache.cornerstone.framework.api.service.InvalidServiceException;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.registry.RegistryPropertiesFactory;
import org.apache.cornerstone.framework.singleton.Singleton;
import org.apache.cornerstone.framework.singleton.SingletonManager;
import org.apache.log4j.Logger;

/**
Factory for getting all service instances.
*/

public class ServiceManager extends Singleton implements IServiceManager
{
    public static final String REVISION = "$Revision$";

    public static final String SERVICE = "service";

    public static final String SERVICE_REGISTRY_FACTORY_CLASS_NAME = "serviceRegistryFactory.className";
    public static final String SERVICE_REGISTRY_DOMAIN_NAME = "serviceRegistry.domainName";
    public static final String DEFAULT_SERVICE_REGISTRY_DOMAIN_NAME = SERVICE;

    public static final String CLASS_NAME = "className";
    public static final String FACTORY_CLASS_NAME = "factoryClassName";
    public static final String NAME = "name";

    public static final String META = "_";
    public static final String META_CLASS_NAME = META + "." + CLASS_NAME;
    public static final String META_FACTORY_CLASS_NAME = META + "." + FACTORY_CLASS_NAME;
    public static final String META_NAME = META + "." + NAME; 

    protected static ServiceManager _Singleton = new ServiceManager();

    private static Logger _Logger = Logger.getLogger(ServiceManager.class);

    public static ServiceManager getSingleton()
    {
        return _Singleton;
    }

    public ServiceManager()
    {
        try
        {
            init();
            // TODO: change to use implementation registry
            _registry = (IRegistry) RegistryPropertiesFactory.getSingleton().createInstance();
        }
        catch (CreationException ce)
        {
            _Logger.error("failed to create instance of " + getClass().getName());
        }
    }

    /**
     * Gets new instance of service by its class name.
     * @param className class name of service
     * @return new instance of service class
     */
    public IService createServiceByClassName(String className) throws ServiceException
    {
        try
        {
            Object myObj = Class.forName(className).newInstance();
            return (IService)myObj ;
        }
        catch(Exception e)
        {
            throw new ServiceException("failed to create service instance (class=" + className + ")", e);
        }
    }

    /**
     * Gets new instance of service by its factory class name.
     * @param factoryClassName class name of service factory
     * @return new instance created by calling createInstance() on
     *   service factory.
     */
    public IService createServiceByFactoryClassName(String factoryClassName)
        throws ServiceException
    {
        IFactory factory = (IFactory) SingletonManager.getSingleton(factoryClassName);
        if (factory != null)
        {
            try
            {
                return (IService) factory.createInstance();
            }
            catch (CreationException ce)
            {
                throw new ServiceException(ce.getCause());
            }
        }

        throw new ServiceException("failed to create instance of factory class " + factoryClassName);
    }

    public IService createServiceByName(String logicalName)
        throws ServiceException
    {
        if (_serviceDescriptorMap != null)
        {
            IServiceDescriptor sd = (IServiceDescriptor) _serviceDescriptorMap.get(logicalName);

            if (sd == null)
            {
                throw new ServiceException("service '" + logicalName + "' not found in registry");
            }

            if (!sd.isValid())
            {
                throw new ServiceException("service '" + logicalName + "' not valid");
            }
        }

        String serviceRegistryDomainName = getServiceRegistryDomainName();
        IRegistryEntry entry = _registry.getRegistryEntry(serviceRegistryDomainName, logicalName);
        if (entry == null)
        {
            throw new ServiceException("service '" + logicalName + "' not found in registry");
        }

        String factoryClassName = entry.getProperty(META_FACTORY_CLASS_NAME);
        if (factoryClassName != null)
        {
            IService service = createServiceByFactoryClassName(factoryClassName);
            service.overwriteConfig(entry.getProperties());
            return new LogicalService(logicalName, service);
        }

        String className = entry.getProperty(META_CLASS_NAME);
        if (className != null)
        {
            IService service = createServiceByClassName(className);
            service.overwriteConfig(entry.getProperties());
            return new LogicalService(logicalName, service);
        }

        throw new ServiceException("'" + META_CLASS_NAME + "' or '"  + META_FACTORY_CLASS_NAME + "' not defined for service '" + logicalName + "' in registry");
    }

    public String getServiceRegistryDomainName()
    {
        String serviceRegistryDomainName = getConfigProperty(SERVICE_REGISTRY_DOMAIN_NAME);
        if (serviceRegistryDomainName == null)
            serviceRegistryDomainName = DEFAULT_SERVICE_REGISTRY_DOMAIN_NAME;

        return serviceRegistryDomainName;
    }

    protected void init() throws CreationException
    {
        initServiceRegistry();
        initServices();    
    }

    /**
     * Initializes service registry.
     */
    protected void initServiceRegistry() throws CreationException
    {
        // ServiceRegistryPropertiesFactory.getSingleton().createInstance();
        String serviceRegistryFactoryClassName = getConfigProperty(SERVICE_REGISTRY_FACTORY_CLASS_NAME);
        IFactory serviceRegistryFactory = (IFactory) SingletonManager.getSingleton(serviceRegistryFactoryClassName);
        serviceRegistryFactory.createInstance();
    }

    protected void initServices()
    {
        Set serviceNameSet = _registry.getRegistryEntryNameSet(getServiceRegistryDomainName());
        Map serviceDescriptorMap = new HashMap();
        for (
            Iterator itr = serviceNameSet.iterator();
            itr.hasNext();
        )
        {
            String serviceName = (String) itr.next();
            IService service = null;
            IServiceDescriptor sd = null;

            try
            {
                service = createServiceByName(serviceName);
                sd = service.getDescriptor();
                serviceDescriptorMap.put(serviceName, sd);
                _Logger.info("service '" + serviceName + "' init OK");                
            }
            catch(InvalidServiceException ise)
            {
                _Logger.error("service '" + serviceName + "' init failed: invalid", ise);
                sd = new ServiceDescriptor();
                sd.setValid(false);
                serviceDescriptorMap.put(serviceName, sd);
            }
            catch(ServiceException se)
            {
                _Logger.error("failed to get service '" + serviceName + "'", se.getCause());
            }
        }

        _serviceDescriptorMap = serviceDescriptorMap;
    }

    protected IRegistry _registry;
    protected Map _serviceDescriptorMap;
}