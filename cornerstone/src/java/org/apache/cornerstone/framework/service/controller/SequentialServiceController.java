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

package org.apache.cornerstone.framework.service.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.ServiceException;
import org.apache.cornerstone.framework.service.ServiceManager;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

/**
Service controller that invokes its services in sequence.
*/

public class SequentialServiceController extends BaseServiceController
{
    public static final String REVISION = "$Revision$";

    public static final String SEQUENCE = "sequence";
    public static final String CLASS_NAME = "className";
    public static final String FACTORY_CLASS_NAME = "factoryClassName";
    public static final String REGISTRY_NAME = "name";

    /**
     * Invokes all my services in sequence.
     * @param context context object that has all input and output
     *   parameters.
     * @return value of service.
     * @exception ServiceException
     */
    protected Object invokeMiddle(IContext context) throws ServiceException
    {
        List serviceList = getServiceList();
        Object result = null;

        if (serviceList != null)
        {
            for (int i = 0; i < serviceList.size(); i++)
            {
                IService service = (IService) serviceList.get(i);
                result = service.invoke(context);
            }
        }

        return result;
    }

    /**
     * Gets list of service instances.
     * @return list of service instances.
     * @throws ServiceException
     */
    protected List getServiceList() throws ServiceException
    {
        if (_serviceList == null)
        {
            _serviceList = new ArrayList();
            String serviceListString =
                getConfigProperty(SEQUENCE);
            List serviceNameList =
                Util.convertStringsToList(serviceListString);
            for (int i = 0; i < serviceNameList.size(); i++)
            {
                String serviceName = (String) serviceNameList.get(i);
                String serviceRegistryName = getConfigProperty(SEQUENCE, serviceName, REGISTRY_NAME);
                String serviceFactoryClassName = getConfigProperty(SEQUENCE, serviceName, FACTORY_CLASS_NAME);
                String serviceClassName = getConfigProperty(SEQUENCE, serviceName, CLASS_NAME);

                if (serviceRegistryName != null)
                {
                    _serviceList.add(ServiceManager.getSingleton().createServiceByName(serviceRegistryName));
                }
                else if (serviceFactoryClassName != null)
                {
                    _serviceList.add(ServiceManager.getSingleton().createServiceByFactoryClassName(serviceFactoryClassName));
                }
                else
                {
                    _serviceList.add(ServiceManager.getSingleton().createServiceByFactoryClassName(serviceClassName));
                }
            }
        }

        return _serviceList;
    }

    protected List _serviceList = null;
    private static Logger _Logger = Logger.getLogger(SequentialServiceController.class);
}
