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

package org.apache.cornerstone.framework.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.cornerstone.framework.api.implementation.IImplementationManager;
import org.apache.cornerstone.framework.api.implementation.ImplementationException;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceDescriptor;
import org.apache.cornerstone.framework.api.service.IServiceManager;
import org.apache.cornerstone.framework.api.service.InvalidServiceException;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.log4j.Logger;

/**
Factory for getting all service instances.
*/

public class BaseServiceManager extends BaseObject implements IServiceManager
{
    public static final String REVISION = "$Revision$";

    public static final String SERVICE_REGISTRY = "serviceRegistry";
    public static final String SERVICE_REGISTRY_DOT = SERVICE_REGISTRY + Constant.DOT;
    public static final String CONFIG_SERVICE_REGISTRY_FACTORY_CLASS_NAME =  SERVICE_REGISTRY_DOT+ Constant.FACTORY_CLASS_NAME;
    public static final String CONFIG_SERVICE_REGISTRY_DOMAIN_NAME = SERVICE_REGISTRY_DOT + "domainName";
    public static final String CONFIG_SERVICE_REGISTRY_INTERFACE_NAME = SERVICE_REGISTRY_DOT + "interfaceName";

    public static final String META_DOT = Constant.META + Constant.DOT;
    public static final String CONFIG_META_INSTANCE_CLASS_NAME = META_DOT + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_META_FACTORY_CLASS_NAME = META_DOT + Constant.FACTORY_CLASS_NAME;
    public static final String CONFIG_META_NAME = META_DOT + Constant.PARENT_NAME; 

    /**
     * Object configuration metadata
     */
    public static final String CONFIG_PARAMS =
		CONFIG_SERVICE_REGISTRY_DOMAIN_NAME + "," +
		CONFIG_SERVICE_REGISTRY_FACTORY_CLASS_NAME + "," +
		CONFIG_META_INSTANCE_CLASS_NAME + "," +
		CONFIG_META_FACTORY_CLASS_NAME + "," +
		CONFIG_META_NAME;

	private static Logger _Logger = Logger.getLogger(BaseServiceManager.class);

    public static BaseServiceManager getSingleton()
    {
        return _Singleton;
    }

	public void init()
	{
		super.init();
		_registry = (IRegistry) Cornerstone.getImplementation(IRegistry.class);
		_serviceDomainName = getConfigProperty(CONFIG_SERVICE_REGISTRY_DOMAIN_NAME);
		_serviceInterfaceName = getConfigProperty(CONFIG_SERVICE_REGISTRY_INTERFACE_NAME);
		initServices();    
	}

    public IService createServiceByName(String logicalName)
        throws ServiceException
    {
        if (!_inInit)
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

		try
		{
			IImplementationManager implementationManager = (IImplementationManager) Cornerstone.getImplementation(IImplementationManager.class);
			IService service = (IService) implementationManager.createImplementation(IService.class, logicalName);
            service.setName(logicalName);   // overwrite class name with logical name
    		return service;
		}
		catch (ImplementationException ie)
		{
			throw new ServiceException(ie.getCause());
		}
    }

    public boolean serviceInitialized(String serviceName)
    {
    	return _serviceDescriptorMap.containsKey(serviceName);
    }

	protected BaseServiceManager()
	{
		init();
	}

    protected void initServices()
    {
        Set serviceNameSet = _registry.getEntryNameSet(_serviceDomainName, _serviceInterfaceName);
        _inInit = true;
        _serviceDescriptorMap = new HashMap();
        for (Iterator itr = serviceNameSet.iterator(); itr.hasNext();)
        {
            String serviceName = (String) itr.next();
            initService(serviceName);
        }
        _inInit = false;
    }

    protected void initService(String serviceName)
    {
    	if (serviceInitialized(serviceName)) return;

		IService service = null;
		IServiceDescriptor sd = null;

		try
		{
			service = createServiceByName(serviceName);
			sd = service.getDescriptor();
			_serviceDescriptorMap.put(serviceName, sd);
			_Logger.info("service '" + serviceName + "' init OK");                
		}
		catch(InvalidServiceException ise)
		{
			_Logger.error("service '" + serviceName + "' init failed: invalid", ise);
			sd = new ServiceDescriptor();
			sd.setValid(false);
			_serviceDescriptorMap.put(serviceName, sd);
		}
		catch(ServiceException se)
		{
			_Logger.error("failed to get service '" + serviceName + "'", se.getCause());
		}
    }

	protected static BaseServiceManager _Singleton = new BaseServiceManager();
    protected IRegistry _registry;
    protected Map _serviceDescriptorMap;
    protected boolean _inInit;
    protected String _serviceDomainName;
    protected String _serviceInterfaceName;
}