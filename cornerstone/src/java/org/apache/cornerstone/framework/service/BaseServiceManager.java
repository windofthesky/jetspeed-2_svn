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
		_registry = Cornerstone.getRegistry();
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
			IService service = (IService) Cornerstone.getImplementationManager().createImplementation(IService.class, logicalName);
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