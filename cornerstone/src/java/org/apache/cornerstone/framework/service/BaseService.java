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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.core.BaseException;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceDescriptor;
import org.apache.cornerstone.framework.api.service.InvalidServiceException;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.api.service.metric.IServiceMetric;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

/**
Superclass of all service classes.
*/

public abstract class BaseService extends BaseObject implements IService
{
    public static final String REVISION = "$Revision$";

    public static final String USER_NAME = "userName";
    public static final String COOKIES = "cookies";
    public static final String REQUEST = "request";

    public static final String INVOKE_DIRECT = "invokeDirect";

    public String getName()
    {
        return _name;
    }

    public void setName(String name)
    {
    	_name = name;
    }

    public IServiceMetric getMetric()
    {
        return _metric;
    }

    public void setMetric(IServiceMetric metric)
    {
        _metric = metric;
    }

    /**
     * Invokes service.  A service can have any number of input parameters
     * and returns exactly one object as the value.  It can also make
     * changes to any number of properties of context as side effects.
     * @param context context object that has all input and output
     *   parameters.
     * @return value of service.
     * @exception ServiceException
     */
    public final Object invoke(IContext context) throws ServiceException
    {
        invokeStart(context);
        Object result = invokeMiddle(context);
        return invokeEnd(context, result);
    }

    public IServiceDescriptor getDescriptor()
        throws InvalidServiceException
    {
        String serviceName = getName();
        IServiceDescriptor serviceDescriptor = (IServiceDescriptor) _ServiceDescriptorMap.get(serviceName);
        if (serviceDescriptor == null)
        {
            serviceDescriptor = buildDescriptor();
            _ServiceDescriptorMap.put(serviceName, serviceDescriptor);
        }

        return serviceDescriptor;
    }

    protected void invokeStart(IContext context)
        throws ServiceException
    {
        _invokeStartTime = System.currentTimeMillis();
    }

    protected Object invokeMiddle(IContext context)
        throws ServiceException
    {
        return callInvokeDirect(context);
    }

    protected Object invokeEnd(IContext context, Object result)
        throws ServiceException
    {
        if (_metric != null)
        {
            long endTime = System.currentTimeMillis();
            _metric.addTimeStamp(context, _invokeStartTime, endTime);
        }

        return result;
    }

    protected Object callInvokeDirect(IContext context)
        throws ServiceException
    {
        try
        {
            IServiceDescriptor sd = getDescriptor();

            String[] inputNames = sd.getInputNames();
            Class[] inputTypes = sd.getInputTypes();
            Object[] args = new Object[inputNames.length];

            for (int i = 0; i < inputNames.length; i++)
            {
                String inputName = inputNames[i];
                Class inputType = inputTypes[i];
                Object input = context.getValue(inputName);
                if (input != null && !inputType.isAssignableFrom(input.getClass()))
                {            
                    throw new ServiceException(new InvalidServiceException(sd.getServiceClass() + ": parameter '" + inputName + "' is not assignable to type " + inputType));
                }

                args[i] = input;
            }

            Method invokeDirect = sd.getMethod();
            Object result = invokeDirect.invoke(this, args);

            String outputName = sd.getOutputName();
            context.setValue(outputName, result);
            return result;
            
        }
        catch(ServiceException se)
        {
            throw se;
        }
        catch(InvocationTargetException ite)
        {
            Throwable targetException = ite.getTargetException();
            if (targetException instanceof ServiceException)
            {
                throw (ServiceException) targetException;
            }
            else if (targetException instanceof BaseException)
            {
                throw new ServiceException(((BaseException)targetException).getCause());
            }
            else
            {
                throw new ServiceException(targetException);
            }
        }
        catch(IllegalAccessException iae)
        {
            throw new ServiceException(iae);
        }
        
    }

    protected IServiceDescriptor buildDescriptor() throws InvalidServiceException
    {
        Method[] methods = getClass().getMethods();
        Method invokeDirect = null;
        int invokeDirectCount = 0;
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(INVOKE_DIRECT))
            {
                invokeDirectCount++;
                invokeDirect = methods[i];
            }
        }

        switch (invokeDirectCount)
        {
            case 0:
            {
                throw new InvalidServiceException(INVOKE_DIRECT + "() method undefined in " + getClass().getName());
            }

            case 1:
            {
                return new ServiceDescriptor(this, invokeDirect);
            }

            default:
            {
                throw new InvalidServiceException(INVOKE_DIRECT + "() method defined more than once in " + getClass().getName());
            }
        }
    }

    private static Logger _Logger = Logger.getLogger(BaseService.class);
    protected static Map _ServiceDescriptorMap = new HashMap();

    protected String _name = getClass().getName();
    protected long _invokeStartTime;
    protected IServiceMetric _metric;
}